/**
 * 
 */
package com.wy.okooo.service.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.wy.okooo.dao.EuroTransAsiaDao;
import com.wy.okooo.domain.EuroTransAsia;
import com.wy.okooo.parse.ParseOdds;
import com.wy.okooo.parse.impl.ParseOddsImpl;
import com.wy.okooo.service.EuroTransAsiaService;

/**
 * 解析亚盘页面service.
 * 
 * @author leslie
 *
 */
public class EuroTransAsiaServiceImpl implements EuroTransAsiaService {

	private EuroTransAsiaDao euroTransAsiaDao;
	
	private ParseOdds parser = new ParseOddsImpl();
	
	public void parseEuroTransAsiaFromFile(File euroTransAsiaHtml, EuroTransAsia euroTransAsiaInit, Map<String, Float> lossRatioMap) {
		euroTransAsiaDao.insertEuroTransAsiaBatch(parser.getEuroTransAsiaFromFile(euroTransAsiaHtml, euroTransAsiaInit, lossRatioMap));
	}

	public void insertEuroTransAsia(EuroTransAsia euroTransAsia) {
		euroTransAsiaDao.insertEuroTransAsia(euroTransAsia);
	}

	public void insertEuroTransAsiaBatch(List<EuroTransAsia> euroTransAsiaList) {
		euroTransAsiaDao.insertEuroTransAsiaBatch(euroTransAsiaList);
	}
	
	public List<EuroTransAsia> queryEuroTransAsiaByOkUrlDate(String okUrlDate,
			Integer beginMatchSeq, Integer endMatchSeq) {
		EuroTransAsia query = new EuroTransAsia();
		query.setOkUrlDate(okUrlDate);
		query.setBeginMatchSeq(beginMatchSeq);
		query.setEndMatchSeq(endMatchSeq);
		return euroTransAsiaDao.queryEuroTransAsiaByOkUrlDate(query);
	}
	
	public Set<String> queryEuroTransAsiaByOkUrlDateInSet(String okUrlDate) {
		Set<String> result = new HashSet<String>();
		List<EuroTransAsia> list = queryEuroTransAsiaByOkUrlDate(okUrlDate, 0, 1000);
		if(list != null){
			for(EuroTransAsia euroTransAsia : list){
				result.add(euroTransAsia.getMatchSeq() + "_" + euroTransAsia.getJobType());
			}
		}
		return result;
	}

	public List<EuroTransAsia> queryEuroTransAsiaByDateType(String okUrlDate, String jobType, Integer beginMatchSeq, Integer endMatchSeq) {
		if(StringUtils.isBlank(okUrlDate) || StringUtils.isBlank(jobType)){
			return null;
		}
		EuroTransAsia query = new EuroTransAsia();
		query.setOkUrlDate(okUrlDate);
		query.setJobType(jobType);
		query.setBeginMatchSeq(beginMatchSeq);
		query.setEndMatchSeq(endMatchSeq);
		return euroTransAsiaDao.queryEuroTransAsiaByDateType(query);
	}
	
	public Set<String> queryEuroTransAsiaByDateTypeInSet(String okUrlDate, String jobType, Integer beginMatchSeq, Integer endMatchSeq){
		List<EuroTransAsia> list = queryEuroTransAsiaByDateType(okUrlDate, jobType, beginMatchSeq, endMatchSeq);
		Set<String> result = new HashSet<String>();
		if(list != null){
			for(EuroTransAsia euroTransAsia : list){
				result.add(euroTransAsia.getMatchSeq() + "_" + euroTransAsia.getJobType());
			}
		}
		return result;
	}
	
	public EuroTransAsiaDao getEuroTransAsiaDao() {
		return euroTransAsiaDao;
	}

	public void setEuroTransAsiaDao(EuroTransAsiaDao euroTransAsiaDao) {
		this.euroTransAsiaDao = euroTransAsiaDao;
	}

}
