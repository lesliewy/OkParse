/**
 * 
 */
package com.wy.okooo.job;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.wy.okooo.data.HtmlPersist;
import com.wy.okooo.domain.EuroOddsHandicap;
import com.wy.okooo.domain.KellyMatchCount;
import com.wy.okooo.domain.Match;
import com.wy.okooo.domain.MatchJob;
import com.wy.okooo.service.AnalyseService;
import com.wy.okooo.service.ConfigService;
import com.wy.okooo.service.EuroOddsHandicapService;
import com.wy.okooo.service.KellyMatchCountService;
import com.wy.okooo.service.MatchJobService;
import com.wy.okooo.service.OkJobService;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * 定时获取okooo让球页面信息(http://www.okooo.com/soccer/match/776908/hodds/)
 * 
 * @author leslie
 * 
 */
public class EuroOddsHandicapJob {
	private static Logger LOGGER = Logger.getLogger(EuroOddsHandicapJob.class
			.getName());
	
	private ConfigService configService;
	
	private MatchJobService matchJobService;
	
	private AnalyseService analyseService;
	
	private OkJobService okJobService;
	
	private KellyMatchCountService kellyMatchCountService;
	
	private EuroOddsHandicapService euroOddsHandicapService;
	
	private String currOkUrlDate = null;
	
	// job 类型
	private static final String JOB_FLAG = "D";
	
	// 所有的jobType, INDEX_STATS_JOB_TYPE_INTERVAL 的配置值可以少于10个.
	private static final String[] ALL_JOB_TYPES = {"D0", "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9"};
	
	// EURO_HANDICAP_JOB_TYPE_INTERVAL 初始值.
	private static final String CONFIG_JOB_D_INTERVAL = "2400,1800,1200,600,300,240,180,120,60,30";
	
	// 单位:min. 以此区分不同的jobType, 以D开头.
	private Set<Integer> jobTypeIntervals = null;
	
	private Integer beginMatchSeq = null;
	
	private Integer endMatchSeq= null;
	
	// 默认的超时时间(单位: s)
	private int delJobUpperLimitSec = 2400;
	
	public void processEuroOddsHandicap(){
		LOGGER.info("JOB D - processEuroOddsHandicap job begin...");
		long begin = System.currentTimeMillis();
		
		// 每次重新初始化.
		initVals();
		
		// 查询配置参数LOT_CONFIG
		if(!initFromConfig()){
			return;
		}
		
		// 清理时间超过指定的R状态的job, 修改状态为D.  时间可配置.
		matchJobService.cleanLongTimeJob(delJobUpperLimitSec, JOB_FLAG);
		
		// 根据 okUrlDate 查询是否有正在执行的job, 有则直接退出.
		List<MatchJob> runningJobs = matchJobService.getRunningJobs(currOkUrlDate, JOB_FLAG);
		if(runningJobs != null && !runningJobs.isEmpty()){
			LOGGER.info("JOB D - running job exists. " + runningJobs + ". return now...");
			return;
		}
		
		// 由JOB-A来获取，只有当match.html不存在时才获取当天的match.html.
		Calendar cal = OkParseUtils.buildCalByOkUrlDate(currOkUrlDate);
		String matchUrl = OkParseUtils.buildUrlByDate(cal);
		String dir = OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar + OkParseUtils.getDirPahtFromUrl(matchUrl);
		File parentDir = new File(dir);
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
		File matchHtml = new File(dir + File.separatorChar + OkConstant.MATCH_FILE_NAME);
		if(!matchHtml.exists()){
			OkParseUtils.persistMatch(matchHtml, matchUrl, true);
		}
		
		// 获取match.html.
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(dir,
				OkConstant.MATCH_FILE_NAME);
		if (matchHtmlFiles == null || matchHtmlFiles.isEmpty()) {
			LOGGER.info("no match.html, return now.");
			return;
		}
		if (matchHtmlFiles.size() > 1) {
			LOGGER.info("more than 1 day, return now.");
			return;
		}
		
		// 获取 match 对象.
		List<Match> matches = okJobService.getMatchesFromHtml(matchHtmlFiles, currOkUrlDate, JOB_FLAG);
		if (matches == null || matches.isEmpty()) {
			LOGGER.error("JOB D - matches is null or empty. return now...");
			return;
		}
		
		// 查询某个jobType时的match是否已经执行过，已经执行过了，就不再获取html. key: {matchSeq}_{jobType}
		Set<String> seqJobTypeSet = getSeqJobTypeSet();
		
		Calendar nowCal = Calendar.getInstance();
		List<Calendar> intervalCals = OkParseUtils.buildCalFromIntervals(jobTypeIntervals);
		Map<Integer, String> jobTypes = new TreeMap<Integer, String>();
		String jobType = "";
		List<Match> toProcessMatches = new ArrayList<Match>();
		Set<Integer> toProcessMatchSeqs = new TreeSet<Integer>();
		for(Match match : matches){
			// 排除掉已经开赛的.
			Calendar matchTime = Calendar.getInstance();
			matchTime.setTimeInMillis(match.getMatchTime().getTime());
			if(nowCal.after(matchTime)){
				continue;
			}
			
			Integer matchSeq = match.getMatchSeq();
			// 计算需要处理的match, 从最近的时间开始。
			for(int i = intervalCals.size()-1; i >= 0; i--){
				Calendar intervalCal = intervalCals.get(i);
				if(intervalCal.after(matchTime)){
					jobType = ALL_JOB_TYPES[i];
	                if(!seqJobTypeSet.contains(matchSeq + "_" + jobType)){
	                	toProcessMatches.add(match);
	                	toProcessMatchSeqs.add(matchSeq);
	                }
	                jobTypes.put(matchSeq, jobType);
	    			// 计算beginMatchSeq, endMatchSeq.
	    			if(matchSeq < beginMatchSeq){
	    				beginMatchSeq = matchSeq;
	    			}
	    			if(matchSeq > endMatchSeq){
	    				endMatchSeq = matchSeq;
	    			}
	                break;
				}
			}
		}
		
		if(toProcessMatches.isEmpty()){
			LOGGER.info("JOB D - no matches to process, return now...");
			return;
		}
		LOGGER.info("JOB D - " + "  " + beginMatchSeq + "-" + endMatchSeq);
		LOGGER.info("JOB D - jobTypes size: " + jobTypes.size() + ": " + jobTypes);
		LOGGER.info("JOB D - toProcessMatches size: " + toProcessMatches.size() + ": " + toProcessMatchSeqs);
		
		// 登记job, 状态为 R-正在执行.
		MatchJob newJob = new MatchJob();
		newJob.setOkUrlDate(currOkUrlDate);
		newJob.setBeginMatchSeq(beginMatchSeq);
		newJob.setEndMatchSeq(endMatchSeq);
		newJob.setJobType(jobTypes.values().iterator().next());
		newJob.setRemark("JOB D running");
		Timestamp now = new Timestamp(Calendar.getInstance()
				.getTimeInMillis());
		newJob.setBeginTime(now);
		newJob.setTimestamp(now);
		StringBuilder timeType = new StringBuilder("");
		Iterator<Integer> iter = jobTypeIntervals.iterator();
		int index = 0;
		while(iter.hasNext()){
			timeType.append(JOB_FLAG).append(index).append(":").append(iter.next()).append("|");
			index++;
		}
		newJob.setTimeType(timeType.toString());
		// // 修改成每个时间段只执行一次后，有可能已经存在状态为S的, 需要先删除已经存在的S状态的
		newJob.setStatus(OkConstant.JOB_STATE_SUCCESS);
		matchJobService.deleteJobById(newJob);
		newJob.setStatus(OkConstant.JOB_STATE_RUNNING);
		matchJobService.insertJob(newJob);
		
		// 同euroOddsChange, 先作删除, 最后要加 "_", 防止删除掉 asiaOddsChange
		OkParseUtils.deleteExistedFiles(matchHtml.getParentFile(), toProcessMatchSeqs, OkConstant.EURO_HANDICAP_FILE_NAME_BASE + "_");
		HtmlPersist htmlPersist = new HtmlPersist();
		htmlPersist.persistEuroHandicapWithLimit(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, toProcessMatches, 
				toProcessMatchSeqs, 0, 0, false, false);
		// 再次执行，确保文件都已下载.
		htmlPersist.persistEuroHandicapWithLimit(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, toProcessMatches, 
				toProcessMatchSeqs, 0, 0, false, false);
		htmlPersist.persistEuroHandicapWithLimit(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, toProcessMatches, 
				toProcessMatchSeqs, 0, 0, false, false);
		
		LOGGER.info("JOB D - persist total time: " + (System.currentTimeMillis() - begin)/1000 + " s.");
		
		// 将数据存入数据库, LOT_ODDS_EURO_HANDICAP
		analyseService.euroHandicapAnalyse(dir + File.separatorChar, jobTypes, 0, 0, toProcessMatchSeqs, currOkUrlDate, matches);
		
		// 登值到 LOT_KELLY_MATCH_COUNT
		persistMatchCount(toProcessMatchSeqs, jobTypes);
		
		// 修改job状态. 将 R状态 修改为 S状态.
		MatchJob updateJob = new MatchJob();
		updateJob.setOkUrlDate(currOkUrlDate);
		updateJob.setJobType(JOB_FLAG);
		updateJob.setStatus(OkConstant.JOB_STATE_SUCCESS);
		updateJob.setRemark("JOB D success");
		updateJob.setTimestamp(new Timestamp(Calendar.getInstance()
				.getTimeInMillis()));
		matchJobService.updateR2S(updateJob);
		LOGGER.info("JOB D - processEuroOddsHandicap total time: " + (System.currentTimeMillis() - begin)/1000 + " s.");
	}
	
	private void initVals(){
		beginMatchSeq = 1000;
		endMatchSeq= -1;
	}
	
	/**
	 * 解析配置参数: okUrlDate, jobTypeIntervals. 其中jobTypeIntervals是TreeSet,降序排列.
	 * @return
	 */
	private boolean initFromConfig(){
		Map<String, String> configs = configService.queryAllConfigInMap();
		currOkUrlDate = configs.get(OkConstant.CONFIG_CURR_OK_URL_DATE);
		String euroOddsHandicapIntevalStr = configs.get(OkConstant.EURO_HANDICAP_JOB_TYPE_INTERVAL);
		String delJobUpperLimitSecStr = configs.get(OkConstant.DEL_JOB_UPPER_LIMIT_SEC);
		
		if(StringUtils.isBlank(currOkUrlDate)){
			LOGGER.error("config CONFIG_CURR_OK_URL_DATE is blank, return now.");
			return false;
		}
		if(StringUtils.isBlank(euroOddsHandicapIntevalStr)){
			euroOddsHandicapIntevalStr = CONFIG_JOB_D_INTERVAL;
		}
		if(!StringUtils.isBlank(delJobUpperLimitSecStr)){
			delJobUpperLimitSec = Integer.valueOf(delJobUpperLimitSecStr);
		}
		String[] jobTypeIntervalArr = euroOddsHandicapIntevalStr.split(",");
		if(jobTypeIntervalArr == null || jobTypeIntervalArr.length == 0){
			LOGGER.error("config EURO_HANDICAP_JOB_TYPE_INTERVAL is blank, return now");
			return false;
		}
		jobTypeIntervals = new TreeSet<Integer>(Collections.reverseOrder());
		for(String jobTypeInterval : jobTypeIntervalArr){
			jobTypeIntervals.add(Integer.valueOf(jobTypeInterval));
		}
		return true;
	}
	
	private Set<String> getSeqJobTypeSet(){
		Set<String> seqJobTypeSet = kellyMatchCountService.querySeqAndJobTypeByOkUrlDateInSet(currOkUrlDate);
		return seqJobTypeSet;
	}
	
	/**
	 * 将变化值插入 LOT_KELLY_MATCH_COUNT.
	 * 将所有公司的赔率平均值记入 EXTEND1, 例如: -1:1.83|2.34|3.23|  其中-1表示让球值.
	 * 将所有公司的kelly指数的方差记入 EXTEND2 例如: -1:2.89|5.87|6.80| 其中-1表示让球值.
	 * 方差计算时使用修正的样本方差: S^2 = (1/n-1)[(x1-x0)^2 + (x2-x0)^2 + (x3 - x0)^2 + ...]
	 * @param toProcessMatchSeqs
	 * @param jobTypes
	 */
	private void persistMatchCount(Set<Integer> toProcessMatchSeqs, Map<Integer, String> jobTypes){
		List<KellyMatchCount> matchCountList = new ArrayList<KellyMatchCount>();
		
		String averageString = "";
		String sdVarianceString = "";
		
		for(Integer matchSeq : toProcessMatchSeqs){
			String jobType = jobTypes.get(matchSeq);
			// 获取 LOT_ODDS_EURO_HANDICAP 中相关的值, 使用列转行.
			EuroOddsHandicap euroOddsHandicap = euroOddsHandicapService.queryTransByDateJobType(currOkUrlDate, matchSeq, jobType);
			String compHandicapStr = String.valueOf(euroOddsHandicap.getCompHandicap());
			averageString = getAverageString(compHandicapStr, euroOddsHandicap.getAllHostOdds(), euroOddsHandicap.getAllEvenOdds(), euroOddsHandicap.getAllVisitingOdds());
			sdVarianceString = getSdVarianceString(compHandicapStr, euroOddsHandicap.getAllHostKelly(), euroOddsHandicap.getAllEvenKelly(), euroOddsHandicap.getAllVisitingKelly());
			KellyMatchCount matchCountHost = new KellyMatchCount();
			matchCountHost.setOkUrlDate(currOkUrlDate);
			matchCountHost.setMatchSeq(matchSeq);
			matchCountHost.setJobType(jobType);
			matchCountHost.setRuleType("KO");
			matchCountHost.setCorpCount(0);
			matchCountHost.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			matchCountHost.setExtend1(averageString);
			matchCountHost.setExtend2(sdVarianceString);
			matchCountList.add(matchCountHost);
		}
		kellyMatchCountService.insertMatchCountBatch(matchCountList);
	}
	
	private String getAverageString(String compHandicap, String hostStr, String evenStr, String visitingStr){
		String result = "";
		if(StringUtils.isBlank(compHandicap) || StringUtils.isBlank(hostStr) || StringUtils.isBlank(evenStr)
				|| StringUtils.isBlank(visitingStr)){
			return result;
		}
		Float avgHost = OkParseUtils.getAverageFromStr(hostStr);
		Float avgEven = OkParseUtils.getAverageFromStr(evenStr);
		Float avgVisiting = OkParseUtils.getAverageFromStr(visitingStr);
		// 保留2位小数.
		return compHandicap + ":" + (Math.round(avgHost * 100))/100.0 + "|" + (Math.round(avgEven * 100))/100.0 + "|"
				+ (Math.round(avgVisiting * 100))/100.0 + "|";
	}
	
	private String getSdVarianceString(String compHandicap, String hostStr, String evenStr, String visitingStr){
		String result = "";
		if(StringUtils.isBlank(compHandicap) || StringUtils.isBlank(hostStr) || StringUtils.isBlank(evenStr)
				|| StringUtils.isBlank(visitingStr)){
			return result;
		}
		Float avgHost = OkParseUtils.getSdVarianceFromStr(hostStr);
		Float avgEven = OkParseUtils.getSdVarianceFromStr(evenStr);
		Float avgVisiting = OkParseUtils.getSdVarianceFromStr(visitingStr);
		// 保留4位小数再乘以100.
		return compHandicap + ":" + (Math.round(avgHost * 10000))/100.0 + "|" + (Math.round(avgEven * 10000))/100.0 + "|"
				+ (Math.round(avgVisiting * 10000))/100.0 + "|";
	}
	
	public MatchJobService getMatchJobService() {
		return matchJobService;
	}

	public void setMatchJobService(MatchJobService matchJobService) {
		this.matchJobService = matchJobService;
	}

	public AnalyseService getAnalyseService() {
		return analyseService;
	}

	public void setAnalyseService(AnalyseService analyseService) {
		this.analyseService = analyseService;
	}

	public ConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	public OkJobService getOkJobService() {
		return okJobService;
	}

	public void setOkJobService(OkJobService okJobService) {
		this.okJobService = okJobService;
	}

	public KellyMatchCountService getKellyMatchCountService() {
		return kellyMatchCountService;
	}

	public void setKellyMatchCountService(
			KellyMatchCountService kellyMatchCountService) {
		this.kellyMatchCountService = kellyMatchCountService;
	}

	public EuroOddsHandicapService getEuroOddsHandicapService() {
		return euroOddsHandicapService;
	}

	public void setEuroOddsHandicapService(
			EuroOddsHandicapService euroOddsHandicapService) {
		this.euroOddsHandicapService = euroOddsHandicapService;
	}
	
}
