/**
 * 
 */
package com.wy.okooo.service;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import com.wy.okooo.domain.Match;

/**
 * 单场胜平负页面service(http://www.okooo.com/danchang/)
 * 
 * @author leslie
 *
 */
public interface SingleMatchService {
	List<Match> getAllMatchFromUrl(String url, int beginMatchSeq, int endMatchSeq);
	
	List<Match> getAllMatchFromFile(File matchHtmlFile, int beginMatchSeq, int endMatchSeq);
	
	List<Match> getAllMatchFromFiles(List<File> matchHtmlFiles, int beginMatchSeq, int endMatchSeq);
	
	void insertMatchBatch(List<Match> matches);
	
	List<Match> queryMatchesByIdRange(int numOfMatch);
	
	List<Match> queryExistedMatchesByTime(Timestamp beginTime, Timestamp endTime);
	
	List<Match> queryMatchesByHostTeamName(String hostTeamName);
	
	List<Match> queryMatchesByVisitingTeamName(String visitingTeamName);
	
	List<Match> queryOkUrlDateFromMatch(String okUrlDate);
	
	List<Match> queryMatchRangeByMatchTeamName(Match match);
}
