package com.wy.okooo.parse.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.wy.okooo.parse.ParseUrl;
import com.wy.okooo.util.JsoupUtils;
import com.wy.okooo.util.OkConstant;

/**
 * 
 * @author leslie
 * 
 */
public class ParseOkoooUrl implements ParseUrl {

	// log4j
	private static Logger LOGGER = Logger.getLogger(ParseOkoooUrl.class
			.getName());

	private static String singleMatchUrl = "http://www.okooo.com/danchang/";

	/**
	 * 获取足球单场的页面.
	 * 
	 * @return
	 */
	public String findMatchUrl() {
		return singleMatchUrl;
	}

	/**
	 * 获取欧赔页面url.
	 * 
	 * @param doc
	 *            足球单场的Document
	 * @param matchSeq
	 *            比赛序号.
	 * @return
	 */
	public String findEuroOddsUrl(Document doc, int matchSeq) {
		if (doc == null) {
			return null;
		}
		String cssPath = "#tr" + matchSeq + " > td.tdfx.td8 > a:nth-child(1)";
		LOGGER.debug("cssPath: " + cssPath);
		Elements select = doc.select(cssPath);
		// 欧赔url, 其他的url由此解析获得.
		return (select == null || select.isEmpty())? "" : OkConstant.mainUrl + select.attr("href");
	}

	/**
	 * 获取亚盘页面url
	 * 
	 * @param doc
	 *            足球单场的Document
	 * @param matchSeq
	 *            比赛序号.
	 * @return
	 */
	public String findAsiaOddsUrl(Document doc, int matchSeq) {
		String euroOddsUrl = findEuroOddsUrl(doc, matchSeq);
		return euroOddsUrl.replaceAll("odds", "ah");
	}

	/**
	 * 获取某个博彩公司某场比赛的欧赔变化页面url
	 * 
	 * @param doc
	 *            足球单场的Document
	 * @param matchSeq
	 *            比赛序号.
	 * @param corpNo
	 *            博彩公司序号.
	 * @return
	 */
	public String findEuroOddsChangeUrl(Document doc, int matchSeq, int corpNo) {
		String euroOddsUrl = findEuroOddsUrl(doc, matchSeq);
		return StringUtils.isBlank(euroOddsUrl) ? StringUtils.EMPTY : euroOddsUrl + "change/" + corpNo + "/";
	}

	/**
	 * 获取某个博彩公司某场比赛的亚盘变化页面url
	 * 
	 * @param doc
	 *            足球单场的Document
	 * @param matchSeq
	 *            比赛序号.
	 * @param corpNo
	 *            博彩公司序号.
	 * @return
	 */
	public String findAsiaOddsChangeUrl(Document doc, int matchSeq, int corpNo) {
		String asiaOddsUrl = findAsiaOddsUrl(doc, matchSeq);
		return StringUtils.isBlank(asiaOddsUrl)? StringUtils.EMPTY : asiaOddsUrl + "change/" + corpNo + "/";
	}

	/**
	 * 根据比赛序号(matchSeq) 获取某场具体比赛的url(如:
	 * http://www.okooo.com/soccer/match/723494/odds/)
	 * 
	 * TODO: mainUrl 需要修改, 考虑期数
	 * 
	 * @param matchSeq
	 *            比赛序号 #SelectLotteryNo > option:nth-child(1) #SelectLotteryNo >
	 *            option:nth-child(2)
	 */

	public String findOneMatchUrl(int matchSeq) {
		Document doc = JsoupUtils.getMatchesDoc();

		// String xpath = "*[@id=\"tr" + matchId + "\"] td[8] a[1]";
		String cssPath = "#tr" + matchSeq + " > td.tdfx.td8 > a:nth-child(1)";
		LOGGER.debug("cssPath: " + cssPath);
		Elements select = doc.select(cssPath);
		return (select == null || select.isEmpty()) ? StringUtils.EMPTY : OkConstant.mainUrl + select.attr("href");
	}

	/**
	 * 获取单场比赛页(http://www.okooo.com/danchang/) 中的所有具体比赛的url.
	 * 
	 */
	public List<String> findOneMatchUrl() {
		int numOfMatch = getNumOfMatch();
		List<String> matchUrls = new ArrayList<String>();
		int i = 0;
		while (i++ < numOfMatch) {
			matchUrls.add(findOneMatchUrl(i));
		}
		// for (Element element : select) {
		// // element.outerHtml() 和 element.toString()效果一样
		// System.out.println("链接源代码：" + element.outerHtml());
		// urlStr = element.attr("href");
		//
		// System.out.println("链接地址：" + urlStr + "  链接文本："
		// + element.text());
		// if(element.nextSibling() != null){
		// System.out.println(" next sbl:" + element.nextSibling().nodeName());
		// }
		// if(urlStr.matches("/soccer/match/.*/odds/")){
		// matchUrls.add(mainUrl + urlStr);
		// }
		// }
		return matchUrls;
	}

	/**
	 * 获取单场页(http://www.okooo.com/danchang/)中的比赛总场次. 根据页面的布局先获取 上一天 + 当天以后.
	 * 
	 * @return
	 */
	private int getNumOfMatch() {
		Document doc = JsoupUtils.getMatchesDoc();
		Elements yesterday = null;
		int yesterdaySize = 0;

		yesterday = doc.select("#table2 > tbody > tr");
		if (yesterday != null) {
			yesterdaySize = yesterday.size() - 1;
		}
		LOGGER.debug("yesterday size: " + yesterdaySize);
		return yesterdaySize + getNumOfMatchFromCurrDay();
	}

	/**
	 * 根据页面布局获取 当天 + 当天以后 的比赛总场次数.
	 * 
	 * @return
	 */
	private int getNumOfMatchFromCurrDay() {
		Document doc = JsoupUtils.getMatchesDoc();
		Elements currentDay = null;
		Elements tomorrow = null;
		int currentDaySize = 0;
		int tomorrowSize = 0;

		currentDay = doc.select("#table3 > tbody > tr");
		tomorrow = doc.select("#table4 > tbody > tr");
		if (currentDay != null) {
			currentDaySize = currentDay.size() - 1;
		}
		if (tomorrow != null) {
			tomorrowSize = tomorrow.size() - 1;
		}
		LOGGER.debug("currentDay size: " + currentDaySize);
		LOGGER.debug("tomorrow size: " + tomorrowSize);
		return currentDaySize + tomorrowSize;

	}

	/**
	 * 根据具体比赛url(如: http://www.okooo.com/soccer/match/723494/odds/)
	 * 获取交易盈亏页面的url(相当于点击 "交易盈亏").
	 * 
	 */
	public String findProfitLossUrl(String matchUrl) {
		URL url;
		Document doc = null;
		Elements select = null;
		try {
			// 连接一个页面
			url = new URL(matchUrl);

			// 解析获取Document对象
			doc = Jsoup.parse(url, 3 * 1000);

			String cssPath = "#qnav > div:nth-child(3) > a:nth-child(3)"; // css
																			// path
			LOGGER.debug("cssPath: " + cssPath);
			select = doc.select(cssPath); // 获取页面上所有的a元素
		} catch (MalformedURLException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}

		return (select == null || select.isEmpty()) ? StringUtils.EMPTY : OkConstant.mainUrl + select.attr("href");
	}

	/**
	 * 获取某场比赛的必发的胜平负的交易量比例.
	 */
	public Map<String, String> findProbability(String profitLossUrl) {
		Map<String, String> probability = new HashMap<String, String>();

		URL url;
		Document doc = null;
		Elements select = null;

		String successPro = null;
		String evenPro = null;
		String defeatPro = null;
		try {
			// 连接一个页面
			url = new URL(profitLossUrl);

			// 解析获取Document对象
			doc = Jsoup.parse(url, 3 * 1000);

			String victoryCssPath = "body > div.container_wrapper > div:nth-child(12) > table > tbody > tr:nth-child(3) > td:nth-child(4)"; // css
																																			// path
			String evenCssPath = "body > div.container_wrapper > div:nth-child(12) > table > tbody > tr:nth-child(4) > td:nth-child(4)";
			String defeatCssPath = "body > div.container_wrapper > div:nth-child(12) > table > tbody > tr:nth-child(5) > td:nth-child(4)";
			LOGGER.debug("victoryCssPath: " + victoryCssPath);
			LOGGER.debug("evenCssPath: " + evenCssPath);
			LOGGER.debug("defeatCssPath: " + defeatCssPath);

			select = doc.select(victoryCssPath);
			if (select != null) {
				successPro = select.text();
			}

			select = doc.select(evenCssPath);
			if (select != null) {
				evenPro = select.text();
			}

			select = doc.select(defeatCssPath);
			if (select != null) {
				defeatPro = select.text();
			}

		} catch (MalformedURLException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		probability.put("success", successPro);
		probability.put("even", evenPro);
		probability.put("defeat", defeatPro);
		return probability;
	}

	/**
	 * 获取当天单场url(http://www.okooo.com/danchang/) 中所有比赛的必发的胜平负的交易量比例.
	 * 
	 * @return
	 */
	public List<Map<String, String>> findAllMatchProbability() {
		int numOfMatch = getNumOfMatch();
		List<Map<String, String>> allMatchProbability = new ArrayList<Map<String, String>>();
		int i = 1;
		while (i++ <= numOfMatch) {
			String profitLossUrl = findProfitLossUrl(findOneMatchUrl(i));
			Map<String, String> map = findProbability(profitLossUrl);
			map.put("matchId", String.valueOf(i));
			allMatchProbability.add(map);
		}
		return allMatchProbability;
	}

	/**
	 * 获取交易盈亏页面url.(http://www.okooo.com/soccer/match/734052/exchanges/)
	 */
	public String findExchangeUrl(Document doc, int matchSeq) {
		String euroOddsUrl = findEuroOddsUrl(doc, matchSeq);
		return euroOddsUrl.replaceAll("odds", "exchanges");
	}
	
	/**
	 * 获取成交数据明细.(http://www.okooo.com/soccer/match/702468/exchanges/detail/)
	 */
	public String findExchangeDetailUrl(Document doc, int matchSeq) {
		String exchangesUrl = findExchangeUrl(doc, matchSeq);
		return StringUtils.isBlank(exchangesUrl) ? StringUtils.EMPTY : exchangesUrl + "detail/";
	}

}
