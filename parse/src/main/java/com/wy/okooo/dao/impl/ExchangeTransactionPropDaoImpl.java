/**
 * 
 */
package com.wy.okooo.dao.impl;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.ExchangeTransactionPropDao;
import com.wy.okooo.domain.ExchangeTransactionProp;
import com.wy.okooo.domain.Match;

/**
 * LOT_TRANS_PROP
 * 
 */
public class ExchangeTransactionPropDaoImpl extends SqlMapClientDaoSupport
		implements ExchangeTransactionPropDao {

	private static Logger LOGGER = Logger.getLogger(ExchangeTransactionPropDaoImpl.class
			.getName());
	
	public void insert(ExchangeTransactionProp transactionProp) {
		if (transactionProp == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertTransactionProp",
					transactionProp);
		}catch (Exception e){
			LOGGER.info("insertTransactionProp: " + e);
		}

	}

	public void insertBatch(List<ExchangeTransactionProp> transactionProps) {
		if (transactionProps == null) {
			return;
		}
		for (ExchangeTransactionProp one : transactionProps) {
			insert(one);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ExchangeTransactionProp> queryTransPropByTime(
			Timestamp beginTime, Timestamp endTime) {
		Match match = new Match();
		match.setBeginTime(beginTime);
		match.setEndTime(endTime);
		
		return getSqlMapClientTemplate().queryForList(
				"queryTransPropByTime", match);
	}
}
