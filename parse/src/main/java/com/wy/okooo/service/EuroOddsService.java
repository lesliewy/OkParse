/**
 * 
 */
package com.wy.okooo.service;

import java.io.File;
import java.util.List;
import java.util.Set;

import com.wy.okooo.domain.EuropeOdds;

/**
 * 解析欧盘页面service.
 * 
 * @author leslie
 *
 */
public interface EuroOddsService {
	void parseEuroOdds(long matchId, int matchSeq, int numOfSeq);
	
	void parseEuroOddsFromFile(File euroOddsHtml, int numOfSeq, String okUrlDate, Integer matchSeq);
	
	boolean isExistsByDateSeq(String okUrlDate, Integer matchSeq);
	
	List<EuropeOdds> queryEuropeOddsByKey(String okUrlDate, Integer matchSeq);
	
	List<EuropeOdds> getEuropeOddsFromFile(File euroOddsHtml, int numOfSeq, String okUrlDate, Integer matchSeq);
	
	Set<String> queryAllCorpNames();
	
	EuropeOdds queryEuropeOddsByOkId(String okUrlDate, Integer matchSeq); 
	
	List<EuropeOdds> queryEuroOddsByCorpName(String corpName);
}
