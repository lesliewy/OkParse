package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.EuroOddsHandicap;

/**
 * 让球页面解析.(http://www.okooo.com/soccer/match/776908/hodds/)
 * 
 * @author leslie
 *
 */
public interface EuroOddsHandicapDao {
	
	void insertEuroOddsHandicap(EuroOddsHandicap euroOddsHandicap);
	
	void insertEuroOddsHandicapBatch(List<EuroOddsHandicap> euroOddsHandicapList);
	
	EuroOddsHandicap queryTransByDateJobType(EuroOddsHandicap query);
	
	List<EuroOddsHandicap> queryCurrJobTypeEuroHandicap(String okUrlDate);
	
}
