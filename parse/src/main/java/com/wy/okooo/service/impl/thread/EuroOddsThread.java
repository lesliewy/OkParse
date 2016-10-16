/**
 * 
 */
package com.wy.okooo.service.impl.thread;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.wy.okooo.service.EuroOddsService;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * @author leslie
 *
 */
public class EuroOddsThread implements Runnable {

	private static Logger LOGGER = Logger.getLogger(EuroOddsThread.class
			.getName());
	
	private List<File> matchHtmlFiles = null;
	
	private EuroOddsService euroOddsService;
	
	public void run() {
		long parseEuroOddsBegin = System.currentTimeMillis();
		List<File> euroOddsHtmls = null;
		for (File matchHtmlFile : matchHtmlFiles) {
			euroOddsHtmls = OkParseUtils.getSameDirFilesFromMatch(
					matchHtmlFile, OkConstant.EURO_ODDS_FILE_NAME_BASE);
			for (File euroOddsHtml : euroOddsHtmls) {
				LOGGER.info("process euroOddsHtml: " + euroOddsHtml.getAbsolutePath());
				String okUrlDate = OkParseUtils.getOkUrlDateFromFile(euroOddsHtml);
				Integer matchSeq = OkParseUtils.getMatchSeqFromOddsFile(euroOddsHtml);
				euroOddsService.parseEuroOddsFromFile(euroOddsHtml, 0, okUrlDate, matchSeq);
			}
		}
		LOGGER.info("progress: insert LOT_ODDS_EURO success, eclipsed "
				+ (System.currentTimeMillis() - parseEuroOddsBegin) + " ms...");
	}

	public List<File> getMatchHtmlFiles() {
		return matchHtmlFiles;
	}

	public void setMatchHtmlFiles(List<File> matchHtmlFiles) {
		this.matchHtmlFiles = matchHtmlFiles;
	}

	public EuroOddsService getEuroOddsService() {
		return euroOddsService;
	}

	public void setEuroOddsService(EuroOddsService euroOddsService) {
		this.euroOddsService = euroOddsService;
	}

}
