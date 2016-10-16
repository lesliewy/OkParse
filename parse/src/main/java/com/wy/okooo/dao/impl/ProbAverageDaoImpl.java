package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.ProbAverageDao;
import com.wy.okooo.domain.ProbAverage;

public class ProbAverageDaoImpl extends SqlMapClientDaoSupport implements ProbAverageDao {

	private static Logger LOGGER = Logger.getLogger(ProbAverageDaoImpl.class
			.getName());

	public void insertProbAverage(ProbAverage probAverage) {
		if (probAverage == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertProbAverage", probAverage);
		}catch (Exception e){
			LOGGER.error(e);
		}
	}

	public void insertProbAverageBatch(List<ProbAverage> probAverageList) {
		if (probAverageList == null) {
			LOGGER.error("probAverageList is null, return.");
			return;
		}
		for (ProbAverage probAverage : probAverageList) {
			insertProbAverage(probAverage);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ProbAverage> queryProbAverageByOkUrlDate(String okUrlDate) {
		if(StringUtils.isBlank(okUrlDate)){
			LOGGER.error("okUrlDate is blank, return.");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryProbAverageByOkUrlDate", okUrlDate);
	}

	@SuppressWarnings("unchecked")
	public List<ProbAverage> queryProbAverageBySeqs(ProbAverage probAverage) {
		if (probAverage == null) {
			LOGGER.error("probAverage is null, return.");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryProbAverageBySeqs", probAverage);
	}

}
