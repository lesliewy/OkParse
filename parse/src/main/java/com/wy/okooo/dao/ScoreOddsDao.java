package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.ScoreOdds;

/**
 * 比分赔率DAO.(http://www.okooo.com/danchang/bifen/)
 * 
 * @author leslie
 *
 */
public interface ScoreOddsDao {
	void insertScoreOdds(ScoreOdds scoreOdds);
	
	void insertScoreOddsBatch(List<ScoreOdds> scoreOddsList);
	
	void deleteScoreOdds(Long okMatchId, String intervalType);
	
	void deleteScoreOdds(List<ScoreOdds> deletedScoreOddsList);
	
	List<ScoreOdds> queryScoreOddsByOkUrlDate(String okUrlDate);
}
