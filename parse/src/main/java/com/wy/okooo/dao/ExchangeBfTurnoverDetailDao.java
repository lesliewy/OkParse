/**
 * 
 */
package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.ExchangeBfTurnoverDetail;

/**
 * LOT_BF_TURNOVER_DETAIL
 * 
 * @author leslie
 *
 */
public interface ExchangeBfTurnoverDetailDao {
	void insert(ExchangeBfTurnoverDetail detail);
	
	void insertBatch(List<ExchangeBfTurnoverDetail> details);
	
	List<ExchangeBfTurnoverDetail> queryBfTurnoverDetailById(long id);
}
