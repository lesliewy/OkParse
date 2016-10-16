/**
 * 
 */
package com.wy.okooo.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wy.okooo.domain.EuroAsiaRefer;
import com.wy.okooo.domain.EuropeChangeDailyStats;
import com.wy.okooo.domain.KellyRule;
import com.wy.okooo.domain.Match;

/**
 * @author leslie
 *
 */
public interface OkJobService {

	List<Match> getMatchesFromHtml(List<File> matchHtmlFiles, String okUrlDate, String jobFlag);
	
	Map<Float, EuroAsiaRefer> getEuroAsiaReferMap();
	
	Map<String, String> queryCorpsNames();
	
	Set<Integer> queryAllCorpsNo();
	
	List<KellyRule> getGeneralKellyRules();
	
	Map<String, EuropeChangeDailyStats> getEuroChangeDailyStatsMap();
	
	void emptyEuroChangeDailyStatsMap();
	
	Map<String, String> getCorpNameCorpNoMap();
	
}
