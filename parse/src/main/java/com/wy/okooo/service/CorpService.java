/**
 * 
 */
package com.wy.okooo.service;

import java.util.List;
import java.util.Map;

import com.wy.okooo.domain.Corp;

/**
 * LOT_CORP
 * 
 * @author leslie
 *
 */
public interface CorpService {
	
	void insertList(List<Corp> corps);
	
	List<Corp> queryAllCorp();
	
	Map<String, String> initialCorpMap();
	
	void updateTimeBeforeMatchList(List<Corp> corpList);
	
}
