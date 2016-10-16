/**
 * 
 */
package com.wy.okooo.service;

import java.io.File;

/**
 * 分析公司的情况.
 * 
 * @author leslie
 *
 */
public interface AnalyseCorpUtilService {
	
	void persistEuroOddsChangeAll(File dir, String oddsCorpName);
	
}
