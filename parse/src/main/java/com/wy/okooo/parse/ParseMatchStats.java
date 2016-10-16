/**
 * 
 */
package com.wy.okooo.parse;

import java.io.File;

import com.wy.okooo.domain.MatchStats;

/**
 * 球员阵容(技术统计)解析.(http://www.okooo.com/soccer/match/768266/)
 * 
 * @author leslie
 */
public interface ParseMatchStats {
	
	MatchStats getMatchStatsFromFile(File matchStatsFile);
	
}
