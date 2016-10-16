/**
 * 
 */
package com.wy.okooo.parse;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.wy.okooo.domain.AsiaOdds;
import com.wy.okooo.domain.AsiaOddsChange;
import com.wy.okooo.domain.AsiaOddsTrends;
import com.wy.okooo.domain.EuroOddsHandicap;
import com.wy.okooo.domain.EuroTransAsia;
import com.wy.okooo.domain.EuropeOdds;
import com.wy.okooo.domain.EuropeOddsChange;
import com.wy.okooo.domain.EuropeOddsChangeAll;
import com.wy.okooo.domain.IndexStats;

/**
 * 解析欧赔、亚盘页面(http://www.okooo.com/soccer/match/686923/odds/ or http://www.okooo.com/soccer/match/686923/ah/)
 * 某个博彩公司欧赔变化、亚盘变化页面(http://www.okooo.com/soccer/match/686923/odds/change/24/ or http://www.okooo.com/soccer/match/686923/ah/change/24/)
 * @author leslie
 *
 */
public interface ParseOdds {
	
	List<EuropeOdds> getEuropeOdds(int matchSeq, int numOfSeq);
	
	List<EuropeOdds> getEuropeOddsFromFile(File euroOddsHtml, int numOfSeq, String okUrlDate, Integer matchSeq);
	
	List<AsiaOdds> getAsiaOdds(int matchSeq);
	
	List<AsiaOdds> getAsiaOddsFromFile(File asiaOddsHtml, Integer matchSeq);
	
	List<AsiaOddsTrends> getAsiaOddsTrendsFromFile(File asiaOddsHtml, AsiaOddsTrends asiaOddsTrendsInit);
	
	List<EuropeOddsChange> getEuropeOddsChange(long matchId, int matchSeq, int corpNo, int numOfSeq, boolean addInitOdds);
	
	List<EuropeOddsChange> getEuropeOddsChangeFromFile(File euroOddsChangeHtml, int numOfSeq, boolean addInitOdds);
	
	List<EuropeOddsChangeAll> getEuropeOddsChangeAllFromFile(File euroOddsChangeHtml, int numOfSeq, String okUrlDate, Integer matchSeq);
	
	List<AsiaOddsChange> getAsiaOddsChange(long matchId, int matchSeq, int corpNo);
	
	List<AsiaOddsChange> getAsiaOddsChangeFromFile(File asiaOddsChangeHtml, boolean toGetAll);
	
	IndexStats getIndexStatsFromFile(File indexStatsHtml, IndexStats indexStatsInit);
	
	List<EuroOddsHandicap> getEuroOddsHandicapFromFile(File euroOddsHandicapHtml, EuroOddsHandicap euroOddsHandicapInit);
	
	List<EuroTransAsia> getEuroTransAsiaFromFile(File euroTransAsiaHtml, EuroTransAsia euroTransAsiaInit, Map<String, Float> lossRatioMap);
	
	List<EuropeOddsChange> getEuropeOddsChangeDailyFromFile(File euroOddsChangeHtml, int numOfSeq, 
			boolean addInitOdds, String okUrlDate, Integer matchSeq);
	
}
