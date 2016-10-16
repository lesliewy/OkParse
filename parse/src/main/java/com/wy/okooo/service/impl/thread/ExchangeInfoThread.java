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
public class ExchangeInfoThread implements Runnable {

	private static Logger LOGGER = Logger.getLogger(ExchangeInfoThread.class
			.getName());
	
	private List<File> matchHtmlFiles = null;
	
	private ExchangeService exchangeService;
	
	public void run() {
		long parseExchangeInfo = System.currentTimeMillis();
		List<File> exchangeInfoHtmls = null;
		for (File matchHtmlFile : matchHtmlFiles) {
			exchangeInfoHtmls = OkParseUtils.getSameDirFilesFromMatch(
					matchHtmlFile, OkConstant.EXCHANGE_INFO_FILE_NAME_BASE);
			for (File exchangeInfoHtml : exchangeInfoHtmls) {
				LOGGER.info("process exchangeInfoHtml: " + exchangeInfoHtml.getAbsolutePath());
				exchangeService.parseExchangeInfoFromFile(exchangeInfoHtml);
			}
		}
		LOGGER.info("progress: insert LOT_ALL_AVERAGE LOT_BF_LISTING LOT_TRANS_PROP, success, eclipsed "
				+ (System.currentTimeMillis() - parseExchangeInfo) + " ms...");

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
