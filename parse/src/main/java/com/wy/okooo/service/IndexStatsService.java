/**
 * 
 */
package com.wy.okooo.service;

import java.io.File;
import java.util.List;
import java.util.Set;

import com.wy.okooo.domain.IndexStats;

/**
 * @author leslie
 *
 */
public interface IndexStatsService {
	
	void insertIndexStats(IndexStats indexStats);
	
	void insertIndexStatsBatch(List<IndexStats> indexStatsList);
	
	Set<String> querySeqJobTypeByOkUrlDate(String okUrlDate);
	
	List<IndexStats> queryAllByOkUrlDate(String okUrlDate);
	
	void parseIndexStatsFromFile(File indexStatsHtml, IndexStats indexStatsInit);
	
	void parseIndexStats(String indexStatsUrl, String encoding, IndexStats indexStatsInit);
	
	List<IndexStats> queryIndexStatsByRange(IndexStats queryIndexStats);
	
	List<IndexStats> queryCurrJobTypeIndex(String okUrlDate);
	
}
