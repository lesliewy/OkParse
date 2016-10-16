/**
 * 
 */
package com.wy.okooo.service.impl;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import com.wy.okooo.dao.ExchangeAllAverageDao;
import com.wy.okooo.dao.ExchangeBfListingDao;
import com.wy.okooo.dao.ExchangeBfTurnoverDetailDao;
import com.wy.okooo.dao.ExchangeTransactionPropDao;
import com.wy.okooo.domain.ExchangeAllAverage;
import com.wy.okooo.domain.ExchangeBfTurnoverDetail;
import com.wy.okooo.domain.ExchangeTransactionProp;
import com.wy.okooo.parse.ParseExchanges;
import com.wy.okooo.parse.impl.ParseExchangesImpl;
import com.wy.okooo.service.ExchangeService;

/**
 * 解析交易盈亏页面service(http://www.okooo.com/soccer/match/709562/exchanges/)
 * 
 * @author leslie
 *
 */
public class ExchangeServiceImpl implements ExchangeService {

	private ExchangeAllAverageDao allAverageDao;
	
	private ExchangeBfListingDao bfListingDao;
	
	private ExchangeBfTurnoverDetailDao turnoverDetailDao;
	
	private ExchangeTransactionPropDao transactionPropDao;
	
	private ParseExchanges parser = new ParseExchangesImpl();
	
	public void parseExchangeInfo(long matchId, int matchSeq) {
		
		parseAllAverage(matchId, matchSeq);
		
		parseBfListing(matchId, matchSeq);
		
		parseTransactionProp(matchId, matchSeq);
	}
	
	public void parseExchangeInfoFromFile(File exchangeInfoHtml) {
		parseAllAverageFromFile(exchangeInfoHtml);
		
		parseBfListingFromFile(exchangeInfoHtml);
		
		parseTransactionPropFromFile(exchangeInfoHtml);
	}
	
	public void parseBfTurnoverDetail(long matchId, int matchSeq){
		turnoverDetailDao.insertBatch(parser.getTurnoverDetail(matchId, matchSeq));
	}
	
	public void parseBfTurnoverDetailFromFile(File turnoverDetailHtml) {
		turnoverDetailDao.insertBatch(parser.getTurnoverDetailFromFile(turnoverDetailHtml));
	}
	
	public List<ExchangeBfTurnoverDetail> getBfTurnoverDetailFromFile(File turnoverDetailHtml){
		return parser.getTurnoverDetailFromFile(turnoverDetailHtml);
	}
	
	public ExchangeTransactionProp getTransactionPropFromFile(File exchangeInfoHtml){
		return parser.getTransactionPropFromFile(exchangeInfoHtml);
	}
	
	public ExchangeTransactionProp getTransactionProp(long matchId, int matchSeq){
		return parser.getTransactionProp(matchId, matchSeq);
	}
	
	/**
	 * 解析99家平均.
	 * @param matchId
	 * @param matchSeq
	 */
	private void parseAllAverage(long matchId, int matchSeq) {
		allAverageDao.insert(parser.getAllAverage(matchId, matchSeq));
	}
	
	/**
	 * 从本地文件解析99家平均.
	 */
	private void parseAllAverageFromFile(File exchangeInfoHtml) {
		allAverageDao.insert(parser.getAllAverageFromFile(exchangeInfoHtml));
	}
	
	/**
	 * 解析必发挂牌价，包括买家、卖家.
	 * @param matchId
	 * @param matchSeq
	 */
	private void parseBfListing(long matchId, int matchSeq){
		bfListingDao.insert(parser.getBfListing(matchId, matchSeq));
	}
	
	/**
	 * 从本地文件解析必发挂牌价，包括买家、卖家.
	 */
	private void parseBfListingFromFile(File exchangeInfoHtml){
		bfListingDao.insert(parser.getBfListingFromFile(exchangeInfoHtml));
	}
	
	/**
	 * 解析交易比例.
	 * @param matchId
	 * @param matchSeq
	 */
	private void parseTransactionProp(long matchId, int matchSeq){
		transactionPropDao.insert(parser.getTransactionProp(matchId, matchSeq));
	}
	
	/**
	 * 从本地文件解析交易比例.
	 */
	private void parseTransactionPropFromFile(File exchangeInfoHtml){
		transactionPropDao.insert(parser.getTransactionPropFromFile(exchangeInfoHtml));
	}
	
	public boolean isExistsAllAvgById(long id) {
		List<ExchangeAllAverage> list = queryAllAverageById(id);
		return list != null && !list.isEmpty();
	}

	public List<ExchangeAllAverage> queryAllAverageById(long id) {
		return allAverageDao.queryAllAverageById(id);
	}
	
	public boolean isExistsTurnoverDetailById(long id) {
		List<ExchangeBfTurnoverDetail> list = queryBfTurnoverDetailById(id);
		return list != null && !list.isEmpty();
	}

	public List<ExchangeBfTurnoverDetail> queryBfTurnoverDetailById(long id) {
		return turnoverDetailDao.queryBfTurnoverDetailById(id);
	}
	
	public List<ExchangeTransactionProp> queryTransPropByTime(
			Timestamp beginTime, Timestamp endTime) {
		return transactionPropDao.queryTransPropByTime(beginTime, endTime);
	}
	
	public ExchangeAllAverageDao getAllAverageDao() {
		return allAverageDao;
	}

	public void setAllAverageDao(ExchangeAllAverageDao allAverageDao) {
		this.allAverageDao = allAverageDao;
	}

	public ExchangeBfListingDao getBfListingDao() {
		return bfListingDao;
	}

	public void setBfListingDao(ExchangeBfListingDao bfListingDao) {
		this.bfListingDao = bfListingDao;
	}

	public ExchangeBfTurnoverDetailDao getTurnoverDetailDao() {
		return turnoverDetailDao;
	}

	public void setTurnoverDetailDao(ExchangeBfTurnoverDetailDao turnoverDetailDao) {
		this.turnoverDetailDao = turnoverDetailDao;
	}

	public ExchangeTransactionPropDao getTransactionPropDao() {
		return transactionPropDao;
	}

	public void setTransactionPropDao(ExchangeTransactionPropDao transactionPropDao) {
		this.transactionPropDao = transactionPropDao;
	}

}
