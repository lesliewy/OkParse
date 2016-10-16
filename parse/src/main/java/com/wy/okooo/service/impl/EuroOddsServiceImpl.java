/**
 * 
 */
package com.wy.okooo.service.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wy.okooo.dao.EuroOddsDao;
import com.wy.okooo.domain.EuropeOdds;
import com.wy.okooo.parse.ParseOdds;
import com.wy.okooo.parse.impl.ParseOddsImpl;
import com.wy.okooo.service.EuroOddsService;

/**
 * 解析欧盘页面service.
 * 
 * @author leslie
 *
 */
public class EuroOddsServiceImpl implements EuroOddsService {

	private EuroOddsDao euroOddsDao;
	
	private ParseOdds parser = new ParseOddsImpl();
	
	public void parseEuroOdds(long matchId, int matchSeq, int numOfSeq) {
		euroOddsDao.insertOddsBatch(parser.getEuropeOdds(matchSeq, numOfSeq));
	}
	
	public void parseEuroOddsFromFile(File euroOddsHtml, int numOfSeq, String okUrlDate, Integer matchSeq) {
		euroOddsDao.insertOddsBatch(parser.getEuropeOddsFromFile(euroOddsHtml, numOfSeq, okUrlDate, matchSeq));
	}
	
	public List<EuropeOdds> getEuropeOddsFromFile(File euroOddsHtml, int numOfSeq, String okUrlDate, Integer matchSeq){
		return parser.getEuropeOddsFromFile(euroOddsHtml, numOfSeq, okUrlDate, matchSeq);
	}
	
	public boolean isExistsByDateSeq(String okUrlDate, Integer matchSeq) {
		List<EuropeOdds> list = queryEuropeOddsByKey(okUrlDate, matchSeq);
		return list != null && !list.isEmpty();
	}
	
	public List<EuropeOdds> queryEuropeOddsByKey(String okUrlDate, Integer matchSeq) {
		EuropeOdds query = new EuropeOdds();
		query.setOkUrlDate(okUrlDate);
		query.setMatchSeq(matchSeq);
		return euroOddsDao.queryEuropeOddsByKey(query);
	}
	
	public List<EuropeOdds> queryEuroOddsByCorpName(String corpName) {
		return euroOddsDao.queryEuroOddsByCorpName(corpName);
	}
	
	public Set<String> queryAllCorpNames() {
		List<EuropeOdds> euroOddsList = euroOddsDao.queryAllCorpNames();
		if(euroOddsList == null){
			return null;
		}
		Set<String> result = new HashSet<String>();
		for(EuropeOdds europeOdds : euroOddsList){
			result.add(europeOdds.getOddsCorpName());
		}
		return result;
	}

	public EuropeOdds queryEuropeOddsByOkId(String okUrlDate,
			Integer matchSeq) {
		return euroOddsDao.queryEuropeOddsByOkId(okUrlDate, matchSeq);
	}
	
	public EuroOddsDao getEuroOddsDao() {
		return euroOddsDao;
	}

	public void setEuroOddsDao(EuroOddsDao euroOddsDao) {
		this.euroOddsDao = euroOddsDao;
	}

}
