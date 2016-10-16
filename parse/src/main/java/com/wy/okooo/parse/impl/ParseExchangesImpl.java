/**
 * 
 */
package com.wy.okooo.parse.impl;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.wy.okooo.domain.ExchangeAllAverage;
import com.wy.okooo.domain.ExchangeBfListing;
import com.wy.okooo.domain.ExchangeBfTurnoverDetail;
import com.wy.okooo.domain.ExchangeTransactionProp;
import com.wy.okooo.parse.ParseExchanges;
import com.wy.okooo.util.JsoupUtils;
import com.wy.okooo.util.OkParseUtils;

/**
 * 解析交易盈亏页面.(http://www.okooo.com/soccer/match/734052/exchanges/)
 * 
 * @author leslie
 * 
 */
public class ParseExchangesImpl implements ParseExchanges {

	// log4j
	private static Logger LOGGER = Logger.getLogger(ParseExchangesImpl.class
			.getName());

	// 确保解析99家平均、必发挂牌价、交易比例等在交易盈亏页面(http://www.okooo.com/soccer/match/686844/exchanges/)
	// 不需要重复获取document.
	private static Document document = null;

	private static int matchSeq4Doc = -1;

	/**
	 * 解析99家平均.
	 */
	public ExchangeAllAverage getAllAverage(long matchId, int matchSeq) {
		Document exchangInfoDoc = getExchangeDoc(matchSeq);
		return getAllAverageFromDoc(exchangInfoDoc);
	}

	/**
	 * 从本地文件解析99家平均.
	 */
	public ExchangeAllAverage getAllAverageFromFile(File exchangeInfoHtml) {
		Document exchangInfoDoc = Jsoup.parse(OkParseUtils
				.getFileContent(exchangeInfoHtml));
		return getAllAverageFromDoc(exchangInfoDoc);
	}

	private ExchangeAllAverage getAllAverageFromDoc(Document exchangInfoDoc) {
		if (exchangInfoDoc == null) {
			LOGGER.error("exchangInfoDoc is null, return now.");
			return null;
		}

		// #qnav > div:nth-child(3) > a:nth-child(3) 从 "交易盈亏" 获取 okMatchId
		String exchangeHrefStr = exchangInfoDoc.select(
				"#qnav > div:nth-child(3) > a:nth-child(3)").attr("href");
		// 考虑异常情况: 页面不完整.
		if(StringUtils.isBlank(exchangeHrefStr)){
			return null;
		}
		// 考虑异常情况: 有页面，但是数据不存在.
		String[] exchangeHrefArr = StringUtils.split(exchangeHrefStr, "/");
		// 考虑异常情况: 有页面，但是数据不存在.
		if(exchangeHrefArr.length < 3 || !StringUtils.isNumeric(exchangeHrefArr[2])){
			return null;
		}
		long okMatchId = Long.valueOf(exchangeHrefArr[2]);
		
		ExchangeAllAverage average = new ExchangeAllAverage();

		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(3)
		Elements hostElements = exchangInfoDoc
				.select("body > div.container_wrapper > div:nth-child(12) > table > tbody > tr:nth-child(3)");
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(3) > td:nth-child(2)
		String hostOddsStr = hostElements.select(" > td:nth-child(2)").text();
		Float hostOdds = null;
		if(StringUtils.isNotBlank(hostOddsStr)){
			hostOdds = Float.valueOf(hostOddsStr);
		}
		
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(3) > td:nth-child(3)
		String hostProbStr = hostElements.select(" > td:nth-child(3)").text();
		Float hostProb = deleteLastOneF(hostProbStr);

		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(4)
		Elements evenElements = exchangInfoDoc
				.select("body > div.container_wrapper > div:nth-child(12) > table > tbody > tr:nth-child(4)");
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(4) > td:nth-child(2)
		String evenOddsStr = evenElements.select(" > td:nth-child(2)").text();
		Float evenOdds = null;
		if(StringUtils.isNotBlank(evenOddsStr)){
			evenOdds = Float.valueOf(evenOddsStr);
		}
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(4) > td:nth-child(3)
		String evenProbStr = evenElements.select(" > td:nth-child(3)").text();
		Float evenProb = deleteLastOneF(evenProbStr);

		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(5)
		Elements visitingElements = exchangInfoDoc
				.select("body > div.container_wrapper > div:nth-child(12) > table > tbody > tr:nth-child(5)");
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(5) > td:nth-child(2)
		String visitingOddsStr = visitingElements.select(" > td:nth-child(2)")
				.text();
		Float visitingOdds = null;
		if(StringUtils.isNotBlank(visitingOddsStr)){
			visitingOdds = Float.valueOf(visitingOddsStr);
		}
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(5) > td:nth-child(3)
		String visitingProbStr = visitingElements.select(" > td:nth-child(3)")
				.text();
		Float visitingProb = deleteLastOneF(visitingProbStr);

		Timestamp timestamp = new Timestamp(Calendar.getInstance()
				.getTimeInMillis());
		LOGGER.debug("hostOdds: " + hostOdds + "; hostProb: " + hostProb
				+ "; evenOdds: " + evenOdds + "; evenProb: " + evenProb
				+ "; visitingOdds: " + visitingOdds + "; visitingProb: "
				+ visitingProb);
		average.setId(okMatchId);
		if (hostOdds != null) {
			average.setHostOdds(hostOdds);
		} else {
			average.setHostOdds(null);
		}

		if (hostProb != null) {
			average.setHostProb(hostProb);
		} else {
			average.setHostProb(null);
		}

		if (evenOdds != null) {
			average.setEvenOdds(evenOdds);
		} else {
			average.setEvenOdds(null);
		}

		if (evenProb != null) {
			average.setEvenProb(evenProb);
		} else {
			average.setEvenProb(null);
		}

		if (visitingOdds != null) {
			average.setVisitingOdds(visitingOdds);
		} else {
			average.setVisitingOdds(null);
		}

		if (visitingProb != null) {
			average.setVisitingProb(visitingProb);
		} else {
			average.setVisitingProb(null);
		}

		average.setTimestamp(timestamp);
		return average;
	}

	/**
	 * 解析交易量比例和庄家盈亏指数.
	 * 
	 * @param matchId
	 * @param matchSeq
	 * @return
	 */
	public ExchangeTransactionProp getTransactionProp(long matchId, int matchSeq) {
		Document exchangInfoDoc = getExchangeDoc(matchSeq);
		return getTransactionPropFromDoc(exchangInfoDoc);
	}

	/**
	 * 从本地文件解析交易量比例和庄家盈亏指数.
	 * 
	 * @param exchangeInfoHtml
	 * @return
	 */
	public ExchangeTransactionProp getTransactionPropFromFile(
			File exchangeInfoHtml) {
		Document exchangInfoDoc = Jsoup.parse(OkParseUtils
				.getFileContent(exchangeInfoHtml));
		return getTransactionPropFromDoc(exchangInfoDoc);
	}

	public ExchangeTransactionProp getTransactionPropFromDoc(
			Document exchangInfoDoc) {
		if (exchangInfoDoc == null) {
			LOGGER.error("exchangInfoDoc is null, return now.");
			return null;
		}

		// #qnav > div:nth-child(3) > a:nth-child(3) 从 "交易盈亏" 获取 okMatchId
		String exchangeHrefStr = exchangInfoDoc.select(
				"#qnav > div:nth-child(3) > a:nth-child(3)").attr("href");
		
		// 考虑异常情况: 页面不完整.
		if(StringUtils.isBlank(exchangeHrefStr)){
			return null;
		}
		// 考虑异常情况: 有页面，但是数据不存在.
		String[] exchangeHrefArr = StringUtils.split(exchangeHrefStr, "/");
		// 考虑异常情况: 有页面，但是数据不存在.
		if(exchangeHrefArr.length < 3 || !StringUtils.isNumeric(exchangeHrefArr[2])){
			return null;
		}
		long okMatchId = Long.valueOf(exchangeHrefArr[2]);
		
		ExchangeTransactionProp transProp = new ExchangeTransactionProp();

		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(3)
		Elements hostElements = exchangInfoDoc
				.select("body > div.container_wrapper > div:nth-child(12) > table > tbody > tr:nth-child(3)");
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(3) > td:nth-child(4)
		String hostBfStr = hostElements.select(" > td:nth-child(4)").text();
		Float hostBf = deleteLastOneF(hostBfStr);
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(3) > td:nth-child(5)
		String hostCompStr = hostElements.select(" > td:nth-child(5)").text();
		Float hostComp = deleteLastOneF(hostCompStr);
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(3) > td:nth-child(6)
		String hostBjSingleStr = hostElements.select(" > td:nth-child(6)")
				.text();
		Float hostBjSingle = deleteLastOneF(hostBjSingleStr);
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(3) > td:nth-child(11)
		String hostBfProlossIndexStr = hostElements.select(
				" > td:nth-child(11)").text();
		Integer hostBfProlossIndex = getIntegerByStr(hostBfProlossIndexStr);
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(3) > td:nth-child(12) > span
		String hostCompProlossIndexStr = hostElements.select(
				" > td:nth-child(12)").text();
		Integer hostCompProlossIndex = getIntegerByStr(hostCompProlossIndexStr);

		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(4)
		Elements evenElements = exchangInfoDoc
				.select("body > div.container_wrapper > div:nth-child(12) > table > tbody > tr:nth-child(4)");
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(4) > td:nth-child(4)
		String evenBfStr = evenElements.select(" > td:nth-child(4)").text();
		Float evenBf = deleteLastOneF(evenBfStr);
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(4) > td:nth-child(5)
		String evenCompStr = evenElements.select(" > td:nth-child(5)").text();
		Float evenComp = deleteLastOneF(evenCompStr);
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(4) > td:nth-child(6)
		String evenBjSingleStr = evenElements.select(" > td:nth-child(6)")
				.text();
		Float evenBjSingle = deleteLastOneF(evenBjSingleStr);
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(4) > td:nth-child(11)
		String evenBfProlossIndexStr = evenElements.select(
				" > td:nth-child(11)").text();
		Integer evenBfProlossIndex = getIntegerByStr(evenBfProlossIndexStr);
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(4) > td:nth-child(12) > span
		String evenCompProlossIndexStr = evenElements.select(
				" > td:nth-child(12)").text();
		Integer evenCompProlossIndex = getIntegerByStr(evenCompProlossIndexStr);

		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(5)
		Elements visitingElements = exchangInfoDoc
				.select("body > div.container_wrapper > div:nth-child(12) > table > tbody > tr:nth-child(5)");
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(5) > td:nth-child(4)
		String visitingBfStr = visitingElements.select(" > td:nth-child(4)")
				.text();
		Float visitingBf = deleteLastOneF(visitingBfStr);
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(5) > td:nth-child(5)
		String visitingCompStr = visitingElements.select(" > td:nth-child(5)")
				.text();
		Float visitingComp = deleteLastOneF(visitingCompStr);
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(5) > td:nth-child(6)
		String visitingBjSingleStr = visitingElements.select(
				" > td:nth-child(6)").text();
		Float visitingBjSingle = deleteLastOneF(visitingBjSingleStr);
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(5) > td:nth-child(11)
		String visitingBfProlossIndexStr = visitingElements.select(
				" > td:nth-child(11)").text();
		Integer visitingBfProlossIndex = getIntegerByStr(visitingBfProlossIndexStr);
		// body > div.container_wrapper > div:nth-child(12) > table > tbody >
		// tr:nth-child(5) > td:nth-child(12) > span
		String visitingCompProlossIndexStr = visitingElements.select(
				" > td:nth-child(12)").text();
		Integer visitingCompProlossIndex = getIntegerByStr(visitingCompProlossIndexStr);

		Timestamp timestamp = new Timestamp(Calendar.getInstance()
				.getTimeInMillis());
		LOGGER.debug("hostBf: " + hostBf + "; hostComp: " + hostComp
				+ "; hostBjSingle: " + hostBjSingle + "; evenBf: " + evenBf
				+ "; evenComp: " + evenComp + "; evenBjSingle: " + evenBjSingle
				+ "; visitingBf: " + visitingBf + "; visitingComp: "
				+ visitingComp + "; visitingBjSingle" + visitingBjSingle
				+ "; hostBfProlossIndex: " + hostBfProlossIndex
				+ "; hostCompProlossIndex: " + hostCompProlossIndex
				+ "; evenBfProlossIndex: " + evenBfProlossIndex
				+ "; evenCompProlossIndex" + evenCompProlossIndex
				+ "; visitingBfProlossIndex: " + visitingBfProlossIndex
				+ "; visitingCompProlossIndex: " + visitingCompProlossIndex);
		transProp.setId(okMatchId);
		if (hostBf != null) {
			transProp.setHostBf(hostBf);
		} else {
			transProp.setHostBf(null);
		}

		if (hostComp != null) {
			transProp.setHostComp(hostComp);
		} else {
			transProp.setHostComp(null);
		}

		if (hostBjSingle != null) {
			transProp.setHostBjSingle(hostBjSingle);
		} else {
			transProp.setHostBjSingle(null);
		}

		if (evenBf != null) {
			transProp.setEvenBf(evenBf);
		} else {
			transProp.setEvenBf(null);
		}

		if (evenComp != null) {
			transProp.setEvenComp(evenComp);
		} else {
			transProp.setEvenComp(null);
		}

		if (evenBjSingle != null) {
			transProp.setEvenBjSingle(evenBjSingle);
		} else {
			transProp.setEvenBjSingle(null);
		}

		if (visitingBf != null) {
			transProp.setVisitingBf(visitingBf);
		} else {
			transProp.setVisitingBf(null);
		}

		if (visitingComp != null) {
			transProp.setVisitingComp(visitingComp);
		} else {
			transProp.setVisitingComp(null);
		}

		if (visitingBjSingle != null) {
			transProp.setVisitingBjSingle(visitingBjSingle);
		} else {
			transProp.setVisitingBjSingle(null);
		}

		if (hostBfProlossIndex != null) {
			transProp.setHostBfProlossIndex(hostBfProlossIndex);
		} else {
			transProp.setHostBfProlossIndex(null);
		}

		if (hostCompProlossIndex != null) {
			transProp.setHostCompProlossIndex(hostCompProlossIndex);
		} else {
			transProp.setHostCompProlossIndex(null);
		}

		if (evenBfProlossIndex != null) {
			transProp.setEvenBfProlossIndex(evenBfProlossIndex);
		} else {
			transProp.setEvenBfProlossIndex(null);
		}

		if (evenCompProlossIndex != null) {
			transProp.setEvenCompProlossIndex(evenCompProlossIndex);
		} else {
			transProp.setEvenCompProlossIndex(null);
		}

		if (visitingBfProlossIndex != null) {
			transProp.setVisitingBfProlossIndex(visitingBfProlossIndex);
		} else {
			transProp.setVisitingBfProlossIndex(null);
		}

		if (visitingCompProlossIndex != null) {
			transProp.setVisitingCompProlossIndex(visitingCompProlossIndex);
		} else {
			transProp.setVisitingCompProlossIndex(null);
		}

		transProp.setTimestamp(timestamp);
		return transProp;
	}

	/**
	 * 解析必发挂牌信息，包括买家、卖家.
	 * 
	 * @param matchId
	 * @param matchSeq
	 * @return
	 */
	public ExchangeBfListing getBfListing(long matchId, int matchSeq) {
		Document exchangInfoDoc = getExchangeDoc(matchSeq);
		return getBfListingFromDoc(exchangInfoDoc);
	}

	/**
	 * 从本地文件解析必发挂牌信息，包括买家、卖家.
	 * 
	 * @param exchangeInfoHtml
	 * @return
	 */
	public ExchangeBfListing getBfListingFromFile(File exchangeInfoHtml) {
		Document exchangInfoDoc = Jsoup.parse(OkParseUtils
				.getFileContent(exchangeInfoHtml));
		return getBfListingFromDoc(exchangInfoDoc);
	}

	private ExchangeBfListing getBfListingFromDoc(Document exchangInfoDoc) {
		if (exchangInfoDoc == null) {
			LOGGER.error("exchangInfoDoc is null, return now.");
			return null;
		}

		// #qnav > div:nth-child(3) > a:nth-child(3) 从 "交易盈亏" 获取 okMatchId
		String exchangeHrefStr = exchangInfoDoc.select(
				"#qnav > div:nth-child(3) > a:nth-child(3)").attr("href");
		// 考虑异常情况: 页面不完整.
		if(StringUtils.isBlank(exchangeHrefStr)){
			return null;
		}
		// 考虑异常情况: 有页面，但是数据不存在.
		String[] exchangeHrefArr = StringUtils.split(exchangeHrefStr, "/");
		// 考虑异常情况: 有页面，但是数据不存在.
		if(exchangeHrefArr.length < 3 || !StringUtils.isNumeric(exchangeHrefArr[2])){
			return null;
		}
		long okMatchId = Long.valueOf(exchangeHrefArr[2]);

		ExchangeBfListing bfListing = new ExchangeBfListing();

		// body > div.container_wrapper > div:nth-child(11) > table > tbody >
		// tr:nth-child(3)
		Elements hostElements = exchangInfoDoc
				.select("body > div.container_wrapper > div:nth-child(11) > table > tbody > tr:nth-child(3)");
		// body > div.container_wrapper > div:nth-child(11) > table > tbody >
		// tr:nth-child(3) > td:nth-child(2)
		String hostBuyersPriceStr = hostElements.select(" > td:nth-child(2)")
				.text();
		Float hostBuyersPrice = getFloatByStr(hostBuyersPriceStr);
		// body > div.container_wrapper > div:nth-child(11) > table > tbody >
		// tr:nth-child(3) > td:nth-child(3)
		String hostBuyersQuantityStr = hostElements
				.select(" > td:nth-child(3)").text();
		Integer hostBuyersQuantity = getIntegerByStr(hostBuyersQuantityStr);
		// body > div.container_wrapper > div:nth-child(11) > table > tbody >
		// tr:nth-child(3) > td:nth-child(4)
		String hostSellersPriceStr = hostElements.select(" > td:nth-child(4)")
				.text();
		Float hostSellersPrice = getFloatByStr(hostSellersPriceStr);
		// body > div.container_wrapper > div:nth-child(11) > table > tbody >
		// tr:nth-child(3) > td:nth-child(5)
		String hostSellersQuantityStr = hostElements.select(
				" > td:nth-child(5)").text();
		Integer hostSellersQuantity = getIntegerByStr(hostSellersQuantityStr);

		// body > div.container_wrapper > div:nth-child(11) > table > tbody >
		// tr:nth-child(4)
		Elements evenElements = exchangInfoDoc
				.select("body > div.container_wrapper > div:nth-child(11) > table > tbody > tr:nth-child(4)");
		String evenBuyersPriceStr = evenElements.select(" > td:nth-child(2)")
				.text();
		Float evenBuyersPrice = getFloatByStr(evenBuyersPriceStr);
		String evenBuyersQuantityStr = evenElements
				.select(" > td:nth-child(3)").text();
		Integer evenBuyersQuantity = getIntegerByStr(evenBuyersQuantityStr);
		String evenSellersPriceStr = evenElements.select(" > td:nth-child(4)")
				.text();
		Float evenSellersPrice = getFloatByStr(evenSellersPriceStr);
		String evenSellersQuantityStr = evenElements.select(
				" > td:nth-child(5)").text();
		Integer evenSellersQuantity = getIntegerByStr(evenSellersQuantityStr);

		// body > div.container_wrapper > div:nth-child(11) > table > tbody >
		// tr:nth-child(5)
		Elements visitingElements = exchangInfoDoc
				.select("body > div.container_wrapper > div:nth-child(11) > table > tbody > tr:nth-child(5)");
		String visitingBuyersPriceStr = visitingElements.select(
				" > td:nth-child(2)").text();
		Float visitingBuyersPrice = getFloatByStr(visitingBuyersPriceStr);
		String visitingBuyersQuantityStr = visitingElements.select(
				" > td:nth-child(3)").text();
		Integer visitingBuyersQuantity = getIntegerByStr(visitingBuyersQuantityStr);
		String visitingSellersPriceStr = visitingElements.select(
				" > td:nth-child(4)").text();
		Float visitingSellersPrice = getFloatByStr(visitingSellersPriceStr);
		String visitingSellersQuantityStr = visitingElements.select(
				" > td:nth-child(5)").text();
		Integer visitingSellersQuantity = getIntegerByStr(visitingSellersQuantityStr);

		Timestamp timestamp = new Timestamp(Calendar.getInstance()
				.getTimeInMillis());
		LOGGER.debug("hostBuyersPrice: " + hostBuyersPrice
				+ "; hostBuyersQuantity: " + hostBuyersQuantity
				+ "; hostSellersPrice: " + hostSellersPrice
				+ "; hostSellersQuantity: " + hostSellersQuantity
				+ "; evenBuyersPrice: " + evenBuyersPrice
				+ "; evenBuyersQuantity: " + evenBuyersQuantity
				+ "; evenSellersPrice: " + evenSellersPrice
				+ "; evenSellersQuantity: " + evenSellersQuantity
				+ "; visitingBuyersPrice" + visitingBuyersPrice
				+ "; visitingBuyersQuantity: " + visitingBuyersQuantity
				+ "; visitingSellersPrice: " + visitingSellersPrice
				+ "; visitingSellersQuantity: " + visitingSellersQuantity);
		bfListing.setId(okMatchId);
		bfListing.setHostBuyersPrice(hostBuyersPrice == null ? null
				: hostBuyersPrice);
		bfListing.setHostBuyersQuantity(hostBuyersQuantity == null ? null
				: hostBuyersQuantity);
		bfListing.setHostSellersPrice(hostSellersPrice == null ? null
				: hostSellersPrice);
		bfListing.setHostSellersQuantity(hostSellersQuantity == null ? null
				: hostSellersQuantity);
		bfListing.setEvenBuyersPrice(evenBuyersPrice == null ? null
				: evenBuyersPrice);
		bfListing.setEvenBuyersQuantity(evenBuyersQuantity == null ? null
				: evenBuyersQuantity);
		bfListing.setEvenSellersPrice(evenSellersPrice == null ? null
				: evenSellersPrice);
		bfListing.setEvenSellersQuantity(evenSellersQuantity == null ? null
				: evenSellersQuantity);
		bfListing.setVisitingBuyersPrice(visitingBuyersPrice == null ? null
				: visitingBuyersPrice);
		bfListing
				.setVisitingBuyersQuantity(visitingBuyersQuantity == null ? null
						: visitingBuyersQuantity);
		bfListing.setVisitingSellersPrice(visitingSellersPrice == null ? null
				: visitingSellersPrice);
		bfListing
				.setVisitingSellersQuantity(visitingSellersQuantity == null ? null
						: visitingSellersQuantity);
		bfListing.setTimestamp(timestamp);
		return bfListing;
	}

	/**
	 * 解析必发交易明细(http://www.okooo.com/soccer/match/709562/exchanges/detail/)
	 * 
	 * @param matchId
	 * @param matchSeq
	 * @return
	 */
	public List<ExchangeBfTurnoverDetail> getTurnoverDetail(long matchId,
			int matchSeq) {
		Document turnoverDetailDoc = JsoupUtils.getExchangeDetailDoc(matchSeq);
		return getTurnoverDetailFromDoc(turnoverDetailDoc);
	}

	/**
	 * 从本地文件解析必发交易明细(http://www.okooo.com/soccer/match/709562/exchanges/detail/)
	 * 
	 * @param turnoverDetailHtml
	 * @return
	 */
	public List<ExchangeBfTurnoverDetail> getTurnoverDetailFromFile(
			File turnoverDetailHtml) {
		Document turnoverDetailDoc = Jsoup.parse(OkParseUtils
				.getFileContent(turnoverDetailHtml));
		return getTurnoverDetailFromDoc(turnoverDetailDoc);
	}

	private List<ExchangeBfTurnoverDetail> getTurnoverDetailFromDoc(
			Document turnoverDetailDoc) {
		if (turnoverDetailDoc == null) {
			LOGGER.error("turnoverDetailDoc is null, return now.");
			return null;
		}

		// body > div > div.lqnav > div > a:nth-child(5) 从面包屑中获取 okMatchId
		String turnoverDetailHrefStr = turnoverDetailDoc.select(
				"body > div > div.lqnav > div > a:nth-child(5)").attr("href");
		// 考虑异常情况: 页面不完整.
		if(StringUtils.isBlank(turnoverDetailHrefStr)){
			return null;
		}
		// 考虑异常情况: 有页面，但是数据不存在.
		String[] turnoverDetailHrefArr = StringUtils.split(turnoverDetailHrefStr, "/");
		// 考虑异常情况: 有页面，但是数据不存在.
		if(turnoverDetailHrefArr.length < 3 || !StringUtils.isNumeric(turnoverDetailHrefArr[2])){
			return null;
		}
		long okMatchId = Long.valueOf(turnoverDetailHrefArr[2]);
		
		List<ExchangeBfTurnoverDetail> details = new ArrayList<ExchangeBfTurnoverDetail>();
		// 设置最大值.
		int max = 20;
		// 只取最近的10次变化
		int numOfChanges = 10;
		int lineNo = 2;
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int seq = 0;
		while (lineNo++ < max) {
			seq++;
			// body > div > table > tbody > tr:nth-child(3)
			// body > div > table > tbody > tr:nth-child(4)
			// body > div > table > tbody > tr:nth-child(5)
			Elements elementsTR = turnoverDetailDoc
					.select("body > div > table > tbody > tr:nth-child("
							+ lineNo + ")");

			// 获取完毕，或者已达到上限则退出
			if (elementsTR == null || elementsTR.isEmpty()
					|| details.size() == numOfChanges) {
				break;
			}

			// body > div > table > tbody > tr:nth-child(3) > td:nth-child(1)
			String toTimeStr = elementsTR.select(" > td:nth-child(1)").text();
			String oddsTimeFormat = year + "-" + toTimeStr;
			Timestamp toTime = Timestamp.valueOf(oddsTimeFormat);

			// body > div > table > tbody > tr:nth-child(3) > td:nth-child(2)
			String hostPriceStr = elementsTR.select(" > td:nth-child(2)")
					.text();
			float hostPrice = Float.valueOf(hostPriceStr);

			// body > div > table > tbody > tr:nth-child(3) > td:nth-child(3)
			String hostTotalStr = elementsTR.select(" > td:nth-child(3)")
					.text();
			Integer hostTotal = null;
			if(StringUtils.isNotBlank(hostTotalStr)){
				hostTotal = Integer.valueOf(hostTotalStr.replaceAll(",", ""));
			}

			// body > div > table > tbody > tr:nth-child(3) > td:nth-child(4)
			String hostSingleStr = elementsTR.select(" > td:nth-child(4)")
					.text();
			Integer hostSingle = getIntegerByStr(hostSingleStr);

			// body > div > table > tbody > tr:nth-child(3) > td:nth-child(5)
			String hostBuySellStr = elementsTR.select(" > td:nth-child(5)")
					.text();
			String hostBuySell = getBuySell(hostBuySellStr);

			// body > div > table > tbody > tr:nth-child(3) >
			// td.borderLeft.trbghui
			String evenPriceStr = elementsTR.select(" > td:nth-child(6)")
					.text();
			float evenPrice = Float.valueOf(evenPriceStr);

			// body > div > table > tbody > tr:nth-child(3) > td:nth-child(7)
			String evenTotalStr = elementsTR.select(" > td:nth-child(7)")
					.text();
			Integer evenTotal = null;
			if(StringUtils.isNotBlank(evenTotalStr)){
				evenTotal = Integer.valueOf(evenTotalStr.replaceAll(",", ""));
			}

			// body > div > table > tbody > tr:nth-child(3) > td:nth-child(8)
			String evenSingleStr = elementsTR.select(" > td:nth-child(8)")
					.text();
			Integer evenSingle = getIntegerByStr(evenSingleStr);

			// body > div > table > tbody > tr:nth-child(3) >
			// td.borderRight.trbghui
			String evenBuySellStr = elementsTR.select(" > td:nth-child(9)")
					.text();
			String evenBuySell = getBuySell(evenBuySellStr);

			// body > div > table > tbody > tr:nth-child(3) > td:nth-child(10)
			String visitingPriceStr = elementsTR.select(" > td:nth-child(10)")
					.text();
			Float visitingPrice = Float.valueOf(visitingPriceStr);

			// body > div > table > tbody > tr:nth-child(3) > td:nth-child(11)
			String visitingTotalStr = elementsTR.select(" > td:nth-child(11)")
					.text();
			Integer visitingTotal = null;
			if(StringUtils.isNotBlank(visitingTotalStr)){
				visitingTotal = Integer.valueOf(visitingTotalStr
						.replaceAll(",", ""));
			}

			// body > div > table > tbody > tr:nth-child(3) > td:nth-child(12)
			String visitingSingleStr = elementsTR.select(" > td:nth-child(12)")
					.text();
			Integer visitingSingle = getIntegerByStr(visitingSingleStr);

			// body > div > table > tbody > tr:nth-child(3) > td:nth-child(13)
			String visitingBuySellStr = elementsTR
					.select(" > td:nth-child(13)").text();
			String visitingBuySell = getBuySell(visitingBuySellStr);

			Timestamp timestamp = new Timestamp(Calendar.getInstance()
					.getTimeInMillis());

			LOGGER.debug("oddsTimeFormat: " + oddsTimeFormat + "; hostPrice: "
					+ hostPrice + "; hostTotal: " + hostTotal
					+ "; hostSingle: " + hostSingle + "; hostBuySell: "
					+ hostBuySell + "; evenPrice: " + evenPrice
					+ "; evenTotal: " + evenTotal + "; evenTotal: " + evenTotal
					+ "; evenSingle: " + evenSingle + "; evenBuySell: "
					+ evenBuySell + "; visitingPrice: " + visitingPrice
					+ "; visitingTotal: " + visitingTotal
					+ "; visitingSingle: " + visitingSingle
					+ "; visitingBuySell: " + visitingBuySell);

			ExchangeBfTurnoverDetail detail = new ExchangeBfTurnoverDetail();
			detail.setId(okMatchId);
			detail.setSeq(seq);
			detail.setToTime(toTime);
			detail.setHostPrice(hostPrice);
			detail.setHostTotal(hostTotal);
			detail.setHostSingle(hostSingle == null ? null : hostSingle);
			detail.setHostBuySell(StringUtils.isEmpty(hostBuySell) ? ""
					: hostBuySell);
			detail.setEvenPrice(evenPrice);
			detail.setEvenTotal(evenTotal);
			detail.setEvenSingle(evenSingle == null ? null : evenSingle);
			detail.setEvenBuySell(StringUtils.isEmpty(evenBuySell) ? ""
					: evenBuySell);
			detail.setVisitingPrice(visitingPrice);
			detail.setVisitingTotal(visitingTotal);
			detail.setVisitingSingle(visitingSingle == null ? null
					: visitingSingle);
			detail.setVisitingBuySell(StringUtils.isEmpty(visitingBuySell) ? ""
					: visitingBuySell);
			detail.setTimestamp(timestamp);
			details.add(detail);
		}

		return details;
	}

	/**
	 * 去掉最后的一个字符，然后转为Float.
	 * 
	 * @param str
	 * @return
	 */
	private Float deleteLastOneF(String str) {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		return Float.valueOf(str.substring(0, str.length() - 1));
	}

	/**
	 * 去掉最后的一个字符，然后转为Integer
	 * 
	 * @param str
	 * @return
	 */
	// private Integer deleteLastOneI(String str) {
	// if (StringUtils.isEmpty(str)) {
	// return null;
	// }
	// return Integer.valueOf(str.substring(0, str.length() - 1));
	// }

	// private void setTransactionProp(ExchangeTransactionProp exchange, Float
	// flo){
	// if(flo != null){
	// exchange.set
	// }
	// }

	private Float getFloatByStr(String str) {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		return Float.valueOf(str);
	}

	private Integer getIntegerByStr(String str) {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		return Integer.valueOf(str);
	}

	private String getBuySell(String str) {
		if ("买".equals(str)) {
			return "B";
		}
		if ("卖".equals(str)) {
			return "S";
		}
		return null;
	}

	/**
	 * 获取交易盈亏页面document, 确保一个页面只获取一次.
	 * 
	 * @param matchSeq
	 * @return
	 */
	private static Document getExchangeDoc(int matchSeq) {
		if (matchSeq4Doc != matchSeq || document == null) {
			document = JsoupUtils.getExchangeDoc(matchSeq);
		}
		matchSeq4Doc = matchSeq;
		return document;
	}

}
