/**
 * 
 */
package com.wy.okooo.job;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.wy.okooo.domain.Match;
import com.wy.okooo.domain.MatchJob;
import com.wy.okooo.domain.ScoreOdds;
import com.wy.okooo.service.MatchJobService;
import com.wy.okooo.service.ScoreOddsService;
import com.wy.okooo.service.SingleMatchService;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * 
 * 定时更新比分的赔率.
 * 
 * @author leslie
 * 
 */
public class ScoreOddsJob {
	
	private static Logger LOGGER = Logger.getLogger(ScoreOddsJob.class
			.getName());
	
	private ScoreOddsService scoreOddsService;
	
	private MatchJobService matchJobService;
	
	private SingleMatchService singleMatchService;
	
	public void processScoreOdds(){
		LOGGER.info("processScoreOdds job begin...");
		long begin = System.currentTimeMillis();
		
		// 获取现在的okUrlDate, 已经降序排列了，第一个即为现在的.
		List<MatchJob> matchJobList = matchJobService.queryOkUrlDateFromMatchJob();
		if(matchJobList == null || matchJobList.isEmpty()){
			LOGGER.info("matchJobList is null, return now.");
			return;
		}
		String okUrlDate = matchJobList.get(0).getOkUrlDate();
		
		// 存在且文件先删除
		File scoreOddsFile = new File(OkConstant.FILE_PATH_BASE + OkParseUtils.getDirPahtFromOkUrlDate(okUrlDate) + "/"
				+ "scoreOdds.html");
		if(scoreOddsFile.exists()){
			scoreOddsFile.delete();
		}
		// 获取最新的比分页面.
		String scoreOddsUrl = "http://www.okooo.com/danchang/bifen/";
		OkParseUtils.persistByUrl(scoreOddsFile, scoreOddsUrl, "gb2312", 2000);
		// 确保获取成功
		if(!scoreOddsFile.exists()){
			OkParseUtils.persistByUrl(scoreOddsFile, scoreOddsUrl, "gb2312", 2000);
		}
		if(!scoreOddsFile.exists()){
			LOGGER.info("get scoreOdds.html failed.");
			return;
		}
		initScoreOdds(OkConstant.FILE_PATH_BASE + OkParseUtils.getDirPahtFromOkUrlDate(okUrlDate) + "/");
		LOGGER.info("processScoreOdds job end. total time: " + (System.currentTimeMillis() - begin)/1000 + " s.");
	}
	
	public void initScoreOdds(String baseDir){
		File matchHtml = new File(baseDir + "match.html");
		if(!matchHtml.exists()){
			LOGGER.info(matchHtml.getAbsolutePath() + " not exists, return now.");
			return;
		}
		File scoreOddsFile = new File(baseDir + "scoreOdds.html");
		if(!scoreOddsFile.exists()){
			LOGGER.info(scoreOddsFile.getAbsolutePath() + " not exists, return now.");
			return;
		}
		List<Match> matches = new ArrayList<Match>(2 ^ 10);
		List<Match> oneMatchHtml = new ArrayList<Match>();
		oneMatchHtml = singleMatchService.getAllMatchFromFile(matchHtml, 0, 1000);
		matches.addAll(oneMatchHtml);

		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return now...");
			return;
		}
		
		// 计算intervalType, 每场比赛都不同.
		Calendar b0Cal = Calendar.getInstance();
		Calendar b1Cal = Calendar.getInstance();
		Calendar b2Cal = Calendar.getInstance();
		b0Cal.add(Calendar.HOUR, 20);
		b1Cal.add(Calendar.HOUR, 10);
		b2Cal.add(Calendar.HOUR, 5);
		Map<Integer, String> intervalTypeMap = new HashMap<Integer, String>();
		for(Match match : matches){
			Integer matchSeq = match.getMatchSeq();
			Timestamp matchTime = match.getMatchTime();
			Calendar matchCal = Calendar.getInstance();
			matchCal.setTimeInMillis(matchTime.getTime());
			if(b0Cal.before(matchCal)){
				intervalTypeMap.put(matchSeq, "B0");
			}else if(b1Cal.before(matchCal) && b0Cal.after(matchCal)){
				intervalTypeMap.put(matchSeq, "B1");
			}else if(b2Cal.before(matchCal) && b1Cal.after(matchCal)){
				intervalTypeMap.put(matchSeq, "B2");
			}else{
				intervalTypeMap.put(matchSeq, "B3");
			}
		}

		List<ScoreOdds> scoreOddsList = scoreOddsService.getScoreOddsFromFile(scoreOddsFile, intervalTypeMap);
		// 先删除.
		scoreOddsService.deleteScoreOdds(scoreOddsList);
		scoreOddsService.insertScoreOddsBatch(scoreOddsList);
	}

	public ScoreOddsService getScoreOddsService() {
		return scoreOddsService;
	}

	public void setScoreOddsService(ScoreOddsService scoreOddsService) {
		this.scoreOddsService = scoreOddsService;
	}

	public MatchJobService getMatchJobService() {
		return matchJobService;
	}

	public void setMatchJobService(MatchJobService matchJobService) {
		this.matchJobService = matchJobService;
	}

	public SingleMatchService getSingleMatchService() {
		return singleMatchService;
	}

	public void setSingleMatchService(SingleMatchService singleMatchService) {
		this.singleMatchService = singleMatchService;
	}

}
