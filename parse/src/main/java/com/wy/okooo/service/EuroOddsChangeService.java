/**
 * 
 */
package com.wy.okooo.service;

import java.io.File;
import java.util.List;

import com.wy.okooo.domain.EuropeOddsChange;

/**
 * 解析欧赔变化页面service(http://www.okooo.com/soccer/match/720252/odds/change/24/)
 * 
 * @author leslie
 *
 */
public interface EuroOddsChangeService {
	void parseEuroOddsChange(long matchId, int matchSeq, int corpNo, int numOfSeq, boolean addInitOdds);
	
	void parseEuroOddsChangeFromFile(File euroOddsChangeHtml, int numOfSeq, boolean addInitOdds);
	
	void parseEuroOddsChangeAllFromFile(File euroOddsChangeHtml, int numOfSeq, String corpName, String okUrlDate, Integer matchSeq);
	
	boolean isExistsByMatchIdAndCorpNo(long matchId, int corpNo);
	
	List<EuropeOddsChange> queryEuroOddsChanByCorpNo(long matchId, int corpNo);
	
	List<EuropeOddsChange> getEuroOddsChange(long matchId, int matchSeq, int corpNo, int numOfSeq, boolean addInitOdds);
	
	List<EuropeOddsChange> getEuroOddsChangeFromFile(File euroOddsChangeHtml, int numOfSeq, boolean addInitOdds);
	
	List<EuropeOddsChange> queryChangeNumByCorp(String oddsCorpName);
	
	void updateEuroOddsChangeNum(List<EuropeOddsChange> europeOddsChangeList);
	
	List<EuropeOddsChange> queryChangeTimeBeforeByCorp(String oddsCorpName);
	
	// begin LOT_ODDS_EURO_CHANGE_DAILY
	void parseEuroOddsChangeDailyFromFile(File euroOddsChangeDailyHtml, int numOfSeq, boolean addInitOdds,
			String okUrlDate, Integer matchSeq);
	
	List<EuropeOddsChange> getEuroOddsChangeDailyFromFile(File euroOddsChangeDailyHtml, int numOfSeq, boolean addInitOdds,
			String okUrlDate, Integer matchSeq);
	
	void insertEuroOddsChangeDailyBatch(List<EuropeOddsChange> europeOddsChangeDailyList);
	
	void deleteEuroOddsChanDailyByCorpName(String okUrlDate, Integer matchSeq, String corpName);
	
	List<EuropeOddsChange> queryEuroOddsChangeDailySb(EuropeOddsChange query);
	
	void deleteEuroOddsChanDailyByMatchSeq(String okUrlDate, Integer matchSeq);
	
	List<EuropeOddsChange> queryDailyInitialWithResult(String oddsCorpName);
	// end
	
}
