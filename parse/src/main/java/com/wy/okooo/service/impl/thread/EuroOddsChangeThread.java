/**
 * 
 */
package com.wy.okooo.service.impl.thread;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.wy.okooo.service.EuroOddsChangeService;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * @author leslie
 *
 */
public class EuroOddsChangeThread implements Runnable {

	private static Logger LOGGER = Logger.getLogger(EuroOddsChangeThread.class
			.getName());
	
	private List<File> matchHtmlFiles = null;
	
	private EuroOddsChangeService euroOddsChangeService;
	
	public void run() {
		long parseEuroOddsChangeBegin = System.currentTimeMillis();
		List<File> euroOddsChangeHtmls = null;
		for (File matchHtmlFile : matchHtmlFiles) {
			euroOddsChangeHtmls = OkParseUtils.getSameDirFilesFromMatch(
					matchHtmlFile, OkConstant.EURO_ODDS_CHANGE_FILE_NAME_BASE);
			for (File euroOddsChangeHtml : euroOddsChangeHtmls) {
				LOGGER.info("process euroOddsChangeHtml: " + euroOddsChangeHtml.getAbsolutePath());
				euroOddsChangeService
						.parseEuroOddsChangeFromFile(euroOddsChangeHtml, 0, false);
			}
		}
		LOGGER.info("progress: insert LOT_ODDS_EURO_CHANGE success, eclipsed "
				+ (System.currentTimeMillis() - parseEuroOddsChangeBegin)
				+ " ms...");
	}

	public List<File> getMatchHtmlFiles() {
		return matchHtmlFiles;
	}

	public void setMatchHtmlFiles(List<File> matchHtmlFiles) {
		this.matchHtmlFiles = matchHtmlFiles;
	}

	public EuroOddsChangeService getEuroOddsChangeService() {
		return euroOddsChangeService;
	}

	public void setEuroOddsChangeService(EuroOddsChangeService euroOddsChangeService) {
		this.euroOddsChangeService = euroOddsChangeService;
	}

}
