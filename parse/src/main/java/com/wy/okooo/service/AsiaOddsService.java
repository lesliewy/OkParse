/**
 * 
 */
package com.wy.okooo.service;

import java.io.File;
import java.util.List;

import com.wy.okooo.domain.AsiaOdds;

/**
 * 解析亚盘页面service.
 * 
 * @author leslie
 *
 */
public interface AsiaOddsService {
	void parseAsiaOdds(long matchId, int matchSeq);
	
	void parseAsiaOddsFromFile(File asiaOddsHtml);
	
	boolean isExistsByMatchId(long matchId);
	
	List<AsiaOdds> queryAsiaOddsByMatchId(long matchId);
	
	List<AsiaOdds> getAsiaOddsFromFile(File asiaOddsHtml, Integer matchSeq);
}
