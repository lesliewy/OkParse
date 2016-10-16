package com.wy.okooo.dao;

import java.sql.Timestamp;
import java.util.List;

import com.wy.okooo.domain.Match;

/**
 * 单场DAO.(http://www.okooo.com/danchang/)
 * 
 * @author leslie
 *
 */
public interface MatchDao {
	void insertMatch(Match match);
	
	void insertMatchBatch(List<Match> matches);
	
	List<Match> queryMatchesByIdRange(int numOfMatch);
	
	void deleteMatch(long id);
	
	List<Match> queryExistedMatchesByTime(Timestamp beginTime, Timestamp endTime);
	
	List<Match> queryMatchesByHostTeamName(String hostTeamName);
	
	List<Match> queryMatchesByVisitingTeamName(String visitingTeamName);
	
	List<Match> queryOkUrlDateFromMatch(String okUrlDate);
	
	List<Match> queryMatchRangeByMatchTeamName(Match match);
}
