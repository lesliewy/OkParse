/**
 * 
 */
package com.wy.okooo.parse;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.wy.okooo.domain.ScoreOdds;

/**
 * 比分.(http://www.okooo.com/danchang/bifen/)
 * 
 * @author leslie
 *
 */
public interface ParseScoreOdds {
	
	List<ScoreOdds> getScoreOddsFromFile(File scoreOddsFile, Map<Integer, String> intervalTypeMap);
}
