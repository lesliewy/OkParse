/**
 * 
 */
package com.wy.okooo.service.impl;

import java.io.File;
import java.util.List;

import com.wy.okooo.dao.MatchStatsDao;
import com.wy.okooo.domain.MatchStats;
import com.wy.okooo.parse.ParseMatchStats;
import com.wy.okooo.parse.impl.ParseMatchStatsImpl;
import com.wy.okooo.service.MatchStatsService;

/**
 * @author leslie
 *
 */
public class MatchStatsServiceImpl implements MatchStatsService {

	private MatchStatsDao matchStatsDao;
	
	private ParseMatchStats parser = new ParseMatchStatsImpl();

	public void insertMatchStats(MatchStats matchStats) {
		matchStatsDao.insertMatchStats(matchStats);
	}

	public void insertMatchStatsBatch(List<MatchStats> matchStatsList) {
		matchStatsDao.insertMatchStatsBatch(matchStatsList);
	}

	public void deleteMatchStats(Long okMatchId) {
		matchStatsDao.deleteMatchStats(okMatchId);
	}
	
	public List<MatchStats> queryOkUrlDateFromMatchStats() {
		return matchStatsDao.queryOkUrlDateFromMatchStats();
	}
	
	public MatchStats queryMatchStatsById(Long okMatchId) {
		return matchStatsDao.queryMatchStatsById(okMatchId);
	}
	
	public MatchStats getMatchStatsFromFile(File matchStatsFile) {
		return parser.getMatchStatsFromFile(matchStatsFile);
	}

	public MatchStatsDao getMatchStatsDao() {
		return matchStatsDao;
	}

	public void setMatchStatsDao(MatchStatsDao matchStatsDao) {
		this.matchStatsDao = matchStatsDao;
	}

}
