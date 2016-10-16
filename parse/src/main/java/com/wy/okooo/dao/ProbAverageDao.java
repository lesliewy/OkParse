package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.ProbAverage;

/**
 * 平均市场概率DAO.
 * 
 * @author leslie
 *
 */
public interface ProbAverageDao {
	
	void insertProbAverage(ProbAverage probAverage);
	
	void insertProbAverageBatch(List<ProbAverage> probAverageList);
	
	List<ProbAverage> queryProbAverageByOkUrlDate(String okUrlDate);
	
	List<ProbAverage> queryProbAverageBySeqs(ProbAverage probAverage);
	
}
