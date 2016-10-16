package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.LeaguePointsDao;
import com.wy.okooo.domain.LeaguePoints;

public class LeaguePointsDaoImpl extends SqlMapClientDaoSupport implements LeaguePointsDao {

	private static Logger LOGGER = Logger.getLogger(LeaguePointsDaoImpl.class
			.getName());

	public void insertLeaguePoints(LeaguePoints leaguePoints) {
		if (leaguePoints == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertLeaguePoints", leaguePoints);
		}catch (Exception e){
			LOGGER.error(e);
		}
	}

	public void insertLeaguePointsBatch(List<LeaguePoints> leaguePointsList) {
		if (leaguePointsList == null) {
			LOGGER.error("leaguePointsList is null, return.");
			return;
		}
		for (LeaguePoints leaguePoints : leaguePointsList) {
			insertLeaguePoints(leaguePoints);
		}
	}
	
	public void deleteLeaguePointsByLeague(LeaguePoints leaguePoints) {
		getSqlMapClientTemplate().delete("deleteLeaguePointsByLeague", leaguePoints);
	}
	
	public LeaguePoints queryLatestLeagPtsByTeamName(String teamName) {
		if(StringUtils.isBlank(teamName)){
			LOGGER.error("teamName is blank, return now.");
			return null;
		}
		return (LeaguePoints) getSqlMapClientTemplate().queryForObject("queryLatestLeagPtsByTeamName", teamName);
	}

	@SuppressWarnings("unchecked")
	public List<LeaguePoints> queryLatestLeagPtsByLeagueName(
			LeaguePoints leaguePoints) {
		if (leaguePoints == null) {
			LOGGER.error("leaguePoints is null, return.");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryLatestLeagPtsByLeagueName", leaguePoints);
	}

}
