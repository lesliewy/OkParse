package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.IndexStats;

/**
 * okooo指数，凯利指数离散度解析(http://www.okooo.com/soccer/match/776381/okoooexponent/#lstu)
 * 
 * @author leslie
 *
 */
public interface IndexStatsDao {
	
	void insertIndexStats(IndexStats indexStats);
	
	void insertIndexStatsBatch(List<IndexStats> indexStatsList);
	
	List<IndexStats> queryAllByOkUrlDate(String okUrlDate);
	
	List<IndexStats> queryIndexStatsByRange(IndexStats queryIndexStats);
	
	List<IndexStats> queryCurrJobTypeIndex(String okUrlDate);
	
}
