/**
 * 
 */
package com.wy.okooo.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.wy.okooo.dao.EuroOddsChangeDao;
import com.wy.okooo.domain.EuropeOddsChange;

/**
 * LOT_ODDS_EURO_CHANGE DAO.
 * @author leslie
 *
 */
public class EuroOddsChangeDaoImpl extends SqlMapClientDaoSupport implements EuroOddsChangeDao{
	
	private static Logger LOGGER = Logger.getLogger(EuroOddsChangeDaoImpl.class
			.getName());
	
	public void insertOddsChange(EuropeOddsChange europeOddsChange) {
		if (europeOddsChange == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertEuroOddsChange",europeOddsChange);
		}catch (Exception e){
			LOGGER.info("insertEuroOddsChange: " + e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void insertOddsChangeBatch(final List<EuropeOddsChange> europeOddsChange) {
		if(europeOddsChange == null){
			return;
		}
//		for(EuropeOddsChange odd : europeOddsChange){
//			insertOddsChange(odd);  
//		}
		
		// 批量方式
		try{
			this.getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
				public Object doInSqlMapClient(SqlMapExecutor executor)
						throws SQLException {
					executor.startBatch();
					// 每次提交最大条数
					final int batchSize = 1000;
					int count = 0;
					for (EuropeOddsChange odd : europeOddsChange) {
						executor.insert("insertEuroOddsChange", odd);
						// 每1000条数据提交一次
						if (++count % batchSize == 0) {
							executor.executeBatch();
						}
					}
					// 提交剩余的数据
					executor.executeBatch();
					return null;
				}
			});
		}catch (Exception e){
			LOGGER.error("insertEuroOddsChange: " + e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<EuropeOddsChange> queryEuroOddsChanByCorpName(long matchId,
			String corpName) {
		EuropeOddsChange europeOddsChange = new EuropeOddsChange();
		europeOddsChange.setOkMatchId(matchId);
		europeOddsChange.setOddsCorpName(corpName);
		return getSqlMapClientTemplate().queryForList("queryEuroOddsChanByCorpName", europeOddsChange);
	}

	@SuppressWarnings("unchecked")
	public List<EuropeOddsChange> queryChangeNumByCorp(String oddsCorpName) {
		if(StringUtils.isBlank(oddsCorpName)){
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryChangeNumByCorp", oddsCorpName);
	}
	
	@SuppressWarnings("unchecked")
	public List<EuropeOddsChange> queryChangeTimeBeforeByCorp(
			String oddsCorpName) {
		if(StringUtils.isBlank(oddsCorpName)){
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryChangeTimeBeforeByCorp", oddsCorpName);
	}
	
	public void updateEuroOddsChangeNum(EuropeOddsChange europeOddsChange) {
		if(europeOddsChange == null){
			return;
		}
		try{
			getSqlMapClientTemplate().insert("updateEuroOddsChangeNum",europeOddsChange);
		}catch (Exception e){
			LOGGER.info("updateEuroOddsChangeNum: " + e);
		}
	}
	
	public void updateEuroOddsChangeNum(
			List<EuropeOddsChange> europeOddsChangeList) {
		if(europeOddsChangeList == null || europeOddsChangeList.isEmpty()){
			return;
		}
		
		for(EuropeOddsChange europeOddsChange : europeOddsChangeList){
			updateEuroOddsChangeNum(europeOddsChange);
		}
	}
	
	public void insertOddsChangeDaily(EuropeOddsChange europeOddsChangeDaily) {
		if (europeOddsChangeDaily == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertOddsChangeDaily",europeOddsChangeDaily);
		}catch (Exception e){
			LOGGER.info("insertOddsChangeDaily: " + e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void insertOddsChangeDailyBatch(final List<EuropeOddsChange> europeOddsChangeDailyList) {
		if(europeOddsChangeDailyList == null){
			return;
		}
		
		// 批量方式
		try{
			this.getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
				public Object doInSqlMapClient(SqlMapExecutor executor)
						throws SQLException {
					executor.startBatch();
					// 每次提交最大条数
					final int batchSize = 1000;
					int count = 0;
					for (EuropeOddsChange odd : europeOddsChangeDailyList) {
						executor.insert("insertEuroOddsChangeDaily", odd);
						// 每1000条数据提交一次
						if (++count % batchSize == 0) {
							executor.executeBatch();
						}
					}
					// 提交剩余的数据
					executor.executeBatch();
					return null;
				}
			});
		}catch (Exception e){
			LOGGER.error("insertOddsChangeDailyBatch: " + e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<EuropeOddsChange> queryEuroOddsChanDailyByCorpName(String okUrlDate, Integer matchSeq,
			String corpName) {
		EuropeOddsChange europeOddsChangeDaily = new EuropeOddsChange();
		europeOddsChangeDaily.setOkUrlDate(okUrlDate);
		europeOddsChangeDaily.setMatchSeq(matchSeq);
		europeOddsChangeDaily.setOddsCorpName(corpName);
		return getSqlMapClientTemplate().queryForList("queryEuroOddsChanDailyByCorpName", europeOddsChangeDaily);
	}

	public void deleteEuroOddsChanDailyByCorpName(EuropeOddsChange deleted) {
		getSqlMapClientTemplate().delete("deleteEuroOddsChanDailyByCorpName", deleted);
	}
	
	public void deleteEuroOddsChanDailyByMatchSeq(EuropeOddsChange deleted) {
		getSqlMapClientTemplate().delete("deleteEuroOddsChanDailyByMatchSeq", deleted);
	}

	@SuppressWarnings("unchecked")
	public List<EuropeOddsChange> queryEuroOddsChangeDailySb(
			EuropeOddsChange query) {
		return getSqlMapClientTemplate().queryForList("queryEuroOddsChangeDailySb", query);
	}

	@SuppressWarnings("unchecked")
	public List<EuropeOddsChange> queryDailyInitialWithResult(String oddsCorpName) {
		if(StringUtils.isBlank(oddsCorpName)){
			LOGGER.error("corpName is null, return now...");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryDailyInitialWithResult", oddsCorpName);
	}
}
