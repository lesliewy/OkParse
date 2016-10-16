/**
 * 
 */
package com.wy.okooo.service.impl;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.wy.okooo.data.HtmlPersist;
import com.wy.okooo.domain.AsiaOdds;
import com.wy.okooo.domain.AsiaOddsTrends;
import com.wy.okooo.domain.EuroAsiaRefer;
import com.wy.okooo.domain.EuroOddsHandicap;
import com.wy.okooo.domain.EuroTransAsia;
import com.wy.okooo.domain.EuropeOdds;
import com.wy.okooo.domain.EuropeOddsChange;
import com.wy.okooo.domain.ExchangeBfTurnoverDetail;
import com.wy.okooo.domain.ExchangeTransactionProp;
import com.wy.okooo.domain.IndexStats;
import com.wy.okooo.domain.KellyCorpResult;
import com.wy.okooo.domain.KellyMatchCount;
import com.wy.okooo.domain.KellyRule;
import com.wy.okooo.domain.Match;
import com.wy.okooo.domain.MatchScore;
import com.wy.okooo.domain.WeightRule;
import com.wy.okooo.service.AnalyseService;
import com.wy.okooo.service.AsiaOddsService;
import com.wy.okooo.service.AsiaOddsTrendsService;
import com.wy.okooo.service.EuroAsiaReferService;
import com.wy.okooo.service.EuroOddsChangeService;
import com.wy.okooo.service.EuroOddsHandicapService;
import com.wy.okooo.service.EuroOddsService;
import com.wy.okooo.service.EuroTransAsiaService;
import com.wy.okooo.service.ExchangeService;
import com.wy.okooo.service.IndexStatsService;
import com.wy.okooo.service.KellyCorpResultService;
import com.wy.okooo.service.KellyMatchCountService;
import com.wy.okooo.service.KellyRuleService;
import com.wy.okooo.service.OkJobService;
import com.wy.okooo.service.SingleMatchService;
import com.wy.okooo.service.WeightRuleService;
import com.wy.okooo.service.impl.thread.KellyAnalyseK2Thread;
import com.wy.okooo.service.impl.thread.KellyAnalyseK3Thread;
import com.wy.okooo.service.impl.thread.KellyAnalyseN3Thread;
import com.wy.okooo.service.impl.thread.PersistCorpEuroOddsChangeKellyThread;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * @author leslie
 *
 */
public class AnalyseServiceImpl implements AnalyseService {

	private static Logger LOGGER = Logger
			.getLogger(AnalyseServiceImpl.class.getName());
	
	private SingleMatchService singleMatchService;
	
	private EuroOddsChangeService euroOddsChangeService;
	
	private WeightRuleService weightRuleService;
	
	private ExchangeService exchangeService;
	
	private EuroOddsService euroOddsService;
	
	private KellyRuleService kellyRuleService;
	
	private KellyCorpResultService kellyCorpResultService;
	
	private AsiaOddsTrendsService asiaOddsTrendsService;
	
	private IndexStatsService indexStatsService;
	
	private EuroOddsHandicapService euroOddsHandicapService;
	
	private KellyMatchCountService kellyMatchCountService;
	
	private EuroTransAsiaService euroTransAsiaService;
	
	private EuroAsiaReferService euroAsiaReferService;
	
	private AsiaOddsService asiaOddsService;
	
	private OkJobService okJobService;
	
	private Map<String, Object> weightMapA = null;
	
	private Map<String, Object> weightMapB = null;
	
	private static final String COUNT = "count";
	private static final String WIN_COUNT = "win_count";
	private static final String EVEN_COUNT = "even_count";
	private static final String NEGA_COUNT = "nega_count";
	private static final String WIN_PROB = "win_prob";
	private static final String EVEN_PROB = "even_prob";
	private static final String NEGA_PROB = "nega_prob";
	private static final String RULE_TYPE = "rule_type";
	
	private static final long maxFileLength = (long) (1 * Math.pow(2, 20));
	
	/**
	 * 指定url, 通过网络获取数据.
	 */
	public void analyse(String url) {
		long getAllMatchFromUrlBegin = System.currentTimeMillis();
		List<Match> matches = singleMatchService.getAllMatchFromUrl(url, 0, 0);
		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return now...");
			return;
		}
		LOGGER.info("progress: find " + matches.size() + " matches, eclipsed "
				+ (System.currentTimeMillis() - getAllMatchFromUrlBegin)
				+ " ms...");
		
		weightMapA = initialWeightAMap();
		long calcuBegin = System.currentTimeMillis();
		for (Match match : matches) {
			Double totalScore = 0d;
			Double avg = 0d;
			int numOfCorp = 0;
			
			for (int corpNo : OkConstant.ODDS_CORP_TR_EURO) {
				LOGGER.info("leslie match.getMatchSeq(): "
						+ match.getMatchSeq() + "; corpNo: " + corpNo);
				List<EuropeOddsChange> europeOddsChangeList = euroOddsChangeService.getEuroOddsChange(0l,
						match.getMatchSeq(), corpNo, 0, false);
				Double score = latestEuroOddsAnalyse(match, europeOddsChangeList);
				if(score != null && score.doubleValue() > 1){
					numOfCorp++;
					totalScore += score;
				}
			}
			if(numOfCorp > 0){
				avg = totalScore / numOfCorp;
			}
			LOGGER.info(match.getOkMatchId() + "  " + match.getOkUrlDate() + "  " + match.getMatchSeq() + "  avg: " + avg);
			break;
		}
		LOGGER.info("progress: calcu avg success, eclipsed "
				+ (System.currentTimeMillis() - calcuBegin)
				+ " ms...");
		
	}
	
	/**
	 * 从本地文件获取数据.
	 */
	public Map<Integer, MatchScore> analyseFromFile(List<Match> matches, String matchDir, Integer beginMatchSeq, Integer endMatchSeq){
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(
				matchDir, OkConstant.MATCH_FILE_NAME);
		LOGGER.info("matchHtmlFiles size: " + matchHtmlFiles.size());

		/*
		 * 获取Match对象;
		 */
		if(matches == null){
			matches = new ArrayList<Match>(2^10);
			List<Match> oneMatchHtml = new ArrayList<Match>();
			for (File matchHtmlFile : matchHtmlFiles) {
				oneMatchHtml = singleMatchService.getAllMatchFromFile(matchHtmlFile, beginMatchSeq, endMatchSeq);
				matches.addAll(oneMatchHtml);
				LOGGER.info("matchHtmlFile: " + matchHtmlFile.getAbsolutePath() + "; num of matches: " + oneMatchHtml.size());
			}
			if (matches == null || matches.isEmpty()) {
				LOGGER.error("matches is null or empty. retry one time now...");
				for (File matchHtmlFile : matchHtmlFiles) {
					oneMatchHtml = singleMatchService.getAllMatchFromFile(matchHtmlFile, beginMatchSeq, endMatchSeq);
					matches.addAll(oneMatchHtml);
					LOGGER.info("matchHtmlFile: " + matchHtmlFile.getAbsolutePath() + "; num of matches: " + oneMatchHtml.size());
				}
			}
		}
		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return null now...");
			return null;
		}
		
		List<MatchScore> scores = new ArrayList<MatchScore>();
		long calcuBegin = System.currentTimeMillis();
		
		// 允许执行的规则
		List<String> allowedRules = new ArrayList<String>();
		allowedRules.add("C");
		
		Map<Integer, MatchScore> scoresMap = new HashMap<Integer, MatchScore>();
		for (Match match : matches) {
//			LOGGER.info("process match: " + match.getMatchSeq());

			// 公共部分.
			MatchScore matchScore = new MatchScore();
			matchScore.setMatchName(match.getMatchName());
			matchScore.setMatchSeq(match.getMatchSeq());
			matchScore.setOkMatchId(match.getOkMatchId());
			matchScore.setOkUrlDate(match.getOkUrlDate());
			matchScore.setRuleType("A");
			matchScore.setHostGoals(match.getHostGoals());
			matchScore.setVisitingGoals(match.getVisitingGoals());
			String matchResult = "未开赛";
			Integer hostGoals = match.getHostGoals() == null ? null : match.getHostGoals();
			Integer visitingGoals = match.getVisitingGoals() == null ? null : match.getVisitingGoals();
			if(hostGoals != null && visitingGoals != null){
				if (hostGoals > visitingGoals) {
					matchResult = "胜";
				} else if (hostGoals == visitingGoals) {
					matchResult = "平";
				} else {
					matchResult = "负";
				}
			}
			matchScore.setMatchResult(matchResult);

			File matchHtmlFile = new File(matchDir + "match.html");

			if (allowedRules != null){
				/*
				 * 规则A: 根据最近欧赔变化计算分数;
				 */
				if(allowedRules.contains("A")){
					if(weightMapA == null){
						weightMapA = initialWeightAMap();
					}
					matchScore = processRuleA(matchHtmlFile, match, matchScore);
				}
				
				/*
				 * 规则B: 必发交易比例判断;
				 */
				if(allowedRules.contains("B")){
					if(weightMapB == null){
						weightMapB = initialWeightBMap();
					}
					matchScore = processRuleB(matchHtmlFile, match, matchScore);
				}
				
				/*
				 * 规则C: 计算竞彩的盈亏.例如:  
				 */
				if(allowedRules.contains("C")){
					matchScore = processRuleC(matchHtmlFile, match, matchScore, matchDir);
					scoresMap.put(match.getMatchSeq(), matchScore);
				}
			}
			
			scores.add(matchScore);
		}
		LOGGER.info("progress: analyse success, eclipsed "
				+ (System.currentTimeMillis() - calcuBegin) + " ms...");
		
		showResult(scores);
		
		return scoresMap;
	}
	
	/*
	 * 规则A: 根据最近欧赔变化计算分数;
	 */
	MatchScore processRuleA(File matchHtmlFile, Match match, MatchScore matchScore){
		long beginA = System.currentTimeMillis();
		Double totalScoreA = 0d;
		Double avgA = 0d;
		int numOfCorp = 0;
		List<File> euroOddsChangeHtmls = OkParseUtils.getSameMatchFilesFromMatch(
				matchHtmlFile, match,
				OkConstant.EURO_ODDS_CHANGE_FILE_NAME_BASE);
		for (File euroOddsChangeHtml : euroOddsChangeHtmls) {
			List<EuropeOddsChange> europeOddsChangeList = euroOddsChangeService
					.getEuroOddsChangeFromFile(euroOddsChangeHtml, 0, false);
			Double scoreA = latestEuroOddsAnalyse(match,
					europeOddsChangeList);
			if (scoreA != null && scoreA.doubleValue() > 1) {
				numOfCorp++;
				totalScoreA += scoreA;
			}
		}
		if (numOfCorp > 0) {
			avgA = totalScoreA / numOfCorp;
		}
		matchScore.setAverageA(avgA);
		matchScore.setTotalScoreA(totalScoreA);
		LOGGER.info("rule A cost: " + (System.currentTimeMillis() - beginA) + " ms.");
		return matchScore;
	}
	
	/*
	 * 规则B: 必发交易比例判断;
	 */
	MatchScore processRuleB(File matchHtmlFile, Match match, MatchScore matchScore){
		long beginB = System.currentTimeMillis();
		List<File> turnoverDetailHtmls = null;
		turnoverDetailHtmls = OkParseUtils.getSameMatchFilesFromMatch(
				matchHtmlFile, match, OkConstant.TURNOVER_DETAIL_FILE_NAME);
		if (turnoverDetailHtmls != null && !turnoverDetailHtmls.isEmpty()) {
			File turnoverDetailHtml = turnoverDetailHtmls.get(0);
			List<ExchangeBfTurnoverDetail> turnoverDetailList = exchangeService
					.getBfTurnoverDetailFromFile(turnoverDetailHtml);
			Double scoreB = bfTurnoverDetailMultiple(match,
					turnoverDetailList);
			matchScore.setScoreB((scoreB == null) ? 0 : scoreB);
			if(turnoverDetailList != null && ! turnoverDetailList.isEmpty()){
				matchScore.setHostOdds(turnoverDetailList.get(0).getHostPrice());
			}
		}
		LOGGER.info("rule B cost: " + (System.currentTimeMillis() - beginB) + " ms.");
		return matchScore;
	}
	
	/*
	 * 规则C: 计算竞彩的盈亏.例如:  
	 * 胜的盈亏: loss_ratio * total ( 平的竞彩交易比例 + 负的竞彩交易比例) - 胜的竞彩交易比例 * total * (胜的赔率 - 1)
	 * 平的盈亏: loss_ratio * total ( 胜的竞彩交易比例 + 负的竞彩交易比例) - 平的竞彩交易比例 * total * (平的赔率 - 1)
	 * 负的盈亏: loss_ratio * total ( 胜的竞彩交易比例 + 平的竞彩交易比例) - 负的竞彩交易比例 * total * (负的赔率 - 1)  
	 * 
	 * 另一种:
	 * 胜的盈亏: loss_ratio * total ( 胜的竞彩交易比例 + 平的竞彩交易比例 + 负的竞彩交易比例) - 胜的竞彩交易比例 * total * 胜的赔率
	 * 平的盈亏: loss_ratio * total ( 胜的竞彩交易比例 + 平的竞彩交易比例 + 负的竞彩交易比例) - 平的竞彩交易比例 * total * 平的赔率
	 * 负的盈亏: loss_ratio * total ( 胜的竞彩交易比例 + 平的竞彩交易比例 + 负的竞彩交易比例) - 负的竞彩交易比例 * total * 负的赔率  
	 *                    
	 */
	MatchScore processRuleC(File matchHtmlFile, Match match, MatchScore matchScore, String matchDir){
		// 从 euroOdds 获取.
		List<EuropeOdds> europeOddsList = null;
//		euroOddsHtmls = OkParseUtils.getSameMatchFilesFromMatch(
//				matchHtmlFile, match, OkConstant.EURO_ODDS_FILE_NAME_BASE);
//		if (euroOddsHtmls != null && !euroOddsHtmls.isEmpty()) {
//			File euroOddsHtml = euroOddsHtmls.get(0);
//			europeOddsList = euroOddsService.getEuropeOddsFromFile(euroOddsHtml, 0);
//		}
		
		// 从 euroOddsChange 获取, 使用99家平均(24)的html.  由于后面的交易量比例来自必发，所以这里使用必发的html(19); 
		String euroOddsChangeHtmlPath = matchDir + "euroOddsChange" + "_" + "19" + "_" + match.getMatchSeq() + ".html";
		File euroOddsChangeHtml = new File(euroOddsChangeHtmlPath);
		if(!euroOddsChangeHtml.exists()){
			return matchScore;
		}
		List<EuropeOddsChange> europeOddsChangeList = euroOddsChangeService
				.getEuroOddsChangeFromFile(euroOddsChangeHtml, 1, false);
		if (europeOddsChangeList == null) {
			return matchScore;
		}
		
		List<File> exchangeInfoHtmls = OkParseUtils.getSameMatchFilesFromMatch(
				matchHtmlFile, match, OkConstant.EXCHANGE_INFO_FILE_NAME_BASE);
		ExchangeTransactionProp transactionProp = null;
		if (exchangeInfoHtmls != null && !exchangeInfoHtmls.isEmpty()) {
			File exchangeInfoHtml = exchangeInfoHtmls.get(0);
			transactionProp = exchangeService.getTransactionPropFromFile(exchangeInfoHtml);
		}
		
		Map<String, Float> compIndexs = compLossIndex(europeOddsList, europeOddsChangeList, transactionProp);
		matchScore.setCompIndexs(compIndexs);
//		LOGGER.info("rule C cost: " + (System.currentTimeMillis() - beginC) + " ms.");
		return matchScore;
	}
	
	/**
	 * 按照凯利指数分析:
	 * rule K1:  LOT_ODDS_EURO_CHANGE 中 ODDS_SEQ=2 时HOST_KELLY < LOSS_RATIO的，分析这些比赛的胜平负情况;
	 * rule K2:  LOT_ODDS_EURO_CHANGE 中 ODDS_SEQ=2 时HOST_KELLY < LOSS_RATIO的 且 ODDS_SEQ=1 时的 HOST_KELLY < ODDS_SEQ=2时的HOST_KELLY, 分析这些比赛的胜平负情况;
	 * 
	 * rule K3:  LOT_ODDS_EURO_CHANGE 中 ODDS_SEQ=2 时HOST_KELLY < ODDS_SEQ=MAXIMUM 时HOST_KELLY 且
	 *                                  ODDS_SEQ=2 时EVEN_KELLY > ODDS_SEQ=MAXIMUM 时EVEN_KELLY 且
	 *                                  ODDS_SEQ=2 时VISITIING_KELLY > ODDS_SEQ=MAXIMUM 时VISITING_KELLY
	 * rule K4: LOT_ODDS_EURO_CHANGE 中 ODDS_SEQ=2 时 HOST_KELLY >= ODDS_SEQ=MAXIMUM 时HOST_KELLY 且
	 *                                  ODDS_SEQ=2 时 EVEN_KELLY >= ODDS_SEQ=MAXIMUM 时EVEN_KELLY 且
	 *                                  ODDS_SEQ=2 时 VISITIING_KELLY < ODDS_SEQ=MAXIMUM 时VISITING_KELLY
	 *                                  
	 * rule K5: LOT_ODDS_EURO_CHANGE 中 ODDS_SEQ=2 时 HOST_KELLY < ODDS_SEQ=MAXIMUM 时HOST_KELLY
	 * rule K6: LOT_ODDS_EURO_CHANGE 中 ODDS_SEQ=2 时 VISITIING_KELLY < ODDS_SEQ=MAXIMUM 时VISITING_KELLY
	 * 
	 * rule K7: LOT_ODDS_EURO_CHANGE 中 ODDS_SEQ=2 时 EVEN_KELLY < ODDS_SEQ=MAXIMUM 时EVEN_KELLY 且
	 *                                  ODDS_SEQ=2 时 HOST_KELLY >= ODDS_SEQ=MAXIMUM 时HOST_KELLY 且
	 *                                  ODDS_SEQ=2 时 VISITIING_KELLY >= ODDS_SEQ=MAXIMUM 时VISITING_KELLY
	 * rule K8: LOT_ODDS_EURO_CHANGE 中 ODDS_SEQ=2 时 EVEN_KELLY < ODDS_SEQ=MAXIMUM 时EVEN_KELLY
	 * 2015-05-21: 修改为获取ODDS_SEQ=1时的kelly数据， 实时的.
	 * 
	 */
	public void kellyAnalyseK2(List<Match> matches, String matchDir, int beginMatchSeq, int endMatchSeq, Set<Integer> limitedMatchSeqs){
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(
				matchDir, OkConstant.MATCH_FILE_NAME);
		LOGGER.info("matchHtmlFiles size: " + matchHtmlFiles.size());

		/*
		 * 获取Match对象;
		 */
		if(matches == null || matches.isEmpty()){
			matches = new ArrayList<Match>(2^10);
			List<Match> oneMatchHtml = new ArrayList<Match>();
			for (File matchHtmlFile : matchHtmlFiles) {
				oneMatchHtml = singleMatchService.getAllMatchFromFile(matchHtmlFile, 0, 0);
				matches.addAll(oneMatchHtml);
				LOGGER.info("matchHtmlFile: " + matchHtmlFile.getAbsolutePath() + "; num of matches: " + oneMatchHtml.size());
			}
		}
		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return now...");
			return;
		}
		
		List<MatchScore> scores = new ArrayList<MatchScore>();
		long calcuBegin = System.currentTimeMillis();

		File matchHtmlFile = new File(matchDir + "match.html");
		String okUrlDate = OkParseUtils.getOkUrlDateFromFile(matchHtmlFile);
		
		// 欧赔亚盘转换表.
		Map<Float, EuroAsiaRefer> referMap = okJobService.getEuroAsiaReferMap();
		// 查询数据库中已经记录的
		Set<String> addedInitOdds = euroTransAsiaService.queryEuroTransAsiaByDateTypeInSet(okUrlDate, "I", beginMatchSeq, endMatchSeq);
		// 由于LOT_KELLY_RULE里有些公司不存在，构造需要的kellyRule.
		List<KellyRule> generalKellyRules = okJobService.getGeneralKellyRules();
		for (Match match : matches) {
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
			LOGGER.info("process match: " + match.getMatchSeq());

			// 查询LOT_KELLY_RULE.
			String matchName = match.getMatchName();
			List<KellyRule> kellyRules = kellyRuleService.queryKellyRulesByMatchName(matchName);
			if(kellyRules == null || kellyRules.isEmpty()){
				LOGGER.info("no kelly rules: " + matchName);
				continue;
			}
			// 添加generalkellyrule
			kellyRules.addAll(generalKellyRules);

			List<EuroTransAsia> euroTransAsiaList = new ArrayList<EuroTransAsia>();
			String asiaOddsHtmlPath = "";
			File asiaOddsHtml = null;
			// 先删除 LOT_ODDS_EURO_CHANGE_DAILY.
			euroOddsChangeService.deleteEuroOddsChanDailyByMatchSeq(okUrlDate, matchSeq);
			for(KellyRule kellyRule : kellyRules){
				String corpNo = kellyRule.getCorpNo();
				String corpName = kellyRule.getOddsCorpName();
				if(corpNo == null || StringUtils.isBlank(corpNo)){
					LOGGER.info("no corpNo: " + corpName);
					break;
				}
				
				String neededFilePath = OkConstant.EURO_ODDS_CHANGE_FILE_NAME_BASE + "_" + corpNo + "_" + match.getMatchSeq() + ".html";
				String euroOddsChangeHtmlPath = matchHtmlFile.getParent() + File.separator + neededFilePath;
				File euroOddsChangeHtml = new File(euroOddsChangeHtmlPath);
				if(!euroOddsChangeHtml.exists() || euroOddsChangeHtml.length() < 10){
					LOGGER.info("file not exists: " + euroOddsChangeHtml.getAbsolutePath());
					continue;
				}
				// 对于过大的文件直接跳过.
				if(euroOddsChangeHtml.length() > maxFileLength){
					LOGGER.info("file too large, skip now: " + euroOddsChangeHtml.getAbsolutePath());
					continue;
				}
				
				// 方便后面的KR, 调用daily的版本.
				List<EuropeOddsChange> europeOddsChangeList = euroOddsChangeService
						.getEuroOddsChangeDailyFromFile(euroOddsChangeHtml, 10, true, okUrlDate, matchSeq);
				if(europeOddsChangeList == null || europeOddsChangeList.isEmpty()){
					continue;
				}
				
				MatchScore matchScore = analyseEuroOddsKelly(europeOddsChangeList, match, kellyRules);
				if(matchScore != null){
					scores.add(matchScore);
				}
				
				// KR: 插入LOT_ODDS_EURO_CHANGE_DAILY
				EuropeOddsChange change1 = europeOddsChangeList.get(0);
				Float latestLossRatio = change1.getLossRatio();
				Float latestHostKellyDis = change1.getHostKelly() - latestLossRatio;
				Float latestVisitingKellyDis = change1.getVisitingKelly() - latestLossRatio;
				int size = europeOddsChangeList.size();
				if((size > 1 && size < 6) && (Math.abs(latestHostKellyDis) > 0.05f || Math.abs(latestVisitingKellyDis) > 0.05f)){
					euroOddsChangeService.insertEuroOddsChangeDailyBatch(europeOddsChangeList);
				}
			}
			
			// KQ Init Odds   2015-05-16: KR中最后一条就是初始的
			if(!addedInitOdds.contains(matchSeq + "_" + "I")){
				// 获取亚盘初始赔率.
				asiaOddsHtmlPath = matchDir + "asiaOdds" + "_" + matchSeq + ".html";
				asiaOddsHtml = new File(asiaOddsHtmlPath);
				Map<String, AsiaOdds> asiaOddsMap = new HashMap<String, AsiaOdds>();
				if(asiaOddsHtml.exists()){
					List<AsiaOdds> asiaOddsList = asiaOddsService.getAsiaOddsFromFile(asiaOddsHtml, null);
					if(asiaOddsList != null && !asiaOddsList.isEmpty()){
						asiaOddsMap = getAsiaOddsMap(asiaOddsList);
					}
				}
				
				for(Integer oddsCorpNo : OkConstant.ODDS_EURO_ASIA_INIT){
					String neededFilePath = OkConstant.EURO_ODDS_CHANGE_FILE_NAME_BASE + "_" + oddsCorpNo + "_" + matchSeq + ".html";
					String euroOddsChangeHtmlPath = matchHtmlFile.getParent() + File.separator + neededFilePath;
					File euroOddsChangeHtml = new File(euroOddsChangeHtmlPath);
					if(!euroOddsChangeHtml.exists() || euroOddsChangeHtml.length() < 10){
						LOGGER.info("file not exists: " + euroOddsChangeHtml.getAbsolutePath());
						continue;
					}
					// 对于过大的文件直接跳过.
					if(euroOddsChangeHtml.length() > maxFileLength){
						LOGGER.info("file too large, skip now: " + euroOddsChangeHtml.getAbsolutePath());
						continue;
					}
					
					List<EuropeOddsChange> europeOddsChangeList = euroOddsChangeService
							.getEuroOddsChangeFromFile(euroOddsChangeHtml, 2, true);
					if(europeOddsChangeList == null || europeOddsChangeList.isEmpty()){
						continue;
					}
					EuroTransAsia euroTransAsia = getEuroTransAsiaFromEuroChange(okUrlDate, matchSeq, europeOddsChangeList.get(europeOddsChangeList.size() - 1),
							referMap, asiaOddsMap);
					if(euroTransAsia != null ){
						euroTransAsiaList.add(euroTransAsia);
					}
				}
				euroTransAsiaService.insertEuroTransAsiaBatch(euroTransAsiaList);
			}
			
			/*
			 ＊ kelly 判断(K7, K8). 为了减少 kellyAnalyseK3() 的执行时间.  2015-04-25 将该部分移到 kellyAnalyseK3() 中, 没必要重复解析文件.
			 */
		}
		showResultK2(scores);
		LOGGER.info("process K2: analyse success, eclipsed "
				+ (System.currentTimeMillis() - calcuBegin) + " ms...");
	}
	
	/**
	 * K3, K4, K5, K6, K7, K8 一起计算
	 *   2015-05-21: 添加 K3a, K4a, K5a, K6a, K7a, K8a
	 */
	public void kellyAnalyseK3(List<Match> matches, String matchDir, int beginMatchSeq, int endMatchSeq, Set<Integer> limitedMatchSeqs,
			Map<Integer, String> jobTypesOfA){
		long begin = System.currentTimeMillis();
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(
				matchDir, OkConstant.MATCH_FILE_NAME);
		LOGGER.info("matchHtmlFiles size: " + matchHtmlFiles.size());

		/*
		 * 获取Match对象;
		 */
		if(matches == null || matches.isEmpty()){
			matches = new ArrayList<Match>(2^10);
			List<Match> oneMatchHtml = new ArrayList<Match>();
			for (File matchHtmlFile : matchHtmlFiles) {
				oneMatchHtml = singleMatchService.getAllMatchFromFile(matchHtmlFile, 0, 0);
				matches.addAll(oneMatchHtml);
				LOGGER.info("matchHtmlFile: " + matchHtmlFile.getAbsolutePath() + "; num of matches: " + oneMatchHtml.size());
			}
		}
		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return now...");
			return;
		}
		
		List<MatchScore> k3Scores = new ArrayList<MatchScore>();
		List<MatchScore> k4Scores = new ArrayList<MatchScore>();
		List<MatchScore> k5Scores = new ArrayList<MatchScore>();
		List<MatchScore> k6Scores = new ArrayList<MatchScore>();
		List<MatchScore> k7Scores = new ArrayList<MatchScore>();
		List<MatchScore> k8Scores = new ArrayList<MatchScore>();
		File matchHtmlFile = matchHtmlFiles.get(0);
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

			LOGGER.info("process match: " + match.getMatchSeq() + "; okUrlDate: " + match.getOkUrlDate());
			
			List<File> euroOddsChangeHtmls = null;
			euroOddsChangeHtmls = OkParseUtils.getSameMatchFilesFromMatch(
					matchHtmlFile, match,
					OkConstant.EURO_ODDS_CHANGE_FILE_NAME_BASE);
			// K3a, K4a, K5a, K6a, K7a, K8a
			Map<String, Integer> matchCountMap = new HashMap<String, Integer>();
			matchCountMap.put("K3a", 0);
			matchCountMap.put("K4a", 0);
			matchCountMap.put("K5a", 0);
			matchCountMap.put("K6a", 0);
			matchCountMap.put("K7a", 0);
			matchCountMap.put("K8a", 0);
			for (File euroOddsChangeHtml : euroOddsChangeHtmls) {
//				LOGGER.info("process euroOddsChangeHtml: " + euroOddsChangeHtml.getAbsolutePath());
				// 对于过大的文件直接跳过.
				if(euroOddsChangeHtml.length() > maxFileLength){
					LOGGER.info("file too large, skip now: " + euroOddsChangeHtml.getAbsolutePath());
					continue;
				}
				
				List<EuropeOddsChange> europeOddsChangeList = euroOddsChangeService
						.getEuroOddsChangeFromFile(euroOddsChangeHtml, 0, false);
				if (europeOddsChangeList == null
						|| europeOddsChangeList.size() <= 2) {
					continue;
				}

				// 获取第二条记录.  2015-05-21: 获取第一条记录，实时的;
				EuropeOddsChange oddsChange1 = null;
				EuropeOddsChange oddsChangeMax = null;
				for (EuropeOddsChange change : europeOddsChangeList) {
					if (change.getOddsSeq() == 1) {
						oddsChange1 = change;
						break;
					}
				}
				oddsChangeMax = europeOddsChangeList.get(europeOddsChangeList
						.size() - 1);

				// kelly 指数判断.
				if (oddsChange1.getHostKelly() < oddsChangeMax.getHostKelly()
						&& oddsChange1.getEvenKelly() > oddsChangeMax
								.getEvenKelly()
						&& oddsChange1.getVisitingKelly() > oddsChangeMax
								.getVisitingKelly()) {
					String ruleType = "K3";
					MatchScore matchScore = new MatchScore();
					matchScore.setMatchName(match.getMatchName());
					matchScore.setMatchSeq(match.getMatchSeq());
					matchScore.setOkUrlDate(match.getOkUrlDate());
					matchScore.setOddsCorpName(oddsChange1.getOddsCorpName());
					matchScore.setHostGoals(match.getHostGoals());
					matchScore.setVisitingGoals(match.getVisitingGoals());
					matchScore.setRuleType(ruleType);
					String matchResult = "未开赛";
					Integer hostGoals = match.getHostGoals();
					Integer visitingGoals = match.getVisitingGoals();
					if (hostGoals != null && visitingGoals != null) {
						if (hostGoals > visitingGoals) {
							matchResult = "胜";
						} else if (match.getHostGoals() == match
								.getVisitingGoals()) {
							matchResult = "平";
						} else {
							matchResult = "负";
						}
					}
					matchScore.setMatchResult(matchResult);
					k3Scores.add(matchScore);
				}else if (oddsChange1.getHostKelly() > oddsChangeMax.getHostKelly()
						&& oddsChange1.getEvenKelly() > oddsChangeMax
								.getEvenKelly()
						&& oddsChange1.getVisitingKelly() < oddsChangeMax
								.getVisitingKelly() && oddsChange1.getVisitingKelly() <= 1.0) {
					String ruleType = "K4";
					MatchScore matchScore = new MatchScore();
					matchScore.setMatchName(match.getMatchName());
					matchScore.setMatchSeq(match.getMatchSeq());
					matchScore.setOkUrlDate(match.getOkUrlDate());
					matchScore.setOddsCorpName(oddsChange1.getOddsCorpName());
					matchScore.setHostGoals(match.getHostGoals());
					matchScore.setVisitingGoals(match.getVisitingGoals());
					matchScore.setRuleType(ruleType);
					String matchResult = "未开赛";
					Integer hostGoals = match.getHostGoals();
					Integer visitingGoals = match.getVisitingGoals();
					if (hostGoals != null && visitingGoals != null) {
						if (hostGoals > visitingGoals) {
							matchResult = "胜";
						} else if (match.getHostGoals() == match
								.getVisitingGoals()) {
							matchResult = "平";
						} else {
							matchResult = "负";
						}
					}
					matchScore.setMatchResult(matchResult);
					k4Scores.add(matchScore);
				}
				
				//K5
				if(oddsChange1.getHostKelly() < oddsChangeMax.getHostKelly()){
					String ruleType = "K5";
					MatchScore matchScore = new MatchScore();
					matchScore.setMatchName(match.getMatchName());
					matchScore.setMatchSeq(match.getMatchSeq());
					matchScore.setOkUrlDate(match.getOkUrlDate());
					matchScore.setOddsCorpName(oddsChange1.getOddsCorpName());
					matchScore.setHostGoals(match.getHostGoals());
					matchScore.setVisitingGoals(match.getVisitingGoals());
					matchScore.setRuleType(ruleType);
					String matchResult = "未开赛";
					Integer hostGoals = match.getHostGoals();
					Integer visitingGoals = match.getVisitingGoals();
					if (hostGoals != null && visitingGoals != null) {
						if (hostGoals > visitingGoals) {
							matchResult = "胜";
						} else if (match.getHostGoals() == match
								.getVisitingGoals()) {
							matchResult = "平";
						} else {
							matchResult = "负";
						}
					}
					matchScore.setMatchResult(matchResult);
					k5Scores.add(matchScore);
				}
				
				// K6
				if(oddsChange1.getVisitingKelly() < oddsChangeMax.getVisitingKelly()){
					String ruleType = "K6";
					MatchScore matchScore = new MatchScore();
					matchScore.setMatchName(match.getMatchName());
					matchScore.setMatchSeq(match.getMatchSeq());
					matchScore.setOkUrlDate(match.getOkUrlDate());
					matchScore.setOddsCorpName(oddsChange1.getOddsCorpName());
					matchScore.setHostGoals(match.getHostGoals());
					matchScore.setVisitingGoals(match.getVisitingGoals());
					matchScore.setRuleType(ruleType);
					String matchResult = "未开赛";
					Integer hostGoals = match.getHostGoals();
					Integer visitingGoals = match.getVisitingGoals();
					if (hostGoals != null && visitingGoals != null) {
						if (hostGoals > visitingGoals) {
							matchResult = "胜";
						} else if (match.getHostGoals() == match
								.getVisitingGoals()) {
							matchResult = "平";
						} else {
							matchResult = "负";
						}
					}
					matchScore.setMatchResult(matchResult);
					k6Scores.add(matchScore);
				}

				//K7
				if (oddsChange1.getEvenKelly() < oddsChangeMax.getEvenKelly()
						&& oddsChange1.getHostKelly() >= oddsChangeMax
								.getHostKelly()
						&& oddsChange1.getVisitingKelly() >= oddsChangeMax
								.getVisitingKelly()) {
					String ruleType = "K7";
					MatchScore matchScore = new MatchScore();
					matchScore.setMatchName(match.getMatchName());
					matchScore.setMatchSeq(match.getMatchSeq());
					matchScore.setOkUrlDate(match.getOkUrlDate());
					matchScore.setOddsCorpName(oddsChange1.getOddsCorpName());
					matchScore.setHostGoals(match.getHostGoals());
					matchScore.setVisitingGoals(match.getVisitingGoals());
					matchScore.setRuleType(ruleType);
					String matchResult = "未开赛";
					Integer hostGoals = match.getHostGoals();
					Integer visitingGoals = match.getVisitingGoals();
					if (hostGoals != null && visitingGoals != null) {
						if (hostGoals > visitingGoals) {
							matchResult = "胜";
						} else if (match.getHostGoals() == match
								.getVisitingGoals()) {
							matchResult = "平";
						} else {
							matchResult = "负";
						}
					}
					matchScore.setMatchResult(matchResult);
					k7Scores.add(matchScore);
				}
					
				//K8
				if(oddsChange1.getEvenKelly() < oddsChangeMax.getEvenKelly()){
					String ruleType = "K8";
					MatchScore matchScore = new MatchScore();
					matchScore.setMatchName(match.getMatchName());
					matchScore.setMatchSeq(match.getMatchSeq());
					matchScore.setOkUrlDate(match.getOkUrlDate());
					matchScore.setOddsCorpName(oddsChange1.getOddsCorpName());
					matchScore.setHostGoals(match.getHostGoals());
					matchScore.setVisitingGoals(match.getVisitingGoals());
					matchScore.setRuleType(ruleType);
					String matchResult = "未开赛";
					Integer hostGoals = match.getHostGoals();
					Integer visitingGoals = match.getVisitingGoals();
					if (hostGoals != null && visitingGoals != null) {
						if (hostGoals > visitingGoals) {
							matchResult = "胜";
						} else if (match.getHostGoals() == match
								.getVisitingGoals()) {
							matchResult = "平";
						} else {
							matchResult = "负";
						}
					}
					matchScore.setMatchResult(matchResult);
					k8Scores.add(matchScore);
				}
				
				// 赔率变化次数为2,3,4, 最近一次变化导致kelly指数的变化: 比如host:从 2.0 调整到 1.8, kelly指数从 0.93调整到0.90, 那么kelly指数变化: 0.90 - (0.90/1.8)*2
				matchCountMap.put("matchSeq", match.getMatchSeq());
				analyseLatestKellyChange(europeOddsChangeList, matchCountMap);
			}
			// 插入LOT_KELLY_MATCH_COUNT
			insertLatestKellyChange(matchCountMap, match.getOkUrlDate(), match.getMatchSeq(), jobTypesOfA.get(match.getMatchSeq()));
		}
		
		showResultK3(k3Scores);
		LOGGER.info("process K3: analyse success, eclipsed "
				+ (System.currentTimeMillis() - begin) + " ms...");
		
		showResultK3(k4Scores);
		LOGGER.info("process K4: analyse success, eclipsed "
				+ (System.currentTimeMillis() - begin) + " ms...");
		
		showResultK3(k5Scores);
		LOGGER.info("process K5: analyse success, eclipsed "
				+ (System.currentTimeMillis() - begin) + " ms...");
		
		showResultK3(k6Scores);
		LOGGER.info("process K6: analyse success, eclipsed "
				+ (System.currentTimeMillis() - begin) + " ms...");
		
		showResultK3(k7Scores);
		LOGGER.info("process K7: analyse success, eclipsed "
				+ (System.currentTimeMillis() - begin) + " ms...");
		
		showResultK3(k8Scores);
		LOGGER.info("process K8: analyse success, eclipsed "
				+ (System.currentTimeMillis() - begin) + " ms...");
	}
	
	/**
	 *  K2, K3(k4) 线程方式;
	 */
	public void kellyAnalyseK23Thread(List<Match> matches, String matchDir, int beginMatchSeq, int endMatchSeq, Set<Integer> limitedMatchSeqs,
			Map<Integer, String> jobTypes, String okUrlDate){
		long beginTime = System.currentTimeMillis();

		// 最大5个Thread, 多余的放入queue, 直至queue满才会new Thread.
		ThreadPoolExecutor pool = new ThreadPoolExecutor(3, 5, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		KellyAnalyseK2Thread k2Thread = new KellyAnalyseK2Thread();
		k2Thread.setAnalyseService(this);
		k2Thread.setBeginMatchSeq(beginMatchSeq);
		k2Thread.setEndMatchSeq(endMatchSeq);
		k2Thread.setMatchDir(matchDir);
		k2Thread.setLimitedMatchSeqs(limitedMatchSeqs);
		k2Thread.setMatches(matches);
		pool.execute(k2Thread);
		
		KellyAnalyseK3Thread k3Thread = new KellyAnalyseK3Thread();
		k3Thread.setAnalyseService(this);
		k3Thread.setBeginMatchSeq(beginMatchSeq);
		k3Thread.setEndMatchSeq(endMatchSeq);
		k3Thread.setMatchDir(matchDir);
		k3Thread.setLimitedMatchSeqs(limitedMatchSeqs);
		k3Thread.setMatches(matches);
		k3Thread.setJobTypesOfA(jobTypes);
		pool.execute(k3Thread);
		
		// 新增一个 Thread.
		KellyAnalyseN3Thread n3Thread = new KellyAnalyseN3Thread();
		n3Thread.setAnalyseService(this);
		n3Thread.setBeginMatchSeq(beginMatchSeq);
		n3Thread.setEndMatchSeq(endMatchSeq);
		n3Thread.setMatchDir(matchDir);
		n3Thread.setLimitedMatchSeqs(limitedMatchSeqs);
		n3Thread.setJobTypes(jobTypes);
		n3Thread.setOkUrlDate(okUrlDate);
		n3Thread.setMatches(matches);
		pool.execute(n3Thread);
		
		for(;;){
			if(pool.getActiveCount() > 0){
				LOGGER.info("getActiveCount: " + pool.getActiveCount() + "; getCompletedTaskCount: " + pool.getCompletedTaskCount() + 
						"; getLargestPoolSize: " + pool.getLargestPoolSize() + "; getMaximumPoolSize: " + pool.getMaximumPoolSize() +
						"; getTaskCount: " + pool.getTaskCount());
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					LOGGER.error(e);
				}
			}else {
				break;
			}
		}
		
		LOGGER.info("process all success, eclipsed "
		+ (System.currentTimeMillis() - beginTime)/(1000 * 60) + " min.");
	
	}
	
	private void analyseLatestKellyChange(List<EuropeOddsChange> europeOddsChangeList, Map<String, Integer> matchCountMap){
		if(europeOddsChangeList.size() <= 2 || europeOddsChangeList.size() >= 6){
			return;
		}
		EuropeOddsChange change2 = europeOddsChangeList.get(1);
		EuropeOddsChange change3 = europeOddsChangeList.get(2);
		Float hostOdds2 = change2.getHostOdds();
		Float evenOdds2 = change2.getEvenOdds();
		Float visitingOdds2 = change2.getVisitingOdds();
		Float hostKelly2 = change2.getHostKelly();
		Float evenKelly2 = change2.getEvenKelly();
		Float visitingKelly2 = change2.getVisitingKelly();
		Float hostOdds3 = change3.getHostOdds();
		Float evenOdds3 = change3.getEvenOdds();
		Float visitingOdds3 = change3.getVisitingOdds();
		Float hostKellyChange = Math.round((hostKelly2 - hostKelly2 / hostOdds2 * hostOdds3) * 100)/100.0f;
		Float evenKellyChange = Math.round((evenKelly2 - evenKelly2 / evenOdds2 * evenOdds3) * 100)/100.0f;
		Float visitingKellyChange = Math.round((visitingKelly2 - visitingKelly2 / visitingOdds2 * visitingOdds3) * 100)/100.0f;
		// K3a: 主胜.  
		if(hostKellyChange < -0.02f && evenKellyChange <= 0 && visitingKellyChange > 0.02f){
			matchCountMap.put("K3a", matchCountMap.get("K3a") + 1);
		}
		// K4a: 客胜
		if(hostKellyChange > 0.02f && evenKellyChange >= 0 && visitingKellyChange < -0.02f){
			matchCountMap.put("K4a", matchCountMap.get("K4a") + 1);
		}
		// K5a:
		if(hostKellyChange < -0.02f){
			matchCountMap.put("K5a", matchCountMap.get("K5a") + 1);
		}
		// K6a
		if(visitingKellyChange < - 0.02f){
			matchCountMap.put("K6a", matchCountMap.get("K6a") + 1);
		}
		// K7a
		if(evenKellyChange < -0.02f && hostKellyChange >= 0 && visitingKellyChange >= 0){
			matchCountMap.put("K7a", matchCountMap.get("K7a") + 1);
		}
		// K8a
		if(evenKellyChange < -0.02f){
			matchCountMap.put("K8a", matchCountMap.get("K8a") + 1);
		}
	}
	
	private void insertLatestKellyChange(Map<String, Integer>matchCountMap, String okUrlDate, Integer matchSeq, String jobType){
		List<KellyMatchCount> list = new ArrayList<KellyMatchCount>();
		Timestamp timestamp = new Timestamp(Calendar.getInstance()
				.getTimeInMillis());
		// test by leslie begin
		LOGGER.info("okUrlDate: " + okUrlDate + " matchSeq:" + matchSeq + " jobType:" + jobType + " matchCountMap:" + matchCountMap);
		// test by leslie end
		KellyMatchCount matchCount1 = new KellyMatchCount();
		matchCount1.setOkUrlDate(okUrlDate);
		matchCount1.setMatchSeq(matchSeq);
		matchCount1.setJobType(jobType);
		matchCount1.setRuleType("K3a");
		matchCount1.setCorpCount(matchCountMap.get("K3a"));
		matchCount1.setTimestamp(timestamp);
		list.add(matchCount1);
		
		KellyMatchCount matchCount2 = new KellyMatchCount();
		matchCount2.setOkUrlDate(okUrlDate);
		matchCount2.setMatchSeq(matchSeq);
		matchCount2.setJobType(jobType);
		matchCount2.setRuleType("K4a");
		matchCount2.setCorpCount(matchCountMap.get("K4a"));
		matchCount2.setTimestamp(timestamp);
		list.add(matchCount2);
		
		KellyMatchCount matchCount3 = new KellyMatchCount();
		matchCount3.setOkUrlDate(okUrlDate);
		matchCount3.setMatchSeq(matchSeq);
		matchCount3.setJobType(jobType);
		matchCount3.setRuleType("K5a");
		matchCount3.setCorpCount(matchCountMap.get("K5a"));
		matchCount3.setTimestamp(timestamp);
		list.add(matchCount3);
		
		KellyMatchCount matchCount4 = new KellyMatchCount();
		matchCount4.setOkUrlDate(okUrlDate);
		matchCount4.setMatchSeq(matchSeq);
		matchCount4.setJobType(jobType);
		matchCount4.setRuleType("K6a");
		matchCount4.setCorpCount(matchCountMap.get("K6a"));
		matchCount4.setTimestamp(timestamp);
		list.add(matchCount4);
		
		KellyMatchCount matchCount5 = new KellyMatchCount();
		matchCount5.setOkUrlDate(okUrlDate);
		matchCount5.setMatchSeq(matchSeq);
		matchCount5.setJobType(jobType);
		matchCount5.setRuleType("K7a");
		matchCount5.setCorpCount(matchCountMap.get("K7a"));
		matchCount5.setTimestamp(timestamp);
		list.add(matchCount5);
		
		KellyMatchCount matchCount6 = new KellyMatchCount();
		matchCount6.setOkUrlDate(okUrlDate);
		matchCount6.setMatchSeq(matchSeq);
		matchCount6.setJobType(jobType);
		matchCount6.setRuleType("K8a");
		matchCount6.setCorpCount(matchCountMap.get("K8a"));
		matchCount6.setTimestamp(timestamp);
		list.add(matchCount6);
		
		kellyMatchCountService.insertMatchCountBatch(list);
	}
	
	/**
	 * 分析亚盘的kelly变化, 与B0时的相比.
	 */
	public void asiaOddsAnalyse(String matchDir, Map<Integer, String> jobTypes, int beginMatchSeq, int endMatchSeq,
			Set<Integer> limitedMatchSeqs, String okUrlDate, List<Match> matches){
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(
				matchDir, OkConstant.MATCH_FILE_NAME);
		LOGGER.info("matchHtmlFiles size: " + matchHtmlFiles.size());

		/*
		 * 获取Match对象;
		 */
		if(matches == null || matches.isEmpty()){
			matches = new ArrayList<Match>(2^10);
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
		}
		
		// 查询已经记录的
		Set<String> seqJobTypeSet = asiaOddsTrendsService.queryAsiaOddsTrendsByOkUrlDateInSet(okUrlDate);

		File asiaOddsHtml = null;
		AsiaOddsTrends asiaOddsTrendsInit = new AsiaOddsTrends();
		asiaOddsTrendsInit.setOkUrlDate(okUrlDate);
		for (Match match : matches) {
			// 从指定matchSeq开始.
			int matchSeq = match.getMatchSeq();
			String jobType = jobTypes.get(matchSeq);
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
			
			if(seqJobTypeSet.contains(matchSeq + "_" + jobType)){
				continue;
            }
			
			LOGGER.info("process match: " + match.getMatchSeq());
			
			asiaOddsTrendsInit.setMatchSeq(matchSeq);
			asiaOddsTrendsInit.setJobType(jobType);
			asiaOddsHtml = new File(matchDir + OkConstant.ASIA_ODDS_FILE_NAME_BASE + "_" + match.getMatchSeq() + ".html");
			if(!asiaOddsHtml.exists()){
				continue;
			}
			asiaOddsTrendsService.parseAsiaOddsTrendsFromFile(asiaOddsHtml, asiaOddsTrendsInit);
		}
	}
	
	public void indexStatsAnalyse(String matchDir, Map<Integer, String> jobTypes, int beginMatchSeq, int endMatchSeq,
			Set<Integer> limitedMatchSeqs, String okUrlDate, List<Match> matches){
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(
				matchDir, OkConstant.MATCH_FILE_NAME);
		LOGGER.info("matchHtmlFiles size: " + matchHtmlFiles.size());

		/*
		 * 获取Match对象;
		 */
		if(matches == null || matches.isEmpty()){
			matches = new ArrayList<Match>(2^10);
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
		}

		String encoding = "gb2312";
		IndexStats indexStatsInit = new IndexStats();
		indexStatsInit.setOkUrlDate(okUrlDate);
		for (Match match : matches) {
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
			LOGGER.info("process match: " + match.getMatchSeq());
			
			indexStatsInit.setMatchSeq(matchSeq);
			indexStatsInit.setJobType(jobTypes.get(matchSeq));
			String indexStatsUrl = "http://www.okooo.com/soccer/match/" + match.getOkMatchId() + "/okoooexponent/xmlData/";
			indexStatsService.parseIndexStats(indexStatsUrl, encoding, indexStatsInit);
		}
	}
	
	/**
	 * 解析让球页面信息, 插入数据库.
	 */
	public void euroHandicapAnalyse(String matchDir, Map<Integer, String> jobTypes, int beginMatchSeq, int endMatchSeq,
			Set<Integer> limitedMatchSeqs, String okUrlDate, List<Match> matches){
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(
				matchDir, OkConstant.MATCH_FILE_NAME);
		LOGGER.info("matchHtmlFiles size: " + matchHtmlFiles.size());

		/*
		 * 获取Match对象;
		 */
		if(matches == null || matches.isEmpty()){
			matches = new ArrayList<Match>(2^10);
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
		}

		File euroHandicapHtml = null;
		EuroOddsHandicap euroOddsHandicapInit = new EuroOddsHandicap();
		euroOddsHandicapInit.setOkUrlDate(okUrlDate);
		for (Match match : matches) {
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
			LOGGER.info("process match: " + match.getMatchSeq());
			
			euroOddsHandicapInit.setMatchSeq(matchSeq);
			euroOddsHandicapInit.setJobType(jobTypes.get(matchSeq));
			euroHandicapHtml = new File(matchDir + OkConstant.EURO_HANDICAP_FILE_NAME_BASE + "_" + match.getMatchSeq() + ".html");
			euroOddsHandicapService.parseEuroOddsHandicapFromFile(euroHandicapHtml, euroOddsHandicapInit);
		}
	}
	
	/**
	 * 展示结果;
	 * @param scores
	 */
	private void showResult(List<MatchScore> scores){
		showResultDetail(scores);
//		showResultA(scores);
//		showResultB(scores);
//		showResultAB(scores);
//		showResultC(scores);
//		showResultK1(scores);
		
//		showResultK3(scores);
	}
	
	private void showResultDetail(List<MatchScore> scores){
		// 显示scores
		StringBuilder sb = new StringBuilder();
		sb.append("\n99家平均:\n");
		for(MatchScore matchScore : scores){
			Float hostC = matchScore.getCompIndexs() == null ? null : matchScore.getCompIndexs().get("host");
			Float evenC = matchScore.getCompIndexs() == null ? null : matchScore.getCompIndexs().get("even");
			Float visitingC = matchScore.getCompIndexs() == null ? null : matchScore.getCompIndexs().get("visiting");
			sb.append(matchScore.getMatchSeq()).append(" ")
			.append(matchScore.getMatchName()).append(" ")
			.append(matchScore.getOkUrlDate()).append(" ")
			.append("[A]").append(" ")
			.append(matchScore.getTotalScoreA()).append(" ")
			.append(matchScore.getAverageA()).append(" ")
			.append("[B]").append(" ")
			.append(matchScore.getScoreB()).append(" ")
			.append("[C]").append(" ")
			.append(hostC).append(" ")
			.append(evenC).append(" ")
			.append(visitingC).append(" ")
			.append(matchScore.getHostGoals()).append(":").append(matchScore.getVisitingGoals()).append(" ")
			.append(matchScore.getMatchResult()).append(" ")
			.append("\n");
			
		}
		LOGGER.info(sb.toString());
	}
	
	public void showResultA(List<MatchScore> scores){
		// 显示scores
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("rule A:\n");
		int allACount = 0;
		int winACount = 0;
		int evenACount = 0;
		int negaACount = 0;
		
	    String[] matchNameArr = {"英超", "英冠", "英甲", "法甲", "德甲", "西甲", "意甲", "葡超"};
	    List<String> matchNames = Arrays.asList(matchNameArr);
	    if(matchNames != null && !matchNames.isEmpty()){
	    	sb.append("limited match: " + matchNames + "\n");
	    }
		
		for(MatchScore matchScore : scores){
			
			if(matchNames != null && !matchNames.isEmpty() && !matchNames.contains(matchScore.getMatchName())){
				continue;
			}
			/*
			 * 规则A
			 */
			if(matchScore.getAverageA() > 1){
				allACount++;
			}
			if("胜".equals(matchScore.getMatchResult()) && matchScore.getAverageA() > 1){
				winACount++;
			}
			if("平".equals(matchScore.getMatchResult()) && matchScore.getAverageA() > 1){
				evenACount++;
			}
			if("负".equals(matchScore.getMatchResult()) && matchScore.getAverageA() > 1){
				negaACount++;
			}
			
		}
		
		sb.append("winACount: ").append(winACount).append(" evenACount: ").append(evenACount).append(" negaACount: ").append(negaACount)
		.append(" allACount: ").append(allACount).append(" 胜率: ").append((double)winACount/(double)allACount)
		.append(" 胜平率: ").append((double)(winACount + evenACount)/(double)allACount).append("\n");
		LOGGER.info(sb.toString());
	}
	
	public void showResultB(List<MatchScore> scores){
		// 显示scores
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("rule B:\n");
		
		int allBCount = 0;
		int winBCount = 0;
		int evenBCount = 0;
		int negaBCount = 0;
		
	    String[] matchNameArr = {"英超", "英冠", "英甲", "法甲", "德甲", "西甲", "意甲", "葡超"};
	    List<String> matchNames = Arrays.asList(matchNameArr);
	    if(matchNames != null && !matchNames.isEmpty()){
	    	sb.append("limited match: " + matchNames + "\n");
	    }
	    
		for(MatchScore matchScore : scores){
			if(matchNames != null && !matchNames.isEmpty() && !matchNames.contains(matchScore.getMatchName())){
				continue;
			}
			
			/*
			 * 规则B
			 */
			if(matchScore.getScoreB() > 1){
				allBCount++;
			}
			if("胜".equals(matchScore.getMatchResult()) && matchScore.getScoreB() > 1){
				winBCount++;
			}
			if("平".equals(matchScore.getMatchResult()) && matchScore.getScoreB() > 1){
				evenBCount++;
			}
			if("负".equals(matchScore.getMatchResult()) && matchScore.getScoreB() > 1){
				negaBCount++;
			}
			
		}
		
		sb.append("winBCount: ").append(winBCount).append(" evenBCount: ").append(evenBCount).append(" negaBCount: ").append(negaBCount)
		.append(" allBCount: ").append(allBCount).append(" 胜率: ").append((double)winBCount/(double)allBCount)
		.append(" 胜平率: ").append((double)(winBCount + evenBCount)/(double)allBCount).append("\n");
		LOGGER.info(sb.toString());
	}
	
	public void showResultAB(List<MatchScore> scores){
		// 显示scores
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("rule A && B: \n");
		
		int allABCount = 0;
		int winABCount = 0;
		int evenABCount = 0;
		int negaABCount = 0;
		
	    String[] matchNameArr = {"英超", "英冠", "英甲", "法甲", "德甲", "西甲", "意甲", "葡超"};
	    List<String> matchNames = Arrays.asList(matchNameArr);
	    if(matchNames != null && !matchNames.isEmpty()){
	    	sb.append("limited match: " + matchNames + "\n");
	    }
	    
		for(MatchScore matchScore : scores){
			
			if(matchNames != null && !matchNames.isEmpty() && !matchNames.contains(matchScore.getMatchName())){
				continue;
			}
			
			/*
			 * A && B 
			 */
			if(matchScore.getAverageA() > 1 && matchScore.getScoreB() > 1){
				allABCount++;
			}
			if("胜".equals(matchScore.getMatchResult()) && matchScore.getAverageA() > 1 && matchScore.getScoreB() > 1){
				winABCount++;
			}
			if("平".equals(matchScore.getMatchResult()) && matchScore.getAverageA() > 1 && matchScore.getScoreB() > 1){
				evenABCount++;
			}
			if("负".equals(matchScore.getMatchResult()) && matchScore.getAverageA() > 1 && matchScore.getScoreB() > 1){
				negaABCount++;
			}
			
		}
		
		sb.append("winABCount: ").append(winABCount).append(" evenABCount: ").append(evenABCount).append(" negaABCount: ").append(negaABCount)
		.append(" allABCount: ").append(allABCount).append(" 胜率: ").append((double)winABCount/(double)allABCount)
		.append(" 胜平率: ").append((double)(winABCount + evenABCount)/(double)allABCount).append("\n");
		
		LOGGER.info(sb.toString());
	}
	
	/**
	 * 存在规则B， 但是不存在规则A.
	 * @param scores
	 */
	public void showResultBNoA(List<MatchScore> scores) {
		// 显示scores
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("rule 有B, 无A: \n");

		int allBNoACount = 0;
		int winBNoACount = 0;
		int evenBNoACount = 0;
		int negaBNoACount = 0;
		
	    String[] matchNameArr = {"英超", "英冠", "英甲", "法甲", "德甲", "西甲", "意甲", "葡超"};
	    List<String> matchNames = Arrays.asList(matchNameArr);
	    if(matchNames != null && !matchNames.isEmpty()){
	    	sb.append("limited match: " + matchNames + "\n");
	    }
	    
		for (MatchScore matchScore : scores) {
			if(matchNames != null && !matchNames.isEmpty() && !matchNames.contains(matchScore.getMatchName())){
				continue;
			}
			
			/*
			 * 有B, 无A, 保证 hostOdds <= 2;
			 */
			if (matchScore.getAverageA() < 1 && matchScore.getScoreB() > 1
					&& matchScore.getHostOdds() <= 2) {
				allBNoACount++;
			}
			if ("胜".equals(matchScore.getMatchResult())
					&& matchScore.getAverageA() < 1
					&& matchScore.getScoreB() > 1
					&& matchScore.getHostOdds() <= 2) {
				winBNoACount++;
			}
			if ("平".equals(matchScore.getMatchResult())
					&& matchScore.getAverageA() < 1
					&& matchScore.getScoreB() > 1
					&& matchScore.getHostOdds() <= 2) {
				evenBNoACount++;
			}
			if ("负".equals(matchScore.getMatchResult())
					&& matchScore.getAverageA() < 1
					&& matchScore.getScoreB() > 1
					&& matchScore.getHostOdds() <= 2) {
				negaBNoACount++;
			}
		}
		
		sb.append("winBNoACount: ")
				.append(winBNoACount)
				.append(" evenBNoACount: ")
				.append(evenBNoACount)
				.append(" negaBNoACount: ")
				.append(negaBNoACount)
				.append(" allBNoACount: ")
				.append(allBNoACount)
				.append(" 胜率: ")
				.append((double) winBNoACount / (double) allBNoACount)
				.append(" 胜平率: ")
				.append((double) (winBNoACount + evenBNoACount)
						/ (double) allBNoACount).append("\n");

		LOGGER.info(sb.toString());
	}
	
	public void showResultC(List<MatchScore> scores){
		// 显示scores
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("rule C: \n");

		int allCount = 0;
		
	    String[] matchNameArr = {"英超", "英冠", "英甲", "法甲", "德甲", "西甲", "意甲", "葡超"};
	    List<String> matchNames = Arrays.asList(matchNameArr);
	    if(matchNames != null && !matchNames.isEmpty()){
	    	sb.append("limited match: " + matchNames + "\n");
	    }
	    
		for (MatchScore matchScore : scores) {
			if(matchNames != null && !matchNames.isEmpty() && !matchNames.contains(matchScore.getMatchName())){
				continue;
			}
			
			Map<String, Float> compIndexs = matchScore.getCompIndexs();
			
			TreeMap<String, Float> compTreeMap = new TreeMap<String, Float>();
			compTreeMap.putAll(compIndexs);
			if(compIndexs != null && compIndexs.get("host") != null){
				allCount++;
			}
			LOGGER.info("compTreeMap: " + compTreeMap + " allCount: " + allCount);
		}

		LOGGER.info(sb.toString());
	
	}
	
	private void showResultK2(List<MatchScore> scores){
		Map<Integer, String> resultMap = new HashMap<Integer, String>();
		// rule K2: 分析当天哪些公司的哪些联赛正确率高. KEY 是 {联赛名称}_{公司名称}  VALUE MAP 的key分别是 allCount, winCount, evenCount, negaCount.
		Map<String, Map<String, Integer>> matchCorpProbMap = new TreeMap<String, Map<String, Integer>>();
		// rule K2: 记录正确的matchSeq;  key 是 {联赛名称}_{公司名称}   value MAP 的key分别 是 allSeq, winSeq
		// 与 matchCorpProbMap 的key是相同的, 个数和值都一样.
		Map<String, Map<String, List<Integer>>> matchCorpSeq = new HashMap<String, Map<String, List<Integer>>>();
		// 显示scores
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for(MatchScore matchScore : scores){
			KellyRule kellyRule = matchScore.getKellyRule();
			sb.append(matchScore.getMatchSeq()).append(" ")
			.append(matchScore.getMatchName()).append(" ")
			.append(matchScore.getOkUrlDate()).append(" ")
			.append(kellyRule.getOddsCorpName()).append(" ")
			.append(kellyRule.getCount()).append(" ")
			.append(kellyRule.getWinCount()).append(" ")
			.append(kellyRule.getWinProb()).append(" ")
			.append(matchScore.getHostOdds()).append(" ")
			.append(matchScore.getHostGoals()).append(":").append(matchScore.getVisitingGoals()).append(" ")
			.append(matchScore.getMatchResult()).append(" ")
			.append("\n");
			
			resultMap.put(matchScore.getMatchSeq(), matchScore.getMatchResult());
			
			String matchName = matchScore.getMatchName();
			String corpName = kellyRule.getOddsCorpName();
			String matchResult = matchScore.getMatchResult();
			String key = matchName + "_" + corpName;
			Map<String, Integer> result = matchCorpProbMap.get(key);
			if(matchCorpProbMap.containsKey(key)){
				Integer allCount = result.get("allCount");
				Integer winCount = result.get("winCount");
				Integer evenCount = result.get("evenCount");
				Integer negaCount = result.get("negaCount");
				result.put("allCount", allCount + 1);
				// 记录 matchSeq.
				if(matchCorpSeq.containsKey(key)){
					Map<String, List<Integer>> matchSeq = matchCorpSeq.get(key);
					List<Integer> allSeqList = matchSeq.get("allSeq");
					if(allSeqList == null){
						allSeqList = new ArrayList<Integer>();
					}
					allSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("allSeq", allSeqList);
					matchCorpSeq.put(key, matchSeq);
				}else{
					Map<String, List<Integer>> matchSeq = new HashMap<String, List<Integer>>();
					List<Integer> allSeqList = new ArrayList<Integer>();
					allSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("allSeq", allSeqList);
					matchCorpSeq.put(key, matchSeq);
				}
				
				if("胜".equals(matchResult)){
					result.put("winCount", winCount + 1);
					// 记录胜的matchSeq
					Map<String, List<Integer>> matchSeq = matchCorpSeq.get(key);
					List<Integer> winSeqList = matchSeq.get("winSeq");
					if(winSeqList == null){
						winSeqList = new ArrayList<Integer>();
					}
					winSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("winSeq", winSeqList);
					matchCorpSeq.put(key, matchSeq);
				}else if("平".equals(matchResult)){
					result.put("evenCount", evenCount + 1);
					// 记录胜的matchSeq
					Map<String, List<Integer>> matchSeq = matchCorpSeq.get(key);
					List<Integer> evenSeqList = matchSeq.get("evenSeq");
					if(evenSeqList == null){
						evenSeqList = new ArrayList<Integer>();
					}
					evenSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("evenSeq", evenSeqList);
					matchCorpSeq.put(key, matchSeq);
				}else if("负".equals(matchResult)){
					result.put("negaCount", negaCount + 1);
					// 记录胜的matchSeq
					Map<String, List<Integer>> matchSeq = matchCorpSeq.get(key);
					List<Integer> negaSeqList = matchSeq.get("negaSeq");
					if(negaSeqList == null){
						negaSeqList = new ArrayList<Integer>();
					}
					negaSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("negaSeq", negaSeqList);
					matchCorpSeq.put(key, matchSeq);
				}
			}else{
				result = new HashMap<String ,Integer>();
				Integer allCount = 0;
				Integer winCount = 0;
				Integer evenCount = 0;
				Integer negaCount = 0;
				result.put("allCount", allCount + 1);
				
				// 记录 matchSeq.
				if(matchCorpSeq.containsKey(key)){
					Map<String, List<Integer>> matchSeq = matchCorpSeq.get(key);
					List<Integer> allSeqList = matchSeq.get("allSeq");
					if(allSeqList == null){
						allSeqList = new ArrayList<Integer>();
					}
					allSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("allSeq", allSeqList);
					matchCorpSeq.put(key, matchSeq);
				}else{
					Map<String, List<Integer>> matchSeq = new HashMap<String, List<Integer>>();
					List<Integer> allSeqList = new ArrayList<Integer>();
					allSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("allSeq", allSeqList);
					matchCorpSeq.put(key, matchSeq);
				}
				
				if("胜".equals(matchResult)){
					result.put("winCount", winCount + 1);
					result.put("evenCount", evenCount);
					result.put("negaCount", negaCount);
					
					// 记录胜的matchSeq
					Map<String, List<Integer>> matchSeq = matchCorpSeq.get(key);
					List<Integer> winSeqList = matchSeq.get("winSeq");
					if(winSeqList == null){
						winSeqList = new ArrayList<Integer>();
					}
					winSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("winSeq", winSeqList);
					matchCorpSeq.put(key, matchSeq);
				}else if("平".equals(matchResult)){
					result.put("winCount", winCount);
					result.put("evenCount", evenCount + 1);
					result.put("negaCount", negaCount);
					
					// 记录胜的matchSeq
					Map<String, List<Integer>> matchSeq = matchCorpSeq.get(key);
					List<Integer> evenSeqList = matchSeq.get("evenSeq");
					if(evenSeqList == null){
						evenSeqList = new ArrayList<Integer>();
					}
					evenSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("evenSeq", evenSeqList);
					matchCorpSeq.put(key, matchSeq);
				}else if("负".equals(matchResult)){
					result.put("winCount", winCount);
					result.put("evenCount", evenCount);
					result.put("negaCount", negaCount + 1);
					
					// 记录胜的matchSeq
					Map<String, List<Integer>> matchSeq = matchCorpSeq.get(key);
					List<Integer> negaSeqList = matchSeq.get("negaSeq");
					if(negaSeqList == null){
						negaSeqList = new ArrayList<Integer>();
					}
					negaSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("negaSeq", negaSeqList);
					matchCorpSeq.put(key, matchSeq);
				}
			}
			matchCorpProbMap.put(key, result);
		}
		sb.append("Rule K2:\n");
		
		Set<Entry<Integer, String>> entrySet = resultMap.entrySet();
		int allCount = 0;
		int winCount = 0;
		int evenCount = 0;
		int negaCount = 0;
		for(Entry<Integer, String> entry : entrySet){
			allCount++;
			if("胜".equals(entry.getValue())){
				winCount++;
			}
			if("平".equals(entry.getValue())){
				evenCount++;
			}
			if("负".equals(entry.getValue())){
				negaCount++;
			}
		}
		
		sb.append("winCount: ").append(winCount).append(" evenCount: ").append(evenCount).append(" negaCount: ").append(negaCount)
		.append(" allCount: ").append(allCount).append(" 胜率: ").append((double)winCount/(double)allCount)
		.append(" 胜平率: ").append((double)(winCount + evenCount)/(double)allCount).append("\n");
		
		// 各个杯赛的概率
		Set<String> matchCorpProbKeys = matchCorpProbMap.keySet();
		for(String key : matchCorpProbKeys){
			Map<String, Integer> result = matchCorpProbMap.get(key);
			Map<String, List<Integer>> seqMap = matchCorpSeq.get(key);
			Integer allCountEntry = result.get("allCount");
			if(allCountEntry < 2){
				continue;
			}
			sb.append(key).append(" ").append(result.get("allCount")).append(" ")
			.append(result.get("winCount")).append(" ")
			.append(result.get("evenCount")).append(" ")
			.append(result.get("negaCount")).append("           ")
			.append("allSeq:").append(seqMap.get("allSeq")).append(" ")
			.append("winSeq:").append(seqMap.get("winSeq")).append(" ")
			.append("\n");
		}
		
		// 插入 KELLY_CORP_RESULT.
		String okUrlDate = scores.get(0).getOkUrlDate();
		insertKellyResult(okUrlDate, "K2", matchCorpProbMap, matchCorpSeq);
		
//		LOGGER.info(sb.toString());
		OkParseUtils.persistByStr(new File("/home/leslie/MyProject/OkParse/charts/my/KellyIndex/allCorpsKelly1_201409/K2/" 
		+ scores.get(0).getOkUrlDate() + ".txt"), sb.toString());
	}
	
	private void showResultK3(List<MatchScore> scores){
		// rule K2: 分析当天哪些公司的哪些联赛正确率高. KEY 是 {联赛名称}_{公司名称}  VALUE MAP 的key分别是 allCount, winCount, evenCount, negaCount.
		Map<String, Map<String, Integer>> matchCorpProbMap = new TreeMap<String, Map<String, Integer>>();
		// rule K2: 记录正确的matchSeq;  key 是 {联赛名称}_{公司名称}   value MAP 的key分别 是 allSeq, winSeq
		// 与 matchCorpProbMap 的key是相同的, 个数和值都一样.
		Map<String, Map<String, List<Integer>>> matchCorpSeq = new HashMap<String, Map<String, List<Integer>>>();
		for(MatchScore matchScore : scores){
			String matchName = matchScore.getMatchName();
			String corpName = matchScore.getOddsCorpName();
			String matchResult = matchScore.getMatchResult();
			String key = matchName + "_" + corpName;
			Map<String, Integer> result = matchCorpProbMap.get(key);
			if(matchCorpProbMap.containsKey(key)){
				Integer allCount = result.get("allCount");
				Integer winCount = result.get("winCount");
				Integer evenCount = result.get("evenCount");
				Integer negaCount = result.get("negaCount");
				result.put("allCount", allCount + 1);
				// 记录 matchSeq.
				if(matchCorpSeq.containsKey(key)){
					Map<String, List<Integer>> matchSeq = matchCorpSeq.get(key);
					List<Integer> allSeqList = matchSeq.get("allSeq");
					if(allSeqList == null){
						allSeqList = new ArrayList<Integer>();
					}
					allSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("allSeq", allSeqList);
					matchCorpSeq.put(key, matchSeq);
				}else{
					Map<String, List<Integer>> matchSeq = new HashMap<String, List<Integer>>();
					List<Integer> allSeqList = new ArrayList<Integer>();
					allSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("allSeq", allSeqList);
					matchCorpSeq.put(key, matchSeq);
				}
				
				if("胜".equals(matchResult)){
					result.put("winCount", winCount + 1);
					// 记录胜的matchSeq
					Map<String, List<Integer>> matchSeq = matchCorpSeq.get(key);
					List<Integer> winSeqList = matchSeq.get("winSeq");
					if(winSeqList == null){
						winSeqList = new ArrayList<Integer>();
					}
					winSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("winSeq", winSeqList);
					matchCorpSeq.put(key, matchSeq);
				}else if("平".equals(matchResult)){
					result.put("evenCount", evenCount + 1);
					// 记录胜的matchSeq
					Map<String, List<Integer>> matchSeq = matchCorpSeq.get(key);
					List<Integer> evenSeqList = matchSeq.get("evenSeq");
					if(evenSeqList == null){
						evenSeqList = new ArrayList<Integer>();
					}
					evenSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("evenSeq", evenSeqList);
					matchCorpSeq.put(key, matchSeq);
				}else if("负".equals(matchResult)){
					result.put("negaCount", negaCount + 1);
					// 记录胜的matchSeq
					Map<String, List<Integer>> matchSeq = matchCorpSeq.get(key);
					List<Integer> negaSeqList = matchSeq.get("negaSeq");
					if(negaSeqList == null){
						negaSeqList = new ArrayList<Integer>();
					}
					negaSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("negaSeq", negaSeqList);
					matchCorpSeq.put(key, matchSeq);
				}
			}else{
				result = new HashMap<String ,Integer>();
				Integer allCount = 0;
				Integer winCount = 0;
				Integer evenCount = 0;
				Integer negaCount = 0;
				result.put("allCount", allCount + 1);
				
				// 记录 matchSeq.
				if(matchCorpSeq.containsKey(key)){
					Map<String, List<Integer>> matchSeq = matchCorpSeq.get(key);
					List<Integer> allSeqList = matchSeq.get("allSeq");
					if(allSeqList == null){
						allSeqList = new ArrayList<Integer>();
					}
					allSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("allSeq", allSeqList);
					matchCorpSeq.put(key, matchSeq);
				}else{
					Map<String, List<Integer>> matchSeq = new HashMap<String, List<Integer>>();
					List<Integer> allSeqList = new ArrayList<Integer>();
					allSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("allSeq", allSeqList);
					matchCorpSeq.put(key, matchSeq);
				}
				
				if("胜".equals(matchResult)){
					result.put("winCount", winCount + 1);
					result.put("evenCount", evenCount);
					result.put("negaCount", negaCount);
					
					// 记录胜的matchSeq
					Map<String, List<Integer>> matchSeq = matchCorpSeq.get(key);
					List<Integer> winSeqList = matchSeq.get("winSeq");
					if(winSeqList == null){
						winSeqList = new ArrayList<Integer>();
					}
					winSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("winSeq", winSeqList);
					matchCorpSeq.put(key, matchSeq);
				}else if("平".equals(matchResult)){
					result.put("winCount", winCount);
					result.put("evenCount", evenCount + 1);
					result.put("negaCount", negaCount);
					
					// 记录胜的matchSeq
					Map<String, List<Integer>> matchSeq = matchCorpSeq.get(key);
					List<Integer> evenSeqList = matchSeq.get("evenSeq");
					if(evenSeqList == null){
						evenSeqList = new ArrayList<Integer>();
					}
					evenSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("evenSeq", evenSeqList);
					matchCorpSeq.put(key, matchSeq);
				}else if("负".equals(matchResult)){
					result.put("winCount", winCount);
					result.put("evenCount", evenCount);
					result.put("negaCount", negaCount + 1);
					
					// 记录胜的matchSeq
					Map<String, List<Integer>> matchSeq = matchCorpSeq.get(key);
					List<Integer> negaSeqList = matchSeq.get("negaSeq");
					if(negaSeqList == null){
						negaSeqList = new ArrayList<Integer>();
					}
					negaSeqList.add(matchScore.getMatchSeq());
					matchSeq.put("negaSeq", negaSeqList);
					matchCorpSeq.put(key, matchSeq);
				}
			}
			matchCorpProbMap.put(key, result);
		}
		
		// 插入 KELLY_CORP_RESULT.
		if(scores != null && !scores.isEmpty()){
			String okUrlDate = scores.get(0).getOkUrlDate();
			insertKellyResult(okUrlDate, scores.get(0).getRuleType(), matchCorpProbMap, matchCorpSeq);
		}
	}
	
	/**
	 * 插入 KELLY_CORP_RESULT.
	 * @param okUrlDate
	 * @param matchCorpProbMap
	 * @param matchCorpSeq
	 */
	private void insertKellyResult(String okUrlDate, String ruleType, Map<String, Map<String, Integer>> matchCorpProbMap, Map<String, Map<String, List<Integer>>> matchCorpSeq){
		// 先删除当天所有的.
		kellyCorpResultService.deleteKellyResult(okUrlDate, ruleType);
		
		List<KellyCorpResult> kellyCorpResultList = new ArrayList<KellyCorpResult>();
		Set<String> matchCorpProbKeys = matchCorpProbMap.keySet();
		for(String key : matchCorpProbKeys){
			String matchName = StringUtils.split(key, "_")[0];
			String oddsCorpName =  StringUtils.split(key, "_")[1];
			Map<String, Integer> probMap = matchCorpProbMap.get(key);
			long count = probMap.get("allCount");
			long winCount;
			long evenCount;
			long negaCount;
			// 未开赛情况.
			if(probMap.get("winCount") == null){
				winCount = 0;
				evenCount = 0;
				negaCount=0;
			}else{
				winCount = probMap.get("winCount");
				evenCount = probMap.get("evenCount");
				negaCount = probMap.get("negaCount");
			}
			
			Map<String, List<Integer>> seqMap = matchCorpSeq.get(key);
			String allSeq = OkParseUtils.transListToStr(seqMap.get("allSeq"), "|");
			String winSeq = OkParseUtils.transListToStr(seqMap.get("winSeq"), "|");
			String evenSeq = OkParseUtils.transListToStr(seqMap.get("evenSeq"), "|");
			String negaSeq = OkParseUtils.transListToStr(seqMap.get("negaSeq"), "|");
			Double winProb = Double.valueOf(winCount)/Double.valueOf(count);
			Double evenProb = Double.valueOf(evenCount)/Double.valueOf(count);
			Double negaProb = Double.valueOf(negaCount)/Double.valueOf(count);
			Timestamp timestamp = new Timestamp(Calendar.getInstance()
					.getTimeInMillis());
			
			KellyCorpResult kellyCorpResult = new KellyCorpResult();
			kellyCorpResult.setOkUrlDate(okUrlDate);
			kellyCorpResult.setMatchName(matchName);
			kellyCorpResult.setOddsCorpName(oddsCorpName);
			kellyCorpResult.setCount(count);
			kellyCorpResult.setWinCount(winCount);
			kellyCorpResult.setEvenCount(evenCount);
			kellyCorpResult.setNegaCount(negaCount);
			kellyCorpResult.setAllSeq(allSeq);
			kellyCorpResult.setWinSeq(winSeq);
			kellyCorpResult.setEvenSeq(evenSeq);
			kellyCorpResult.setNegaSeq(negaSeq);
			kellyCorpResult.setWinProb(winProb);
			kellyCorpResult.setEvenProb(evenProb);
			kellyCorpResult.setNegaProb(negaProb);
			kellyCorpResult.setTimestamp(timestamp);
			kellyCorpResult.setRuleType(ruleType);
			kellyCorpResultList.add(kellyCorpResult);
		}
		kellyCorpResultService.insertList(kellyCorpResultList);
	}
	
	/**
	 * 规则A: 特定公司、特定联赛、主胜赔率<2.0, 最近一次降赔.
	 */
	private Double latestEuroOddsAnalyse(Match match, List<EuropeOddsChange> europeOddsChangeList){
		if(europeOddsChangeList == null || europeOddsChangeList.size() < 3){
			return null;
		}
		String matchName = match.getMatchName();
		String corpName = europeOddsChangeList.get(0).getOddsCorpName();
		
		float hostOdds = europeOddsChangeList.get(0).getHostOdds();
		if(hostOdds > 2.0){
			return null;
		}
		
		EuropeOddsChange europeOddsChange2 = europeOddsChangeList.get(1);
		if(europeOddsChange2 == null){
			return null;
		}
		float hostOdds2 = europeOddsChange2.getHostOdds();
		
		EuropeOddsChange europeOddsChange3 = europeOddsChangeList.get(2);
		if(europeOddsChange3 == null){
			return null;
		}
		float hostOdds3 = europeOddsChange3.getHostOdds();
		
		if(hostOdds2 >= hostOdds3){
			return null;
		}
		
		String key = corpName + "_" + matchName;
		Double score = ((Double)weightMapA.get(key + "_" + WIN_PROB)) == null ? 0 : ((Double)weightMapA.get(key + "_" + WIN_PROB)) * 100;
//		LOGGER.info("key: " + key + "; score: " + score);
		return score;
	}
	
	/**
	 * 规则B: LOT_BF_TURNOVER_DETAIL 中交易量的比例.
	 * @return
	 */
	private Double bfTurnoverDetailMultiple(Match match, List<ExchangeBfTurnoverDetail> turnoverDetailList){
		if(turnoverDetailList == null || turnoverDetailList.isEmpty()){
			return null;
		}
		ExchangeBfTurnoverDetail turnoverDetail = turnoverDetailList.get(0);
		Integer hostTotal = turnoverDetail.getHostTotal();
		Integer evenTotal = turnoverDetail.getEvenTotal();
		Integer visitingTotal = turnoverDetail.getVisitingTotal();
		hostTotal = (hostTotal == null) ? 0 : hostTotal;
		evenTotal = (evenTotal == null) ? 0 : evenTotal;
		visitingTotal = (visitingTotal == null) ? 0 : visitingTotal;
		if(hostTotal < 2 * evenTotal){
			return null;
		}
		
		String matchName = match.getMatchName();
		String multiple = "";
		String key = "";
		Double realMultiple = (double)hostTotal /(double)visitingTotal;
		if(realMultiple >= 1 && realMultiple < 1.5){
			multiple = "1.0";
		}else if(realMultiple >= 1.5 && realMultiple < 2){
			multiple = "1.5";
		}else if(realMultiple >= 2 && realMultiple < 2.5){
			multiple = "2.0";
		}else if(realMultiple >= 2.5 && realMultiple < 3){
			multiple = "2.5";
		}else if(realMultiple >= 3 && realMultiple < 3.5){
			multiple = "3.0";
		}else if(realMultiple >= 3.5 && realMultiple < 4){
			multiple = "3.5";
		}else if(realMultiple >= 4 && realMultiple < 4.5){
			multiple = "4.0";
		}else if(realMultiple >= 4.5 && realMultiple < 5){
			multiple = "4.5";
		}else if(realMultiple >= 5){
			multiple = "5.0";
		}
		key = matchName + "_" + multiple;
		Double score = ((Double)weightMapB.get(key + "_" + WIN_PROB)) == null ? 0 : ((Double)weightMapB.get(key + "_" + WIN_PROB)) * 100;
//		LOGGER.info("realMultiple: " + realMultiple + "; key: " + key + "; score: " + score);
		return score;
	}
	
	/**
	 * 规则C
	 * @param match
	 * @return
	 */
	private Map<String, Float> compLossIndex(List<EuropeOdds> europeOddsList, List<EuropeOddsChange> europeOddsChangeList, ExchangeTransactionProp transactionProp ){
		Map<String, Float> result = new HashMap<String, Float>();
		if(transactionProp== null || ((europeOddsList == null || europeOddsList.isEmpty()) 
				&& (europeOddsChangeList == null || europeOddsChangeList.isEmpty()))){
			return result;
		}
		
		// 查找竞彩官方.
		EuropeOdds europeOdds = null;
		EuropeOddsChange euroOddsChange = null;
		// 两种获取 europeOdds 方式: europeOddsList 和 europeOddsChangeList.
		if(europeOddsList != null && !europeOddsList.isEmpty()){
			for(EuropeOdds odd : europeOddsList){
				if("竞彩官方".equals(odd.getOddsCorpName())){
					europeOdds = odd;
					break;
				}
			}
			if(europeOdds != null){
				result = compLossIndex(europeOdds, euroOddsChange, transactionProp);
			}
		}else if(europeOddsChangeList != null && !europeOddsChangeList.isEmpty()){
			euroOddsChange = europeOddsChangeList.get(0);
			if(euroOddsChange != null){
				result = compLossIndex(europeOdds, euroOddsChange, transactionProp);
			}
		}
		
		return result;
	}
	
	public Map<String, Float> compLossIndex(EuropeOdds europeOdds, EuropeOddsChange europeOddsChange, ExchangeTransactionProp transactionProp ){
		Map<String, Float> result = new HashMap<String, Float>();
		if(transactionProp == null || (europeOdds == null && europeOddsChange == null)){
			return result;
		}
		
		Float lossRatio = null;
		Float hostOdds = null;
		Float evenOdds = null;
		Float visitingOdds = null;
		if(europeOdds != null){
			lossRatio = europeOdds.getLossRatio();
			hostOdds = europeOdds.getHostOdds();
			evenOdds = europeOdds.getEvenOdds();
			visitingOdds = europeOdds.getVisitingOdds();
		}else if(europeOddsChange != null){
			lossRatio = europeOddsChange.getLossRatio();
			hostOdds = europeOddsChange.getHostOdds();
			evenOdds = europeOddsChange.getEvenOdds();
			visitingOdds = europeOddsChange.getVisitingOdds();
		}
		
		Float hostProp = null;
		Float evenProp = null;
		Float visitingProp = null;
		
		// 必发
		hostProp = transactionProp.getHostBf() == null ? 0 : transactionProp.getHostBf()/100;
		evenProp = transactionProp.getEvenBf() == null ? 0 : transactionProp.getEvenBf()/100;
		visitingProp = transactionProp.getVisitingBf() == null ? 0 : transactionProp.getVisitingBf()/100;
		
		// 竞彩
//		hostProp = transactionProp.getHostComp() == null ? 0 : transactionProp.getHostComp()/100;
//		evenProp = transactionProp.getEvenComp() == null ? 0 : transactionProp.getEvenComp()/100;
//		visitingProp = transactionProp.getVisitingComp() == null ? 0 : transactionProp.getVisitingComp()/100;
		
		
		if(hostProp == null || evenProp == null || visitingProp == null){
			return result;
		}
//		Float hostIndex = lossRatio * (evenProp + visitingProp) - hostProp * (hostOdds -1);
//		Float evenIndex = lossRatio * (hostProp + visitingProp) - evenProp * (evenOdds -1);
//		Float visitingIndex = lossRatio * (hostProp + evenProp) - visitingProp * (visitingOdds -1);
		
		// 另一种
		Float hostIndex = lossRatio * (hostProp + evenProp + visitingProp) - hostProp * hostOdds;
		Float evenIndex = lossRatio * (hostProp + evenProp + visitingProp) - evenProp * evenOdds;
		Float visitingIndex = lossRatio * (hostProp + evenProp + visitingProp) - visitingProp * visitingOdds;
		
		result.put("host", hostIndex);
		result.put("even", evenIndex);
		result.put("visiting", visitingIndex);
		
		return result;
	}
	
	private MatchScore analyseEuroOddsKelly(List<EuropeOddsChange> europeOddsChangeList, Match match, List<KellyRule> kellyRules){
		if(europeOddsChangeList == null || europeOddsChangeList.isEmpty()){
			return null;
		}
		
		for(EuropeOddsChange oddsChange : europeOddsChangeList){
			if(oddsChange.getOddsSeq() == 1){
				// 添加赔付率的条件.
				if(oddsChange.getHostKelly() <= oddsChange.getLossRatio()){
					for(KellyRule kellyRule : kellyRules){
						if(kellyRule.getOddsCorpName().equals(oddsChange.getOddsCorpName())){
							MatchScore matchScore = new MatchScore();
							matchScore.setMatchName(match.getMatchName());
							matchScore.setMatchSeq(match.getMatchSeq());
							matchScore.setOkMatchId(match.getOkMatchId());
							matchScore.setOkUrlDate(match.getOkUrlDate());
							matchScore.setHostOdds(oddsChange.getHostOdds());
							matchScore.setRuleType("K1");
							matchScore.setHostGoals(match.getHostGoals());
							matchScore.setVisitingGoals(match.getVisitingGoals());
							String matchResult = "未开赛";
							Integer hostGoals = match.getHostGoals();
							Integer visitingGoals = match.getVisitingGoals();
							if(hostGoals != null && visitingGoals != null){
								if (hostGoals > visitingGoals) {
									matchResult = "胜";
								} else if (match.getHostGoals() == match.getVisitingGoals()) {
									matchResult = "平";
								} else {
									matchResult = "负";
								}
							}
							matchScore.setMatchResult(matchResult);
							matchScore.setKellyRule(kellyRule);
							return matchScore;
						}
					}
				}
			}
		}
		return null;
	}

	private Map<String, Object> initialWeightAMap(){
		List<WeightRule> allRules = weightRuleService.queryWeightRulesByType("A");
		Map<String, Object> result = new HashMap<String, Object>();
		
		String oddsCorpName = "";
		String matchName = "";
		String key = "";
		Long count = 0L;

		for(WeightRule rule:allRules){
			oddsCorpName = rule.getOddsCorpName();
			matchName = rule.getMatchName();
			key = oddsCorpName + "_" + matchName;
			count = rule.getCount();
			// 只有总数大于 50 时才使用.
			if(count >= 50){
				result.put(key + "_" + COUNT, count);
				result.put(key + "_" + WIN_COUNT, rule.getWinCount());
				result.put(key + "_" + EVEN_COUNT, rule.getEvenCount());
				result.put(key + "_" + NEGA_COUNT, rule.getNegaCount());
				result.put(key + "_" + WIN_PROB, rule.getWinProb());
				result.put(key + "_" + EVEN_PROB, rule.getEvenProb());
				result.put(key + "_" + NEGA_PROB, rule.getNegaProb());
				result.put(key + "_" + RULE_TYPE, rule.getRuleType());
			}
		}
		return result;
	}
	
	private Map<String, Object> initialWeightBMap(){
		List<WeightRule> allRules = weightRuleService.queryWeightRulesByType("B");
		Map<String, Object> result = new HashMap<String, Object>();
		
		String matchName = "";
		String multiple = "";
		String key = "";
		Long count = 0L;

		for(WeightRule rule:allRules){
			matchName = rule.getMatchName();
			multiple = String.valueOf(rule.getMultiple());
			key = matchName + "_" + multiple;
			count = rule.getCount();
			// 只有总数大于 10 时才使用.
			if(count >= 10){
				result.put(key + "_" + COUNT, count);
				result.put(key + "_" + WIN_COUNT, rule.getWinCount());
				result.put(key + "_" + EVEN_COUNT, rule.getEvenCount());
				result.put(key + "_" + NEGA_COUNT, rule.getNegaCount());
				result.put(key + "_" + WIN_PROB, rule.getWinProb());
				result.put(key + "_" + EVEN_PROB, rule.getEvenProb());
				result.put(key + "_" + NEGA_PROB, rule.getNegaProb());
				result.put(key + "_" + RULE_TYPE, rule.getRuleType());
			}
		}
		return result;
	}
	
	/**
	 * 获取某天的match.html 和 euroOddsChange, 用于 kelly rule.
	 * @param limitedMatchSeqs 只处理该list中的matchSeq, 优先级最高.
	 * @param beginMatchSeq, endMatchSeq  只处理该范围内的matchSeq, 优先级低于limitedMatchSeqs.
	 */
	public void persistCorpEuroOddsChangeKelly(String baseDir, Calendar cal, int beginMatchSeq, int endMatchSeq,
			List<Match> matches, Set<Integer> limitedMatchSeqs, boolean replace, boolean reGetMatchHtml){
		long begin = System.currentTimeMillis();
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
		
		/*
		 * 避免重复解析matches, 只有当参数中matches为null, 才获取Match对象;
		 */
		if(matches == null){
			matches = new ArrayList<Match>(2^10);
			List<Match> oneMatchHtml = new ArrayList<Match>();
			for (File matchHtmlFile : matchHtmlFiles) {
				oneMatchHtml = singleMatchService.getAllMatchFromFile(matchHtmlFile, 0, 0);
				matches.addAll(oneMatchHtml);
				LOGGER.info("matchHtmlFile: " + matchHtmlFile.getAbsolutePath() + "; num of matches: " + oneMatchHtml.size());
			}
		}

		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return now...");
			return;
		}
		
		File matchHtml = matchHtmlFiles.get(0);
		String matchHtmlPath = matchHtml.getAbsolutePath();
		Map<String, Set<String>> corpNameMap = new HashMap<String, Set<String>>();
		List<String> corpNoAdded = new ArrayList<String>();
		for(Integer corpNo : OkConstant.ODDS_CORP_EURO_TRANS_ASIA){
			corpNoAdded.add(String.valueOf(corpNo));
		}
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
			
			String matchName = match.getMatchName();
			// 如果之前没有出现过该联赛，则去查询数据库.  因为有些corp这种方式查不到，手动添加.
			Set<String> corpNoSet = new HashSet<String>();
			corpNoSet.addAll(corpNoAdded);
			if(corpNameMap.containsKey(matchName)){
				corpNoSet = corpNameMap.get(matchName);
			}else{
				List<KellyRule> kellyRules = kellyRuleService.queryKellyRulesByMatchName(matchName);
				for(KellyRule kellyRule : kellyRules){
					corpNoSet.add(kellyRule.getCorpNo());
				}
				corpNameMap.put(matchName, corpNoSet);
			}
			
			for(String corpNo : corpNoSet){
				String euroOddsChangePath = matchHtmlPath.replaceFirst(
						OkConstant.MATCH_FILE_NAME, OkConstant.EURO_ODDS_CHANGE_FILE_NAME_BASE + "_"
								+ corpNo + "_" + matchSeq + ".html");
				File euroOddsChangeFile = new File(euroOddsChangePath);
				
				if(replace){
					euroOddsChangeFile.delete();
				}
				// 文件存在且非空时不做处理.
				if (!replace && OkParseUtils.checkFileExists(euroOddsChangeFile) && OkParseUtils.checkFileSize(euroOddsChangeFile, 10)) {
					continue;
				}

				// 为了加快速度，直接构造.  http://www.okooo.com/soccer/match/720283/odds/change/355/ 
				String matchUrl = "http://www.okooo.com/soccer/match/" + match.getOkMatchId() + "/odds/change/" + corpNo + "/";
				// 获取 euroOddsChange 的页面信息.
				OkParseUtils.persistByUrl(euroOddsChangeFile, matchUrl, "gb2312", 1000);
//				LOGGER.info("matchHtmlPath: " + matchHtmlPath + "; matchUrl: " + matchUrl + "; matchSeq: " + matchSeq);
			}
		}
		
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/(1000*60) + " min.");
	}
	
	/**
	 * 线程方式.
	 */
	public void persistCorpEuroOddsChangeKellyThread(String baseDir, Calendar cal, int numOfThread, int beginMatchSeq, int endMatchSeq,
			List<Match> matches, Set<Integer> limitedMatchSeqs, boolean replace, boolean reGetMatchHtml){
		long beginTime = System.currentTimeMillis();
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
		
		/*
		 * 避免重复解析matches, 只有当参数中matches为null, 才获取Match对象;
		 */
		if(matches == null){
			matches = new ArrayList<Match>(2^10);
			List<Match> oneMatchHtml = new ArrayList<Match>();
			for (File matchHtmlFile : matchHtmlFiles) {
				oneMatchHtml = singleMatchService.getAllMatchFromFile(matchHtmlFile, 0, 0);
				matches.addAll(oneMatchHtml);
				LOGGER.info("matchHtmlFile: " + matchHtmlFile.getAbsolutePath() + "; num of matches: " + oneMatchHtml.size());
			}
		}

		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return now...");
			return;
		}
		int numOfMatch;
		if(limitedMatchSeqs != null){
			numOfMatch = limitedMatchSeqs.size();
		}else{
			numOfMatch = Math.min(endMatchSeq - beginMatchSeq + 1, matches.size());
		}
		double exact = Double.valueOf(numOfMatch) / Double.valueOf(numOfThread);
		int numOfPerThread = (int) Math.ceil(exact);
		int maxSeqInMatches = 0;
		for(Match match : matches){
			maxSeqInMatches = Math.max(maxSeqInMatches, match.getMatchSeq());
		}
		int maxSeq = Math.min(endMatchSeq, maxSeqInMatches);
//		ExecutorService service = Executors.newCachedThreadPool();
		
		// 最大10个Thread, 多余的放入queue, 直至queue满才会new Thread.
		ThreadPoolExecutor pool = new ThreadPoolExecutor(12, 50, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		
		int begin;
		int end;
		Integer[] limitedMatchSeqsArr = new Integer[numOfMatch];
		if(limitedMatchSeqs != null){
			limitedMatchSeqs.toArray(limitedMatchSeqsArr);
		}
		
		for(int i = 0; i < numOfThread + 3; i++ ){
			Set<Integer> limitedMatchSeqsThread = null;
			if(limitedMatchSeqs != null){
				begin = i * numOfPerThread;
				if(begin >= limitedMatchSeqsArr.length){
					break;
				}
				end = (i+1) * numOfPerThread - 1;
				limitedMatchSeqsThread = new HashSet<Integer>();
				for(int j = begin; j <= end; j++){
					if(j >= limitedMatchSeqsArr.length){
						break;
					}
					limitedMatchSeqsThread.add(limitedMatchSeqsArr[j]);
				}
			}else{
				begin = i * numOfPerThread + beginMatchSeq;
				if(begin > maxSeq){
					break;
				}
				end = (i+1) * numOfPerThread + beginMatchSeq - 1;
				end = Math.min(end, maxSeq);
			}

			PersistCorpEuroOddsChangeKellyThread kellyThread = new PersistCorpEuroOddsChangeKellyThread();
			kellyThread.setAnalyseService(this);
			kellyThread.setCal(cal);
			kellyThread.setBeginMatchSeq(begin);
			kellyThread.setEndMatchSeq(end);
			kellyThread.setBaseDir(baseDir);
			kellyThread.setMatches(matches);
			kellyThread.setLimitedMatchSeqs(limitedMatchSeqsThread);
			kellyThread.setReplace(replace);
			kellyThread.setReGetMatchHtml(reGetMatchHtml);
			pool.execute(kellyThread);
		}
		
		for(;;){
			if(pool.getActiveCount() > 0){
				LOGGER.info("getActiveCount: " + pool.getActiveCount() + "; getCompletedTaskCount: " + pool.getCompletedTaskCount() + 
						"; getLargestPoolSize: " + pool.getLargestPoolSize() + "; getMaximumPoolSize: " + pool.getMaximumPoolSize() +
						"; getTaskCount: " + pool.getTaskCount());
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					LOGGER.error(e);
				}
			}else {
				break;
			}
		}
		
//		try {
//			service.awaitTermination(2, TimeUnit.DAYS);
//			
//		} catch (InterruptedException e1) {
//			LOGGER.error(e1);
//		}
		
//		try {
//			Thread.currentThread().join();
//		} catch (InterruptedException e) {
//			LOGGER.error(e);
//		}
		
		LOGGER.info("process all success, eclipsed "
		+ (System.currentTimeMillis() - beginTime)/(1000 * 60) + " min.");
	}
	
	public StringBuilder highKellyPredict(String okUrlDate, String ruleType){
		StringBuilder sb = new StringBuilder("");
		if(StringUtils.isBlank(okUrlDate) || StringUtils.isBlank(ruleType)){
			return sb;
		}
		Formatter formatter = new Formatter();
//		sb.append("MATCH_NAME ").append("CORP ").append("ALL_SEQ ")
//		  .append("ALL_WIN_COUNT ").append("ALL_COUNT ").append("TIME_BEFORE")
//		  .append("\n");
		if("K3".equals(ruleType)){
			sb.append("\nK3 High Correct Rate: \n");
			List<Map<String, String>> k3List = kellyCorpResultService.highK3Predict(okUrlDate);
			for(Map<String, String> map : k3List){
				formatter.format("%6s %20s %20s %4s %4s %8s\n", map.get("matchName"), map.get("oddsCorpName"), 
						map.get("allSeq"),map.get("allWinCount"), map.get("allCount"), map.get("euroTimeBeforeMatch"));
			}
			sb.append(formatter.toString());
		}else if("K4".equals(ruleType)){
			sb.append("\nK4 High Correct Rate: \n");
			List<Map<String, String>> k4List = kellyCorpResultService.highK4Predict(okUrlDate);
			for(Map<String, String> map : k4List){
				formatter.format("%6s %20s %20s %4s %4s %8s\n", map.get("matchName"), map.get("oddsCorpName"), 
						map.get("allSeq"),map.get("allWinCount"), map.get("allCount"), map.get("euroTimeBeforeMatch"));
			}
			sb.append(formatter.toString());
		}else if("K32".equals(ruleType)){
			sb.append("\nK3 WIN_EVEN High Correct Rate: \n");
			List<Map<String, String>> k32List = kellyCorpResultService.highK3WinEvenPredict(okUrlDate);
			for(Map<String, String> map : k32List){
				formatter.format("%6s %20s %20s %4s %4s\n", map.get("matchName"), map.get("oddsCorpName"), 
						map.get("allSeq"),map.get("processedCount"), map.get("allCount"));
			}
			sb.append(formatter.toString());
		}else if("K42".equals(ruleType)){
			sb.append("\nK4 EVEN_NEGA High Correct Rate: \n");
			List<Map<String, String>> k42List = kellyCorpResultService.highK4EvenNegaPredict(okUrlDate);
			for(Map<String, String> map : k42List){
				formatter.format("%6s %20s %20s %4s %4s\n", map.get("matchName"), map.get("oddsCorpName"), 
						map.get("allSeq"),map.get("processedCount"), map.get("allCount"));
			}
			sb.append(formatter.toString());
		}
		formatter.close();
		LOGGER.info(sb.toString());
		
		return sb;
	}
	
	/**
	 * RULE: KK 
	 * @param matchDir
	 * @param beginMatchSeq
	 * @param endMatchSeq
	 * @param limitedMatchSeqs
	 */
	public void analyseOddsChangeFreq(String matchDir, int beginMatchSeq, int endMatchSeq, Set<Integer> limitedMatchSeqs){
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(
				matchDir, OkConstant.MATCH_FILE_NAME);
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
		
		File matchHtmlFile = matchHtmlFiles.get(0);
		// 用于存放RULE:KL 中赔率变化值.  key: {matchSeq}_{H}  value: 变化值.
		Map<String, List<Float>> changeMap = new HashMap<String, List<Float>>();
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

			LOGGER.info("process match: " + match.getMatchSeq() + "; okUrlDate: " + match.getOkUrlDate());
			
			// 初始化changeMap
			String hostKey = matchSeq + "_" + "H";
			String evenKey = matchSeq + "_" + "E";
			String visitingKey = matchSeq + "_" + "V";
			if(changeMap.get(hostKey) == null){
				changeMap.put(hostKey, new ArrayList<Float>());
			}
			if(changeMap.get(evenKey) == null){
				changeMap.put(evenKey, new ArrayList<Float>());
			}
			if(changeMap.get(visitingKey) == null){
				changeMap.put(visitingKey, new ArrayList<Float>());
			}
			
			List<File> euroOddsChangeHtmls = null;
			euroOddsChangeHtmls = OkParseUtils.getSameMatchFilesFromMatch(
					matchHtmlFile, match,
					OkConstant.EURO_ODDS_CHANGE_FILE_NAME_BASE);
			for (File euroOddsChangeHtml : euroOddsChangeHtmls) {
				// 对于过大的文件直接跳过.
				if(euroOddsChangeHtml.length() > maxFileLength){
					LOGGER.info("file too large, skip now: " + euroOddsChangeHtml.getAbsolutePath());
					continue;
				}
				
				List<EuropeOddsChange> europeOddsChangeList = euroOddsChangeService
						.getEuroOddsChangeFromFile(euroOddsChangeHtml, 0, false);
				if (europeOddsChangeList == null
						|| europeOddsChangeList.size() <= 2) {
					continue;
				}

				// 获取第二条记录.
				EuropeOddsChange oddsChange2 = null;
				EuropeOddsChange oddsChangeMax = null;
				for (EuropeOddsChange change : europeOddsChangeList) {
					if (change.getOddsSeq() == 2) {
						oddsChange2 = change;
						break;
					}
				}
				oddsChangeMax = europeOddsChangeList.get(europeOddsChangeList
						.size() - 1);
				
				// RULE:KL 当前的赔率和最初的赔率变化值的频数.
				Float hostOdds2 = oddsChange2.getHostOdds();
				Float evenOdds2 = oddsChange2.getEvenOdds();
				Float visitingOdds2 = oddsChange2.getVisitingOdds();
				Float hostOddsMax = oddsChangeMax.getHostOdds();
				Float evenOddsMax = oddsChangeMax.getEvenOdds();
				Float visitingOddsMax = oddsChangeMax.getVisitingOdds();
				changeMap.get(hostKey).add(hostOdds2 - hostOddsMax);
				changeMap.get(evenKey).add(evenOdds2 - evenOddsMax);
				changeMap.get(visitingKey).add(visitingOdds2 - visitingOddsMax);
			}
		}
		
		// 登记到数据库.
		
		
//		showResultK3(k3Scores);
//		LOGGER.info("process K3: analyse success, eclipsed "
//				+ (System.currentTimeMillis() - begin) + " ms...");
//		showResultK3(k4Scores);
//		LOGGER.info("process K4: analyse success, eclipsed "
//				+ (System.currentTimeMillis() - begin) + " ms...");
//		
//		showResultK3(k5Scores);
//		LOGGER.info("process K5: analyse success, eclipsed "
//				+ (System.currentTimeMillis() - begin) + " ms...");
//		
//		showResultK3(k6Scores);
//		LOGGER.info("process K6: analyse success, eclipsed "
//				+ (System.currentTimeMillis() - begin) + " ms...");
	
	}
	
	/**
	 * 分区间计算euroOdds
	 * RULE: KJ 对欧赔按照lossRatio分段进行kelly计算.   第一段: [0.90, 0.94]  第二段: [0.95, 0.99]
	 * @param matchDir
	 * @param beginMatchSeq
	 * @param endMatchSeq
	 * @param limitedMatchSeqs
	 */
	public void analyseOddsSection(List<Match> matches, String matchDir, int beginMatchSeq, int endMatchSeq, Set<Integer> limitedMatchSeqs, 
			Map<Integer, String> jobTypes, String okUrlDate){
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(
				matchDir, OkConstant.MATCH_FILE_NAME);
		LOGGER.info("matchHtmlFiles size: " + matchHtmlFiles.size());

		// 区间
		Float firstSecLow = 0.90f;
		Float firstSecHigh = 0.94f;
		Float secondSecLow = 0.95f;
		Float secondSecHigh = 0.99f;
		String firstSecStr = "[" + firstSecLow + "," + firstSecHigh + "]";
		String secondSecStr = "[" + secondSecLow + "," + secondSecHigh + "]";
		
		/*
		 * 获取Match对象;
		 */
		if(matches == null || matches.isEmpty()){
			matches = new ArrayList<Match>(2^10);
			List<Match> oneMatchHtml = new ArrayList<Match>();
			for (File matchHtmlFile : matchHtmlFiles) {
				oneMatchHtml = singleMatchService.getAllMatchFromFile(matchHtmlFile, 0, 0);
				matches.addAll(oneMatchHtml);
				LOGGER.info("matchHtmlFile: " + matchHtmlFile.getAbsolutePath() + "; num of matches: " + oneMatchHtml.size());
			}
		}
		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return now...");
			return;
		}
		
		File matchHtmlFile = matchHtmlFiles.get(0);
		
		List<Float> hostSection1 = new ArrayList<Float>();
		List<Float> evenSection1 = new ArrayList<Float>();
		List<Float> visitingSection1 = new ArrayList<Float>();
		List<Float> hostSection2 = new ArrayList<Float>();
		List<Float> evenSection2 = new ArrayList<Float>();
		List<Float> visitingSection2 = new ArrayList<Float>();
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

			LOGGER.info("process match: " + match.getMatchSeq());
			hostSection1.clear();
			evenSection1.clear();
			visitingSection1.clear();
			hostSection2.clear();
			evenSection2.clear();
			visitingSection2.clear();
			
			List<File> euroOddsChangeHtmls = null;
			euroOddsChangeHtmls = OkParseUtils.getSameMatchFilesFromMatch(
					matchHtmlFile, match,
					OkConstant.EURO_ODDS_CHANGE_FILE_NAME_BASE);
			for (File euroOddsChangeHtml : euroOddsChangeHtmls) {
				// 对于过大的文件直接跳过.
				if(euroOddsChangeHtml.length() > maxFileLength){
					LOGGER.info("file too large, skip now: " + euroOddsChangeHtml.getAbsolutePath());
					continue;
				}
				
				List<EuropeOddsChange> europeOddsChangeList = euroOddsChangeService
						.getEuroOddsChangeFromFile(euroOddsChangeHtml, 2, false);
				if (europeOddsChangeList == null) {
					continue;
				}

				// 获取第二条记录.
				EuropeOddsChange oddsChange2 = null;
				for (EuropeOddsChange change : europeOddsChangeList) {
					if (change.getOddsSeq() == 2) {
						oddsChange2 = change;
						break;
					}
				}
				if(oddsChange2 == null){
					continue;
				}
				
				// RULE:KL 当前的赔率和最初的赔率变化值的频数.
				Float hostOdds2 = oddsChange2.getHostOdds();
				Float evenOdds2 = oddsChange2.getEvenOdds();
				Float visitingOdds2 = oddsChange2.getVisitingOdds();
				Float lossRatio = oddsChange2.getLossRatio();
				if(lossRatio >= firstSecLow && lossRatio <= firstSecHigh){
					hostSection1.add(hostOdds2);
					evenSection1.add(evenOdds2);
					visitingSection1.add(visitingOdds2);
				}else if(lossRatio >= secondSecLow && lossRatio <= secondSecHigh){
					hostSection2.add(hostOdds2);
					evenSection2.add(evenOdds2);
					visitingSection2.add(visitingOdds2);
				}
			}
			
			if(hostSection1.isEmpty() || evenSection1.isEmpty() || visitingSection1.isEmpty()
					|| hostSection2.isEmpty() || evenSection2.isEmpty() || visitingSection2.isEmpty()){
				continue;
			}
			
			// 计算标准差.
			Float[] hostSection1Arr = new Float[hostSection1.size()];
			Float[] evenSection1Arr = new Float[evenSection1.size()];
			Float[] visitingSection1Arr = new Float[visitingSection1.size()];
			Float[] hostSection2Arr = new Float[hostSection2.size()];
			Float[] evenSection2Arr = new Float[evenSection2.size()];
			Float[] visitingSection2Arr = new Float[visitingSection2.size()];
			Float hostSection1StdVar = OkParseUtils.calStdVariance(hostSection1.toArray(hostSection1Arr));
			Float evenSection1StdVar = OkParseUtils.calStdVariance(evenSection1.toArray(evenSection1Arr));
			Float visitingSection1StdVar = OkParseUtils.calStdVariance(visitingSection1.toArray(visitingSection1Arr));
			Float hostSection2StdVar = OkParseUtils.calStdVariance(hostSection2.toArray(hostSection2Arr));
			Float evenSection2StdVar = OkParseUtils.calStdVariance(evenSection2.toArray(evenSection2Arr));
			Float visitingSection2StdVar = OkParseUtils.calStdVariance(visitingSection2.toArray(visitingSection2Arr));
			// 拼接字符串.
			String varStr1 = firstSecStr + ":" + hostSection1.size() + "," + Math.round(hostSection1StdVar * 10000)/100.0 + "|"
					+ evenSection1.size() + "," + Math.round(evenSection1StdVar * 10000)/100.0 + "|"
					+ visitingSection1.size() + "," + Math.round(visitingSection1StdVar * 10000)/100.0 + "|";
			String varStr2 = secondSecStr + ":" + hostSection2.size() + "," + Math.round(hostSection2StdVar * 10000)/100.0 + "|"
					+ evenSection2.size() + "," + Math.round(evenSection2StdVar * 10000)/100.0 + "|"
					+ visitingSection2.size() + "," + Math.round(visitingSection2StdVar * 10000)/100.0 + "|";
			// 登记到数据库 LOT_KELLY_MATCH_COUNT.
			KellyMatchCount kellyMatchCount = new KellyMatchCount();
			kellyMatchCount.setOkUrlDate(okUrlDate);
			kellyMatchCount.setMatchSeq(matchSeq);
			kellyMatchCount.setJobType(jobTypes.get(matchSeq));
			kellyMatchCount.setRuleType("KJ");
			kellyMatchCount.setCorpCount(0);
			kellyMatchCount.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			kellyMatchCount.setExtend1(varStr1);
			kellyMatchCount.setExtend2(varStr2);
			kellyMatchCountService.insertMatchCount(kellyMatchCount);
		}
	}
	
	/**
	 * 解析欧赔转换为亚盘页面, 并将结果存入数据库.
	 * @param matches
	 * @param matchDir
	 * @param beginMatchSeq
	 * @param endMatchSeq
	 * @param limitedMatchSeqs
	 * @param jobTypes
	 * @param okUrlDate
	 */
	public void analyseEuroTransAsia(List<Match> matches, String matchDir, int beginMatchSeq, int endMatchSeq, Set<Integer> limitedMatchSeqs, 
			Map<Integer, String> jobTypes, String okUrlDate){
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(
				matchDir, OkConstant.MATCH_FILE_NAME);
		LOGGER.info("matchHtmlFiles size: " + matchHtmlFiles.size());

		/*
		 * 获取Match对象;
		 */
		if(matches == null || matches.isEmpty()){
			matches = new ArrayList<Match>(2^10);
			List<Match> oneMatchHtml = new ArrayList<Match>();
			for (File matchHtmlFile : matchHtmlFiles) {
				oneMatchHtml = singleMatchService.getAllMatchFromFile(matchHtmlFile, 0, 0);
				matches.addAll(oneMatchHtml);
				LOGGER.info("matchHtmlFile: " + matchHtmlFile.getAbsolutePath() + "; num of matches: " + oneMatchHtml.size());
			}
		}
		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return now...");
			return;
		}
		
		// 查询已经记录的
		Set<String> seqJobTypeSet = euroTransAsiaService.queryEuroTransAsiaByOkUrlDateInSet(okUrlDate);
		
		File matchHtmlFile = matchHtmlFiles.get(0);
		String euroTransAsiaHtmlPath = "";
		File euroTransAsiaHtml = null;
		String asiaOddsHtmlPath = "";
		File asiaOddsHtml = null;
		String euroOddsChangeHtmlPath = "";
		File euroOddsChangeHtml = null;
		// 存放实际亚盘的lossRatio key: oddsCorpName  value: lossRatioAsia.
		Map<String, Float> lossRatioMap = new HashMap<String, Float>();
		for(Match match : matches){
			// 从指定matchSeq开始.
			int matchSeq = match.getMatchSeq();
			String jobType = jobTypes.get(matchSeq);
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
			
			if(seqJobTypeSet.contains(matchSeq + "_" + jobType)){
				continue;
            }

			LOGGER.info("process match: " + match.getMatchSeq());
			
			EuroTransAsia euroTransAsiaInit = new EuroTransAsia();
			// 获取lossRatioAsia
			asiaOddsHtmlPath = matchDir + "asiaOdds" + "_" + matchSeq + ".html";
			asiaOddsHtml = new File(asiaOddsHtmlPath);
			if(asiaOddsHtml.exists()){
				List<AsiaOdds> asiaOddsList = asiaOddsService.getAsiaOddsFromFile(asiaOddsHtml, null);
				if(asiaOddsList != null && !asiaOddsList.isEmpty()){
					for(AsiaOdds asiaOdds : asiaOddsList){
						lossRatioMap.put(asiaOdds.getOddsCorpName() + "_" + "A" + "_" + "L", asiaOdds.getLossRatio());
						lossRatioMap.put(asiaOdds.getOddsCorpName() + "_" + "A" + "_" + "HK", asiaOdds.getHostKelly());
						lossRatioMap.put(asiaOdds.getOddsCorpName() + "_" + "A" + "_" + "VK", asiaOdds.getVisitingKelly());
					}
				}
			}
			
			/*
			 * 获取lossRatioEuro
			 * 计算各个公司的HK，EK，VK，不再使用数据中的值.  
			 * 计算方法: 先找出最近改变赔率的公司，使用公式: HK/H, EK/E, VK/V 分别计算出概率，当作最新的市场平均概率。 然后乘以各个博彩公司的胜平负赔率，
			 * 得到各个公司的HK, EK, VK.
			 * 
			 * 没必要计算, 取 euroOddsChange 页面的第一条记录, 实时的.
			 */
			for(int corpNo : OkConstant.ODDS_CORP_EURO_TRANS_ASIA){
				euroOddsChangeHtmlPath = matchDir + OkConstant.EURO_ODDS_CHANGE_FILE_NAME_BASE + "_" + corpNo + "_" + matchSeq + ".html";
				euroOddsChangeHtml = new File(euroOddsChangeHtmlPath);
				if(!euroOddsChangeHtml.exists()){
					continue;
				}
				List<EuropeOddsChange> europeOddsChangeList = euroOddsChangeService
						.getEuroOddsChangeFromFile(euroOddsChangeHtml, 2, false);
				if (europeOddsChangeList == null
						|| europeOddsChangeList.size() < 2) {
					continue;
				}
				EuropeOddsChange oddsChange1 = null;
				for (EuropeOddsChange change : europeOddsChangeList) {
					if (change.getOddsSeq() == 1) {
						oddsChange1 = change;
						break;
					}
				}
				if(oddsChange1 == null){
					continue;
				}
				String oddsCorpName = oddsChange1.getOddsCorpName();
				if(StringUtils.isBlank(oddsCorpName)){
					continue;
				}
				lossRatioMap.put(oddsCorpName + "_" + "E" + "_" + "HK", oddsChange1.getHostKelly());
				lossRatioMap.put(oddsCorpName + "_" + "E" + "_" + "EK", oddsChange1.getEvenKelly());
				lossRatioMap.put(oddsCorpName + "_" + "E" + "_" + "VK", oddsChange1.getVisitingKelly());
			}
			
			euroTransAsiaInit.setOkUrlDate(okUrlDate);
			euroTransAsiaInit.setMatchSeq(matchSeq);
			euroTransAsiaInit.setJobType(jobType);
			euroTransAsiaHtmlPath = matchHtmlFile.getAbsolutePath().replace(OkConstant.MATCH_FILE_NAME, 
					OkConstant.EURO_TRANS_ASIA_FILE_NAME_BASE + "_" + matchSeq + ".html");
			euroTransAsiaHtml = new File(euroTransAsiaHtmlPath);
			if(!euroTransAsiaHtml.exists()){
				continue;
			}
			euroTransAsiaService.parseEuroTransAsiaFromFile(euroTransAsiaHtml, euroTransAsiaInit, lossRatioMap);
		}
	}
	
	private EuroTransAsia getEuroTransAsiaFromEuroChange(String okUrlDate, Integer matchSeq, EuropeOddsChange europeOddsChange,
			Map<Float, EuroAsiaRefer> referMap, Map<String, AsiaOdds> asiaOddsMap){
		EuroTransAsia euroTransAsia = new EuroTransAsia();
		euroTransAsia.setOkUrlDate(okUrlDate);
		euroTransAsia.setMatchSeq(matchSeq);
		euroTransAsia.setJobType("I");
		String oddsCorpName = europeOddsChange.getOddsCorpName();
		euroTransAsia.setOddsCorpName(oddsCorpName);
		Float hostOddsEuro = europeOddsChange.getHostOdds();
		euroTransAsia.setHostOddsEuro(hostOddsEuro);
		euroTransAsia.setEvenOddsEuro(europeOddsChange.getEvenOdds());
		Float visitingOddsEuro = europeOddsChange.getVisitingOdds();
		euroTransAsia.setVisitingOddsEuro(visitingOddsEuro);
		euroTransAsia.setLossRatioEuro(europeOddsChange.getLossRatio());
		EuroAsiaRefer euroAsiaRefer = null;
		if(hostOddsEuro < visitingOddsEuro){
			euroAsiaRefer = referMap.get(hostOddsEuro);
			if(euroAsiaRefer == null){
				return null;
			}
			euroTransAsia.setHostOddsAsiaTrans(euroAsiaRefer.getOddsAsiaTop());
			euroTransAsia.setHandicapAsiaTrans(euroAsiaRefer.getHandicapAsia());
			euroTransAsia.setVisitingOddsAsiaTrans(euroAsiaRefer.getOddsAsiaUnder());
		}else{
			euroAsiaRefer = referMap.get(visitingOddsEuro);
			if(euroAsiaRefer == null){
				return null;
			}
			euroTransAsia.setHostOddsAsiaTrans(euroAsiaRefer.getOddsAsiaUnder());
			euroTransAsia.setHandicapAsiaTrans(0 - euroAsiaRefer.getHandicapAsia());
			euroTransAsia.setVisitingOddsAsiaTrans(euroAsiaRefer.getOddsAsiaTop());
		}
		euroTransAsia.setTotalDiscountTrans(euroAsiaRefer.getTotalDiscount());
		AsiaOdds asiaOdds = asiaOddsMap.get(oddsCorpName);
		if(asiaOdds != null){
			Float initHostOddsAsia = asiaOdds.getInitHostOdds();
			euroTransAsia.setHostOddsAsia(initHostOddsAsia);
			euroTransAsia.setHandicapAsia(asiaOdds.getInitHandicap());
			Float initVisitingOddsAsia = asiaOdds.getInitVisitingOdds();
			euroTransAsia.setVisitingOddsAsia(initVisitingOddsAsia);
			euroTransAsia.setTotalDiscount(initHostOddsAsia + initVisitingOddsAsia);
		}
		euroTransAsia.setTimestamp(new Timestamp(Calendar.getInstance()
				.getTimeInMillis()));
		euroTransAsia.setHostKellyEuro(europeOddsChange.getHostKelly());
		euroTransAsia.setEvenKellyEuro(europeOddsChange.getEvenKelly());
		euroTransAsia.setVisitingKellyEuro(europeOddsChange.getVisitingKelly());
		return euroTransAsia;
	}
	
	private Map<String, AsiaOdds> getAsiaOddsMap(List<AsiaOdds> asiaOddsList){
		Map<String, AsiaOdds> asiaOddsMap = new HashMap<String, AsiaOdds>();
		if(asiaOddsList == null || asiaOddsList.isEmpty()){
			return asiaOddsMap;
		}
		for(AsiaOdds asiaOdds : asiaOddsList){
			asiaOddsMap.put(asiaOdds.getOddsCorpName(), asiaOdds);
		}
		return asiaOddsMap;
	}
	
	public SingleMatchService getSingleMatchService() {
		return singleMatchService;
	}

	public void setSingleMatchService(SingleMatchService singleMatchService) {
		this.singleMatchService = singleMatchService;
	}

	public EuroOddsChangeService getEuroOddsChangeService() {
		return euroOddsChangeService;
	}

	public void setEuroOddsChangeService(EuroOddsChangeService euroOddsChangeService) {
		this.euroOddsChangeService = euroOddsChangeService;
	}

	public WeightRuleService getWeightRuleService() {
		return weightRuleService;
	}

	public void setWeightRuleService(WeightRuleService weightRuleService) {
		this.weightRuleService = weightRuleService;
	}

	public ExchangeService getExchangeService() {
		return exchangeService;
	}

	public void setExchangeService(ExchangeService exchangeService) {
		this.exchangeService = exchangeService;
	}

	public EuroOddsService getEuroOddsService() {
		return euroOddsService;
	}

	public void setEuroOddsService(EuroOddsService euroOddsService) {
		this.euroOddsService = euroOddsService;
	}

	public KellyRuleService getKellyRuleService() {
		return kellyRuleService;
	}

	public void setKellyRuleService(KellyRuleService kellyRuleService) {
		this.kellyRuleService = kellyRuleService;
	}

	public KellyCorpResultService getKellyCorpResultService() {
		return kellyCorpResultService;
	}

	public void setKellyCorpResultService(
			KellyCorpResultService kellyCorpResultService) {
		this.kellyCorpResultService = kellyCorpResultService;
	}

	public AsiaOddsTrendsService getAsiaOddsTrendsService() {
		return asiaOddsTrendsService;
	}

	public void setAsiaOddsTrendsService(AsiaOddsTrendsService asiaOddsTrendsService) {
		this.asiaOddsTrendsService = asiaOddsTrendsService;
	}

	public IndexStatsService getIndexStatsService() {
		return indexStatsService;
	}

	public void setIndexStatsService(IndexStatsService indexStatsService) {
		this.indexStatsService = indexStatsService;
	}

	public EuroOddsHandicapService getEuroOddsHandicapService() {
		return euroOddsHandicapService;
	}

	public void setEuroOddsHandicapService(
			EuroOddsHandicapService euroOddsHandicapService) {
		this.euroOddsHandicapService = euroOddsHandicapService;
	}

	public KellyMatchCountService getKellyMatchCountService() {
		return kellyMatchCountService;
	}

	public void setKellyMatchCountService(
			KellyMatchCountService kellyMatchCountService) {
		this.kellyMatchCountService = kellyMatchCountService;
	}

	public EuroTransAsiaService getEuroTransAsiaService() {
		return euroTransAsiaService;
	}

	public void setEuroTransAsiaService(EuroTransAsiaService euroTransAsiaService) {
		this.euroTransAsiaService = euroTransAsiaService;
	}

	public AsiaOddsService getAsiaOddsService() {
		return asiaOddsService;
	}

	public void setAsiaOddsService(AsiaOddsService asiaOddsService) {
		this.asiaOddsService = asiaOddsService;
	}

	public EuroAsiaReferService getEuroAsiaReferService() {
		return euroAsiaReferService;
	}

	public void setEuroAsiaReferService(EuroAsiaReferService euroAsiaReferService) {
		this.euroAsiaReferService = euroAsiaReferService;
	}

	public OkJobService getOkJobService() {
		return okJobService;
	}

	public void setOkJobService(OkJobService okJobService) {
		this.okJobService = okJobService;
	}

}
