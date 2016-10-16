package com.wy.okooo.dao.impl;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.MatchDao;
import com.wy.okooo.domain.Match;

public class MatchDaoImpl extends SqlMapClientDaoSupport implements MatchDao {

	private static Logger LOGGER = Logger.getLogger(MatchDaoImpl.class
			.getName());

	public void insertMatch(Match match) {
		if (match == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertMatch", match);
		}catch (Exception e){
			LOGGER.error(e);
		}
	}

	public void deleteMatch(long id) {
		getSqlMapClientTemplate().delete("deleteMatch", id);
	}

	public void insertMatchBatch(List<Match> matches) {
		if (matches == null) {
			LOGGER.error("matches is null, return.");
			return;
		}
		for (Match match : matches) {
			insertMatch(match);
		}
	}

	/**
	 * 查询 LOT_MATCH 中的新增记录.
	 * 
	 * @param numOfMatch
	 *            LOT_MATCH中新增的个数;
	 */
	@SuppressWarnings("unchecked")
	public List<Match> queryMatchesByIdRange(int numOfMatch) {
		return getSqlMapClientTemplate().queryForList("queryMatchesByIdRange",
				numOfMatch);
	}

	@SuppressWarnings("unchecked")
	public List<Match> queryExistedMatchesByTime(Timestamp beginTime,
			Timestamp endTime) {
		Match match = new Match();
		match.setBeginTime(beginTime);
		match.setEndTime(endTime);
		return getSqlMapClientTemplate().queryForList(
				"queryExistedMatchesByTime", match);
	}

	@SuppressWarnings("unchecked")
	public List<Match> queryMatchesByHostTeamName(String hostTeamName) {
		if(StringUtils.isBlank(hostTeamName)){
			return null;
		}
		return getSqlMapClientTemplate().queryForList(
				"queryMatchesByHostTeamName", hostTeamName);
	}

	@SuppressWarnings("unchecked")
	public List<Match> queryMatchesByVisitingTeamName(String visitingTeamName) {
		if(StringUtils.isBlank(visitingTeamName)){
			return null;
		}
		return getSqlMapClientTemplate().queryForList(
				"queryMatchesByVisitingTeamName", visitingTeamName);
	}

	@SuppressWarnings("unchecked")
	public List<Match> queryOkUrlDateFromMatch(String okUrlDate) {
		if(StringUtils.isBlank(okUrlDate)){
			LOGGER.error("okUrlDate is blank, return now.");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryOkUrlDateFromMatch", okUrlDate);
	}
	
	@SuppressWarnings("unchecked")
	public List<Match> queryMatchRangeByMatchTeamName(Match match) {
		if(match == null){
			LOGGER.error("match is null, return now.");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryMatchRangeByMatchTeamName", match);
	}
}
