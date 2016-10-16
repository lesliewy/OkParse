/**
 * 
 */
package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.EuroTransAsia;

/**
 * LOT_EURO_TRANS_ASIA
 * 
 * @author leslie
 *
 */
public interface EuroTransAsiaDao {
	
	void insertEuroTransAsia(EuroTransAsia euroTransAsia);
	
	void insertEuroTransAsiaBatch(List<EuroTransAsia> euroTransAsiaList);
	
	List<EuroTransAsia> queryEuroTransAsiaByOkUrlDate(EuroTransAsia query);
	
	List<EuroTransAsia> queryEuroTransAsiaByDateType(EuroTransAsia query);
	
}
