/**
 * 
 */
package com.wy.okooo.service;

import java.util.List;
import java.util.Set;

import com.wy.okooo.domain.ProbAverage;

/**
 * 市场平均概率.
 * 
 * @author leslie
 *
 */
public interface ProbAverageService {
	
	void insertProbAverage(ProbAverage probAverage);
	
	void insertProbAverageBatch(List<ProbAverage> probAverageList);
	
	List<ProbAverage> queryProbAverageByOkUrlDate(String okUrlDate);
	
	Set<String> querySeqAndJobTypeByOkUrlDateInSet(String okUrlDate);
	
	List<ProbAverage> queryProbAverageBySeqs(ProbAverage probAverage);
}
