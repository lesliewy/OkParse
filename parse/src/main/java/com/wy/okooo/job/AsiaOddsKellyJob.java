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
import com.wy.okooo.domain.AsiaOddsTrends;
import com.wy.okooo.domain.KellyMatchCount;
import com.wy.okooo.domain.Match;
import com.wy.okooo.domain.MatchJob;
import com.wy.okooo.service.AnalyseService;
import com.wy.okooo.service.AsiaOddsTrendsService;
import com.wy.okooo.service.ConfigService;
import com.wy.okooo.service.KellyMatchCountService;
import com.wy.okooo.service.MatchJobService;
import com.wy.okooo.service.SingleMatchService;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * 分析亚盘kelly指数变化(与B0相比).
 * KI: HK < HKB0 && VK > VKB0 记为KI的值;  
 * Ki: HK > HKB0 && VK < VKB0 记为Ki的值;
 * 将变化的值插入 LOT_KELLY_MATCH_COUNT.
 * 该JOB_TYPE可以通过参数控制 LOT_CONFIG中的 ASIA_KELLY_JOB_TYPE_INTERVAL, 分钟数，以","分隔, 例如: 2400, 1800, 1200.
 * @author leslie
 * 
 */
public class AsiaOddsKellyJob {
	
	private static Logger LOGGER = Logger.getLogger(AsiaOddsKellyJob.class
			.getName());
	
	private ConfigService configService;
	
	private SingleMatchService singleMatchService;
	
	private MatchJobService matchJobService;
	
	private KellyMatchCountService kellyMatchCountService;
	
	private AnalyseService analyseService;
	
	private AsiaOddsTrendsService asiaOddsTrendsService;
	
	private String currOkUrlDate = null;
	
	// 单位:min. 以此区分不同的jobType, 以B开头.
	private Set<Integer> jobTypeIntervals = null;
	
	private Integer beginMatchSeq = null;
	
	private Integer endMatchSeq= null;
	
	// 所有的jobType, ASIA_KELLY_JOB_TYPE_INTERVAL 的配置值可以少于10个.
	private static final String[] ALL_JOB_TYPES = {"B0", "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9"};
	
	// ASIA_KELLY_JOB_TYPE_INTERVAL初始值.
	private static final String CONFIG_JOB_B_INTERVAL = "2400,1800,1200,600,300,240,180,120,60,30";
	
	// 默认的超时时间(单位: s)
	private int delJobUpperLimitSec = 3000;
	
	public void processAsiaKelly(){
		LOGGER.info("JOB B - processAsiaKelly job begin...");
		long begin = System.currentTimeMillis();
		
		initVals();
		
		// 查询配置参数LOT_CONFIG
		if(!initFromConfig()){
			return;
		}
		
		// 清理时间超过指定的R状态的job, 修改状态为D.  时间可配置.
		matchJobService.cleanLongTimeJob(delJobUpperLimitSec, "B");
		
		// 根据 okUrlDate 查询是否有正在执行的job, 有则直接退出.
		List<MatchJob> runningJobs = matchJobService.getRunningJobs(currOkUrlDate, "B");
		if(runningJobs != null && !runningJobs.isEmpty()){
			LOGGER.info("JOB B - running job exists. " + runningJobs + ". return now...");
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
		List<Match> matches = getMatchesFromHtml(matchHtmlFiles);
		if (matches == null || matches.isEmpty()) {
			LOGGER.error("JOB B - matches is null or empty. return now...");
			return;
		}
		
		// 查询某个jobType时的match是否已经执行过，已经执行过了，就不再获取html, 也不在重新计算kelly变化值. key: {matchSeq}_{jobType}
		Set<String> seqJobTypeSet = getSeqJobTypeSet();
		
		Calendar nowCal = Calendar.getInstance();
		List<Calendar> intervalCals = buildCalFromIntervals(jobTypeIntervals);
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
			LOGGER.info("JOB B - no matches to process, return now...");
			return;
		}
		LOGGER.info("JOB B - " + "  " + beginMatchSeq + "-" + endMatchSeq);
		LOGGER.info("JOB B - jobTypes size: " + jobTypes.size() + ": " + jobTypes);
		LOGGER.info("JOB B - toProcessMatches size: " + toProcessMatches.size() + ": " + toProcessMatchSeqs);
		
		// 登记job, 状态为 R-正在执行.
		MatchJob newJob = new MatchJob();
		newJob.setOkUrlDate(currOkUrlDate);
		newJob.setBeginMatchSeq(beginMatchSeq);
		newJob.setEndMatchSeq(endMatchSeq);
		newJob.setJobType(jobTypes.values().iterator().next());
		newJob.setRemark("JOB B running");
		Timestamp now = new Timestamp(Calendar.getInstance()
				.getTimeInMillis());
		newJob.setBeginTime(now);
		newJob.setTimestamp(now);
		StringBuilder timeType = new StringBuilder("");
		Iterator<Integer> iter = jobTypeIntervals.iterator();
		int index = 0;
		while(iter.hasNext()){
			timeType.append("B").append(index).append(":").append(iter.next()).append("|");
			index++;
		}
		newJob.setTimeType(timeType.toString());
		// // 修改成每个时间段只执行一次后，有可能已经存在状态为S的, 需要先删除已经存在的S状态的
		newJob.setStatus(OkConstant.JOB_STATE_SUCCESS);
		matchJobService.deleteJobById(newJob);
		newJob.setStatus(OkConstant.JOB_STATE_RUNNING);
		matchJobService.insertJob(newJob);
		
		// 同euroOddsChange, 先作删除, 最后要加 "_", 防止删除掉 asiaOddsChange
		OkParseUtils.deleteExistedFiles(matchHtml.getParentFile(), toProcessMatchSeqs, OkConstant.ASIA_ODDS_FILE_NAME_BASE + "_");
		HtmlPersist htmlPersist = new HtmlPersist();
		htmlPersist.persistAsiaOddsWithLimit(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, toProcessMatches, 
				toProcessMatchSeqs, 0, 0, false, false);
		// 再次执行，确保文件都已下载.
		htmlPersist.persistAsiaOddsWithLimit(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, toProcessMatches, 
				toProcessMatchSeqs, 0, 0, false, false);
		htmlPersist.persistAsiaOddsWithLimit(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, toProcessMatches, 
				toProcessMatchSeqs, 0, 0, false, false);
		
		LOGGER.info("JOB B - processAsiaKelly persist total time: " + (System.currentTimeMillis() - begin)/1000 + " s.");
		
		// 将亚盘数据存入数据库, LOT_ODDS_ASIA_TRENDS
		analyseService.asiaOddsAnalyse(dir + File.separatorChar, jobTypes, 0, 0, toProcessMatchSeqs, currOkUrlDate, matches);

		// 登记变化值到 LOT_KELLY_MATCH_COUNT
		persistMatchCount(toProcessMatchSeqs, jobTypes);
		
		// 修改job状态. 将 R状态 修改为 S状态.
		MatchJob updateJob = new MatchJob();
		updateJob.setOkUrlDate(currOkUrlDate);
		updateJob.setJobType("B");
		updateJob.setStatus(OkConstant.JOB_STATE_SUCCESS);
		updateJob.setRemark("JOB B success");
		updateJob.setTimestamp(new Timestamp(Calendar.getInstance()
				.getTimeInMillis()));
		matchJobService.updateR2S(updateJob);
		LOGGER.info("JOB B - processAsiaKelly total time: " + (System.currentTimeMillis() - begin)/1000 + " s.");
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
		String jobTypeIntervalStr = configs.get(OkConstant.ASIA_KELLY_JOB_TYPE_INTERVAL);
		String delJobUpperLimitSecStr = configs.get(OkConstant.DEL_JOB_UPPER_LIMIT_SEC);
		
		if(StringUtils.isBlank(currOkUrlDate)){
			LOGGER.error("config CONFIG_CURR_OK_URL_DATE is blank, return now.");
			return false;
		}
		if(StringUtils.isBlank(jobTypeIntervalStr)){
			jobTypeIntervalStr = CONFIG_JOB_B_INTERVAL;
		}
		if(!StringUtils.isBlank(delJobUpperLimitSecStr)){
			delJobUpperLimitSec = Integer.valueOf(delJobUpperLimitSecStr);
		}
		String[] jobTypeIntervalArr = jobTypeIntervalStr.split(",");
		if(jobTypeIntervalArr == null || jobTypeIntervalArr.length == 0){
			LOGGER.error("config ASIA_KELLY_JOB_TYPE_INTERVAL is blank, return now");
			return false;
		}
		jobTypeIntervals = new TreeSet<Integer>(Collections.reverseOrder());
		for(String jobTypeInterval : jobTypeIntervalArr){
			jobTypeIntervals.add(Integer.valueOf(jobTypeInterval));
		}
		return true;
	}
	
	/**
	 * 解析match.html获取Match对象.
	 * @param matchHtmlFiles
	 * @return
	 */
	private List<Match> getMatchesFromHtml(List<File> matchHtmlFiles){
		MatchJob queryRunningJob = new MatchJob();
		queryRunningJob.setOkUrlDate(currOkUrlDate);
		queryRunningJob.setJobType("B");
		Integer queryBeginSeq = matchJobService.queryMaxBeginSeqByOkUrlDate(queryRunningJob);
		Integer queryEndSeq = 1000;
		if(queryBeginSeq != null){
			queryEndSeq = queryBeginSeq + 1000;
		}else{
			queryBeginSeq = 1;
		}
		
		return singleMatchService.getAllMatchFromFiles(matchHtmlFiles, queryBeginSeq, queryEndSeq);
	}
	
	private Set<String> getSeqJobTypeSet(){
		Set<String> seqJobTypeSet = kellyMatchCountService.querySeqAndJobTypeByOkUrlDateInSet(currOkUrlDate);
		
		// 查询 LOT_ODDS_ASIA_TRENDS 中的B0的matchSeq, 加入seqJobTypeSet.
		AsiaOddsTrends queryTrends = new AsiaOddsTrends();
		queryTrends.setOkUrlDate(currOkUrlDate);
		queryTrends.setJobType("B0");
		List<AsiaOddsTrends> b0TrendsList = asiaOddsTrendsService.queryAsiaTrendsByJobType(queryTrends);
		if(b0TrendsList != null && !b0TrendsList.isEmpty()){
			for(AsiaOddsTrends trend : b0TrendsList){
				seqJobTypeSet.add(trend.getMatchSeq() + "_" + trend.getJobType());
			}
		}
		return seqJobTypeSet;
	}
	/**
	 * 将 jobTypeIntervals 转换成 Calendar List.
	 * @param jobTypeIntervals
	 * @return
	 */
	private List<Calendar> buildCalFromIntervals(Set<Integer> jobTypeIntervals){
		List<Calendar> cals = new ArrayList<Calendar>();
		for(Integer jobTypeInterval : jobTypeIntervals){
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, jobTypeInterval);
			cals.add(cal);
		}
		return cals;
	}
	
	/**
	 * 将变化值插入 LOT_KELLY_MATCH_COUNT.
	 * KI: kelly 变化， 与A0的kelly相比，hostKelly变小 && visitingKelly 变大的公司数, 同时将当前变化的数目存入 LOT_KELLY_MATCH_COUNT.
	 * Ki: kelly 变化， 与A0的kelly相比，hostKelly变大 && visitingKelly 变小的公司数, 同时将当前变化的数目存入 LOT_KELLY_MATCH_COUNT.
	 * @param toProcessMatchSeqs
	 * @param jobTypes
	 */
	private void persistMatchCount(Set<Integer> toProcessMatchSeqs, Map<Integer, String> jobTypes){
		List<KellyMatchCount> matchCountList = new ArrayList<KellyMatchCount>();
	    // 用于KI／Ki: kelly变化(与A0相比)  key: {matchSeq}  value key: {oddsCorpName}_{jobType}_{flag}  value: kelly值. 其中{flag} H: hostKelly V:visitingKelly
	    Map<Integer, Map<String, Float>> kellyMap = new HashMap<Integer, Map<String, Float>>();
	    // 当前jobType的开盘公司数，而不是所有的jobType. key: {matchSeq} value: List<oddsCorpNames>
	    Map<Integer, List<String>> corpNamesMap = new HashMap<Integer, List<String>>();
	    // 构造 kellyMap.
		AsiaOddsTrends queryTrends = new AsiaOddsTrends();
		queryTrends.setOkUrlDate(currOkUrlDate);
		queryTrends.setBeginMatchSeq(beginMatchSeq);
		queryTrends.setEndMatchSeq(endMatchSeq);
		List<AsiaOddsTrends> asiaOddsTrendsList = asiaOddsTrendsService.queryAsiaTrendsByRange(queryTrends);
		
	    for(AsiaOddsTrends trends : asiaOddsTrendsList){
			Integer matchSeq = trends.getMatchSeq();
			String jobType = trends.getJobType();
			String oddsCorpName = trends.getOddsCorpName();
			// 构造 kellyMap.
			String preKellyKey = oddsCorpName + "_" + jobType;
			Float hostKelly = trends.getHostKelly();
			Float visitingKelly = trends.getVisitingKelly();
			Map<String, Float> kellyValueMap = kellyMap.get(matchSeq);
			if(kellyValueMap == null){
				kellyValueMap = new HashMap<String, Float>();
				kellyValueMap.put(preKellyKey + "_" + "H", hostKelly);
				kellyValueMap.put(preKellyKey + "_" + "V", visitingKelly);
			}else{
				kellyValueMap.put(preKellyKey + "_" + "H", hostKelly);
				kellyValueMap.put(preKellyKey + "_" + "V", visitingKelly);
			}
			kellyMap.put(matchSeq,  kellyValueMap);
			
			// 构造 corpNamesMap.
			String currJobType = jobTypes.get(Integer.valueOf(matchSeq));
			if(!StringUtils.isBlank(currJobType) && currJobType.equals(jobType)
					&& !"平均值".equals(oddsCorpName) && !"最大值".equals(oddsCorpName) && !"最小值".equals(oddsCorpName)){
				List<String> corpNamesList = corpNamesMap.get(matchSeq);
				if(corpNamesList == null){
					corpNamesList = new ArrayList<String>();
					corpNamesList.add(oddsCorpName);
				}else{
					corpNamesList.add(oddsCorpName);
				}
				corpNamesMap.put(matchSeq, corpNamesList);
			}
	    }
	    
		String averageString = "";
		String sdVarianceString = "";
		
		for(Integer matchSeq : toProcessMatchSeqs){
			// 当前的jobType
			String jobTypeMatch = jobTypes.get(matchSeq);
			if("B0".equals(jobTypeMatch)){
				continue;
			}
			Map<String, Float> kellyValueMap = kellyMap.get(matchSeq);
			if(kellyValueMap == null){
				continue;
			}
			
			List<String> corpNamesList = corpNamesMap.get(matchSeq);
			if(corpNamesList == null){
				continue;
			}
			// 与B0相比，HK < HKB0 && VK > VKB0 记为KI的值;  HK > HKB0 && VK < VKB0 记为Ki的值, 插入LOT_ODDS_ASIA_TRENDS.
			int hostKellyNum = 0;
			int visitingKellyNum = 0;
			// 遍历该matchSeq的所有公司，判断kelly变化情况.
			for(String corpName : corpNamesList){
				// {oddsCorpName}_{jobType}_{flag}
				String hostKellyB0Key = corpName + "_" + "B0" + "_" + "H";
				String visitingKellyB0Key = corpName + "_" + "B0" + "_" + "V";
				String hostKellyCurrKey = corpName + "_" + jobTypeMatch + "_" + "H";
				String visitingKellyCurrKey = corpName + "_" + jobTypeMatch + "_" + "V";
				Float hostKellyB0 = kellyValueMap.get(hostKellyB0Key);
				Float visitingKellyB0 = kellyValueMap.get(visitingKellyB0Key);
				Float hostKellyCurr = kellyValueMap.get(hostKellyCurrKey);
				Float visitingKellyCurr = kellyValueMap.get(visitingKellyCurrKey);
				if(hostKellyCurr == null || visitingKellyCurr == null || hostKellyB0 == null || visitingKellyB0 == null){
					continue;
				}
				if(hostKellyCurr < hostKellyB0 && visitingKellyCurr > visitingKellyB0){
					hostKellyNum++;
				}
				if(hostKellyCurr > hostKellyB0 && visitingKellyCurr < visitingKellyB0){
					visitingKellyNum++;
				}
			}
			// 存入LOT_KELLY_MATCH_COUNT.
			KellyMatchCount matchCountHost = new KellyMatchCount();
			matchCountHost.setOkUrlDate(currOkUrlDate);
			matchCountHost.setMatchSeq(matchSeq);
			matchCountHost.setJobType(jobTypeMatch);
			matchCountHost.setRuleType("KI");
			matchCountHost.setCorpCount(hostKellyNum);
			matchCountHost.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			matchCountList.add(matchCountHost);
			KellyMatchCount matchCountVisiting = new KellyMatchCount();
			matchCountVisiting.setOkUrlDate(currOkUrlDate);
			matchCountVisiting.setMatchSeq(matchSeq);
			matchCountVisiting.setJobType(jobTypeMatch);
			matchCountVisiting.setRuleType("Ki");
			matchCountVisiting.setCorpCount(visitingKellyNum);
			matchCountVisiting.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			matchCountList.add(matchCountVisiting);
			
			// HOST_KELLY VISITING_KELLY 的离散度.
			String jobType = jobTypes.get(matchSeq);
			// 获取 LOT_ODDS_ASIA_TRENDS 中相关的值, 使用列转行, 只获取lossRatio在 [0.90, 0.94] 上的数据.
			AsiaOddsTrends asiaOddsTrends = asiaOddsTrendsService.queryKellyTrendsByDateJobType(currOkUrlDate, matchSeq, jobType);
			averageString = getAverageString(asiaOddsTrends.getAllHostKelly(), asiaOddsTrends.getAllVisitingKelly());
			sdVarianceString = getSdVarianceString(asiaOddsTrends.getAllHostKelly(), asiaOddsTrends.getAllVisitingKelly());
			KellyMatchCount matchCountKk = new KellyMatchCount();
			matchCountKk.setOkUrlDate(currOkUrlDate);
			matchCountKk.setMatchSeq(matchSeq);
			matchCountKk.setJobType(jobType);
			matchCountKk.setRuleType("KK");
			matchCountKk.setCorpCount(0);
			matchCountKk.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			matchCountKk.setExtend1(averageString);
			matchCountKk.setExtend2(sdVarianceString);
			matchCountList.add(matchCountKk);
		}
		kellyMatchCountService.insertMatchCountBatch(matchCountList);
	}
	
	private String getAverageString(String hostStr, String visitingStr){
		String result = "";
		if(StringUtils.isBlank(hostStr) || StringUtils.isBlank(visitingStr)){
			return result;
		}
		Float avgHost = OkParseUtils.getAverageFromStr(hostStr);
		Float avgVisiting = OkParseUtils.getAverageFromStr(visitingStr);
		// 保留2位小数.
		return (Math.round(avgHost * 100))/100.0 + "|" + (Math.round(avgVisiting * 100))/100.0 + "|";
	}
	
	private String getSdVarianceString(String hostStr, String visitingStr){
		String result = "";
		if(StringUtils.isBlank(hostStr) || StringUtils.isBlank(visitingStr)){
			return result;
		}
		Float avgHost = OkParseUtils.getSdVarianceFromStr(hostStr);
		Float avgVisiting = OkParseUtils.getSdVarianceFromStr(visitingStr);
		// 保留4位小数再乘以100.
		return (Math.round(avgHost * 10000))/100.0 + "|" + (Math.round(avgVisiting * 10000))/100.0 + "|";
	}
	
	public SingleMatchService getSingleMatchService() {
		return singleMatchService;
	}

	public void setSingleMatchService(SingleMatchService singleMatchService) {
		this.singleMatchService = singleMatchService;
	}

	public MatchJobService getMatchJobService() {
		return matchJobService;
	}

	public void setMatchJobService(MatchJobService matchJobService) {
		this.matchJobService = matchJobService;
	}

	public ConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	public KellyMatchCountService getKellyMatchCountService() {
		return kellyMatchCountService;
	}

	public void setKellyMatchCountService(
			KellyMatchCountService kellyMatchCountService) {
		this.kellyMatchCountService = kellyMatchCountService;
	}

	public AnalyseService getAnalyseService() {
		return analyseService;
	}

	public void setAnalyseService(AnalyseService analyseService) {
		this.analyseService = analyseService;
	}

	public AsiaOddsTrendsService getAsiaOddsTrendsService() {
		return asiaOddsTrendsService;
	}

	public void setAsiaOddsTrendsService(AsiaOddsTrendsService asiaOddsTrendsService) {
		this.asiaOddsTrendsService = asiaOddsTrendsService;
	}

}
