/**
 * 
 */
package com.wy.okooo.service;

import java.io.File;
import java.util.List;

import com.wy.okooo.domain.EuroOddsHandicap;

/**
 * @author leslie
 *
 */
public interface EuroOddsHandicapService {
	
	void insertEuroOddsHandicap(EuroOddsHandicap euroOddsHandicap);
	
	void insertEuroOddsHandicapBatch(List<EuroOddsHandicap> euroOddsHandicapList);
	
	void parseEuroOddsHandicapFromFile(File euroHandicapHtml, EuroOddsHandicap euroOddsHandicapInit);
	
	EuroOddsHandicap queryTransByDateJobType(String okUrlDate, Integer matchSeq, String jobType);
	
	List<EuroOddsHandicap> queryCurrJobTypeEuroHandicap(String okUrlDate);
	
}
