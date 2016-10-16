/**
 * 
 */
package com.wy.okooo.job;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.wy.okooo.data.HtmlPersist;
import com.wy.okooo.domain.Match;
import com.wy.okooo.domain.MatchJob;
import com.wy.okooo.domain.MatchStats;
import com.wy.okooo.service.MatchJobService;
import com.wy.okooo.service.MatchStatsService;
import com.wy.okooo.service.SingleMatchService;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * 定时获取已经结束的okUrlDate的比赛的技术统计.
 * 
 * @author leslie
 * 
 */
public class MatchStatsJob {
	
	private static Logger LOGGER = Logger.getLogger(MatchStatsJob.class
			.getName());
	
	private SingleMatchService singleMatchService;
	
	private MatchStatsService matchStatsService;
	
	private MatchJobService matchJobService;
	
	private HtmlPersist persist = new HtmlPersist();
	
	public void processMatchStats(){
		LOGGER.info("processMatchStats job begin...");
		long begin = System.currentTimeMillis();
		// 构造路径， 对于 LOT_JOB 中已经完场的okUrlDate(okUrlDate递减排序中从第二个开始), 且在 LOT_MATCH_STATS 中不存在的.
		List<MatchJob> matchJobsList = matchJobService.queryOkUrlDateFromMatchJob();
		List<MatchStats> matchStatsList = matchStatsService.queryOkUrlDateFromMatchStats();
		List<String> okUrlDateMatchJobs = new ArrayList<String>();
		List<String> okUrlDateMatchStats = new ArrayList<String>();
		if(matchJobsList != null){
			for(MatchJob matchJob : matchJobsList){
				okUrlDateMatchJobs.add(matchJob.getOkUrlDate());
			}
		}
		if(matchStatsList != null){
			for(MatchStats matchStats : matchStatsList){
				okUrlDateMatchStats.add(matchStats.getOkUrlDate());
			}
		}

		// 去掉当前正在执行的okUrlDate, 因为不是所有比赛都结束了.
		if(!okUrlDateMatchJobs.isEmpty()){
			okUrlDateMatchJobs.remove(0);
		}
		
		for(String okUrlDate : okUrlDateMatchJobs){
			if(okUrlDateMatchStats.contains(okUrlDate)){
				continue;
			}
			
			LOGGER.info("process okUrlDate: " + okUrlDate);
			String baseDir = OkConstant.FILE_PATH_BASE + OkParseUtils.getDirPahtFromOkUrlDate(okUrlDate) + "/";
			// 首先获取最新的match.html
			persist.persistMatchByOkUrlDate(okUrlDate);
			
			persistMatchStats(baseDir);
			// 再次执行;
			persistMatchStats(baseDir);
			persistMatchStats(baseDir);
			initMatchStats(baseDir);
		}
		LOGGER.info("processMatchStats total time: " + (System.currentTimeMillis() - begin)/1000 + " s.");
	}
	
	/**
	 * 获取球队阵容页面(http://www.okooo.com/soccer/match/768266/), 解析其中的技术统计.
	 * @param baseDir
	 * @param beginMatchSeq
	 * @param endMatchSeq
	 */
	public void persistMatchStats(String baseDir){
		long beginTime = System.currentTimeMillis();
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(baseDir, OkConstant.MATCH_FILE_NAME);

		if(matchHtmlFiles != null && matchHtmlFiles.size() > 32){
			LOGGER.info("more than 1 month, return now.");
			return;
		}
		
		String preMatchStatsUrl = "http://www.okooo.com/soccer/match/";
		String matchStatsUrl = "";
		File matchStatsFile = null;
		for(File matchHtml : matchHtmlFiles){
			List<Match> oneMatchHtml = new ArrayList<Match>();
			oneMatchHtml = singleMatchService.getAllMatchFromFile(matchHtml, 0, 0);
			LOGGER.info("matchHtmlFile: " + matchHtml.getAbsolutePath() + "; num of matches: " + oneMatchHtml.size());
			if (oneMatchHtml == null || oneMatchHtml.isEmpty()) {
				LOGGER.error("matches is null or empty. return now...");
				return;
			}

			String baseMatchStatsPath = matchHtml.getAbsolutePath().replaceFirst(
					OkConstant.MATCH_FILE_NAME, OkConstant.MATCH_STATS_FILE_NAME);
			for(Match match : oneMatchHtml){
				Long okMatchId = match.getOkMatchId();
				Integer matchSeq = match.getMatchSeq();
				if(okMatchId == null){
					continue;
				}
				// 存在且文件足够大则跳过.
				matchStatsFile = new File(baseMatchStatsPath + "_" + matchSeq + ".html");
				if(matchStatsFile.exists() && matchStatsFile.length() > 1000){
					continue;
				}
				
//				LOGGER.info("process matchSeq: " + matchSeq);
				// 直接构造url
				matchStatsUrl = preMatchStatsUrl + okMatchId + "/";
				OkParseUtils.persistByUrl(matchStatsFile, matchStatsUrl, "gb2312", 2000);
			}
		}
		
		LOGGER.info("total time: " + (System.currentTimeMillis() - beginTime)/1000 + " s.");
	}
	
	public void initMatchStats(String baseDir){
		long begin = System.currentTimeMillis();
		File matchHtml = new File(baseDir + "match.html");
		if(!matchHtml.exists()){
			LOGGER.info(matchHtml.getAbsolutePath() + " not exists, return now.");
			return;
		}
		List<File> matchStatsHtmls = OkParseUtils.getSameDirFilesFromMatch(
				matchHtml, OkConstant.MATCH_STATS_FILE_NAME);
		if(matchStatsHtmls == null || matchStatsHtmls.isEmpty()){
			LOGGER.info("matchStatsHtmls is empty, return now.");
			return;
		}
		
		for(File matchStatsHtml : matchStatsHtmls){
//			LOGGER.info("process file: " + matchStatsHtml.getAbsolutePath());
			if(!matchStatsHtml.exists() || matchStatsHtml.length() < 100){
				continue;
			}
			MatchStats matchStats = matchStatsService.getMatchStatsFromFile(matchStatsHtml);
			if(matchStats == null){
				continue;
			}
			// 先删除;
			Long okMatchId = matchStats.getOkMatchId();
			matchStatsService.deleteMatchStats(okMatchId);
			
			matchStatsService.insertMatchStats(matchStats);
		}
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/1000 + " s.");
	
	}
	
	public SingleMatchService getSingleMatchService() {
		return singleMatchService;
	}

	public void setSingleMatchService(SingleMatchService singleMatchService) {
		this.singleMatchService = singleMatchService;
	}

	public MatchStatsService getMatchStatsService() {
		return matchStatsService;
	}

	public void setMatchStatsService(MatchStatsService matchStatsService) {
		this.matchStatsService = matchStatsService;
	}

	public MatchJobService getMatchJobService() {
		return matchJobService;
	}

	public void setMatchJobService(MatchJobService matchJobService) {
		this.matchJobService = matchJobService;
	}

}
