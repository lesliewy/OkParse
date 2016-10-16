/**
 * 
 */
package com.wy.okooo.parse.impl;

import java.io.File;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.wy.okooo.domain.MatchStats;
import com.wy.okooo.parse.ParseMatchStats;
import com.wy.okooo.util.OkParseUtils;

/**
 * 球员阵容(技术统计)解析.(http://www.okooo.com/soccer/match/768266/)
 * 
 * @author leslie
 * 
 */
public class ParseMatchStatsImpl implements ParseMatchStats {
	// log4j
	private static Logger LOGGER = Logger.getLogger(ParseMatchStatsImpl.class
			.getName());

	public MatchStats getMatchStatsFromFile(File matchStatsFile) {
		Document matchStatsDoc = Jsoup.parse(OkParseUtils.getFileContent(matchStatsFile));
		String okUrlDate = OkParseUtils.getOkUrlDateFromFile(matchStatsFile);
		Integer matchSeq = Integer.valueOf(matchStatsFile.getName().split("_")[1].replace(".html", ""));
		return getMatchStatsFromDoc(matchStatsDoc, okUrlDate, matchSeq);
	}
	
	private MatchStats getMatchStatsFromDoc(Document matchStatsDoc, String okUrlDate, Integer matchSeq){
		if(matchStatsDoc == null){
			LOGGER.error("matchStatsDoc is null, return now.");
			return null;
		}
		
		/*
		Elements bodyElements = matchStatsDoc.select("#zbConObj > div.float_l.livejstjbox.tech_ana > div.livejstjtable.newlivetable > table > tbody");
		
		// 射正: #zbConObj > div.float_l.livejstjbox.tech_ana > div.livejstjtable.newlivetable > table > tbody > tr:nth-child(2) > td:nth-child(3)
		String shotsOnTargetStr = bodyElements.select(" > tr:nth-child(2) > td:nth-child(3)").text();
		// 射偏: #zbConObj > div.float_l.livejstjbox.tech_ana > div.livejstjtable.newlivetable > table > tbody > tr:nth-child(3) > td:nth-child(3)
		String shotsOffTargetStr = bodyElements.select(" > tr:nth-child(3) > td:nth-child(3)").text();
		// 任意球: #zbConObj > div.float_l.livejstjbox.tech_ana > div.livejstjtable.newlivetable > table > tbody > tr:nth-child(4) > td:nth-child(3)
		String freeKick = bodyElements.select(" > tr:nth-child(4) > td:nth-child(3)").text();
		// 角球: #zbConObj > div.float_l.livejstjbox.tech_ana > div.livejstjtable.newlivetable > table > tbody > tr:nth-child(5) > td:nth-child(3)
		String cornersStr = bodyElements.select(" > tr:nth-child(5) > td:nth-child(3)").text();
		// 界外球: #zbConObj > div.float_l.livejstjbox.tech_ana > div.livejstjtable.newlivetable > table > tbody > tr:nth-child(6) > td:nth-child(3)
		String throwInsStr = bodyElements.select(" > tr:nth-child(6) > td:nth-child(3)").text();
		// 球门球: #zbConObj > div.float_l.livejstjbox.tech_ana > div.livejstjtable.newlivetable > table > tbody > tr:nth-child(7) > td:nth-child(3)
		String goalkeeperDistStr = bodyElements.select(" > tr:nth-child(7) > td:nth-child(3)").text();
		// 扑球: #zbConObj > div.float_l.livejstjbox.tech_ana > div.livejstjtable.newlivetable > table > tbody > tr:nth-child(8) > td:nth-child(3)
		String beatOutShotStr = bodyElements.select(" > tr:nth-child(8) > td:nth-child(3)").text();
		// 越位: #zbConObj > div.float_l.livejstjbox.tech_ana > div.livejstjtable.newlivetable > table > tbody > tr:nth-child(9) > td:nth-child(3)
		String offsideStr = bodyElements.select(" > tr:nth-child(9) > td:nth-child(3)").text();
		// 犯规: #zbConObj > div.float_l.livejstjbox.tech_ana > div.livejstjtable.newlivetable > table > tbody > tr:nth-child(10) > td:nth-child(3)
		String foulCommittedStr = bodyElements.select(" > tr:nth-child(10) > td:nth-child(3)").text();
		// 控球率: #zbConObj > div.float_l.livejstjbox.tech_ana > div.livejstjtable.newlivetable > table > tbody > tr:nth-child(11) > td:nth-child(3)
		String possessionStr = bodyElements.select(" > tr:nth-child(11) > td:nth-child(3)").text();
		Integer shotsOnTargetHost = 0;
		Integer shotsOnTargetVisiting = 0;
		Integer shotsOffTargetHost = 0;
		Integer shotsOffTargetVisiting = 0;
		if("射正".equals(shotsOnTargetStr)){
			// #shezNumH
			shotsOnTargetHost = Integer.valueOf(matchStatsDoc.select("#shezNumH").text());
			// #shezNumA
			shotsOnTargetVisiting = Integer.valueOf(matchStatsDoc.select("#shezNumA").text());
		}*/
		
		Long okMatchId = Long.valueOf(matchStatsDoc.select("#qnav > div:nth-child(4) > div > span > a").attr("href").split("/")[3]);
		
		// 主队射正: #shezNumH
		Integer shotsOnTargetHost = Integer.valueOf(matchStatsDoc.select("#shezNumH").text());
		// 客队射正: #shezNumA
		Integer shotsOnTargetVisiting = Integer.valueOf(matchStatsDoc.select("#shezNumA").text());
		String shotsOnTarget = shotsOnTargetHost + "|" + shotsOnTargetVisiting;
		
		// 主队射偏: #shepNumH
		Integer shotsOffTargetHost = Integer.valueOf(matchStatsDoc.select("#shepNumH").text());
		// 客队射偏: #shepNumA
		Integer shotsOffTargetVisiting = Integer.valueOf(matchStatsDoc.select("#shepNumA").text());
		String shotsOffTarget = shotsOffTargetHost + "|" + shotsOffTargetVisiting;
		
		// 主队任意球: #renyNumH
		Integer freeKickHost = Integer.valueOf(matchStatsDoc.select("#renyNumH").text());
		// 客队任意球: #renyNumA
		Integer freeKickVisiting = Integer.valueOf(matchStatsDoc.select("#renyNumA").text());
		String freeKick = freeKickHost + "|" + freeKickVisiting;
		
		// 主队角球: #jiaoqNumH
		Integer cornersHost = Integer.valueOf(matchStatsDoc.select("#jiaoqNumH").text());
		// 客队角球: #jiaoqNumA
		Integer cornersVisiting = Integer.valueOf(matchStatsDoc.select("#jiaoqNumA").text());
		String corners = cornersHost + "|" + cornersVisiting;
		
		// 主队界外球: #jiewNumH
		Integer throwInsHost = Integer.valueOf(matchStatsDoc.select("#jiewNumH").text());
		// 客队界外球: #jiewNumA
		Integer throwInsVisiting = Integer.valueOf(matchStatsDoc.select("#jiewNumA").text());
		String throwIns = throwInsHost + "|" + throwInsVisiting;
		
		// 主队球门球: #qmqNumH
		Integer goalkeeperDistHost = Integer.valueOf(matchStatsDoc.select("#qmqNumH").text());
		// 客队球门球: #qmqNumA
		Integer goalkeeperDistVisiting = Integer.valueOf(matchStatsDoc.select("#qmqNumA").text());
		String goalkeeperDist = goalkeeperDistHost + "|" + goalkeeperDistVisiting;
		
		// 主队扑球: #puqNumH
		Integer beatOutShotHost = Integer.valueOf(matchStatsDoc.select("#puqNumH").text());
		// 客队扑球: #puqNumA
		Integer beatOutShotVisiting = Integer.valueOf(matchStatsDoc.select("#puqNumA").text());
		String beatOutShot = beatOutShotHost + "|" + beatOutShotVisiting;
		
		// 主队越位: #yuewNumH
		Integer offsideHost = Integer.valueOf(matchStatsDoc.select("#yuewNumH").text());
		// 客队越位: #yuewNumA
		Integer offsideVisiting = Integer.valueOf(matchStatsDoc.select("#yuewNumA").text());
		String offside = offsideHost + "|" + offsideVisiting;
		
		// 主队犯规: #fangNumH
		Integer foulCommittedHost = Integer.valueOf(matchStatsDoc.select("#fangNumH").text());
		// 客队犯规: #fangNumA
		Integer foulCommittedVisiting = Integer.valueOf(matchStatsDoc.select("#fangNumA").text());
		String foulCommitted = foulCommittedHost + "|" + foulCommittedVisiting;
		
		// 主队控球率: #kongqlNumH
		String possessionHost = matchStatsDoc.select("#kongqlNumH").text().replace("%", "");
		// 客队控球率: #kongqlNumA
		String possessionVisiting = matchStatsDoc.select("#kongqlNumA").text().replace("%", "");
		String possession = possessionHost + "|" + possessionVisiting;
		
		// 进球时间; 需要获取 左面图片部分的html(url: #zbConObj > iframe)
		// 主队得分: #bfValObj > b.font_red   #bfValObj > b:nth-child(1)
		// 可对得分: #bfValObj > b.font_blue  #bfValObj > b:nth-child(3)
		// #srlive_container > div.sc-pitch.clearfix > div > div > div.sc-field > div.sc-match-tab-content.sc-match-info.active > div.sc-matchinfo-wrapper.sc-notification > div.sc-matchinfo-goal.matchinfo-navigation-container.active > div > div > div > div.matchinfo-team-container.home > table > tbody > tr > td.time
		// #srlive_container > div.sc-pitch.clearfix > div > div > div.sc-field > div.sc-match-tab-content.sc-match-info.active > div.sc-matchinfo-wrapper.sc-notification > div.sc-matchinfo-goal.matchinfo-navigation-container.active > div > div > div > div.matchinfo-team-container.away > table > tbody > tr > td.time
		// #srlive_container > div.sc-pitch.clearfix > div > div > div.sc-field > div.sc-match-tab-content.sc-match-info.active > div.sc-matchinfo-wrapper.sc-notification > div.sc-matchinfo-goal.matchinfo-navigation-container.active > div > div > div > div.matchinfo-team-container.home > table > tbody > tr:nth-child(1) > td.time
		// #srlive_container > div.sc-pitch.clearfix > div > div > div.sc-field > div.sc-match-tab-content.sc-match-info.active > div.sc-matchinfo-wrapper.sc-notification > div.sc-matchinfo-goal.matchinfo-navigation-container.active > div > div > div > div.matchinfo-team-container.home > table > tbody > tr:nth-child(4) > td.time
		// #srlive_container > div.sc-pitch.clearfix > div > div > div.sc-field > div.sc-match-tab-content.sc-match-info.active > div.sc-matchinfo-wrapper.sc-notification > div.sc-matchinfo-goal.matchinfo-navigation-container.active > div > div > div > div.matchinfo-team-container.away > table > tbody > tr:nth-child(2) > td.time
//		Integer hostGoals = Integer.valueOf(matchStatsDoc.select("#bfValObj > b:nth-child(1)").text());
//		Integer visitingGoals = Integer.valueOf(matchStatsDoc.select("#bfValObj > b:nth-child(3)").text());
		String hostGoalsTimeStr = "";
		String visitingGoalsTimeStr = "";
//		if(hostGoals == 1){
//			hostGoalsTimeStr = matchStatsDoc.select("#srlive_container > div.sc-pitch.clearfix > div > div > div.sc-field > div.sc-match-tab-content.sc-match-info.active > div.sc-matchinfo-wrapper.sc-notification > div.sc-matchinfo-goal.matchinfo-navigation-container.active > div > div > div > div.matchinfo-team-container.home > table > tbody > tr > td.time").text();
//		}else if(hostGoals > 1){
//			for(int i = 1; i <= hostGoals; i++){
//				hostGoalsTimeStr += hostGoalsTimeStr + matchStatsDoc.select("#srlive_container > div.sc-pitch.clearfix > div > div > div.sc-field > div.sc-match-tab-content.sc-match-info.active > div.sc-matchinfo-wrapper.sc-notification > div.sc-matchinfo-goal.matchinfo-navigation-container.active > div > div > div > div.matchinfo-team-container.home > table > tbody > tr:nth-child(" + i + ") > td.time").text() + ",";
//			}
//		}
//		
//		if(visitingGoals == 1){
//			visitingGoalsTimeStr = matchStatsDoc.select("#srlive_container > div.sc-pitch.clearfix > div > div > div.sc-field > div.sc-match-tab-content.sc-match-info.active > div.sc-matchinfo-wrapper.sc-notification > div.sc-matchinfo-goal.matchinfo-navigation-container.active > div > div > div > div.matchinfo-team-container.away > table > tbody > tr > td.time").text();
//		}else if(visitingGoals > 1){
//			for(int i = 1; i <= visitingGoals; i++){
//				visitingGoalsTimeStr += visitingGoalsTimeStr + matchStatsDoc.select("#srlive_container > div.sc-pitch.clearfix > div > div > div.sc-field > div.sc-match-tab-content.sc-match-info.active > div.sc-matchinfo-wrapper.sc-notification > div.sc-matchinfo-goal.matchinfo-navigation-container.active > div > div > div > div.matchinfo-team-container.away > table > tbody > tr:nth-child(" + i + ") > td.time").text() + ",";
//			}
//		}
		String goalTime = hostGoalsTimeStr + "|" + visitingGoalsTimeStr;
		
		Timestamp timestamp = new Timestamp(Calendar.getInstance()
				.getTimeInMillis());;
		
		MatchStats matchStats = new MatchStats();
		matchStats.setOkMatchId(okMatchId);
		matchStats.setOkUrlDate(okUrlDate);
		matchStats.setMatchSeq(matchSeq);
		matchStats.setShotsOnTarget(shotsOnTarget);
		matchStats.setShotsOffTarget(shotsOffTarget);
		matchStats.setFreeKick(freeKick);
		matchStats.setCorners(corners);
		matchStats.setThrowIns(throwIns);
		matchStats.setGoalkeeperDist(goalkeeperDist);
		matchStats.setBeatOutShot(beatOutShot);
		matchStats.setOffside(offside);
		matchStats.setFoulCommitted(foulCommitted);
		matchStats.setPossession(possession);
		matchStats.setGoalTime(goalTime);
		matchStats.setTimestamp(timestamp);
		
		return matchStats;
	}

}
