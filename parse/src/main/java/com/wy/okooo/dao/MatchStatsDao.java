package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.MatchStats;

/**
 * 比赛技术统计DAO.(http://www.okooo.com/soccer/match/768205/)
 * 
 * @author leslie
 *
 */
public interface MatchStatsDao {
	
	void insertMatchStats(MatchStats matchStats);
	
	void insertMatchStatsBatch(List<MatchStats> matchStatsList);
	
	void deleteMatchStats(Long okMatchId);
	
	List<MatchStats> queryOkUrlDateFromMatchStats();
	
	MatchStats queryMatchStatsById(Long okMatchId);
	
}
