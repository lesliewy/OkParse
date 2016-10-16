/**
 * 
 */
package com.wy.okooo.service;

import java.io.File;
import java.util.List;
import java.util.Set;

import com.wy.okooo.domain.AsiaOddsTrends;

/**
 * 解析亚盘页面service.
 * 
 * @author leslie
 *
 */
public interface AsiaOddsTrendsService {
	
	void parseAsiaOddsTrendsFromFile(File asiaOddsHtml, AsiaOddsTrends asiaOddsTrendsInit);
	
	/**
	 * 根据 okUrlDate, beginMatchSeq, endMatchSeq 查询 LOT_ODDS_ASIA_TRENDS.
	 * @param queryTrends
	 * @return
	 */
	List<AsiaOddsTrends> queryAsiaTrendsByRange(AsiaOddsTrends queryTrends);
	
	/**
	 * 根据okUrlDate, jobType 查询matchSeq.
	 * @param queryTrends
	 * @return
	 */
	List<AsiaOddsTrends> queryAsiaTrendsByJobType(AsiaOddsTrends queryTrends);
	
	/**
	 * 根据okUrlDate获取某场比赛最大的jobType(B开头).
	 * @param okUrlDate
	 * @return
	 */
	List<AsiaOddsTrends> queryCurrMatchJobType(String okUrlDate);
	
	/**
	 * 根据okUrlDate, matchSeq, jobType查询hostKelly, visitingKelly, 使用列转行.
	 * @param okUrlDate
	 * @param matchSeq
	 * @param jobType
	 * @return
	 */
	AsiaOddsTrends queryKellyTrendsByDateJobType(String okUrlDate, Integer matchSeq, String jobType);
	
	List<AsiaOddsTrends> queryAsiaOddsTrendsByOkUrlDate(String okUrlDate,
			Integer beginMatchSeq, Integer endMatchSeq);
	
	Set<String> queryAsiaOddsTrendsByOkUrlDateInSet(String okUrlDate);
}
