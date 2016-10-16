/**
 * 
 */
package com.wy.okooo.service;

import java.io.File;
import java.util.List;

import com.wy.okooo.domain.MatchStats;

/**
 * @author leslie
 *
 */
public interface MatchStatsService {
	
	void insertMatchStats(MatchStats matchStats);
	
	void insertMatchStatsBatch(List<MatchStats> matchStatsList);
	
	void deleteMatchStats(Long okMatchId);
	
	MatchStats getMatchStatsFromFile(File matchStatsFile);
	
	List<MatchStats> queryOkUrlDateFromMatchStats();
	
	MatchStats queryMatchStatsById(Long okMatchId);
}
