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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.wy.okooo.domain.ScoreOdds;
import com.wy.okooo.parse.ParseScoreOdds;
import com.wy.okooo.util.OkParseUtils;

/**
 * 解析比分页面(http://www.okooo.com/danchang/bifen/)
 * 
 * @author leslie
 * 
 */
public class ParseScoreOddsImpl implements ParseScoreOdds {
	
	public List<ScoreOdds> getScoreOddsFromFile(File scoreOddsFile, Map<Integer, String> intervalTypeMap) {
		Document scoreOddsDoc = Jsoup.parse(OkParseUtils.getFileContent(scoreOddsFile));
		return getScoreOddsFromDoc(scoreOddsDoc, intervalTypeMap);
	}
	
	private List<ScoreOdds> getScoreOddsFromDoc(Document scoreOddsDoc, Map<Integer, String> intervalTypeMap){
		if(scoreOddsDoc == null){
			return null;
		}
		
		List<ScoreOdds> scoreOddsList = new ArrayList<ScoreOdds>();
		// #SelectLotteryNo > option:nth-child(1)
		String okUrlDate = scoreOddsDoc.select("#SelectLotteryNo > option:nth-child(1)").text().substring(0, 6);
		// 周日: #table0; 周一: #table1; 周二: #table2; ...
		for(int i = 0; i<= 6; i++){
			// #table3 > tbody > tr:nth-child(1)
			Iterator<Element> iter = scoreOddsDoc.select("#table" + i + " > tbody > tr").iterator();
			while(iter.hasNext()){
				Element elem = iter.next();
				
				// 解析包含比赛信息的tr
				// #tr22 > td.td9.tdfx > a:nth-child(1)   有两种情况:  http://www.okooo.com/soccer/match/736236/odds/;  和 javascript:warnMsg('/soccer/match/770361/odds/');
				String okMatchIdStr = elem.select(" > td.td9.tdfx > a:nth-child(1)").attr("href");
				if(StringUtils.isBlank(okMatchIdStr)){
					continue;
				}
				Long okMatchId = null;
				if(okMatchIdStr.startsWith("http")){
					okMatchId = Long.valueOf(okMatchIdStr.split("/")[5]);
				}else if(okMatchIdStr.startsWith("javascript")){
					okMatchId = Long.valueOf(okMatchIdStr.split("/")[3]);
				}
				
				Integer matchSeq = Integer.valueOf(elem.attr("id").replace("tr", ""));
				
				// 解析包含赔率的tr.
				elem = iter.next();
				
				// 胜的赔率
				Iterator<Element> winIter = elem.select("> td.scoretz.MatchBetobj > div > p:nth-child(1) > a").iterator();
				String winOdds = "";
				while(winIter.hasNext()){
					Element winElem = winIter.next();
					// #table3 > tbody > tr:nth-child(7) > td.scoretz.MatchBetobj > div > p:nth-child(1) > a:nth-child(1) > em
					String score = winElem.select(" > em").text();
					if(StringUtils.isBlank(score)){
						break;
					}
					// #table3 > tbody > tr:nth-child(7) > td.scoretz.MatchBetobj > div > p:nth-child(1) > a:nth-child(1) > span
					String odds = winElem.select(" > span").text();
					winOdds += score + "," + odds + "|";
				}
				if(StringUtils.isBlank(winOdds)){
					continue;
				}
				
				// 平的赔率
				Iterator<Element> evenIter = elem.select("> td.scoretz.MatchBetobj > div > p:nth-child(2) > a").iterator();
				String evenOdds = "";
				while(evenIter.hasNext()){
					Element evenElem = evenIter.next();
					// #table3 > tbody > tr:nth-child(7) > td.scoretz.MatchBetobj > div > p:nth-child(1) > a:nth-child(1) > em
					String score = evenElem.select(" > em").text();
					if(StringUtils.isBlank(score)){
						break;
					}
					// #table3 > tbody > tr:nth-child(7) > td.scoretz.MatchBetobj > div > p:nth-child(1) > a:nth-child(1) > span
					String odds = evenElem.select(" > span").text();
					evenOdds += score + "," + odds + "|";
				}
				if(StringUtils.isBlank(evenOdds)){
					continue;
				}
				
				// 负的赔率
				Iterator<Element> negaIter = elem.select("> td.scoretz.MatchBetobj > div > p:nth-child(3) > a").iterator();
				String negaOdds = "";
				while(negaIter.hasNext()){
					Element negaElem = negaIter.next();
					// #table3 > tbody > tr:nth-child(7) > td.scoretz.MatchBetobj > div > p:nth-child(1) > a:nth-child(1) > em
					String score = negaElem.select(" > em").text();
					if(StringUtils.isBlank(score)){
						break;
					}
					// #table3 > tbody > tr:nth-child(7) > td.scoretz.MatchBetobj > div > p:nth-child(1) > a:nth-child(1) > span
					String odds = negaElem.select(" > span").text();
					negaOdds += score + "," + odds + "|";
				}
				if(StringUtils.isBlank(negaOdds)){
					continue;
				}
				
				Timestamp timestamp = new Timestamp(Calendar.getInstance()
						.getTimeInMillis());
				
				ScoreOdds scoreOdds = new ScoreOdds();
				scoreOdds.setOkMatchId(okMatchId);
				scoreOdds.setOkUrlDate(okUrlDate);
				scoreOdds.setMatchSeq(matchSeq);
				scoreOdds.setWinOdds(winOdds);
				scoreOdds.setEvenOdds(evenOdds);
				scoreOdds.setNegaOdds(negaOdds);
				scoreOdds.setIntervalType(intervalTypeMap.get(matchSeq));
				scoreOdds.setTimestamp(timestamp);
				
				scoreOddsList.add(scoreOdds);
			}
		}
		return scoreOddsList;
	}
}
