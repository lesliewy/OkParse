/**
 * 
 */
package com.wy.okooo.service.impl;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.wy.okooo.dao.MatchDao;
import com.wy.okooo.domain.Match;
import com.wy.okooo.parse.ParseMatches;
import com.wy.okooo.parse.impl.ParseMatchesImpl;
import com.wy.okooo.service.SingleMatchService;

/**
 * 单场胜平负页面service(http://www.okooo.com/danchang/)
 * 
 * @author leslie
 *
 */
public class SingleMatchServiceImpl implements SingleMatchService {

	private MatchDao matchDao;
	
	private ParseMatches parser = new ParseMatchesImpl();

	public List<Match> getAllMatchFromUrl(String url, int beginMatchSeq, int endMatchSeq) {
		return parser.getAllMatchFromUrl(url, beginMatchSeq, endMatchSeq);
	}
	
	public List<Match> getAllMatchFromFile(File matchHtmlFile, int beginMatchSeq, int endMatchSeq) {
		return parser.getAllMatchFromFile(matchHtmlFile, beginMatchSeq, endMatchSeq);
	}
	
	public List<Match> getAllMatchFromFiles(List<File> matchHtmlFiles, int beginMatchSeq, int endMatchSeq){
		List<Match> matches = new ArrayList<Match>(2 ^ 10);
		List<Match> oneMatchHtml = new ArrayList<Match>();
		for (File matchHtmlFile : matchHtmlFiles) {
			oneMatchHtml = getAllMatchFromFile(matchHtmlFile, beginMatchSeq, endMatchSeq);
			matches.addAll(oneMatchHtml);
		}
		return matches;
	}


	public void insertMatchBatch(List<Match> matches) {
		matchDao.insertMatchBatch(matches);
	}

	public List<Match> queryMatchesByIdRange(int numOfMatch) {
		return matchDao.queryMatchesByIdRange(numOfMatch);
	}

	public List<Match> queryExistedMatchesByTime(Timestamp beginTime,
			Timestamp endTime) {
		return matchDao.queryExistedMatchesByTime(beginTime, endTime);
	}
	
	public List<Match> queryMatchesByHostTeamName(String hostTeamName) {
		return matchDao.queryMatchesByHostTeamName(hostTeamName);
	}
	
	public List<Match> queryMatchesByVisitingTeamName(String visitingTeamName) {
		return matchDao.queryMatchesByVisitingTeamName(visitingTeamName);
	}
	
	public List<Match> queryOkUrlDateFromMatch(String okUrlDate) {
		return matchDao.queryOkUrlDateFromMatch(okUrlDate);
	}
	
	public List<Match> queryMatchRangeByMatchTeamName(Match match) {
		return matchDao.queryMatchRangeByMatchTeamName(match);
	}
	
	public MatchDao getMatchDao() {
		return matchDao;
	}

	public void setMatchDao(MatchDao matchDao) {
		this.matchDao = matchDao;
	}

}
