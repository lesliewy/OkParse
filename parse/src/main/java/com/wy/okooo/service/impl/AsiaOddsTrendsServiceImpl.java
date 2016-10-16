/**
 * 
 */
package com.wy.okooo.service.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wy.okooo.dao.AsiaOddsTrendsDao;
import com.wy.okooo.domain.AsiaOddsTrends;
import com.wy.okooo.parse.ParseOdds;
import com.wy.okooo.parse.impl.ParseOddsImpl;
import com.wy.okooo.service.AsiaOddsTrendsService;

/**
 * 解析亚盘页面service.
 * 
 * @author leslie
 *
 */
public class AsiaOddsTrendsServiceImpl implements AsiaOddsTrendsService {

	private AsiaOddsTrendsDao asiaOddsTrendsDao;
	
	private ParseOdds parser = new ParseOddsImpl();
	
	public void parseAsiaOddsTrendsFromFile(File asiaOddsHtml, AsiaOddsTrends asiaOddsTrendsInit) {
		asiaOddsTrendsDao.insertAsiaOddsTrendsBatch(parser.getAsiaOddsTrendsFromFile(asiaOddsHtml, asiaOddsTrendsInit));
	}
	
	public List<AsiaOddsTrends> queryAsiaTrendsByJobType(
			AsiaOddsTrends queryTrends) {
		return asiaOddsTrendsDao.queryAsiaTrendsByJobType(queryTrends);
	}
	
	public List<AsiaOddsTrends> queryAsiaTrendsByRange(AsiaOddsTrends queryTrends) {
		return asiaOddsTrendsDao.queryAsiaTrendsByRange(queryTrends);
	}

	public List<AsiaOddsTrends> queryCurrMatchJobType(String okUrlDate) {
		return asiaOddsTrendsDao.queryCurrMatchJobType(okUrlDate);
	}
	
	public AsiaOddsTrends queryKellyTrendsByDateJobType(String okUrlDate,
			Integer matchSeq, String jobType) {
		AsiaOddsTrends query = new AsiaOddsTrends();
		query.setOkUrlDate(okUrlDate);
		query.setMatchSeq(matchSeq);
		query.setJobType(jobType);
		return asiaOddsTrendsDao.queryKellyTrendsByDateJobType(query);
	}
	
	public List<AsiaOddsTrends> queryAsiaOddsTrendsByOkUrlDate(String okUrlDate,
			Integer beginMatchSeq, Integer endMatchSeq) {
		AsiaOddsTrends query = new AsiaOddsTrends();
		query.setOkUrlDate(okUrlDate);
		query.setBeginMatchSeq(beginMatchSeq);
		query.setEndMatchSeq(endMatchSeq);
		return asiaOddsTrendsDao.queryAsiaOddsTrendsByOkUrlDate(query);
	}
	
	public Set<String> queryAsiaOddsTrendsByOkUrlDateInSet(String okUrlDate) {
		Set<String> result = new HashSet<String>();
		List<AsiaOddsTrends> list = queryAsiaOddsTrendsByOkUrlDate(okUrlDate, 0, 1000);
		if(list != null){
			for(AsiaOddsTrends asiaOddsTrends : list){
				result.add(asiaOddsTrends.getMatchSeq() + "_" + asiaOddsTrends.getJobType());
			}
		}
		return result;
	}
	
	public AsiaOddsTrendsDao getAsiaOddsTrendsDao() {
		return asiaOddsTrendsDao;
	}

	public void setAsiaOddsTrendsDao(AsiaOddsTrendsDao asiaOddsTrendsDao) {
		this.asiaOddsTrendsDao = asiaOddsTrendsDao;
	}

}
