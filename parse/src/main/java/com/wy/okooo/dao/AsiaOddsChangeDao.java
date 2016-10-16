/**
 * 
 */
package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.AsiaOddsChange;

/**
 * LOT_ODDS_ASIA_CHANGE
 * 
 * @author leslie
 * 
 */
public interface AsiaOddsChangeDao {
	
	void insertOddsChange(AsiaOddsChange asiapeOddsChange);

	void insertOddsChangeBatch(List<AsiaOddsChange> asiapeOddsChange);
	
	List<AsiaOddsChange> queryAsiaOddsChanByCorpName(long matchId, String corpName);
	
	// begin LOT_ODDS_ASIA_CHANGE_DAILY
	void insertOddsChangeDaily(AsiaOddsChange asiaOddsChange);
	
	void insertOddsChangeDailyBatch(List<AsiaOddsChange> asiaOddsChangeList);
	
	List<AsiaOddsChange> querySeqJobTypeByOkUrlDate(String okUrlDate);
	
	List<AsiaOddsChange> queryAsiaOddsChangeDailySb(AsiaOddsChange query);
	
	void deleteAsiaChangeDailyByMatchSeq(AsiaOddsChange deleted);
	// end LOT_ODDS_ASIA_CHANGE_DAILY
}
