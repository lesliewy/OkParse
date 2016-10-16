/**
 * 
 */
package com.wy.okooo.service;

import java.io.File;
import java.util.List;

import com.wy.okooo.domain.LeaguePoints;

/**
 * 联赛积分
 * 
 * @author leslie
 *
 */
public interface LeaguePointsService {
	
	void insertLeaguePoints(LeaguePoints leaguePoints);
	
	void insertLeaguePointsBatch(List<LeaguePoints> LeaguePointsList);
	
	void deleteLeaguePointsByLeague(LeaguePoints leaguePoints);
	
	List<String> getLeaguePointsUrl(File allLeagueFile);
	
	List<LeaguePoints> getLeaguePointsFromFile(File leaguePointsFile);
	
	LeaguePoints queryLatestLeagPtsByTeamName(String teamName);
	
	List<LeaguePoints> queryLatestLeagPtsByLeagueName(LeaguePoints leaguePoints);
	
}
