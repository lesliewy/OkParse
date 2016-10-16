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

import com.wy.okooo.domain.EuropeOddsChange;
import com.wy.okooo.domain.Match;
import com.wy.okooo.domain.MatchJob;
import com.wy.okooo.domain.ProbAverage;
import com.wy.okooo.service.ConfigService;
import com.wy.okooo.service.EuroOddsChangeService;
import com.wy.okooo.service.MatchJobService;
import com.wy.okooo.service.OkJobService;
import com.wy.okooo.service.ProbAverageService;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * 定时计算市场的平均概率
 * 
 * @author leslie
 * 
 */
public class ProbAverageJob {
	private static Logger LOGGER = Logger.getLogger(ProbAverageJob.class
			.getName());
	
	private ConfigService configService;
	
	private MatchJobService matchJobService;
	
	private ProbAverageService probAverageService;
	
	private EuroOddsChangeService euroOddsChangeService;
	
	private OkJobService okJobService;
	
	private String currOkUrlDate = null;
	
	// job 类型
	private static final String JOB_FLAG = "E";
	
	// 所有的jobType, PROB_AVERAGE_JOB_TYPE_INTERVAL 的配置值可以少于10个.
	private static final String[] ALL_JOB_TYPES = {"E0", "E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9"};
	
	// PROB_AVERAGE_JOB_TYPE_INTERVAL 初始值.
	private static final String CONFIG_JOB_E_INTERVAL = "4800,3600,2400,1200,900,660,420,300,180,60";
	
	// 单位:min. 以此区分不同的jobType, 以D开头.
	private Set<Integer> jobTypeIntervals = null;
	
	private Integer beginMatchSeq = null;
	
	private Integer endMatchSeq= null;
	
	// 默认的超时时间(单位: s)
	private int delJobUpperLimitSec = 2400;
	
	private Integer[] corpsNo = {14, 82, 65, 27, 43, 448, 373, 614, 24};
	
	public void processProbAverage(){
		LOGGER.info("JOB E - processProbAverage job begin...");
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
			LOGGER.info("JOB E - running job exists. " + runningJobs + ". return now...");
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
			LOGGER.error("JOB E - matches is null or empty. return now...");
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
			LOGGER.info("JOB E - no matches to process, return now...");
			return;
		}
		LOGGER.info("JOB E - " + "  " + beginMatchSeq + "-" + endMatchSeq);
		LOGGER.info("JOB E - jobTypes size: " + jobTypes.size() + ": " + jobTypes);
		LOGGER.info("JOB E - toProcessMatches size: " + toProcessMatches.size() + ": " + toProcessMatchSeqs);
		
		// 登记job, 状态为 R-正在执行.
		MatchJob newJob = new MatchJob();
		newJob.setOkUrlDate(currOkUrlDate);
		newJob.setBeginMatchSeq(beginMatchSeq);
		newJob.setEndMatchSeq(endMatchSeq);
		newJob.setJobType(jobTypes.values().iterator().next());
		newJob.setRemark("JOB E running");
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
		
		// 获取euroOddsChange文件, 只需要某一家的就可以.
		String euroOddsChangePath = "";
		File euroOddsChangeFile = null;
		List<ProbAverage> probAverageList = new ArrayList<ProbAverage>();
		for(Match match : toProcessMatches){
			Integer matchSeq = match.getMatchSeq();
			if(!toProcessMatchSeqs.contains(matchSeq)){
				continue;
			}
			// 优先从第一个获取euroOddsChange html文件, 获取到则跳出.
			for(Integer corpNo : corpsNo){
				euroOddsChangePath = dir + File.separatorChar + OkConstant.EURO_ODDS_CHANGE_FILE_NAME_BASE 
						+ "_"+ corpNo + "_" + matchSeq + ".html";
				euroOddsChangeFile = new File(euroOddsChangePath);
				if(euroOddsChangeFile.exists()){
					euroOddsChangeFile.delete();
				}

				// 为了加快速度，直接构造.  http://www.okooo.com/soccer/match/720283/odds/change/355/ 
				String euroOddsChangeUrl = "http://www.okooo.com/soccer/match/" + match.getOkMatchId() + "/odds/change/" + corpNo + "/";
				// 获取 euroOddsChange 的页面信息.
				OkParseUtils.persistByUrl(euroOddsChangeFile, euroOddsChangeUrl, "gb2312", 1000);
				if(!euroOddsChangeFile.exists()){
					continue;
				}
				List<EuropeOddsChange> euroOddsChangeList = euroOddsChangeService.getEuroOddsChangeFromFile(euroOddsChangeFile, 2, false);
				if(euroOddsChangeList == null || euroOddsChangeList.size() < 2){
					continue;
				}
				EuropeOddsChange euroOddsChangeCurr = euroOddsChangeList.get(0);
				Float hostOdds = euroOddsChangeCurr.getHostOdds();
				Float evenOdds = euroOddsChangeCurr.getEvenOdds();
				Float visitingOdds = euroOddsChangeCurr.getVisitingOdds();
				Float hostKelly = euroOddsChangeCurr.getHostKelly();
				Float evenKelly = euroOddsChangeCurr.getEvenKelly();
				Float visitingKelly = euroOddsChangeCurr.getVisitingKelly();
				Float hostProb = Math.round(hostKelly / hostOdds * 10000)/100.0f;
				Float evenProb = Math.round(evenKelly / evenOdds * 10000)/100.0f;
				Float visitingProb = Math.round(visitingKelly / visitingOdds * 10000)/100.0f;
				
				ProbAverage probAverage = new ProbAverage();
				probAverage.setOkUrlDate(currOkUrlDate);
				probAverage.setMatchSeq(matchSeq);
				probAverage.setJobType(jobTypes.get(matchSeq));
				probAverage.setHostProb(hostProb);
				probAverage.setEvenProb(evenProb);
				probAverage.setVisitingProb(visitingProb);
				probAverage.setTimestamp(new Timestamp(Calendar.getInstance()
						.getTimeInMillis()));
				probAverageList.add(probAverage);
				break;
			}
		}
		// 将数据存入数据库, LOT_PROB_AVERAGE
		probAverageService.insertProbAverageBatch(probAverageList);
		
		// 修改job状态. 将 R状态 修改为 S状态.
		MatchJob updateJob = new MatchJob();
		updateJob.setOkUrlDate(currOkUrlDate);
		updateJob.setJobType(JOB_FLAG);
		updateJob.setStatus(OkConstant.JOB_STATE_SUCCESS);
		updateJob.setRemark("JOB E success");
		updateJob.setTimestamp(new Timestamp(Calendar.getInstance()
				.getTimeInMillis()));
		matchJobService.updateR2S(updateJob);
		LOGGER.info("JOB E - processProbAverage total time: " + (System.currentTimeMillis() - begin)/1000 + " s.");
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
		String probAverageIntevalStr = configs.get(OkConstant.PROB_AVERAGE_JOB_TYPE_INTERVAL);
		String delJobUpperLimitSecStr = configs.get(OkConstant.DEL_JOB_UPPER_LIMIT_SEC);
		
		if(StringUtils.isBlank(currOkUrlDate)){
			LOGGER.error("config CONFIG_CURR_OK_URL_DATE is blank, return now.");
			return false;
		}
		if(StringUtils.isBlank(probAverageIntevalStr)){
			probAverageIntevalStr = CONFIG_JOB_E_INTERVAL;
		}
		if(!StringUtils.isBlank(delJobUpperLimitSecStr)){
			delJobUpperLimitSec = Integer.valueOf(delJobUpperLimitSecStr);
		}
		String[] jobTypeIntervalArr = probAverageIntevalStr.split(",");
		if(jobTypeIntervalArr == null || jobTypeIntervalArr.length == 0){
			LOGGER.error("config PROB_AVERAGE_JOB_TYPE_INTERVAL is blank, return now");
			return false;
		}
		jobTypeIntervals = new TreeSet<Integer>(Collections.reverseOrder());
		for(String jobTypeInterval : jobTypeIntervalArr){
			jobTypeIntervals.add(Integer.valueOf(jobTypeInterval));
		}
		return true;
	}
	
	private Set<String> getSeqJobTypeSet(){
		Set<String> seqJobTypeSet = probAverageService.querySeqAndJobTypeByOkUrlDateInSet(currOkUrlDate);
		return seqJobTypeSet;
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

	public ProbAverageService getProbAverageService() {
		return probAverageService;
	}

	public void setProbAverageService(ProbAverageService probAverageService) {
		this.probAverageService = probAverageService;
	}

	public EuroOddsChangeService getEuroOddsChangeService() {
		return euroOddsChangeService;
	}

	public void setEuroOddsChangeService(EuroOddsChangeService euroOddsChangeService) {
		this.euroOddsChangeService = euroOddsChangeService;
	}

	public OkJobService getOkJobService() {
		return okJobService;
	}

	public void setOkJobService(OkJobService okJobService) {
		this.okJobService = okJobService;
	}
	
}
