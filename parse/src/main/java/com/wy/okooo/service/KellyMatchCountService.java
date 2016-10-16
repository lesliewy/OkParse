/**
 * 
 */
package com.wy.okooo.service;

import java.util.List;
import java.util.Set;

import com.wy.okooo.domain.KellyMatchCount;

/**
 * @author leslie
 *
 */
public interface KellyMatchCountService {
	
	void insertMatchCount(KellyMatchCount kellyMatchCount);
	
	void insertMatchCountBatch(List<KellyMatchCount> kellyMatchCounts);
	
	List<KellyMatchCount> queryExistsMatchCount(KellyMatchCount kellyMatchCount);
	
	List<KellyMatchCount> queryExistsMatchCountByDate(String okUrlDate);
	
	List<KellyMatchCount> queryAllMatchCount();
	
	List<KellyMatchCount> querySeqAndJobTypeByOkUrlDate(String okUrlDate);
	
	Set<String> querySeqAndJobTypeByOkUrlDateInSet(String okUrlDate);
	
	List<KellyMatchCount> queryMatchCountByDateJobFlag(String okUrlDate, String jobFlag);
	
	List<KellyMatchCount>  queryMatchCountByDateJobFlagRule(String okUrlDate, String jobFlag, String ruleType);
}
