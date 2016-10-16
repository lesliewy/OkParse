/**
 * 
 */
package com.wy.okooo.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wy.okooo.domain.AsiaOddsChange;

/**
 * 解析亚盘赔率变化页面service(http://www.okooo.com/soccer/match/686854/ah/change/322/)
 * 
 * @author leslie
 *
 */
public interface AsiaOddsChangeService {
	void parseAsiaOddsChange(long matchId, int matchSeq, int corpNo);
	
	void parseAsiaOddsChangeFromFile(File asiaOddsChangeHtml);
	
	List<AsiaOddsChange> getAsiaOddsChangeFromFile(File asiaOddsChangeHtml, boolean toGetAll);
	
	boolean isExistsByMatchIdAndCorpNo(long matchId, int corpNo);
	
	List<AsiaOddsChange> queryAsiaOddsChanByCorpNo(long matchId, int corpNo);
	
	// begin LOT_ODDS_ASIA_CHANGE_DAILY
	void insertOddsChangeDaily(AsiaOddsChange asiaOddsChange);
	
	void insertOddsChangeDailyBatch(List<AsiaOddsChange> asiaOddsChangeList);
	
	List<AsiaOddsChange> querySeqJobTypeByOkUrlDate(String okUrlDate);
	
	Set<String> querySeqJobTypeInSetByOkUrlDate(String okUrlDate);
	
	List<AsiaOddsChange> queryAsiaOddsChangeDailySb(AsiaOddsChange query);
	
	void deleteAsiaChangeDailyByMatchSeq(String okUrlDate, Integer matchSeq);
	
	void analyseAsiaOddsChangeDaily(File dir, Map<Integer, String> jobTypes, Map<String, String> cropNoNameMap,
			Set<Integer> toProcessMatchSeqs, String okUrlDate);
	// end LOT_ODDS_ASIA_CHANGE_DAILY
		
}
