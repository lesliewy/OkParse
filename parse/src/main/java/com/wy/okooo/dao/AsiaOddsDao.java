/**
 * 
 */
package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.AsiaOdds;

/**
 * LOT_ODDS_AISA
 * 
 * @author leslie
 *
 */
public interface AsiaOddsDao {
	void insertOdds(AsiaOdds asiaOdds);
	
	void insertOddsBatch(List<AsiaOdds> asiaOdds);
	
	List<AsiaOdds> queryAsiaOddsByMatchId(long matchId);
}
