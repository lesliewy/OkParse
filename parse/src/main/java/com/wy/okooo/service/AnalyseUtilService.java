/**
 * 
 */
package com.wy.okooo.service;

import java.util.List;
import java.util.Map;

import com.wy.okooo.domain.AsiaOddsTrends;
import com.wy.okooo.domain.Match;
import com.wy.okooo.domain.MatchScore;

/**
 * @author leslie
 *
 */
public interface AnalyseUtilService {

	StringBuilder showKellySummary(List<Match> matches, String okUrlDate, Map<Integer, String> jobTypes, String baseDir);
	
	String showKellySummary(String okUrlDate, String matchName, Map<String, Map<Integer, String>> allJobTypes,
			String baseDir, Map<Integer, MatchScore> scoresMap, List<Match> matches, List<AsiaOddsTrends> asiaTrendsList,
			Map<String, Float> kellyStdDevMap,Map<String, String> euroHandicapMap, Map<String, String> euroTransAsiaMap);
	
	void initCorp(String matchHtmlFilePath);
	
	void updateEuroOddsChangeNum();
	
	void calcuCorpAvgTimeBeforeMatch();
	
	void initKellyCorpCount();
	
	void initKellyCorpCount(String matchName);
	
	String showKellyCountProb(String matchName, Map<String, Map<String, Integer>> summaryMap, String currOkUrlDate);
	
	void showKellyCountProb();
	
	void analyseScoreOdds(String okUrlDate, List<Integer> matchSeqs);
	
}
