/**
 * 
 */
package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.ExchangeAllAverageDao;
import com.wy.okooo.domain.ExchangeAllAverage;

/**
 * LOT_ALL_AVERAGE
 * 
 */
public class ExchangeAllAverageDaoImpl extends SqlMapClientDaoSupport implements
		ExchangeAllAverageDao {
	
	private static Logger LOGGER = Logger.getLogger(ExchangeAllAverageDaoImpl.class
			.getName());
	
	public void insert(ExchangeAllAverage allAverage) {
		if (allAverage == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertAllAverage", allAverage);
		}catch (Exception e){
			LOGGER.info("insertAllAverage: " + e);
		}
	}

	public void insertBatch(List<ExchangeAllAverage> allAverage) {
		if (allAverage == null) {
			return;
		}
		for (ExchangeAllAverage one : allAverage) {
			insert(one);
		}

	}

	@SuppressWarnings("unchecked")
	public List<ExchangeAllAverage> queryAllAverageById(long id) {
		return getSqlMapClientTemplate().queryForList("queryAllAverageById", id);
	}

}
