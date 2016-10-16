/**
 * 
 */
package com.wy.okooo.service.impl.thread;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.wy.okooo.service.ExchangeService;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * @author leslie
 *
 */
public class TurnoverDetailThread implements Runnable {

	private static Logger LOGGER = Logger.getLogger(TurnoverDetailThread.class
			.getName());
	
	private List<File> matchHtmlFiles = null;
	
	private ExchangeService exchangeService;
	
	public void run() {
		long parseTurnoverDetail = System.currentTimeMillis();
		List<File> turnoverDetailHtmls = null;
		for (File matchHtmlFile : matchHtmlFiles) {
			turnoverDetailHtmls = OkParseUtils.getSameDirFilesFromMatch(
					matchHtmlFile, OkConstant.TURNOVER_DETAIL_FILE_NAME);
			for (File turnoverDetailHtml : turnoverDetailHtmls) {
				LOGGER.info("process turnoverDetailHtml: " + turnoverDetailHtml.getAbsolutePath());
				exchangeService
						.parseBfTurnoverDetailFromFile(turnoverDetailHtml);
			}
		}
		LOGGER.info("progress: insert LOT_BF_TURNOVER_DETAIL success, eclipsed "
				+ (System.currentTimeMillis() - parseTurnoverDetail) + " ms...");
	}

	public List<File> getMatchHtmlFiles() {
		return matchHtmlFiles;
	}

	public void setMatchHtmlFiles(List<File> matchHtmlFiles) {
		this.matchHtmlFiles = matchHtmlFiles;
	}

	public ExchangeService getExchangeService() {
		return exchangeService;
	}

	public void setExchangeService(ExchangeService exchangeService) {
		this.exchangeService = exchangeService;
	}

}
