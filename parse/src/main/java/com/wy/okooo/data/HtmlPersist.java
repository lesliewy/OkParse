/**
 * 
 */
package com.wy.okooo.data;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.web.util.UriUtils;

import com.wy.okooo.domain.AsiaOdds;
import com.wy.okooo.domain.Match;
import com.wy.okooo.parse.impl.ParseOkoooUrl;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * 搜集数据, 存储在本地磁盘.
 * 
 * @author leslie
 * 
 */
public class HtmlPersist {

	// log4j
	private static Logger LOGGER = Logger
			.getLogger(HtmlPersist.class.getName());

	private static ParseOkoooUrl parseOkoooUrl = new ParseOkoooUrl();

	private static final String AJAX_DATA_FLAG = "<tr id=\"filterTips\"  class=\"fTrObj\"><td colspan=\"17\">数据加载中...</td></tr>";

	private int MAX_FILE_NUM = 32;
	
	private static final int MAX_MATCH_SEQ = 600;
	
	public void persistAll(Calendar cal, int beginMatchSeq, int endMatchSeq){
		long begin = System.currentTimeMillis();
		persistMatchBatch(cal);
		String dir = OkConstant.FILE_PATH_BASE;
		String month = StringUtils.leftPad(
				String.valueOf(cal.get(Calendar.MONTH) + 1), 2, "0");
		String dayOfMonth = StringUtils.leftPad(
				String.valueOf(cal.get(Calendar.DAY_OF_MONTH)), 2, "0");
		dir = dir + cal.get(Calendar.YEAR) + "/" + month + "/" + dayOfMonth;
		persistEuroOddsBatch(dir);
		persistAsiaOddsBatch(dir);
//		persistEuroOddsChangeBatch(dir);
		persistAllCorpEuroOddsChange(dir);
		persistAsiaOddsChangeBatch(dir);
		persistExchangeInfoBatch(dir, beginMatchSeq, endMatchSeq, null, false);
		persistTurnoverDetailBatch(dir);
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/(1000*60) + " min.");
	}
	
	/**
	 * 线程方式, 适合于获取某天(当天)的情况.
	 * @param cal
	 */
	public void persistAllThread(Calendar cal){
		// 先获取 match.html
		persistMatchBatch(cal);
		String dir = OkConstant.FILE_PATH_BASE;
		String month = StringUtils.leftPad(
				String.valueOf(cal.get(Calendar.MONTH) + 1), 2, "0");
		String dayOfMonth = StringUtils.leftPad(
				String.valueOf(cal.get(Calendar.DAY_OF_MONTH)), 2, "0");
		dir = dir + cal.get(Calendar.YEAR) + "/" + month + "/" + dayOfMonth;
		
		ExecutorService service = Executors.newFixedThreadPool(10);
		EuroOddsPersistThread euroOddsPersistThread = new EuroOddsPersistThread();
		euroOddsPersistThread.setDir(dir);
		service.execute(euroOddsPersistThread);
		
		AsiaOddsPersistThread asiaOddsPersistThread = new AsiaOddsPersistThread();
		asiaOddsPersistThread.setDir(dir);
		service.execute(asiaOddsPersistThread);
		
		AllEuroOddsChangePersist allEuroOddsChangePersist = new AllEuroOddsChangePersist();
		allEuroOddsChangePersist.setDir(dir);
		service.execute(allEuroOddsChangePersist);
		
		AsiaOddsChangePersistThread asiaOddsChangePersistThread = new AsiaOddsChangePersistThread();
		asiaOddsChangePersistThread.setDir(dir);
		service.execute(asiaOddsChangePersistThread);
		
		ExchangeInfoPersistThread exchangeInfoPersistThread = new ExchangeInfoPersistThread();
		exchangeInfoPersistThread.setDir(dir);
		service.execute(exchangeInfoPersistThread);
		
		TurnoverDetailPersistThread turnoverDetailPersistThread = new TurnoverDetailPersistThread();
		turnoverDetailPersistThread.setDir(dir);
		service.execute(turnoverDetailPersistThread);
		
	}
	
	/**
	 * okooo 多期的单场;
	 */
	public void persistMatchBatch(Calendar cal) {
		String url = OkParseUtils.buildUrlByDate(cal);
		LOGGER.info("process url: " + url);
		
		String relaDirPath = OkParseUtils.getDirPahtFromUrl(url);
		String dirPath = OkConstant.FILE_PATH_BASE + "/" + relaDirPath;
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File matchHtml = new File(dirPath + "/" + OkConstant.MATCH_FILE_NAME);
		OkParseUtils.persistMatch(matchHtml, url, true);
	}
	
	/**
	 * okooo 多期的单场;
	 */
	public void persistMatchBatch(String baseDir, Calendar cal) {
		String url = OkParseUtils.buildUrlByDate(cal);
		LOGGER.info("process url: " + url);
		
		String relaDirPath = OkParseUtils.getDirPahtFromUrl(url);
		String dirPath = baseDir + "/" + relaDirPath;
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File matchHtml = new File(dirPath + "/" + OkConstant.MATCH_FILE_NAME);
		OkParseUtils.persistMatch(matchHtml, url, true);
	}

	/**
	 * okooo 多期的欧赔.
	 */
	public void persistEuroOddsBatch(String dir) {
		long begin = System.currentTimeMillis();
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(dir, OkConstant.MATCH_FILE_NAME);
//		LOGGER.info("matchHtmlFiles size: " + matchHtmlFiles.size());
		if(matchHtmlFiles != null && matchHtmlFiles.size() > MAX_FILE_NUM){
			LOGGER.info("more than 1 month, return now.");
			return;
		}
		for (File matchHtmlFile : matchHtmlFiles) {
//			LOGGER.info("process matchHtmlFile: " + matchHtmlFile.getAbsolutePath());
			persistEuroOdds(matchHtmlFile);
		}
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/(1000*60) + " min.");
	}

	/**
	 * okooo 多期的亚盘
	 */
	public void persistAsiaOddsBatch(String dir) {
		long begin = System.currentTimeMillis();
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(dir, OkConstant.MATCH_FILE_NAME);
//		LOGGER.info("matchHtmlFiles size: " + matchHtmlFiles.size());
		if(matchHtmlFiles != null && matchHtmlFiles.size() > MAX_FILE_NUM){
			LOGGER.info("more than 1 month, return now.");
			return;
		}
		
		for (File matchHtmlFile : matchHtmlFiles) {
//			LOGGER.info("process matchHtmlFile: " + matchHtmlFile.getAbsolutePath());
			persistAsiaOdds(matchHtmlFile);
		}
		
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/(1000*60) + " min.");
	}

	/**
	 * okooo 多期的欧赔变化页面
	 */
	public void persistEuroOddsChangeBatch(String dir) {
		long begin = System.currentTimeMillis();
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(dir, OkConstant.MATCH_FILE_NAME);
//		LOGGER.info("matchHtmlFiles size: " + matchHtmlFiles.size());

		if(matchHtmlFiles != null && matchHtmlFiles.size() > MAX_FILE_NUM){
			LOGGER.info("more than 1 month, return now.");
			return;
		}
		
		for (File matchHtmlFile : matchHtmlFiles) {
//			LOGGER.info("process matchHtmlFile: " + matchHtmlFile.getAbsolutePath());
			persistEuroOddsChange(matchHtmlFile);
		}
		
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/(1000*60) + " min.");
	}

	/**
	 * okooo 多期的亚盘变化页面
	 */
	public void persistAsiaOddsChangeBatch(String dir) {
		long begin = System.currentTimeMillis();
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(dir, OkConstant.MATCH_FILE_NAME);
//		LOGGER.info("matchHtmlFiles size: " + matchHtmlFiles.size());

		if(matchHtmlFiles != null && matchHtmlFiles.size() > MAX_FILE_NUM){
			LOGGER.info("more than 1 month, return now.");
			return;
		}
		
		for (File matchHtmlFile : matchHtmlFiles) {
//			LOGGER.info("process matchHtmlFile: " + matchHtmlFile.getAbsolutePath());
			persistAsiaOddsChange(matchHtmlFile);
		}
		
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/(1000*60) + " min.");
	}

	/**
	 * okooo 多期的交易盈亏页面
	 */
	public void persistExchangeInfoBatch(String dir, int beginMatchSeq, int endMatchSeq, Set<Integer> limitedMatchSeqs, boolean replace) {
		long begin = System.currentTimeMillis();
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(dir, OkConstant.MATCH_FILE_NAME);
//		LOGGER.info("matchHtmlFiles size: " + matchHtmlFiles.size());

		if(matchHtmlFiles != null && matchHtmlFiles.size() > MAX_FILE_NUM){
			LOGGER.info("more than 1 month, return now.");
			return;
		}
		
		for (File matchHtmlFile : matchHtmlFiles) {
//			LOGGER.info("process matchHtmlFile: " + matchHtmlFile.getAbsolutePath());
			persistExchangeInfo(matchHtmlFile, beginMatchSeq, endMatchSeq, limitedMatchSeqs, replace);
		}
		
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/(1000*60) + " min.");
	}

	/**
	 * okooo 多期的成交明细页面
	 */
	public void persistTurnoverDetailBatch(String dir) {
		long begin = System.currentTimeMillis();
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(dir, OkConstant.MATCH_FILE_NAME);
//		LOGGER.info("matchHtmlFiles size: " + matchHtmlFiles.size());

		if(matchHtmlFiles != null && matchHtmlFiles.size() > MAX_FILE_NUM){
			LOGGER.info("more than 1 month, return now.");
			return;
		}
		
		for (File matchHtmlFile : matchHtmlFiles) {
//			LOGGER.info("process matchHtmlFile: " + matchHtmlFile.getAbsolutePath());
			persistTurnoverDetail(matchHtmlFile);
		}
		
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/(1000*60) + " min.");
	}
	
	/**
	 * 获取所有公司的欧赔变化页面.(http://www.okooo.com/soccer/match/669061/odds/change/82/)
	 * @param dir
	 */
	public void persistAllCorpEuroOddsChange(String dir){
		long begin = System.currentTimeMillis();
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(dir, OkConstant.MATCH_FILE_NAME);
		if(matchHtmlFiles != null && matchHtmlFiles.size() > MAX_FILE_NUM){
			LOGGER.info("more than 1 month, return now.");
			return;
		}
		
		String euroOddsChangePath = "";
		File euroOddsChangeFile = null;
		List<Integer> corpsNo = null;
		List<File> euroOddsHtmls = null;
		String matchSeq = "0";
		
		for(File matchHtml : matchHtmlFiles){
			String matchHtmlPath = matchHtml.getAbsolutePath();
			euroOddsHtmls = OkParseUtils.getSameDirFilesFromMatch(matchHtml, OkConstant.EURO_ODDS_FILE_NAME_BASE);
			
			Document matchDoc = Jsoup.parse(OkParseUtils.getFileContent(matchHtml));
			for(File euroOddsHtml : euroOddsHtmls){
				// 获取所有的博彩公司的序号.
				corpsNo = getAllCorpsNo(euroOddsHtml);
				matchSeq = euroOddsHtml.getName().split("_")[1].replaceAll(".html", "");
				for(int corpNo : corpsNo){
					euroOddsChangePath = matchHtmlPath.replaceFirst(
							OkConstant.MATCH_FILE_NAME, OkConstant.EURO_ODDS_CHANGE_FILE_NAME_BASE + "_"
									+ corpNo + "_" + matchSeq + ".html");
					euroOddsChangeFile = new File(euroOddsChangePath);

					// 文件存在且非空时不做处理.
					if (OkParseUtils.checkFileExists(euroOddsChangeFile) && OkParseUtils.checkFileSize(euroOddsChangeFile, 10)) {
						continue;
					}

					String matchUrl = parseOkoooUrl.findEuroOddsChangeUrl(matchDoc,
							Integer.valueOf(matchSeq), corpNo);
					if (StringUtils.isEmpty(matchUrl)) {
						break;
					}
					// 获取 euroOddsChange 的页面信息.
					OkParseUtils.persistByUrl(euroOddsChangeFile, matchUrl, "gb2312", 1000);
					
					LOGGER.info("matchHtmlPath: " + matchHtmlPath + "; matchUrl: " + matchUrl + "; matchSeq: " + matchSeq);
				}
			}

		}
		
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/(1000*60) + " min.");
	}
	
	/**
	 * 一期当中的每一场的欧陪页面: http://www.okooo.com/soccer/match/153028/odds/
	 * 由于okooo使用的是ajax, 所以赔率部分的数据使用ajax的url, 最后合并起来.
	 * 
	 */
	private void persistEuroOdds(File matchHtml) {
		String matchHtmlPath = matchHtml.getAbsolutePath();
		if (!matchHtml.exists() || matchHtml.length() < 1000) {
			LOGGER.info("matchHtml: " + matchHtmlPath
					+ " not exists or less than 1000 bytes.");
			return;
		}
		Document matchDoc = Jsoup.parse(OkParseUtils.getFileContent(matchHtml));
		int matchSeq = 0;
		String euroOddsHtmlPath = "";
		File euroOddsHtml;
		while (matchSeq++ < MAX_MATCH_SEQ) {
			euroOddsHtmlPath = matchHtmlPath.replaceFirst(OkConstant.MATCH_FILE_NAME,
					OkConstant.EURO_ODDS_FILE_NAME_BASE + "_" + matchSeq + ".html");
			euroOddsHtml = new File(euroOddsHtmlPath);
			// 文件存在且文件大于 100000b 时不做处理， 另外对于大小是68701(文件大小73010)的文件跳过不做处理, 该文件是正常文件，只是没有赔率数据
			if (OkParseUtils.checkFileExists(euroOddsHtml) && (OkParseUtils.checkFileSize(euroOddsHtml, 100000) || euroOddsHtml.length() == 73010)) {
				continue;
			}
			
			String matchUrl = parseOkoooUrl.findEuroOddsUrl(matchDoc, matchSeq);
			if (StringUtils.isEmpty(matchUrl)) {
				break;
			}

			// 获取euroOdds的页面信息.
			String euroOddsPage = null;;
			try {
				euroOddsPage = OkParseUtils.getMessageFromUrl(matchUrl, "gb2312", 2000);
			} catch (IOException e) {
				LOGGER.error("matchUrl: " + matchUrl + "; delete file: " + euroOddsHtml.getAbsolutePath() + "===" + e);
				// 删除文件
				deleteFile(euroOddsHtml);
				continue;
			}

			// 如果没有使用ajax, 即没有 “数据加载中", 则不做处理.  另外，如果getMessageFromUrl异常，则euroOddsPage为空串，生成空文件.
			if (!euroOddsPage.contains(AJAX_DATA_FLAG)) {
				OkParseUtils.persistByStr(euroOddsHtml, euroOddsPage);
				continue;
			}

			// 获取多次赔率数据，最终合并起来
			StringBuilder euroOddsSb;
			try {
				euroOddsSb = getAjaxOddsData(matchUrl);
			} catch (IOException e) {
				LOGGER.error("matchUrl: " + matchUrl + "; delete file: " + euroOddsHtml.getAbsolutePath() + "===" + e);
				// 删除文件
				deleteFile(euroOddsHtml);
				continue;
			}

			// 将赔率部分合入欧赔页面.
			String afterCombine = euroOddsPage.replaceFirst(AJAX_DATA_FLAG,
					euroOddsSb.toString());

			// 将完整的页面写入文件.
			OkParseUtils.persistByStr(euroOddsHtml, afterCombine);
			LOGGER.info("matchHtmlPath: " + matchHtmlPath + "; matchUrl: " + matchUrl + "; matchSeq: " + matchSeq + "; afterCombine size: " + afterCombine.length());
		}
	}

	/**
	 * 基本同 euroodds
	 * 
	 * @param matchHtml
	 */
	private void persistAsiaOdds(File matchHtml) {
		String matchHtmlPath = matchHtml.getAbsolutePath();
		if (!matchHtml.exists() || matchHtml.length() < 1000) {
			LOGGER.info("matchHtml: " + matchHtmlPath
					+ " not exists or less than 1000 bytes.");
			return;
		}
		Document matchDoc = Jsoup.parse(OkParseUtils.getFileContent(matchHtml));
		int matchSeq = 0;
		String asiaOddsHtmlPath = "";
		File asiaOddsHtml;
		while (matchSeq++ < MAX_MATCH_SEQ) {
			asiaOddsHtmlPath = matchHtmlPath.replaceFirst(OkConstant.MATCH_FILE_NAME,
					OkConstant.ASIA_ODDS_FILE_NAME_BASE + "_" + matchSeq + ".html");
			asiaOddsHtml = new File(asiaOddsHtmlPath);
			
			// 文件存在且文件大于 10b 时不做处理, 因为亚盘页面不存在ajax, 所以这里认为文件存在即不做处理.
			if (OkParseUtils.checkFileExists(asiaOddsHtml) && OkParseUtils.checkFileSize(asiaOddsHtml, 10)) {
				continue;
			}
			
			String matchUrl = parseOkoooUrl.findAsiaOddsUrl(matchDoc, matchSeq);
			if (StringUtils.isEmpty(matchUrl)) {
				break;
			}

			// 获取asiaOdds的页面信息.
			String asiaOddsPage;
			try {
				asiaOddsPage = OkParseUtils.getMessageFromUrl(matchUrl, "gb2312", 2000);
			} catch (IOException e) {
				LOGGER.error("matchUrl: " + matchUrl + "; delete file: " + asiaOddsHtml.getAbsolutePath() + "===" + e);
				// 删除文件
				deleteFile(asiaOddsHtml);
				continue;
			}

			LOGGER.info("matchHtmlPath: " + matchHtmlPath + "; matchUrl: " + matchUrl + "; matchSeq: " + matchSeq + ";asiaOddsPage size: " + asiaOddsPage.length());
			
			// 如果没有使用ajax, 即没有 “数据加载中", 则不做处理.
			if (!asiaOddsPage.contains(AJAX_DATA_FLAG)) {
				OkParseUtils.persistByStr(asiaOddsHtml, asiaOddsPage);
				continue;
			}

			// 获取多次赔率数据，最终合并起来
			StringBuilder asiaOddsSb;
			try {
				asiaOddsSb = getAjaxOddsData(matchUrl);
			} catch (IOException e) {
				LOGGER.error("matchUrl: " + matchUrl + "; delete file: " + asiaOddsHtml.getAbsolutePath() + "===" + e);
				// 删除文件
				deleteFile(asiaOddsHtml);
				continue;
			}

			// 将赔率部分合入欧赔页面.
			String afterCombine = asiaOddsPage.replaceFirst(AJAX_DATA_FLAG,
					asiaOddsSb.toString());

			// 将完整的页面写入文件.
			OkParseUtils.persistByStr(asiaOddsHtml, afterCombine);
		}
	}
	
	/**
	 * 获取满足限制条件的亚盘页面(http://www.okooo.com/soccer/match/776375/ah/)
	 * @param baseDir 存放html的路径, 后面还需要添加类似: 2015/04/03/
	 * @param cal 日期
	 * @param matches 带获取页面的match对象.
	 * @param limitedMatchSeqs 指定的matchSeq, 优先级高于 beginMatchSeq, endMatchSeq
	 * @param beginMatchSeq 开始的matchSeq. 如果limitedMatchSeqs为空，使用该条件
	 * @param endMatchSeq   结束的matchSeq. 如果limitedMatchSeqs为空，使用该条件
	 * @param replace 是否替换同名文件.
	 */
	public void persistAsiaOddsWithLimit(String baseDir, Calendar cal, List<Match> matches, Set<Integer> limitedMatchSeqs, 
			int beginMatchSeq, int endMatchSeq, boolean replace, boolean reGetMatchHtml) {
		long begin = System.currentTimeMillis();
		// 确保 matches 不为空, 因为本类中没有注入service.
		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return now...");
			return;
		}
		
		// 构造存放文件的路径.
		String dir = baseDir
				+ cal.get(Calendar.YEAR) + "/" 
				+ StringUtils.leftPad(String.valueOf(cal.get(Calendar.MONTH) + 1), 2, '0') + "/" 
				+ StringUtils.leftPad(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)), 2, '0') + "/";
		// 先获取match.html.
		if(reGetMatchHtml){
			HtmlPersist persist = new HtmlPersist();
			persist.persistMatchBatch(cal);
		}
		
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(dir, OkConstant.MATCH_FILE_NAME);
		if(matchHtmlFiles == null || matchHtmlFiles.isEmpty()){
			LOGGER.info("no match.html, return now.");
			return;
		}
		if(matchHtmlFiles.size() > 1){
			LOGGER.info("more than 1 day, return now.");
			return;
		}
		
		File matchHtml = matchHtmlFiles.get(0);
		String matchHtmlPath = matchHtml.getAbsolutePath();
		String asiaOddsHtmlPath = "";
		File asiaOddsHtml;
		for(Match match : matches){
			// 从指定matchSeq开始.
			int matchSeq = match.getMatchSeq();
			if(limitedMatchSeqs != null){
				if(!limitedMatchSeqs.contains(matchSeq)){
					continue;
				}
			}else{
				if(matchSeq < beginMatchSeq){
					continue;
				}
				// 到指定matchSeq为止.
				if(matchSeq > endMatchSeq){
					break;
				}
			}
			
			asiaOddsHtmlPath = matchHtmlPath.replaceFirst(OkConstant.MATCH_FILE_NAME,
					OkConstant.ASIA_ODDS_FILE_NAME_BASE + "_" + matchSeq + ".html");
			asiaOddsHtml = new File(asiaOddsHtmlPath);

			if(replace){
				asiaOddsHtml.delete();
			}
			// 文件存在且非空时不做处理.
			if (!replace && OkParseUtils.checkFileExists(asiaOddsHtml) && OkParseUtils.checkFileSize(asiaOddsHtml, 10)) {
				continue;
			}

			LOGGER.info("matchHtmlPath: " + matchHtmlPath + "; matchSeq: " + matchSeq);
			// 为了加快速度，直接构造.  http://www.okooo.com/soccer/match/713202/ah/
			String asiaOddsUrl = "http://www.okooo.com/soccer/match/" + match.getOkMatchId() + "/ah/";
			// 获取asiaOdds的页面信息.
			String asiaOddsPage;
			try {
				asiaOddsPage = OkParseUtils.getMessageFromUrl(asiaOddsUrl, "gb2312", 2000);
			} catch (IOException e) {
				LOGGER.error("matchUrl: " + asiaOddsUrl + "; delete file: " + asiaOddsHtml.getAbsolutePath() + "===" + e);
				// 删除文件
				deleteFile(asiaOddsHtml);
				continue;
			}

			// 如果没有使用ajax, 即没有 “数据加载中", 则不做处理.
			if (!asiaOddsPage.contains(AJAX_DATA_FLAG)) {
				OkParseUtils.persistByStr(asiaOddsHtml, asiaOddsPage);
				continue;
			}

			// 获取多次赔率数据，最终合并起来
			StringBuilder asiaOddsSb;
			try {
				asiaOddsSb = getAjaxOddsData(asiaOddsUrl);
			} catch (IOException e) {
				LOGGER.error("matchUrl: " + asiaOddsUrl + "; delete file: " + asiaOddsHtml.getAbsolutePath() + "===" + e);
				// 删除文件
				deleteFile(asiaOddsHtml);
				continue;
			}
			// 将赔率部分合入欧赔页面.
			String afterCombine = asiaOddsPage.replaceFirst(AJAX_DATA_FLAG,
					asiaOddsSb.toString());
			// 将完整的页面写入文件.
			OkParseUtils.persistByStr(asiaOddsHtml, afterCombine);
		}
		
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/(1000*60) + " min.");
	}
	
	/**
	 * 获取满足限制条件的欧赔页面(http://www.okooo.com/soccer/match/783992/odds/)
	 * @param baseDir 存放html的路径, 后面还需要添加类似: 2015/04/03/
	 * @param cal 日期
	 * @param matches 带获取页面的match对象.
	 * @param limitedMatchSeqs 指定的matchSeq, 优先级高于 beginMatchSeq, endMatchSeq
	 * @param beginMatchSeq 开始的matchSeq. 如果limitedMatchSeqs为空，使用该条件
	 * @param endMatchSeq   结束的matchSeq. 如果limitedMatchSeqs为空，使用该条件
	 * @param replace 是否替换同名文件.
	 */
	public void persistEuroOddsWithLimit(String baseDir, Calendar cal, List<Match> matches, Set<Integer> limitedMatchSeqs, 
			int beginMatchSeq, int endMatchSeq, boolean replace) {
		long begin = System.currentTimeMillis();
		// 确保 matches 不为空, 因为本类中没有注入service.
		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return now...");
			return;
		}
		
		// 构造存放文件的路径.
		String dir = baseDir
				+ cal.get(Calendar.YEAR) + "/" 
				+ StringUtils.leftPad(String.valueOf(cal.get(Calendar.MONTH) + 1), 2, '0') + "/" 
				+ StringUtils.leftPad(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)), 2, '0') + "/";
		// 先获取match.html.
		HtmlPersist persist = new HtmlPersist();
		persist.persistMatchBatch(cal);
		
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(dir, OkConstant.MATCH_FILE_NAME);
		if(matchHtmlFiles == null || matchHtmlFiles.isEmpty()){
			LOGGER.info("no match.html, return now.");
			return;
		}
		if(matchHtmlFiles.size() > 1){
			LOGGER.info("more than 1 day, return now.");
			return;
		}
		
		File matchHtml = matchHtmlFiles.get(0);
		String matchHtmlPath = matchHtml.getAbsolutePath();
		String euroOddsHtmlPath = "";
		File euroOddsHtml;
		for(Match match : matches){
			// 从指定matchSeq开始.
			int matchSeq = match.getMatchSeq();
			if(limitedMatchSeqs != null){
				if(!limitedMatchSeqs.contains(matchSeq)){
					continue;
				}
			}else{
				if(matchSeq < beginMatchSeq){
					continue;
				}
				// 到指定matchSeq为止.
				if(matchSeq > endMatchSeq){
					break;
				}
			}
			LOGGER.info("matchHtmlPath: " + matchHtmlPath + "; matchSeq: " + matchSeq);
			
			euroOddsHtmlPath = matchHtmlPath.replaceFirst(OkConstant.MATCH_FILE_NAME,
					OkConstant.EURO_ODDS_FILE_NAME_BASE + "_" + matchSeq + ".html");
			euroOddsHtml = new File(euroOddsHtmlPath);

			if(replace){
				euroOddsHtml.delete();
			}
			// 文件存在且非空时不做处理.
			if (!replace && OkParseUtils.checkFileExists(euroOddsHtml) && OkParseUtils.checkFileSize(euroOddsHtml, 10)) {
				continue;
			}

			// 为了加快速度，直接构造.  http://www.okooo.com/soccer/match/783992/odds/
			String euroOddsUrl = "http://www.okooo.com/soccer/match/" + match.getOkMatchId() + "/odds/";
			// 获取euroOdds的页面信息.
			String euroOddsPage;
			try {
				euroOddsPage = OkParseUtils.getMessageFromUrl(euroOddsUrl, "gb2312", 2000);
			} catch (IOException e) {
				LOGGER.error("matchUrl: " + euroOddsUrl + "; delete file: " + euroOddsHtml.getAbsolutePath() + "===" + e);
				// 删除文件
				deleteFile(euroOddsHtml);
				continue;
			}

			// 如果没有使用ajax, 即没有 “数据加载中", 则不做处理.
			if (!euroOddsPage.contains(AJAX_DATA_FLAG)) {
				OkParseUtils.persistByStr(euroOddsHtml, euroOddsPage);
				continue;
			}

			// 获取多次赔率数据，最终合并起来
			StringBuilder euroOddsSb;
			try {
				euroOddsSb = getAjaxOddsData(euroOddsUrl);
			} catch (IOException e) {
				LOGGER.error("matchUrl: " + euroOddsUrl + "; delete file: " + euroOddsHtml.getAbsolutePath() + "===" + e);
				// 删除文件
				deleteFile(euroOddsHtml);
				continue;
			}
			// 将赔率部分合入欧赔页面.
			String afterCombine = euroOddsPage.replaceFirst(AJAX_DATA_FLAG,
					euroOddsSb.toString());
			// 将完整的页面写入文件.
			OkParseUtils.persistByStr(euroOddsHtml, afterCombine);
		}
		
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/(1000*60) + " min.");
	}
	
	/**
	 * 获取满足限制条件的okooo指数页面(http://www.okooo.com/soccer/match/776375/okoooexponent/#lstu)
	 * @param baseDir 存放html的路径, 后面还需要添加类似: 2015/04/03/
	 * @param cal 日期
	 * @param matches 带获取页面的match对象.
	 * @param limitedMatchSeqs 指定的matchSeq, 优先级高于 beginMatchSeq, endMatchSeq
	 * @param beginMatchSeq 开始的matchSeq. 如果limitedMatchSeqs为空，使用该条件
	 * @param endMatchSeq   结束的matchSeq. 如果limitedMatchSeqs为空，使用该条件
	 * @param replace 是否替换同名文件.
	 */
	public void persistIndexStatsWithLimit(String baseDir, Calendar cal, List<Match> matches, Set<Integer> limitedMatchSeqs, 
			int beginMatchSeq, int endMatchSeq, boolean replace) {
		long begin = System.currentTimeMillis();
		// 确保 matches 不为空, 因为本类中没有注入service.
		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return now...");
			return;
		}
		
		// 构造存放文件的路径.
		String dir = baseDir
				+ cal.get(Calendar.YEAR) + "/" 
				+ StringUtils.leftPad(String.valueOf(cal.get(Calendar.MONTH) + 1), 2, '0') + "/" 
				+ StringUtils.leftPad(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)), 2, '0') + "/";
		// 先获取match.html.
		HtmlPersist persist = new HtmlPersist();
		persist.persistMatchBatch(cal);
		
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(dir, OkConstant.MATCH_FILE_NAME);
		if(matchHtmlFiles == null || matchHtmlFiles.isEmpty()){
			LOGGER.info("no match.html, return now.");
			return;
		}
		if(matchHtmlFiles.size() > 1){
			LOGGER.info("more than 1 day, return now.");
			return;
		}
		
		File matchHtml = matchHtmlFiles.get(0);
		String matchHtmlPath = matchHtml.getAbsolutePath();
		String indexStatsHtmlPath = "";
		File indexStatsHtml;
		for(Match match : matches){
			// 从指定matchSeq开始.
			int matchSeq = match.getMatchSeq();
			if(limitedMatchSeqs != null){
				if(!limitedMatchSeqs.contains(matchSeq)){
					continue;
				}
			}else{
				if(matchSeq < beginMatchSeq){
					continue;
				}
				// 到指定matchSeq为止.
				if(matchSeq > endMatchSeq){
					break;
				}
			}
			LOGGER.info("matchHtmlPath: " + matchHtmlPath + "; matchSeq: " + matchSeq);
			
			indexStatsHtmlPath = matchHtmlPath.replaceFirst(OkConstant.MATCH_FILE_NAME,
					OkConstant.INDEX_STATS_FILE_NAME + "_" + matchSeq + ".html");
			indexStatsHtml = new File(indexStatsHtmlPath);

			if(replace){
				indexStatsHtml.delete();
			}
			// 文件存在且非空时不做处理.
			if (!replace && OkParseUtils.checkFileExists(indexStatsHtml) && OkParseUtils.checkFileSize(indexStatsHtml, 10)) {
				continue;
			}

			// 为了加快速度，直接构造.  http://www.okooo.com/soccer/match/776375/okoooexponent/#lstu
			String indexStatsUrl = "http://www.okooo.com/soccer/match/" + match.getOkMatchId() + "/okoooexponent/#lstu";
			
			/*
			 * 由于指数值是异步显示在页面上的，所以无法使用原来的方式来获取。
			 * 可以直接访问该异步url: http://www.okooo.com/soccer/match/736914/okoooexponent/xmlData/
			 * 根据type参数的不同, 获取不同的指数。type=okooo: 澳客指数; type=okoooexponent 离散度指数.
			 */
			OkParseUtils.persistByUrl(indexStatsHtml, indexStatsUrl, "gb2312", 2000);
		}
		
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/(1000*60) + " min.");
	}
	
	/**
	 * 获取满足限制条件的让球页面(http://www.okooo.com/soccer/match/776908/hodds/)
	 * @param baseDir 存放html的路径, 后面还需要添加类似: 2015/04/03/
	 * @param cal 日期
	 * @param matches 带获取页面的match对象.
	 * @param limitedMatchSeqs 指定的matchSeq, 优先级高于 beginMatchSeq, endMatchSeq
	 * @param beginMatchSeq 开始的matchSeq. 如果limitedMatchSeqs为空，使用该条件
	 * @param endMatchSeq   结束的matchSeq. 如果limitedMatchSeqs为空，使用该条件
	 * @param replace 是否替换同名文件.
	 */
	public void persistEuroHandicapWithLimit(String baseDir, Calendar cal, List<Match> matches, Set<Integer> limitedMatchSeqs, 
			int beginMatchSeq, int endMatchSeq, boolean replace, boolean reGetMatchHtml) {
		long begin = System.currentTimeMillis();
		// 确保 matches 不为空, 因为本类中没有注入service.
		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return now...");
			return;
		}
		
		// 构造存放文件的路径.
		String dir = baseDir
				+ cal.get(Calendar.YEAR) + "/" 
				+ StringUtils.leftPad(String.valueOf(cal.get(Calendar.MONTH) + 1), 2, '0') + "/" 
				+ StringUtils.leftPad(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)), 2, '0') + "/";
		// 先获取match.html.
		if(reGetMatchHtml){
			HtmlPersist persist = new HtmlPersist();
			persist.persistMatchBatch(cal);
		}
		
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(dir, OkConstant.MATCH_FILE_NAME);
		if(matchHtmlFiles == null || matchHtmlFiles.isEmpty()){
			LOGGER.info("no match.html, return now.");
			return;
		}
		if(matchHtmlFiles.size() > 1){
			LOGGER.info("more than 1 day, return now.");
			return;
		}
		
		File matchHtml = matchHtmlFiles.get(0);
		String matchHtmlPath = matchHtml.getAbsolutePath();
		String euroHandicapHtmlPath = "";
		File euroHandicapHtml;
		for(Match match : matches){
			// 从指定matchSeq开始.
			int matchSeq = match.getMatchSeq();
			if(limitedMatchSeqs != null){
				if(!limitedMatchSeqs.contains(matchSeq)){
					continue;
				}
			}else{
				if(matchSeq < beginMatchSeq){
					continue;
				}
				// 到指定matchSeq为止.
				if(matchSeq > endMatchSeq){
					break;
				}
			}
			LOGGER.info("matchHtmlPath: " + matchHtmlPath + "; matchSeq: " + matchSeq);
			
			euroHandicapHtmlPath = matchHtmlPath.replaceFirst(OkConstant.MATCH_FILE_NAME,
					OkConstant.EURO_HANDICAP_FILE_NAME_BASE + "_" + matchSeq + ".html");
			euroHandicapHtml = new File(euroHandicapHtmlPath);

			if(replace){
				euroHandicapHtml.delete();
			}
			// 文件存在且非空时不做处理.
			if (!replace && OkParseUtils.checkFileExists(euroHandicapHtml) && OkParseUtils.checkFileSize(euroHandicapHtml, 10)) {
				continue;
			}

			// 为了加快速度，直接构造.  http://www.okooo.com/soccer/match/776908/hodds/
			String euroHandicapUrl = "http://www.okooo.com/soccer/match/" + match.getOkMatchId() + "/hodds/";
			OkParseUtils.persistByUrl(euroHandicapHtml, euroHandicapUrl, "gb2312", 2000);
		}
		
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/(1000*60) + " min.");
	}

	/**
	 * 获取满足限制条件的让球页面(http://www.okooo.com/soccer/match/776908/hodds/)
	 * @param baseDir 存放html的路径, 后面还需要添加类似: 2015/04/03/
	 * @param cal 日期
	 * @param matches 带获取页面的match对象.
	 * @param limitedMatchSeqs 指定的matchSeq, 优先级高于 beginMatchSeq, endMatchSeq
	 * @param beginMatchSeq 开始的matchSeq. 如果limitedMatchSeqs为空，使用该条件
	 * @param endMatchSeq   结束的matchSeq. 如果limitedMatchSeqs为空，使用该条件
	 * @param replace 是否替换同名文件.
	 */
	@SuppressWarnings("deprecation")
	public void persistEuroTransAsiaWithLimit(String baseDir, Calendar cal, List<Match> matches, Set<Integer> limitedMatchSeqs, 
			int beginMatchSeq, int endMatchSeq, boolean replace) {
		long begin = System.currentTimeMillis();
		// 确保 matches 不为空, 因为本类中没有注入service.
		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return now...");
			return;
		}
		
		// 构造存放文件的路径.
		String dir = baseDir
				+ cal.get(Calendar.YEAR) + "/" 
				+ StringUtils.leftPad(String.valueOf(cal.get(Calendar.MONTH) + 1), 2, '0') + "/" 
				+ StringUtils.leftPad(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)), 2, '0') + "/";
		
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(dir, OkConstant.MATCH_FILE_NAME);
		if(matchHtmlFiles == null || matchHtmlFiles.isEmpty()){
			LOGGER.info("no match.html, return now.");
			return;
		}
		if(matchHtmlFiles.size() > 1){
			LOGGER.info("more than 1 day, return now.");
			return;
		}
		
		File matchHtml = matchHtmlFiles.get(0);
		String matchHtmlPath = matchHtml.getAbsolutePath();
		String euroTransAsiaHtmlPath = "";
		File euroTransAsiaHtml;
		for(Match match : matches){
			// 从指定matchSeq开始.
			int matchSeq = match.getMatchSeq();
			if(limitedMatchSeqs != null){
				if(!limitedMatchSeqs.contains(matchSeq)){
					continue;
				}
			}else{
				if(matchSeq < beginMatchSeq){
					continue;
				}
				// 到指定matchSeq为止.
				if(matchSeq > endMatchSeq){
					break;
				}
			}
			LOGGER.info("matchHtmlPath: " + matchHtmlPath + "; matchSeq: " + matchSeq);
			
			euroTransAsiaHtmlPath = matchHtmlPath.replaceFirst(OkConstant.MATCH_FILE_NAME,
					OkConstant.EURO_TRANS_ASIA_FILE_NAME_BASE + "_" + matchSeq + ".html");
			euroTransAsiaHtml = new File(euroTransAsiaHtmlPath);

			if(replace){
				euroTransAsiaHtml.delete();
			}
			// 文件存在且非空时不做处理.
			if (!replace && OkParseUtils.checkFileExists(euroTransAsiaHtml) && OkParseUtils.checkFileSize(euroTransAsiaHtml, 10)) {
				continue;
			}

			// 为了加快速度，直接构造. http://www.okooo.com/soccer/match/713907/ah/?action=euro2asia&MatchID=713907&MakerIDList=0|82,1|65,2|19,3|84,4|220,5|280,6|106,7|543,8|593,9|696
			String queryUrlStr = "";
			for(int i = 0; i < OkConstant.ODDS_CORP_EURO_TRANS_ASIA.length; i++){
				queryUrlStr += i + "|" + OkConstant.ODDS_CORP_EURO_TRANS_ASIA[i] + ",";
			}
			queryUrlStr = queryUrlStr.substring(0, queryUrlStr.length() - 1);
			String euroTransAsiaUrl = "http://www.okooo.com/soccer/match/" + match.getOkMatchId() + "/ah/?action=euro2asia&MatchID=" + match.getOkMatchId() + "&MakerIDList=" + queryUrlStr;
//			String euroTransAsiaUrl = "http://www.okooo.com/soccer/match/" + match.getOkMatchId() + "/ah/?action=euro2asia&MatchID=" + match.getOkMatchId() + "&MakerIDList=0|82,1|65,2|19,3|84,4|220,5|280,6|106,7|543,8|593,9|696,10|14,11|27,12|13,13|108,14|634,15|614,16|331,17|560";
			try {
				// 需要编码，因为有 "|".
				euroTransAsiaUrl = UriUtils.encodeHttpUrl(euroTransAsiaUrl, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error("url Encode error: " + e);
				continue;
			}
			OkParseUtils.persistByUrl(euroTransAsiaHtml, euroTransAsiaUrl, "gb2312", 2000);
		}
		
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/(1000*60) + " min.");
	}
	
	/**
	 * 获取欧赔变化页面. 本地文件格式: euroOdds_{corpNo}_{matchSeq}.html
	 * 
	 * @param matchHtml
	 */
	private void persistEuroOddsChange(File matchHtml) {
		String matchHtmlPath = matchHtml.getAbsolutePath();
		int matchSeq = 0;
		String euroOddsChangePath = "";
		File euroOddsChangeFile = null;
		Document matchDoc = Jsoup.parse(OkParseUtils.getFileContent(matchHtml));
		while (matchSeq++ < MAX_MATCH_SEQ) {
			for (int corpNo : OkConstant.ODDS_CORP_TR_EURO) {
				euroOddsChangePath = matchHtmlPath.replaceFirst(
						OkConstant.MATCH_FILE_NAME, OkConstant.EURO_ODDS_CHANGE_FILE_NAME_BASE + "_"
								+ corpNo + "_" + matchSeq + ".html");
				euroOddsChangeFile = new File(euroOddsChangePath);

				// 文件存在且非空时不做处理.
				if (OkParseUtils.checkFileExists(euroOddsChangeFile) && OkParseUtils.checkFileSize(euroOddsChangeFile, 10)) {
					continue;
				}

				String matchUrl = parseOkoooUrl.findEuroOddsChangeUrl(matchDoc,
						matchSeq, corpNo);
				if (StringUtils.isEmpty(matchUrl)) {
					break;
				}
				// 获取 euroOddsChange 的页面信息.
				OkParseUtils.persistByUrl(euroOddsChangeFile, matchUrl, "gb2312", 1000);
				
				LOGGER.info("matchHtmlPath: " + matchHtmlPath + "; matchUrl: " + matchUrl + "; matchSeq: " + matchSeq);
			}
		}
	}
	
	/**
	 * 获取亚盘变化页面. 本地文件格式: asiaOdds_{corpNo}_{matchSeq}.html
	 * 
	 * @param matchHtml
	 */
	private void persistAsiaOddsChange(File matchHtml) {
		String matchHtmlPath = matchHtml.getAbsolutePath();
		int matchSeq = 0;
		String asiaOddsChangePath = "";
		File asiaOddsChangeFile = null;
		Document matchDoc = Jsoup.parse(OkParseUtils.getFileContent(matchHtml));
		while (matchSeq++ < MAX_MATCH_SEQ) {
			for (int corpNo : OkConstant.ODDS_CORP_TR_ASIA) {
				asiaOddsChangePath = matchHtmlPath.replaceFirst(
						OkConstant.MATCH_FILE_NAME, OkConstant.ASIA_ODDS_CHANGE_FILE_NAME_BASE + "_"
								+ corpNo + "_" + matchSeq + ".html");
				asiaOddsChangeFile = new File(asiaOddsChangePath);

				// 文件存在且非空时不做处理.
				if (OkParseUtils.checkFileExists(asiaOddsChangeFile) && OkParseUtils.checkFileSize(asiaOddsChangeFile, 10)) {
					continue;
				}

				String matchUrl = parseOkoooUrl.findAsiaOddsChangeUrl(matchDoc,
						matchSeq, corpNo);
				if (StringUtils.isEmpty(matchUrl)) {
					break;
				}
				// 获取 asiaOddsChange 的页面信息.
				OkParseUtils.persistByUrl(asiaOddsChangeFile, matchUrl, "gb2312", 2000);
				
				LOGGER.info("matchHtmlPath: " + matchHtmlPath + "; matchUrl: " + matchUrl + "; matchSeq: " + matchSeq);
			}
		}
	}
	
	/**
	 * 根据asiaOddsList(一般是通过解析http://www.okooo.com/soccer/match/791370/ah/获得), 
	 * 获取页面中所有公司的asiaOddsChange页面(http://www.okooo.com/soccer/match/791370/ah/change/599/)
	 */
	public void persistAsiaOddsChangeFromAsiaOddsList(String dir, List<AsiaOdds> asiaOddsList, Map<String, String> corpNameNoMap){
		if(asiaOddsList == null || asiaOddsList.isEmpty()){
			return;
		}
		String preAsiaOddsChangeUrl = "http://www.okooo.com/soccer/match/";
		for(AsiaOdds asiaOdds : asiaOddsList){
			String oddsCorpName = asiaOdds.getOddsCorpName();
			Integer matchSeq = asiaOdds.getMatchSeq();
			if(!corpNameNoMap.containsKey(oddsCorpName)){
				continue;
			}
			String oddsCorpNo = corpNameNoMap.get(oddsCorpName);
			// 构造asiaOddsChange页面的url: http://www.okooo.com/soccer/match/791370/ah/change/599/
			String url = preAsiaOddsChangeUrl + asiaOdds.getOkMatchId() + "/ah/change/" + oddsCorpNo + "/";
			
			String asiaOddsChangePath = dir + OkConstant.ASIA_ODDS_CHANGE_FILE_NAME_BASE + "_"
					+ oddsCorpNo + "_" + matchSeq + ".html"; 
			File asiaOddsChangeFile = new File(asiaOddsChangePath);

			// 文件存在且非空时不做处理.
			if (OkParseUtils.checkFileExists(asiaOddsChangeFile) && OkParseUtils.checkFileSize(asiaOddsChangeFile, 10)) {
				continue;
			}

			// 获取 asiaOddsChange 的页面信息.
			OkParseUtils.persistByUrl(asiaOddsChangeFile, url, "gb2312", 2000);
			
			LOGGER.info("asiaOddsChangeFile: " + asiaOddsChangeFile.getAbsolutePath() + "; url: " + url + "; matchSeq: " + matchSeq);
		}
	}

	/**
	 * 获取交易盈亏页面.
	 * 
	 * @param matchHtml
	 * @param beginMatchSeq, endMatchSeq 只获取该范围内的html, 优先级低于 limitedMatchSeqs.
	 * @param limitedMatchSeqs 只获取该list中的html, 优先级最高.
	 */
	private void persistExchangeInfo(File matchHtml, int beginMatchSeq, int endMatchSeq, Set<Integer> limitedMatchSeqs, boolean replace) {
		String matchHtmlPath = matchHtml.getAbsolutePath();
		String exchangeInfoPath = "";
		File exchangeInfoFile = null;
		Document matchDoc = Jsoup.parse(OkParseUtils.getFileContent(matchHtml));
		int matchSeq = 0;
		while (matchSeq++ < MAX_MATCH_SEQ) {
			if(limitedMatchSeqs != null){
				if(!limitedMatchSeqs.contains(matchSeq)){
					continue;
				}
			}else{
				if(matchSeq < beginMatchSeq){
					continue;
				}
				// 到指定matchSeq为止.
				if(matchSeq > endMatchSeq){
					break;
				}
			}
			
			exchangeInfoPath = matchHtmlPath.replaceFirst(
					OkConstant.MATCH_FILE_NAME, OkConstant.EXCHANGE_INFO_FILE_NAME_BASE + "_" + matchSeq + ".html");
			
			exchangeInfoFile = new File(exchangeInfoPath);

			if(replace){
				exchangeInfoFile.delete();
			}
			// 文件存在且非空时不做处理.
			if (!replace && OkParseUtils.checkFileExists(exchangeInfoFile) && OkParseUtils.checkFileSize(exchangeInfoFile, 10)) {
				continue;
			}

			String matchUrl = parseOkoooUrl.findExchangeUrl(matchDoc, matchSeq);
			if (StringUtils.isEmpty(matchUrl)) {
				break;
			}
			// 获取 交易盈亏 的页面信息.
			OkParseUtils.persistByUrl(exchangeInfoFile, matchUrl, "gb2312", 2000);
			
			LOGGER.info("matchHtmlPath: " + matchHtmlPath + "; matchUrl: " + matchUrl + "; matchSeq: " + matchSeq);
		}
	}

	/**
	 * 获取成交明细页面.
	 * 
	 * @param matchHtml
	 */
	private void persistTurnoverDetail(File matchHtml) {
		String matchHtmlPath = matchHtml.getAbsolutePath();
		File turnoverDetailFile = null;
		String turnoverDetailFilePath = "";
		Document matchDoc = Jsoup.parse(OkParseUtils.getFileContent(matchHtml));
		int matchSeq = 0;
		String matchUrl = "";
		while (matchSeq++ < MAX_MATCH_SEQ) {
			turnoverDetailFilePath = matchHtmlPath.replaceFirst(
					OkConstant.MATCH_FILE_NAME, OkConstant.TURNOVER_DETAIL_FILE_NAME + "_" + matchSeq + ".html");
			turnoverDetailFile = new File(turnoverDetailFilePath);

			// 文件存在且非空时不做处理.
			if (OkParseUtils.checkFileExists(turnoverDetailFile) && OkParseUtils.checkFileSize(turnoverDetailFile, 10)) {
				continue;
			}

			matchUrl = parseOkoooUrl.findExchangeDetailUrl(matchDoc, matchSeq);
			if (StringUtils.isEmpty(matchUrl)) {
				break;
			}
			// 获取 成交明细 的页面信息.
			OkParseUtils.persistByUrl(turnoverDetailFile, matchUrl, "gb2312", 2000);
			
			LOGGER.info("matchHtmlPath: " + matchHtmlPath + "; matchUrl: " + matchUrl + "; matchSeq: " + matchSeq);
		}
	}
	
	public void persistMatchByOkUrlDate(String okUrlDate){
		Calendar cal = Calendar.getInstance();
		int year = Integer.valueOf("20" + okUrlDate.substring(0, 2));
		// month 月份 = 实际月份 - 1.
		int month = Integer.valueOf(okUrlDate.substring(2, 4)) - 1;
		int day = Integer.valueOf(okUrlDate.substring(4, 6));
		cal.set(year, month, day, 00, 00);
		persistMatchBatch(cal);
	}
	
	/**
	 * 使用ajax的url获取欧赔的数据, 前后添加 tbody 标签.
	 * 
	 * @param url
	 * @return
	 * @throws IOException 
	 */
	private StringBuilder getAjaxOddsData(String url) throws IOException {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		int pageNo = 0;
		String ajaxUrl = url + "ajax/?page=" + pageNo
				+ "&companytype=BaijiaBooks&type=0";
		String html = "";
		String subHtml = "";
		String ajaxEncoding = "UTF-8";
		StringBuilder sb = new StringBuilder("<tbody>");
		try {
			html = OkParseUtils.getMessageFromUrl(ajaxUrl, ajaxEncoding, 2000);
		} catch (IOException e) {
			throw e;
		}
		while (!html.trim().startsWith("<script>")) {
//			LOGGER.info("url: " + url + "; pageNo: " + pageNo);
			// 去除 <script> 标签.
			subHtml = html.replaceAll("<script>.*</script>", "");
			sb.append(subHtml);

			pageNo++;
			ajaxUrl = url + "ajax/?page=" + pageNo
					+ "&companytype=BaijiaBooks&type=0";
			try {
				html = OkParseUtils.getMessageFromUrl(ajaxUrl, ajaxEncoding, 2000);
			} catch (IOException e) {
				throw e;
			}
		}
		sb.append("</tbody>");
		return sb;
	}
	
	/**
	 * 删除指定的文件;
	 * @param file
	 */
	private void deleteFile(File file){
		if(file == null ||!file.exists()){
			return;
		}
		file.delete();
		return;
	}
	
	/**
	 * 从 euroOdds_{matchSeq}.html 中获取所有的博彩公司的序号;
	 * @param euroOddsHtml
	 * @return
	 */
	private List<Integer> getAllCorpsNo(File euroOddsHtml){
		List<Integer> corpsNo = new ArrayList<Integer>();
		if(!euroOddsHtml.exists()){
			return corpsNo;
		}
		Document euroOddsDoc = Jsoup.parse(OkParseUtils.getFileContent(euroOddsHtml));
		if(euroOddsDoc == null){
			return corpsNo;
		}
		
		int index = 1;
		Elements elements = null;
		while(index++ <= 1000){
			elements = euroOddsDoc.select("#tr" + index);
			if (elements == null || elements.isEmpty()) {
				continue;
			}
			corpsNo.add(index);
		}
		return corpsNo;
	}
	
}
