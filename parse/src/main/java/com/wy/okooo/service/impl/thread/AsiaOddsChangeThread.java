/**
 * 
 */
package com.wy.okooo.service.impl.thread;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.wy.okooo.service.AsiaOddsChangeService;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * @author leslie
 *
 */
public class AsiaOddsChangeThread implements Runnable {

	private static Logger LOGGER = Logger.getLogger(AsiaOddsChangeThread.class
			.getName());
	
	private List<File> matchHtmlFiles = null;
	
	private AsiaOddsChangeService asiaOddsChangeService;
	
	public void run() {
		long parseAsiaOddsChangeBegin = System.currentTimeMillis();
		List<File> asiaOddsChangeHtmls = null;
		for (File matchHtmlFile : matchHtmlFiles) {
			asiaOddsChangeHtmls = OkParseUtils.getSameDirFilesFromMatch(
					matchHtmlFile, OkConstant.ASIA_ODDS_CHANGE_FILE_NAME_BASE);
			for (File asiaOddsChangeHtml : asiaOddsChangeHtmls) {
				LOGGER.info("process asiaOddsChangeHtml: " + asiaOddsChangeHtml.getAbsolutePath());
				asiaOddsChangeService
						.parseAsiaOddsChangeFromFile(asiaOddsChangeHtml);
			}
		}
		LOGGER.info("progress: insert LOT_ODDS_ASIA_CHANGE success, eclipsed "
				+ (System.currentTimeMillis() - parseAsiaOddsChangeBegin)
				+ " ms...");
	}

	public List<File> getMatchHtmlFiles() {
		return matchHtmlFiles;
	}

	public void setMatchHtmlFiles(List<File> matchHtmlFiles) {
		this.matchHtmlFiles = matchHtmlFiles;
	}

	public AsiaOddsChangeService getAsiaOddsChangeService() {
		return asiaOddsChangeService;
	}

	public void setAsiaOddsChangeService(AsiaOddsChangeService asiaOddsChangeService) {
		this.asiaOddsChangeService = asiaOddsChangeService;
	}

}
