/**
 * 
 */
package com.wy.okooo.service.impl.thread;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.wy.okooo.service.AsiaOddsService;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * @author leslie
 *
 */
public class AsiaOddsThread implements Runnable {

	private static Logger LOGGER = Logger.getLogger(AsiaOddsThread.class
			.getName());
	
	private List<File> matchHtmlFiles = null;
	
	private AsiaOddsService asiaOddsService;
	
	public void run() {
		long parseAsiaOddsBegin = System.currentTimeMillis();
		List<File> asiaOddsHtmls = null;
		for (File matchHtmlFile : matchHtmlFiles) {
			asiaOddsHtmls = OkParseUtils.getSameDirFilesFromMatch(
					matchHtmlFile, OkConstant.ASIA_ODDS_FILE_NAME_BASE);
			for (File asiaOddsHtml : asiaOddsHtmls) {
				LOGGER.info("process asiaOddsHtml: " + asiaOddsHtml.getAbsolutePath());
				asiaOddsService.parseAsiaOddsFromFile(asiaOddsHtml);
			}
		}
		LOGGER.info("progress: insert LOT_ODDS_ASIA success, eclipsed "
				+ (System.currentTimeMillis() - parseAsiaOddsBegin) + " ms...");
	}

	public List<File> getMatchHtmlFiles() {
		return matchHtmlFiles;
	}

	public void setMatchHtmlFiles(List<File> matchHtmlFiles) {
		this.matchHtmlFiles = matchHtmlFiles;
	}

	public AsiaOddsService getAsiaOddsService() {
		return asiaOddsService;
	}

	public void setAsiaOddsService(AsiaOddsService asiaOddsService) {
		this.asiaOddsService = asiaOddsService;
	}

}
