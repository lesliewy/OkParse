/**
 * 
 */
package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.EuropeOddsChange;

/**
 * LOT_ODDS_EURO_CHANGE
 * 
 * @author leslie
 *
 */
public interface EuroOddsChangeDao {
	// begin LOT_ODDS_EURO_CHANGE
	void insertOddsChange(EuropeOddsChange europeOddsChange);
	
	void insertOddsChangeBatch(List<EuropeOddsChange> europeOddsChange);
	
	List<EuropeOddsChange> queryEuroOddsChanByCorpName(long matchId, String corpName);
	
	void updateEuroOddsChangeNum(EuropeOddsChange europeOddsChange);
	
	void updateEuroOddsChangeNum(List<EuropeOddsChange> europeOddsChangeList);
	
	List<EuropeOddsChange> queryChangeNumByCorp(String oddsCorpName);
	
	List<EuropeOddsChange> queryChangeTimeBeforeByCorp(String oddsCorpName);
	// end
	
	// begin LOT_ODDS_EURO_CHANGE_DAILY
	void insertOddsChangeDaily(EuropeOddsChange europeOddsChange);
	
	void insertOddsChangeDailyBatch(List<EuropeOddsChange> europeOddsChangeList);
	
	List<EuropeOddsChange> queryEuroOddsChanDailyByCorpName(String okUrlDate, Integer matchSeq, String corpName);
	
	void deleteEuroOddsChanDailyByCorpName(EuropeOddsChange deleted);
	
	List<EuropeOddsChange> queryEuroOddsChangeDailySb(EuropeOddsChange query);
	
	void deleteEuroOddsChanDailyByMatchSeq(EuropeOddsChange deleted);
	
	List<EuropeOddsChange> queryDailyInitialWithResult(String oddsCorpName);
	// end
}
