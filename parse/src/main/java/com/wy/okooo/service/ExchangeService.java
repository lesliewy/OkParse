/**
 * 
 */
package com.wy.okooo.service;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import com.wy.okooo.domain.ExchangeAllAverage;
import com.wy.okooo.domain.ExchangeBfTurnoverDetail;
import com.wy.okooo.domain.ExchangeTransactionProp;

/**
 * 解析交易盈亏页面service(http://www.okooo.com/soccer/match/709562/exchanges/)
 * 
 * @author leslie
 *
 */
public interface ExchangeService {
	
	void parseExchangeInfo(long matchId, int matchSeq);
	
	void  parseExchangeInfoFromFile(File exchangeInfoHtml);
	
	void parseBfTurnoverDetail(long matchId, int matchSeq);
	
	void parseBfTurnoverDetailFromFile(File turnoverDetailHtml);
	
	boolean isExistsAllAvgById(long id);
	
	List<ExchangeAllAverage> queryAllAverageById(long id);
	
	boolean isExistsTurnoverDetailById(long id);
	
	List<ExchangeBfTurnoverDetail> queryBfTurnoverDetailById(long id);
	
	List<ExchangeBfTurnoverDetail> getBfTurnoverDetailFromFile(File turnoverDetailHtml);
	
	ExchangeTransactionProp getTransactionProp(long matchId, int matchSeq);
	
	ExchangeTransactionProp getTransactionPropFromFile(File exchangeInfoHtml);
	
	List<ExchangeTransactionProp> queryTransPropByTime(
			Timestamp beginTime, Timestamp endTime);
}
