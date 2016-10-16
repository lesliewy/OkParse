/**
 * 
 */
package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.AsiaOddsTrends;

/**
 * LOT_ODDS_AISA_TRENDS
 * 
 * @author leslie
 *
 */
public interface AsiaOddsTrendsDao {
	
	void insertAsiaOddsTrends(AsiaOddsTrends asiaOddsTrends);
	
	void insertAsiaOddsTrendsBatch(List<AsiaOddsTrends> asiaOddsTrendsList);
	
	List<AsiaOddsTrends> queryAsiaTrendsByRange(AsiaOddsTrends queryTrends);
	
	List<AsiaOddsTrends> queryAsiaTrendsByJobType(AsiaOddsTrends queryTrends);
	
	List<AsiaOddsTrends> queryCurrMatchJobType(String okUrlDate);
	
	AsiaOddsTrends queryKellyTrendsByDateJobType(AsiaOddsTrends query);
	
	List<AsiaOddsTrends> queryAsiaOddsTrendsByOkUrlDate(AsiaOddsTrends query);
}
