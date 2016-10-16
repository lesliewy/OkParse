/**
 * 
 */
package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.EuropeOdds;

/**
 * LOT_ODDS_EURO
 * 
 * @author leslie
 *
 */
public interface EuroOddsDao {
	void insertOdds(EuropeOdds europeOdds);
	
	void insertOddsBatch(List<EuropeOdds> europeOdds);
	
	List<EuropeOdds> queryEuropeOddsByKey(EuropeOdds europeOdds);
	
	List<EuropeOdds> queryAllCorpNames();
	
	EuropeOdds queryEuropeOddsByOkId(String okUrlDate, Integer matchSeq);
	
	List<EuropeOdds> queryEuroOddsByCorpName(String corpName);
	
}
