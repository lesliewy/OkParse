/**
 * 
 */
package com.wy.okooo.job;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.wy.okooo.domain.LeaguePoints;
import com.wy.okooo.service.LeaguePointsService;
import com.wy.okooo.service.impl.LeaguePointsServiceImpl;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * 
 * 定时更新各个联赛的积分情况;
 * 
 * @author leslie
 * 
 */
public class LeaguePointsJob {
	
	private static Logger LOGGER = Logger.getLogger(LeaguePointsJob.class
			.getName());
	
	private LeaguePointsService leaguePointsService;
	
	public void processLeaguePoints(){
		LOGGER.info("processLeaguePoints job begin...");
		long begin = System.currentTimeMillis();
		// 获取联赛积分页面(http://www.okooo.com/soccer/league/17/);
		String baseDirPath = "/home/leslie/MyProject/OkParse/html/leaguePoints/14_15/";
		persistLeaguePoints(baseDirPath);
		
		File allLeagueFile = new File("/home/leslie/MyProject/OkParse/html/leaguePoints/14_15/allLeague.html");
		initLeaguePoints(allLeagueFile);
		LOGGER.info("LeaguePointsJob end. total time: " + (System.currentTimeMillis() - begin)/1000 + " s.");
	}
	
	/**
	 * 获取赛事一览(联赛积分; http://www.okooo.com/soccer/league/17/)页面.
	 * 
	 */
	public void persistLeaguePoints(String baseDirPath) {
		long begin = System.currentTimeMillis();
		File baseDir = new File(baseDirPath);
		if(!baseDir.exists()){
			baseDir.mkdir();
		}
		
		// 先获取 http://www.okooo.com/soccer/
		String allLeagueUrl = "http://www.okooo.com/soccer/";
		File allLeagueFile = new File(baseDirPath + "allLeague.html");
		// 先删除
		if(allLeagueFile.exists()){
			allLeagueFile.delete();
		}
		OkParseUtils.persistByUrl(allLeagueFile, allLeagueUrl, "gb2312", 2000);
		// 解析allLeague
		LeaguePointsService leaguePointsService = new LeaguePointsServiceImpl();
		List<String> urls = leaguePointsService.getLeaguePointsUrl(allLeagueFile);
		if(urls == null){
			LOGGER.error("urls is empty, return now.");
			return;
		}
		
		File leaguePointsFile = null;
		String leaguePointsFilePath = "";
		String leaguePointsUrl = "";
		String leaguePointsPre = "leaguePoints";
		for(String url : urls){
			String leagueId = url.split("/")[3];
			leaguePointsFilePath = baseDirPath + leaguePointsPre + "_" + leagueId + ".html";
			leaguePointsFile = new File(leaguePointsFilePath);

			// 文件存在则删除;
			if(OkParseUtils.checkFileExists(leaguePointsFile)){
				leaguePointsFile.delete();
			}
			
			leaguePointsUrl = "http://www.okooo.com" + url;
			OkParseUtils.persistByUrl(leaguePointsFile, leaguePointsUrl, "gb2312", 2000);
			if(!leaguePointsFile.exists() || leaguePointsFile.length() < 50000){
				OkParseUtils.persistByUrl(leaguePointsFile, leaguePointsUrl, "gb2312", 2000);
			}
			
//			LOGGER.info("leaguePointsFilePath: " + leaguePointsFilePath + "; leaguePointsUrl: " + leaguePointsUrl + "; leagueId: " + leagueId);
		}
		LOGGER.info("persistLeaguePoints total time: " + (System.currentTimeMillis() - begin)/1000 + "s.");
	}
	
	/**
	 * 登记各个联赛积分排名情况(LOT_LEAGUE_POINTS);
	 * @param allLeagueFile
	 */
	public void initLeaguePoints(File allLeagueFile){
		long begin = System.currentTimeMillis();
		// 获取leaguePoints html.
		List<File> leaguePointsHtmls = OkParseUtils.getSameDirFilesFromMatch(
				allLeagueFile, OkConstant.LEAGUE_POINTS_FILE_NAME_BASE);
		if(leaguePointsHtmls == null || leaguePointsHtmls.isEmpty()){
			LOGGER.info("leaguePointsHtmls is empty, return now.");
			return;
		}
		
		for(File leaguePointsFile : leaguePointsHtmls){
//			LOGGER.info("process file: " + leaguePointsFile.getAbsolutePath());
			if(!leaguePointsFile.exists() || leaguePointsFile.length() < 50000){
				continue;
			}
			List<LeaguePoints> LeaguePointsList = leaguePointsService.getLeaguePointsFromFile(leaguePointsFile);
			if(LeaguePointsList == null || LeaguePointsList.isEmpty()){
				continue;
			}
			// 先删除该赛季该联赛的.
			LeaguePoints deleteLeaguePoints = new LeaguePoints();
			deleteLeaguePoints.setLeagueTime(LeaguePointsList.get(0).getLeagueTime());
			deleteLeaguePoints.setLeagueId(LeaguePointsList.get(0).getLeagueId());
			leaguePointsService.deleteLeaguePointsByLeague(deleteLeaguePoints);
			
			leaguePointsService.insertLeaguePointsBatch(LeaguePointsList);
		}
		LOGGER.info("initLeaguePoints total time: " + (System.currentTimeMillis() - begin)/1000 + " s.");
	}

	public LeaguePointsService getLeaguePointsService() {
		return leaguePointsService;
	}

	public void setLeaguePointsService(LeaguePointsService leaguePointsService) {
		this.leaguePointsService = leaguePointsService;
	}

}
