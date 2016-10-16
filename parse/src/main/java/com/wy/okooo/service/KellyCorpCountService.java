/**
 * 
 */
package com.wy.okooo.service;

import java.util.List;

import com.wy.okooo.domain.KellyCorpCount;

/**
 * LOT_KELLY_CORP_COUNT
 * 
 * @author leslie
 *
 */
public interface KellyCorpCountService {
	
	void insert(KellyCorpCount corpCount);
	
	void insertList(List<KellyCorpCount> corpCountList);
	
	void deleteCorpCountByMatchName(String matchName);
}
