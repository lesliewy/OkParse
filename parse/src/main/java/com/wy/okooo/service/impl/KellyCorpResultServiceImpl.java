/**
 * 
 */
package com.wy.okooo.service.impl;

import java.util.List;
import java.util.Map;

import com.wy.okooo.dao.KellyCorpResultDao;
import com.wy.okooo.domain.KellyCorpResult;
import com.wy.okooo.service.KellyCorpResultService;

/**
 * LOT_KELLY_CORP_RESULT service
 * 
 * @author leslie
 *
 */
public class KellyCorpResultServiceImpl implements KellyCorpResultService {

	private KellyCorpResultDao kellyCorpResultDao;

	public void insert(KellyCorpResult result) {
		kellyCorpResultDao.insert(result);
	}

	public void insertList(List<KellyCorpResult> resultList) {
		kellyCorpResultDao.insertList(resultList);
	}

	public void deleteKellyResult(String okUrlDate, String ruleType) {
		kellyCorpResultDao.deleteKellyResult(okUrlDate, ruleType);
	}
	
	public Integer queryCountCorpsByMatchName(String matchName) {
		return kellyCorpResultDao.queryCountCorpsByMatchName(matchName);
	}

	public List<KellyCorpResult> queryResultByKey(
			KellyCorpResult kellyCorpResult) {
		return kellyCorpResultDao.queryResultByKey(kellyCorpResult);
	}
	
	public List<KellyCorpResult> queryResultByMatchName(String matchName, String okUrlDate) {
		return kellyCorpResultDao.queryResultByMatchName(matchName, okUrlDate);
	}
	
	public List<KellyCorpResult> queryAllCorpName() {
		return kellyCorpResultDao.queryAllCorpName();
	}
	
	public List<KellyCorpResult> queryAllMatchName() {
		return kellyCorpResultDao.queryAllMatchName();
	}
	
	public List<KellyCorpResult> queryAllMatchNameByOkUrlDate(String okUrlDate) {
		return kellyCorpResultDao.queryAllMatchNameByOkUrlDate(okUrlDate);
	}
	
	public List<Map<String, String>> highK3Predict(String okUrlDate) {
		return kellyCorpResultDao.highK3Predict(okUrlDate);
	}

	public List<Map<String, String>> highK4Predict(String okUrlDate) {
		return kellyCorpResultDao.highK4Predict(okUrlDate);
	}
	
	public List<Map<String, String>> highK3WinEvenPredict(String okUrlDate) {
		return kellyCorpResultDao.highK3WinEvenPredict(okUrlDate);
	}

	public List<Map<String, String>> highK4EvenNegaPredict(String okUrlDate) {
		return kellyCorpResultDao.highK4EvenNegaPredict(okUrlDate);
	}
	
	public List<KellyCorpResult> queryLatestOkUrlDateFromKellyCorpResult() {
		return kellyCorpResultDao.queryLatestOkUrlDateFromKellyCorpResult();
	}
	
	public KellyCorpResultDao getKellyCorpResultDao() {
		return kellyCorpResultDao;
	}

	public void setKellyCorpResultDao(KellyCorpResultDao kellyCorpResultDao) {
		this.kellyCorpResultDao = kellyCorpResultDao;
	}

}
