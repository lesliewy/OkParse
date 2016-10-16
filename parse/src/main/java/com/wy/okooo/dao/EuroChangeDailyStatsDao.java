/**
 * 
 */
package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.EuropeChangeDailyStats;

/**
 * LOT_EURO_CHANGE_DAILY_STATS
 * 
 * @author leslie
 *
 */
public interface EuroChangeDailyStatsDao {
	void insertDailyStats(EuropeChangeDailyStats dailyStats);
	
	void insertDailyStatsBatch(List<EuropeChangeDailyStats> dailyStatsList);
	
	void deleteDailyStatsByStatsType(EuropeChangeDailyStats delete);
	
	List<EuropeChangeDailyStats> queryDailyStatsByStatsType(EuropeChangeDailyStats query);
	
	List<EuropeChangeDailyStats> queryDailyStatsByStatsTypeProb(EuropeChangeDailyStats query);
}
