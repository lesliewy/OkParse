/**
 * 
 */
package com.wy.okooo.service.impl;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.wy.okooo.service.AnalyseCorpUtilService;
import com.wy.okooo.service.EuroOddsChangeService;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * @author leslie
 *
 */
public class AnalyseCorpUtilServiceImpl implements AnalyseCorpUtilService {

	private static Logger LOGGER = Logger.getLogger(AnalyseCorpUtilServiceImpl.class
			.getName());
	
	private EuroOddsChangeService euroOddsChangeService;
	
	/**
	 * 指定公司的数据插入 LOT_ODDS_EURO_CHANGE_ALL, 记录的是所有的变化数据，不限制数量.
	 * @param corpNo
	 */
	public void persistEuroOddsChangeAll(File dir, String corpName) {
		Integer corpNo = OkParseUtils.translateCorpNo(corpName);
		String baseName = OkConstant.EURO_ODDS_CHANGE_FILE_NAME_BASE + "_" + corpNo + "_";
		Integer numOfSeqs = 2000;
		List<File> files = OkParseUtils.getFilesFromDir(dir, baseName);
		if(files == null){
			LOGGER.info("files is null, return now.");
			return;
		}
		
		String okUrlDate = OkParseUtils.getOkUrlDateFromFile(new File(dir.getAbsolutePath() + File.separatorChar + "match.html"));
		for(File file : files){
			Integer matchSeq = Integer.valueOf(file.getName().split("_")[2].replace(".html", ""));
			euroOddsChangeService.parseEuroOddsChangeAllFromFile(file, numOfSeqs, corpName, okUrlDate, matchSeq);
		}
		
	}

	public EuroOddsChangeService getEuroOddsChangeService() {
		return euroOddsChangeService;
	}

	public void setEuroOddsChangeService(EuroOddsChangeService euroOddsChangeService) {
		this.euroOddsChangeService = euroOddsChangeService;
	}

}
