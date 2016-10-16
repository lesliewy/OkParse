/**
 * 
 */
package com.wy.okooo.service;

import java.io.File;

/**
 * @author leslie
 *
 */
public interface AllSingleMatchService {
	
	void parseAllMatch(String url);
	
	void parseAllMatch();
	
	void parseAllMatchThread();
	
	void parseEuroOddsThread();
	
	void parseSingleMatch(String baseDir);
	
	void parseExchangeInfo(String baseDir);
	
	void parseEuroOddsFromFile(File dir);
}
