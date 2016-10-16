/**
 * 
 */
package com.wy.okooo.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.wy.okooo.dao.EuroOddsChangeAllDao;
import com.wy.okooo.domain.EuropeOddsChangeAll;

/**
 * LOT_ODDS_EURO_CHANGE_ALL DAO.
 * @author leslie
 *
 */
public class EuroOddsChangeAllDaoImpl extends SqlMapClientDaoSupport implements EuroOddsChangeAllDao{
	
	private static Logger LOGGER = Logger.getLogger(EuroOddsChangeAllDaoImpl.class
			.getName());
	
	public void insertOddsChangeAll(EuropeOddsChangeAll europeOddsChangeAll) {
		if (europeOddsChangeAll == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertEuroOddsChangeAll",europeOddsChangeAll);
		}catch (Exception e){
			LOGGER.info("insertOddsChangeAll: " + e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void insertOddsChangeAllBatch(final List<EuropeOddsChangeAll> europeOddsChangeAll) {
		if(europeOddsChangeAll == null){
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
					for (EuropeOddsChangeAll odd : europeOddsChangeAll) {
						executor.insert("insertEuroOddsChangeAll", odd);
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
			LOGGER.error("insertOddsChangeAllBatch: " + e);
		}
	}

	public void deleteByOkUrlDateMatchSeq(
			EuropeOddsChangeAll europeOddsChangeAll) {
		if(europeOddsChangeAll == null){
			LOGGER.info("europeOddsChangeAll is null, return now.");
			return;
		}
		getSqlMapClientTemplate().insert("deleteByOkUrlDateMatchSeq",europeOddsChangeAll);
	}

}
