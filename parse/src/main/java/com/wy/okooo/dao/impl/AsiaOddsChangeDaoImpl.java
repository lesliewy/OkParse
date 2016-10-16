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
import com.wy.okooo.dao.AsiaOddsChangeDao;
import com.wy.okooo.domain.AsiaOddsChange;

/**
 * LOT_ODDS_ASIA_CHANGE DAO.
 * 
 * @author leslie
 * 
 */
public class AsiaOddsChangeDaoImpl extends SqlMapClientDaoSupport implements
		AsiaOddsChangeDao {

	private static Logger LOGGER = Logger.getLogger(AsiaOddsChangeDaoImpl.class
			.getName());
	
	public void insertOddsChange(AsiaOddsChange asiaOddsChange) {
		if (asiaOddsChange == null) {
			return;
		}
		try{
			getSqlMapClientTemplate()
			.insert("insertAsiaOddsChange", asiaOddsChange);
		}catch(Exception e){
			LOGGER.info("insertAsiaOddsChange: " + e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void insertOddsChangeBatch(
			final List<AsiaOddsChange> asiaOddsChange) {
		if (asiaOddsChange == null) {
			return;
		}
		// for (AsiaOddsChange odd : asiapeOddsChange) {
		// insertOddsChange(odd);
		// }

		// 批量方式
		try{
			this.getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
				public Object doInSqlMapClient(SqlMapExecutor executor)
						throws SQLException {
					executor.startBatch();
					// 每次提交最大条数
					final int batchSize = 1000;
					int count = 0;
					for (AsiaOddsChange odd : asiaOddsChange) {
						executor.insert("insertAsiaOddsChange", odd);
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
			LOGGER.error("insertAsiaOddsChange: " + e);
		}

	}

	@SuppressWarnings("unchecked")
	public List<AsiaOddsChange> queryAsiaOddsChanByCorpName(long matchId,
			String corpName) {
		AsiaOddsChange asiapeOddsChange = new AsiaOddsChange();
		asiapeOddsChange.setOkMatchId(matchId);
		asiapeOddsChange.setOddsCorpName(corpName);
		return getSqlMapClientTemplate().queryForList(
				"queryAsiaOddsChanByCorpName", asiapeOddsChange);
	}

	public void insertOddsChangeDaily(AsiaOddsChange asiaOddsChange) {
		if (asiaOddsChange == null) {
			return;
		}
		getSqlMapClientTemplate().insert("insertOddsChangeDaily", asiaOddsChange);
	}

	public void insertOddsChangeDailyBatch(
			List<AsiaOddsChange> asiaOddsChangeList) {
		if (asiaOddsChangeList == null || asiaOddsChangeList.isEmpty()) {
			return;
		}
		for(AsiaOddsChange asiaOddsChange : asiaOddsChangeList){
			insertOddsChangeDaily(asiaOddsChange);
		}
	}

	@SuppressWarnings("unchecked")
	public List<AsiaOddsChange> querySeqJobTypeByOkUrlDate(String okUrlDate) {
		if(StringUtils.isBlank(okUrlDate)){
			return null;
		}
		return getSqlMapClientTemplate().queryForList("querySeqJobTypeByOkUrlDate", okUrlDate);
	}

	@SuppressWarnings("unchecked")
	public List<AsiaOddsChange> queryAsiaOddsChangeDailySb(AsiaOddsChange query) {
		if(query == null){
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryAsiaOddsChangeDailySb", query);
	}

	public void deleteAsiaChangeDailyByMatchSeq(AsiaOddsChange deleted) {
		getSqlMapClientTemplate().delete("deleteAsiaChangeDailyByMatchSeq", deleted);
	}
}
