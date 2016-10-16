/**
 * 
 */
package com.wy.okooo.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.wy.okooo.domain.ScoreOdds;

/**
 * @author leslie
 *
 */
public interface ScoreOddsService {
	void insertScoreOdds(ScoreOdds scoreOdds);
	
	void insertScoreOddsBatch(List<ScoreOdds> scoreOddsList);
	
	void deleteScoreOdds(Long okMatchId, String intervalType);
	
	List<ScoreOdds> getScoreOddsFromFile(File scoreOddsFile, Map<Integer, String> intervalTypeMap);
	
	void deleteScoreOdds(List<ScoreOdds> deletedScoreOddsList);
	
	List<ScoreOdds> queryScoreOddsByOkUrlDate(String okUrlDate);
}
