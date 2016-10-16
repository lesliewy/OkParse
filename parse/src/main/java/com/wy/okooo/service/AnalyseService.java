/**
 * 
 */
package com.wy.okooo.service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wy.okooo.domain.EuropeOdds;
import com.wy.okooo.domain.EuropeOddsChange;
import com.wy.okooo.domain.ExchangeTransactionProp;
import com.wy.okooo.domain.Match;
import com.wy.okooo.domain.MatchScore;

/**
 * @author leslie
 *
 */
public interface AnalyseService {
	void analyse(String url);
	
	Map<Integer, MatchScore> analyseFromFile(List<Match> matches, String matchDir, Integer beginMatchSeq, Integer endMatchSeq);
	
	Map<String, Float> compLossIndex(EuropeOdds europeOdds, EuropeOddsChange euroOddsChange, ExchangeTransactionProp transactionProp);
	
	void kellyAnalyseK2(List<Match> matches, String matchDir, int beginMatchSeq, int endMatchSeq, Set<Integer> limitedMatchSeqs);
	
	void kellyAnalyseK3(List<Match> matches, String matchDir, int beginMatchSeq, int endMatchSeq, Set<Integer> limitedMatchSeqs, Map<Integer, String> jobTypesOfA);
	
	void kellyAnalyseK23Thread(List<Match> matches, String matchDir, int beginMatchSeq, int endMatchSeq, Set<Integer> limitedMatchSeqs,
			Map<Integer, String> jobTypes, String okUrlDate);
	
	void persistCorpEuroOddsChangeKelly(String baseDir, Calendar cal, int beginMatchSeq, int endMatchSeq,
			List<Match> matches, Set<Integer> limitedMatchSeqs, boolean replace, boolean reGetMatchHtml);
	
	void persistCorpEuroOddsChangeKellyThread(String baseDir, Calendar cal, int numOfThread, int beginMatchSeq, int endMatchSeq,
			List<Match> matches, Set<Integer> limitedMatchSeqs, boolean replace, boolean reGetMatchHtml);
	
	StringBuilder highKellyPredict(String okUrlDate, String ruleType);
	
	void asiaOddsAnalyse(String matchDir, Map<Integer, String> jobTypes, int beginMatchSeq, int endMatchSeq,
			Set<Integer> limitedMatchSeqs, String okUrlDate, List<Match> matches);
	
	void indexStatsAnalyse(String matchDir, Map<Integer, String> jobTypes, int beginMatchSeq, int endMatchSeq,
			Set<Integer> limitedMatchSeqs, String okUrlDate, List<Match> matches);
	
	void euroHandicapAnalyse(String matchDir, Map<Integer, String> jobTypes, int beginMatchSeq, int endMatchSeq,
			Set<Integer> limitedMatchSeqs, String okUrlDate, List<Match> matches);
	
	void analyseOddsSection(List<Match> matches, String matchDir, int beginMatchSeq, int endMatchSeq, Set<Integer> limitedMatchSeqs, 
			Map<Integer, String> jobTypes, String okUrlDate);
	
	void analyseEuroTransAsia(List<Match> matches, String matchDir, int beginMatchSeq, int endMatchSeq, Set<Integer> limitedMatchSeqs, 
			Map<Integer, String> jobTypes, String okUrlDate);
}
