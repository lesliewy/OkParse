/**
 * 
 */
package service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.job.LeaguePointsJob;
import com.wy.okooo.job.MatchStatsJob;
import com.wy.okooo.service.AnalyseUtilService;

/**
 * @author leslie
 *
 */
public class TestAnalyseUtilService {

	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}
	
	@Test
	public void testInitCorp(){
		AnalyseUtilService analyseUtilService = (AnalyseUtilService) applicationContext
				.getBean("analyseUtilService");
		String prefix = "/home/leslie/MyProject/OkParse/html/2014/10/";
		String post = "/match.html";
		int beginDay = 3;
		int endDay = 10;
		for(int i=beginDay; i<=endDay; i++){
			analyseUtilService.initCorp(prefix + StringUtils.leftPad(String.valueOf(i), 2, '0') + post);
		}

	}
	
	
	@Test
	public void testUpdateEuroOddsChangeNum(){
		AnalyseUtilService analyseUtilService = (AnalyseUtilService) applicationContext
				.getBean("analyseUtilService");
		analyseUtilService.updateEuroOddsChangeNum();
	}
	
	@Test
	public void testCalcuCorpAvgTimeBeforeMatch(){
		AnalyseUtilService analyseUtilService = (AnalyseUtilService) applicationContext
				.getBean("analyseUtilService");
		analyseUtilService.calcuCorpAvgTimeBeforeMatch();
	}
	
	@Test
	public void testShowKellySummary(){
		AnalyseUtilService analyseUtilService = (AnalyseUtilService) applicationContext
				.getBean("analyseUtilService");
		String okUrlDate = "160201";
//		String baseDir = "/home/leslie/MyProject/OkParse/html/2015/03/04/";
		String baseDir = "/home/leslie/MyProject/OkParse/html/daily/match/2016/02/01/";
		Map<Integer, String> jobTypes = new HashMap<Integer, String>();
		for (int i = 85; i <= 85; i++){
			jobTypes.put(i, "A3");
		}
		analyseUtilService.showKellySummary(null, okUrlDate, jobTypes, baseDir);
	}
	
	@Test
	public void testGetEuroTransAsiaMap(){
//		AnalyseUtilService analyseUtilService = (AnalyseUtilService) applicationContext
//				.getBean("analyseUtilService");
//		String okUrlDate = "150404";
//		int beginMatchSeq = 301;
//		int endMatchSeq = 304;
//		analyseUtilService.getEuroTransAsiaMap(okUrlDate, beginMatchSeq, endMatchSeq);
	}
	
	/*
	 * 需要 exchangeInfo_84.html
	 */
	@Test
	public void testShowKellySummary2(){
		AnalyseUtilService analyseUtilService = (AnalyseUtilService) applicationContext
				.getBean("analyseUtilService");
		String okUrlDate = "150104";
		String matchName = "意甲";
		String baseDir = "/home/leslie/MyProject/OkParse/html/2015/01/04/";
		Map<Integer, String> jobTypes = new HashMap<Integer, String>();
		for (int i = 240; i <= 241; i++){
			jobTypes.put(i, "A3");
		}
		analyseUtilService.showKellySummary(okUrlDate, matchName, null, baseDir, null, null, null, null, null, null);
	}
	
	@Test
	public void testInitKellyCorpCount(){
		AnalyseUtilService analyseUtilService = (AnalyseUtilService) applicationContext
				.getBean("analyseUtilService");
//		String matchName = "英超";
//		analyseService.initKellyCorpCount(matchName);
		analyseUtilService.initKellyCorpCount();
	}
	
	@Test
	public void testShowKellyCountProb(){
		AnalyseUtilService analyseUtilService = (AnalyseUtilService) applicationContext
				.getBean("analyseUtilService");
		String matchName = "瑞士超";
		analyseUtilService.showKellyCountProb(matchName, null, null);
//		analyseService.showKellyCountProb();
	}
	
	@Test
	public void testInitLeaguePoints(){
		LeaguePointsJob leaguePointsJob = (LeaguePointsJob) applicationContext
				.getBean("leaguePointsJob");
		File allLeagueFile = new File("/home/leslie/MyProject/OkParse/html/leaguePoints/14_15/allLeague.html");
		leaguePointsJob.initLeaguePoints(allLeagueFile);
	}
	
	@Test
	public void testInitMatchStats(){
		MatchStatsJob matchStatsJob = (MatchStatsJob) applicationContext
				.getBean("matchStatsJob");
		String baseDir = "/home/leslie/MyProject/OkParse/html/2014/12/04/";
		matchStatsJob.initMatchStats(baseDir);
	}
	
	@Test
	public void testAnalyseScoreOdds(){
		AnalyseUtilService analyseUtilService = (AnalyseUtilService) applicationContext
				.getBean("analyseUtilService");
		String okUrlDate = "150202";
		List<Integer> matchSeqs = new ArrayList<Integer>();
		for(int i = 60; i <= 92; i++){
			matchSeqs.add(i);
		}
		analyseUtilService.analyseScoreOdds(okUrlDate, matchSeqs);;
	}
	
}
