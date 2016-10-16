/**
 * 
 */
package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.EuropeOddsChangeAll;

/**
 * LOT_ODDS_EURO_CHANGE_ALL
 * 
 * @author leslie
 *
 */
public interface EuroOddsChangeAllDao {
	void insertOddsChangeAll(EuropeOddsChangeAll europeOddsChangeAll);
	
	void insertOddsChangeAllBatch(List<EuropeOddsChangeAll> europeOddsChangeAll);
	
	void deleteByOkUrlDateMatchSeq(EuropeOddsChangeAll europeOddsChangeAll);
	
}
