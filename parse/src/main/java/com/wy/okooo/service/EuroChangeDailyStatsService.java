/**
 * 
 */
package com.wy.okooo.service;

import java.util.List;

import com.wy.okooo.domain.EuropeChangeDailyStats;

/**
 * LOT_EURO_CHANGE_DAILY_STATS 分析 service.
 * 
 * @author leslie
 *
 */
public interface EuroChangeDailyStatsService {
	
	void insertDailyStats(EuropeChangeDailyStats dailyStats);
	
	void insertDailyStatsBatch(List<EuropeChangeDailyStats> dailyStatsList);
	
	void deleteDailyStatsByStatsType(String okUrlDate, String statsType);
	
	List<EuropeChangeDailyStats> queryDailyStatsByStatsType(String okUrlDate, String statsType);
	
	List<EuropeChangeDailyStats> queryDailyStatsByStatsTypeProb(String okUrlDate, String statsType, Float prob);
}
