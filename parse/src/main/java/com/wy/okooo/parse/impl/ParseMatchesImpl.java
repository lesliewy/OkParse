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

import com.wy.okooo.domain.Match;
import com.wy.okooo.parse.ParseMatches;
import com.wy.okooo.util.JsoupUtils;
import com.wy.okooo.util.OkParseUtils;

/**
 * 解析单场胜平负页面(http://www.okooo.com/danchang/)
 * 
 * @author leslie
 * 
 */
public class ParseMatchesImpl implements ParseMatches {
	// log4j
	private static Logger LOGGER = Logger.getLogger(ParseMatchesImpl.class
			.getName());

	/**
	 * 将当期单场url(http://www.okooo.com/danchang/) 中所有比赛转换为Match对象
	 */
	public List<Match> getAllMatchFromUrl(int beginMatchSeq, int endMatchSeq) {
		Document doc = null;
		doc = JsoupUtils.getMatchesDoc();
		String okUrlDate = null;
		return getAllMatchesFromDoc(doc, okUrlDate, beginMatchSeq, endMatchSeq);
	}

	/**
	 * 将指定的单场url(http://www.okooo.com/danchang/140912/) 中所有比赛转换为Match对象
	 * 用于往期的url.
	 * @param url
	 * @return
	 */
	public List<Match> getAllMatchFromUrl(String url, int beginMatchSeq, int endMatchSeq) {
		Document doc = null;
		if (!StringUtils.isEmpty(url)) {
			doc = JsoupUtils.getMatchesDoc(url);
		} else {
			doc = JsoupUtils.getMatchesDoc();
		}
		String[] okUrlDateArr = url.split("/");
		String okUrlDate = okUrlDateArr[okUrlDateArr.length-1];
		return getAllMatchesFromDoc(doc, okUrlDate, beginMatchSeq, endMatchSeq);
	}
	
	/**
	 * 解析本地文件获取match 对象.
	 */
	public List<Match> getAllMatchFromFile(File matchHtmlFile, int beginMatchSeq, int endMatchSeq) {
		Document matchDoc = Jsoup.parse(OkParseUtils.getFileContent(matchHtmlFile));
		String okUrlDate = OkParseUtils.getOkUrlDateFromFile(matchHtmlFile);
		return getAllMatchesFromDoc(matchDoc, okUrlDate, beginMatchSeq, endMatchSeq);
	}

	private List<Match> getAllMatchesFromDoc(Document doc, String okUrlDate, int beginMatchSeq, int endMatchSeq) {
		if(doc == null){
			return null;
		}
//		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		List<Match> result = new ArrayList<Match>();
		int matchSeq = 1;
		if(beginMatchSeq > 0){
			matchSeq = beginMatchSeq;
		}
		// #tr1
		Elements elements = doc.select("#tr" + matchSeq);
		while (elements != null && !elements.isEmpty()) {
			if(endMatchSeq > 0 && matchSeq > endMatchSeq){
				break;
			}
			matchSeq++;
			Match match = new Match();
			
			// 通过 "欧"(欧赔页面url)获取okooo的 matchid.  elements.attr("matchid") 这种方式获取不到.
			// 需要考虑 href="javascript:warnMsg('/soccer/match/166965/odds/');" 这种情况
			String euroHrefStr = elements.select(" > td.tdfx.td8 > a:nth-child(1)").attr("href");
			String okMatchIdStr2 = StringUtils.split(euroHrefStr, "/")[2];
			long okMatchId = 0;
			if(StringUtils.isNumeric(okMatchIdStr2)){
				okMatchId = Long.valueOf(okMatchIdStr2);
			}else{
				okMatchId = Long.valueOf(StringUtils.split(euroHrefStr, "/")[3]);
			}
			
			String matchName = elements.select("td.td1.tdsx > a").text();
			
			//比赛时间: #tr42 > td.switchtime.timetd.td2
			String matchTime = elements.select("td.switchtime.timetd.td2")
					.attr("title").substring(5);
			
			//停售时间: #tr41 > td.switchtime.timetd.td2 > span, 跳过延期的比赛.
			String closeTimeStr = elements.select(
					"td.switchtime.timetd.td2 > span").text();
			if(StringUtils.isNotBlank(closeTimeStr) && "延期".equals(closeTimeStr)){
				elements = doc.select("#tr" + matchSeq);
				continue;
			}
			// td.ztbox.overbg.td3 > a.sbg > span.homenameobj.homename
			// #tr3 > td.ztbox.td3 > a.sbg > span.homenameobj.homename
			// #tr1 > td.ztbox.overbg.td3 > a.fbg > span
			String hostName = elements.select("span.homenameobj.homename")
					.text();
			String visitingName = elements.select("a.fbg > span").text();
			
			// 解析让球数: #tr16 > td.ztbox.overbg.td3 > a.sbg > span.handicapobj.font_red
			//          #tr299 > td.ztbox.td3 > a.sbg > span.handicapobj.font_green
			String handicapStr1 = elements.select("> td.ztbox.overbg.td3 > a.sbg > span.handicapobj.font_red").text();
			String handicapStr2 = elements.select("> td.ztbox.td3 > a.sbg > span.handicapobj.font_green").text();
			String handicapStr3 = elements.select("> td.ztbox.td3 > a.sbg > span.handicapobj.font_red").text();
			Integer handicap = 0;
			if(!StringUtils.isBlank(handicapStr1)){
				handicap = Integer.valueOf(handicapStr1.replace("(", "").replace(")", ""));
			}else if(!StringUtils.isBlank(handicapStr2)){
				handicap = Integer.valueOf(handicapStr2.replace("(", "").replace(")", ""));
			}else if(!StringUtils.isBlank(handicapStr3)){
				handicap = Integer.valueOf(handicapStr3.replace("(", "").replace(")", ""));
			}
			
			// 解析99家平均的赔率: #tr15 > td.ddtxt.td9 > span.noborder0  #tr15 > td.ddtxt.td9 > span.noborder1  #tr15 > td.ddtxt.td9 > span.noborder2
			//                               #tr1 > td.ddtxt.td9 > span.noborder0
			// 无法通过解析html获取match.html中的99家平均赔率，都是0.
			
			// #tr1 > td.tdfx.td6  
			String score = elements.select(" > td.tdfx.td6").text();
			Integer hostGoals = null;
			Integer visitingGoals = null;
			// 如果是延期，只会显示 "-", 要考虑这种情况.
			if(StringUtils.isNotBlank(score) && !score.trim().equals("-")){
				hostGoals = Integer.valueOf(StringUtils.split(score,"-")[0]);
				visitingGoals = Integer.valueOf(StringUtils.split(score,"-")[1]);
			}
			
			Timestamp timestamp = new Timestamp(Calendar.getInstance()
					.getTimeInMillis());

			LOGGER.debug("matchSeq: " + (matchSeq - 1) + ";matchName: "
					+ matchName + ";matchTime: " + matchTime + ";closeTimeStr "
					+ closeTimeStr + ";hostName: " + hostName
					+ ";visitingName: " + visitingName);

			match.setOkMatchId(okMatchId);
			match.setMatchSeq(matchSeq - 1);
			match.setMatchName(matchName);
			match.setMatchTime(Timestamp.valueOf(matchTime));
			// 考虑延期
			if(!"延期".equals(closeTimeStr.trim())){
				match.setCloseTime(Timestamp.valueOf(matchTime.substring(0,4) + "-"
						+ closeTimeStr + ":00"));
			}
			match.setHostTeamName(hostName);
			match.setVisitingTeamName(visitingName);
			match.setHandicap(handicap);
			match.setHostGoals(hostGoals == null ? null : hostGoals);
			match.setVisitingGoals(visitingGoals == null ? null : visitingGoals);
			match.setOkUrlDate(okUrlDate);
			match.setTimestamp(timestamp);
			result.add(match);

			elements = doc.select("#tr" + matchSeq);
		}
		return result;

	}

}
