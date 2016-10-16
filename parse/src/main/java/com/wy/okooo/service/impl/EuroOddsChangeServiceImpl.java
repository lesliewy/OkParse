/**
 * 
 */
package com.wy.okooo.service.impl;

import java.io.File;
import java.util.List;

import com.wy.okooo.dao.EuroOddsChangeAllDao;
import com.wy.okooo.dao.EuroOddsChangeDao;
import com.wy.okooo.domain.EuropeOddsChange;
import com.wy.okooo.domain.EuropeOddsChangeAll;
import com.wy.okooo.parse.ParseOdds;
import com.wy.okooo.parse.impl.ParseOddsImpl;
import com.wy.okooo.service.EuroOddsChangeService;
import com.wy.okooo.util.OkParseUtils;

/**
 * 解析欧盘赔率变化页面service(http://www.okooo.com/soccer/match/720252/odds/change/24/)
 * 
 * @author leslie
 *
 */
public class EuroOddsChangeServiceImpl implements EuroOddsChangeService {

	private EuroOddsChangeDao euroOddsChangeDao;
	
	private EuroOddsChangeAllDao euroOddsChangeAllDao;
	
	private ParseOdds parser = new ParseOddsImpl();
	
	public void parseEuroOddsChange(long matchId, int matchSeq, int corpNo, int numOfSeq, boolean addInitOdds) {
		euroOddsChangeDao.insertOddsChangeBatch(parser.getEuropeOddsChange(matchId, matchSeq, corpNo, numOfSeq, addInitOdds));
	}
	
	public void parseEuroOddsChangeFromFile(File euroOddsChangeHtml, int numOfSeq, boolean addInitOdds) {
		euroOddsChangeDao.insertOddsChangeBatch(parser.getEuropeOddsChangeFromFile(euroOddsChangeHtml, numOfSeq, addInitOdds));
	}
	
	public void parseEuroOddsChangeAllFromFile(File euroOddsChangeHtml, int numOfSeq, String corpName,
			String okUrlDate, Integer matchSeq) {
		// 先根据okUrlDate, matchSeq 删除.
		EuropeOddsChangeAll deleted = new EuropeOddsChangeAll();
		deleted.setOkUrlDate(okUrlDate);
		deleted.setMatchSeq(matchSeq);
		deleted.setOddsCorpName(corpName);
		euroOddsChangeAllDao.deleteByOkUrlDateMatchSeq(deleted);
		
		euroOddsChangeAllDao.insertOddsChangeAllBatch(parser.getEuropeOddsChangeAllFromFile(euroOddsChangeHtml, numOfSeq, 
				okUrlDate, matchSeq));
	}
	
	public boolean isExistsByMatchIdAndCorpNo(long matchId, int corpNo) {
		List<EuropeOddsChange> list = queryEuroOddsChanByCorpNo(matchId, corpNo);
		return list != null && !list.isEmpty();
	}
	
	public List<EuropeOddsChange> queryEuroOddsChanByCorpNo(
			long matchId, int corpNo) {
		String corpName = OkParseUtils.translateCorpName(corpNo);
		return euroOddsChangeDao.queryEuroOddsChanByCorpName(matchId, corpName);
	}
	
	public List<EuropeOddsChange> queryChangeNumByCorp(String oddsCorpName) {
		return euroOddsChangeDao.queryChangeNumByCorp(oddsCorpName);
	}
	
	public List<EuropeOddsChange> queryChangeTimeBeforeByCorp(
			String oddsCorpName) {
		return euroOddsChangeDao.queryChangeTimeBeforeByCorp(oddsCorpName);
	}
	
	public void updateEuroOddsChangeNum(
			List<EuropeOddsChange> europeOddsChangeList) {
		euroOddsChangeDao.updateEuroOddsChangeNum(europeOddsChangeList);
	}
	
	public void parseEuroOddsChangeDailyFromFile(File euroOddsChangeDailyHtml, int numOfSeq, boolean addInitOdds,
			String okUrlDate, Integer matchSeq) {
		euroOddsChangeDao.insertOddsChangeDailyBatch(parser.getEuropeOddsChangeDailyFromFile(euroOddsChangeDailyHtml,
				numOfSeq, addInitOdds, okUrlDate, matchSeq));
	}
	
	/**
	 * 从本地解析文件获取 EuropeOddsChangeDaily.
	 */
	public List<EuropeOddsChange> getEuroOddsChangeDailyFromFile(File euroOddsChangeDailyHtml,
			int numOfSeq, boolean addInitOdds, String okUrlDate, Integer matchSeq) {
		return parser.getEuropeOddsChangeDailyFromFile(euroOddsChangeDailyHtml, numOfSeq, addInitOdds, okUrlDate, matchSeq);
	}
	
	public void insertEuroOddsChangeDailyBatch(
			List<EuropeOddsChange> europeOddsChangeDailyList) {
		euroOddsChangeDao.insertOddsChangeDailyBatch(europeOddsChangeDailyList);
	}
	
	public void deleteEuroOddsChanDailyByCorpName(String okUrlDate,
			Integer matchSeq, String corpName) {
		EuropeOddsChange deleted = new EuropeOddsChange();
		deleted.setOkUrlDate(okUrlDate);
		deleted.setMatchSeq(matchSeq);
		deleted.setOddsCorpName(corpName);
		euroOddsChangeDao.deleteEuroOddsChanDailyByCorpName(deleted);
	}
	
	public void deleteEuroOddsChanDailyByMatchSeq(String okUrlDate,
			Integer matchSeq) {
		EuropeOddsChange deleted = new EuropeOddsChange();
		deleted.setOkUrlDate(okUrlDate);
		deleted.setMatchSeq(matchSeq);
		euroOddsChangeDao.deleteEuroOddsChanDailyByMatchSeq(deleted);
	}
	
	public List<EuropeOddsChange> queryEuroOddsChangeDailySb(
			EuropeOddsChange query) {
		return euroOddsChangeDao.queryEuroOddsChangeDailySb(query);
	}
	
	public List<EuropeOddsChange> queryDailyInitialWithResult(
			String oddsCorpName) {
		return euroOddsChangeDao.queryDailyInitialWithResult(oddsCorpName);
	}

	public EuroOddsChangeDao getEuroOddsChangeDao() {
		return euroOddsChangeDao;
	}

	public void setEuroOddsChangeDao(EuroOddsChangeDao euroOddsChangeDao) {
		this.euroOddsChangeDao = euroOddsChangeDao;
	}

	public EuroOddsChangeAllDao getEuroOddsChangeAllDao() {
		return euroOddsChangeAllDao;
	}

	public void setEuroOddsChangeAllDao(EuroOddsChangeAllDao euroOddsChangeAllDao) {
		this.euroOddsChangeAllDao = euroOddsChangeAllDao;
	}

	/**
	 * 获取 EuropeOddsChange.
	 */
	public List<EuropeOddsChange> getEuroOddsChange(long matchId, int matchSeq,
			int corpNo, int numOfSeq, boolean addInitOdds) {
		return parser.getEuropeOddsChange(matchId, matchSeq, corpNo, numOfSeq, addInitOdds);
	}
	
	/**
	 * 从本地解析文件获取 EuropeOddsChange.
	 */
	public List<EuropeOddsChange> getEuroOddsChangeFromFile(File euroOddsChangeHtml, int numOfSeq, boolean addInitOdds) {
		return parser.getEuropeOddsChangeFromFile(euroOddsChangeHtml, numOfSeq, addInitOdds);
	}

}
