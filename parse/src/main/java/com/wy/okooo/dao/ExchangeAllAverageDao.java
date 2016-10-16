/**
 * 
 */
package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.ExchangeAllAverage;

/**
 * LOT_ALL_AVERAGE
 * 
 * @author leslie
 *
 */
public interface ExchangeAllAverageDao {
	void insert(ExchangeAllAverage allAverage);
	
	void insertBatch(List<ExchangeAllAverage> allAverage);
	
	List<ExchangeAllAverage> queryAllAverageById(long id);
}
