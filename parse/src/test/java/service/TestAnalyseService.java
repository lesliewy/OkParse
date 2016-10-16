/**
 * 
 */
package service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.domain.EuropeOdds;
import com.wy.okooo.domain.ExchangeTransactionProp;
import com.wy.okooo.job.MatchStatsJob;
import com.wy.okooo.service.AnalyseService;

/**
 * @author leslie
 *
 */
public class TestAnalyseService {

	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}
	
	@Test
	public void testAnalyse(){
		AnalyseService analyseService = (AnalyseService) applicationContext
				.getBean("analyseService");
		String url = "http://www.okooo.com/danchang/141001/";
		analyseService.analyse(url);
	}
	
	@Test
	public void testAnalyseFromFile(){
		AnalyseService analyseService = (AnalyseService) applicationContext
				.getBean("analyseService");
		String matchDir = "/home/leslie/MyProject/OkParse/html/2015/01/01/";
//		String matchDir = "/home/leslie/MyProject/OkParse/html/daily/match/2015/01/01/";
		Integer beginMatchSeq = 10;
		Integer endMatchSeq = 20;
		analyseService.analyseFromFile(null, matchDir, beginMatchSeq, endMatchSeq);
	}
	
	@Test
	public void testCompLossIndex(){
		AnalyseService analyseService = (AnalyseService) applicationContext
				.getBean("analyseService");
		EuropeOdds europeOdds = new EuropeOdds();
		ExchangeTransactionProp transactionProp = new ExchangeTransactionProp();
		
		europeOdds.setLossRatio(0.90f);
		europeOdds.setHostOdds(1.65f);
		europeOdds.setEvenOdds(3.50f);
		europeOdds.setVisitingOdds(4.53f);
		
		transactionProp.setHostComp(74.09f);
		transactionProp.setEvenComp(11.68f);
		transactionProp.setVisitingComp(14.23f);
		Map<String, Float> result1 = analyseService.compLossIndex(europeOdds, null, transactionProp);
		System.out.println("host: " + result1.get("host") + "; even: " + result1.get("even") + "; visiting: " + result1.get("visiting"));
		
		europeOdds.setLossRatio(0.90f);
		europeOdds.setHostOdds(1.72f);
		europeOdds.setEvenOdds(3.60f);
		europeOdds.setVisitingOdds(3.95f);
		
		Map<String, Float> result2 = analyseService.compLossIndex(europeOdds, null, transactionProp);
		System.out.println("host: " + result2.get("host") + "; even: " + result2.get("even") + "; visiting: " + result2.get("visiting"));
	}
	
	/*
	 * 需要 match.html, euroOddsChange_487_117.html;
	 * 
	 */
	@Test
	public void testKellyAnalyseK2(){
		AnalyseService analyseService = (AnalyseService) applicationContext
				.getBean("analyseService");
//		String matchDirPrefix = "/home/leslie/MyProject/OkParse/html/2015/";
		String matchDirPrefix = "/home/leslie/MyProject/OkParse/html/daily/match/2015/";
		
		String[] matchDirPostArr = {"03/05/"};
		
		int beginMatchSeq = 22;
		int endMatchSeq = 43;
		for(String matchDirPost : matchDirPostArr){
			analyseService.kellyAnalyseK2(null, matchDirPrefix + matchDirPost, beginMatchSeq, endMatchSeq, null);
		}
	}
	
	/*
	 * 需要 match.html, euroOddsChange_487_117.html;
	 * 
	 */
	@Test
	public void testKellyAnalyseK3(){
		AnalyseService analyseService = (AnalyseService) applicationContext
				.getBean("analyseService");
		String matchDirPrefix = "/home/leslie/MyProject/OkParse/html/2015/";
		
		String[] matchDirPostArr = {"01/04/"};
		
		int beginMatchSeq = 29;
		int endMatchSeq = 45;
		for(String matchDirPost : matchDirPostArr){
			analyseService.kellyAnalyseK3(null, matchDirPrefix + matchDirPost, beginMatchSeq, endMatchSeq, null, null);
		}
	}
	
	@Test
	public void testPersistCorpEuroOddsChangeKelly(){
		AnalyseService analyseService = (AnalyseService) applicationContext
				.getBean("analyseService");
		Calendar cal = Calendar.getInstance();
		int beginDay = 4;
		int endDay = 4;
		int beginMatchSeq = 29;
		int endMatchSeq = 45;
		String baseDir = "/home/leslie/MyProject/OkParse/html/";
//		String baseDir = "/home/leslie/MyProject/OkParse/html/daily/match/";
		
		for(int i = beginDay; i <= endDay; i++){
			// month =  实际月份 - 1.
			cal.set(2015, 0, i, 00, 00);
			analyseService.persistCorpEuroOddsChangeKelly(baseDir, cal, beginMatchSeq, endMatchSeq, null, null, false, true);
		}
	}
	
	@Test
	public void testPersistCorpEuroOddsChangeKellyThread(){
		AnalyseService analyseService = (AnalyseService) applicationContext
				.getBean("analyseService");
		Calendar cal = Calendar.getInstance();
		int numOfThread = 10;
		// month =  实际月份 - 1.   150102
		cal.set(2015, 0, 4, 00, 00);
		String baseDir = "/home/leslie/MyProject/OkParse/html/";
		int beginMatchSeq = 29;
		int endMatchSeq = 45;
		analyseService.persistCorpEuroOddsChangeKellyThread(baseDir, cal, numOfThread, beginMatchSeq, endMatchSeq, null, null, false, true);
	}
	
	@Test
	public void testHighKellyPredict(){
		AnalyseService analyseService = (AnalyseService) applicationContext
				.getBean("analyseService");
		String okUrlDate = "141204";
		String ruleType = "K32";
		analyseService.highKellyPredict(okUrlDate, ruleType);
	}
	
	@Test
	public void testPersistMatchStats(){
		MatchStatsJob matchStatsJob = (MatchStatsJob) applicationContext
				.getBean("matchStatsJob");
		String baseDir = "/home/leslie/MyProject/OkParse/html/2014/12/";
		matchStatsJob.persistMatchStats(baseDir);
	}
	
	@Test
	public void test1(){
		String a = "-0.218|-0.262|0.590|";
		System.out.println("a[0]: " + a.split("\\|")[0]);
		System.out.println("a[1]: " + a.split("\\|")[1]);
		System.out.println("a[2]: " + a.split("\\|")[2]);
	}
	
	@Test
	public void testAsiaOddsAnalyse(){
		AnalyseService analyseService = (AnalyseService) applicationContext
				.getBean("analyseService");
		String okUrlDate = "150305";
		String matchDir = "/home/leslie/MyProject/OkParse/html/daily/match/2015/03/05/";
		Map<Integer, String> jobTypes = new HashMap<Integer, String>();
		for (int i = 12; i <= 15; i++){
			jobTypes.put(i, "A0");
		}
		int beginMatchSeq = 12;
		int endMatchSeq = 15;
		Set<Integer> limitedMatchSeqs = null;
		analyseService.asiaOddsAnalyse(matchDir, jobTypes, beginMatchSeq, endMatchSeq, limitedMatchSeqs, okUrlDate, null);
	}
	
	@Test
	public void testAnalyseOddsSection(){
		AnalyseService analyseService = (AnalyseService) applicationContext
				.getBean("analyseService");
		String matchDir = "/home/leslie/MyProject/OkParse/html/daily/match/2015/04/04/";
		int beginMatchSeq = 130;
		int endMatchSeq = 131;
		Set<Integer> limitedMatchSeqs = new HashSet<Integer>();
		limitedMatchSeqs.add(130);
		limitedMatchSeqs.add(131);
		Map<Integer, String> jobTypes = new HashMap<Integer, String>();
		jobTypes.put(130, "A4");
		jobTypes.put(131, "A4");
		String okUrlDate = "150404";
		analyseService.analyseOddsSection(null, matchDir, beginMatchSeq, endMatchSeq, limitedMatchSeqs, jobTypes, okUrlDate);
	}
	
	@Test
	public void testAnalyseEuroTransAsia(){
		AnalyseService analyseService = (AnalyseService) applicationContext
				.getBean("analyseService");
		String matchDir = "/home/leslie/MyProject/OkParse/html/daily/match/2015/04/04/";
		int beginMatchSeq = 301;
		int endMatchSeq = 304;
		Set<Integer> limitedMatchSeqs = new HashSet<Integer>();
		limitedMatchSeqs.add(301);
		limitedMatchSeqs.add(302);
		limitedMatchSeqs.add(303);
		limitedMatchSeqs.add(304);
		Map<Integer, String> jobTypes = new HashMap<Integer, String>();
		jobTypes.put(301, "A4");
		jobTypes.put(302, "A4");
		jobTypes.put(303, "A4");
		jobTypes.put(304, "A4");
		String okUrlDate = "150404";
		analyseService.analyseEuroTransAsia(null, matchDir, beginMatchSeq, endMatchSeq, limitedMatchSeqs, jobTypes, okUrlDate);
	}
}
