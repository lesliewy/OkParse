package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.LeaguePoints;

/**
 * 联赛积分DAO.(http://www.okooo.com/soccer/league/17/)
 * 
 * @author leslie
 *
 */
public interface LeaguePointsDao {
	
	void insertLeaguePoints(LeaguePoints leaguePoints);
	
	void insertLeaguePointsBatch(List<LeaguePoints> LeaguePointsList);
	
	void deleteLeaguePointsByLeague(LeaguePoints leaguePoints);
	
	LeaguePoints queryLatestLeagPtsByTeamName(String teamName);
	
	List<LeaguePoints> queryLatestLeagPtsByLeagueName(LeaguePoints leaguePoints);
	
}
