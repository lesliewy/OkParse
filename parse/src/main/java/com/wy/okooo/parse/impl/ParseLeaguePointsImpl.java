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

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wy.okooo.domain.LeaguePoints;
import com.wy.okooo.parse.ParseLeaguePoints;
import com.wy.okooo.util.OkParseUtils;

/**
 * 解析赛事一览页面(http://www.okooo.com/soccer/league/34/)
 * 
 * @author leslie
 * 
 */
public class ParseLeaguePointsImpl implements ParseLeaguePoints {
	// log4j
	private static Logger LOGGER = Logger.getLogger(ParseLeaguePointsImpl.class
			.getName());

	/**
	 * 获取league url.
	 */
	public List<String> getLeaguePointsUrl(File allLeagueFile) {
		if(!allLeagueFile.exists()){
			LOGGER.error(allLeagueFile.getAbsolutePath() + " not exists, return null now.");
			return null;
		}
		Document allLeagueDoc = Jsoup.parse(OkParseUtils.getFileContent(allLeagueFile));
		if(allLeagueDoc == null){
			return null;
		}
		
		List<String> urls = new ArrayList<String>();
		// 左面联赛列表: body > div.CenterWidth > div.LeagueInfoMapLeft > div
		// 获取所有包含a的儿子节点: body > div.CenterWidth > div.LeagueInfoMapLeft > div > a:nth-child(77)
		Elements allLeagues = allLeagueDoc.select("body > div.CenterWidth > div.LeagueInfoMapLeft > div > a");
		Iterator<Element> allLeaguesIter = allLeagues.iterator();
		while(allLeaguesIter.hasNext()){
			Element element = allLeaguesIter.next();
//			String leageName = element.text();
			String url = element.attr("href");
			urls.add(url);
		}
		return urls;
	}

	public List<LeaguePoints> getLeaguePointsFromFile(File leaguePointsFile) {
		Document leaguePointsDoc = Jsoup.parse(OkParseUtils.getFileContent(leaguePointsFile));
		return getLeaguePointsFromDoc(leaguePointsDoc);
	}
	
	private List<LeaguePoints> getLeaguePointsFromDoc(Document leaguePointsDoc){
		if(leaguePointsDoc == null){
			return null;
		}
		Elements bodyElements = leaguePointsDoc.select("#showcontentbycodi > table > tbody");
		// body > div.page.ddbox > div.zxmaindata > div.clearfix.NewLotteryLeftNav > a:nth-child(3)
		//                                              body > div.page.ddbox > div.zxmaindata > div.clearfix.NewLotteryLeftNav > a:nth-child(3)
		Elements headElements = leaguePointsDoc.select("body > div.page.ddbox > div.zxmaindata > div.clearfix.NewLotteryLeftNav > a:nth-child(3)");
		Integer leagueId = Integer.valueOf(headElements.attr("href").split("/")[3]);
		String leagueName = headElements.text().split(" ")[0];
		String leagueTime = headElements.text().split(" ")[1];
		
		List<LeaguePoints> result = new ArrayList<LeaguePoints>();
		int trIndex = 3;
		int tdIndex = 0;
		Elements elements = bodyElements.select("> tr:nth-child(" + trIndex + ") > td");
		while (elements != null && !elements.isEmpty()) {
			trIndex++;
			tdIndex = 0;
			LeaguePoints leaguePoints = new LeaguePoints();
			
			// 排名 #showcontentbycodi > table > tbody > tr:nth-child(3) > td.jfbgyellow
			Integer rank = Integer.valueOf(elements.get(tdIndex++).text());
			// 球队
			String teamName = elements.get(tdIndex++).text();
			// 积分总榜-赛
			Integer totalMatch = Integer.valueOf(elements.get(tdIndex++).text());
			// 积分总榜-胜
			Integer totalWin = Integer.valueOf(elements.get(tdIndex++).text());
			// 积分总榜-平
			Integer totalEven = Integer.valueOf(elements.get(tdIndex++).text());
			// 积分总榜-负
			Integer totalNega = Integer.valueOf(elements.get(tdIndex++).text());
			// 积分总榜-进
			Integer totalGoals = Integer.valueOf(elements.get(tdIndex++).text());
			// 积分总榜-失
			Integer totalLost = Integer.valueOf(elements.get(tdIndex++).text());
			// 积分总榜-净
			Integer totalGoalsDiff = Integer.valueOf(elements.get(tdIndex++).text());
			
			// 主场成绩-赛
			Integer hostMatch = Integer.valueOf(elements.get(tdIndex++).text());
			// 主场成绩-胜
			Integer hostWin = Integer.valueOf(elements.get(tdIndex++).text());
			// 主场成绩-平
			Integer hostEven = Integer.valueOf(elements.get(tdIndex++).text());
			// 主场成绩-负
			Integer hostNega = Integer.valueOf(elements.get(tdIndex++).text());
			// 主场成绩-进
			Integer hostGoals = Integer.valueOf(elements.get(tdIndex++).text());
			// 主场成绩-失
			Integer hostLost = Integer.valueOf(elements.get(tdIndex++).text());

			// 客场成绩-赛
			Integer visitingMatch = Integer.valueOf(elements.get(tdIndex++).text());
			// 客场成绩-胜
			Integer visitingWin = Integer.valueOf(elements.get(tdIndex++).text());
			// 客场成绩-平
			Integer visitingEven = Integer.valueOf(elements.get(tdIndex++).text());
			// 客场成绩-负
			Integer visitingNega = Integer.valueOf(elements.get(tdIndex++).text());
			// 客场成绩-进
			Integer visitingGoals = Integer.valueOf(elements.get(tdIndex++).text());
			// 客场成绩-失
			Integer visitingLost = Integer.valueOf(elements.get(tdIndex++).text());
			
			// 积分
			Integer points = Integer.valueOf(elements.get(tdIndex++).text());
			
			leaguePoints.setLeagueTime(leagueTime);
			leaguePoints.setLeagueId(leagueId);
			leaguePoints.setLeagueName(leagueName);
			leaguePoints.setRank(rank);
			leaguePoints.setTeamName(teamName);
			leaguePoints.setTotalMatch(totalMatch);
			leaguePoints.setTotalWin(totalWin);
			leaguePoints.setTotalEven(totalEven);
			leaguePoints.setTotalNega(totalNega);
			leaguePoints.setTotalGoals(totalGoals);
			leaguePoints.setTotalLost(totalLost);
			leaguePoints.setTotalGoalsDiff(totalGoalsDiff);
			leaguePoints.setHostMatch(hostMatch);
			leaguePoints.setHostWin(hostWin);
			leaguePoints.setHostEven(hostEven);
			leaguePoints.setHostNega(hostNega);
			leaguePoints.setHostGoals(hostGoals);
			leaguePoints.setHostLost(hostLost);
			leaguePoints.setVisitingMatch(visitingMatch);
			leaguePoints.setVisitingWin(visitingWin);
			leaguePoints.setVisitingEven(visitingEven);
			leaguePoints.setVisitingNega(visitingNega);
			leaguePoints.setVisitingGoals(visitingGoals);
			leaguePoints.setVisitingLost(visitingLost);
			leaguePoints.setPoints(points);
			Timestamp timestamp = new Timestamp(Calendar.getInstance()
					.getTimeInMillis());
			leaguePoints.setTimestamp(timestamp);
			result.add(leaguePoints);
			
			elements = bodyElements.select("> tr:nth-child(" + trIndex + ") > td");
		}
		return result;
	}

}
