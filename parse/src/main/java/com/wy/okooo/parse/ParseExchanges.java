/**
 * 
 */
package com.wy.okooo.parse;

import java.io.File;
import java.util.List;

import com.wy.okooo.domain.ExchangeAllAverage;
import com.wy.okooo.domain.ExchangeBfListing;
import com.wy.okooo.domain.ExchangeBfTurnoverDetail;
import com.wy.okooo.domain.ExchangeTransactionProp;

/**
 * 解析交易盈亏页面.(http://www.okooo.com/soccer/match/734052/exchanges/)
 * 
 * @author leslie
 * 
 */
public interface ParseExchanges {

	ExchangeAllAverage getAllAverage(long matchId, int matchSeq);
	
	ExchangeAllAverage getAllAverageFromFile(File exchangeInfoHtml);

	ExchangeTransactionProp getTransactionProp(long matchId, int matchSeq);
	
	ExchangeTransactionProp getTransactionPropFromFile(File exchangeInfoHtml);

	ExchangeBfListing getBfListing(long matchId, int matchSeq);
	
	ExchangeBfListing getBfListingFromFile(File exchangeInfoHtml);

	List<ExchangeBfTurnoverDetail> getTurnoverDetail(long matchId, int matchSeq);
	
	List<ExchangeBfTurnoverDetail> getTurnoverDetailFromFile(File turnoverDetailHtml);
}
