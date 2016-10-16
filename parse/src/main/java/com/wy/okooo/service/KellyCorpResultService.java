/**
 * 
 */
package com.wy.okooo.service;

import java.util.List;
import java.util.Map;

import com.wy.okooo.domain.KellyCorpResult;

/**
 * LOT_KELLY_CORP_RESULT
 * 
 * @author leslie
 *
 */
public interface KellyCorpResultService {
	
	void insert(KellyCorpResult result);
	
	void insertList(List<KellyCorpResult> resultList);
	
	void deleteKellyResult(String okUrlDate, String ruleType);
	
	Integer queryCountCorpsByMatchName(String matchName);
	
	List<KellyCorpResult> queryResultByKey(KellyCorpResult kellyCorpResult);
	
	List<KellyCorpResult> queryResultByMatchName(String matchName, String okUrlDate);
	
	List<KellyCorpResult> queryAllCorpName();
	
	List<KellyCorpResult> queryAllMatchName();
	
	List<KellyCorpResult> queryAllMatchNameByOkUrlDate(String okUrlDate);
	
	List<Map<String, String>> highK3Predict(String okUrlDate);
	
	List<Map<String, String>> highK4Predict(String okUrlDate);
	
	List<Map<String, String>> highK3WinEvenPredict(String okUrlDate);
	
	List<Map<String, String>> highK4EvenNegaPredict(String okUrlDate);
	
	List<KellyCorpResult> queryLatestOkUrlDateFromKellyCorpResult();
}
