/**
 * 
 */
package service;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.wy.okooo.data.HtmlPersist;
import com.wy.okooo.domain.Match;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * @author leslie
 *
 */
public class TestHtmlPersist {
	
	private static Logger LOGGER = Logger
			.getLogger(TestHtmlPersist.class.getName());
	
	private HtmlPersist persist = new HtmlPersist();
	
	private String dir = OkConstant.FILE_PATH_BASE;
	
	@Test
	public void testAll(){
		Calendar cal = Calendar.getInstance();
		// month 实际月份要 +1.
		cal.set(2014, 10, 9, 00, 00);
		persist.persistAll(cal, 1, 1000);
	}
	
	@Test
	public void testPersistAllThread(){
		Calendar cal = Calendar.getInstance();
//		int beginDay = 5;
//		int endDay = 8;
//		for(int i = beginDay; i <= endDay; i++){
//			// month 实际月份要 +1.
//			cal.set(2014, 9, i, 00, 00);
//			persist.persistAllThread(cal);
//		}
		
		// month 实际月份要 +1.
		cal.set(2014, 9, 14, 00, 00);
		persist.persistAllThread(cal);
		
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}
	}
	
	@Test
	public void testPersistMatchBatch(){
		Calendar cal = Calendar.getInstance();
		// month 月份 = 实际月份 - 1.
		for(int i = 1; i <= 4; i++){
			cal.set(2014, 11, i, 00, 00);
			persist.persistMatchBatch(cal);
		}
	}
	
	@Test
	public void testPersistEuroOddsBatch(){
		String baseDir = "/home/leslie/MyProject/OkParse/html/2014/12/03/";
		persist.persistEuroOddsBatch(baseDir);
	}
	
	@Test
	public void testPersistAsiaOddsBatch(){
		persist.persistAsiaOddsBatch(dir);
	}
	
	@Test
	public void testPersistEuroOddsChangeBatch(){
		persist.persistEuroOddsChangeBatch(dir);
	}
	
	@Test
	public void testPersistAsiaOddsChangeBatch(){
		persist.persistAsiaOddsChangeBatch(dir);
	}
	
	/*
	 * 获取交易盈亏页面， 需要现有match.html.
	 */
	@Test
	public void testPersistExchangeInfoBatch(){
//		String dir = "/home/leslie/MyProject/OkParse/html/2014/12/02_finish/";
//		persist.persistExchangeInfoBatch(dir, 1, 1000);
		
		String baseDir = "/home/leslie/MyProject/OkParse/html/";
		String dir = "";
		String yearStr = "2015";
		// 实际月份，用于拼接 path;
		String monthStr = "01";
		// okUrlDate 中的day.
		int day = 4;
		for(int i = day; i <= day; i++){
			dir = baseDir + yearStr + "/" + monthStr + "/" +
					StringUtils.leftPad(String.valueOf(i), 2, "0") + "/";
			persist.persistExchangeInfoBatch(dir, 29, 45, null, false);
		}
	}
	
	@Test
	public void testPersistTurnoverDetail(){
		persist.persistTurnoverDetailBatch(dir);
	}
	
	@Test
	public void testPersistAllCorpEuroOddsChange(){
		persist.persistAllCorpEuroOddsChange(dir);
	}
	
	@Test
	public void testPersistAsiaOddsWithLimit(){
		String currOkUrlDate = "150305";
		Calendar cal = OkParseUtils.buildCalByOkUrlDate(currOkUrlDate);
		List<Match> matches = new ArrayList<Match>(2^10);
		Match match1 = new Match();
		match1.setOkMatchId(705696L);
		match1.setMatchSeq(12);
		
		Match match2 = new Match();
		match2.setOkMatchId(713483L);
		match2.setMatchSeq(13);
		
		Match match3 = new Match();
		match3.setOkMatchId(769409L);
		match3.setMatchSeq(14);
		
		Match match4 = new Match();
		match4.setOkMatchId(769407L);
		match4.setMatchSeq(15);
		matches.add(match1);
		matches.add(match2);
		matches.add(match3);
		matches.add(match4);
		
		Set<Integer> limitedMatchSeqs = new HashSet<Integer>();
		limitedMatchSeqs.add(12);
		limitedMatchSeqs.add(13);
		limitedMatchSeqs.add(14);
		limitedMatchSeqs.add(15);
		
		int beginMatchSeq = 0;
		int endMatchSeq = 0;
		boolean replace = false;
		persist.persistAsiaOddsWithLimit(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, matches, limitedMatchSeqs, 
				beginMatchSeq, endMatchSeq, replace, true);
	}
	
	@Test
	public void testPersistEuroTransAsiaWithLimit(){
		String currOkUrlDate = "150404";
		Calendar cal = OkParseUtils.buildCalByOkUrlDate(currOkUrlDate);
		List<Match> matches = new ArrayList<Match>(2^10);
		Match match1 = new Match();
		match1.setOkMatchId(717391L);
		match1.setMatchSeq(301);
		
		Match match2 = new Match();
		match2.setOkMatchId(734393L);
		match2.setMatchSeq(302);
		
		Match match3 = new Match();
		match3.setOkMatchId(772044L);
		match3.setMatchSeq(303);
		
		Match match4 = new Match();
		match4.setOkMatchId(772053L);
		match4.setMatchSeq(304);
		matches.add(match1);
		matches.add(match2);
		matches.add(match3);
		matches.add(match4);
		
		Set<Integer> limitedMatchSeqs = new HashSet<Integer>();
		limitedMatchSeqs.add(301);
		limitedMatchSeqs.add(302);
		limitedMatchSeqs.add(303);
		limitedMatchSeqs.add(304);
		
		int beginMatchSeq = 0;
		int endMatchSeq = 0;
		boolean replace = false;
		persist.persistEuroTransAsiaWithLimit(OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar, cal, matches, limitedMatchSeqs, 
				beginMatchSeq, endMatchSeq, replace);
	}
}
