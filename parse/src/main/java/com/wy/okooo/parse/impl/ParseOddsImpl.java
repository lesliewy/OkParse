/**
 * 
 */
package com.wy.okooo.parse.impl;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wy.okooo.domain.AsiaOdds;
import com.wy.okooo.domain.AsiaOddsChange;
import com.wy.okooo.domain.AsiaOddsTrends;
import com.wy.okooo.domain.EuroOddsHandicap;
import com.wy.okooo.domain.EuroTransAsia;
import com.wy.okooo.domain.EuropeOdds;
import com.wy.okooo.domain.EuropeOddsChange;
import com.wy.okooo.domain.EuropeOddsChangeAll;
import com.wy.okooo.domain.IndexStats;
import com.wy.okooo.parse.ParseOdds;
import com.wy.okooo.util.JsoupUtils;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * 解析欧赔、亚盘页面(http://www.okooo.com/soccer/match/686923/odds/ or
 * http://www.okooo.com/soccer/match/686923/ah/)
 * 某个博彩公司欧赔变化、亚盘变化页面(http://www.okooo.com/soccer/match/686923/odds/change/24/ or
 * http://www.okooo.com/soccer/match/686923/ah/change/24/)
 * 
 * @author leslie
 * 
 */
public class ParseOddsImpl implements ParseOdds {

	// log4j
	private static Logger LOGGER = Logger.getLogger(ParseOddsImpl.class
			.getName());

	/**
	 * 解析欧赔页面,获取EuropeOdds对象. 只解析部分博彩公司.
	 */
	public List<EuropeOdds> getEuropeOdds(int matchSeq, int numOfSeq) {
		Document doc = JsoupUtils.getOddsDoc(matchSeq,
				OkConstant.DOC_TYPE_EURO_ODDS);
		return getEuropeOddsFromDoc(doc, numOfSeq, null, null);
	}
	
	/**
	 * 从本地文件解析欧赔页面,获取EuropeOdds对象. 只解析部分博彩公司.
	 */
	public List<EuropeOdds> getEuropeOddsFromFile(File euroOddsHtml, int numOfSeq, String okUrlDate, Integer matchSeq) {
		Document euroOddsDoc = Jsoup.parse(OkParseUtils.getFileContent(euroOddsHtml));
		return getEuropeOddsFromDoc(euroOddsDoc, numOfSeq, okUrlDate, matchSeq);
	}
	
	private List<EuropeOdds> getEuropeOddsFromDoc(Document euroOddsDoc, int numOfSeq, String okUrlDate, Integer matchSeq){
		if (euroOddsDoc == null) {
			LOGGER.error("doc is null, return now.");
			return null;
		}
		
		// #qnav > div:nth-child(1) > a:nth-child(2)   通过 "欧赔" 获取 okMatchId, 考虑异常情况: 页面不完整.
		String euroHrefStr = euroOddsDoc.select("#qnav > div:nth-child(1) > a:nth-child(2)").attr("href");
		if(StringUtils.isBlank(euroHrefStr)){
			return null;
		}

		List<EuropeOdds> europeOdds = new ArrayList<EuropeOdds>();
		// 开始时只获取指定的几家公司的数据.
//		for (int trNum : OkConstant.ODDS_CORP_TR_EURO) {
		// 获取所有的公司的数据.
		int trNum = 0;
		while(trNum++ <= 1000){
			// #tr14 > td.bright.borderLeft > span
			Elements elements = euroOddsDoc.select("#tr" + trNum);
			if (elements == null || elements.isEmpty()) {
				continue;
			}
			
			if(numOfSeq != 0 && europeOdds.size() > numOfSeq){
				break;
			}
			
			// 博彩公司编号, LOT_ODDS_EURO 中没有 corp_no, 此处只做解析使用.
			String oddsCorpNo = String.valueOf(trNum);
			
			String oddsCorpName = elements.select(
					" > td.bright.borderLeft > span").text();
			// #tr14 > td.borderLeft.feedbackObj.csObj > span
			String initHostOddsStr = elements.select(
					" > td.borderLeft.feedbackObj.csObj > span").text();

			// #tr14 > td:nth-child(4) > span
			String initEvenOddsStr = elements.select(
					" > td:nth-child(4) > span").text();

			// #tr14 > td.feedbackObj.bright.csObj > span
			String initVisitingOddsStr = elements.select(
					" > td.feedbackObj.bright.csObj > span").text();

			// #tr14 > td.borderLeft.feedbackObj.csObj
			String initTimeTitle = elements.select(
					" > td.borderLeft.feedbackObj.csObj").attr("title");
			String hourStr = initTimeTitle.substring(
					initTimeTitle.lastIndexOf("前") + 1,
					initTimeTitle.lastIndexOf("时"));
			String minStr = initTimeTitle.substring(
					initTimeTitle.lastIndexOf("时") + 1,
					initTimeTitle.lastIndexOf("分"));
			String initTime = hourStr + "." + minStr;

			// #tr14 > td.borderLeft.trbghui.feedbackObj > a > span
			String hostOddsStr = elements.select(
					" > td.borderLeft.trbghui.feedbackObj > a > span").text();

			// #tr14 > td:nth-child(7) > a > span
			String evenOddsStr = elements.select(
					" > td:nth-child(7) > a > span").text();

			// #tr14 > td:nth-child(8) > div > a > span
			String visitingOddsStr = elements.select(
					" > td:nth-child(8) > div > a > span").text();
			
			// #tr24 > td:nth-child(13) > span
			String hostKellyStr = elements.select(" > td:nth-child(13) > span").text();
			String evenKellyStr = elements.select(" > td:nth-child(14) > span").text();
			String visitingKellyStr = elements.select(" > td:nth-child(15) > span").text();

			// #tr14 > td.borderRight.borderLeft.feedbackObj > span
			String lossRatioStr = elements.select(
					" > td.borderRight.borderLeft.feedbackObj > span").text();
			Timestamp timestamp = new Timestamp(Calendar.getInstance()
					.getTimeInMillis());
			LOGGER.debug("oddsCorpName: oddsCorpName" + oddsCorpName
					+ "; initHostOddsStr: " + initHostOddsStr
					+ "; initEvenOddsStr: " + initEvenOddsStr
					+ "; initVisitingOddsStr: " + initVisitingOddsStr
					+ "; initTime: " + initTime + "; hostOddsStr: "
					+ hostOddsStr + "; evenOddsStr: " + evenOddsStr
					+ "; visitingOddsStr: " + visitingOddsStr
					+ "; lossRatioStr: " + lossRatioStr);
			EuropeOdds odd = new EuropeOdds();
			odd.setOkUrlDate(okUrlDate);
			odd.setMatchSeq(matchSeq);
			odd.setOddsCorpNo(oddsCorpNo);
			odd.setOddsCorpName(oddsCorpName);
			odd.setHostOdds(Float.valueOf(hostOddsStr));
			odd.setEvenOdds(Float.valueOf(evenOddsStr));
			odd.setVisitingOdds(Float.valueOf(visitingOddsStr));
			odd.setInitTime(initTime);
			odd.setInitHostOdds(Float.valueOf(initHostOddsStr));
			odd.setInitEvenOdds(Float.valueOf(initEvenOddsStr));
			odd.setInitVisitingOdds(Float.valueOf(initVisitingOddsStr));
			odd.setHostKelly(Float.valueOf(hostKellyStr));
			odd.setEvenKelly(Float.valueOf(evenKellyStr));
			odd.setVisitingKelly(Float.valueOf(visitingKellyStr));
			odd.setLossRatio(Float.valueOf(lossRatioStr));
			odd.setTimestamp(timestamp);
			europeOdds.add(odd);
		}

		return europeOdds;
	}
	
	/**
	 * 解析亚盘页面,获取AsiaOdds对象. 只解析部分博彩公司.
	 */
	public List<AsiaOdds> getAsiaOdds(int matchSeq) {
		Document asiaDoc = JsoupUtils.getOddsDoc(matchSeq,
				OkConstant.DOC_TYPE_ASIA_ODDS);
		return getAsiaOddsFromDoc(asiaDoc, matchSeq);
	}
	
	/**
	 * 从本地文件解析亚盘页面,获取AsiaOdds对象. 只解析部分博彩公司.
	 */
	public List<AsiaOdds> getAsiaOddsFromFile(File asiaOddsHtml, Integer matchSeq) {
		Document asiaOddsDoc = Jsoup.parse(OkParseUtils.getFileContent(asiaOddsHtml));
		return getAsiaOddsFromDoc(asiaOddsDoc, matchSeq);
	}
	
	/**
	 * 从本地文件解析亚盘页面,获取AsiaOddsTrends对象
	 */
	public List<AsiaOddsTrends> getAsiaOddsTrendsFromFile(File asiaOddsHtml, AsiaOddsTrends asiaOddsTrendsInit) {
		Document asiaOddsTrendsDoc = Jsoup.parse(OkParseUtils.getFileContent(asiaOddsHtml));
		return getAsiaOddsTrendsFromDoc(asiaOddsTrendsDoc, asiaOddsTrendsInit);
	}
	
	private List<AsiaOdds> getAsiaOddsFromDoc(Document asiaOddsDoc, Integer matchSeq){
		if (asiaOddsDoc == null) {
			LOGGER.error("asiaDoc is null, return now.");
			return null;
		}

		// #qnav > div:nth-child(1) > a:nth-child(3) 通过 "亚盘" 获取 okMatchId, 考虑异常情况: 页面不完整.
		String asiaHrefStr = asiaOddsDoc.select("#qnav > div:nth-child(1) > a:nth-child(3)").attr("href");
		if(StringUtils.isBlank(asiaHrefStr)){
			return null;
		}
		long okMatchId = Long.valueOf(StringUtils.split(asiaHrefStr, "/")[2]);
		
		List<AsiaOdds> asiaOdds = new ArrayList<AsiaOdds>();
		int trNum = 0;
		while(trNum++ <= 1000){
			// #tr27
			Elements elements = asiaOddsDoc.select("#tr" + trNum);
			if (elements == null || elements.isEmpty()) {
				continue;
			}

			// #tr27 > td.borderLeft.bright > span
			String oddsCorpName = elements.select(
					" > td.borderLeft.bright > span").text();

			// #tr27 > td:nth-child(4) > a > span
			Float initHandicap = OkParseUtils.translateHandicap(elements
					.select(" > td:nth-child(4) > a > span").text());
			if(initHandicap == null){
				continue;
			}

			// #tr27 > td:nth-child(3) > a > span
			String initHostOddsStr = elements.select(
					" > td:nth-child(3) > a > span").text();

			// #tr27 > td.bright.feedbackObj > a > span
			String initVisitingOddsStr = elements.select(
					" > td.bright.feedbackObj > a > span").text();

			// #tr27 > td:nth-child(3)
			String initTimeTitle = elements.select(" > td:nth-child(3)").attr(
					"title");
			String hourStr = initTimeTitle.substring(
					initTimeTitle.lastIndexOf("前") + 1,
					initTimeTitle.lastIndexOf("时"));
			String minStr = initTimeTitle.substring(
					initTimeTitle.lastIndexOf("时") + 1,
					initTimeTitle.lastIndexOf("分"));
			String initTime = hourStr + "." + minStr;

			// #tr27 > td:nth-child(7) > a > span
			Float handicap = OkParseUtils.translateHandicap(elements.select(
					" > td:nth-child(7) > a > span").text());
			if(handicap == null){
				continue;
			}

			// #tr27 > td.borderLeft.trbghui.feedbackObj > a > span
			String hostOddsStr = elements.select(
					" > td.borderLeft.trbghui.feedbackObj > a > span").text();

			// #tr27 > td:nth-child(8) > a > span
			String visitingOddsStr = elements.select(
					" > td:nth-child(8) > a > span").text();

			// #tr27 > td:nth-child(11) > span
			String hostKellyStr = elements.select(
					" > td:nth-child(11) > span").text();
			Float hostKelly = null;
			if(!StringUtils.isBlank(hostKellyStr)){
				hostKelly = Float.valueOf(hostKellyStr);
			}
			
			// #tr27 > td:nth-child(12) > span
			String visitingKellyStr = elements.select(
					" > td:nth-child(12) > span").text();
			Float visitingKelly = null;
			if(!StringUtils.isBlank(visitingKellyStr)){
				visitingKelly = Float.valueOf(visitingKellyStr);
			}
			
			// #tr27 > td.borderLeft.borderRight.feedbackObj > span
			String lossRatioStr = elements.select(
					" > td.borderLeft.borderRight.feedbackObj > span").text();

			Timestamp timestamp = new Timestamp(Calendar.getInstance()
					.getTimeInMillis());
			LOGGER.debug("oddsCorpName: oddsCorpName" + oddsCorpName
					+ "; initHandicap: " + initHandicap + "; initHostOddsStr: "
					+ initHostOddsStr + "; initVisitingOddsStr: "
					+ initVisitingOddsStr + "; initTime: " + initTime
					+ "; handicap: " + handicap + "; hostOddsStr: "
					+ hostOddsStr + "; visitingOddsStr: " + visitingOddsStr
					+ "; lossRatioStr: " + lossRatioStr);
			AsiaOdds odd = new AsiaOdds();
			odd.setOkMatchId(okMatchId);
			odd.setMatchSeq(matchSeq);
			odd.setHandicap(handicap);
			odd.setOddsCorpName(oddsCorpName);
			odd.setHostOdds(Float.valueOf(hostOddsStr));
			odd.setVisitingOdds(Float.valueOf(visitingOddsStr));
			odd.setInitTime(initTime);
			odd.setInitHandicap(initHandicap);
			odd.setInitHostOdds(Float.valueOf(initHostOddsStr));
			odd.setInitVisitingOdds(Float.valueOf(initVisitingOddsStr));
			odd.setLossRatio(Float.valueOf(lossRatioStr));
			odd.setTimestamp(timestamp);
			odd.setHostKelly(hostKelly);
			odd.setVisitingKelly(visitingKelly);
			asiaOdds.add(odd);
		}

		return asiaOdds;
	}
	
	/**
	 * 从本地文件解析让球页面,获取EuroOddsHandicap对象
	 */
	public List<EuroOddsHandicap> getEuroOddsHandicapFromFile(File euroOddsHandicapHtml, EuroOddsHandicap euroOddsHandicapInit) {
		Document euroOddsHandicapDoc = Jsoup.parse(OkParseUtils.getFileContent(euroOddsHandicapHtml));
		return getEuroOddsHandicapFromDoc(euroOddsHandicapDoc, euroOddsHandicapInit);
	}
	
	private List<EuroOddsHandicap> getEuroOddsHandicapFromDoc(Document euroOddsHandicapDoc, EuroOddsHandicap euroOddsHandicapInit){
		if (euroOddsHandicapDoc == null) {
			LOGGER.error("euroOddsHandicapDoc is null, return now.");
			return null;
		}

		List<EuroOddsHandicap> euroOddsHandicaps = new ArrayList<EuroOddsHandicap>();
		
		// 竞彩数值 #filterType > div > a   获取val属性.
		String compHandicapStr = "";
		Elements a = euroOddsHandicapDoc.select("#filterType > div > a:nth-child(2)");
		if(a != null){
			compHandicapStr = a.attr("val");
		}
		Integer compHandicap = null;
		if(!StringUtils.isBlank(compHandicapStr)){
			compHandicap = Integer.valueOf(compHandicapStr);
		}
		
		// #datatable1 > table > tbody > tr
		Elements elementsTrs = euroOddsHandicapDoc.select("#datatable1 > table > tbody > tr");
		if(elementsTrs == null){
			return null;
		}
		
		Iterator<Element> iter = elementsTrs.iterator();
		while(iter.hasNext()){
			Element element = iter.next();
			
			// oddsCorpName: #tr2 > td:nth-child(2) > span
			String oddsCorpName = element.select("> td:nth-child(2) > span").text();
			if("".equalsIgnoreCase(oddsCorpName) || "平均值".equalsIgnoreCase(oddsCorpName) || "最大值".equalsIgnoreCase(oddsCorpName)
					|| "最小值".equalsIgnoreCase(oddsCorpName)){
				continue;
			}
			
			// 让球值: #tr2 > td.borderLeft.bright > span  
			// 记录让球值和竞彩值相等的数据;  如果竞彩值不存在，记录让球值为1, -1两种情况.
			String euroHandicapStr = element.select("> td:nth-child(3) > span").text();
			Integer euroHandicap = null;
			if(!StringUtils.isBlank(euroHandicapStr)){
				euroHandicap = Integer.valueOf(euroHandicapStr);
			}
			if(compHandicap != null && euroHandicap != null && compHandicap.intValue() != euroHandicap.intValue()){
				continue;
			}
			if(compHandicap == null && euroHandicap != null && euroHandicap != 1 && euroHandicap != -1){
				continue;
			}
			
			// 初始指数
			// #tr2 > td:nth-child(4) > a > span
			String initHostOddsStr = element.select(" > td:nth-child(4) > a > span").text();
			String initEvenOddsStr = element.select(" > td:nth-child(5) > a > span").text();
			String initVisitingOddsStr = element.select(" > td:nth-child(6) > a > span").text();
			Float initHostOdds = 0f;
			if(!StringUtils.isBlank(initHostOddsStr)){
				initHostOdds = Float.valueOf(initHostOddsStr);
			}
			Float initEvenOdds = 0f;
			if(!StringUtils.isBlank(initEvenOddsStr)){
				initEvenOdds = Float.valueOf(initEvenOddsStr);
			}
			Float initVisitingOdds = 0f;
			if(!StringUtils.isBlank(initVisitingOddsStr)){
				initVisitingOdds = Float.valueOf(initVisitingOddsStr);
			}
			
			// 最新指数
			// #tr2 > td:nth-child(7) > a > span
			String hostOddsStr = element.select(" > td:nth-child(7) > a > span").text();
			String evenOddsStr = element.select(" > td:nth-child(8) > a > span").text();
			String visitingOddsStr = element.select(" > td:nth-child(9) > a > span").text();
			Float hostOdds = 0f;
			if(!StringUtils.isBlank(hostOddsStr)){
				hostOdds = Float.valueOf(hostOddsStr);
			}
			Float evenOdds = 0f;
			if(!StringUtils.isBlank(evenOddsStr)){
				evenOdds = Float.valueOf(evenOddsStr);
			}
			Float visitingOdds = 0f;
			if(!StringUtils.isBlank(visitingOddsStr)){
				visitingOdds = Float.valueOf(visitingOddsStr);
			}
			
			// 最新凯利指数
			// #tr2 > td:nth-child(14) > span
			String hostKellyStr = element.select(" > td:nth-child(14) > span").text();
			String evenKellyStr = element.select(" > td:nth-child(15) > span").text();
			String visitingKellyStr = element.select(" > td:nth-child(16) > span").text();
			Float hostKelly = 0f;
			if(!StringUtils.isBlank(hostKellyStr)){
				hostKelly = Float.valueOf(hostKellyStr);
			}
			Float evenKelly = 0f;
			if(!StringUtils.isBlank(evenKellyStr)){
				evenKelly = Float.valueOf(evenKellyStr);
			}
			Float visitingKelly = 0f;
			if(!StringUtils.isBlank(visitingKellyStr)){
				visitingKelly = Float.valueOf(visitingKellyStr);
			}
			
			// 赔付率 #tr2 > td:nth-child(17) > span
			String lossRatioStr = element.select(" > td:nth-child(17) > span").text();
			Float lossRatio = 0f;
			if(!StringUtils.isBlank(lossRatioStr)){
				lossRatio = Float.valueOf(lossRatioStr);
			}
			
			Timestamp timestamp = new Timestamp(Calendar.getInstance()
					.getTimeInMillis());
			
			EuroOddsHandicap odd = new EuroOddsHandicap();
			odd.setOkUrlDate(euroOddsHandicapInit.getOkUrlDate());
			odd.setMatchSeq(euroOddsHandicapInit.getMatchSeq());
			odd.setJobType(euroOddsHandicapInit.getJobType());
			odd.setOddsCorpName(oddsCorpName);
			odd.setEuroHandicap(euroHandicap);
			odd.setCompHandicap(compHandicap);
			odd.setInitHostOdds(initHostOdds);
			odd.setInitEvenOdds(initEvenOdds);
			odd.setInitVisitingOdds(initVisitingOdds);
			odd.setHostOdds(hostOdds);
			odd.setEvenOdds(evenOdds);
			odd.setVisitingOdds(visitingOdds);
			odd.setHostKelly(hostKelly);
			odd.setEvenKelly(evenKelly);
			odd.setVisitingKelly(visitingKelly);
			odd.setLossRatio(lossRatio);
			odd.setTimestamp(timestamp);
			euroOddsHandicaps.add(odd);
		}

		return euroOddsHandicaps;
	}
	
	private List<AsiaOddsTrends> getAsiaOddsTrendsFromDoc(Document asiaOddsTrendsDoc, AsiaOddsTrends asiaOddsTrendsInit){
		if (asiaOddsTrendsDoc == null) {
			LOGGER.error("asiaOddsTrendsDoc is null, return now.");
			return null;
		}

		// #qnav > div:nth-child(1) > a:nth-child(3)  考虑异常情况: 页面不完整.
		String asiaHrefStr = asiaOddsTrendsDoc.select("#qnav > div:nth-child(1) > a:nth-child(3)").attr("href");
		if(StringUtils.isBlank(asiaHrefStr)){
			return null;
		}
		
		// #matchNum 超过指定公司数才处理.
		String matchNumStr = asiaOddsTrendsDoc.select("#matchNum").text();
		if(!StringUtils.isBlank(matchNumStr)){
			Integer matchNum = Integer.valueOf(matchNumStr);
			if(matchNum <= 30){
				LOGGER.info("asia odds corps num: " + matchNum + ",  skip...");
				return null;
			}
		}
		
		List<AsiaOddsTrends> asiaOddsTrends = new ArrayList<AsiaOddsTrends>();
		// #datatable1 > table > tbody
		Elements elementsTbody = asiaOddsTrendsDoc.select("#datatable1 > table > tbody > tr");
		if(elementsTbody == null){
			return null;
		}
		
		// matchName: #lunci > div.qk_two > p > span > a:nth-child(1)
		//            #lunci > div:nth-child(1) > p:nth-child(1) > span > a:nth-child(1)
		// 发现欧洲联赛, 解放者杯, 欧冠是后者, 可能非联赛都是这样的; 比甲也是后者.
		String matchName = asiaOddsTrendsDoc.select("#lunci > div.qk_two > p > span > a:nth-child(1)").text();
		if(StringUtils.isBlank(matchName)){
			matchName = asiaOddsTrendsDoc.select("#lunci > div:nth-child(1) > p:nth-child(1) > span > a:nth-child(1)").text();
		}
		
		Iterator<Element> iter = elementsTbody.iterator();
		while(iter.hasNext()){
			Element element = iter.next();
			// > td.borderLeft.bright > span
			String oddsCorpName = element.select(
					" > td.borderLeft.bright > span").text();
			// 解析平均值, 最大值, 最小值.
			if(StringUtils.isBlank(oddsCorpName)){
				// 平均值: #avgObj > td.borderLeft.bright
				// 最大值: #maxObj > td.borderLeft.bright
				// 最小值: #minObj > td.borderLeft.bright > div
				oddsCorpName = element.select("#avgObj > td.borderLeft.bright").text()
						+ element.select("#maxObj > td.borderLeft.bright").text()
						+ element.select("#minObj > td.borderLeft.bright > div").text();
			}
			
			// #tr27 > td:nth-child(4) > a > span
			Float initHandicap = null;
			String initHandicapStr = element
					.select(" > td:nth-child(4) > a > span").text();
			
			String initTime = "";

			// #tr27 > td:nth-child(3) > a > span
			String initHostOddsStr = element.select(
					" > td:nth-child(3) > a > span").text();

			// #tr27 > td.bright.feedbackObj > a > span
			String initVisitingOddsStr = element.select(
					" > td.bright.feedbackObj > a > span").text();

			// #tr27 > td:nth-child(7) > a > span
			Float handicap = null;
			String handicapChi = element.select(
					" > td:nth-child(7) > a > span").text();
			
			// #tr27 > td.borderLeft.trbghui.feedbackObj > a > span
			String hostOddsStr = element.select(
					" > td.borderLeft.trbghui.feedbackObj > a > span").text();

			// #tr27 > td:nth-child(8) > a > span
			String visitingOddsStr = element.select(
					" > td:nth-child(8) > a > span").text();

			// #tr27 > td.borderLeft.borderRight.feedbackObj > span
			String lossRatioStr = element.select(
					" > td.borderLeft.borderRight.feedbackObj > span").text();

			// #tr27 > td:nth-child(11) > span
			String hostKellyStr = element.select(" > td:nth-child(11) > span").text();
			
			// #tr27 > td:nth-child(12) > span
			String visitingKellyStr = element.select(" > td:nth-child(12) > span").text();
			
			Timestamp timestamp = new Timestamp(Calendar.getInstance()
					.getTimeInMillis());
			
			// 平均值，最大值，最小值获取方式不一样;
			if("平均值".equals(oddsCorpName) || "最大值".equals(oddsCorpName) || "最小值".equals(oddsCorpName)){
				initHandicapStr = element.select("#avgObj > td:nth-child(4)").text()
						+ element.select("#maxObj > td:nth-child(4)").text()
						+ element.select("#minObj > td:nth-child(4)").text();
				
				// #avgObj > td:nth-child(3)  #maxObj > td:nth-child(3)  #minObj > td:nth-child(3)
				initHostOddsStr = element.select("#avgObj > td:nth-child(3)").text()
						+ element.select("#maxObj > td:nth-child(3)").text()
						+ element.select("#minObj > td:nth-child(3)").text();
				
				// #avgObj > td:nth-child(5) #maxObj > td:nth-child(5) #minObj > td:nth-child(5)
				initVisitingOddsStr = element.select("#avgObj > td:nth-child(5)").text()
						+ element.select("#maxObj > td:nth-child(5)").text()
						+ element.select("#minObj > td:nth-child(5)").text();
				
				// #avgObj > td:nth-child(7) #maxObj > td:nth-child(7) #minObj > td:nth-child(7)
				handicapChi = element.select("#avgObj > td:nth-child(7)").text()
						+ element.select("#maxObj > td:nth-child(7)").text()
						+ element.select("#minObj > td:nth-child(7)").text();
				
				// #avgObj > td.borderLeft.trbghui #maxObj > td.borderLeft.trbghui #minObj > td.borderLeft.trbghui
				hostOddsStr = element.select("#avgObj > td.borderLeft.trbghui").text()
						+ element.select("#maxObj > td.borderLeft.trbghui").text()
						+ element.select("#minObj > td.borderLeft.trbghui").text();
				
				// #avgObj > td:nth-child(8) #maxObj > td:nth-child(8) #minObj > td:nth-child(8)
				visitingOddsStr = element.select("#avgObj > td:nth-child(8)").text()
						+ element.select("#maxObj > td:nth-child(8)").text()
						+ element.select("#minObj > td:nth-child(8)").text();
				
				// #avgObj > td.borderLeft.borderRight #maxObj > td:nth-child(13) #minObj > td.borderLeft.borderRight
				lossRatioStr = element.select("#avgObj > td.borderLeft.borderRight").text()
						+ element.select("#maxObj > td:nth-child(13)").text()
						+ element.select("#minObj > td.borderLeft.borderRight").text();
				
				// #avgObj > td:nth-child(11) #maxObj > td:nth-child(11) #minObj > td:nth-child(11)
				hostKellyStr = element.select("#avgObj > td:nth-child(11)").text()
						+ element.select("#maxObj > td:nth-child(11)").text()
						+ element.select("#minObj > td:nth-child(11)").text();
				
				// #avgObj > td:nth-child(12) #maxObj > td:nth-child(12) #minObj > td:nth-child(12)
				visitingKellyStr = element.select("#avgObj > td:nth-child(12)").text()
						+ element.select("#maxObj > td:nth-child(12)").text()
						+ element.select("#minObj > td:nth-child(12)").text();
				
			}else{
				// #tr27 > td:nth-child(3)
				String initTimeTitle = element.select(" > td:nth-child(3)").attr(
						"title");
				String hourStr = initTimeTitle.substring(
						initTimeTitle.lastIndexOf("前") + 1,
						initTimeTitle.lastIndexOf("时"));
				String minStr = initTimeTitle.substring(
						initTimeTitle.lastIndexOf("时") + 1,
						initTimeTitle.lastIndexOf("分"));
				initTime = hourStr + "." + minStr;
			}
			
			// 如果是平均值的盘口，不需要转义, 这里okooo让球是负数.
			if(!StringUtils.isBlank(initHandicapStr) && "平均值".equals(oddsCorpName)){
				initHandicap = Float.valueOf(initHandicapStr);
			}else if(!StringUtils.isBlank(initHandicapStr)){
				initHandicap = OkParseUtils.translateHandicap(initHandicapStr);
			}
			if(initHandicap == null){
				continue;
			}
			
			if(!StringUtils.isBlank(handicapChi) && "平均值".equals(oddsCorpName)){
				handicap = Float.valueOf(handicapChi);
			}else if(!StringUtils.isBlank(handicapChi)){
				handicap = OkParseUtils.translateHandicap(handicapChi);
			}
			if(handicap == null){
				continue;
			}
			
			AsiaOddsTrends odd = new AsiaOddsTrends();
			odd.setOkUrlDate(asiaOddsTrendsInit.getOkUrlDate());
			odd.setMatchSeq(asiaOddsTrendsInit.getMatchSeq());
			odd.setOddsCorpName(oddsCorpName);
			odd.setJobType(asiaOddsTrendsInit.getJobType());
			odd.setMatchName(matchName);
			odd.setInitTime(initTime);
			odd.setInitHandicap(initHandicap);
			odd.setInitHostOdds(StringUtils.isBlank(initHostOddsStr) ? 0 : Float.valueOf(initHostOddsStr));
			odd.setInitVisitingOdds(StringUtils.isBlank(initVisitingOddsStr) ? 0 : Float.valueOf(initVisitingOddsStr));
			odd.setHostOdds(StringUtils.isBlank(hostOddsStr) ? 0 : Float.valueOf(hostOddsStr));
			odd.setHandicap(handicap);
			odd.setVisitingOdds(StringUtils.isBlank(visitingOddsStr) ? 0 : Float.valueOf(visitingOddsStr));
			odd.setHostKelly(StringUtils.isBlank(hostKellyStr) ? 0 : Float.valueOf(hostKellyStr));
			odd.setVisitingKelly(StringUtils.isBlank(visitingKellyStr) ? 0 : Float.valueOf(visitingKellyStr));
			odd.setLossRatio(StringUtils.isBlank(lossRatioStr) ? 0 : Float.valueOf(lossRatioStr));
			odd.setTimestamp(timestamp);
			asiaOddsTrends.add(odd);
		}

		return asiaOddsTrends;
	}

	/**
	 * 从本地文件解析okooo指数页面,获取IndexStats对象
	 */
	public IndexStats getIndexStatsFromFile(File indexStatsHtml, IndexStats indexStatsInit) {
		Document indexStatsDoc = Jsoup.parse(OkParseUtils.getFileContent(indexStatsHtml));
		return getIndexStatsFromDoc(indexStatsDoc, indexStatsInit);
	}
	
	private IndexStats getIndexStatsFromDoc(Document indexStatsDoc, IndexStats indexStatsInit){
		if (indexStatsDoc == null) {
			LOGGER.error("indexStatsDoc is null, return now.");
			return null;
		}

		String okUrlDate = indexStatsInit.getOkUrlDate();
		Integer matchSeq = indexStatsInit.getMatchSeq();
		String jobType = indexStatsInit.getJobType();
		
		// okooo 指数: 初始主胜 #okstarthome
		String initOkoooHostStr = indexStatsDoc.select("#okstarthome").text();
		Float initOkoooHost = -1f;
		if(!StringUtils.isBlank(initOkoooHostStr)){
			initOkoooHost = Float.valueOf(initOkoooHostStr);
		}
		// okooo 指数: 初始平局 #okstartdraw
		String initOkoooEvenStr = indexStatsDoc.select("#okstartdraw").text();
		Float initOkoooEven = -1f;
		if(!StringUtils.isBlank(initOkoooEvenStr)){
			initOkoooEven = Float.valueOf(initOkoooEvenStr);
		}
		// okooo 指数: 初始客胜 #okstartaway
		String initOkoooVisitingStr = indexStatsDoc.select("#okstartaway").text();
		Float initOkoooVisiting = -1f;
		if(!StringUtils.isBlank(initOkoooVisitingStr)){
			initOkoooVisiting = Float.valueOf(initOkoooVisitingStr);
		}
		
		// okooo 指数: 最新主胜 #okendhome
		String okoooHostStr = indexStatsDoc.select("#okendhome").text();
		Float okoooHost = -1f;
		if(!StringUtils.isBlank(okoooHostStr)){
			okoooHost = Float.valueOf(okoooHostStr);
		}
		// okooo 指数: 最新平局 #okenddraw
		String okoooEvenStr = indexStatsDoc.select("#okenddraw").text();
		Float okoooEven = -1f;
		if(!StringUtils.isBlank(okoooEvenStr)){
			okoooEven = Float.valueOf(okoooEvenStr);
		}
		// okooo 指数: 最新客胜 #okendaway
		String okoooVisitingStr = indexStatsDoc.select("#okendaway").text();
		Float okoooVisiting = -1f;
		if(!StringUtils.isBlank(okoooVisitingStr)){
			okoooVisiting = Float.valueOf(okoooVisitingStr);
		}
		
		// 离散度 初始主胜 #kellystarthome
		String initStdDevHostStr = indexStatsDoc.select("#kellystarthome").text();
		Float initStdDevHost = -1f;
		if(!StringUtils.isBlank(initStdDevHostStr)){
			initStdDevHost = Float.valueOf(initStdDevHostStr);
		}
		// 离散度 初始平局 #kellystartdraw
		String initStdDevEvenStr = indexStatsDoc.select("#kellystartdraw").text();
		Float initStdDevEven = -1f;
		if(!StringUtils.isBlank(initStdDevEvenStr)){
			initStdDevEven = Float.valueOf(initStdDevEvenStr);
		}
		// 离散度 初始客胜 #kellystartaway
		String initStdDevVisitingStr = indexStatsDoc.select("#kellystartaway").text();
		Float initStdDevVisiting = -1f;
		if(!StringUtils.isBlank(initStdDevVisitingStr)){
			initStdDevVisiting = Float.valueOf(initStdDevVisitingStr);
		}
		
		// 离散度 最新主胜 #kellyendhome
		String stdDevHostStr = indexStatsDoc.select("#kellyendhome").text();
		Float stdDevHost = -1f;
		if(!StringUtils.isBlank(stdDevHostStr)){
			stdDevHost = Float.valueOf(stdDevHostStr);
		}
		
		// 离散度 最新平局 #kellyenddraw
		String stdDevEvenStr = indexStatsDoc.select("#kellyenddraw").text();
		Float stdDevEven = -1f;
		if(!StringUtils.isBlank(stdDevEvenStr)){
			stdDevEven = Float.valueOf(stdDevEvenStr);
		}
		
		// 离散度 最新客胜 #kellyendaway
		String stdDevVisitingStr = indexStatsDoc.select("#kellyendaway").text();
		Float stdDevVisiting = -1f;
		if(!StringUtils.isBlank(stdDevVisitingStr)){
			stdDevVisiting = Float.valueOf(stdDevVisitingStr);
		}
		
		Timestamp timestamp = new Timestamp(Calendar.getInstance()
				.getTimeInMillis());
		
		IndexStats indexStats = new IndexStats();
		indexStats.setOkUrlDate(okUrlDate);
		indexStats.setMatchSeq(matchSeq);
		indexStats.setJobType(jobType);
		indexStats.setInitOkoooHost(initOkoooHost);
		indexStats.setInitOkoooEven(initOkoooEven);
		indexStats.setInitOkoooVisiting(initOkoooVisiting);
		indexStats.setOkoooHost(okoooHost);
		indexStats.setOkoooEven(okoooEven);
		indexStats.setOkoooVisiting(okoooVisiting);
		indexStats.setInitStdDevHost(initStdDevHost);
		indexStats.setInitStdDevEven(initStdDevEven);
		indexStats.setInitStdDevVisiting(initStdDevVisiting);
		indexStats.setStdDevHost(stdDevHost);
		indexStats.setStdDevEven(stdDevEven);
		indexStats.setStdDevVisiting(stdDevVisiting);
		indexStats.setTimestamp(timestamp);
		return indexStats;
	}
	
	/**
	 * 解析欧赔变化页面.(http://www.okooo.com/soccer/match/686854/odds/change/14/)
	 * 只获取最近的部分.
	 */
	public List<EuropeOddsChange> getEuropeOddsChange(long matchId,
			int matchSeq, int corpNo, int numOfSeq, boolean addInitOdds) {
		Document europeOddsChangeDoc = JsoupUtils.getOddsChangeDoc(matchSeq,
				OkConstant.DOC_TYPE_EURO_ODDS_CHANGE, corpNo);
		return getEuropeOddsChangeFromDoc(europeOddsChangeDoc, corpNo, numOfSeq, addInitOdds);
	}
	
	/**
	 * 从本地文件解析欧赔变化页面.(http://www.okooo.com/soccer/match/686854/odds/change/14/)
	 * 只获取最近的部分.
	 */
	public List<EuropeOddsChange> getEuropeOddsChangeFromFile(
			File euroOddsChangeHtml, int numOfSeq, boolean addInitOdds) {
		Document euroOddsChangeDoc = Jsoup.parse(OkParseUtils.getFileContent(euroOddsChangeHtml));
		int corpNo = OkParseUtils.getCorpNoFromOddsChangeFile(euroOddsChangeHtml);
		return getEuropeOddsChangeFromDoc(euroOddsChangeDoc, corpNo, numOfSeq, addInitOdds);
	}
	
	public List<EuropeOddsChangeAll> getEuropeOddsChangeAllFromFile(
			File euroOddsChangeHtml, int numOfSeq, String okUrlDate, Integer matchSeq) {
		Document euroOddsChangeDoc = Jsoup.parse(OkParseUtils.getFileContent(euroOddsChangeHtml));
		return getEuropeOddsChangeAllFromDoc(euroOddsChangeDoc, numOfSeq, okUrlDate, matchSeq);
	}
	
	public List<EuropeOddsChange> getEuropeOddsChangeDailyFromFile(File euroOddsChangeHtml, int numOfSeq, 
			boolean addInitOdds, String okUrlDate, Integer matchSeq) {
		Document euroOddsChangeDoc = Jsoup.parse(OkParseUtils.getFileContent(euroOddsChangeHtml));
		return getEuropeOddsChangeDailyFromDoc(euroOddsChangeDoc, numOfSeq, addInitOdds, okUrlDate, matchSeq);
	}
	
	private List<EuropeOddsChange> getEuropeOddsChangeDailyFromDoc(Document europeOddsChangeDoc, int numOfSeq, boolean addInitOdds,
			String okUrlDate, Integer matchSeq){
		if (europeOddsChangeDoc == null) {
			LOGGER.error("doc is null, return now.");
			return null;
		}
		
		// body > div.wrap > div.lqnav > div > a:nth-child(5) 从面包屑中获取 okMatchId
		String euroChangeHrefStr = europeOddsChangeDoc.select("body > div.wrap > div.lqnav > div > a:nth-child(5)").attr("href");
		// 考虑异常情况: 页面不完整.
		if(StringUtils.isBlank(euroChangeHrefStr)){
			return null;
		}
		String[] euroChangeHrefArr = StringUtils.split(euroChangeHrefStr, "/");
		// 考虑异常情况: 有页面，但是数据不存在.
		if(euroChangeHrefArr.length < 3 || !StringUtils.isNumeric(euroChangeHrefArr[2])){
			return null;
		}

		List<EuropeOddsChange> odds = new ArrayList<EuropeOddsChange>();

		// 设置最大值.
		int max = 200;
		int lineNo = 2;
		int oddsSeq = 0;
		int changeNum = 0;
		while (lineNo++ < max) {
			// body > div.wrap > table > tbody > tr:nth-child(3) body > div.wrap
			// > table > tbody > tr:nth-child(5) body > div.wrap > table > tbody
			// > tr:nth-child(6)
			// 跳过 4.
			if (lineNo == 4) {
				continue;
			}
			
			Elements elementsTR = europeOddsChangeDoc
					.select("body > div.wrap > table > tbody > tr:nth-child("
							+ lineNo + ")");

			// 获取完毕 或者 达到上限则退出
			if (elementsTR == null || elementsTR.isEmpty()) {
				break;
			}

			// 从面包屑中获取:  body > div.wrap > div.lqnav > div > b
			String oddsCorpName = europeOddsChangeDoc.select("body > div.wrap > div.lqnav > div > b").text();

			// 考虑该不存在的情况
			String oddsTimeTemp =  elementsTR.select(" > td.noborder.bright").text();
			if(StringUtils.isBlank(oddsTimeTemp)){
				break;
			}
			
			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td.noborder.bright
			String oddsTimeStrOriginal = elementsTR.select(" > td.noborder.bright")
					.text();
			
			oddsSeq++;
			if(numOfSeq != 0 && oddsSeq > numOfSeq){
				break;
			}
			
			String oddsTimeStr = oddsTimeStrOriginal.substring(0, 16);
			String oddsTime = oddsTimeStr.replaceAll("/", "-") + ":00";
			
			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(2)
			String timeBeforeMatchStr = elementsTR.select(" > td:nth-child(2)")
					.text();
			String hourStr = timeBeforeMatchStr.substring(
					timeBeforeMatchStr.lastIndexOf("前") + 1,
					timeBeforeMatchStr.lastIndexOf("小"));
			String minStr = timeBeforeMatchStr.substring(
					timeBeforeMatchStr.lastIndexOf("时") + 1,
					timeBeforeMatchStr.lastIndexOf("分"));
			String timeBeforeMatch = hourStr + "." + minStr;

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(3)
			String hostOddsStr = elementsTR.select(" > td:nth-child(3)").text();
			Float hostOdds = parseOddsAndKelly(hostOddsStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(4)
			String evenOddsStr = elementsTR.select(" > td:nth-child(4)").text();
			Float evenOdds = parseOddsAndKelly(evenOddsStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(5)
			String visitingOddsStr = elementsTR.select(" > td:nth-child(5)")
					.text();
			Float visitingOdds = parseOddsAndKelly(visitingOddsStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(6)
			String hostProbStr = elementsTR.select(" > td:nth-child(6)").text();
			Float hostProb = parseOddsAndKelly(hostProbStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(7)
			String evenProbStr = elementsTR.select(" > td:nth-child(7)").text();
			Float evenProb = parseOddsAndKelly(evenProbStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(8)
			String visitingProbStr = elementsTR.select(" > td:nth-child(8)")
					.text();
			Float visitingProb = parseOddsAndKelly(visitingProbStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(9) > span
			// 由于 <span> 用来控制显示的颜色, 忽略span.
			String hostKellyStr = elementsTR.select(" > td:nth-child(9)")
					.text();
			Float hostKelly = parseOddsAndKelly(hostKellyStr);

			// body > div.wrrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(10) > span
			String evenKellyStr = elementsTR.select(" > td:nth-child(10)")
					.text();
			Float evenKelly = parseOddsAndKelly(evenKellyStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(11) > span
			String visitingKellyStr = elementsTR.select(" > td:nth-child(11)")
					.text();
			Float visitingKelly = parseOddsAndKelly(visitingKellyStr);
			
			// body > div.wrap > table > tbody > tr:nth-child(3) > td:nth-child(12)
			String lossRatioStr = elementsTR.select(" > td:nth-child(12)").text();
			Float lossRatio = Float.valueOf(lossRatioStr);
			changeNum = oddsSeq;
			
			LOGGER.debug("oddsCorpName: " + oddsCorpName + "; oddsTime: "
					+ oddsTime + "; timeBeforeMatch: " + timeBeforeMatch
					+ "; hostOdds: " + hostOdds + "; evenOdds: " + evenOdds
					+ "; visitingOdds: " + visitingOdds + "; hostProb: "
					+ hostProb + "; evenProb: " + evenProb + "; visitingProb: "
					+ visitingProb + "; hostKelly: " + hostKelly
					+ "; evenKelly: " + evenKelly + "; visitingKelly: "
					+ visitingKelly);

			EuropeOddsChange odd = new EuropeOddsChange();
			odd.setOkUrlDate(okUrlDate);
			odd.setMatchSeq(matchSeq);
			odd.setOddsCorpName(oddsCorpName);
			odd.setOddsSeq(oddsSeq);
			odd.setOddsTime(Timestamp.valueOf(oddsTime));
			odd.setTimeBeforeMatch(timeBeforeMatch);
			odd.setHostOdds(hostOdds);
			odd.setEvenOdds(evenOdds);
			odd.setVisitingOdds(visitingOdds);
			odd.setHostProb(hostProb);
			odd.setEvenProb(evenProb);
			odd.setVisitingProb(visitingProb);
			odd.setHostKelly(hostKelly);
			odd.setEvenKelly(evenKelly);
			odd.setVisitingKelly(visitingKelly);
			odd.setLossRatio(lossRatio);
			odd.setTimestamp(new Timestamp(Calendar.getInstance()
					.getTimeInMillis()));

			odds.add(odd);
		}
		
		// 修改changeNum
		for(EuropeOddsChange odd : odds){
			odd.setChangeNum(changeNum);
		}
		
		// 添加最后一个为 "初"的. 最后2个可能均为"初".
		if(addInitOdds){
			Elements elementsTR1 = europeOddsChangeDoc.getElementsContainingText("(初)");
			EuropeOddsChange euroOddsChangeDaily = null;
			int lastOddsSeq = 0;
			if(odds != null && odds.size() > 0){
				lastOddsSeq = odds.get(odds.size() - 1).getOddsSeq();
			}
			euroOddsChangeDaily = getEuroOddsChangeDailyFromElement(elementsTR1, europeOddsChangeDoc, lastOddsSeq);
			if(euroOddsChangeDaily != null){
				euroOddsChangeDaily.setOkUrlDate(okUrlDate);
				euroOddsChangeDaily.setMatchSeq(matchSeq);
				odds.add(euroOddsChangeDaily);
			}
		}
		
		return odds;
	}
	
	private List<EuropeOddsChangeAll> getEuropeOddsChangeAllFromDoc(Document europeOddsChangeDoc, int numOfSeq, 
			String okUrlDate, Integer matchSeq){
		if (europeOddsChangeDoc == null) {
			LOGGER.error("doc is null, return now.");
			return null;
		}
		
		// body > div.wrap > div.lqnav > div > a:nth-child(5) 从面包屑中获取 okMatchId
		String euroChangeHrefStr = europeOddsChangeDoc.select("body > div.wrap > div.lqnav > div > a:nth-child(5)").attr("href");
		// 考虑异常情况: 页面不完整.
		if(StringUtils.isBlank(euroChangeHrefStr)){
			return null;
		}
		String[] euroChangeHrefArr = StringUtils.split(euroChangeHrefStr, "/");
		// 考虑异常情况: 有页面，但是数据不存在.
		if(euroChangeHrefArr.length < 3 || !StringUtils.isNumeric(euroChangeHrefArr[2])){
			return null;
		}

		List<EuropeOddsChangeAll> odds = new ArrayList<EuropeOddsChangeAll>();

		// 设置最大值.
		int max = 2000;
		int lineNo = 2;
		int oddsSeq = 0;
		while (lineNo++ < max) {
			// body > div.wrap > table > tbody > tr:nth-child(3) body > div.wrap
			// > table > tbody > tr:nth-child(5) body > div.wrap > table > tbody
			// > tr:nth-child(6)
			// 跳过 4.
			if (lineNo == 4) {
				continue;
			}
			
			Elements elementsTR = europeOddsChangeDoc
					.select("body > div.wrap > table > tbody > tr:nth-child("
							+ lineNo + ")");

			// 获取完毕 或者 达到上限则退出
			if (elementsTR == null || elementsTR.isEmpty()) {
				break;
			}

			// 从面包屑中获取:  body > div.wrap > div.lqnav > div > b
			String oddsCorpName = europeOddsChangeDoc.select("body > div.wrap > div.lqnav > div > b").text();

			// 考虑该不存在的情况
			String oddsTimeTemp =  elementsTR.select(" > td.noborder.bright").text();
			if(StringUtils.isBlank(oddsTimeTemp)){
				break;
			}
			
			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td.noborder.bright
			String oddsTimeStrOriginal = elementsTR.select(" > td.noborder.bright")
					.text();
			// 达到上限-1, 最后一个取 "初"的，即: 2014/12/01 23:25(初). 最多11条数据.
//			if(odds.size() == (numOfChanges - 1) && !oddsTimeStrOriginal.contains("初")){
//				continue;
//			}
			
			oddsSeq++;
			if(numOfSeq != 0 && oddsSeq > numOfSeq){
				break;
			}
			
			String oddsTimeStr = oddsTimeStrOriginal.substring(0, 16);
			String oddsTime = oddsTimeStr.replaceAll("/", "-") + ":00";
			
			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(2)
			String timeBeforeMatchStr = elementsTR.select(" > td:nth-child(2)")
					.text();
			String hourStr = timeBeforeMatchStr.substring(
					timeBeforeMatchStr.lastIndexOf("前") + 1,
					timeBeforeMatchStr.lastIndexOf("小"));
			String minStr = timeBeforeMatchStr.substring(
					timeBeforeMatchStr.lastIndexOf("时") + 1,
					timeBeforeMatchStr.lastIndexOf("分"));
			String timeBeforeMatch = hourStr + "." + minStr;

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(3)
			String hostOddsStr = elementsTR.select(" > td:nth-child(3)").text();
			Float hostOdds = parseOddsAndKelly(hostOddsStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(4)
			String evenOddsStr = elementsTR.select(" > td:nth-child(4)").text();
			Float evenOdds = parseOddsAndKelly(evenOddsStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(5)
			String visitingOddsStr = elementsTR.select(" > td:nth-child(5)")
					.text();
			Float visitingOdds = parseOddsAndKelly(visitingOddsStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(6)
			String hostProbStr = elementsTR.select(" > td:nth-child(6)").text();
			Float hostProb = parseOddsAndKelly(hostProbStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(7)
			String evenProbStr = elementsTR.select(" > td:nth-child(7)").text();
			Float evenProb = parseOddsAndKelly(evenProbStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(8)
			String visitingProbStr = elementsTR.select(" > td:nth-child(8)")
					.text();
			Float visitingProb = parseOddsAndKelly(visitingProbStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(9) > span
			// 由于 <span> 用来控制显示的颜色, 忽略span.
			String hostKellyStr = elementsTR.select(" > td:nth-child(9)")
					.text();
			Float hostKelly = parseOddsAndKelly(hostKellyStr);

			// body > div.wrrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(10) > span
			String evenKellyStr = elementsTR.select(" > td:nth-child(10)")
					.text();
			Float evenKelly = parseOddsAndKelly(evenKellyStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(11) > span
			String visitingKellyStr = elementsTR.select(" > td:nth-child(11)")
					.text();
			Float visitingKelly = parseOddsAndKelly(visitingKellyStr);
			
			// body > div.wrap > table > tbody > tr:nth-child(14) > td.noborder.bright

			LOGGER.debug("oddsCorpName: " + oddsCorpName + "; oddsTime: "
					+ oddsTime + "; timeBeforeMatch: " + timeBeforeMatch
					+ "; hostOdds: " + hostOdds + "; evenOdds: " + evenOdds
					+ "; visitingOdds: " + visitingOdds + "; hostProb: "
					+ hostProb + "; evenProb: " + evenProb + "; visitingProb: "
					+ visitingProb + "; hostKelly: " + hostKelly
					+ "; evenKelly: " + evenKelly + "; visitingKelly: "
					+ visitingKelly);

			EuropeOddsChangeAll odd = new EuropeOddsChangeAll();
			odd.setOkUrlDate(okUrlDate);
			odd.setMatchSeq(matchSeq);
			odd.setOddsCorpName(oddsCorpName);
			odd.setOddsSeq(oddsSeq);
			odd.setOddsTime(Timestamp.valueOf(oddsTime));
			odd.setTimeBeforeMatch(timeBeforeMatch);
			odd.setHostOdds(hostOdds);
			odd.setEvenOdds(evenOdds);
			odd.setVisitingOdds(visitingOdds);
			odd.setHostProb(hostProb);
			odd.setEvenProb(evenProb);
			odd.setVisitingProb(visitingProb);
			odd.setHostKelly(hostKelly);
			odd.setEvenKelly(evenKelly);
			odd.setVisitingKelly(visitingKelly);
			odd.setTimestamp(new Timestamp(Calendar.getInstance()
					.getTimeInMillis()));

			odds.add(odd);
		}
		
		return odds;
	}
	
	private List<EuropeOddsChange> getEuropeOddsChangeFromDoc(Document europeOddsChangeDoc, int corpNo, int numOfSeq, boolean addInitOdds){
		if (europeOddsChangeDoc == null) {
			LOGGER.error("doc is null, return now.");
			return null;
		}
		
		// body > div.wrap > div.lqnav > div > a:nth-child(5) 从面包屑中获取 okMatchId
		String euroChangeHrefStr = europeOddsChangeDoc.select("body > div.wrap > div.lqnav > div > a:nth-child(5)").attr("href");
		// 考虑异常情况: 页面不完整.
		if(StringUtils.isBlank(euroChangeHrefStr)){
			return null;
		}
		String[] euroChangeHrefArr = StringUtils.split(euroChangeHrefStr, "/");
		// 考虑异常情况: 有页面，但是数据不存在.
		if(euroChangeHrefArr.length < 3 || !StringUtils.isNumeric(euroChangeHrefArr[2])){
			return null;
		}
		long okMatchId = Long.valueOf(euroChangeHrefArr[2]);

		List<EuropeOddsChange> odds = new ArrayList<EuropeOddsChange>();

		// 设置最大值.
		int max = 20;
		// 只取最近几次变化
		int numOfChanges = 10;
		int lineNo = 2;
		int oddsSeq = 0;
		int changeNum = 0;
		while (lineNo++ < max) {
			// body > div.wrap > table > tbody > tr:nth-child(3) body > div.wrap
			// > table > tbody > tr:nth-child(5) body > div.wrap > table > tbody
			// > tr:nth-child(6)
			// 跳过 4.
			if (lineNo == 4) {
				continue;
			}
			
			Elements elementsTR = europeOddsChangeDoc
					.select("body > div.wrap > table > tbody > tr:nth-child("
							+ lineNo + ")");

			// 获取完毕 或者 达到上限则退出
			if (elementsTR == null || elementsTR.isEmpty() || odds.size() == numOfChanges) {
				break;
			}

			// 从面包屑中获取:  body > div.wrap > div.lqnav > div > b
			String oddsCorpName = europeOddsChangeDoc.select("body > div.wrap > div.lqnav > div > b").text();

			// 考虑该不存在的情况
			String oddsTimeTemp =  elementsTR.select(" > td.noborder.bright").text();
			if(StringUtils.isBlank(oddsTimeTemp)){
				break;
			}
			
			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td.noborder.bright
			String oddsTimeStrOriginal = elementsTR.select(" > td.noborder.bright")
					.text();
			// 达到上限-1, 最后一个取 "初"的，即: 2014/12/01 23:25(初). 最多11条数据.
//			if(odds.size() == (numOfChanges - 1) && !oddsTimeStrOriginal.contains("初")){
//				continue;
//			}
			
			oddsSeq++;
			if(numOfSeq != 0 && oddsSeq > numOfSeq){
				break;
			}
			
			String oddsTimeStr = oddsTimeStrOriginal.substring(0, 16);
			String oddsTime = oddsTimeStr.replaceAll("/", "-") + ":00";
			
			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(2)
			String timeBeforeMatchStr = elementsTR.select(" > td:nth-child(2)")
					.text();
			String hourStr = timeBeforeMatchStr.substring(
					timeBeforeMatchStr.lastIndexOf("前") + 1,
					timeBeforeMatchStr.lastIndexOf("小"));
			String minStr = timeBeforeMatchStr.substring(
					timeBeforeMatchStr.lastIndexOf("时") + 1,
					timeBeforeMatchStr.lastIndexOf("分"));
			String timeBeforeMatch = hourStr + "." + minStr;

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(3)
			String hostOddsStr = elementsTR.select(" > td:nth-child(3)").text();
			Float hostOdds = parseOddsAndKelly(hostOddsStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(4)
			String evenOddsStr = elementsTR.select(" > td:nth-child(4)").text();
			Float evenOdds = parseOddsAndKelly(evenOddsStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(5)
			String visitingOddsStr = elementsTR.select(" > td:nth-child(5)")
					.text();
			Float visitingOdds = parseOddsAndKelly(visitingOddsStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(6)
			String hostProbStr = elementsTR.select(" > td:nth-child(6)").text();
			Float hostProb = parseOddsAndKelly(hostProbStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(7)
			String evenProbStr = elementsTR.select(" > td:nth-child(7)").text();
			Float evenProb = parseOddsAndKelly(evenProbStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(8)
			String visitingProbStr = elementsTR.select(" > td:nth-child(8)")
					.text();
			Float visitingProb = parseOddsAndKelly(visitingProbStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(9) > span
			// 由于 <span> 用来控制显示的颜色, 忽略span.
			String hostKellyStr = elementsTR.select(" > td:nth-child(9)")
					.text();
			Float hostKelly = parseOddsAndKelly(hostKellyStr);

			// body > div.wrrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(10) > span
			String evenKellyStr = elementsTR.select(" > td:nth-child(10)")
					.text();
			Float evenKelly = parseOddsAndKelly(evenKellyStr);

			// body > div.wrap > table > tbody > tr:nth-child(3) >
			// td:nth-child(11) > span
			String visitingKellyStr = elementsTR.select(" > td:nth-child(11)")
					.text();
			Float visitingKelly = parseOddsAndKelly(visitingKellyStr);
			
			// 赔付率, 不插入数据库, 用作从html分析数据.
			// body > div.wrap > table > tbody > tr:nth-child(5) > td:nth-child(12)
			String lossRatioStr = elementsTR.select(" > td:nth-child(12)").text();
			Float lossRatio = parseOddsAndKelly(lossRatioStr);
			changeNum = oddsSeq;
			
			// body > div.wrap > table > tbody > tr:nth-child(14) > td.noborder.bright

			LOGGER.debug("oddsCorpName: " + oddsCorpName + "; oddsTime: "
					+ oddsTime + "; timeBeforeMatch: " + timeBeforeMatch
					+ "; hostOdds: " + hostOdds + "; evenOdds: " + evenOdds
					+ "; visitingOdds: " + visitingOdds + "; hostProb: "
					+ hostProb + "; evenProb: " + evenProb + "; visitingProb: "
					+ visitingProb + "; hostKelly: " + hostKelly
					+ "; evenKelly: " + evenKelly + "; visitingKelly: "
					+ visitingKelly);

			EuropeOddsChange odd = new EuropeOddsChange();
			odd.setOkMatchId(okMatchId);
			odd.setOddsCorpName(oddsCorpName);
			odd.setOddsSeq(oddsSeq);
			odd.setOddsTime(Timestamp.valueOf(oddsTime));
			odd.setTimeBeforeMatch(timeBeforeMatch);
			odd.setHostOdds(hostOdds);
			odd.setEvenOdds(evenOdds);
			odd.setVisitingOdds(visitingOdds);
			odd.setHostProb(hostProb);
			odd.setEvenProb(evenProb);
			odd.setVisitingProb(visitingProb);
			odd.setHostKelly(hostKelly);
			odd.setEvenKelly(evenKelly);
			odd.setVisitingKelly(visitingKelly);
			odd.setLossRatio(lossRatio);
			odd.setTimestamp(new Timestamp(Calendar.getInstance()
					.getTimeInMillis()));

			odds.add(odd);
		}
		// 修改changeNum
		for(EuropeOddsChange odd : odds){
			odd.setChangeNum(changeNum);
		}
		
		// 添加最后一个为 "初"的. 第10，11个可能均为"初".
		if(addInitOdds || (odds != null && odds.size() >= 10)){
			Elements elementsTR1 = europeOddsChangeDoc.getElementsContainingText("(初)");
			EuropeOddsChange euroOddsChange = null;
			int lastOddsSeq = 0;
			if(odds != null && odds.size() > 0){
				lastOddsSeq = odds.get(odds.size() - 1).getOddsSeq();
			}
			euroOddsChange = getEuroOddsChangeFromElement(elementsTR1, europeOddsChangeDoc, lastOddsSeq);
			if(euroOddsChange != null){
				odds.add(euroOddsChange);
			}
		}
		
		return odds;
	}
	
	private EuropeOddsChange getEuroOddsChangeFromElement(Elements elementsTR, Document europeOddsChangeDoc, int lastOddsSeq){
		// 获取完毕 或者 达到上限则退出
		if (elementsTR == null || elementsTR.isEmpty()) {
			return null;
		}

		// body > div.wrap > div.lqnav > div > a:nth-child(5) 从面包屑中获取 okMatchId
		String euroChangeHrefStr = europeOddsChangeDoc.select("body > div.wrap > div.lqnav > div > a:nth-child(5)").attr("href");
		String[] euroChangeHrefArr = StringUtils.split(euroChangeHrefStr, "/");
		// 考虑异常情况: 有页面，但是数据不存在.
		if(euroChangeHrefArr.length < 3 || !StringUtils.isNumeric(euroChangeHrefArr[2])){
			return null;
		}
		long okMatchId = Long.valueOf(euroChangeHrefArr[2]);
		
		// 从面包屑中获取:  body > div.wrap > div.lqnav > div > b
		String oddsCorpName = europeOddsChangeDoc.select("body > div.wrap > div.lqnav > div > b").text();

		// 考虑该不存在的情况
		String oddsTimeTemp =  elementsTR.select(" > td.noborder.bright").text();
		if(StringUtils.isBlank(oddsTimeTemp)){
			return null;
		}
		
		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td.noborder.bright
		String oddsTimeStrOriginal = elementsTR.select(" > td.noborder.bright")
				.text();
		
		int oddsSeq = lastOddsSeq + 1;
		
		String oddsTimeStr = oddsTimeStrOriginal.substring(0, 16);
		String oddsTime = oddsTimeStr.replaceAll("/", "-") + ":00";
		
		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(2)
		String timeBeforeMatchStr = elementsTR.select(" > td:nth-child(2)")
				.text();
		String hourStr = timeBeforeMatchStr.substring(
				timeBeforeMatchStr.lastIndexOf("前") + 1,
				timeBeforeMatchStr.lastIndexOf("小"));
		String minStr = timeBeforeMatchStr.substring(
				timeBeforeMatchStr.lastIndexOf("时") + 1,
				timeBeforeMatchStr.lastIndexOf("分"));
		String timeBeforeMatch = hourStr + "." + minStr;

		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(3)
		String hostOddsStr = elementsTR.select(" > td:nth-child(3)").text();
		Float hostOdds = parseOddsAndKelly(hostOddsStr);

		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(4)
		String evenOddsStr = elementsTR.select(" > td:nth-child(4)").text();
		Float evenOdds = parseOddsAndKelly(evenOddsStr);

		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(5)
		String visitingOddsStr = elementsTR.select(" > td:nth-child(5)")
				.text();
		Float visitingOdds = parseOddsAndKelly(visitingOddsStr);

		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(6)
		String hostProbStr = elementsTR.select(" > td:nth-child(6)").text();
		Float hostProb = parseOddsAndKelly(hostProbStr);

		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(7)
		String evenProbStr = elementsTR.select(" > td:nth-child(7)").text();
		Float evenProb = parseOddsAndKelly(evenProbStr);

		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(8)
		String visitingProbStr = elementsTR.select(" > td:nth-child(8)")
				.text();
		Float visitingProb = parseOddsAndKelly(visitingProbStr);

		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(9) > span
		// 由于 <span> 用来控制显示的颜色, 忽略span.
		String hostKellyStr = elementsTR.select(" > td:nth-child(9)")
				.text();
		Float hostKelly = parseOddsAndKelly(hostKellyStr);

		// body > div.wrrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(10) > span
		String evenKellyStr = elementsTR.select(" > td:nth-child(10)")
				.text();
		Float evenKelly = parseOddsAndKelly(evenKellyStr);

		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(11) > span
		String visitingKellyStr = elementsTR.select(" > td:nth-child(11)")
				.text();
		Float visitingKelly = parseOddsAndKelly(visitingKellyStr);
		
		// 赔付率, 不插入数据库, 用作从html分析数据.
		// body > div.wrap > table > tbody > tr:nth-child(5) > td:nth-child(12)
		String lossRatioStr = elementsTR.select(" > td:nth-child(12)").text();
		Float lossRatio = parseOddsAndKelly(lossRatioStr);
		int changeNum = oddsSeq;
		
		// body > div.wrap > table > tbody > tr:nth-child(14) > td.noborder.bright

		LOGGER.debug("oddsCorpName: " + oddsCorpName + "; oddsTime: "
				+ oddsTime + "; timeBeforeMatch: " + timeBeforeMatch
				+ "; hostOdds: " + hostOdds + "; evenOdds: " + evenOdds
				+ "; visitingOdds: " + visitingOdds + "; hostProb: "
				+ hostProb + "; evenProb: " + evenProb + "; visitingProb: "
				+ visitingProb + "; hostKelly: " + hostKelly
				+ "; evenKelly: " + evenKelly + "; visitingKelly: "
				+ visitingKelly);

		EuropeOddsChange odd = new EuropeOddsChange();
		odd.setOkMatchId(okMatchId);
		odd.setOddsCorpName(oddsCorpName);
		odd.setOddsSeq(oddsSeq);
		odd.setOddsTime(Timestamp.valueOf(oddsTime));
		odd.setTimeBeforeMatch(timeBeforeMatch);
		odd.setHostOdds(hostOdds);
		odd.setEvenOdds(evenOdds);
		odd.setVisitingOdds(visitingOdds);
		odd.setHostProb(hostProb);
		odd.setEvenProb(evenProb);
		odd.setVisitingProb(visitingProb);
		odd.setHostKelly(hostKelly);
		odd.setEvenKelly(evenKelly);
		odd.setVisitingKelly(visitingKelly);
		odd.setLossRatio(lossRatio);
		odd.setTimestamp(new Timestamp(Calendar.getInstance()
				.getTimeInMillis()));
		odd.setChangeNum(changeNum);
		return odd;
	}
	
	private EuropeOddsChange getEuroOddsChangeDailyFromElement(Elements elementsTR, Document europeOddsChangeDoc, int lastOddsSeq){
		// 获取完毕 或者 达到上限则退出
		if (elementsTR == null || elementsTR.isEmpty()) {
			return null;
		}

		// 从面包屑中获取:  body > div.wrap > div.lqnav > div > b
		String oddsCorpName = europeOddsChangeDoc.select("body > div.wrap > div.lqnav > div > b").text();

		// 考虑该不存在的情况
		String oddsTimeTemp =  elementsTR.select(" > td.noborder.bright").text();
		if(StringUtils.isBlank(oddsTimeTemp)){
			return null;
		}
		
		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td.noborder.bright
		String oddsTimeStrOriginal = elementsTR.select(" > td.noborder.bright")
				.text();
		
		int oddsSeq = lastOddsSeq + 1;
		
		String oddsTimeStr = oddsTimeStrOriginal.substring(0, 16);
		String oddsTime = oddsTimeStr.replaceAll("/", "-") + ":00";
		
		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(2)
		String timeBeforeMatchStr = elementsTR.select(" > td:nth-child(2)")
				.text();
		String hourStr = timeBeforeMatchStr.substring(
				timeBeforeMatchStr.lastIndexOf("前") + 1,
				timeBeforeMatchStr.lastIndexOf("小"));
		String minStr = timeBeforeMatchStr.substring(
				timeBeforeMatchStr.lastIndexOf("时") + 1,
				timeBeforeMatchStr.lastIndexOf("分"));
		String timeBeforeMatch = hourStr + "." + minStr;

		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(3)
		String hostOddsStr = elementsTR.select(" > td:nth-child(3)").text();
		Float hostOdds = parseOddsAndKelly(hostOddsStr);

		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(4)
		String evenOddsStr = elementsTR.select(" > td:nth-child(4)").text();
		Float evenOdds = parseOddsAndKelly(evenOddsStr);

		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(5)
		String visitingOddsStr = elementsTR.select(" > td:nth-child(5)")
				.text();
		Float visitingOdds = parseOddsAndKelly(visitingOddsStr);

		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(6)
		String hostProbStr = elementsTR.select(" > td:nth-child(6)").text();
		Float hostProb = parseOddsAndKelly(hostProbStr);

		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(7)
		String evenProbStr = elementsTR.select(" > td:nth-child(7)").text();
		Float evenProb = parseOddsAndKelly(evenProbStr);

		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(8)
		String visitingProbStr = elementsTR.select(" > td:nth-child(8)")
				.text();
		Float visitingProb = parseOddsAndKelly(visitingProbStr);

		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(9) > span
		// 由于 <span> 用来控制显示的颜色, 忽略span.
		String hostKellyStr = elementsTR.select(" > td:nth-child(9)")
				.text();
		Float hostKelly = parseOddsAndKelly(hostKellyStr);

		// body > div.wrrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(10) > span
		String evenKellyStr = elementsTR.select(" > td:nth-child(10)")
				.text();
		Float evenKelly = parseOddsAndKelly(evenKellyStr);

		// body > div.wrap > table > tbody > tr:nth-child(3) >
		// td:nth-child(11) > span
		String visitingKellyStr = elementsTR.select(" > td:nth-child(11)")
				.text();
		Float visitingKelly = parseOddsAndKelly(visitingKellyStr);
		
		// 赔付率, 不插入数据库, 用作从html分析数据.
		// body > div.wrap > table > tbody > tr:nth-child(5) > td:nth-child(12)
		String lossRatioStr = elementsTR.select(" > td:nth-child(12)").text();
		Float lossRatio = parseOddsAndKelly(lossRatioStr);
		// 这里和其他oddsSeq的changeNum保持一致.
		int changeNum = oddsSeq - 1;
		

		EuropeOddsChange odd = new EuropeOddsChange();
		odd.setOddsCorpName(oddsCorpName);
		odd.setOddsSeq(oddsSeq);
		odd.setOddsTime(Timestamp.valueOf(oddsTime));
		odd.setTimeBeforeMatch(timeBeforeMatch);
		odd.setHostOdds(hostOdds);
		odd.setEvenOdds(evenOdds);
		odd.setVisitingOdds(visitingOdds);
		odd.setHostProb(hostProb);
		odd.setEvenProb(evenProb);
		odd.setVisitingProb(visitingProb);
		odd.setHostKelly(hostKelly);
		odd.setEvenKelly(evenKelly);
		odd.setVisitingKelly(visitingKelly);
		odd.setLossRatio(lossRatio);
		odd.setTimestamp(new Timestamp(Calendar.getInstance()
				.getTimeInMillis()));
		odd.setChangeNum(changeNum);
		return odd;
	}
	
	/**
	 * 解析亚盘变化页面.(http://www.okooo.com/soccer/match/734052/ah/change/27/)
	 * 只获取最近的部分.
	 */
	public List<AsiaOddsChange> getAsiaOddsChange(long matchId, int matchSeq,
			int corpNo) {
		Document asiaOddsChangeDoc = JsoupUtils.getOddsChangeDoc(matchSeq,
				OkConstant.DOC_TYPE_ASIA_ODDS_CHANGE, corpNo);
		return getAsiaOddsChangeFromDoc(asiaOddsChangeDoc, corpNo, false);
	}
	
	/**
	 * 从本地文件解析亚盘变化页面.(http://www.okooo.com/soccer/match/734052/ah/change/27/)
	 */
	public List<AsiaOddsChange> getAsiaOddsChangeFromFile(
			File asiaOddsChangeHtml, boolean toGetAll) {
		if(!asiaOddsChangeHtml.exists()){
			return null;
		}
		Document asiaOddsChangeDoc = Jsoup.parse(OkParseUtils.getFileContent(asiaOddsChangeHtml));
		int corpNo = OkParseUtils.getCorpNoFromOddsChangeFile(asiaOddsChangeHtml);
		return getAsiaOddsChangeFromDoc(asiaOddsChangeDoc, corpNo, toGetAll);
	}
	
	private List<AsiaOddsChange> getAsiaOddsChangeFromDoc(Document asiaOddsChangeDoc, int corpNo, boolean toGetAll){
		if (asiaOddsChangeDoc == null) {
			LOGGER.error("doc is null, return now.");
			return null;
		}

		// body > div.wrap > div.lqnav > div > a:nth-child(5) 从面包屑中获取 okMatchId
		String asiaChangeHrefStr = asiaOddsChangeDoc.select("body > div.wrap > div.lqnav > div > a:nth-child(5)").attr("href");
		// 考虑异常情况: 页面不完整.
		if(StringUtils.isBlank(asiaChangeHrefStr)){
			return null;
		}
		// 考虑异常情况: 有页面，但是数据不存在.
		String[] asiaChangeHrefArr = StringUtils.split(asiaChangeHrefStr, "/");
		// 考虑异常情况: 有页面，但是数据不存在.
		if(asiaChangeHrefArr.length < 3 || !StringUtils.isNumeric(asiaChangeHrefArr[2])){
			return null;
		}
		long okMatchId = Long.valueOf(asiaChangeHrefArr[2]);
		
		List<AsiaOddsChange> odds = new ArrayList<AsiaOddsChange>();

		// 设置最大值.
		int max = 20;
		// 只取最近的10次变化
		int numOfChanges = 10;
		if(toGetAll){
			max = 2000;
			numOfChanges = 2000;
		}
		int lineNo = 2;
		int oddsSeq = 0;
		int changeNum = 0;
		while (lineNo++ < max) {
			// body > div.wrap > table:nth-child(4) > tbody > tr:nth-child(3)
			// body > div.wrap > table:nth-child(4) > tbody > tr:nth-child(4)
			// body > div.wrap > table:nth-child(4) > tbody > tr:nth-child(5)
			Elements elementsTR = asiaOddsChangeDoc
					.select("body > div.wrap > table:nth-child(4) > tbody > tr:nth-child("
							+ lineNo + ")");
			
			oddsSeq++;

			// 获取完毕，或者已达到上限则退出
			if (elementsTR == null || elementsTR.isEmpty()
					|| odds.size() == numOfChanges) {
				break;
			}

			String oddsCorpName = OkParseUtils.translateCorpName(corpNo);

			// body > div.wrap > table:nth-child(4) > tbody > tr:nth-child(3) >
			// td.noborder.bright
			String oddsTimeStr = elementsTR.select(" > td.noborder.bright")
					.text().substring(0, 16);
			String oddsTimeFormat = oddsTimeStr.replaceAll("/", "-") + ":00";

			// body > div.wrap > table:nth-child(4) > tbody > tr:nth-child(3) >
			// td:nth-child(2)
			String timeBeforeMatchStr = elementsTR.select(" > td:nth-child(2)")
					.text();
			String hourStr = timeBeforeMatchStr.substring(
					timeBeforeMatchStr.lastIndexOf("前") + 1,
					timeBeforeMatchStr.lastIndexOf("时"));
			String minStr = timeBeforeMatchStr.substring(
					timeBeforeMatchStr.lastIndexOf("时") + 1,
					timeBeforeMatchStr.lastIndexOf("分"));
			String timeBeforeMatch = hourStr + "." + minStr;

			// body > div.wrap > table:nth-child(4) > tbody > tr:nth-child(3) >
			// td:nth-child(3) > span > span
			String hostOddsStr = elementsTR.select(" > td:nth-child(3)").text();
			Float hostOdds = parseOddsAndKelly(hostOddsStr);

			// body > div.wrap > table:nth-child(4) > tbody > tr:nth-child(3) >
			// td:nth-child(4)
			String handicapStr = elementsTR.select(" > td:nth-child(4)").text();
			Float handicap = OkParseUtils.translateHandicap(handicapStr);
			if(handicap == null){
				continue;
			}

			// body > div.wrap > table:nth-child(4) > tbody > tr:nth-child(3) >
			// td:nth-child(5) > span > span
			String visitingOddsStr = elementsTR.select(" > td:nth-child(5)")
					.text();
			Float visitingOdds = parseOddsAndKelly(visitingOddsStr);
			
			changeNum = oddsSeq;

			LOGGER.debug("oddsCorpName: " + oddsCorpName + "; oddsTimeFormat: "
					+ oddsTimeFormat + "; timeBeforeMatch: " + timeBeforeMatch
					+ "; hostOdds: " + hostOdds + "; visitingOdds: "
					+ visitingOdds + "; handicap: " + handicap);

			AsiaOddsChange odd = new AsiaOddsChange();
			odd.setOkMatchId(okMatchId);
			odd.setOddsCorpName(oddsCorpName);
			odd.setOddsSeq(oddsSeq);
			odd.setOddsTime(Timestamp.valueOf(oddsTimeFormat));
			odd.setTimeBeforeMatch(timeBeforeMatch);
			odd.setHostOdds(hostOdds);
			odd.setHandicap(handicap);
			odd.setVisitingOdds(visitingOdds);
			odd.setTimestamp(new Timestamp(Calendar.getInstance()
					.getTimeInMillis()));

			odds.add(odd);
		}
		
		// 修改changeNum
		for(AsiaOddsChange odd : odds){
			odd.setChangeNum(changeNum);
		}

		return odds;
	}
	
	/**
	 * 从本地文件解析欧赔转换为亚盘页面.(http://www.okooo.com/soccer/match/713907/ah/?action=euro2asia&MatchID=713907&MakerIDList=0|82,1|65,2|19,3|84,4|220,5|280,6|106,7|543,8|593,9|696)
	 */
	public List<EuroTransAsia> getEuroTransAsiaFromFile(File euroTransAsiaHtml, EuroTransAsia euroTransAsiaInit, Map<String, Float> lossRatioMap) {
		Document euroTransAsiaDoc = Jsoup.parse(OkParseUtils.getFileContent(euroTransAsiaHtml));
		return getEuroTransAsiaFromDoc(euroTransAsiaDoc, euroTransAsiaInit, lossRatioMap);
	}
	
	private List<EuroTransAsia> getEuroTransAsiaFromDoc(Document euroTransAsiaDoc, EuroTransAsia euroTransAsiaInit, Map<String, Float> lossRatioMap){
		if (euroTransAsiaDoc == null) {
			LOGGER.error("doc is null, return now.");
			return null;
		}
		
		List<EuroTransAsia> euroTransAsiaList = new ArrayList<EuroTransAsia>();
		// #datatable1 > table > tbody > tr:nth-child(4) > td:nth-child(1)
		// #datatable1 > table > tbody
		Elements elementsTbody = euroTransAsiaDoc.select("#datatable1 > table > tbody > tr");
		if(elementsTbody == null){
			return null;
		}
		
		Iterator<Element> iter = elementsTbody.iterator();
		while (iter.hasNext()) {
			Element elementTr = iter.next();
			//oddsCorpName: #datatable1 > table > tbody > tr:nth-child(3) > td:nth-child(1)
			String oddsCorpName = elementTr.select(" > td:nth-child(1)").text();
			if(StringUtils.isBlank(oddsCorpName)){
				continue;
			}
			
			// hostOddsEuro: #datatable1 > table > tbody > tr:nth-child(3) > td:nth-child(2)
			String hostOddsEuroStr = elementTr.select(" > td:nth-child(2)").text();
			if(StringUtils.isBlank(hostOddsEuroStr)){
				continue;
			}
			Float hostOddsEuro = Float.valueOf(hostOddsEuroStr);
			
			// evenOddsEuro: #datatable1 > table > tbody > tr:nth-child(3) > td:nth-child(3)
			String evenOddsEuroStr = elementTr.select(" > td:nth-child(3)").text();
			if(StringUtils.isBlank(evenOddsEuroStr)){
				continue;
			}
			Float evenOddsEuro = Float.valueOf(evenOddsEuroStr);
			
			// visitingOddsEuro: #datatable1 > table > tbody > tr:nth-child(3) > td:nth-child(4)
			String visitingOddsEuroStr = elementTr.select(" > td:nth-child(4)").text();
			if(StringUtils.isBlank(visitingOddsEuroStr)){
				continue;
			}
			Float visitingOddsEuro = Float.valueOf(visitingOddsEuroStr);
			
			// lossRatioEuro: #datatable1 > table > tbody > tr:nth-child(3) > td:nth-child(5)
			String lossRatioEuroStr = elementTr.select(" > td:nth-child(5)").text();
			if(StringUtils.isBlank(lossRatioEuroStr)){
				continue;
			}
			Float lossRatioEuro = Float.valueOf(lossRatioEuroStr);
			
			// hostOddsAsiaTrans: #datatable1 > table > tbody > tr:nth-child(3) > td:nth-child(6)
			String hostOddsAsiaTransStr = elementTr.select(" > td:nth-child(6)").text();
			if(StringUtils.isBlank(hostOddsAsiaTransStr)){
				continue;
			}
			Float hostOddsAsiaTrans = Float.valueOf(hostOddsAsiaTransStr);
			
			// handicapAsiaTrans: #datatable1 > table > tbody > tr:nth-child(3) > td:nth-child(7)
			String handicapAsiaTransStr = elementTr.select(" > td:nth-child(7)").text();
			if(StringUtils.isBlank(handicapAsiaTransStr)){
				continue;
			}
			Float handicapAsiaTrans = OkParseUtils.translateHandicap(handicapAsiaTransStr);
			if(handicapAsiaTrans == null){
				continue;
			}
			
			// visitingOddsAsiaTrans: #datatable1 > table > tbody > tr:nth-child(3) > td:nth-child(8)
			String visitingOddsAsiaTransStr = elementTr.select(" > td:nth-child(8)").text();
			if(StringUtils.isBlank(visitingOddsAsiaTransStr)){
				continue;
			}
			Float visitingOddsAsiaTrans = Float.valueOf(visitingOddsAsiaTransStr);
			
			// totalDiscountTrans: #datatable1 > table > tbody > tr:nth-child(3) > td:nth-child(9)
			String totalDiscountTransStr = elementTr.select(" > td:nth-child(9)").text();
			if(StringUtils.isBlank(totalDiscountTransStr)){
				continue;
			}
			Float totalDiscountTrans = Float.valueOf(totalDiscountTransStr);
			
			// hostOddsAsia: #datatable1 > table > tbody > tr:nth-child(3) > td:nth-child(10)
			String hostOddsAsiaStr = elementTr.select(" > td:nth-child(10)").text();
			Float hostOddsAsia = null;
			if(!StringUtils.isBlank(hostOddsAsiaStr)){
				hostOddsAsia = Float.valueOf(hostOddsAsiaStr);
			}
			
			// handicapAsia: #datatable1 > table > tbody > tr:nth-child(3) > td:nth-child(11)
			String handicapAsiaStr = elementTr.select(" > td:nth-child(11)").text();
			Float handicapAsia = null;
			if(!StringUtils.isBlank(handicapAsiaStr)){
				handicapAsia = OkParseUtils.translateHandicap(handicapAsiaStr);
			}
			
			// visitingOddsAsia: #datatable1 > table > tbody > tr:nth-child(3) > td:nth-child(12)
			String visitingOddsAsiaStr = elementTr.select(" > td:nth-child(12)").text();
			Float visitingOddsAsia = null;
			if(!StringUtils.isBlank(visitingOddsAsiaStr)){
				visitingOddsAsia = Float.valueOf(visitingOddsAsiaStr);
			}
			
			// totalDiscount: #datatable1 > table > tbody > tr:nth-child(3) > td:nth-child(13)
			String totalDiscountStr = elementTr.select(" > td:nth-child(13)").text();
			Float totalDiscount = null;
			if(!StringUtils.isBlank(totalDiscountStr)){
				totalDiscount = Float.valueOf(totalDiscountStr);
			}
			
			EuroTransAsia euroTransAsia = new EuroTransAsia();
			euroTransAsia.setOkUrlDate(euroTransAsiaInit.getOkUrlDate());
			euroTransAsia.setMatchSeq(euroTransAsiaInit.getMatchSeq());
			euroTransAsia.setJobType(euroTransAsiaInit.getJobType());
			euroTransAsia.setOddsCorpName(oddsCorpName);
			euroTransAsia.setHostOddsEuro(hostOddsEuro);
			euroTransAsia.setEvenOddsEuro(evenOddsEuro);
			euroTransAsia.setVisitingOddsEuro(visitingOddsEuro);
			euroTransAsia.setLossRatioEuro(lossRatioEuro);
			euroTransAsia.setHostOddsAsiaTrans(hostOddsAsiaTrans);
			euroTransAsia.setHandicapAsiaTrans(handicapAsiaTrans);
			euroTransAsia.setVisitingOddsAsiaTrans(visitingOddsAsiaTrans);
			euroTransAsia.setTotalDiscountTrans(totalDiscountTrans);
			euroTransAsia.setHostOddsAsia(hostOddsAsia);
			euroTransAsia.setHandicapAsia(handicapAsia);
			euroTransAsia.setVisitingOddsAsia(visitingOddsAsia);
			euroTransAsia.setTotalDiscount(totalDiscount);
			euroTransAsia.setLossRatioAsia(lossRatioMap.get(oddsCorpName + "_" + "A" + "_" + "L"));
			euroTransAsia.setTimestamp(new Timestamp(Calendar.getInstance()
					.getTimeInMillis()));
			euroTransAsia.setHostKellyAsia(lossRatioMap.get(oddsCorpName + "_" + "A" + "_" + "HK"));
			euroTransAsia.setVisitingKellyAsia(lossRatioMap.get(oddsCorpName + "_" + "A" + "_" + "VK"));
			euroTransAsia.setHostKellyEuro(lossRatioMap.get(oddsCorpName + "_" + "E" + "_" + "HK"));
			euroTransAsia.setEvenKellyEuro(lossRatioMap.get(oddsCorpName + "_" + "E" + "_" + "EK"));
			euroTransAsia.setVisitingKellyEuro(lossRatioMap.get(oddsCorpName + "_" + "E" + "_" + "VK"));
			euroTransAsiaList.add(euroTransAsia);
		}
		
		return euroTransAsiaList;
	}

	/**
	 * 欧赔变化页面使用, 去掉最后的 ↑ or ↓
	 * 
	 * @param str
	 * @return
	 */
	private float parseOddsAndKelly(String str) {
		
		if (!NumberUtils.isNumber(str)) {
			return Float.valueOf(str.substring(0, str.length() - 1));
		}
		return Float.valueOf(str);
	}

}
