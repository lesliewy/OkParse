/**
 * 
 */
package com.wy.okooo.parse;

import java.io.File;
import java.util.List;

import com.wy.okooo.domain.Match;

/**
 * 单场页面解析.(http://www.okooo.com/danchang/)
 * 
 * @author leslie
 *
 */
public interface ParseMatches {

	List<Match> getAllMatchFromUrl(int beginMatchSeq, int endMatchSeq);
	
	List<Match> getAllMatchFromUrl(String url, int beginMatchSeq, int endMatchSeq);
	
	List<Match> getAllMatchFromFile(File matchHtmlFile, int beginMatchSeq, int endMatchSeq);
}
