/**
 * 
 */
package com.wy.okooo.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.wy.okooo.dao.EuroChangeDailyStatsDao;
import com.wy.okooo.domain.EuropeChangeDailyStats;
import com.wy.okooo.service.EuroChangeDailyStatsService;

/**
 * LOT_EURO_CHANGE_DAILY_STATS 分析 service.
 * 
 * @author leslie
 *
 */
public class EuroChangeDailyStatsServiceImpl implements EuroChangeDailyStatsService {

	private EuroChangeDailyStatsDao euroChangeDailyStatsDao;

	public void insertDailyStats(EuropeChangeDailyStats dailyStats) {
		euroChangeDailyStatsDao.insertDailyStats(dailyStats);
	}

	public void insertDailyStatsBatch(
			List<EuropeChangeDailyStats> dailyStatsList) {
		euroChangeDailyStatsDao.insertDailyStatsBatch(dailyStatsList);
	}
	
	public void deleteDailyStatsByStatsType(String okUrlDate, String statsType) {
		EuropeChangeDailyStats delete = new EuropeChangeDailyStats();
		delete.setOkUrlDate(okUrlDate);
		delete.setStatsType(statsType);
		euroChangeDailyStatsDao.deleteDailyStatsByStatsType(delete);
	}
	
	public List<EuropeChangeDailyStats> queryDailyStatsByStatsType(
			String okUrlDate, String statsType) {
		EuropeChangeDailyStats query = new EuropeChangeDailyStats();
		query.setOkUrlDate(okUrlDate);
		query.setStatsType(statsType);
		
		return euroChangeDailyStatsDao.queryDailyStatsByStatsType(query);
	}
	
	public List<EuropeChangeDailyStats> queryDailyStatsByStatsTypeProb(
			String okUrlDate, String statsType, Float prob) {
		if(StringUtils.isBlank(okUrlDate) || StringUtils.isBlank(statsType) || prob == null){
			return null;
		}
		EuropeChangeDailyStats query = new EuropeChangeDailyStats();
		query.setOkUrlDate(okUrlDate);
		query.setStatsType(statsType);
		query.setProb(prob);
		return euroChangeDailyStatsDao.queryDailyStatsByStatsTypeProb(query);
	}

	public EuroChangeDailyStatsDao getEuroChangeDailyStatsDao() {
		return euroChangeDailyStatsDao;
	}

	public void setEuroChangeDailyStatsDao(
			EuroChangeDailyStatsDao euroChangeDailyStatsDao) {
		this.euroChangeDailyStatsDao = euroChangeDailyStatsDao;
	}

}
