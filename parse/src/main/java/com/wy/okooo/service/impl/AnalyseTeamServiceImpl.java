/**
 * 
 */
package com.wy.okooo.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.wy.okooo.domain.LeaguePoints;
import com.wy.okooo.domain.Match;
import com.wy.okooo.domain.MatchStats;
import com.wy.okooo.service.AnalyseTeamService;
import com.wy.okooo.service.LeaguePointsService;
import com.wy.okooo.service.MatchStatsService;
import com.wy.okooo.service.SingleMatchService;

/**
 * 分析球队的比赛得失情况;
 * 
 * @author leslie
 *
 */
public class AnalyseTeamServiceImpl implements AnalyseTeamService{

	private static Logger LOGGER = Logger
			.getLogger(AnalyseTeamServiceImpl.class.getName());
	
	private SingleMatchService singleMatchService;
	
	private LeaguePointsService leaguePointsService;
	
	private MatchStatsService matchStatsService;
	
	/**
	 * 分析某支球队的得分胜负情况;
	 * @param teamName
	 */
	public void analyseTeamStrength(String teamName){
		if(StringUtils.isBlank(teamName)){
			LOGGER.error("teamName is blank, return now.");
			return;
		}
		
		LeaguePoints leaguePoints = leaguePointsService.queryLatestLeagPtsByTeamName(teamName);
		if(leaguePoints == null){
			LOGGER.error("leaguePoints is null, return now.");
			return;
		}
		String matchName = leaguePoints.getLeagueName();
		Integer totalMatch = leaguePoints.getTotalMatch();
		String leagueTime = "14/15";
		
		// 查询 LOT_MATCH
		Match queryMatch = new Match();
		queryMatch.setMatchName(matchName);
		queryMatch.setTeamName(teamName);
		queryMatch.setLimitedNum(totalMatch);
		List<Match> matches = singleMatchService.queryMatchRangeByMatchTeamName(queryMatch);
		
		// 查询 LOT_LEAGUE_POINTS
		LeaguePoints queryLeaguePoints = new LeaguePoints();
		queryLeaguePoints.setLeagueName(matchName);
		queryLeaguePoints.setLeagueTime(leagueTime);
		List<LeaguePoints> leaguePointsList = leaguePointsService.queryLatestLeagPtsByLeagueName(queryLeaguePoints);
		Map<String, LeaguePoints> leaguePointsMap = transLeagPtsList2Map(leaguePointsList);
		
		
		StringBuilder sb = new StringBuilder("\n");
		// 总的积分情况;
		sb.append("球队:").append(teamName).append(" 联赛:").append(matchName).append(" 积分:").append(leaguePoints.getPoints())
		  .append(" 排名:").append(leaguePoints.getRank()).append("\n");
        sb.append("积分总榜 ").append(" 赛:").append(leaguePoints.getTotalMatch()).append(" 胜:").append(leaguePoints.getTotalWin())
          .append(" 平:").append(leaguePoints.getTotalEven()).append(" 负:").append(leaguePoints.getTotalNega())
          .append(" 进:").append(leaguePoints.getTotalGoals()).append(" 失:").append(leaguePoints.getTotalLost()).append(" 净:").append(leaguePoints.getTotalGoalsDiff())
          .append("\n");
        sb.append("主场成绩 ").append(" 赛:").append(leaguePoints.getHostMatch()).append(" 胜:").append(leaguePoints.getHostWin())
          .append(" 平:").append(leaguePoints.getHostEven()).append(" 负:").append(leaguePoints.getHostNega())
          .append(" 进:").append(leaguePoints.getHostGoals()).append(" 失:").append(leaguePoints.getHostLost())
          .append("\n");
        sb.append("客场成绩 ").append(" 赛:").append(leaguePoints.getVisitingMatch()).append(" 胜:").append(leaguePoints.getVisitingWin())
          .append(" 平:").append(leaguePoints.getVisitingEven()).append(" 负:").append(leaguePoints.getVisitingNega())
          .append(" 进:").append(leaguePoints.getVisitingGoals()).append(" 失:").append(leaguePoints.getVisitingLost())
          .append("\n");
        
        // 每场的情况;
        sb.append("\n联赛明细:\n");
        StringBuilder hostSb = new StringBuilder();
        StringBuilder visitingSb = new StringBuilder();
        for(Match match : matches){
        	// 查询技术统计;
        	Long okMatchId = match.getOkMatchId();
        	MatchStats matchStats = matchStatsService.queryMatchStatsById(okMatchId);
        	if(matchStats == null){
        		matchStats = initMatchStats();
        	}
        	
        	if(teamName.equalsIgnoreCase(match.getHostTeamName())){
        		String oppoName = match.getVisitingTeamName();
        		LeaguePoints oppoLeagPts = leaguePointsMap.get(oppoName);
        		if(oppoLeagPts == null){
        			oppoLeagPts = initLeaguePoints();
        		}
        		hostSb.append(match.getMatchTime()).append(" ").append(match.getHostTeamName()).append(" ").append(match.getVisitingTeamName())
          	      .append(" ").append(match.getHostGoals()).append(":").append(match.getVisitingGoals()).append(" 排名:").append(oppoLeagPts.getRank())
          	      .append(" 积分:").append(oppoLeagPts.getPoints()).append(" 总进:").append(oppoLeagPts.getTotalGoals()).append(" 总失:").append(oppoLeagPts.getTotalLost())
          	      .append(" 客进:").append(oppoLeagPts.getVisitingGoals()).append(" 客失:").append(oppoLeagPts.getVisitingLost())
          	      .append(" 射正:").append(matchStats.getShotsOnTarget()).append(" 射偏:").append(matchStats.getShotsOffTarget())
          	      .append(" 任意:").append(matchStats.getFreeKick()).append(" 角球:").append(matchStats.getCorners())
          	      .append(" 球门球:").append(matchStats.getGoalkeeperDist()).append(" 扑球:").append(matchStats.getBeatOutShot())
          	      .append(" 越位:").append(matchStats.getOffside()).append(" 犯规:").append(matchStats.getFoulCommitted())
          	      .append(" 控球率:").append(matchStats.getPossession())
          	      .append("\n");
        	}else if(teamName.equals(match.getVisitingTeamName())){
        		String oppoName = match.getHostTeamName();
        		LeaguePoints oppoLeagPts = leaguePointsMap.get(oppoName);
        		if(oppoLeagPts == null){
        			oppoLeagPts = initLeaguePoints();
        		}
        		visitingSb.append(match.getMatchTime()).append(" ").append(match.getHostTeamName()).append(" ").append(match.getVisitingTeamName())
        	      .append(" ").append(match.getHostGoals()).append(":").append(match.getVisitingGoals()).append(" 排名:").append(oppoLeagPts.getRank())
        	      .append(" 积分:").append(oppoLeagPts.getPoints()).append(" 总进:").append(oppoLeagPts.getTotalGoals()).append(" 总失:").append(oppoLeagPts.getTotalLost())
        	      .append(" 主进:").append(oppoLeagPts.getHostGoals()).append(" 主失:").append(oppoLeagPts.getHostLost())
        	      .append(" 射正:").append(matchStats.getShotsOnTarget()).append(" 射偏:").append(matchStats.getShotsOffTarget())
          	      .append(" 任意:").append(matchStats.getFreeKick()).append(" 角球:").append(matchStats.getCorners())
          	      .append(" 球门球:").append(matchStats.getGoalkeeperDist()).append(" 扑球:").append(matchStats.getBeatOutShot())
          	      .append(" 越位:").append(matchStats.getOffside()).append(" 犯规:").append(matchStats.getFoulCommitted())
          	      .append(" 控球率:").append(matchStats.getPossession())
        	      .append("\n");
        	}
        }
        sb.append("主场:\n").append(hostSb).append("\n客场:\n").append(visitingSb).append("\n");
        LOGGER.info(sb.toString());
        
	}
	
	private Map<String, LeaguePoints> transLeagPtsList2Map(List<LeaguePoints> leaguePointsList){
		if(leaguePointsList == null || leaguePointsList.isEmpty()){
			LOGGER.error("leaguePointsList is null or empty.");
			return null;
		}
		
		// key: {teamName} value: LeaguePoints
		Map<String, LeaguePoints> result = new HashMap<String, LeaguePoints>();
		for(LeaguePoints leaguePoints : leaguePointsList){
			String teamName = leaguePoints.getTeamName();
			result.put(teamName, leaguePoints);
		}
		return result;
	}
	
	private MatchStats initMatchStats(){
		MatchStats matchStats = new MatchStats();
		matchStats.setShotsOnTarget("0|0");
		matchStats.setShotsOffTarget("0|0");
		matchStats.setFreeKick("0|0");
		matchStats.setCorners("0|0");
		matchStats.setThrowIns("0|0");
		matchStats.setGoalkeeperDist("0|0");
		matchStats.setBeatOutShot("0|0");
		matchStats.setOffside("0|0");
		matchStats.setFoulCommitted("0|0");
		matchStats.setPossession("0|0");
		return matchStats;
	}
	
	private LeaguePoints initLeaguePoints(){
		LeaguePoints leaguePoints = new LeaguePoints();
		leaguePoints.setRank(0);
		leaguePoints.setTotalMatch(0);
		leaguePoints.setTotalWin(0);
		leaguePoints.setTotalEven(0);
		leaguePoints.setTotalNega(0);
		leaguePoints.setTotalGoals(0);
		leaguePoints.setTotalLost(0);
		leaguePoints.setTotalGoalsDiff(0);
		leaguePoints.setHostMatch(0);
		leaguePoints.setHostWin(0);
		leaguePoints.setHostEven(0);
		leaguePoints.setHostNega(0);
		leaguePoints.setHostGoals(0);
		leaguePoints.setHostLost(0);
		leaguePoints.setVisitingMatch(0);
		leaguePoints.setVisitingWin(0);
		leaguePoints.setVisitingEven(0);
		leaguePoints.setVisitingNega(0);
		leaguePoints.setVisitingGoals(0);
		leaguePoints.setVisitingLost(0);
		return leaguePoints;
	}
	
	public SingleMatchService getSingleMatchService() {
		return singleMatchService;
	}

	public void setSingleMatchService(SingleMatchService singleMatchService) {
		this.singleMatchService = singleMatchService;
	}

	public LeaguePointsService getLeaguePointsService() {
		return leaguePointsService;
	}

	public void setLeaguePointsService(LeaguePointsService leaguePointsService) {
		this.leaguePointsService = leaguePointsService;
	}

	public MatchStatsService getMatchStatsService() {
		return matchStatsService;
	}

	public void setMatchStatsService(MatchStatsService matchStatsService) {
		this.matchStatsService = matchStatsService;
	}

}
