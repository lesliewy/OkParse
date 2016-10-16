/**
 * 
 */
package com.wy.okooo.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.wy.okooo.domain.Match;
import com.wy.okooo.service.AllSingleMatchService;
import com.wy.okooo.service.AsiaOddsChangeService;
import com.wy.okooo.service.AsiaOddsService;
import com.wy.okooo.service.EuroOddsChangeService;
import com.wy.okooo.service.EuroOddsService;
import com.wy.okooo.service.ExchangeService;
import com.wy.okooo.service.SingleMatchService;
import com.wy.okooo.service.impl.thread.EuroOddsThread;
import com.wy.okooo.service.impl.thread.ExchangeInfoThread;
import com.wy.okooo.util.JsoupUtils;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * 
 * 
 * @author leslie
 * 
 */
public class AllSingleMatchServiceImpl implements AllSingleMatchService {

	private static Logger LOGGER = Logger
			.getLogger(AllSingleMatchServiceImpl.class.getName());

	private SingleMatchService singleMatchService;

	private EuroOddsService euroOddsService;

	private AsiaOddsService asiaOddsService;

	private EuroOddsChangeService euroOddsChangeService;

	private AsiaOddsChangeService asiaOddsChangeService;

	private ExchangeService exchangeService;

	public void parseAllMatch(String url) {
		/*
		 * 获取单场胜平负页面所有的Match对象;
		 */
		long getAllMatchFromUrlBegin = System.currentTimeMillis();
		List<Match> matches = singleMatchService.getAllMatchFromUrl(url, 0, 0);
		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return now...");
			return;
		}
		LOGGER.info("progress: find " + matches.size() + " matches, eclipsed "
				+ (System.currentTimeMillis() - getAllMatchFromUrlBegin)
				+ " ms...");
		/*
		 * 插入 LOT_MATCH
		 */
		long insertMatchBatchBegin = System.currentTimeMillis();
		singleMatchService.insertMatchBatch(matches);
		LOGGER.info("progress: insert LOT_MATCH success, eclipsed "
				+ (System.currentTimeMillis() - insertMatchBatchBegin) + " ms,"
				+ "; match.size: " + matches.size());

		List<Match> lastInserted = singleMatchService
				.queryMatchesByIdRange(matches.size());

		/*
		 * 插入 LOT_ODDS_EURO; 先查询 LOT_MATCH 中刚插入的ID, MATCH_SEQ
		 */
		long parseEuroOddsBegin = System.currentTimeMillis();
		for (Match match : lastInserted) {
			euroOddsService.parseEuroOdds(match.getId(), match.getMatchSeq(), 0);
		}
		LOGGER.info("progress: insert LOT_ODDS_EURO success, eclipsed "
				+ (System.currentTimeMillis() - parseEuroOddsBegin) + " ms...");

		/*
		 * 插入 LOT_ODDS_ASIA.
		 */
		long parseAsiaOddsBegin = System.currentTimeMillis();
		for (Match match : lastInserted) {
			asiaOddsService.parseAsiaOdds(match.getId(), match.getMatchSeq());
		}
		LOGGER.info("progress: insert LOT_ODDS_ASIA success, eclipsed "
				+ (System.currentTimeMillis() - parseAsiaOddsBegin) + " ms...");

		/*
		 * 插入 LOT_ODDS_EURO_CHANGE. 每一场比赛每一个博彩公司的欧赔变化.
		 */
		long parseEuroOddsChangeBegin = System.currentTimeMillis();
		for (Match match : lastInserted) {
			for (int corpNo : OkConstant.ODDS_CORP_TR_EURO) {
				LOGGER.info("leslie match.getMatchSeq(): "
						+ match.getMatchSeq() + "; corpNo: " + corpNo);
				euroOddsChangeService.parseEuroOddsChange(match.getId(),
						match.getMatchSeq(), corpNo, 0, false);
			}
		}
		LOGGER.info("progress: insert LOT_ODDS_EURO_CHANGE success, eclipsed "
				+ (System.currentTimeMillis() - parseEuroOddsChangeBegin)
				+ " ms...");

		/*
		 * 插入 LOT_ODDS_ASIA_CHANGE. 每一场比赛每一个博彩公司的亚盘变化.
		 */
		long parseAsiaOddsChangeBegin = System.currentTimeMillis();
		for (Match match : lastInserted) {
			for (int corpNo : OkConstant.ODDS_CORP_TR_ASIA) {
				asiaOddsChangeService.parseAsiaOddsChange(match.getId(),
						match.getMatchSeq(), corpNo);
			}
		}
		LOGGER.info("progress: insert LOT_ODDS_ASIA_CHANGE success, eclipsed "
				+ (System.currentTimeMillis() - parseAsiaOddsChangeBegin)
				+ " ms...");

		/**
		 * 插入 LOT_ALL_AVERAGE LOT_BF_LISTING LOT_TRANS_PROP
		 */
		long parseExchangeInfo = System.currentTimeMillis();
		for (Match match : lastInserted) {
			exchangeService.parseExchangeInfo(match.getId(),
					match.getMatchSeq());
		}
		LOGGER.info("progress: insert LOT_ALL_AVERAGE LOT_BF_LISTING LOT_TRANS_PROP, success, eclipsed "
				+ (System.currentTimeMillis() - parseExchangeInfo) + " ms...");

		/**
		 * 插入 LOT_BF_TURNOVER_DETAIL
		 */
		long parseTurnoverDetail = System.currentTimeMillis();
		for (Match match : lastInserted) {
			exchangeService.parseBfTurnoverDetail(match.getId(),
					match.getMatchSeq());
		}
		LOGGER.info("progress: insert LOT_BF_TURNOVER_DETAIL success, eclipsed "
				+ (System.currentTimeMillis() - parseTurnoverDetail) + " ms...");

		LOGGER.info("total connection times: " + JsoupUtils.getNumOfConn());
	}

	/**
	 * 从本地文件解析， 不需要连接网络.
	 */
	public void parseAllMatch() {
		long begin = System.currentTimeMillis();
		/*
		 * 获取 match.html 文件列表
		 */
//		List<File> matchHtmlFiles = OkParseUtils.findFileByName(
//				OkConstant.FILE_PATH_BASE, OkConstant.MATCH_FILE_NAME);
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(
				"/home/leslie/MyProject/OkParse/html/2015/01/03/", OkConstant.MATCH_FILE_NAME);
		LOGGER.info("matchHtmlFiles size: " + matchHtmlFiles.size());

		/*
		 * 获取Match对象;
		 */
		List<Match> matches = new ArrayList<Match>(2^10);
		List<Match> oneMatchHtml = new ArrayList<Match>();
		for (File matchHtmlFile : matchHtmlFiles) {
			oneMatchHtml = singleMatchService.getAllMatchFromFile(matchHtmlFile, 0, 0);
			matches.addAll(oneMatchHtml);
			LOGGER.info("matchHtmlFile: " + matchHtmlFile.getAbsolutePath() + "; num of matches: " + oneMatchHtml.size());
		}

		long getAllMatchFromUrlBegin = System.currentTimeMillis();
		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return now...");
			return;
		}

		LOGGER.info("progress: find " + matches.size() + " matches, eclipsed "
				+ (System.currentTimeMillis() - getAllMatchFromUrlBegin)
				+ " ms...");
		/*
		 * 插入 LOT_MATCH
		 */
		long insertMatchBatchBegin = System.currentTimeMillis();
		singleMatchService.insertMatchBatch(matches);
		LOGGER.info("progress: insert LOT_MATCH success, eclipsed "
				+ (System.currentTimeMillis() - insertMatchBatchBegin) + " ms,"
				+ "; match.size: " + matches.size());

		/*
		 * 插入 LOT_ODDS_EURO
		 */
//		long parseEuroOddsBegin = System.currentTimeMillis();
//		List<File> euroOddsHtmls = null;
//		for (File matchHtmlFile : matchHtmlFiles) {
//			euroOddsHtmls = OkParseUtils.getSameDirFilesFromMatch(
//					matchHtmlFile, OkConstant.EURO_ODDS_FILE_NAME_BASE);
//			for (File euroOddsHtml : euroOddsHtmls) {
//				LOGGER.info("process euroOddsHtml: " + euroOddsHtml.getAbsolutePath());
//				euroOddsService.parseEuroOddsFromFile(euroOddsHtml, 0);
//			}
//		}
//		LOGGER.info("progress: insert LOT_ODDS_EURO success, eclipsed "
//				+ (System.currentTimeMillis() - parseEuroOddsBegin) + " ms...");

		/*
		 * 插入 LOT_ODDS_ASIA.
		 */
//		long parseAsiaOddsBegin = System.currentTimeMillis();
//		List<File> asiaOddsHtmls = null;
//		for (File matchHtmlFile : matchHtmlFiles) {
//			asiaOddsHtmls = OkParseUtils.getSameDirFilesFromMatch(
//					matchHtmlFile, OkConstant.ASIA_ODDS_FILE_NAME_BASE);
//			for (File asiaOddsHtml : asiaOddsHtmls) {
//				LOGGER.info("process asiaOddsHtml: " + asiaOddsHtml.getAbsolutePath());
//				asiaOddsService.parseAsiaOddsFromFile(asiaOddsHtml);
//			}
//		}
//		LOGGER.info("progress: insert LOT_ODDS_ASIA success, eclipsed "
//				+ (System.currentTimeMillis() - parseAsiaOddsBegin) + " ms...");

		/*
		 * 插入 LOT_ODDS_EURO_CHANGE. 每一场比赛每一个博彩公司的欧赔变化.
		 */
//		long parseEuroOddsChangeBegin = System.currentTimeMillis();
//		List<File> euroOddsChangeHtmls = null;
//		for (File matchHtmlFile : matchHtmlFiles) {
//			euroOddsChangeHtmls = OkParseUtils.getSameDirFilesFromMatch(
//					matchHtmlFile, OkConstant.EURO_ODDS_CHANGE_FILE_NAME_BASE);
//			for (File euroOddsChangeHtml : euroOddsChangeHtmls) {
//				LOGGER.info("process euroOddsChangeHtml: " + euroOddsChangeHtml.getAbsolutePath());
//				euroOddsChangeService
//						.parseEuroOddsChangeFromFile(euroOddsChangeHtml, 0);
//			}
//		}
//		LOGGER.info("progress: insert LOT_ODDS_EURO_CHANGE success, eclipsed "
//				+ (System.currentTimeMillis() - parseEuroOddsChangeBegin)
//				+ " ms...");

		/*
		 * 插入 LOT_ODDS_ASIA_CHANGE. 每一场比赛每一个博彩公司的亚盘变化.
		 */
//		long parseAsiaOddsChangeBegin = System.currentTimeMillis();
//		List<File> asiaOddsChangeHtmls = null;
//		for (File matchHtmlFile : matchHtmlFiles) {
//			asiaOddsChangeHtmls = OkParseUtils.getSameDirFilesFromMatch(
//					matchHtmlFile, OkConstant.ASIA_ODDS_CHANGE_FILE_NAME_BASE);
//			for (File asiaOddsChangeHtml : asiaOddsChangeHtmls) {
//				LOGGER.info("process asiaOddsChangeHtml: " + asiaOddsChangeHtml.getAbsolutePath());
//				asiaOddsChangeService
//						.parseAsiaOddsChangeFromFile(asiaOddsChangeHtml);
//			}
//		}
//		LOGGER.info("progress: insert LOT_ODDS_ASIA_CHANGE success, eclipsed "
//				+ (System.currentTimeMillis() - parseAsiaOddsChangeBegin)
//				+ " ms...");

		/**
		 * 插入 LOT_ALL_AVERAGE LOT_BF_LISTING LOT_TRANS_PROP
		 */
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

		/**
		 * 插入 LOT_BF_TURNOVER_DETAIL
		 */
//		long parseTurnoverDetail = System.currentTimeMillis();
//		List<File> turnoverDetailHtmls = null;
//		for (File matchHtmlFile : matchHtmlFiles) {
//			turnoverDetailHtmls = OkParseUtils.getSameDirFilesFromMatch(
//					matchHtmlFile, OkConstant.TURNOVER_DETAIL_FILE_NAME);
//			for (File turnoverDetailHtml : turnoverDetailHtmls) {
//				LOGGER.info("process turnoverDetailHtml: " + turnoverDetailHtml.getAbsolutePath());
//				exchangeService
//						.parseBfTurnoverDetailFromFile(turnoverDetailHtml);
//			}
//		}
//		LOGGER.info("progress: insert LOT_BF_TURNOVER_DETAIL success, eclipsed "
//				+ (System.currentTimeMillis() - parseTurnoverDetail) + " ms...");
//
//		LOGGER.info("total connection times: " + JsoupUtils.getNumOfConn());
		LOGGER.info("total time: " + ((System.currentTimeMillis() - begin)/(1000*60)) + " min.");

	}
	
	public void parseSingleMatch(String baseDir){
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(
				baseDir, OkConstant.MATCH_FILE_NAME);
		LOGGER.info("matchHtmlFiles size: " + matchHtmlFiles.size());

		/*
		 * 获取Match对象;
		 */
		List<Match> matches = new ArrayList<Match>(2^10);
		List<Match> oneMatchHtml = new ArrayList<Match>();
		for (File matchHtmlFile : matchHtmlFiles) {
			oneMatchHtml = singleMatchService.getAllMatchFromFile(matchHtmlFile, 0, 0);
			matches.addAll(oneMatchHtml);
			LOGGER.info("matchHtmlFile: " + matchHtmlFile.getAbsolutePath() + "; num of matches: " + oneMatchHtml.size());
		}

		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return now...");
			return;
		}

		/*
		 * 插入 LOT_MATCH
		 */
		long insertMatchBatchBegin = System.currentTimeMillis();
		singleMatchService.insertMatchBatch(matches);
		LOGGER.info("progress: insert LOT_MATCH success, eclipsed "
				+ (System.currentTimeMillis() - insertMatchBatchBegin) + " ms,"
				+ "; match.size: " + matches.size());
	}
	
	
	/**
	 * 插入 LOT_ALL_AVERAGE LOT_BF_LISTING LOT_TRANS_PROP
	 */
	public void parseExchangeInfo(String baseDir){
		long parseExchangeInfo = System.currentTimeMillis();
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(
				baseDir, OkConstant.MATCH_FILE_NAME);
		
		List<File> exchangeInfoHtmls = null;
		for (File matchHtmlFile : matchHtmlFiles) {
			exchangeInfoHtmls = OkParseUtils.getSameDirFilesFromMatch(
					matchHtmlFile, OkConstant.EXCHANGE_INFO_FILE_NAME_BASE);
			for (File exchangeInfoHtml : exchangeInfoHtmls) {
//				LOGGER.info("process exchangeInfoHtml: " + exchangeInfoHtml.getAbsolutePath());
				exchangeService.parseExchangeInfoFromFile(exchangeInfoHtml);
			}
		}
		LOGGER.info("progress: insert LOT_ALL_AVERAGE LOT_BF_LISTING LOT_TRANS_PROP, success, eclipsed "
				+ (System.currentTimeMillis() - parseExchangeInfo) + " ms...");
	}
	
	
	/**
	 * 线程方式.
	 */
	public void parseAllMatchThread(){
		/*
		 * 获取 match.html 文件列表
		 */
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(
				"/home/leslie/MyProject/OkParse/html/2014/10/", OkConstant.MATCH_FILE_NAME);
		LOGGER.info("matchHtmlFiles size: " + matchHtmlFiles.size());

		/*
		 * 获取Match对象;
		 */
		List<Match> matches = new ArrayList<Match>(2^10);
		List<Match> oneMatchHtml = new ArrayList<Match>();
		for (File matchHtmlFile : matchHtmlFiles) {
			LOGGER.info("matchHtmlFile:" + matchHtmlFile.getAbsolutePath());
			oneMatchHtml = singleMatchService.getAllMatchFromFile(matchHtmlFile, 1, 1000);
			matches.addAll(oneMatchHtml);
			LOGGER.info("matchHtmlFile: " + matchHtmlFile.getAbsolutePath() + "; num of matches: " + oneMatchHtml.size());
		}

		long getAllMatchFromUrlBegin = System.currentTimeMillis();
		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return now...");
			return;
		}

		LOGGER.info("progress: find " + matches.size() + " matches, eclipsed "
				+ (System.currentTimeMillis() - getAllMatchFromUrlBegin)
				+ " ms...");
		/*
		 * 插入 LOT_MATCH
		 */
		long insertMatchBatchBegin = System.currentTimeMillis();
		singleMatchService.insertMatchBatch(matches);
		LOGGER.info("progress: insert LOT_MATCH success, eclipsed "
				+ (System.currentTimeMillis() - insertMatchBatchBegin) + " ms,"
				+ "; match.size: " + matches.size());

		ExecutorService service = Executors.newFixedThreadPool(10);
		
		/*
		 * 插入 LOT_ODDS_EURO
		 */
//		EuroOddsThread euroOddsThread = new EuroOddsThread();
//		euroOddsThread.setMatchHtmlFiles(matchHtmlFiles);
//		euroOddsThread.setEuroOddsService(euroOddsService);
//		service.execute(euroOddsThread);
		
		/*
		 * 插入 LOT_ODDS_ASIA.
		 */
//		AsiaOddsThread asiaOddsThread = new AsiaOddsThread();
//		asiaOddsThread.setMatchHtmlFiles(matchHtmlFiles);
//		asiaOddsThread.setAsiaOddsService(asiaOddsService);
//		service.execute(asiaOddsThread);
		
		/*
		 * 插入 LOT_ODDS_EURO_CHANGE. 每一场比赛每一个博彩公司的欧赔变化.
		 */
//		EuroOddsChangeThread euroOddsChangeThread = new EuroOddsChangeThread();
//		euroOddsChangeThread.setMatchHtmlFiles(matchHtmlFiles);
//		euroOddsChangeThread.setEuroOddsChangeService(euroOddsChangeService);
//		service.execute(euroOddsChangeThread);
		
		/*
		 * 插入 LOT_ODDS_ASIA_CHANGE. 每一场比赛每一个博彩公司的亚盘变化.
		 */
//		AsiaOddsChangeThread asiaOddsChangeThread = new AsiaOddsChangeThread();
//		asiaOddsChangeThread.setMatchHtmlFiles(matchHtmlFiles);
//		asiaOddsChangeThread.setAsiaOddsChangeService(asiaOddsChangeService);
//		service.execute(asiaOddsChangeThread);
		
		/*
		 * 插入 LOT_ALL_AVERAGE LOT_BF_LISTING LOT_TRANS_PROP
		 */
		ExchangeInfoThread exchangeInfoThread = new ExchangeInfoThread();
		exchangeInfoThread.setMatchHtmlFiles(matchHtmlFiles);
		exchangeInfoThread.setExchangeService(exchangeService);
		service.execute(exchangeInfoThread);
		
		/*
		 * 插入 LOT_BF_TURNOVER_DETAIL
		 */
//		TurnoverDetailThread turnoverDetailThread = new TurnoverDetailThread();
//		turnoverDetailThread.setMatchHtmlFiles(matchHtmlFiles);
//		turnoverDetailThread.setExchangeService(exchangeService);
//		service.execute(turnoverDetailThread);
		
//		try {
//			Thread.currentThread().join();
//		} catch (InterruptedException e) {
//			LOGGER.error(e);
//		}
	}
	
	/**
	 * 解析 LOT_ODDS_EURO;
	 */
	public void parseEuroOddsThread(){
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(
				"/home/leslie/MyProject/OkParse/html/2014/09/", OkConstant.MATCH_FILE_NAME);
		LOGGER.info("matchHtmlFiles size: " + matchHtmlFiles.size());
		ExecutorService service = Executors.newFixedThreadPool(20);
		
		EuroOddsThread euroOddsThread = new EuroOddsThread();
		euroOddsThread.setMatchHtmlFiles(matchHtmlFiles);
		euroOddsThread.setEuroOddsService(euroOddsService);
		service.execute(euroOddsThread);
		
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}
	}
	
	/**
	 * 解析 LOT_ODDS_EURO;
	 */
	public void parseEuroOddsFromFile(File dir){
		long parseEuroOddsBegin = System.currentTimeMillis();
		List<File> euroOddsHtmls = OkParseUtils.findFileByPrefix(dir.getAbsolutePath(), OkConstant.EURO_ODDS_FILE_NAME_BASE + "_");
		for (File euroOddsHtml : euroOddsHtmls) {
			LOGGER.info("process euroOddsHtml: " + euroOddsHtml.getAbsolutePath());
			String okUrlDate = OkParseUtils.getOkUrlDateFromFile(euroOddsHtml);
			Integer matchSeq = OkParseUtils.getMatchSeqFromOddsFile(euroOddsHtml);
			if(euroOddsService.isExistsByDateSeq(okUrlDate, matchSeq)){
				continue;
			}
			euroOddsService.parseEuroOddsFromFile(euroOddsHtml, 0, okUrlDate, matchSeq);
		}
		LOGGER.info("progress: insert LOT_ODDS_EURO success, eclipsed "
				+ (System.currentTimeMillis() - parseEuroOddsBegin) + " ms...");
	}

	public SingleMatchService getSingleMatchService() {
		return singleMatchService;
	}

	public void setSingleMatchService(SingleMatchService singleMatchService) {
		this.singleMatchService = singleMatchService;
	}

	public EuroOddsService getEuroOddsService() {
		return euroOddsService;
	}

	public void setEuroOddsService(EuroOddsService euroOddsService) {
		this.euroOddsService = euroOddsService;
	}

	public AsiaOddsService getAsiaOddsService() {
		return asiaOddsService;
	}

	public void setAsiaOddsService(AsiaOddsService asiaOddsService) {
		this.asiaOddsService = asiaOddsService;
	}

	public EuroOddsChangeService getEuroOddsChangeService() {
		return euroOddsChangeService;
	}

	public void setEuroOddsChangeService(
			EuroOddsChangeService euroOddsChangeService) {
		this.euroOddsChangeService = euroOddsChangeService;
	}

	public AsiaOddsChangeService getAsiaOddsChangeService() {
		return asiaOddsChangeService;
	}

	public void setAsiaOddsChangeService(
			AsiaOddsChangeService asiaOddsChangeService) {
		this.asiaOddsChangeService = asiaOddsChangeService;
	}

	public ExchangeService getExchangeService() {
		return exchangeService;
	}

	public void setExchangeService(ExchangeService exchangeService) {
		this.exchangeService = exchangeService;
	}

}
