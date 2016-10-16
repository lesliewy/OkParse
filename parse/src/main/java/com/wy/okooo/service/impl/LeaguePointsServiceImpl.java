/**
 * 
 */
package com.wy.okooo.service.impl;

import java.io.File;
import java.util.List;

import com.wy.okooo.dao.LeaguePointsDao;
import com.wy.okooo.domain.LeaguePoints;
import com.wy.okooo.parse.ParseLeaguePoints;
import com.wy.okooo.parse.impl.ParseLeaguePointsImpl;
import com.wy.okooo.service.LeaguePointsService;

/**
 * 联赛积分
 * 
 * @author leslie
 *
 */
public class LeaguePointsServiceImpl implements LeaguePointsService {

	private LeaguePointsDao leaguePointsDao;
	
	private ParseLeaguePoints parser = new ParseLeaguePointsImpl();

	public void insertLeaguePoints(LeaguePoints leaguePoints) {
		leaguePointsDao.insertLeaguePoints(leaguePoints);
	}

	public void insertLeaguePointsBatch(List<LeaguePoints> LeaguePointsList) {
		leaguePointsDao.insertLeaguePointsBatch(LeaguePointsList);
	}

	public void deleteLeaguePointsByLeague(LeaguePoints leaguePoints) {
		leaguePointsDao.deleteLeaguePointsByLeague(leaguePoints);
	}
	
	public List<String> getLeaguePointsUrl(File allLeagueFile){
		return parser.getLeaguePointsUrl(allLeagueFile);
	}
	
	public List<LeaguePoints> getLeaguePointsFromFile(File leaguePointsFile) {
		return parser.getLeaguePointsFromFile(leaguePointsFile);
	}

	public LeaguePoints queryLatestLeagPtsByTeamName(String teamName) {
		return leaguePointsDao.queryLatestLeagPtsByTeamName(teamName);
	}
	
	public List<LeaguePoints> queryLatestLeagPtsByLeagueName(
			LeaguePoints leaguePoints) {
		return leaguePointsDao.queryLatestLeagPtsByLeagueName(leaguePoints);
	}
	
	public LeaguePointsDao getLeaguePointsDao() {
		return leaguePointsDao;
	}

	public void setLeaguePointsDao(LeaguePointsDao leaguePointsDao) {
		this.leaguePointsDao = leaguePointsDao;
	}

}
