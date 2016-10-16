/**
 * 
 */
package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.EuroChangeDailyStatsDao;
import com.wy.okooo.domain.EuropeChangeDailyStats;

/**
 * LOT_EURO_CHANGE_DAILY_STATS DAO.
 * 
 * @author leslie
 *
 */
public class EuroChangeDailyStatsDaoImpl extends SqlMapClientDaoSupport implements EuroChangeDailyStatsDao{

	private static Logger LOGGER = Logger.getLogger(EuroChangeDailyStatsDaoImpl.class
			.getName());
	
	public void insertDailyStats(EuropeChangeDailyStats dailyStats) {
		if (dailyStats == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertDailyStats",dailyStats);
		}catch (Exception e){
			LOGGER.info("insertDailyStats: " + e);
		}
	}

	public void insertDailyStatsBatch(List<EuropeChangeDailyStats> dailyStatsList) {
		if(dailyStatsList == null){
			return;
		}
		for(EuropeChangeDailyStats daily : dailyStatsList){
			insertDailyStats(daily);
		}
	}

	public void deleteDailyStatsByStatsType(EuropeChangeDailyStats delete) {
		if(StringUtils.isBlank(delete.getOkUrlDate()) || StringUtils.isBlank(delete.getStatsType())){
			LOGGER.info("okUrlDate or statsType is blank, return now...");
			return;
		}
		getSqlMapClientTemplate().delete("deleteDailyStatsByStatsType", delete);
	}

	@SuppressWarnings("unchecked")
	public List<EuropeChangeDailyStats> queryDailyStatsByStatsType(
			EuropeChangeDailyStats query) {
		if(query == null){
			LOGGER.info("query is null, return now...");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryDailyStatsByStatsType",query);
	}

	@SuppressWarnings("unchecked")
	public List<EuropeChangeDailyStats> queryDailyStatsByStatsTypeProb(
			EuropeChangeDailyStats query) {
		if(query == null){
			LOGGER.info("query is null, return now...");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryDailyStatsByStatsTypeProb", query);
	}
}
