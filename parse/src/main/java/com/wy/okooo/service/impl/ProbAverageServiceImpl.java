/**
 * 
 */
package com.wy.okooo.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wy.okooo.dao.ProbAverageDao;
import com.wy.okooo.domain.ProbAverage;
import com.wy.okooo.service.ProbAverageService;

/**
 * 市场平均概率.
 * 
 * @author leslie
 *
 */
public class ProbAverageServiceImpl implements ProbAverageService {

	private ProbAverageDao probAverageDao;

	public void insertProbAverage(ProbAverage probAverage) {
		probAverageDao.insertProbAverage(probAverage);
	}

	public void insertProbAverageBatch(List<ProbAverage> probAverageList) {
		probAverageDao.insertProbAverageBatch(probAverageList);
	}

	public List<ProbAverage> queryProbAverageByOkUrlDate(String okUrlDate) {
		return probAverageDao.queryProbAverageByOkUrlDate(okUrlDate);
	}
	
	public Set<String> querySeqAndJobTypeByOkUrlDateInSet(String okUrlDate) {
		Set<String> result = new HashSet<String>();
		List<ProbAverage> list = queryProbAverageByOkUrlDate(okUrlDate);
		if(list != null){
			for(ProbAverage probAverage : list){
				result.add(probAverage.getMatchSeq() + "_" + probAverage.getJobType());
			}
		}
		return result;
	}
	
	public ProbAverageDao getProbAverageDao() {
		return probAverageDao;
	}

	public void setProbAverageDao(ProbAverageDao probAverageDao) {
		this.probAverageDao = probAverageDao;
	}

	public List<ProbAverage> queryProbAverageBySeqs(ProbAverage probAverage) {
		return probAverageDao.queryProbAverageBySeqs(probAverage);
	}

}
