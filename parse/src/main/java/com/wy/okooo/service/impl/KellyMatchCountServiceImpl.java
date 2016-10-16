/**
 * 
 */
package com.wy.okooo.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wy.okooo.dao.KellyMatchCountDao;
import com.wy.okooo.domain.KellyMatchCount;
import com.wy.okooo.service.KellyMatchCountService;

/**
 * @author leslie
 *
 */
public class KellyMatchCountServiceImpl implements KellyMatchCountService {

	private KellyMatchCountDao kellyMatchCountDao;
	
	public void insertMatchCount(KellyMatchCount kellyMatchCount) {
		kellyMatchCountDao.insertMatchCount(kellyMatchCount);
	}

	public void insertMatchCountBatch(List<KellyMatchCount> kellyMatchCounts) {
		kellyMatchCountDao.insertMatchCountBatch(kellyMatchCounts);
	}

	public List<KellyMatchCount> queryExistsMatchCount(
			KellyMatchCount kellyMatchCount) {
		return kellyMatchCountDao.queryExistsMatchCount(kellyMatchCount);
	}
	
	public List<KellyMatchCount> queryExistsMatchCountByDate(String okUrlDate) {
		return kellyMatchCountDao.queryExistsMatchCountByDate(okUrlDate);
	}
	
	public List<KellyMatchCount> queryAllMatchCount() {
		return kellyMatchCountDao.queryAllMatchCount();
	}
	
	public List<KellyMatchCount> querySeqAndJobTypeByOkUrlDate(String okUrlDate) {
		return kellyMatchCountDao.querySeqAndJobTypeByOkUrlDate(okUrlDate);
	}
	
	public Set<String> querySeqAndJobTypeByOkUrlDateInSet(
			String okUrlDate) {
		Set<String> result = new HashSet<String>();
		List<KellyMatchCount> list = querySeqAndJobTypeByOkUrlDate(okUrlDate);
		if(list != null){
			for(KellyMatchCount matchCount : list){
				result.add(matchCount.getMatchSeq() + "_" + matchCount.getJobType());
			}
		}
		return result;
	}
	
	public List<KellyMatchCount> queryMatchCountByDateJobFlag(String okUrlDate,
			String jobFlag) {
		KellyMatchCount query = new KellyMatchCount();
		query.setOkUrlDate(okUrlDate);
		query.setJobFlag(jobFlag);
		return kellyMatchCountDao.queryMatchCountByDateJobFlag(query);
	}
	
	public List<KellyMatchCount> queryMatchCountByDateJobFlagRule(
			String okUrlDate, String jobFlag, String ruleType) {
		KellyMatchCount query = new KellyMatchCount();
		query.setOkUrlDate(okUrlDate);
		query.setJobFlag(jobFlag);
		query.setRuleType(ruleType);
		return kellyMatchCountDao.queryMatchCountByDateJobFlagRule(query);
	}

	public KellyMatchCountDao getKellyMatchCountDao() {
		return kellyMatchCountDao;
	}

	public void setKellyMatchCountDao(KellyMatchCountDao kellyMatchCountDao) {
		this.kellyMatchCountDao = kellyMatchCountDao;
	}

}
