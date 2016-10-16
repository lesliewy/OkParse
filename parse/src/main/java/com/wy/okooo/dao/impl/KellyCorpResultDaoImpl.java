package com.wy.okooo.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.KellyCorpResultDao;
import com.wy.okooo.domain.KellyCorpResult;

public class KellyCorpResultDaoImpl extends SqlMapClientDaoSupport implements KellyCorpResultDao {

	private static Logger LOGGER = Logger.getLogger(KellyCorpResultDaoImpl.class
			.getName());

	public void insert(KellyCorpResult result) {
		if (result == null) {
			return;
		}
//		try{
			getSqlMapClientTemplate().insert("insert", result);
//		}catch (Exception e){
//			LOGGER.error(e);
//		}
	}

	public void insertList(List<KellyCorpResult> resultList) {
		if (resultList == null) {
			LOGGER.error("resultList is null, return.");
			return;
		}
		for (KellyCorpResult result : resultList) {
			insert(result);
		}
	}

	public void deleteKellyResult(String okUrlDate, String ruleType) {
		if(StringUtils.isBlank(okUrlDate)){
			LOGGER.error("okUrlDate is blank, return.");
			return;
		}
		KellyCorpResult result = new KellyCorpResult();
		result.setOkUrlDate(okUrlDate);
		result.setRuleType(ruleType);
		getSqlMapClientTemplate().delete("deleteKellyResult", result);
	}
	
	public Integer queryCountCorpsByMatchName(String matchName) {
		if(StringUtils.isBlank(matchName)){
			LOGGER.error("matchName is blank, return.");
			return null;
		}
		return (Integer) getSqlMapClientTemplate().queryForObject("queryCountCorpsByMatchName", matchName);
	}

	@SuppressWarnings("unchecked")
	public List<KellyCorpResult> queryResultByKey(
			KellyCorpResult kellyCorpResult) {
		if(kellyCorpResult == null){
			LOGGER.error("kellyCorpResult is null, return now...");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryResultByKey", kellyCorpResult);
	}

	@SuppressWarnings("unchecked")
	public List<KellyCorpResult> queryResultByMatchName(String matchName, String okUrlDate) {
		if(StringUtils.isBlank(matchName)){
			LOGGER.error("matchName is blank, return.");
			return null;
		}
		
		if(StringUtils.isBlank(okUrlDate)){
			return getSqlMapClientTemplate().queryForList("queryResultByMatchName", matchName);
		}else{
			KellyCorpResult query = new KellyCorpResult();
			query.setMatchName(matchName);
			query.setOkUrlDate(okUrlDate);
			return getSqlMapClientTemplate().queryForList("queryResultByMatchNameExclusive", query);
		}
	}

	@SuppressWarnings("unchecked")
	public List<KellyCorpResult> queryAllCorpName() {
		return getSqlMapClientTemplate().queryForList("queryAllCorpName");
	}

	@SuppressWarnings("unchecked")
	public List<KellyCorpResult> queryAllMatchName() {
		return getSqlMapClientTemplate().queryForList("queryAllMatchName");
	}

	@SuppressWarnings("unchecked")
	public List<KellyCorpResult> queryAllMatchNameByOkUrlDate(String okUrlDate) {
		return getSqlMapClientTemplate().queryForList("queryAllMatchNameByOkUrlDate", okUrlDate);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, String>> highK3Predict(String okUrlDate) {
		if(StringUtils.isBlank(okUrlDate)){
			return null;
		}
		return getSqlMapClientTemplate().queryForList("highK3Predict", okUrlDate);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, String>> highK4Predict(String okUrlDate) {
		if(StringUtils.isBlank(okUrlDate)){
			return null;
		}
		return getSqlMapClientTemplate().queryForList("highK4Predict", okUrlDate);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> highK3WinEvenPredict(String okUrlDate) {
		if(StringUtils.isBlank(okUrlDate)){
			return null;
		}
		return getSqlMapClientTemplate().queryForList("highK3WinEvenPredict", okUrlDate);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, String>> highK4EvenNegaPredict(String okUrlDate) {
		if(StringUtils.isBlank(okUrlDate)){
			return null;
		}
		return getSqlMapClientTemplate().queryForList("highK4EvenNegaPredict", okUrlDate);
	}

	@SuppressWarnings("unchecked")
	public List<KellyCorpResult> queryLatestOkUrlDateFromKellyCorpResult() {
		return getSqlMapClientTemplate().queryForList("queryLatestOkUrlDateFromKellyCorpResult");
	}

}
