package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.KellyMatchCount;

/**
 * LOT_KELLY_MATCH_COUNT DAO.
 * 
 * @author leslie
 *
 */
public interface KellyMatchCountDao {
	
	void insertMatchCount(KellyMatchCount kellyMatchCount);
	
	void insertMatchCountBatch(List<KellyMatchCount> kellyMatchCounts);
	
	List<KellyMatchCount> queryExistsMatchCount(KellyMatchCount kellyMatchCount);
	
	List<KellyMatchCount> queryExistsMatchCountByDate(String okUrlDate);
	
	List<KellyMatchCount> queryAllMatchCount();
	
	void deleteByKey(KellyMatchCount kellyMatchCount);
	
	List<KellyMatchCount> querySeqAndJobTypeByOkUrlDate(String okUrlDate);
	
	List<KellyMatchCount> queryMatchCountByDateJobFlag(KellyMatchCount query);
	
	List<KellyMatchCount> queryMatchCountByDateJobFlagRule(KellyMatchCount query);
}
