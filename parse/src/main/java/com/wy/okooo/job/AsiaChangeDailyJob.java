/**
 * 
 */
package com.wy.okooo.job;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.wy.okooo.data.HtmlPersist;
import com.wy.okooo.domain.AsiaOdds;
import com.wy.okooo.domain.Match;
import com.wy.okooo.domain.MatchJob;
import com.wy.okooo.service.AsiaOddsChangeService;
import com.wy.okooo.service.AsiaOddsService;
import com.wy.okooo.service.ConfigService;
import com.wy.okooo.service.MatchJobService;
import com.wy.okooo.service.MatchSkipService;
import com.wy.okooo.service.OkJobService;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * 定时获取所有公司的亚盘页面， 将每个公司最初、盘口变化、最新的赔率数据记入LOT_ODDS_ASIA_CHANGE_DAILY.
 * 
 * @author leslie
 * 
 */
public class AsiaChangeDailyJob {
	private static Logger LOGGER = Logger.getLogger(AsiaChangeDailyJob.class
			.getName());
	
	private ConfigService configService;
	
	private MatchJobService matchJobService;
	
	private AsiaOddsService asiaOddsService;
	
	private AsiaOddsChangeService asiaOddsChangeService;
	
	private OkJobService okJobService;
	
	private MatchSkipService matchSkipService;
	
	private String currOkUrlDate = null;
	
	private Map<String, Object> jobMap = null;
	
	// 所有的jobType.
	private static final String[] ALL_JOB_TYPES = {"F0", "F1", "F2", "F3", "F4"};
	
	// 单位:min. 以此区分不同的jobType, 以C开头.
	private Set<Integer> jobTypeIntervals = null;
	
	private Integer beginMatchSeq = null;
	
	private Integer endMatchSeq= null;
	
	// 默认的超时时间(单位: s)
	private int delJobUpperLimitSec = OkConstant.DEL_JOB_UPPER_LIMIT_SEC_DEFALUT2;
	
	// 当前配置的jobType
	private List<String> currJobTypes = new ArrayList<String>();
	
	// job 类型
	private static final String JOB_FLAG = "F";
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void processAsiaChangeDaily(){
		LOGGER.info("JOB F - processAsiaChangeDaily job begin...");
		long begin = System.currentTimeMillis();
		
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
			LOGGER.info("JOB "+ JOB_FLAG + " - running job exists. " + runningJobs + ". return now...");
			return;
		}
		
		// 由JOB-A来获取，只有当match.html不存在时才获取当天的match.html.
		String dir = OkParseUtils.persistMatch(currOkUrlDate, false);
		Calendar cal = OkParseUtils.buildCalByOkUrlDate(currOkUrlDate);
		
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
			LOGGER.error("JOB "+ JOB_FLAG + " - matches is null or empty. return now...");
			return;
		}
		
		// 查询某个jobType时的match是否已经执行过，已经执行过了，就不再获取html. key: {matchSeq}_{jobType}
		Set<String> seqJobTypeSet = getSeqJobTypeSet();
		// 查看是否有需要跳过的比赛场次.
		Set<Integer> skipSeqSet = matchSkipService.querySkipMatchesByOkUrlDateInSet(currOkUrlDate);
		
		Calendar nowCal = Calendar.getInstance();
		List<Calendar> intervalCals = OkParseUtils.buildCalFromIntervals(jobTypeIntervals);
		Map<Integer, String> jobTypes = new TreeMap<Integer, String>();
		String jobType = "";
		List<Match> toProcessMatches = new ArrayList<Match>();
		Set<Integer> toProcessMatchSeqs = new TreeSet<Integer>();
		boolean breakNow = false;
		for(Match match : matches){
			// 控制总个数。 例如: 如果此时存在A3的比赛，那么不管是不是A3的比赛的总个数不能超过配置的A3的限制数.
			int totalMatchesSize = jobTypes.size();
			for(int i = currJobTypes.size() -1; i >= 0; i--){
				String type = currJobTypes.get(i);
				int length = ((List)jobMap.get(type + "_list")).size();
				int limit = (Integer)jobMap.get(type + "_num");
				if(length > 0 && totalMatchesSize >= limit){
					breakNow = true;
					break;
				}
			}
			if(breakNow){
				break;
			}
			
			// 排除掉已经开赛的.
			Calendar matchTime = Calendar.getInstance();
			matchTime.setTimeInMillis(match.getMatchTime().getTime());
			if(nowCal.after(matchTime)){
				continue;
			}
			
			Integer matchSeq = match.getMatchSeq();
			// 排除掉需要跳过的。
			if(skipSeqSet != null && skipSeqSet.contains(matchSeq)){
				continue;
			}
						
			// 计算需要处理的match, 从最近的时间开始。
			for(int i = intervalCals.size()-1; i >= 0; i--){
				Calendar intervalCal = intervalCals.get(i);
				if(intervalCal.after(matchTime)){
					jobType = ALL_JOB_TYPES[i];
	                if(!seqJobTypeSet.contains(matchSeq + "_" + jobType)){
	                	toProcessMatches.add(match);
	                	toProcessMatchSeqs.add(matchSeq);
	                	((List)jobMap.get(ALL_JOB_TYPES[i] + "_list")).add(match);
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
			LOGGER.info("JOB "+ JOB_FLAG + " - no matches to process, return now...");
			return;
		}
		LOGGER.info("JOB "+ JOB_FLAG + " - " + "  " + beginMatchSeq + "-" + endMatchSeq);
		LOGGER.info("JOB "+ JOB_FLAG + " - jobTypes size: " + jobTypes.size() + ": " + jobTypes);
		LOGGER.info("JOB "+ JOB_FLAG + " - toProcessMatches size: " + toProcessMatches.size() + ": " + toProcessMatchSeqs);
		
		// 登记job, 状态为 R-正在执行.
		MatchJob newJob = new MatchJob();
		newJob.setOkUrlDate(currOkUrlDate);
		newJob.setBeginMatchSeq(beginMatchSeq);
		newJob.setEndMatchSeq(endMatchSeq);
		newJob.setJobType(jobTypes.values().iterator().next());
		newJob.setRemark("JOB "+ JOB_FLAG + " running");
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
		
		Map<String, String> corpNameNoMap = okJobService.getCorpNameCorpNoMap();
		Map<String, String> cropNoNameMap = okJobService.queryCorpsNames();
		
		/*
		 * 获取AsiaOdds页面(http://www.okooo.com/soccer/match/791370/ah/)
		 */
		long beginPersist = System.currentTimeMillis();
		File okUrlDateDir = matchHtmlFiles.get(0).getParentFile();
		String okUrlDateDirPath = okUrlDateDir.getAbsolutePath() + "/";
		OkParseUtils.deleteExistedFiles(okUrlDateDir, toProcessMatchSeqs, OkConstant.ASIA_ODDS_FILE_NAME_BASE + "_");
		HtmlPersist htmlPersist = new HtmlPersist();
		// 再次执行，确保文件都已下载.
		htmlPersist.persistAsiaOddsWithLimit(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, toProcessMatches, 
				toProcessMatchSeqs, 0, 0, false, false);
		htmlPersist.persistAsiaOddsWithLimit(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, toProcessMatches, 
				toProcessMatchSeqs, 0, 0, false, false);
		htmlPersist.persistAsiaOddsWithLimit(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, toProcessMatches, 
				toProcessMatchSeqs, 0, 0, false, false);
		
		/*
		 *  解析asiaOdds, 获取asiaOddsChange
		 */
		// 先删除
		OkParseUtils.deleteExistedFiles(okUrlDateDir, toProcessMatchSeqs, OkConstant.ASIA_ODDS_CHANGE_FILE_NAME_BASE + "_");
		for(Integer matchSeq : toProcessMatchSeqs){
			File asiaOddsHtml = new File(okUrlDateDirPath + OkConstant.ASIA_ODDS_FILE_NAME_BASE + "_" + matchSeq + ".html");
			if(!asiaOddsHtml.exists()){
				LOGGER.error(asiaOddsHtml.getAbsolutePath() + " not exists.");
				continue;
			}
			// 下载 asiaOddsHtml 中所有公司的 asiaOddsChange 页面.
			List<AsiaOdds> asiaOddsList = asiaOddsService.getAsiaOddsFromFile(asiaOddsHtml, matchSeq);
			if(asiaOddsList == null || asiaOddsList.isEmpty()){
				continue;
			}
			htmlPersist.persistAsiaOddsChangeFromAsiaOddsList(okUrlDateDirPath, asiaOddsList, corpNameNoMap);
			// 再次执行
			htmlPersist.persistAsiaOddsChangeFromAsiaOddsList(okUrlDateDirPath, asiaOddsList, corpNameNoMap);
			htmlPersist.persistAsiaOddsChangeFromAsiaOddsList(okUrlDateDirPath, asiaOddsList, corpNameNoMap);
		}
		
		LOGGER.info("JOB "+ JOB_FLAG + " - persist success, total time: " + (System.currentTimeMillis() - beginPersist)/1000 + " s.");
		
		/*
		 *  解析asiaOddsChange， 存入 LOT_ODDS_ASIA_CHANGE_DAILY
		 *  记录最初的、盘口发生变化后的、最新的3种情况的赔率数据.
		 */
		asiaOddsChangeService.analyseAsiaOddsChangeDaily(matchHtmlFiles.get(0).getParentFile(), jobTypes, cropNoNameMap, toProcessMatchSeqs, currOkUrlDate);
		
		// 修改job状态. 将 R状态 修改为 S状态.
		MatchJob updateJob = new MatchJob();
		updateJob.setOkUrlDate(currOkUrlDate);
		updateJob.setJobType(JOB_FLAG);
		updateJob.setStatus(OkConstant.JOB_STATE_SUCCESS);
		updateJob.setRemark("JOB "+ JOB_FLAG + " success");
		updateJob.setTimestamp(new Timestamp(Calendar.getInstance()
				.getTimeInMillis()));
		matchJobService.updateR2S(updateJob);
		LOGGER.info("JOB "+ JOB_FLAG + " - processAsiaChangeDaily total time: " + (System.currentTimeMillis() - begin)/1000 + " s.");
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
		String jobFIntevalStr = configs.get(OkConstant.JOB_F_INTERVAL);
		String delJobUpperLimitSecStr = configs.get(OkConstant.DEL_JOB_UPPER_LIMIT_SEC);
		String configJobAMatchNumStr = configs.get(OkConstant.CONFIG_JOB_A_MATCH_NUM);
		
		if(StringUtils.isBlank(currOkUrlDate)){
			LOGGER.error("config CONFIG_CURR_OK_URL_DATE is blank, return now.");
			return false;
		}
		// 如果没有配置JOB_F_INTERVAL, 获取PARSE_MAIL_JOB_TYPE_INTERVAL(即JOB A的INTERVAL)， 再加上30
		if(StringUtils.isBlank(jobFIntevalStr)){
			jobFIntevalStr = configs.get(OkConstant.PARSE_MAIL_JOB_TYPE_INTERVAL);
			if(StringUtils.isBlank(jobFIntevalStr)){
				jobFIntevalStr = OkConstant.JOB_A_INTERVAL_DEFAULT;
			}
		}
		if(!StringUtils.isBlank(delJobUpperLimitSecStr)){
			delJobUpperLimitSec = Integer.valueOf(delJobUpperLimitSecStr);
		}
		String[] jobTypeIntervalArr = jobFIntevalStr.split(",");
		if(jobTypeIntervalArr == null || jobTypeIntervalArr.length == 0){
			LOGGER.error("config JOB_F_INTERVAL is blank, return now");
			return false;
		}
		jobTypeIntervals = new TreeSet<Integer>(Collections.reverseOrder());
		for(String jobTypeInterval : jobTypeIntervalArr){
			jobTypeIntervals.add(Integer.valueOf(jobTypeInterval) + 30);
		}
		
		// 处理matchNum
		if(StringUtils.isBlank(configJobAMatchNumStr)){
			configJobAMatchNumStr =  OkConstant.CONFIG_JOB_A_MATCH_NUM_DEFAULT;;
		}
		String[] matchNumArr = configJobAMatchNumStr.split(",");
		if(matchNumArr == null || matchNumArr.length == 0){
			LOGGER.error("config CONFIG_JOB_A_MATCH_NUM is blank, return now");
			return false;
		}
		if(matchNumArr.length != jobTypeIntervalArr.length){
			LOGGER.error("matchNumArr length: " + matchNumArr.length + "; jobTypeIntervalArr length: " + jobTypeIntervalArr.length + ", return now...");
			return false;
		}
		jobMap = new HashMap<String, Object>();
		for(int i = 0; i < matchNumArr.length; i++){
			jobMap.put(ALL_JOB_TYPES[i] + "_num", Integer.valueOf(matchNumArr[i]));
			jobMap.put(ALL_JOB_TYPES[i] + "_list", new ArrayList<Match>());
			if(!currJobTypes.contains(ALL_JOB_TYPES[i])){
				currJobTypes.add(ALL_JOB_TYPES[i]);
			}
		}
		return true;
	}
	
	private Set<String> getSeqJobTypeSet(){
		return asiaOddsChangeService.querySeqJobTypeInSetByOkUrlDate(currOkUrlDate);
	}

	public ConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	public MatchJobService getMatchJobService() {
		return matchJobService;
	}

	public void setMatchJobService(MatchJobService matchJobService) {
		this.matchJobService = matchJobService;
	}

	public AsiaOddsService getAsiaOddsService() {
		return asiaOddsService;
	}

	public void setAsiaOddsService(AsiaOddsService asiaOddsService) {
		this.asiaOddsService = asiaOddsService;
	}

	public AsiaOddsChangeService getAsiaOddsChangeService() {
		return asiaOddsChangeService;
	}

	public void setAsiaOddsChangeService(AsiaOddsChangeService asiaOddsChangeService) {
		this.asiaOddsChangeService = asiaOddsChangeService;
	}

	public OkJobService getOkJobService() {
		return okJobService;
	}

	public void setOkJobService(OkJobService okJobService) {
		this.okJobService = okJobService;
	}

	public MatchSkipService getMatchSkipService() {
		return matchSkipService;
	}

	public void setMatchSkipService(MatchSkipService matchSkipService) {
		this.matchSkipService = matchSkipService;
	}
	
}
