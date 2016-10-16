package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.KellyCorpCount;

/**
 * LOT_KELLY_CORP_COUNT DAO.
 * @author leslie
 *
 */
public interface KellyCorpCountDao {
	void insert(KellyCorpCount kellyCorpCount);
	
	void insertList(List<KellyCorpCount> kellyCorpCountList);
	
	void deleteCorpCountByMatchName(String matchName);
	
}
