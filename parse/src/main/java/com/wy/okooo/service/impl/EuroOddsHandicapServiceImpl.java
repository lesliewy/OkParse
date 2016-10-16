/**
 * 
 */
package com.wy.okooo.service.impl;

import java.io.File;
import java.util.List;

import com.wy.okooo.dao.EuroOddsHandicapDao;
import com.wy.okooo.domain.EuroOddsHandicap;
import com.wy.okooo.parse.ParseOdds;
import com.wy.okooo.parse.impl.ParseOddsImpl;
import com.wy.okooo.service.EuroOddsHandicapService;

/**
 * @author leslie
 *
 */
public class EuroOddsHandicapServiceImpl implements EuroOddsHandicapService {

	private EuroOddsHandicapDao euroOddsHandicapDao;
	
	private ParseOdds parser = new ParseOddsImpl();
	
	public void insertEuroOddsHandicap(EuroOddsHandicap euroOddsHandicap) {
		euroOddsHandicapDao.insertEuroOddsHandicap(euroOddsHandicap);
	}

	public void insertEuroOddsHandicapBatch(List<EuroOddsHandicap> euroOddsHandicapList){
		euroOddsHandicapDao.insertEuroOddsHandicapBatch(euroOddsHandicapList);
	}
	
	public void parseEuroOddsHandicapFromFile(File euroHandicapHtml, EuroOddsHandicap euroOddsHandicapInit) {
		euroOddsHandicapDao.insertEuroOddsHandicapBatch(parser.getEuroOddsHandicapFromFile(euroHandicapHtml, euroOddsHandicapInit));
	}
	
	public EuroOddsHandicap queryTransByDateJobType(String okUrlDate, Integer matchSeq, String jobType){
		EuroOddsHandicap query = new EuroOddsHandicap();
		query.setOkUrlDate(okUrlDate);
		query.setMatchSeq(matchSeq);
		query.setJobType(jobType);
		return euroOddsHandicapDao.queryTransByDateJobType(query);
	}
	
	public List<EuroOddsHandicap> queryCurrJobTypeEuroHandicap(String okUrlDate) {
		return euroOddsHandicapDao.queryCurrJobTypeEuroHandicap(okUrlDate);
	}
	
	public EuroOddsHandicapDao getEuroOddsHandicapDao() {
		return euroOddsHandicapDao;
	}

	public void setEuroOddsHandicapDao(EuroOddsHandicapDao euroOddsHandicapDao) {
		this.euroOddsHandicapDao = euroOddsHandicapDao;
	}

}
