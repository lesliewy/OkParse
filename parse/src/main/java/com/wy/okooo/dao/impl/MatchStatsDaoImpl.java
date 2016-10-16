package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.MatchStatsDao;
import com.wy.okooo.domain.MatchStats;

public class MatchStatsDaoImpl extends SqlMapClientDaoSupport implements MatchStatsDao {

	private static Logger LOGGER = Logger.getLogger(MatchStatsDaoImpl.class
			.getName());

	public void insertMatchStats(MatchStats matchStats) {
		if (matchStats == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertMatchStats", matchStats);
		}catch (Exception e){
			LOGGER.error(e);
		}
	}

	public void deleteMatchStats(Long okMatchId) {
		getSqlMapClientTemplate().delete("deleteMatchStats", okMatchId);
	}

	public void insertMatchStatsBatch(List<MatchStats> matchStatsList) {
		if (matchStatsList == null) {
			LOGGER.error("matchStatsList is null, return.");
			return;
		}
		for (MatchStats matchStats : matchStatsList) {
			insertMatchStats(matchStats);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<MatchStats> queryOkUrlDateFromMatchStats() {
		return getSqlMapClientTemplate().queryForList("queryOkUrlDateFromMatchStats");
	}

	public MatchStats queryMatchStatsById(Long okMatchId) {
		if(okMatchId == null){
			LOGGER.error("okMatchId is null");
			return null;
		}
		return (MatchStats) getSqlMapClientTemplate().queryForObject("queryMatchStatsById", okMatchId);
	}

}
