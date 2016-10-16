/**
 * 
 */
package com.wy.okooo.job;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.wy.okooo.data.HtmlPersist;
import com.wy.okooo.domain.Match;
import com.wy.okooo.domain.MatchJob;
import com.wy.okooo.service.AnalyseService;
import com.wy.okooo.service.AnalyseUtilService;
import com.wy.okooo.service.ConfigService;
import com.wy.okooo.service.KellyMatchCountService;
import com.wy.okooo.service.MatchJobService;
import com.wy.okooo.service.MatchSkipService;
import com.wy.okooo.service.SingleMatchService;
import com.wy.okooo.util.MailUtils;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * @author leslie
 * * 将LATEST_0_MAX, LATEST_1_MAX 这些控制量放入数据库中,从数据库中读取就不需要重新部署了;
 * * 如果某场比赛已经存在了该JOB_TYPE的数据信息，就跳过; 不获取html, 也不做任何数据库的操作;
 * 
 */
public class ParseAndMailJob {
	
	private static Logger LOGGER = Logger.getLogger(ParseAndMailJob.class
			.getName());
	
	private SingleMatchService singleMatchService;
	
	private AnalyseService analyseService;
	
	private AnalyseUtilService analyseUtilService;
	
	private MatchJobService matchJobService;
	
	private ConfigService configService;
	
	private KellyMatchCountService kellyMatchCountService;
	
	private MatchSkipService matchSkipService;
	
	/*
	 * 默认值.
	 */
	// 不执行job的时间范围 LATEST_1_BEFORE < matchTime < LATEST_2_BEFORE  (min.)
	private Integer LATEST_1_BEFORE = 60;
	private Integer LATEST_2_BEFORE = 120;
	// 获取文件时开启的线程数. (历史: 10)
	private int NUM_OF_THREAD_MAIL_JOB = 10;
	// 删除超过该时间(s)的正在运行的JOB.
	private int DEL_R_UPPER_LIMIT = 18000;
	
	// 单位:min. 以此区分不同的jobType, 以A开头.
	private Set<Integer> jobTypeIntervals = null;
	// 
	private Map<String, Object> jobMap = null;
	
	// 所有的jobType.
	private static final String[] ALL_JOB_TYPES = {"A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9"};
	// 当前配置的jobType
	private List<String> currJobTypes = new ArrayList<String>();
	
	// 当前的okurldate
	private String currOkUrlDate = "";
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void processMatch() {
		LOGGER.info("JOB A - processMatch job begin...");
		long beginTime = System.currentTimeMillis();
		
		if(!initConfig()){
			return;
		}
		
		// 获取当天的match.html
		Calendar cal = OkParseUtils.buildCalByOkUrlDate(currOkUrlDate);
		String matchUrl = OkParseUtils.buildUrlByDate(cal);
		
		// 计算okUrlDate
		String okUrlDate = currOkUrlDate;
		
		// 清理时间超过18000s的R状态的job, 修改状态为D.  时间可配置.
		matchJobService.cleanLongTimeJob(DEL_R_UPPER_LIMIT, "A");
		
		// 根据 okUrlDate 查询是否有正在执行的job, 有则直接退出.
		List<MatchJob> runningJobs = matchJobService.getRunningJobs(okUrlDate, "A");
		if(runningJobs != null && !runningJobs.isEmpty()){
			LOGGER.info("JOB A - running job exists. " + runningJobs + ". return now...");
			return;
		}
		
		/*
		 * 2015-02-08: 不获取所有的比赛，对于A0，A1，A2，A3对应的区间内只执行一次.例如, 赛前20-25h属于A3，如果该match已经执行过，则跳过不再获取html. matchSeq不再是连续的,
		 * 需要做相应改动, 只修改html获取的，后面插入数据库(kellyAnalyseK23Thread, showKellySummary) 保持不变.
		 */
		// key: {matchSeq}_{jobType}
		Set<String> seqJobTypeSet = kellyMatchCountService.querySeqAndJobTypeByOkUrlDateInSet(okUrlDate);
		// 查看是否有需要跳过的比赛场次.
		Set<Integer> skipSeqSet = matchSkipService.querySkipMatchesByOkUrlDateInSet(okUrlDate);
		
		String dir = OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar + OkParseUtils.getDirPahtFromUrl(matchUrl);
		File parentDir = new File(dir);
		// 存在则删除， 不存在新建; 20150201: 不再删除整个目录，对于具体的html在persist的时候决定是否需要替换现有的;
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
		File matchHtml = new File(dir + File.separatorChar + OkConstant.MATCH_FILE_NAME);
		OkParseUtils.persistMatch(matchHtml, matchUrl, true);

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

		/*
		 * 获取Match对象, 为了避免match.html中match过多，先查询下job记录, 根据LOT_JOB中的BEGIN_MATCH_SEQ, 只选出部分.
		 * 如果没有查询到 BEGIN_MATCH_SEQ, 就解析所有的.
		 */
		// 获取Match对象.
		List<Match> matches = getMatchesFromHtml(matchHtmlFiles);
		if (matches == null || matches.isEmpty()) {
			LOGGER.error("JOB A - matches is null or empty. return now...");
			return;
		}
		
		List<Calendar> intervalCals = buildCalFromIntervals(jobTypeIntervals);
		
		String jobType = "";
		String status = OkConstant.JOB_STATE_RUNNING;
		String remark = "running";
		
		Calendar nowCal = Calendar.getInstance();
		
		// 不执行job的时间段.
		Calendar latest1Before = Calendar.getInstance();
		latest1Before.add(Calendar.MINUTE, LATEST_1_BEFORE);
		Calendar latest2Before = Calendar.getInstance();
		latest2Before.add(Calendar.MINUTE, LATEST_2_BEFORE);
		
		int beginMatchSeq = 1000;
		int endMatchSeq = 0;
		
		// 记录开赛时间，用于计算各场比赛的jobType, 不再是最近的那一场的jobType. 插入 LOT_KELLY_MATCH_COUNT;
		Map<Integer, String> jobTypes = new TreeMap<Integer, String>();
		int firstMatchFlag = 0;
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
			
			// 排除掉已经开赛的
			Calendar matchTime = Calendar.getInstance();
			matchTime.setTimeInMillis(match.getMatchTime().getTime());
			if(nowCal.after(matchTime)){
				continue;
			}
			
			int matchSeq = match.getMatchSeq();
			// 排除掉需要跳过的。
			if(skipSeqSet != null && skipSeqSet.contains(matchSeq)){
				continue;
			}
			
			// 某段时间内不执行
			if(firstMatchFlag++ <= 0){
				if((latest1Before.before(matchTime) && latest2Before.after(matchTime))){
					LOGGER.info(LATEST_1_BEFORE + "min <= matchTime <= " + LATEST_2_BEFORE + "min. return now...");
					break;
				}
			}
			
			// 计算需要处理的match, 从最近的时间开始。
			for(int i = intervalCals.size()-1; i >= 0; i--){
				Calendar intervalCal = intervalCals.get(i);
				if(intervalCal.after(matchTime)){
					jobType = ALL_JOB_TYPES[i];
	                if(!seqJobTypeSet.contains(matchSeq + "_" + jobType)){
	                	((List)jobMap.get(ALL_JOB_TYPES[i] + "_list")).add(match);
	                }
	    			jobTypes.put(matchSeq, jobType);
	    			beginMatchSeq = Math.min(beginMatchSeq, matchSeq);
	    			endMatchSeq = Math.max(endMatchSeq, matchSeq);
	                break;
				}
			}
		}
		
		List<Match> toProcessMatches = new ArrayList<Match>();
		for(String type : currJobTypes){
			toProcessMatches.addAll((List)jobMap.get(type + "_list"));
		}
		if(toProcessMatches.isEmpty()){
			LOGGER.info("JOB A - no matches to process, return now...");
			return;
		}
		Set<Integer> toProcessMatchSeqs = new TreeSet<Integer>();
		for(Match match : toProcessMatches){
			toProcessMatchSeqs.add(match.getMatchSeq());
		}
		
		// 最近一场比赛的jobType.
		String jobTypeBegin = jobTypes.get(beginMatchSeq);
		LOGGER.info("JOB A - jobTypeBegin: " + jobTypeBegin + "  " + beginMatchSeq + "-" + endMatchSeq);
		LOGGER.info("JOB A - jobTypes size: " + jobTypes.size() + ": " + jobTypes);
		LOGGER.info("JOB A - toProcessMatches size: " + toProcessMatches.size() + ": " + toProcessMatchSeqs);
		
		// 查询job是否已经执行成功过.  条件: okUrlDate, beginMatchSeq, jobType, status   修改成每个时间段内只获取一次后，此处检查不再需要
		
		// 登记job, 状态为 R-正在执行.
		MatchJob newJob = new MatchJob();
		newJob.setOkUrlDate(okUrlDate);
		newJob.setBeginMatchSeq(beginMatchSeq);
		newJob.setEndMatchSeq(endMatchSeq);
		newJob.setJobType(jobTypeBegin);
		newJob.setRemark(remark);
		Timestamp now = new Timestamp(Calendar.getInstance()
				.getTimeInMillis());
		newJob.setBeginTime(now);
		newJob.setTimestamp(now);
		StringBuilder timeType = new StringBuilder("");
		Iterator<Integer> iter = jobTypeIntervals.iterator();
		int index = 0;
		while(iter.hasNext()){
			timeType.append("A").append(index).append(":").append(iter.next()).append("|");
			index++;
		}
		newJob.setTimeType(timeType.toString());
		// 修改成每个时间段只执行一次后，有可能已经存在状态为S的, 需要先删除已经存在的S状态的
		newJob.setStatus(OkConstant.JOB_STATE_SUCCESS);
		matchJobService.deleteJobById(newJob);
		newJob.setStatus(status);
		matchJobService.insertJob(newJob);
		
		// 获取euroOddsChange页面之前，先删除包含toProcessMatchSeqs中的matchSeq的页面.
		OkParseUtils.deleteExistedFiles(matchHtml.getParentFile(), toProcessMatchSeqs, OkConstant.EURO_ODDS_CHANGE_FILE_NAME_BASE);
		long beginPersist = System.currentTimeMillis();
		analyseService.persistCorpEuroOddsChangeKellyThread(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, NUM_OF_THREAD_MAIL_JOB, beginMatchSeq, endMatchSeq,
				matches, toProcessMatchSeqs, false, false);
		analyseService.persistCorpEuroOddsChangeKelly(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, beginMatchSeq, endMatchSeq, 
				matches, toProcessMatchSeqs, false, false);
		
		// 获取 交易盈亏页面.
		OkParseUtils.deleteExistedFiles(matchHtml.getParentFile(), toProcessMatchSeqs, OkConstant.EXCHANGE_INFO_FILE_NAME_BASE);
		HtmlPersist persist = new HtmlPersist();
		persist.persistExchangeInfoBatch(OkConstant.JOB_HTML_FILE_BASE_DIR + OkParseUtils.getDirPahtFromOkUrlDate(okUrlDate) + File.separatorChar,
				beginMatchSeq, endMatchSeq, toProcessMatchSeqs, false);
		// 再次执行确保文件都已下载.
		persist.persistExchangeInfoBatch(OkConstant.JOB_HTML_FILE_BASE_DIR + OkParseUtils.getDirPahtFromOkUrlDate(okUrlDate) + File.separatorChar,
				beginMatchSeq, endMatchSeq, toProcessMatchSeqs, false);

		// 获取 欧赔转换为亚盘页面.
		OkParseUtils.deleteExistedFiles(matchHtml.getParentFile(), toProcessMatchSeqs, OkConstant.EURO_TRANS_ASIA_FILE_NAME_BASE + "_");
		persist.persistEuroTransAsiaWithLimit(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, toProcessMatches, 
				toProcessMatchSeqs, 0, 0, false);
		persist.persistEuroTransAsiaWithLimit(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, toProcessMatches, 
				toProcessMatchSeqs, 0, 0, false);
		persist.persistEuroTransAsiaWithLimit(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, toProcessMatches, 
				toProcessMatchSeqs, 0, 0, false);
		
		LOGGER.info("JOB A - persist success, total time: " + (System.currentTimeMillis() - beginPersist)/1000 + " s.");
		
		// k2 k3(k4)  kellyAnalyseK23Thread() 中不能指定limitedMatchSeqs, 因为如果指定了，LOT_KELLY_CORP_RESULT会先清掉当天的，这样就只包含limitedMatchSeqs中的了,
		// 而limitedMatchSeqs 中的不全.
		long beginKelly = System.currentTimeMillis();
		analyseService.kellyAnalyseK23Thread(matches, dir + File.separatorChar, beginMatchSeq, endMatchSeq, null, jobTypes, okUrlDate);
		
		StringBuilder sb = analyseUtilService.showKellySummary(matches, okUrlDate, jobTypes, dir + File.separatorChar);
		LOGGER.info("JOB A - analyse success, total time: " + (System.currentTimeMillis() - beginKelly)/1000 + " s.");
		
		/*
		 * 2015-05-01 去掉该预测.
		// high correct rate predict
		long beginHighPredict = System.currentTimeMillis();
		sb.append(analyseService.highKellyPredict(okUrlDate, "K3"));
		sb.append(analyseService.highKellyPredict(okUrlDate, "K4"));
		sb.append(analyseService.highKellyPredict(okUrlDate, "K32"));
		sb.append(analyseService.highKellyPredict(okUrlDate, "K42"));
		LOGGER.info("JOB A - high correct rate predict success, total time: " + (System.currentTimeMillis() - beginHighPredict)/1000 + " s.");
		*/
		
		status = OkConstant.JOB_STATE_SUCCESS;
		remark = "success";
		// 发邮件, 发送到163邮箱的, 手机上可以实时提示;  gmail 会延迟或者不会提醒，需手动接收;
		long beginMail = System.currentTimeMillis();
		try {
			Map<String, String> mailMap = new HashMap<String, String>();
			mailMap.put("jobType", jobTypeBegin);
			mailMap.put("okUrlDate", okUrlDate);
			mailMap.put("beginMatchSeq", String.valueOf(beginMatchSeq));
			mailMap.put("endMatchSeq", String.valueOf(endMatchSeq));
			MailUtils.sendMailTo163(sb.toString(), mailMap);
		} catch (MessagingException e) {
			status = OkConstant.JOB_STATE_FAILED;
			remark = "send mail failed";
			LOGGER.error("send mail failed, " + e);
		}
		LOGGER.info("JOB A - send mail success, total time: " + (System.currentTimeMillis() - beginMail)/1000 + " s.");
		
		// 修改job状态. 将 R状态 修改为 S状态.
		MatchJob updateJob = new MatchJob();
		updateJob.setOkUrlDate(okUrlDate);
		updateJob.setJobType("A");
		updateJob.setStatus(status);
		updateJob.setRemark(remark);
		updateJob.setTimestamp(new Timestamp(Calendar.getInstance()
				.getTimeInMillis()));
		newJob.setStatus(status);
		matchJobService.updateR2S(updateJob);
		
		// 保存日志.
		String nowTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String logFileName = okUrlDate + "_" + beginMatchSeq + "_" + endMatchSeq + "_" + jobTypeBegin + "_" + nowTime + ".log";
		File log = new File(OkConstant.DAILY_LOG_FILE_DIR + File.separatorChar + logFileName);
		OkParseUtils.persistByStr(log, sb.toString());
		LOGGER.info("JOB A - processMatch total time: " + (System.currentTimeMillis() - beginTime)/(1000 * 60) + " min.");
	}
	
	/**
	 * 初始化配置参数.
	 * @return
	 */
	private boolean initConfig(){
		// 查询配置参数LOT_CONFIG
		Map<String, String> configs = configService.queryAllConfigInMap();
		currOkUrlDate = configs.get(OkConstant.CONFIG_CURR_OK_URL_DATE);
		String latest1BeforeStr = configs.get(OkConstant.CONFIG_LATEST_1_BEFORE);
		String latest2BeforeStr = configs.get(OkConstant.CONFIG_LATEST_2_BEFORE);
		
		String numOfThreadMailJobStr = configs.get(OkConstant.CONFIG_NUM_OF_THREAD_MAIL_JOB);
		String configJobAMatchNumStr = configs.get(OkConstant.CONFIG_JOB_A_MATCH_NUM);
		String delrUpperLimitStr = configs.get(OkConstant.CONFIG_DEL_R_UPPER_LIMIT);
		String jobTypeIntervalStr = configs.get(OkConstant.PARSE_MAIL_JOB_TYPE_INTERVAL);
		
		// 必须是整数，才可以生效.
		LATEST_1_BEFORE = StringUtils.isNumeric(latest1BeforeStr) ? Integer.valueOf(latest1BeforeStr) : LATEST_1_BEFORE;
		LATEST_2_BEFORE = StringUtils.isNumeric(latest2BeforeStr) ? Integer.valueOf(latest2BeforeStr) : LATEST_2_BEFORE;
		NUM_OF_THREAD_MAIL_JOB = StringUtils.isNumeric(numOfThreadMailJobStr) ? Integer.valueOf(numOfThreadMailJobStr) : NUM_OF_THREAD_MAIL_JOB;
		DEL_R_UPPER_LIMIT = StringUtils.isNumeric(delrUpperLimitStr) ? Integer.valueOf(delrUpperLimitStr) : DEL_R_UPPER_LIMIT;
		
		if(StringUtils.isBlank(currOkUrlDate)){
			LOGGER.error("config CONFIG_CURR_OK_URL_DATE is null, return now.");
			return false;
		}
		// 处理jobTypeInterval
		if(StringUtils.isBlank(jobTypeIntervalStr)){
			jobTypeIntervalStr = OkConstant.JOB_A_INTERVAL_DEFAULT;
		}
		String[] jobTypeIntervalArr = jobTypeIntervalStr.split(",");
		if(jobTypeIntervalArr == null || jobTypeIntervalArr.length == 0){
			LOGGER.error("config PARSE_MAIL_JOB_TYPE_INTERVAL is blank, return now");
			return false;
		}
		jobTypeIntervals = new TreeSet<Integer>(Collections.reverseOrder());
		for(String jobTypeInterval : jobTypeIntervalArr){
			jobTypeIntervals.add(Integer.valueOf(jobTypeInterval));
		}
		
		// 处理matchNum
		if(StringUtils.isBlank(configJobAMatchNumStr)){
			configJobAMatchNumStr = OkConstant.CONFIG_JOB_A_MATCH_NUM_DEFAULT;
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
	
	private List<Match> getMatchesFromHtml(List<File> matchHtmlFiles){
		MatchJob queryJob = new MatchJob();
		queryJob.setOkUrlDate(currOkUrlDate);
		queryJob.setJobType("A");
		Integer queryBeginSeq = matchJobService.queryMaxBeginSeqByOkUrlDate(queryJob);
		Integer queryEndSeq = 1000;
		if(queryBeginSeq != null){
			queryEndSeq = queryBeginSeq + 1000;
		}else{
			queryBeginSeq = 1;
		}
		
		return singleMatchService.getAllMatchFromFiles(matchHtmlFiles, queryBeginSeq, queryEndSeq);
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
	
	public SingleMatchService getSingleMatchService() {
		return singleMatchService;
	}

	public void setSingleMatchService(SingleMatchService singleMatchService) {
		this.singleMatchService = singleMatchService;
	}

	public AnalyseService getAnalyseService() {
		return analyseService;
	}

	public void setAnalyseService(AnalyseService analyseService) {
		this.analyseService = analyseService;
	}

	public AnalyseUtilService getAnalyseUtilService() {
		return analyseUtilService;
	}

	public void setAnalyseUtilService(AnalyseUtilService analyseUtilService) {
		this.analyseUtilService = analyseUtilService;
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

	public MatchSkipService getMatchSkipService() {
		return matchSkipService;
	}

	public void setMatchSkipService(MatchSkipService matchSkipService) {
		this.matchSkipService = matchSkipService;
	}
	
}
