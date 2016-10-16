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

import com.wy.okooo.domain.Match;
import com.wy.okooo.domain.MatchJob;
import com.wy.okooo.service.AnalyseService;
import com.wy.okooo.service.ConfigService;
import com.wy.okooo.service.IndexStatsService;
import com.wy.okooo.service.MatchJobService;
import com.wy.okooo.service.MatchSkipService;
import com.wy.okooo.service.OkJobService;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * 定时获取okooo胜负指数、凯利指数离散度(http://www.okooo.com/soccer/match/776381/okoooexponent/#lstu)
 * 
 * @author leslie
 * 
 */
public class IndexStatsJob {
	private static Logger LOGGER = Logger.getLogger(IndexStatsJob.class
			.getName());
	
	private ConfigService configService;
	
	private MatchJobService matchJobService;
	
	private IndexStatsService indexStatsService;
	
	private AnalyseService analyseService;
	
	private OkJobService okJobService;
	
	private MatchSkipService matchSkipService;
	
	private String currOkUrlDate = null;
	
	// 所有的jobType, INDEX_STATS_JOB_TYPE_INTERVAL 的配置值可以少于10个.
	private static final String[] ALL_JOB_TYPES = {"C0", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9"};
	
	// INDEX_STATS_JOB_TYPE_INTERVAL初始值.
	private static final String CONFIG_JOB_C_INTERVAL = "2400,1800,1200,600,300,240,180,120,60,30";
	
	// 单位:min. 以此区分不同的jobType, 以C开头.
	private Set<Integer> jobTypeIntervals = null;
	
	private Integer beginMatchSeq = null;
	
	private Integer endMatchSeq= null;
	
	// 默认的超时时间(单位: s)
	private int delJobUpperLimitSec = 2400;
	
	// job 类型
	private static final String JOB_FLAG = "C";
	
	public void processIndexStats(){
		LOGGER.info("JOB C - processIndexStats job begin...");
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
			LOGGER.info("JOB C - running job exists. " + runningJobs + ". return now...");
			return;
		}
		
		// 由JOB-A来获取，只有当match.html不存在时才获取当天的match.html.
		String dir = OkParseUtils.persistMatch(currOkUrlDate, false);
		
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
			LOGGER.error("JOB C - matches is null or empty. return now...");
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
		for(Match match : matches){
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
			LOGGER.info("JOB C - no matches to process, return now...");
			return;
		}
		LOGGER.info("JOB C - " + "  " + beginMatchSeq + "-" + endMatchSeq);
		LOGGER.info("JOB C - jobTypes size: " + jobTypes.size() + ": " + jobTypes);
		LOGGER.info("JOB C - toProcessMatches size: " + toProcessMatches.size() + ": " + toProcessMatchSeqs);
		
		// 登记job, 状态为 R-正在执行.
		MatchJob newJob = new MatchJob();
		newJob.setOkUrlDate(currOkUrlDate);
		newJob.setBeginMatchSeq(beginMatchSeq);
		newJob.setEndMatchSeq(endMatchSeq);
		newJob.setJobType(jobTypes.values().iterator().next());
		newJob.setRemark("JOB C running");
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
		
		/*
		 * euroOdds页面暂时无法获取，使用ajax方式经常访问不到: http://www.okooo.com/soccer/match/776383/odds/ajax/?page=2&companytype=BaijiaBooks&type=0
		// 获取euroOdds页面. 先删除包含toProcessMatchSeqs中的euroOdds的页面.
		OkParseUtils.deleteExistedFiles(matchHtml.getParentFile(), toProcessMatchSeqs, OkConstant.EURO_ODDS_FILE_NAME_BASE + "_");
		HtmlPersist htmlPersist = new HtmlPersist();
		htmlPersist.persistEuroOddsWithLimit(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, toProcessMatches, 
				toProcessMatchSeqs, 0, 0, false);
		// 再次执行，确保文件都已下载.
		htmlPersist.persistEuroOddsWithLimit(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, toProcessMatches, 
				toProcessMatchSeqs, 0, 0, false);
		htmlPersist.persistEuroOddsWithLimit(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, toProcessMatches, 
				toProcessMatchSeqs, 0, 0, false);
		*/
		
		/*
		 * 由于指数值是异步显示在页面上的，所以无法使用原来的方式来获取.
		 * 可以直接访问该异步url: http://www.okooo.com/soccer/match/736914/okoooexponent/xmlData/
		 * 根据type参数的不同, 获取不同的指数。type=okooo: 澳客指数; type=okoooexponent 离散度指数.
		 * 这里不获取html页面，不保存ajax返回的信息，直接解析获得对象，并插入数据库.
		 */
		
		// 将指数数据存入数据库, LOT_INDEX_STATS
		analyseService.indexStatsAnalyse(dir + File.separatorChar, jobTypes, 0, 0, toProcessMatchSeqs, currOkUrlDate, matches);
		
		// 修改job状态. 将 R状态 修改为 S状态.
		MatchJob updateJob = new MatchJob();
		updateJob.setOkUrlDate(currOkUrlDate);
		updateJob.setJobType(JOB_FLAG);
		updateJob.setStatus(OkConstant.JOB_STATE_SUCCESS);
		updateJob.setRemark("JOB C success");
		updateJob.setTimestamp(new Timestamp(Calendar.getInstance()
				.getTimeInMillis()));
		matchJobService.updateR2S(updateJob);
		LOGGER.info("JOB C - processIndexStats total time: " + (System.currentTimeMillis() - begin)/1000 + " s.");
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
		String indexStatsJobTypeIntevalStr = configs.get(OkConstant.INDEX_STATS_JOB_TYPE_INTERVAL);
		String delJobUpperLimitSecStr = configs.get(OkConstant.DEL_JOB_UPPER_LIMIT_SEC);
		
		if(StringUtils.isBlank(currOkUrlDate)){
			LOGGER.error("config CONFIG_CURR_OK_URL_DATE is blank, return now.");
			return false;
		}
		if(StringUtils.isBlank(indexStatsJobTypeIntevalStr)){
			indexStatsJobTypeIntevalStr = CONFIG_JOB_C_INTERVAL;
		}
		if(!StringUtils.isBlank(delJobUpperLimitSecStr)){
			delJobUpperLimitSec = Integer.valueOf(delJobUpperLimitSecStr);
		}
		String[] jobTypeIntervalArr = indexStatsJobTypeIntevalStr.split(",");
		if(jobTypeIntervalArr == null || jobTypeIntervalArr.length == 0){
			LOGGER.error("config INDEX_STATS_JOB_TYPE_INTERVAL is blank, return now");
			return false;
		}
		jobTypeIntervals = new TreeSet<Integer>(Collections.reverseOrder());
		for(String jobTypeInterval : jobTypeIntervalArr){
			jobTypeIntervals.add(Integer.valueOf(jobTypeInterval));
		}
		return true;
	}
	
	/**
	 * 根据okUrlDate获取 LOT_INDEX_STATS 中已经执行过的数据并组装成key.{matchSeq}_{jobType}
	 * @return
	 */
	private Set<String> getSeqJobTypeSet(){
		Set<String> seqJobTypeSet = indexStatsService.querySeqJobTypeByOkUrlDate(currOkUrlDate);
		return seqJobTypeSet;
	}

	public MatchJobService getMatchJobService() {
		return matchJobService;
	}

	public void setMatchJobService(MatchJobService matchJobService) {
		this.matchJobService = matchJobService;
	}

	public IndexStatsService getIndexStatsService() {
		return indexStatsService;
	}

	public void setIndexStatsService(IndexStatsService indexStatsService) {
		this.indexStatsService = indexStatsService;
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

	public MatchSkipService getMatchSkipService() {
		return matchSkipService;
	}

	public void setMatchSkipService(MatchSkipService matchSkipService) {
		this.matchSkipService = matchSkipService;
	}
	
}
