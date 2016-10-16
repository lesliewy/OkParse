/**
 * 
 */
package com.wy.okooo.service.impl;

import java.io.File;
import java.util.List;

import com.wy.okooo.dao.AsiaOddsDao;
import com.wy.okooo.domain.AsiaOdds;
import com.wy.okooo.parse.ParseOdds;
import com.wy.okooo.parse.impl.ParseOddsImpl;
import com.wy.okooo.service.AsiaOddsService;

/**
 * 解析亚盘页面service.
 * 
 * @author leslie
 *
 */
public class AsiaOddsServiceImpl implements AsiaOddsService {

	private AsiaOddsDao asiaOddsDao;
	
	private ParseOdds parser = new ParseOddsImpl();
	
	public void parseAsiaOdds(long matchId, int matchSeq) {
		asiaOddsDao.insertOddsBatch(parser.getAsiaOdds(matchSeq));
	}
	
	public void parseAsiaOddsFromFile(File asiaOddsHtml) {
		asiaOddsDao.insertOddsBatch(getAsiaOddsFromFile(asiaOddsHtml, null));
	}
	
	public List<AsiaOdds> getAsiaOddsFromFile(File asiaOddsHtml, Integer matchSeq){
		return parser.getAsiaOddsFromFile(asiaOddsHtml, matchSeq);
	}
	
	public boolean isExistsByMatchId(long matchId) {
		List<AsiaOdds> list = queryAsiaOddsByMatchId(matchId);
		return list != null && !list.isEmpty();
	}

	public List<AsiaOdds> queryAsiaOddsByMatchId(long matchId) {
		return asiaOddsDao.queryAsiaOddsByMatchId(matchId);
	}

	public AsiaOddsDao getAsiaOddsDao() {
		return asiaOddsDao;
	}

	public void setAsiaOddsDao(AsiaOddsDao asiaOddsDao) {
		this.asiaOddsDao = asiaOddsDao;
	}

}
