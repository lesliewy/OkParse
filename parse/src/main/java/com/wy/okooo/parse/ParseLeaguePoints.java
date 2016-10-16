/**
 * 
 */
package com.wy.okooo.parse;

import java.io.File;
import java.util.List;

import com.wy.okooo.domain.LeaguePoints;

/**
 * 赛事一览解析.(http://www.okooo.com/soccer/league/34/)
 * 
 * @author leslie
 *
 */
public interface ParseLeaguePoints {
	
	List<String> getLeaguePointsUrl(File allLeagueFile);
	
	List<LeaguePoints> getLeaguePointsFromFile(File leaguePointsFile);
}
