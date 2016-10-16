/**
 * 
 */
package com.wy.okooo.service.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.wy.okooo.dao.ScoreOddsDao;
import com.wy.okooo.domain.ScoreOdds;
import com.wy.okooo.parse.ParseScoreOdds;
import com.wy.okooo.parse.impl.ParseScoreOddsImpl;
import com.wy.okooo.service.ScoreOddsService;

/**
 * 
 * @author leslie
 * 
 */
public class ScoreOddsServiceImpl implements ScoreOddsService {

	private ScoreOddsDao scoreOddsDao;
	
	private ParseScoreOdds parseScoreOdds = new ParseScoreOddsImpl();
	
	public void insertScoreOdds(ScoreOdds scoreOdds) {
		scoreOddsDao.insertScoreOdds(scoreOdds);
	}

	public void insertScoreOddsBatch(List<ScoreOdds> scoreOddsList) {
		scoreOddsDao.insertScoreOddsBatch(scoreOddsList);
	}

	public void deleteScoreOdds(Long okMatchId, String intervalType) {
		scoreOddsDao.deleteScoreOdds(okMatchId, intervalType);
	}
	
	public void deleteScoreOdds(List<ScoreOdds> deletedScoreOddsList) {
		scoreOddsDao.deleteScoreOdds(deletedScoreOddsList);
	}
	
	public List<ScoreOdds> getScoreOddsFromFile(File scoreOddsFile, Map<Integer, String> intervalTypeMap){
		return parseScoreOdds.getScoreOddsFromFile(scoreOddsFile, intervalTypeMap);
	}
	
	public List<ScoreOdds> queryScoreOddsByOkUrlDate(String okUrlDate) {
		return scoreOddsDao.queryScoreOddsByOkUrlDate(okUrlDate);
	}

	public ScoreOddsDao getScoreOddsDao() {
		return scoreOddsDao;
	}

	public void setScoreOddsDao(ScoreOddsDao scoreOddsDao) {
		this.scoreOddsDao = scoreOddsDao;
	}

}
