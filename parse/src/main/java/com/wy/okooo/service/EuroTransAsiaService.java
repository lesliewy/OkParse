/**
 * 
 */
package com.wy.okooo.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wy.okooo.domain.EuroTransAsia;

/**
 * 欧赔转换为亚盘service.
 * 
 * @author leslie
 *
 */
public interface EuroTransAsiaService {
	
	void insertEuroTransAsia(EuroTransAsia euroTransAsia);
	
	void insertEuroTransAsiaBatch(List<EuroTransAsia> euroTransAsiaList);
	
	void parseEuroTransAsiaFromFile(File euroTransAsiaHtml, EuroTransAsia euroTransAsiaInit, Map<String, Float> lossRatioMap);
	
	List<EuroTransAsia> queryEuroTransAsiaByOkUrlDate(String okUrlDate, Integer beginMatchSeq, Integer endMatchSeq);
	
	Set<String> queryEuroTransAsiaByOkUrlDateInSet(String okUrlDate);
	
	List<EuroTransAsia> queryEuroTransAsiaByDateType(String okUrlDate, String jobType, Integer beginMatchSeq, Integer endMatchSeq);
	
	Set<String> queryEuroTransAsiaByDateTypeInSet(String okUrlDate, String jobType, Integer beginMatchSeq, Integer endMatchSeq);
}
