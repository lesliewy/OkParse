/**
 * 
 */
package com.wy.okooo.dao;

import java.sql.Timestamp;
import java.util.List;

import com.wy.okooo.domain.ExchangeTransactionProp;

/**
 * LOT_TRANS_PROP
 * 
 * @author leslie
 *
 */
public interface ExchangeTransactionPropDao {
	void insert(ExchangeTransactionProp transactionProp);
	
	void insertBatch(List<ExchangeTransactionProp> transactionProps);
	
	List<ExchangeTransactionProp> queryTransPropByTime(Timestamp beginTime, Timestamp endTime);
}
