/**
 * 
 */
package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.ExchangeBfTurnoverDetailDao;
import com.wy.okooo.domain.ExchangeBfTurnoverDetail;

/**
 * LOT_BF_TURNOVER_DETAIL
 * 
 */
public class ExchangeBfTurnoverDetailDaoImpl extends SqlMapClientDaoSupport
		implements ExchangeBfTurnoverDetailDao {
	
	private static Logger LOGGER = Logger.getLogger(ExchangeBfTurnoverDetailDaoImpl.class
			.getName());
	
	public void insert(ExchangeBfTurnoverDetail detail) {
		if (detail == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertTurnoverDetail", detail);
		}catch (Exception e){
			LOGGER.info("insertTurnoverDetail: " + e);
		}
		
	}

	public void insertBatch(List<ExchangeBfTurnoverDetail> details) {
		if (details == null) {
			return;
		}
		for (ExchangeBfTurnoverDetail one : details) {
			insert(one);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ExchangeBfTurnoverDetail> queryBfTurnoverDetailById(long id) {
		return getSqlMapClientTemplate().queryForList("queryBfTurnoverDetailById", id);
	}

}
