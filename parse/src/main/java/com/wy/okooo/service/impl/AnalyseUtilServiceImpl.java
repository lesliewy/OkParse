/**
 * 
 */
package com.wy.okooo.service.impl;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.wy.okooo.domain.AsiaOddsChange;
import com.wy.okooo.domain.AsiaOddsTrends;
import com.wy.okooo.domain.Corp;
import com.wy.okooo.domain.EuroOddsHandicap;
import com.wy.okooo.domain.EuroTransAsia;
import com.wy.okooo.domain.EuropeChangeDailyStats;
import com.wy.okooo.domain.EuropeOdds;
import com.wy.okooo.domain.EuropeOddsChange;
import com.wy.okooo.domain.ExchangeTransactionProp;
import com.wy.okooo.domain.IndexStats;
import com.wy.okooo.domain.KellyCorpCount;
import com.wy.okooo.domain.KellyCorpResult;
import com.wy.okooo.domain.KellyMatchCount;
import com.wy.okooo.domain.Match;
import com.wy.okooo.domain.MatchScore;
import com.wy.okooo.domain.ProbAverage;
import com.wy.okooo.domain.ScoreOdds;
import com.wy.okooo.service.AnalyseUtilService;
import com.wy.okooo.service.AsiaOddsChangeService;
import com.wy.okooo.service.AsiaOddsTrendsService;
import com.wy.okooo.service.CorpService;
import com.wy.okooo.service.EuroOddsChangeService;
import com.wy.okooo.service.EuroOddsHandicapService;
import com.wy.okooo.service.EuroOddsService;
import com.wy.okooo.service.EuroTransAsiaService;
import com.wy.okooo.service.ExchangeService;
import com.wy.okooo.service.IndexStatsService;
import com.wy.okooo.service.KellyCorpCountService;
import com.wy.okooo.service.KellyCorpResultService;
import com.wy.okooo.service.KellyMatchCountService;
import com.wy.okooo.service.LeaguePointsService;
import com.wy.okooo.service.MatchStatsService;
import com.wy.okooo.service.OkJobService;
import com.wy.okooo.service.ProbAverageService;
import com.wy.okooo.service.ScoreOddsService;
import com.wy.okooo.service.SingleMatchService;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * @author leslie
 *
 */
public class AnalyseUtilServiceImpl implements AnalyseUtilService{

	private static Logger LOGGER = Logger
			.getLogger(AnalyseUtilServiceImpl.class.getName());
	
	private KellyCorpResultService kellyCorpResultService;
	
	private CorpService corpService;
	
	private EuroOddsService euroOddsService;
	
	private EuroOddsChangeService euroOddsChangeService;
	
	private AsiaOddsChangeService asiaOddsChangeService;
	
	private KellyCorpCountService kellyCorpCountService;
	
	private KellyMatchCountService kellyMatchCountService;
	
	private ExchangeService exchangeService;
	
	private SingleMatchService singleMatchService;
	
	private AnalyseServiceImpl analyseService;
	
	private LeaguePointsService leaguePointsService;
	
	private MatchStatsService matchStatsService;
	
	private ScoreOddsService scoreOddsService;
	
	private AsiaOddsTrendsService asiaOddsTrendsService;
	
	private IndexStatsService indexStatsService;
	
	private EuroOddsHandicapService euroOddsHandicapService;
	
	private EuroTransAsiaService euroTransAsiaService;
	
	private ProbAverageService probAverageService;
	
	private OkJobService okJobService;
	
	public StringBuilder showKellySummary(List<Match> matches, String okUrlDate, Map<Integer, String> jobTypesOfA, String baseDir) {
		long begin = System.currentTimeMillis();
		
		List<Integer> sortedMatchSeqs = sortMatchSeqByJobTypes(jobTypesOfA);
		// 计算盈亏率，即预测方法C.
		Map<Integer, MatchScore> scoresMap = null;
		if(jobTypesOfA != null){
			scoresMap = analyseService.analyseFromFile(matches, baseDir, sortedMatchSeqs.get(0), sortedMatchSeqs.get(sortedMatchSeqs.size()-1));
		}
		
		// 解析 match.html 页面，获取让球数(handicap), 解析 euroOddsChange_24_261.html 获取最近的99家平均赔率; match.html 页面无法获取，都是0.
		// 然后按照matchName分组放到map中.
		File matchHtmlFile = new File(baseDir + "match.html");
		Map<String, List<Match>> matchesMap = getMatchesMapByMatchName(matchHtmlFile, jobTypesOfA);
		
		// 亚盘: 分析 LOT_ODDS_ASIA_TRENDS 中的数据; 先查询需要的数据. 
		// key:{matchSeq}_{jobType}; value的key: {ruleType}, value: {corpCount};
		Map<String, List<AsiaOddsTrends>> asiaTrendsMap = getAsiaTrendsMap(okUrlDate, sortedMatchSeqs.get(0), sortedMatchSeqs.get(sortedMatchSeqs.size()-1));
		Map<Integer, String> jobTypesOfB = getCurrMatchJobTypeMap(okUrlDate);
		
		// kelly指数离散度分析.
		Map<String, Float> kellyStdDevMap = getKellyStdDevMap(okUrlDate, sortedMatchSeqs.get(0), sortedMatchSeqs.get(sortedMatchSeqs.size()-1));
		Map<Integer, String> jobTypesOfC = getJobTypesOfC(okUrlDate);

		// euroHandicap 让球分析.
		Map<String, String> euroHandicapMap = getEuroHandicapMap(okUrlDate, sortedMatchSeqs.get(0), sortedMatchSeqs.get(sortedMatchSeqs.size()-1));
		Map<Integer, String> jobTypesOfD = getJobTypesOfD(okUrlDate);
		
		// euroTransAsia 欧亚转换分析
		Map<String, String> euroTransAsiaMap = getEuroTransAsiaMap(okUrlDate, sortedMatchSeqs.get(0), sortedMatchSeqs.get(sortedMatchSeqs.size()-1));
		
		// 将各个job的jobTypes放到一起.
		Map<String, Map<Integer, String>> allJobTypes = new HashMap<String, Map<Integer, String>>();
		allJobTypes.put("A", jobTypesOfA);
		allJobTypes.put("B", jobTypesOfB);
		allJobTypes.put("C", jobTypesOfC);
		allJobTypes.put("D", jobTypesOfD);
		// 当天的 LOT_KELLY_CORP_RESULT 中只有需要预测的match, 因为在插入之前都会先删除所有的;
		List<KellyCorpResult> matchNames = kellyCorpResultService.queryAllMatchNameByOkUrlDate(okUrlDate);
		
		StringBuilder sb = new StringBuilder("");
		for(KellyCorpResult result : matchNames){
			sb.append(showKellySummary(okUrlDate, result.getMatchName(), allJobTypes, baseDir, scoresMap,
					matchesMap.get(result.getMatchName()), asiaTrendsMap.get(result.getMatchName()), kellyStdDevMap, 
					euroHandicapMap, euroTransAsiaMap));
		}
		
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin) + " ms.");
		return sb;
	}
	
	/**
	 * 显示 LOT_KELLY_CORP_RESULT 的 ALL_SEQ 中包含指定联赛的符合K2，K3，K4规则的公司数.
	 */
	public String showKellySummary(String okUrlDate, String matchName, Map<String, Map<Integer, String>> allJobTypes, String baseDir, 
			Map<Integer, MatchScore> scoresMap, List<Match> matches, List<AsiaOddsTrends> asiaTrendsList, Map<String, Float> kellyStdDevMap,
			Map<String, String> euroHandicapMap, Map<String, String> euroTransAsiaMap){
		// 获取各个job的jobTypes.
		Map<Integer, String> jobTypesOfA = allJobTypes.get("A");
		Map<Integer, String> jobTypesOfD = allJobTypes.get("D");
		// 该联赛参与过的总公司数
		int totalNumOfCorps = kellyCorpResultService.queryCountCorpsByMatchName(matchName);
		// 各场比赛参与公司排序.
		KellyCorpResult kellyCorpResult = new KellyCorpResult();
		kellyCorpResult.setOkUrlDate(okUrlDate);
		kellyCorpResult.setMatchName(matchName);
		List<KellyCorpResult> allResult = kellyCorpResultService.queryResultByKey(kellyCorpResult);
		// key 是ruleType, value的map key是 matchSeq, value 是次数.
		Map<String, Map<String, Integer>> summaryMap = new HashMap<String, Map<String, Integer>>();
		List<Integer> allMatches = new ArrayList<Integer>();
		List<String> winMatches = new ArrayList<String>();
		List<String> evenMatches = new ArrayList<String>();
		List<String> negaMatches = new ArrayList<String>();
		for(KellyCorpResult result : allResult){
			String ruleType = result.getRuleType();
			String[] allSeqArr = result.getAllSeq().split("\\|");
			String[] winSeqArr = result.getWinSeq().split("\\|");
			String[] evenSeqArr = result.getEvenSeq().split("\\|");
			String[] negaSeqArr = result.getNegaSeq().split("\\|");
			Map<String, Integer> ruleMap = summaryMap.get(ruleType);
			
			/*
			 * 记录胜平负.
			 */
			for(String matchSeq : winSeqArr){
				winMatches.add(matchSeq);
			}
			
			for(String matchSeq : evenSeqArr){
				evenMatches.add(matchSeq);
			}
			
			for(String matchSeq : negaSeqArr){
				negaMatches.add(matchSeq);
			}
			
			// 记录次数.
			if(ruleMap == null){
				ruleMap = new HashMap<String, Integer>();
				for(String matchSeq : allSeqArr){
					ruleMap.put(matchSeq, 1);
				}
			}else{
				for(String matchSeq : allSeqArr){
					Integer matchCount = ruleMap.get(matchSeq);
					if(matchCount == null){
						ruleMap.put(matchSeq, 1);
					}else{
						ruleMap.put(matchSeq, matchCount + 1);
					}
				}
			}
			
			// 记录all matchSeq, 只要 K2,K3,K4,K5,K6,... 有一个包含.
			for(String matchSeqStr : allSeqArr){
				Integer matchSeq = Integer.valueOf(matchSeqStr);
				if(!allMatches.contains(matchSeq)){
					allMatches.add(matchSeq);
				}
			}
			summaryMap.put(ruleType, ruleMap);
		}
		
		// 查询 LOT_KELLY_MATCH_COUNT 用于计算值的变化.  key:  {matchSeq}_{jobType}; value的key: {ruleType}, value: {corpCount};
		Map<String, Map<String, Integer>> kellyCountMap = getKellyCountMap(okUrlDate);

		StringBuilder sb2 = new StringBuilder("K2: HK_1 < LR\n");
		StringBuilder sb3 = new StringBuilder("K3: HK_1 < HK_MAX  E>. V>.\n");
		StringBuilder sb4 = new StringBuilder("K4: VK_1 < VK_MAX  H>. E>.\n");
		StringBuilder sb5 = new StringBuilder("K5: HK_1 < HK_MAX\n");
		StringBuilder sb6 = new StringBuilder("K6: VK_1 < VK_MAX\n");
		StringBuilder sb7 = new StringBuilder("K7: EK_1 < EK_MAX  H>. V>.\n");
		StringBuilder sb8 = new StringBuilder("K8: EK_1 < EK_MAX\n");
		
		Set<String> summaryKeys = summaryMap.keySet();
		for(String ruleType : summaryKeys){
			Map<String, Integer> ruleMap = summaryMap.get(ruleType);
			if("K2".equals(ruleType)){
				// 按照Map中value降序排列;
				List<Map.Entry<String,Integer>> sortList=new ArrayList<Map.Entry<String,Integer>>();
				sortList.addAll(ruleMap.entrySet());  
		        Collections.sort(sortList,OkParseUtils.descMapComparator); 
				for(Map.Entry<String,Integer> entry : sortList){
					String matchSeq = entry.getKey();
					sb2.append(matchSeq).append("  ").append(ruleMap.get(matchSeq)).append("  ")
					.append(winMatches.contains(matchSeq)? "胜" : (evenMatches.contains(matchSeq)? "平": (negaMatches.contains(matchSeq)? "负" : "未")));
					
					int oldJobTypeNum = -1;
					// 获取jobType后面的数字，用于计算 corpCount 的变化情况;
					String jobType = jobTypesOfA.get(Integer.valueOf(matchSeq));
					if(StringUtils.isBlank(jobType)){
						continue;
					}
					Integer jobTypeNum = Integer.valueOf(jobType.substring(1));
					while( ++oldJobTypeNum < jobTypeNum){
						String oldJobType = "A" + oldJobTypeNum;
						String key = matchSeq + "_" + oldJobType;
						if(kellyCountMap.get(key) == null || kellyCountMap.get(key).get(ruleType) == null){
							// 变化值
//							sb2.append(" A(").append(jobTypeNum).append("-").append(oldJobTypeNum).append("):")
//							.append("NA ");
							sb2.append(" A(").append(oldJobTypeNum).append("):")
							.append("NA ");
						}else{
							// 变化值
//							Integer corpCountChange = ruleMap.get(matchSeq) - kellyCountMap.get(key).get(ruleType);
//							sb2.append(" A(").append(jobTypeNum).append("-").append(oldJobTypeNum).append("):")
//							.append(corpCountChange).append(" ");
							Integer oldCorpCount = kellyCountMap.get(key).get(ruleType);
							sb2.append(" A(").append(oldJobTypeNum).append("):")
							.append(oldCorpCount).append(" ");
						}
					}
					
					// 添加 必发交易比例.
					String exchangeInfoHtml = OkConstant.JOB_HTML_FILE_BASE_DIR + OkParseUtils.getDirPahtFromOkUrlDate(okUrlDate) + File.separatorChar + "exchangeInfo_" + matchSeq + ".html";
					File exchangeInfoHtmlFile = new File(exchangeInfoHtml);
					if(exchangeInfoHtmlFile.exists()){
						ExchangeTransactionProp exchangeTransactionProp = exchangeService.getTransactionPropFromFile(new File(exchangeInfoHtml));
						if(exchangeTransactionProp != null){
							Float hostBf = exchangeTransactionProp.getHostBf();
							Float evenBf = exchangeTransactionProp.getEvenBf();
							Float visitingBf = exchangeTransactionProp.getVisitingBf();
							sb2.append(" ").append("BF H:").append(hostBf)
								.append(" E:").append(evenBf)
								.append(" N:").append(visitingBf);
						}
					}
					sb2.append("\n");
				}
			}else if("K3".equals(ruleType)){
				// 按照Map中value降序排列;
				List<Map.Entry<String,Integer>> sortList=new ArrayList<Map.Entry<String,Integer>>();
				sortList.addAll(ruleMap.entrySet());  
		        Collections.sort(sortList,OkParseUtils.descMapComparator); 
				for(Map.Entry<String,Integer> entry : sortList){
					String matchSeq = entry.getKey();
					sb3.append(matchSeq).append("   ").append(ruleMap.get(matchSeq)).append("   ")
					.append(winMatches.contains(matchSeq)? "胜" : (evenMatches.contains(matchSeq)? "平": (negaMatches.contains(matchSeq)? "负" : "未")));
				
					String jobType = jobTypesOfA.get(Integer.valueOf(matchSeq));
					if(StringUtils.isBlank(jobType)){
						continue;
					}
					int oldJobTypeNum = -1;
					// 获取jobType后面的数字，用于计算 corpCount 的变化情况;
					Integer jobTypeNum = Integer.valueOf(jobType.substring(1));
					while( ++oldJobTypeNum < jobTypeNum){
						String oldJobType = "A" + oldJobTypeNum;
						String key = matchSeq + "_" + oldJobType;
						if(kellyCountMap.get(key) == null || kellyCountMap.get(key).get(ruleType) == null){
							sb3.append(" A(").append(oldJobTypeNum).append("):")
							.append("NA ");
						}else{
							Integer oldCorpCount = kellyCountMap.get(key).get(ruleType);
							sb3.append(" A(").append(oldJobTypeNum).append("):")
							.append(oldCorpCount).append(" ");
						}
					}
					sb3.append("\n");
					
					/* 添加 K3a, K4a, K5a, K6a, K7a, K8a
					 即: 赔率变化次数为2,3,4, 最近一次变化导致kelly指数的变化
					 比如host:从 2.0 调整到 1.8, kelly指数从 0.93调整到0.90, 那么kelly指数变化: 0.90 - (0.90/1.8)*2
					 K3a: hostKellyChange < -0.02f && evenKellyChange <= 0 && visitingKellyChange > 0.02f
					 K4a: hostKellyChange > 0.02f && evenKellyChange >= 0 && visitingKellyChange < -0.02f
					 K5a: hostKellyChange < -0.02f
					 K6a: visitingKellyChange < - 0.02f
					 K7a: evenKellyChange < -0.02f && hostKellyChange >= 0 && visitingKellyChange >= 0
					 K8a: evenKellyChange < -0.02f
					 */
					String keya = matchSeq + "_" + jobType;
					String k3aRuleType = "K3a";
					Map<String, Integer> k3aMap = kellyCountMap.get(keya);
					sb3.append("         ").append(k3aMap.get("K3a"));
					oldJobTypeNum = -1;
					while( ++oldJobTypeNum < jobTypeNum){
						String oldJobType = "A" + oldJobTypeNum;
						String key = matchSeq + "_" + oldJobType;
						if(kellyCountMap.get(key) == null || kellyCountMap.get(key).get(k3aRuleType) == null){
							sb3.append(" A(").append(oldJobTypeNum).append("):")
							.append("NA ");
						}else{
							Integer oldCorpCount = kellyCountMap.get(key).get(k3aRuleType);
							sb3.append(" A(").append(oldJobTypeNum).append("):")
							.append(oldCorpCount).append(" ");
						}
					}
					sb3.append("\n");
				}
			}else if("K4".equals(ruleType)){
				// 按照Map中value升序排列;
				List<Map.Entry<String,Integer>> sortList=new ArrayList<Map.Entry<String,Integer>>();
				sortList.addAll(ruleMap.entrySet());  
		        Collections.sort(sortList,OkParseUtils.ascMapComparator); 
				for(Map.Entry<String,Integer> entry : sortList){
					String matchSeq = entry.getKey();
					sb4.append(matchSeq).append("   ").append(ruleMap.get(matchSeq)).append("   ")
					.append(winMatches.contains(matchSeq)? "胜" : (evenMatches.contains(matchSeq)? "平": (negaMatches.contains(matchSeq)? "负" : "未")));
				
					String jobType = jobTypesOfA.get(Integer.valueOf(matchSeq));
					if(StringUtils.isBlank(jobType)){
						continue;
					}
					int oldJobTypeNum = -1;
					// 获取jobType后面的数字，用于计算 corpCount 的变化情况;
					Integer jobTypeNum = Integer.valueOf(jobType.substring(1));
					while( ++oldJobTypeNum < jobTypeNum){
						String oldJobType = "A" + oldJobTypeNum;
						String key = matchSeq + "_" + oldJobType;
						if(kellyCountMap.get(key) == null || kellyCountMap.get(key).get(ruleType) == null){
							sb4.append(" A(").append(oldJobTypeNum).append("):")
							.append("NA ");
						}else{
							Integer oldCorpCount = kellyCountMap.get(key).get(ruleType);
							sb4.append(" A(").append(oldJobTypeNum).append("):")
							.append(oldCorpCount).append(" ");
						}
					}
					sb4.append("\n");
					
					// 添加 K3a, K4a, K5a, K6a, K7a, K8a
					String keya = matchSeq + "_" + jobType;
					String k4aRuleType = "K4a";
					Map<String, Integer> k4aMap = kellyCountMap.get(keya);
					sb4.append("         ").append(k4aMap.get("K4a"));
					oldJobTypeNum = -1;
					while( ++oldJobTypeNum < jobTypeNum){
						String oldJobType = "A" + oldJobTypeNum;
						String key = matchSeq + "_" + oldJobType;
						if(kellyCountMap.get(key) == null || kellyCountMap.get(key).get(k4aRuleType) == null){
							sb4.append(" A(").append(oldJobTypeNum).append("):")
							.append("NA ");
						}else{
							Integer oldCorpCount = kellyCountMap.get(key).get(k4aRuleType);
							sb4.append(" A(").append(oldJobTypeNum).append("):")
							.append(oldCorpCount).append(" ");
						}
					}
					sb4.append("\n");
				}
			}else if("K5".equals(ruleType)){
				// 按照Map中value升序排列;
				List<Map.Entry<String,Integer>> sortList=new ArrayList<Map.Entry<String,Integer>>();
				sortList.addAll(ruleMap.entrySet());  
		        Collections.sort(sortList,OkParseUtils.ascMapComparator); 
				for(Map.Entry<String,Integer> entry : sortList){
					String matchSeq = entry.getKey();
					sb5.append(matchSeq).append("   ").append(ruleMap.get(matchSeq)).append("   ")
					.append(winMatches.contains(matchSeq)? "胜" : (evenMatches.contains(matchSeq)? "平": (negaMatches.contains(matchSeq)? "负" : "未")));
				
					String jobType = jobTypesOfA.get(Integer.valueOf(matchSeq));
					if(StringUtils.isBlank(jobType)){
						continue;
					}
					int oldJobTypeNum = -1;
					// 获取jobType后面的数字，用于计算 corpCount 的变化情况;
					Integer jobTypeNum = Integer.valueOf(jobType.substring(1));
					while( ++oldJobTypeNum < jobTypeNum){
						String oldJobType = "A" + oldJobTypeNum;
						String key = matchSeq + "_" + oldJobType;
						if(kellyCountMap.get(key) == null || kellyCountMap.get(key).get(ruleType) == null){
							sb5.append(" A(").append(oldJobTypeNum).append("):")
							.append("NA ");
						}else{
							Integer oldCorpCount = kellyCountMap.get(key).get(ruleType);
							sb5.append(" A(").append(oldJobTypeNum).append("):")
							.append(oldCorpCount).append(" ");
						}
					}
					sb5.append("\n");
					
					// 添加 K3a, K4a, K5a, K6a, K7a, K8a
					String keya = matchSeq + "_" + jobType;
					String k5aRuleType = "K5a";
					Map<String, Integer> k5aMap = kellyCountMap.get(keya);
					sb5.append("         ").append(k5aMap.get("K5a"));
					oldJobTypeNum = -1;
					while( ++oldJobTypeNum < jobTypeNum){
						String oldJobType = "A" + oldJobTypeNum;
						String key = matchSeq + "_" + oldJobType;
						if(kellyCountMap.get(key) == null || kellyCountMap.get(key).get(k5aRuleType) == null){
							sb5.append(" A(").append(oldJobTypeNum).append("):")
							.append("NA ");
						}else{
							Integer oldCorpCount = kellyCountMap.get(key).get(k5aRuleType);
							sb5.append(" A(").append(oldJobTypeNum).append("):")
							.append(oldCorpCount).append(" ");
						}
					}
					sb5.append("\n");
				}
			}else if("K6".equals(ruleType)){
				// 按照Map中value升序排列;
				List<Map.Entry<String,Integer>> sortList=new ArrayList<Map.Entry<String,Integer>>();
				sortList.addAll(ruleMap.entrySet());  
		        Collections.sort(sortList,OkParseUtils.ascMapComparator); 
				for(Map.Entry<String,Integer> entry : sortList){
					String matchSeq = entry.getKey();
					sb6.append(matchSeq).append("   ").append(ruleMap.get(matchSeq)).append("   ")
					.append(winMatches.contains(matchSeq)? "胜" : (evenMatches.contains(matchSeq)? "平": (negaMatches.contains(matchSeq)? "负" : "未")));
				
					String jobType = jobTypesOfA.get(Integer.valueOf(matchSeq));
					if(StringUtils.isBlank(jobType)){
						continue;
					}
					int oldJobTypeNum = -1;
					// 获取jobType后面的数字，用于计算 corpCount 的变化情况;
					Integer jobTypeNum = Integer.valueOf(jobType.substring(1));
					while( ++oldJobTypeNum < jobTypeNum){
						String oldJobType = "A" + oldJobTypeNum;
						String key = matchSeq + "_" + oldJobType;
						if(kellyCountMap.get(key) == null || kellyCountMap.get(key).get(ruleType) == null){
							sb6.append(" A(").append(oldJobTypeNum).append("):")
							.append("NA ");
						}else{
							Integer oldCorpCount = kellyCountMap.get(key).get(ruleType);
							sb6.append(" A(").append(oldJobTypeNum).append("):")
							.append(oldCorpCount).append(" ");
						}
					}
					sb6.append("\n");
					
					// 添加 K3a, K4a, K5a, K6a, K7a, K8a
					String keya = matchSeq + "_" + jobType;
					String k6aRuleType = "K6a";
					Map<String, Integer> k6aMap = kellyCountMap.get(keya);
					sb6.append("         ").append(k6aMap.get("K6a"));
					oldJobTypeNum = -1;
					while( ++oldJobTypeNum < jobTypeNum){
						String oldJobType = "A" + oldJobTypeNum;
						String key = matchSeq + "_" + oldJobType;
						if(kellyCountMap.get(key) == null || kellyCountMap.get(key).get(k6aRuleType) == null){
							sb6.append(" A(").append(oldJobTypeNum).append("):")
							.append("NA ");
						}else{
							Integer oldCorpCount = kellyCountMap.get(key).get(k6aRuleType);
							sb6.append(" A(").append(oldJobTypeNum).append("):")
							.append(oldCorpCount).append(" ");
						}
					}
					sb6.append("\n");
				}
			}else if("K7".equals(ruleType)){
				// 按照Map中value升序排列;
				List<Map.Entry<String,Integer>> sortList=new ArrayList<Map.Entry<String,Integer>>();
				sortList.addAll(ruleMap.entrySet());  
		        Collections.sort(sortList,OkParseUtils.ascMapComparator); 
				for(Map.Entry<String,Integer> entry : sortList){
					String matchSeq = entry.getKey();
					sb7.append(matchSeq).append("   ").append(ruleMap.get(matchSeq)).append("   ")
					.append(winMatches.contains(matchSeq)? "胜" : (evenMatches.contains(matchSeq)? "平": (negaMatches.contains(matchSeq)? "负" : "未")));
				
					String jobType = jobTypesOfA.get(Integer.valueOf(matchSeq));
					if(StringUtils.isBlank(jobType)){
						continue;
					}
					int oldJobTypeNum = -1;
					// 获取jobType后面的数字，用于计算 corpCount 的变化情况;
					Integer jobTypeNum = Integer.valueOf(jobType.substring(1));
					while( ++oldJobTypeNum < jobTypeNum){
						String oldJobType = "A" + oldJobTypeNum;
						String key = matchSeq + "_" + oldJobType;
						if(kellyCountMap.get(key) == null || kellyCountMap.get(key).get(ruleType) == null){
							sb7.append(" A(").append(oldJobTypeNum).append("):")
							.append("NA ");
						}else{
							Integer oldCorpCount = kellyCountMap.get(key).get(ruleType);
							sb7.append(" A(").append(oldJobTypeNum).append("):")
							.append(oldCorpCount).append(" ");
						}
					}
					sb7.append("\n");
					
					// 添加 K3a, K4a, K5a, K6a, K7a, K8a
					String keya = matchSeq + "_" + jobType;
					String k7aRuleType = "K7a";
					Map<String, Integer> k7aMap = kellyCountMap.get(keya);
					sb7.append("         ").append(k7aMap.get("K7a"));
					oldJobTypeNum = -1;
					while( ++oldJobTypeNum < jobTypeNum){
						String oldJobType = "A" + oldJobTypeNum;
						String key = matchSeq + "_" + oldJobType;
						if(kellyCountMap.get(key) == null || kellyCountMap.get(key).get(k7aRuleType) == null){
							sb7.append(" A(").append(oldJobTypeNum).append("):")
							.append("NA ");
						}else{
							Integer oldCorpCount = kellyCountMap.get(key).get(k7aRuleType);
							sb7.append(" A(").append(oldJobTypeNum).append("):")
							.append(oldCorpCount).append(" ");
						}
					}
					sb7.append("\n");
				}
			}else if("K8".equals(ruleType)){
				// 按照Map中value升序排列;
				List<Map.Entry<String,Integer>> sortList=new ArrayList<Map.Entry<String,Integer>>();
				sortList.addAll(ruleMap.entrySet());  
		        Collections.sort(sortList,OkParseUtils.ascMapComparator); 
				for(Map.Entry<String,Integer> entry : sortList){
					String matchSeq = entry.getKey();
					sb8.append(matchSeq).append("   ").append(ruleMap.get(matchSeq)).append("   ")
					.append(winMatches.contains(matchSeq)? "胜" : (evenMatches.contains(matchSeq)? "平": (negaMatches.contains(matchSeq)? "负" : "未")));
				
					String jobType = jobTypesOfA.get(Integer.valueOf(matchSeq));
					if(StringUtils.isBlank(jobType)){
						continue;
					}
					int oldJobTypeNum = -1;
					// 获取jobType后面的数字，用于计算 corpCount 的变化情况;
					Integer jobTypeNum = Integer.valueOf(jobType.substring(1));
					while( ++oldJobTypeNum < jobTypeNum){
						String oldJobType = "A" + oldJobTypeNum;
						String key = matchSeq + "_" + oldJobType;
						if(kellyCountMap.get(key) == null || kellyCountMap.get(key).get(ruleType) == null){
							sb8.append(" A(").append(oldJobTypeNum).append("):")
							.append("NA ");
						}else{
							Integer oldCorpCount = kellyCountMap.get(key).get(ruleType);
							sb8.append(" A(").append(oldJobTypeNum).append("):")
							.append(oldCorpCount).append(" ");
						}
					}
					sb8.append("\n");
					
					// 添加 K3a, K4a, K5a, K6a, K7a, K8a
					String keya = matchSeq + "_" + jobType;
					String k8aRuleType = "K8a";
					Map<String, Integer> k8aMap = kellyCountMap.get(keya);
					sb8.append("         ").append(k8aMap.get("K8a"));
					oldJobTypeNum = -1;
					while( ++oldJobTypeNum < jobTypeNum){
						String oldJobType = "A" + oldJobTypeNum;
						String key = matchSeq + "_" + oldJobType;
						if(kellyCountMap.get(key) == null || kellyCountMap.get(key).get(k8aRuleType) == null){
							sb8.append(" A(").append(oldJobTypeNum).append("):")
							.append("NA ");
						}else{
							Integer oldCorpCount = kellyCountMap.get(key).get(k8aRuleType);
							sb8.append(" A(").append(oldJobTypeNum).append("):")
							.append(oldCorpCount).append(" ");
						}
					}
					sb8.append("\n");
				}
			}
		}
		
		// proLoss 的变化, ruleType 为P1, 暂时在数据库中没记录 ruleType.
		StringBuilder sbP1 = showProLoss(okUrlDate, scoresMap, jobTypesOfA, allMatches);
		
		// 比赛的让球数和99家平均的胜平负赔率.
		StringBuilder handicapSb = getHandicapOddsSb(matches);
		
		// 亚盘的分析 RULE KH KI/Ki KJ KK
		StringBuilder asiaSb = getAsiaOddsAnalyseSb(asiaTrendsList, allJobTypes, winMatches, evenMatches, negaMatches,
				kellyCountMap, kellyStdDevMap, euroHandicapMap);
		
		// 最近几场比赛结果;
		StringBuilder latestResultSb = queryLatestMatchResult(baseDir, allMatches);
		
		// RULE KO: 让球分析.
		StringBuilder euroHandicapSb = getEuroHandicapSb(matches, jobTypesOfD, euroHandicapMap);
		
		// RULE KP: 欧亚转换.
		StringBuilder euroTransAsiaSb = getEuroTransAsiaSb(matches, jobTypesOfA, euroTransAsiaMap);
		
		// RULE KR: 欧赔变化.
		StringBuilder euroOddsChangeDailySb = getEuroOddsChangeDailySb(matches, okUrlDate, matchName);
		
		// RULE KS: 亚盘变化  显示每个公司最初的、盘口变化后的、最新的赔率数据
		StringBuilder asiaOddsChangeDailySb = getAsiaOddsChangeDailySb(matches, okUrlDate);
		
		// rule KK: 欧赔变化值(当前值 - 初始值)的频数分析. 
		
		// 将数量信息插入 LOT_KELLY_MATCH_COUNT
		if (jobTypesOfA != null && !jobTypesOfA.isEmpty()) {
			// 不判断是否存在，在 kellyMatchCountService.insertMatchCount 中都会先删除再插入;
			List<KellyMatchCount> matchCountList = new ArrayList<KellyMatchCount>();
			Timestamp matchCountTime = new Timestamp(Calendar.getInstance()
					.getTimeInMillis());
			Set<String> matchCountKeys = summaryMap.keySet();
			for (String ruleType : matchCountKeys) {
				Map<String, Integer> ruleMap = summaryMap.get(ruleType);
				// 按照Map中value降序排列;
				List<Map.Entry<String, Integer>> sortList = new ArrayList<Map.Entry<String, Integer>>();
				sortList.addAll(ruleMap.entrySet());
				Collections.sort(sortList, OkParseUtils.descMapComparator);
				for (Map.Entry<String, Integer> entry : sortList) {
					String jobType = jobTypesOfA.get(Integer.valueOf(entry.getKey()));
					if(StringUtils.isBlank(jobType)){
						continue;
					}
					KellyMatchCount matchCount = new KellyMatchCount();
					matchCount.setOkUrlDate(okUrlDate);
					Integer matchSeq = Integer.valueOf(entry.getKey());
					matchCount.setMatchSeq(matchSeq);
					matchCount.setJobType(jobType);
					matchCount.setRuleType(ruleType);
					matchCount.setCorpCount(ruleMap.get(entry.getKey()));
					
					// 添加盈亏率, okUrlDate, matchSeq, jobType 3者相同的话， proLoss就相同, 和 ruleType 没关系.
					if(scoresMap != null){
						MatchScore matchScore = scoresMap.get(matchSeq);
						String hostC = matchScore.getCompIndexs() == null ? null : 
							String.format("%.3f", matchScore.getCompIndexs().get("host"));
						String evenC = matchScore.getCompIndexs() == null ? null : 
							String.format("%.3f", matchScore.getCompIndexs().get("even"));
						String visitingC = matchScore.getCompIndexs() == null ? null : 
							String.format("%.3f", matchScore.getCompIndexs().get("visiting"));
						matchCount.setProLoss(hostC + "|" + evenC + "|" + visitingC + "|");
					}
					
					matchCount.setTimestamp(matchCountTime);
					matchCountList.add(matchCount);
				}
			}
			kellyMatchCountService.insertMatchCountBatch(matchCountList);
		}
		
		// 清空euroChangeDailyStatsMap
		okJobService.emptyEuroChangeDailyStatsMap();
		
		String result = "\n" + matchName + "  totalNumOfCorps: " + totalNumOfCorps + "\n" + sb2.toString() + "\n" + sb3.toString() + "\n" + sb4.toString();
		LOGGER.info("\n" + matchName + "  totalNumOfCorps: " + totalNumOfCorps + "\n" + sb2.toString() + "\n" + sb3.toString() + "\n" + sb4.toString());
		
		result += "\n" + handicapSb.toString();
		LOGGER.info("\n" + handicapSb.toString());
		
		result += "\n" + sb5.toString() + "\n" + sb6.toString();
		LOGGER.info("\n" + sb5.toString() + "\n" + sb6.toString());
		
		result += "\n" + sb7.toString() + "\n" + sb8.toString();
		LOGGER.info("\n" + sb7.toString() + "\n" + sb8.toString());
		
		result += "\n" + asiaSb.toString();
		LOGGER.info("\n" + asiaSb.toString());
		
		result += "\n" + euroHandicapSb.toString();
		LOGGER.info("\n" + euroHandicapSb.toString());
		
		result += "\n" + sbP1.toString();
		LOGGER.info("\n" + sbP1.toString());
		
		result += "\n" + latestResultSb.toString();
		LOGGER.info("\n" + latestResultSb.toString());
		
		result += "\n" + euroTransAsiaSb.toString();
		LOGGER.info("\n" + euroTransAsiaSb.toString());
		
		result += "\n" + euroOddsChangeDailySb.toString();
		LOGGER.info("\n" + euroOddsChangeDailySb.toString());
		
		result += "\n" + asiaOddsChangeDailySb.toString();
		LOGGER.info("\n" + asiaOddsChangeDailySb.toString());
		
		result += "\n";
		return result;
	}
		
	/*
	 * P1: proLoss(预测方法C) 的变化情况;
	 */
	private StringBuilder showProLoss(String okUrlDate, Map<Integer, MatchScore> scoresMap, Map<Integer, String> jobTypes,
			List<Integer> allMatches){
		StringBuilder sb = new StringBuilder("PL BF: lossRatio - HProb * H \n");
		List<KellyMatchCount> matchCounts = kellyMatchCountService.queryExistsMatchCountByDate(okUrlDate);
		if(scoresMap == null || matchCounts == null || matchCounts.isEmpty()){
			return sb;
		}
		
		Map<String, String> proLossMap = new HashMap<String, String>();
		Set<String> processedKeys = new HashSet<String>();
		Set<String> allMatchSeqs = new HashSet<String>();
		for(KellyMatchCount matchCount : matchCounts){
			String matchSeq = String.valueOf(matchCount.getMatchSeq());
			String jobType = matchCount.getJobType();
			String key = matchSeq + "_" + jobType;
			String proLoss = matchCount.getProLoss();

			// 已处理过的.
			if(processedKeys.contains(key)){
				continue;
			}
			processedKeys.add(key);
			allMatchSeqs.add(matchSeq);
			
			// 初始化map.
			String hostKey = key + "_H";
			String evenKey = key + "_E";
			String negaKey = key + "_N";
			proLossMap.put(hostKey, null);
			proLossMap.put(evenKey, null);
			proLossMap.put(negaKey, null);
			
			if(StringUtils.isBlank(proLoss) || proLoss.equalsIgnoreCase("null")){
				continue;
			}
			String hostProLoss = proLoss.split("\\|")[0];
			String evenProLoss = proLoss.split("\\|")[1];
			String negaProLoss = proLoss.split("\\|")[2];
			proLossMap.put(hostKey, hostProLoss);
			proLossMap.put(evenKey, evenProLoss);
			proLossMap.put(negaKey, negaProLoss);
		}
		
		for(Integer matchSeq : allMatches){
			// 文件是否存在.
			String exchangeInfoHtml = OkConstant.JOB_HTML_FILE_BASE_DIR + OkParseUtils.getDirPahtFromOkUrlDate(okUrlDate) + File.separatorChar + "exchangeInfo_" + matchSeq + ".html";
			File exchangeInfoHtmlFile = new File(exchangeInfoHtml);
			if(!exchangeInfoHtmlFile.exists()){
				continue;
			}
			
			// 只有当必发交易比例在某个范围内才会显示 P1: proLoss  去掉这个限制.
			/*
			ExchangeTransactionProp exchangeTransactionProp = exchangeService.getTransactionPropFromFile(new File(exchangeInfoHtml));
			if(exchangeTransactionProp == null){
				continue;
			}
			Float hostBf = exchangeTransactionProp.getHostBf();
			Float evenBf = exchangeTransactionProp.getEvenBf();
			Float visitingBf = exchangeTransactionProp.getVisitingBf();
			// 去掉
			if(hostBf == null || evenBf == null || visitingBf == null 
					|| hostBf > 70.0 || evenBf > 70.0 || visitingBf > 70.0){
				continue;
			}
			*/
			// 本次的proLoss值.
			MatchScore matchScore = scoresMap.get(Integer.valueOf(matchSeq));
			String hostC = matchScore.getCompIndexs() == null ? null : 
				String.format("%.3f", matchScore.getCompIndexs().get("host"));
			String evenC = matchScore.getCompIndexs() == null ? null : 
				String.format("%.3f", matchScore.getCompIndexs().get("even"));
			String visitingC = matchScore.getCompIndexs() == null ? null : 
				String.format("%.3f", matchScore.getCompIndexs().get("visiting"));
			
			String jobType = jobTypes.get(Integer.valueOf(matchSeq));
			if(StringUtils.isBlank(jobType)){
				LOGGER.error("matchSeq not exists in jobTypes: " + matchSeq);
				continue;
			}
			// 历史的proLoss值.
			int oldJobTypeNum = -1;
			Integer jobTypeNum = Integer.valueOf(jobType.substring(1));
			String oldHost = "";
			String oldEven = "";
			String oldNega = "";
			while( ++oldJobTypeNum < jobTypeNum){
				String oldJobType = "A" + oldJobTypeNum;
				String key = matchSeq + "_" + oldJobType;
				String hostKey = key + "_H";
				String evenKey = key + "_E";
				String negaKey = key + "_N";
				
				oldHost += " A(" + oldJobTypeNum + "):" + proLossMap.get(hostKey) + " ";
				oldEven += " A(" + oldJobTypeNum + "):" + proLossMap.get(evenKey) + " ";
				oldNega += " A(" + oldJobTypeNum + "):" + proLossMap.get(negaKey) + " ";
			}
			
			sb.append(matchSeq).append("   H:").append(hostC).append(" ").append(oldHost).append("\n")
			                   .append("     E:").append(evenC).append(" ").append(oldEven).append("\n")
			                   .append("     N:").append(visitingC).append(" ").append(oldNega).append("\n");
		}
		return sb;
	}
	
	/**
	 * 查看最近几场比赛的结果, 分别查询主队在主、客场, 客队在主、客场的情况;
	 * @param baseDir
	 * @param allMatches
	 * @return
	 */
	private StringBuilder queryLatestMatchResult(String baseDir, List<Integer> allMatches){
		StringBuilder latestResultSb = new StringBuilder("Latest:\n");
		Collections.sort(allMatches);
		List<File> matchHtmlFiles = OkParseUtils.findFileByName(
				baseDir, OkConstant.MATCH_FILE_NAME);
		List<Match> matches = singleMatchService.getAllMatchFromFile(matchHtmlFiles.get(0), allMatches.get(0), allMatches.get(allMatches.size()-1));
		for(Match match : matches){
			for(Integer matchSeq : allMatches){
				if(matchSeq.intValue() != match.getMatchSeq().intValue()){
					continue;
				}
				latestResultSb.append(matchSeq).append(" ");
				String hostTeamName = match.getHostTeamName();
				String visitingTeamName = match.getVisitingTeamName();
				
				latestResultSb.append("主主: ");
				List<Match> hostHostMatches = singleMatchService.queryMatchesByHostTeamName(hostTeamName);
				for(Match hostHostMatch : hostHostMatches){
					latestResultSb.append(hostHostMatch.getHostGoals()).append(":").append(hostHostMatch.getVisitingGoals()).append(" ");
				}
				latestResultSb.append("   主客: ");
				List<Match> hostVisitingMatches = singleMatchService.queryMatchesByVisitingTeamName(hostTeamName);
				for(Match hostVisitingMatch : hostVisitingMatches){
					latestResultSb.append(hostVisitingMatch.getHostGoals()).append(":").append(hostVisitingMatch.getVisitingGoals()).append(" ");
				}
				latestResultSb.append("\n");
				
				latestResultSb.append("       客主: ");
				List<Match> visitingHostMatches = singleMatchService.queryMatchesByHostTeamName(visitingTeamName);
				for(Match visitingHostMatch : visitingHostMatches){
					latestResultSb.append(visitingHostMatch.getHostGoals()).append(":").append(visitingHostMatch.getVisitingGoals()).append(" ");
				}
				latestResultSb.append("   客客: ");
				List<Match> visitingVisitingMatches = singleMatchService.queryMatchesByVisitingTeamName(visitingTeamName);
				for(Match visitingVisitingMatch : visitingVisitingMatches){
					latestResultSb.append(visitingVisitingMatch.getHostGoals()).append(":").append(visitingVisitingMatch.getVisitingGoals()).append(" ");
				}
				latestResultSb.append("\n");
			}
		}
		return latestResultSb;
	}
	
	private StringBuilder getEuroHandicapSb(List<Match> matches, Map<Integer, String> jobTypesOfD, Map<String, String> euroHandicapMap){
		StringBuilder sb = new StringBuilder("");
		sb.append("KO: Euro Handicap Average").append("\n");
		for(Match match : matches){
			Integer matchSeq = match.getMatchSeq();
			String jobType = jobTypesOfD.get(matchSeq);
			if(StringUtils.isBlank(jobType)){
				LOGGER.error("matchSeq not exists in jobTypeOfD: " + matchSeq);
				continue;
			}
			// 先输出最近的, 后面才是以前的jobType.
			String preKey = matchSeq + "_" + jobType + "_" + "AVG";
			
			sb.append(matchSeq).append(" ").append(euroHandicapMap.get(preKey + "_" + "HC")).append(" ")
			.append("H:").append(euroHandicapMap.get(preKey + "_" + "H")).append(" ")
			.append("E:").append(euroHandicapMap.get(preKey + "_" + "E")).append(" ")
			.append("V:").append(euroHandicapMap.get(preKey + "_" + "V")).append(" ")
			.append("\n");
			
			// 获取jobType后面的数字，用于计算变化情况;
			Integer jobTypeNum = Integer.valueOf(jobType.substring(1));
			int oldJobTypeNum = -1;
			while( ++oldJobTypeNum < jobTypeNum){
				String oldJobType = "D" + oldJobTypeNum;
				String preKeyOld = matchSeq + "_" + oldJobType + "_" + "AVG";
				if(euroHandicapMap.get(preKeyOld + "_" + "H") == null){
					sb.append("     D(").append(oldJobTypeNum).append("):")
					.append("NA ");
				}else{
					sb.append("     D(").append(oldJobTypeNum).append("):")
					.append("H:").append(euroHandicapMap.get(preKeyOld + "_" + "H")).append(" ")
					.append("E:").append(euroHandicapMap.get(preKeyOld + "_" + "E")).append(" ")
					.append("V:").append(euroHandicapMap.get(preKeyOld + "_" + "V")).append(" ");
				}
				sb.append("\n");
			}
		}
		
		sb.append("\n Euro Handicap dispersion").append("\n");
		for(Match match : matches){
			Integer matchSeq = match.getMatchSeq();
			String jobType = jobTypesOfD.get(matchSeq);
			if(StringUtils.isBlank(jobType)){
				LOGGER.error("matchSeq not exists in jobTypeOfD: " + matchSeq);
				continue;
			}
			// 先输出最近的, 后面才是以前的jobType.
			String preKey = matchSeq + "_" + jobType + "_" + "DIS";
			
			sb.append(matchSeq).append(" ").append(euroHandicapMap.get(preKey + "_" + "HC")).append(" ")
			.append("H:").append(euroHandicapMap.get(preKey + "_" + "H")).append(" ")
			.append("E:").append(euroHandicapMap.get(preKey + "_" + "E")).append(" ")
			.append("V:").append(euroHandicapMap.get(preKey + "_" + "V")).append(" ")
			.append("\n");
			
			// 获取jobType后面的数字，用于计算变化情况;
			Integer jobTypeNum = Integer.valueOf(jobType.substring(1));
			int oldJobTypeNum = -1;
			while( ++oldJobTypeNum < jobTypeNum){
				String oldJobType = "D" + oldJobTypeNum;
				String preKeyOld = matchSeq + "_" + oldJobType + "_" + "DIS";
				if(euroHandicapMap.get(preKeyOld + "_" + "H") == null){
					sb.append("   D(").append(oldJobTypeNum).append("):")
					.append("NA ");
				}else{
					sb.append("    D(").append(oldJobTypeNum).append("):")
					.append("H:").append(euroHandicapMap.get(preKeyOld + "_" + "H")).append(" ")
					.append("E:").append(euroHandicapMap.get(preKeyOld + "_" + "E")).append(" ")
					.append("V:").append(euroHandicapMap.get(preKeyOld + "_" + "V")).append(" ");
				}
				sb.append("\n");
			}
		}
		return sb;
	}
	
	/**
	 * 欧赔99家平均.
	 * @param matches
	 * @return
	 */
	private StringBuilder getHandicapOddsSb(List<Match> matches){
		StringBuilder sb = new StringBuilder("");
		sb.append("Euro 99 Average:").append("\n");
		if(matches != null && !matches.isEmpty()){
			for(Match match : matches){
				sb.append(match.getMatchSeq()).append("  ").append("Bei Handicap: ").append(match.getHandicap() > 0 ? ("+" + match.getHandicap()) : match.getHandicap())
				.append("  H:").append(match.getHostOdds())
				.append("  E:").append(match.getEvenOdds())
				.append("  N:").append(match.getVisitingOdds())
				.append("\n");
			}
		}
		return sb;
	}
	
	
	private StringBuilder getAsiaOddsAnalyseSb(List<AsiaOddsTrends> asiaTrendsList, Map<String, Map<Integer, String>> allJobTypes,
			List<String> winMatches, List<String> evenMatches, List<String> negaMatches, Map<String, Map<String, Integer>> kellyCountMap, 
			Map<String, Float> kellyStdDevMap, Map<String, String> kellyDispersionMap){
		Map<Integer, String> jobTypesOfA = allJobTypes.get("A");
		Map<Integer, String> jobTypesOfB = allJobTypes.get("B");
		Map<Integer, String> jobTypesOfC = allJobTypes.get("C");
		StringBuilder sb = new StringBuilder("");
		if(asiaTrendsList == null || asiaTrendsList.isEmpty()){
			return sb;
		}
		
		// 构造平均值map  key: {matchSeq}_{jobType}_{flag}  value: 对应值，  
		// 其中flag: H:最新主赔率; HC:最新盘口; V:最新客赔率. IH, IHC, IV: 初始的. HK: 最新主凯利指数  VK: 最新客凯利指数.
	    Map<String, String> avgMap = new HashMap<String, String>();
	    // 存放matchSeq.
	    Set<Integer> matchSeqs = new HashSet<Integer>();
	    // 当前jobType的开盘公司数，而不是所有的jobType. key: {matchSeq} value: List<oddsCorpNames>
	    Map<Integer, List<String>> corpNamesMap = new HashMap<Integer, List<String>>();
		for(AsiaOddsTrends trends : asiaTrendsList){
			Integer matchSeq = trends.getMatchSeq();
			String jobType = trends.getJobType();
			String oddsCorpName = trends.getOddsCorpName();
			if("平均值".equals(oddsCorpName)){
				avgMap.put(matchSeq + "_" + jobType +"_" + "H", String.valueOf(trends.getHostOdds()));
				avgMap.put(matchSeq + "_" + jobType +"_" + "HC", String.valueOf(trends.getHandicap()));
				avgMap.put(matchSeq + "_" + jobType +"_" + "V", String.valueOf(trends.getVisitingOdds()));
				avgMap.put(matchSeq + "_" + jobType +"_" + "IH", String.valueOf(trends.getInitHostOdds()));
				avgMap.put(matchSeq + "_" + jobType +"_" + "IHC", String.valueOf(trends.getInitHandicap()));
				avgMap.put(matchSeq + "_" + jobType +"_" + "IV", String.valueOf(trends.getInitVisitingOdds()));
				avgMap.put(matchSeq + "_" + jobType +"_" + "HK", String.valueOf(trends.getHostKelly()));
				avgMap.put(matchSeq + "_" + jobType +"_" + "VK", String.valueOf(trends.getVisitingKelly()));
			}
			matchSeqs.add(matchSeq);
			
			// 构造 corpNamesMap.
			String currJobType = jobTypesOfB.get(matchSeq);
			if(!StringUtils.isBlank(currJobType) && currJobType.equals(jobType)
					&& !"平均值".equals(oddsCorpName) && !"最大值".equals(oddsCorpName) && !"最小值".equals(oddsCorpName)){
				List<String> corpNamesList = corpNamesMap.get(matchSeq);
				if(corpNamesList == null){
					corpNamesList = new ArrayList<String>();
					corpNamesList.add(oddsCorpName);
				}else{
					corpNamesList.add(oddsCorpName);
				}
				corpNamesMap.put(matchSeq, corpNamesList);
			}
		}
		
		//KH: 平均值.
		// 开盘的公司数， 去掉 平均值，最小值，最大值.
		sb.append("KH: Asia Average \n");
		for(Integer matchSeq : matchSeqs){
			String jobType = jobTypesOfB.get(matchSeq);
			if(StringUtils.isBlank(jobType)){
				continue;
			}
			// 先输出最近的, 后面才是以前的jobType.
			String preKey = matchSeq + "_" + jobType;
			sb.append(matchSeq).append(" ").append(corpNamesMap.get(matchSeq) == null ? 0 : corpNamesMap.get(matchSeq).size()).append(" ")
			.append("H:").append(avgMap.get(preKey + "_" + "H")).append(" ")
			.append("HC:").append(avgMap.get(preKey + "_" + "HC")).append(" ")
			.append("V:").append(avgMap.get(preKey + "_" + "V")).append(" ")
			.append("\n").append("           ")
			.append("IH:").append(avgMap.get(preKey + "_" + "IH")).append(" ")
			.append("IHC:").append(avgMap.get(preKey + "_" + "IHC")).append(" ")
			.append("IV:").append(avgMap.get(preKey + "_" + "IV")).append(" ")
			.append(winMatches.contains(matchSeq)? "胜" : (evenMatches.contains(matchSeq)? "平": (negaMatches.contains(matchSeq)? "负" : "未")))
			.append("\n");
			
			// 获取jobType后面的数字，用于计算变化情况;
			Integer jobTypeNum = Integer.valueOf(jobType.substring(1));
			int oldJobTypeNum = -1;
			while( ++oldJobTypeNum < jobTypeNum){
				String oldJobType = "B" + oldJobTypeNum;
				String preKeyOld = matchSeq + "_" + oldJobType;
				if(avgMap.get(preKeyOld + "_" + "H") == null){
					sb.append("       B(").append(oldJobTypeNum).append("):")
					.append("NA ");
				}else{
					sb.append("       B(").append(oldJobTypeNum).append("):")
					.append("H:").append(avgMap.get(preKeyOld + "_" + "H")).append(" ")
					.append("HC:").append(avgMap.get(preKeyOld + "_" + "HC")).append(" ")
					.append("V:").append(avgMap.get(preKeyOld + "_" + "V")).append(" ");
//					.append("\n");
//					.append("            ")
//					.append("IH:").append(avgMap.get(preKeyOld + "_" + "IH")).append(" ")
//					.append("IHC:").append(avgMap.get(preKeyOld + "_" + "IHC")).append(" ")
//					.append("IV:").append(avgMap.get(preKeyOld + "_" + "IV")).append(" ");
				}
				sb.append("\n");
			}
		}
		
		// KI/Ki (AsiaOddsKellyJob中计算)展示, 先展示当前的.  B0没有值， 从B1开始, 从 kellyCountMap 中获取.
		// 去掉 KI/Ki 2015-05-15
		
		// KJ: euro kelly指数离散度分析.
		sb.append("\n").append("KJ: euro kelly dispersion all\n");
		for(Integer matchSeq : matchSeqs){
			String jobType = jobTypesOfC.get(matchSeq);
			if(StringUtils.isBlank(jobType)){
				LOGGER.error("matchSeq not exists in jobTypeOfC: " + matchSeq);
				continue;
			}
			// 先输出最近的
			String preKey = matchSeq + "_" + jobType;
			sb.append(matchSeq).append(" ")
			.append("H:").append(kellyStdDevMap.get(preKey + "_" + "H")).append(" ")
			.append("E:").append(kellyStdDevMap.get(preKey + "_" + "E")).append(" ")
			.append("V:").append(kellyStdDevMap.get(preKey + "_" + "V")).append(" ")
			.append("\n").append("      ")
			.append("IH:").append(kellyStdDevMap.get(preKey + "_" + "IH")).append(" ")
			.append("IE:").append(kellyStdDevMap.get(preKey + "_" + "IE")).append(" ")
			.append("IV:").append(kellyStdDevMap.get(preKey + "_" + "IV")).append(" ")
			.append(winMatches.contains(matchSeq)? "胜" : (evenMatches.contains(matchSeq)? "平": (negaMatches.contains(matchSeq)? "负" : "未")))
			.append("\n");
			
			// 获取jobType后面的数字，用于计算变化情况;
			Integer jobTypeNum = Integer.valueOf(jobType.substring(1));
			int oldJobTypeNum = -1;
			while( ++oldJobTypeNum < jobTypeNum){
				String oldJobType = "C" + oldJobTypeNum;
				String preKeyOld = matchSeq + "_" + oldJobType;
				if(kellyStdDevMap.get(preKeyOld + "_" + "H") == null){
					sb.append("    C(").append(oldJobTypeNum).append("):")
					.append("NA ");
				}else{
					sb.append("    C(").append(oldJobTypeNum).append("):")
					.append("H:").append(kellyStdDevMap.get(preKeyOld + "_" + "H")).append(" ")
					.append("E:").append(kellyStdDevMap.get(preKeyOld + "_" + "E")).append(" ")
					.append("V:").append(kellyStdDevMap.get(preKeyOld + "_" + "V")).append(" ");
				}
				sb.append("\n");
			}
		}
		
		// KJ: euro kelly指数离散度分析: 按照lossRatio分区间.  这里是按照jobTypesOfA来做的。 因为在job C 中不方便分析 euroOddsChange, 而 euroOdds页面通过ajax获取不到.
		sb.append("\n").append("euro kelly dispersion section\n");
		StringBuilder section1Sb = new StringBuilder("");
		StringBuilder section2Sb = new StringBuilder("");
		String section1Sec = "";
		String section2Sec = "";
		for(Integer matchSeq : matchSeqs){
			String jobType = jobTypesOfA.get(matchSeq);
			if(StringUtils.isBlank(jobType)){
				LOGGER.error("matchSeq not exists in jobTypesOfA: " + matchSeq);
				continue;
			}
			// 先输出最近的
			String section1PreKey = matchSeq + "_" + jobType + "_" + "SEC1";
			String section2PreKey = matchSeq + "_" + jobType + "_" + "SEC2";
			section1Sec = kellyDispersionMap.get(section1PreKey + "_" + "S");
			section2Sec = kellyDispersionMap.get(section2PreKey + "_" + "S");
			section1Sb.append(matchSeq).append(" ").append(kellyDispersionMap.get(section1PreKey + "_" + "N")).append(" ")
			.append("H:").append(kellyDispersionMap.get(section1PreKey + "_" + "H")).append(" ")
			.append("E:").append(kellyDispersionMap.get(section1PreKey + "_" + "E")).append(" ")
			.append("V:").append(kellyDispersionMap.get(section1PreKey + "_" + "V")).append(" ")
			.append(winMatches.contains(matchSeq)? "胜" : (evenMatches.contains(matchSeq)? "平": (negaMatches.contains(matchSeq)? "负" : "未")))
			.append("\n");
			section2Sb.append(matchSeq).append(" ").append(kellyDispersionMap.get(section2PreKey + "_" + "N")).append(" ")
			.append("H:").append(kellyDispersionMap.get(section2PreKey + "_" + "H")).append(" ")
			.append("E:").append(kellyDispersionMap.get(section2PreKey + "_" + "E")).append(" ")
			.append("V:").append(kellyDispersionMap.get(section2PreKey + "_" + "V")).append(" ")
			.append(winMatches.contains(matchSeq)? "胜" : (evenMatches.contains(matchSeq)? "平": (negaMatches.contains(matchSeq)? "负" : "未")))
			.append("\n");
			
			// 获取jobType后面的数字，用于计算变化情况;
			Integer jobTypeNum = Integer.valueOf(jobType.substring(1));
			int oldJobTypeNum = -1;
			while( ++oldJobTypeNum < jobTypeNum){
				String oldJobType = "A" + oldJobTypeNum;
				String section1PreKeyOld = matchSeq + "_" + oldJobType + "_" + "SEC1";
				String section2PreKeyOld = matchSeq + "_" + oldJobType + "_" + "SEC2";
				if(kellyDispersionMap.get(section1PreKeyOld + "_" + "S") == null 
						|| kellyDispersionMap.get(section2PreKeyOld + "_" + "S") == null){
					section1Sb.append("    A(").append(oldJobTypeNum).append("):")
					.append("NA ");
					section2Sb.append("    A(").append(oldJobTypeNum).append("):")
					.append("NA ");
				}else{
					section1Sb.append("    A(").append(oldJobTypeNum).append("):")
					.append("H:").append(kellyDispersionMap.get(section1PreKeyOld + "_" + "H")).append(" ")
					.append("E:").append(kellyDispersionMap.get(section1PreKeyOld + "_" + "E")).append(" ")
					.append("V:").append(kellyDispersionMap.get(section1PreKeyOld + "_" + "V")).append(" ");
					section2Sb.append("    A(").append(oldJobTypeNum).append("):")
					.append("H:").append(kellyDispersionMap.get(section2PreKeyOld + "_" + "H")).append(" ")
					.append("E:").append(kellyDispersionMap.get(section2PreKeyOld + "_" + "E")).append(" ")
					.append("V:").append(kellyDispersionMap.get(section2PreKeyOld + "_" + "V")).append(" ");
				}
				section1Sb.append("\n");
				section2Sb.append("\n");
			}
		}
		section1Sb.insert(0, section1Sec + "\n");
		section2Sb.insert(0, section2Sec + "\n");
		sb.append(section1Sb).append(section2Sb);
		
		// KK: asia 凯利离散值.
		sb.append("\nKK: Asia kelly dispersion [0.90, 0.94]").append("\n");
		for(Integer matchSeq : matchSeqs){
			String jobType = jobTypesOfB.get(matchSeq);
			if(StringUtils.isBlank(jobType)){
				continue;
			}
			// 先输出最近的, 后面才是以前的jobType.
			String preKey = matchSeq + "_" + jobType + "_" + "DIS";
			
			sb.append(matchSeq).append(" ")
			.append("H:").append(kellyDispersionMap.get(preKey + "_" + "H")).append(" ")
			.append("V:").append(kellyDispersionMap.get(preKey + "_" + "V")).append(" ")
			.append("\n");
			
			// 获取jobType后面的数字，用于计算变化情况;
			Integer jobTypeNum = Integer.valueOf(jobType.substring(1));
			int oldJobTypeNum = -1;
			while( ++oldJobTypeNum < jobTypeNum){
				String oldJobType = "B" + oldJobTypeNum;
				String preKeyOld = matchSeq + "_" + oldJobType + "_" + "DIS";
				if(kellyDispersionMap.get(preKeyOld + "_" + "H") == null){
					sb.append("   B(").append(oldJobTypeNum).append("):")
					.append("NA ");
				}else{
					sb.append("    B(").append(oldJobTypeNum).append("):")
					.append("H:").append(kellyDispersionMap.get(preKeyOld + "_" + "H")).append(" ")
					.append("V:").append(kellyDispersionMap.get(preKeyOld + "_" + "V")).append(" ");
				}
				sb.append("\n");
			}
		}
		
		// KJ: kelly < lossRatio 的公司数.
		
		// Kj: kelly > lossRatio 的公司数.
		
		// KK: kelly < 1 的个数.
		
		return sb;
	}
	
	private StringBuilder getEuroTransAsiaSb(List<Match> matches, Map<Integer, String> jobTypesOfA, Map<String, String> euroTransAsiaMap){
		StringBuilder sb = new StringBuilder("");
		sb.append("KP: Euro Trans Asia").append("\n");
		
		for(Match match : matches){
			Integer matchSeq = match.getMatchSeq();
			String jobType = jobTypesOfA.get(matchSeq);
			if(StringUtils.isBlank(jobType)){
				continue;
			}
			sb.append("* ").append(matchSeq);
			for(String oddsCorpName : OkConstant.CORP_EURO_TRANS_ASIA_NAME){
				// 先输出最近的, 后面才是以前的jobType.
				String corpPreKey = matchSeq + "_" + jobType + "_" + oddsCorpName + "_" + "C";
				String euroPreKey = matchSeq + "_" + jobType + "_" + oddsCorpName + "_" + "E";
				String transPreKey = matchSeq + "_" + jobType + "_" + oddsCorpName + "_" + "T";
				String asiaPreKey = matchSeq + "_" + jobType + "_" + oddsCorpName + "_" + "A";
				
				// 如果没有就不显示.
				if(euroTransAsiaMap.get(euroPreKey + "_" + "H") == null){
					continue;
				}
				
				// euro的H E V 中某一项与lossRatio差距超过指定值的时候才会显示。否则不显示.
				Float hostKellyEuroFloat = 0f;
				Float evenKellyEuroFloat = 0f;
				Float visitingKelyEuro = 0f;
				Float lossRatioEuro = 0f;
				String hostKellyEuroStr = euroTransAsiaMap.get(euroPreKey + "_" + "HK");
				String evenKellyEuroStr = euroTransAsiaMap.get(euroPreKey + "_" + "EK");
				String visitingKelyEuroStr = euroTransAsiaMap.get(euroPreKey + "_" + "VK");
				String lossRatioEuroStr = euroTransAsiaMap.get(euroPreKey + "_" + "L");
				if(!StringUtils.isBlank(hostKellyEuroStr) && !"NA".equalsIgnoreCase(hostKellyEuroStr)){
					hostKellyEuroFloat = Float.valueOf(hostKellyEuroStr);
				}
			    if(!StringUtils.isBlank(evenKellyEuroStr) && !"NA".equalsIgnoreCase(evenKellyEuroStr)){
			    	evenKellyEuroFloat = Float.valueOf(evenKellyEuroStr);
			    }
			    if(!StringUtils.isBlank(visitingKelyEuroStr) && !"NA".equalsIgnoreCase(visitingKelyEuroStr)){
			    	visitingKelyEuro = Float.valueOf(visitingKelyEuroStr);
			    }
			    if(!StringUtils.isBlank(lossRatioEuroStr) && !"NA".equalsIgnoreCase(lossRatioEuroStr)){
			    	lossRatioEuro = Float.valueOf(lossRatioEuroStr);
			    }
				if(oddsDiffLimited(hostKellyEuroFloat, evenKellyEuroFloat, visitingKelyEuro, lossRatioEuro)){
					continue;
				}
				
				sb.append(" ").append(euroTransAsiaMap.get(corpPreKey))
				.append(" E:")
//				.append(euroTransAsiaMap.get(euroPreKey + "_" + "H")).append(" ")
//				.append(euroTransAsiaMap.get(euroPreKey + "_" + "E")).append(" ")
//				.append(euroTransAsiaMap.get(euroPreKey + "_" + "V")).append(" ")
				.append(euroTransAsiaMap.get(euroPreKey + "_" + "L")).append(" ")
				.append(euroTransAsiaMap.get(euroPreKey + "_" + "HK")).append(" ")
				.append(euroTransAsiaMap.get(euroPreKey + "_" + "EK")).append(" ")
				.append(euroTransAsiaMap.get(euroPreKey + "_" + "VK")).append(" ");
				
				// 如果不存在亚盘，就不显示T 和 A.
				if(euroTransAsiaMap.get(asiaPreKey + "_" + "H") != null
						&& !"NA".equalsIgnoreCase(euroTransAsiaMap.get(asiaPreKey + "_" + "H"))){
					sb.append("T:")
					.append(euroTransAsiaMap.get(transPreKey + "_" + "H")).append(" ")
					.append(euroTransAsiaMap.get(transPreKey + "_" + "HC")).append(" ")
					.append(euroTransAsiaMap.get(transPreKey + "_" + "V")).append(" ")
					.append(euroTransAsiaMap.get(transPreKey + "_" + "T")).append(" ")
					.append("A:")
					.append(euroTransAsiaMap.get(asiaPreKey + "_" + "H")).append(" ")
					.append(euroTransAsiaMap.get(asiaPreKey + "_" + "HC")).append(" ")
					.append(euroTransAsiaMap.get(asiaPreKey + "_" + "V")).append(" ")
					.append(euroTransAsiaMap.get(asiaPreKey + "_" + "T")).append(" ")
					.append(euroTransAsiaMap.get(asiaPreKey + "_" + "L")).append(" ")
					.append(euroTransAsiaMap.get(asiaPreKey + "_" + "HK")).append(" ")
					.append(euroTransAsiaMap.get(asiaPreKey + "_" + "VK")).append(" ");
				}
				sb.append("\n");
			}
			
			// 获取jobType后面的数字，用于计算变化情况;
			/*
			Integer jobTypeNum = Integer.valueOf(jobType.substring(1));
			int oldJobTypeNum = -1;
			while( ++oldJobTypeNum < jobTypeNum){
				String oldJobType = "A" + oldJobTypeNum;
				sb.append("     A(").append(oldJobTypeNum).append("):");
				// 避免信息过大, 只有指定的公司才会显示历史.
				for(String oddsCorpName : OkConstant.CORP_EURO_TRANS_ASIA_NAME_HISTORY){
					String corpPreKeyOld = matchSeq + "_" + oldJobType + "_" + oddsCorpName + "_" + "C";
					String euroPreKeyOld = matchSeq + "_" + oldJobType + "_" + oddsCorpName + "_" + "E";
					String transPreKeyOld = matchSeq + "_" + oldJobType + "_" + oddsCorpName + "_" + "T";
					String asiaPreKeyOld = matchSeq + "_" + oldJobType + "_" + oddsCorpName + "_" + "A";
					if(euroTransAsiaMap.get(corpPreKeyOld) == null){
						continue;
					}else{
						sb.append(" ").append(euroTransAsiaMap.get(corpPreKeyOld))
						.append(" E:")
						.append(euroTransAsiaMap.get(euroPreKeyOld + "_" + "L")).append(" ")
						.append(euroTransAsiaMap.get(euroPreKeyOld + "_" + "HK")).append(" ")
						.append(euroTransAsiaMap.get(euroPreKeyOld + "_" + "EK")).append(" ")
						.append(euroTransAsiaMap.get(euroPreKeyOld + "_" + "VK")).append(" ")
						.append("T:")
						.append(euroTransAsiaMap.get(transPreKeyOld + "_" + "H")).append(" ")
						.append(euroTransAsiaMap.get(transPreKeyOld + "_" + "HC")).append(" ")
						.append(euroTransAsiaMap.get(transPreKeyOld + "_" + "V")).append(" ")
						.append(euroTransAsiaMap.get(transPreKeyOld + "_" + "T")).append(" ")
						.append("A:")
						.append(euroTransAsiaMap.get(asiaPreKeyOld + "_" + "H")).append(" ")
						.append(euroTransAsiaMap.get(asiaPreKeyOld + "_" + "HC")).append(" ")
						.append(euroTransAsiaMap.get(asiaPreKeyOld + "_" + "V")).append(" ")
						.append(euroTransAsiaMap.get(asiaPreKeyOld + "_" + "T")).append(" ")
						.append(euroTransAsiaMap.get(asiaPreKeyOld + "_" + "L")).append(" ")
						.append(euroTransAsiaMap.get(asiaPreKeyOld + "_" + "HK")).append(" ")
				        .append(euroTransAsiaMap.get(asiaPreKeyOld + "_" + "VK")).append(" ");
						sb.append("\n");
					}
				}
			}
			*/
			sb.append("\n");
		}
		
		sb.append("KQ: Init odds").append("\n");
		Map<String, String> oddsCorpNames = okJobService.queryCorpsNames();
		for(Match match : matches){
			Integer matchSeq = match.getMatchSeq();
			String initJobType = "I";
			sb.append(matchSeq);
			for(Integer oddsCorpNo : OkConstant.ODDS_EURO_ASIA_INIT){
				// 先输出最近的, 后面才是以前的jobType.
				String oddsCorpName = oddsCorpNames.get(String.valueOf(oddsCorpNo));
				String corpPreKey = matchSeq + "_" + initJobType + "_" + oddsCorpName + "_" + "C";
				String euroPreKey = matchSeq + "_" + initJobType + "_" + oddsCorpName + "_" + "E";
				String transPreKey = matchSeq + "_" + initJobType + "_" + oddsCorpName + "_" + "T";
				String asiaPreKey = matchSeq + "_" + initJobType + "_" + oddsCorpName + "_" + "A";
				
				// 如果没有就不显示.
				if(euroTransAsiaMap.get(euroPreKey + "_" + "H") == null){
					continue;
				}
				
				sb.append(" ").append(euroTransAsiaMap.get(corpPreKey))
				.append(" E:")
				.append(euroTransAsiaMap.get(euroPreKey + "_" + "H")).append(" ")
				.append(euroTransAsiaMap.get(euroPreKey + "_" + "E")).append(" ")
				.append(euroTransAsiaMap.get(euroPreKey + "_" + "V")).append(" ")
				.append(euroTransAsiaMap.get(euroPreKey + "_" + "L")).append(" ")
				.append(euroTransAsiaMap.get(euroPreKey + "_" + "HK")).append(" ")
				.append(euroTransAsiaMap.get(euroPreKey + "_" + "EK")).append(" ")
				.append(euroTransAsiaMap.get(euroPreKey + "_" + "VK")).append(" ");
				
				// 如果不存在亚盘，就不显示T 和 A.
				if(euroTransAsiaMap.get(asiaPreKey + "_" + "H") != null
						&& !"NA".equalsIgnoreCase(euroTransAsiaMap.get(asiaPreKey + "_" + "H"))){
					sb.append("T:")
					.append(euroTransAsiaMap.get(transPreKey + "_" + "H")).append(" ")
					.append(euroTransAsiaMap.get(transPreKey + "_" + "HC")).append(" ")
					.append(euroTransAsiaMap.get(transPreKey + "_" + "V")).append(" ")
					.append(euroTransAsiaMap.get(transPreKey + "_" + "T")).append(" ")
					.append("A:")
					.append(euroTransAsiaMap.get(asiaPreKey + "_" + "H")).append(" ")
					.append(euroTransAsiaMap.get(asiaPreKey + "_" + "HC")).append(" ")
					.append(euroTransAsiaMap.get(asiaPreKey + "_" + "V")).append(" ")
					.append(euroTransAsiaMap.get(asiaPreKey + "_" + "T")).append(" ");
				}
				sb.append("\n");
			}
			sb.append("\n");
		}
		return sb;
	}
	
	private StringBuilder getEuroOddsChangeDailySb(List<Match> matches, String okUrlDate, String matchName){
		StringBuilder sb = new StringBuilder("");
		sb.append("KR: Euro odds change").append("\n");
		
		List<Integer> matchSeqsInSql = new ArrayList<Integer>();
		matchSeqsInSql.add(0);
		for(Match match : matches){
			matchSeqsInSql.add(match.getMatchSeq());
		}
		
		EuropeOddsChange query = new EuropeOddsChange();
		query.setOkUrlDate(okUrlDate);
		query.setMatchSeqsInSql(matchSeqsInSql);
		query.setMaxOddsSeq(5);
		List<EuropeOddsChange> euroOddsChangeList = euroOddsChangeService.queryEuroOddsChangeDailySb(query);
		if(euroOddsChangeList == null){
			return sb;
		}
		
		// 查询市场平均概率
		ProbAverage probAverageQeury = new ProbAverage();
		probAverageQeury.setOkUrlDate(okUrlDate);
		probAverageQeury.setMatchSeqsInSql(matchSeqsInSql);
		List<ProbAverage> probAverageList = probAverageService.queryProbAverageBySeqs(probAverageQeury);
		Map<Integer, StringBuilder> probAverageMap = getProbAverageMap(probAverageList);
		
		// 查询LOT_EURO_CHANGE_DAILY_STATS 的分析结果
		Map<String, EuropeChangeDailyStats> changeStatsDailyMap = okJobService.getEuroChangeDailyStatsMap();
		
		// 已经处理过的matchSeq,需要做特殊处理.  list按照 matchSeq, oddsCorpName, oddsSeq 排好序.
		Set<String> processed = new HashSet<String>();
		Set<Integer> processedMatchSeqs = new HashSet<Integer>();
		for(EuropeOddsChange change : euroOddsChangeList){
			Integer matchSeq = change.getMatchSeq();
			String oddsCorpName = change.getOddsCorpName();
			String processedKey = matchSeq + "_" + oddsCorpName;
			if(!processedMatchSeqs.contains(matchSeq)){
				sb.append(probAverageMap.get(matchSeq));
				processedMatchSeqs.add(matchSeq);
			}
			
			if(!processed.contains(processedKey)){
				if(changeStatsDailyMap.containsKey("H" + "_" + oddsCorpName)
						|| changeStatsDailyMap.containsKey("HE" + "_" + oddsCorpName)
						|| changeStatsDailyMap.containsKey("V" + "_" + oddsCorpName)
						|| changeStatsDailyMap.containsKey("VE" + "_" + oddsCorpName)){
					sb.append("***");
				}
				sb.append(matchSeq).append(" ").append(oddsCorpName).append("   ");
				if(changeStatsDailyMap.containsKey("H" + "_" + oddsCorpName)){
					EuropeChangeDailyStats daily = changeStatsDailyMap.get("H" + "_" + oddsCorpName);
					sb.append("H:").append(daily.getRank()).append(" ")
					.append(daily.getTotalMatches()).append(" ")
					.append(daily.getProb()).append(" ");
				}
				if(changeStatsDailyMap.containsKey("HE" + "_" + oddsCorpName)){
					EuropeChangeDailyStats daily = changeStatsDailyMap.get("HE" + "_" + oddsCorpName);
					sb.append("HE:").append(daily.getRank()).append(" ")
					.append(daily.getTotalMatches()).append(" ")
					.append(daily.getProb()).append(" ");
				}
				if(changeStatsDailyMap.containsKey("V" + "_" + oddsCorpName)){
					EuropeChangeDailyStats daily = changeStatsDailyMap.get("V" + "_" + oddsCorpName);
					sb.append("V:").append(daily.getRank()).append(" ")
					.append(daily.getTotalMatches()).append(" ")
					.append(daily.getProb()).append(" ");
				}
				if(changeStatsDailyMap.containsKey("VE" + "_" + oddsCorpName)){
					EuropeChangeDailyStats daily = changeStatsDailyMap.get("VE" + "_" + oddsCorpName);
					sb.append("VE:").append(daily.getRank()).append(" ")
					.append(daily.getTotalMatches()).append(" ")
					.append(daily.getProb()).append(" ");
				}
				sb.append("\n");
				processed.add(processedKey);
			}
			Formatter formatter = new Formatter();
			formatter.format("%9s %5s %5s %5s %4s %4s %4s %4s\n", change.getTimeBeforeMatch(),
					change.getHostOdds(), change.getEvenOdds(), change.getVisitingOdds(),
					change.getLossRatio(), change.getHostKelly(), change.getEvenKelly(), change.getVisitingKelly());
			sb.append(formatter.toString());
			formatter.close();
		}
		return sb;
	}
	
	private StringBuilder getAsiaOddsChangeDailySb(List<Match> matches, String okUrlDate){
		StringBuilder sb = new StringBuilder("");
		sb.append("KS: Asia odds change").append("\n");
		
		List<Integer> matchSeqsInSql = new ArrayList<Integer>();
		matchSeqsInSql.add(0);
		for(Match match : matches){
			matchSeqsInSql.add(match.getMatchSeq());
		}
		
		AsiaOddsChange query = new AsiaOddsChange();
		query.setOkUrlDate(okUrlDate);
		query.setMatchSeqsInSql(matchSeqsInSql);
		List<AsiaOddsChange> asiaOddsChangeList = asiaOddsChangeService.queryAsiaOddsChangeDailySb(query);
		if(asiaOddsChangeList == null){
			return sb;
		}
		
		// 已经处理过的matchSeq,需要做特殊处理.  list按照 matchSeq, oddsCorpName, oddsSeq 排好序.
		Set<String> processed = new HashSet<String>();
		Set<String> toProcessCorps = new HashSet<String>();
		toProcessCorps.add("澳门彩票");
		toProcessCorps.add("利记(sbobet)");
		toProcessCorps.add("沙巴(IBCBET)");
		toProcessCorps.add("易胜博");
		toProcessCorps.add("Bovada");
		toProcessCorps.add("olimpkz");
		toProcessCorps.add("Yabet");
		toProcessCorps.add("TheGreek.com");
		toProcessCorps.add("UEDBET亚洲");
		toProcessCorps.add("24hPoker");
		for(AsiaOddsChange change : asiaOddsChangeList){
			Integer matchSeq = change.getMatchSeq();
			String oddsCorpName = change.getOddsCorpName();
			String processedKey = matchSeq + "_" + oddsCorpName;
			
			if(!toProcessCorps.contains(oddsCorpName)){
				continue;
			}
			
			if(!processed.contains(processedKey)){
				sb.append(matchSeq).append(" ").append(oddsCorpName).append("   ").append("\n");
				processed.add(processedKey);
			}
			Formatter formatter = new Formatter();
			formatter.format("%9s %5s %5s %5s %5s\n", change.getTimeBeforeMatch(), change.getOddsSeq(),
					change.getHostOdds(), change.getHandicap(), change.getVisitingOdds());
			sb.append(formatter.toString());
			formatter.close();
		}
		return sb;
	}
	
	private boolean oddsDiffLimited(Float hostKelly, Float evenKelly, Float visitingKelly, Float lossRatio){
		Float limitedDiff = 0.03f;
		if(Math.abs(hostKelly - lossRatio) > limitedDiff || Math.abs(evenKelly - lossRatio) > limitedDiff || Math.abs(visitingKelly - lossRatio) > limitedDiff){
			return false;
		}
		return true;
	}
	
	/**
	 * 从probAverageList构造 probAverageMap, key是matchSeq, value是各个时间段的胜平负的平均概率的StringBuilder.
	 * probAverageList已经按照 matchSeq, jobType 排好序了.
	 * 
	 * 这里先手工写死各个区间的值.
	 */
	private Map<Integer, StringBuilder> getProbAverageMap(List<ProbAverage> probAverageList){
		Map<Integer, StringBuilder> result = new HashMap<Integer, StringBuilder>();
		if(probAverageList == null || probAverageList.isEmpty()){
			return result;
		}
		Set<Integer> processed = new HashSet<Integer>();
		StringBuilder sb = null;
		for(ProbAverage probAverage : probAverageList){
			Integer matchSeq = probAverage.getMatchSeq();
			String jobType = probAverage.getJobType();
			Float hostProb = probAverage.getHostProb();
			Float evenProb = probAverage.getEvenProb();
			Float visitingProb = probAverage.getVisitingProb();
			if(!processed.contains(matchSeq)){
				sb = new StringBuilder("");
				sb.append(matchSeq).append(" ");
				sb.append(translateEJobInteval(jobType)).append("h ")
				.append("H:").append(hostProb).append(" ")
				.append("E:").append(evenProb).append(" ")
				.append("V:").append(visitingProb).append(" ")
				.append("\n");
				processed.add(matchSeq);
			}else{
				sb.append(translateEJobInteval(jobType)).append("h ")
				.append("H:").append(hostProb).append(" ")
				.append("E:").append(evenProb).append(" ")
				.append("V:").append(visitingProb).append(" ")
				.append("\n");
			}
			result.put(matchSeq, sb);
		}
		return result;
	}
	
	private Integer translateEJobInteval(String jobType){
		Integer result = null;
		if("E0".equals(jobType)){
			result = 80;
		}else if("E1".equals(jobType)){
			result = 60;
		}else if("E2".equals(jobType)){
			result = 40;
		}else if("E3".equals(jobType)){
			result = 20;
		}else if("E4".equals(jobType)){
			result = 15;
		}else if("E5".equals(jobType)){
			result = 11;
		}else if("E6".equals(jobType)){
			result = 7;
		}else if("E7".equals(jobType)){
			result = 5;
		}else if("E8".equals(jobType)){
			result = 3;
		}else if("E9".equals(jobType)){
			result = 1;
		}else{
			result = -99;
		}
		
		return result;
	}
	
	private Map<String, Map<String, Integer>> getKellyCountMap(String okUrlDate){
		List<KellyMatchCount> matchCounts = kellyMatchCountService.queryExistsMatchCountByDate(okUrlDate);
		Map<String, Map<String, Integer>> result = new HashMap<String, Map<String, Integer>>();
		if(matchCounts == null || matchCounts.isEmpty()){
			return result;
		}
		
		for(KellyMatchCount matchCount : matchCounts){
			String matchSeq = String.valueOf(matchCount.getMatchSeq());
			String jobType = matchCount.getJobType();
			String key = matchSeq + "_" + jobType;
			String ruleType = matchCount.getRuleType();
			Integer corpCount = matchCount.getCorpCount();
			
			Map<String, Integer> ruleMap = result.get(key);
			if(ruleMap == null){
				ruleMap = new HashMap<String, Integer>();
				ruleMap.put(ruleType, corpCount);
			}else{
				ruleMap.put(ruleType, corpCount);
			}
			
			result.put(key, ruleMap);
		}
		return result;
	}
	
	/**
	 * 初始化 LOT_CORP, 不包括 TIME_BEFORE_MATCH
	 */
	public void initCorp(String matchHtmlFilePath){
		// 获取现有的corpNo
		Map<String, String> allCorps = corpService.initialCorpMap();
		
		List<Corp> corpsList = new ArrayList<Corp>();
		List<File> euroOddsHtmls = null;
			File matchHtmlFile = new File(matchHtmlFilePath);
			euroOddsHtmls = OkParseUtils.getSameDirFilesFromMatch(
					matchHtmlFile,
					OkConstant.EURO_ODDS_FILE_NAME_BASE);
			String corpNo = "";
			String corpName = "";
			for (File euroOddsHtml : euroOddsHtmls) {
				LOGGER.info("process euroOddsHtml: " + euroOddsHtml.getAbsolutePath() + " ; size: " + corpsList.size());
				String okUrlDate = OkParseUtils.getOkUrlDateFromFile(euroOddsHtml);
				Integer matchSeq = OkParseUtils.getMatchSeqFromOddsFile(euroOddsHtml);
				List<EuropeOdds> europeOddsList = euroOddsService
						.getEuropeOddsFromFile(euroOddsHtml, 0, okUrlDate, matchSeq);
				if(europeOddsList == null || europeOddsList.isEmpty()){
					continue;
				}
				for(EuropeOdds odd : europeOddsList){
					corpNo = odd.getOddsCorpNo();
					corpName = odd.getOddsCorpName();
					if(!allCorps.containsKey(corpName)){
						Corp corp = new Corp();
						corp.setCorpNo(corpNo);
						corp.setCorpName(corpName);
						corp.setTimestamp(odd.getTimestamp());
						corpsList.add(corp);
					}
				}
				insertCorpsNo(corpsList);
			}
	}
	
	/**
	 * 更新 LOT_ODDS_EURO_CHANGE 中的CHANGE_NUM, 这个字段是后来添加的.
	 */
	public void updateEuroOddsChangeNum(){
		long allBegin = System.currentTimeMillis();
		Set<String> allCorpNames = euroOddsService.queryAllCorpNames();
		
		if(allCorpNames == null || allCorpNames.isEmpty()){
			LOGGER.info("allCorpNames is null");
			return;
		}
		LOGGER.info("allCorpNames size: " + allCorpNames.size());
		
		for(String corpName : allCorpNames){
			long begin = System.currentTimeMillis();
			List<EuropeOddsChange> changeList = euroOddsChangeService.queryChangeNumByCorp(corpName);
			if(changeList == null || changeList.isEmpty()){
				LOGGER.info("changeList is null, oddsCorpName: " + corpName);
				continue;
			}else{
				euroOddsChangeService.updateEuroOddsChangeNum(changeList);
				LOGGER.info(corpName + " eclipsed time: " + (System.currentTimeMillis() - begin)/1000 + " s.");
			}
		}
		LOGGER.info("total eclipsed time: " + (System.currentTimeMillis() - allBegin)/1000 + " s.");
	}
	
	/**
	 * 更新 LOT_CORP 中的 TIME_BEFORE_MATCH字段. 平均的
	 */
	public void calcuCorpAvgTimeBeforeMatch(){
		long begin = System.currentTimeMillis();
		
		// 查询所有的公司.
		List<Corp> allCorps = corpService.queryAllCorp();
		List<Corp> corpList = new ArrayList<Corp>();
		for(Corp corp : allCorps){
			long begin1 = System.currentTimeMillis();
			String oddsCorpName = corp.getCorpName();
			// 根据 oddsCorpName 查询 timeBeforeMatch
			List<EuropeOddsChange> oddsChangeList = euroOddsChangeService.queryChangeTimeBeforeByCorp(oddsCorpName);
			Double sum = 0d;
			for(EuropeOddsChange oddsChange : oddsChangeList){
				String timeBeforeMatchStr = oddsChange.getTimeBeforeMatch();
				double timeBeforeMatch = OkParseUtils.transTimeBeforeMatch(timeBeforeMatchStr);
				sum += timeBeforeMatch;
			}
			Double avgTimeBeforeMatch = sum / oddsChangeList.size();
			Corp corpForTime = new Corp();
			corpForTime.setCorpName(oddsCorpName);
			corpForTime.setEuroTimeBeforeMatch(avgTimeBeforeMatch);
			corpList.add(corpForTime);
			LOGGER.info(oddsCorpName + " eclipsed time: " + (System.currentTimeMillis() - begin1) + " ms.");
		}
		
		corpService.updateTimeBeforeMatchList(corpList);
		LOGGER.info("total eclipsed time: " + (System.currentTimeMillis() - begin) + " ms.");
	}
	
	public void initKellyCorpCount(){
		long begin = System.currentTimeMillis();
		List<KellyCorpResult> kellyResultList = kellyCorpResultService.queryAllMatchName();
		for(KellyCorpResult corpResult : kellyResultList){
			initKellyCorpCount(corpResult.getMatchName());
		}
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/(1000 * 60) + " min.");
	}
	/**
	 * 初始化 LOT_KELLY_CORP_COUNT
	 * 计算平均每场胜的比赛参与公司的平均数, 同时按照hostOdds进行分组计算, hostOdds为99家平均的.  分组为: ［1.00-1.30), [1.30-1.60), [1.60-1.90)
	 */
	public void initKellyCorpCount(String matchName){
		long begin1 = System.currentTimeMillis();
		// 查询所有的
		List<KellyCorpResult> kellyResultList = kellyCorpResultService.queryResultByMatchName(matchName, null);
		// key 是 hostOdds, value的Map: key 是 {okUrlDate}_{matchSeq}  value是出现的次数;
		Map<Float, Map<String, Integer>> k2Map = new HashMap<Float, Map<String, Integer>>();
		Map<Float, Map<String, Integer>> k3Map = new HashMap<Float, Map<String, Integer>>();
		for(KellyCorpResult kellyResult : kellyResultList){
			String ruleType = kellyResult.getRuleType();
			String winSeq = kellyResult.getWinSeq();
			String okUrlDate = kellyResult.getOkUrlDate();
			String[] winSeqArr = winSeq.split("\\|");
			for(String matchSeq : winSeqArr){
				if(StringUtils.isBlank(matchSeq)){
					continue;
				}
				EuropeOdds euroOdds = euroOddsService.queryEuropeOddsByOkId(okUrlDate, Integer.valueOf(matchSeq));
				if(euroOdds == null){
					continue;
				}
				Float hostOdds = groupHostOdds(euroOdds.getHostOdds());
				String countKey = okUrlDate + "_" + matchSeq;
				if("K2".equals(ruleType)){
					Map<String, Integer> countMap = k2Map.get(hostOdds);
					if(countMap == null){
						countMap = new HashMap<String, Integer>();
						countMap.put(countKey, 1);
					}else{
						// 改变次数;
						Integer count = countMap.get(countKey);
						if(count == null){
							countMap.put(countKey, 1);
						}else{
							countMap.put(countKey, count + 1);
						}
					}
					k2Map.put(hostOdds, countMap);
					
				}else if("K3".equals(ruleType)){
					Map<String, Integer> countMap = k3Map.get(hostOdds);
					if(countMap == null){
						countMap = new HashMap<String, Integer>();
						countMap.put(countKey, 1);
					}else{
						// 改变次数;
						Integer count = countMap.get(countKey);
						if(count == null){
							countMap.put(countKey, 1);
						}else{
							countMap.put(countKey, count + 1);
						}
					}
					k3Map.put(hostOdds, countMap);
				}
			}
		}
		
		List<KellyCorpCount> corpCountList = new ArrayList<KellyCorpCount>();
		Set<Float> k2HostOddsSet = k2Map.keySet();
		for(Float hostOdds : k2HostOddsSet){
			Map<String, Integer> countMap = k2Map.get(hostOdds);
			int sum = 0;
			for(Entry<String, Integer> entry : countMap.entrySet()){
				sum += entry.getValue();
			}
			KellyCorpCount corpCount = new KellyCorpCount();
			corpCount.setMatchName(matchName);
			corpCount.setBeginHostOdds(hostOdds - 0.3);
			corpCount.setEndHostOdds(Double.valueOf(hostOdds));
			corpCount.setRuleType("K2");
			corpCount.setCorpCountWin(sum/countMap.size());
			corpCount.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			corpCountList.add(corpCount);
		}
		
		Set<Float> k3HostOddsSet = k3Map.keySet();
		for(Float hostOdds : k3HostOddsSet){
			Map<String, Integer> countMap = k3Map.get(hostOdds);
			int sum = 0;
			for(Entry<String, Integer> entry : countMap.entrySet()){
				sum += entry.getValue();
			}
			KellyCorpCount corpCount = new KellyCorpCount();
			corpCount.setMatchName(matchName);
			corpCount.setBeginHostOdds(hostOdds - 0.3);
			corpCount.setEndHostOdds(Double.valueOf(hostOdds));
			corpCount.setRuleType("K3");
			corpCount.setCorpCountWin(sum/countMap.size());
			corpCount.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			corpCountList.add(corpCount);
		}
		
		// 先删除
		kellyCorpCountService.deleteCorpCountByMatchName(matchName);
		kellyCorpCountService.insertList(corpCountList);
		LOGGER.info(matchName + " eclipsed time: " + (System.currentTimeMillis() - begin1)/(1000 * 60) + " min.");
	}
	
	private Float groupHostOdds(Float hostOdds){
		if(hostOdds >= 1.00 && hostOdds < 1.30){
			return 1.30f;
		}else if(hostOdds >=1.30 && hostOdds < 1.60){
			return 1.60f;
		}else if(hostOdds >=1.60 && hostOdds < 1.90){
			return 1.90f;
		}else if(hostOdds >=1.90 && hostOdds < 2.20){
			return 2.20f;
		}else if(hostOdds >=2.20 && hostOdds < 2.50){
			return 2.50f;
		}else if(hostOdds >=2.50 && hostOdds < 2.80){
			return 2.80f;
		}else if(hostOdds >=2.80 && hostOdds < 3.20){
			return 3.20f;
		}else if(hostOdds >=3.20 && hostOdds < 3.50){
			return 3.50f;
		}else if(hostOdds >= 3.50 && hostOdds < 3.80){
			return 3.80f;
		}else if(hostOdds >= 3.80 && hostOdds < 4.10){
			return 4.10f;
		}else if(hostOdds >= 4.10 && hostOdds < 4.40){
			return 4.40f;
		}else if(hostOdds >= 4.40 && hostOdds < 4.70){
			return 4.70f;
		}else if(hostOdds >= 4.70 && hostOdds < 5.00){
			return 5.00f;
		}
		
		return 5.30f;
	}
	
	/**
	 * 计算所有公司的K2, K3, K4 分析方法中主胜次数的分布情况.
	 */
	public void showKellyCountProb(){
		long begin = System.currentTimeMillis();
		List<KellyCorpResult> kellyResultList = kellyCorpResultService.queryAllMatchName();
		for(KellyCorpResult corpResult : kellyResultList){
			showKellyCountProb(corpResult.getMatchName(), null, null);
		}
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/(1000 * 60) + " min.");
	}
	
	/**
	 * 计算各个ruleType主胜的ALL_SEQ 分布情况
	 * @param matchName
	 * @param map 各场比赛中 K2,K3,K4 的结果, 用于 K3 && K4的预测;
	 */
	public String showKellyCountProb(String matchName, Map<String, Map<String, Integer>> matchSeqCountMap, String currOkUrlDate){
		long begin = System.currentTimeMillis();
		String result = "";
		List<KellyCorpResult> corpResultList = kellyCorpResultService.queryResultByMatchName(matchName, currOkUrlDate);
		// key 是 ruleType, value Map: key 是 {okUrlDate}_{matchSeq}, value 是出现次数.
		Map<String, Map<String, Integer>> ruleTypeMap = new HashMap<String, Map<String, Integer>>();
		Set<String> winSet = new HashSet<String>();
		Set<String> evenSet = new HashSet<String>();
		Set<String> negaSet = new HashSet<String>();
		for(KellyCorpResult corpResult : corpResultList){
			String ruleType = corpResult.getRuleType();
			String okUrlDate = corpResult.getOkUrlDate();
			String allSeq = corpResult.getAllSeq();
			String winSeq = corpResult.getWinSeq();
			String evenSeq = corpResult.getEvenSeq();
			String negaSeq = corpResult.getNegaSeq();
			String[] allSeqArr = allSeq.split("\\|");
			String[] winSeqArr = winSeq.split("\\|");
			String[] evenSeqArr = evenSeq.split("\\|");
			String[] negaSeqArr = negaSeq.split("\\|");
			for(String seq : winSeqArr){
				if(StringUtils.isBlank(seq)){
					continue;
				}
				winSet.add(okUrlDate + "_" + seq);
			}
			for(String seq : evenSeqArr){
				if(StringUtils.isBlank(seq)){
					continue;
				}
				evenSet.add(okUrlDate + "_" + seq);
			}
			for(String seq : negaSeqArr){
				if(StringUtils.isBlank(seq)){
					continue;
				}
				negaSet.add(okUrlDate + "_" + seq);
			}
			
			Map<String, Integer> countMap = ruleTypeMap.get(ruleType);
			for(String seq : allSeqArr){
				if(StringUtils.isBlank(seq)){
					continue;
				}
				String key = okUrlDate + "_" + seq;
				if(countMap == null){
					countMap = new HashMap<String, Integer>();
					countMap.put(key, 1);
				}else{
					Integer count = countMap.get(key);
					if(count == null){
						countMap.put(key, 1);
					}else{
						countMap.put(key, count + 1);
					}
				}
			}
			ruleTypeMap.put(ruleType, countMap);
		}
		
		/*  去掉 K2的分区间统计.
		Map<String, Integer> k2Map = ruleTypeMap.get("K2");
		Map<Integer, Map<String, Integer>> k2SummaryMap = new HashMap<Integer, Map<String, Integer>>();
		initialCountMap(k2SummaryMap);
		if(k2Map != null){
			for(Entry<String, Integer> entry : k2Map.entrySet()){
				Integer count = groupCount(entry.getValue());
				String key = entry.getKey();
				
				Map<String, Integer> k2CountMap = k2SummaryMap.get(count);
				Integer allCount = k2CountMap.get("all") + 1;
				k2CountMap.put("all", allCount);
				if(winSet.contains(key)){
					k2CountMap.put("win", k2CountMap.get("win") + 1);
				}else if(evenSet.contains(key)){
					k2CountMap.put("even", k2CountMap.get("even") + 1);
				}else if(negaSet.contains(key)){
					k2CountMap.put("nega", k2CountMap.get("nega") + 1);
				}			
				k2SummaryMap.put(count, k2CountMap);
			}
			
			Set<Integer> k2Set = k2SummaryMap.keySet();
			List<Integer> k2List = new ArrayList<Integer>();
			k2List.addAll(k2Set);
			Collections.sort(k2List);
			StringBuilder k2Sb = new StringBuilder("\nK2: " + matchName + "\n");
			for(Integer count : k2List){
				Map<String, Integer> k2CountMap = k2SummaryMap.get(count);
				if(k2CountMap.get("all") > 0){
					k2Sb.append(count).append(" ").append("W: ").append(k2CountMap.get("win")).append(" E: ")
					.append(k2CountMap.get("even")).append(" N: ").append(k2CountMap.get("nega"))
					.append(" WP: ").append(String.format("%.3f", Double.valueOf(k2CountMap.get("win"))/k2CountMap.get("all"))).append("\n");
				}
			}
			LOGGER.info(k2Sb.toString());
			result += k2Sb.toString();
		}
		*/
		
		/* 去掉K3, K4的分区间统计，保留 K3 && K4.
		Map<String, Integer> k3Map = ruleTypeMap.get("K3");
		Map<Integer, Map<String, Integer>> k3SummaryMap = new HashMap<Integer, Map<String, Integer>>();
		initialCountMap(k3SummaryMap);
		if(k3Map != null){
			for(Entry<String, Integer> entry : k3Map.entrySet()){
				Integer count = groupCount(entry.getValue());
				String key = entry.getKey();
				
				Map<String, Integer> k3CountMap = k3SummaryMap.get(count);
				Integer allCount = k3CountMap.get("all") + 1;
				k3CountMap.put("all", allCount);
				if(winSet.contains(key)){
					k3CountMap.put("win", k3CountMap.get("win") + 1);
				}else if(evenSet.contains(key)){
					k3CountMap.put("even", k3CountMap.get("even") + 1);
				}else if(negaSet.contains(key)){
					k3CountMap.put("nega", k3CountMap.get("nega") + 1);
				}
				k3SummaryMap.put(count, k3CountMap);
			}
			
			Set<Integer> k3Set = k3SummaryMap.keySet();
			List<Integer> k3List = new ArrayList<Integer>();
			k3List.addAll(k3Set);
			Collections.sort(k3List);
			StringBuilder k3Sb = new StringBuilder("\nK3: " + matchName + "\n");
			for(Integer count : k3List){
				Map<String, Integer> k3CountMap = k3SummaryMap.get(count);
				if(k3CountMap.get("all") > 0){
					k3Sb.append(count).append(" ").append("W: ").append(k3CountMap.get("win")).append(" E: ")
					.append(k3CountMap.get("even")).append(" N: ").append(k3CountMap.get("nega"))
					.append(" WP: ").append(String.format("%.3f", Double.valueOf(k3CountMap.get("win"))/k3CountMap.get("all"))).append("\n");
				}
			}
			LOGGER.info(k3Sb.toString());
			result += k3Sb.toString();
		}
		
		Map<String, Integer> k4Map = ruleTypeMap.get("K4");
		Map<Integer, Map<String, Integer>> k4SummaryMap = new HashMap<Integer, Map<String, Integer>>();
		initialCountMap(k4SummaryMap);
		if(k4Map != null){
			for(Entry<String, Integer> entry : k4Map.entrySet()){
				Integer count = groupCount(entry.getValue());
				String key = entry.getKey();
				
				Map<String, Integer> k4CountMap = k4SummaryMap.get(count);
				Integer allCount = k4CountMap.get("all") + 1;
				k4CountMap.put("all", allCount);
				if(winSet.contains(key)){
					k4CountMap.put("win", k4CountMap.get("win") + 1);
				}else if(evenSet.contains(key)){
					k4CountMap.put("even", k4CountMap.get("even") + 1);
				}else if(negaSet.contains(key)){
					k4CountMap.put("nega", k4CountMap.get("nega") + 1);
				}			
				k4SummaryMap.put(count, k4CountMap);
			}
			
			Set<Integer> k4Set = k4SummaryMap.keySet();
			List<Integer> k4List = new ArrayList<Integer>();
			k4List.addAll(k4Set);
			Collections.sort(k4List);
			StringBuilder k4Sb = new StringBuilder("\nK4: " + matchName + "\n");
			for(Integer count : k4List){
				Map<String, Integer> k4CountMap = k4SummaryMap.get(count);
				if(k4CountMap.get("all") > 0){
					k4Sb.append(count).append(" ").append("W: ").append(k4CountMap.get("win")).append(" E: ")
					.append(k4CountMap.get("even")).append(" N: ").append(k4CountMap.get("nega"))
					.append(" WP: ").append(String.format("%.3f", Double.valueOf(k4CountMap.get("win"))/k4CountMap.get("all")))
					.append(" NP: ").append(String.format("%.3f", Double.valueOf(k4CountMap.get("nega"))/k4CountMap.get("all"))).append("\n");
				}
			}
			LOGGER.info(k4Sb.toString());
			result += k4Sb.toString();
		}
		*/
		
		/* 去掉 K3 && K4 结合预测, 相当于该方法不再使用了.
		// K3 && K4 结合预测方法;
		Map<String, Set<String>> resultMap = new HashMap<String, Set<String>>();
		resultMap.put("win", winSet);
		resultMap.put("even", evenSet);
		resultMap.put("nega", negaSet);
		result += k3k4Analyse(matchSeqCountMap, ruleTypeMap, resultMap, matchName);
		*/

		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/1000 + " s.");
		return result;
	}
	
	private void insertCorpsNo(List<Corp> corps){
		corpService.insertList(corps);
	}
	
	/**
	 * 比分赔率.
	 * 
	 */
	public void analyseScoreOdds(String okUrlDate, List<Integer> matchSeqs) {
		// 查询比分赔率表.
		List<ScoreOdds> scoreOddsList = scoreOddsService.queryScoreOddsByOkUrlDate(okUrlDate);
		// key: {matchSeq_intervalType_win}  value: key: 1:0 value: 9.83
		Map<String, Map<String, String>> intervalTypeMap = new HashMap<String, Map<String, String>>();
		for(ScoreOdds scoreOdds : scoreOddsList){
			Integer matchSeq = scoreOdds.getMatchSeq();
			String intervalType = scoreOdds.getIntervalType();
			String winOdds = scoreOdds.getWinOdds();
			String evenOdds = scoreOdds.getEvenOdds();
			String negaOdds = scoreOdds.getNegaOdds();
			String[] winOddsArr = winOdds.split("\\|");
			Map<String, String> winOddsMap = new HashMap<String, String>();
			Map<String, String> evenOddsMap = new HashMap<String, String>();
			Map<String, String> negaOddsMap = new HashMap<String, String>();
			for(String oneWinOdds : winOddsArr){
				if(winOddsArr.length < 2){
					break;
				}
				String winScore = oneWinOdds.split("\\,")[0];
				String winOddsStr = oneWinOdds.split("\\,")[1];
				winOddsMap.put(winScore, winOddsStr);
			}
			
			String[] evenOddsArr = evenOdds.split("\\|");
			for(String oneEvenOdds : evenOddsArr){
				if(evenOddsArr.length < 2){
					break;
				}
				String evenScore = oneEvenOdds.split("\\,")[0];
				String evenOddsStr = oneEvenOdds.split("\\,")[1];
				evenOddsMap.put(evenScore, evenOddsStr);
			}
			
			String[] negaOddsArr = negaOdds.split("\\|");
			for(String oneNegaOdds : negaOddsArr){
				if(negaOddsArr.length < 2){
					break;
				}
				String negaScore = oneNegaOdds.split("\\,")[0];
				String negaOddsStr = oneNegaOdds.split("\\,")[1];
				negaOddsMap.put(negaScore, negaOddsStr);
			}
			String winKey = String.valueOf(matchSeq) + "_" + intervalType + "_win";
			String evenKey = String.valueOf(matchSeq) + "_" + intervalType + "_even";
			String negaKey = String.valueOf(matchSeq) + "_" + intervalType + "_nega";
			intervalTypeMap.put(winKey , winOddsMap);
			intervalTypeMap.put(evenKey , evenOddsMap);
			intervalTypeMap.put(negaKey , negaOddsMap);
		}
		
		StringBuilder sb = new StringBuilder();
		for(int matchSeq : matchSeqs){
			sb.append("\n" + matchSeq + "\n");
			String winB0Key = String.valueOf(matchSeq) + "_" + "B0" + "_win";
			String winB1Key = String.valueOf(matchSeq) + "_" + "B1" + "_win";
			String winB2Key = String.valueOf(matchSeq) + "_" + "B2" + "_win";
			String winB3Key = String.valueOf(matchSeq) + "_" + "B3" + "_win";
			String evenB0Key = String.valueOf(matchSeq) + "_" + "B0" + "_even";
			String evenB1Key = String.valueOf(matchSeq) + "_" + "B1" + "_even";
			String evenB2Key = String.valueOf(matchSeq) + "_" + "B2" + "_even";
			String evenB3Key = String.valueOf(matchSeq) + "_" + "B3" + "_even";
			String negaB0Key = String.valueOf(matchSeq) + "_" + "B0" + "_nega";
			String negaB1Key = String.valueOf(matchSeq) + "_" + "B1" + "_nega";
			String negaB2Key = String.valueOf(matchSeq) + "_" + "B2" + "_nega";
			String negaB3Key = String.valueOf(matchSeq) + "_" + "B3" + "_nega";
			
			sb.append("win:\n");
			Map<String, String> winB0Map = intervalTypeMap.get(winB0Key) == null ? new HashMap<String, String>() : intervalTypeMap.get(winB0Key);
			Map<String, String> winB1Map = intervalTypeMap.get(winB1Key) == null ? new HashMap<String, String>() : intervalTypeMap.get(winB1Key);
			Map<String, String> winB2Map = intervalTypeMap.get(winB2Key) == null ? new HashMap<String, String>() : intervalTypeMap.get(winB2Key);
			Map<String, String> winB3Map = intervalTypeMap.get(winB3Key) == null ? new HashMap<String, String>() : intervalTypeMap.get(winB3Key);
			//["1:0", "2:0", "3:0"]
			Set<String> winKeySet = new HashSet<String>();
			winKeySet.addAll(winB0Map.keySet());
			winKeySet.addAll(winB1Map.keySet());
			winKeySet.addAll(winB2Map.keySet());
			winKeySet.addAll(winB3Map.keySet());
			for(String key : winKeySet){
				sb.append(key).append(" B0:").append(winB0Map.get(key))
				.append("  B1:").append(winB1Map.get(key))
				.append("  B2:").append(winB2Map.get(key))
				.append("  B3:").append(winB3Map.get(key))
				.append("\n");
			}
			
			sb.append("even:\n");
			Map<String, String> evenB0Map = intervalTypeMap.get(evenB0Key) == null ? new HashMap<String, String>() : intervalTypeMap.get(evenB0Key);
			Map<String, String> evenB1Map = intervalTypeMap.get(evenB1Key) == null ? new HashMap<String, String>() : intervalTypeMap.get(evenB1Key);
			Map<String, String> evenB2Map = intervalTypeMap.get(evenB2Key) == null ? new HashMap<String, String>() : intervalTypeMap.get(evenB2Key);
			Map<String, String> evenB3Map = intervalTypeMap.get(evenB3Key) == null ? new HashMap<String, String>() : intervalTypeMap.get(evenB3Key);
			//["1:0", "2:0", "3:0"]
			Set<String> evenKeySet = new HashSet<String>();
			evenKeySet.addAll(evenB0Map.keySet());
			evenKeySet.addAll(evenB1Map.keySet());
			evenKeySet.addAll(evenB2Map.keySet());
			evenKeySet.addAll(evenB3Map.keySet());
			for(String key : evenKeySet){
				sb.append(key).append(" B0:").append(evenB0Map.get(key))
				.append("  B1:").append(evenB1Map.get(key))
				.append("  B2:").append(evenB2Map.get(key))
				.append("  B3:").append(evenB3Map.get(key))
				.append("\n");
			}
			
			sb.append("nega:\n");
			Map<String, String> negaB0Map = intervalTypeMap.get(negaB0Key) == null ? new HashMap<String, String>() : intervalTypeMap.get(negaB0Key);
			Map<String, String> negaB1Map = intervalTypeMap.get(negaB1Key) == null ? new HashMap<String, String>() : intervalTypeMap.get(negaB1Key);
			Map<String, String> negaB2Map = intervalTypeMap.get(negaB2Key) == null ? new HashMap<String, String>() : intervalTypeMap.get(negaB2Key);
			Map<String, String> negaB3Map = intervalTypeMap.get(negaB3Key) == null ? new HashMap<String, String>() : intervalTypeMap.get(negaB3Key);
			//["1:0", "2:0", "3:0"]
			Set<String> negaKeySet = new HashSet<String>();
			negaKeySet.addAll(negaB0Map.keySet());
			negaKeySet.addAll(negaB1Map.keySet());
			negaKeySet.addAll(negaB2Map.keySet());
			negaKeySet.addAll(negaB3Map.keySet());
			for(String key : negaKeySet){
				sb.append(key).append(" B0:").append(negaB0Map.get(key))
				.append("  B1:").append(negaB1Map.get(key))
				.append("  B2:").append(negaB2Map.get(key))
				.append("  B3:").append(negaB3Map.get(key))
				.append("\n");
			}
		}
		
		LOGGER.info(sb.toString());
	}
	
	private Map<String, List<Match>> getMatchesMapByMatchName(File matchHtmlFile, Map<Integer, String> jobTypes){
		ArrayList<Integer> sortedMatchSeqs = new ArrayList<Integer>();
		sortedMatchSeqs.addAll(jobTypes.keySet());
		Collections.sort(sortedMatchSeqs);
		List<Match> matches = singleMatchService.getAllMatchFromFile(matchHtmlFile, sortedMatchSeqs.get(0), sortedMatchSeqs.get(sortedMatchSeqs.size()-1));
		
		Map<String, List<Match>> result = null;
		if(matches != null){
			result = new HashMap<String, List<Match>>();
			for(Match match : matches){
				// 获取99家平均的赔率，最近的.
				String eurOddsChangeHtmlPath = matchHtmlFile.getAbsolutePath().replace("match.html", "euroOddsChange_24_" + match.getMatchSeq() + ".html");
				File eurOddsChangeHtml = new File(eurOddsChangeHtmlPath);
				if(eurOddsChangeHtml.exists()){
					List<EuropeOddsChange> euroOddsChangeList = euroOddsChangeService.getEuroOddsChangeFromFile(eurOddsChangeHtml, 1, false);
					if(euroOddsChangeList != null && !euroOddsChangeList.isEmpty()){
						EuropeOddsChange europeOddsChange = euroOddsChangeList.get(0);
						match.setHostOdds(europeOddsChange.getHostOdds());
						match.setEvenOdds(europeOddsChange.getEvenOdds());
						match.setVisitingOdds(europeOddsChange.getVisitingOdds());
					}
				}
				
				String matchName = match.getMatchName();
				List<Match> list = result.get(matchName);
				if(list == null){
					list = new ArrayList<Match>();
					list.add(match);
					result.put(matchName, list);
				}else{
					list.add(match);
					result.put(matchName, list);
				}
			}
		}
		return result;
	}
	
	/**
	 * 根据okUrlDate, beginMatchSeq, endMatchSeq 查询 LOT_ODDS_ASIA_TRENDS, 并按照matchName分组，放入map中.
	 * @param okUrlDate
	 * @param beginMatchSeq
	 * @param endMatchSeq
	 * @return
	 */
	private Map<String, List<AsiaOddsTrends>> getAsiaTrendsMap(String okUrlDate, int beginMatchSeq, int endMatchSeq){
		Map<String, List<AsiaOddsTrends>> result = new HashMap<String, List<AsiaOddsTrends>>();
		AsiaOddsTrends queryTrends = new AsiaOddsTrends();
		queryTrends.setOkUrlDate(okUrlDate);
		queryTrends.setBeginMatchSeq(beginMatchSeq);
		queryTrends.setEndMatchSeq(endMatchSeq);
		List<AsiaOddsTrends> asiaOddsTrendsList = asiaOddsTrendsService.queryAsiaTrendsByRange(queryTrends);
		if(asiaOddsTrendsList == null || asiaOddsTrendsList.isEmpty()){
			return result;
		}
		
		for(AsiaOddsTrends trends : asiaOddsTrendsList){
			String matchName = trends.getMatchName();
			List<AsiaOddsTrends> list = result.get(matchName);
			if(list == null){
				list = new ArrayList<AsiaOddsTrends>();
				list.add(trends);
				result.put(matchName, list);
			}else{
				list.add(trends);
				result.put(matchName, list);
			}
		}
		return result;
	}
	
	/**
	 * 获取okooo指数，kelly指数离散度.
	 * @param okUrlDate
	 * @param beginMatchSeq
	 * @param endMatchSeq
	 * @return
	 */
	private Map<String, Float> getKellyStdDevMap(String okUrlDate, int beginMatchSeq, int endMatchSeq){
		Map<String, Float> result = new HashMap<String, Float>();
		IndexStats queryIndexStats = new IndexStats();
		queryIndexStats.setOkUrlDate(okUrlDate);
		queryIndexStats.setBeginMatchSeq(beginMatchSeq);
		queryIndexStats.setEndMatchSeq(endMatchSeq);
		List<IndexStats> indexStatsList = indexStatsService.queryIndexStatsByRange(queryIndexStats);
		if(indexStatsList == null || indexStatsList.isEmpty()){
			return result;
		}
		
		for(IndexStats indexStats : indexStatsList){
			Integer matchSeq = indexStats.getMatchSeq();
			String jobType = indexStats.getJobType();
			String preKey = matchSeq + "_" + jobType;
			result.put(preKey + "_" + "IH", indexStats.getInitStdDevHost());
			result.put(preKey + "_" + "IE", indexStats.getInitStdDevEven());
			result.put(preKey + "_" + "IV", indexStats.getInitStdDevVisiting());
			result.put(preKey + "_" + "H", indexStats.getStdDevHost());
			result.put(preKey + "_" + "E", indexStats.getStdDevEven());
			result.put(preKey + "_" + "V", indexStats.getStdDevVisiting());
		}
		return result;
	}
	
	/**
	 * 获取让球(euroOddsHandicap)信息, 增加 asiaOddsTrends(KK)的值.
	 * @param okUrlDate
	 * @param beginMatchSeq
	 * @param endMatchSeq
	 * @return
	 */
	private Map<String, String> getEuroHandicapMap(String okUrlDate, int beginMatchSeq, int endMatchSeq){
		Map<String, String> result = new HashMap<String, String>();
		List<KellyMatchCount> matchCounts = kellyMatchCountService.queryMatchCountByDateJobFlag(okUrlDate, "D");
		if(matchCounts == null || matchCounts.isEmpty()){
			return result;
		}
		
		for(KellyMatchCount matchCount : matchCounts){
			Integer matchSeq = matchCount.getMatchSeq();
			String jobType = matchCount.getJobType();
			String avgStr = matchCount.getExtend1();
			String varStr = matchCount.getExtend2();
			if(StringUtils.isBlank(avgStr) || StringUtils.isBlank(varStr)){
				continue;
			}
			String avgPreKey = matchSeq + "_" + jobType + "_" + "AVG";
			String varPreKey = matchSeq + "_" + jobType + "_" + "DIS";
			String avgHandicap = avgStr.split(":")[0];
			String varHandicap = varStr.split(":")[0];
			String avgVal = avgStr.split(":")[1];
			String varVal = varStr.split(":")[1];
			result.put(avgPreKey + "_" + "HC", avgHandicap);
			result.put(avgPreKey + "_" + "H", avgVal.split("\\|")[0]);
			result.put(avgPreKey + "_" + "E", avgVal.split("\\|")[1]);
			result.put(avgPreKey + "_" + "V", avgVal.split("\\|")[2]);
			result.put(varPreKey + "_" + "HC", varHandicap);
			result.put(varPreKey + "_" + "H", varVal.split("\\|")[0]);
			result.put(varPreKey + "_" + "E", varVal.split("\\|")[1]);
			result.put(varPreKey + "_" + "V", varVal.split("\\|")[2]);
		}
		
		// 增加 asiaOddsTrends(KK)的值.
		List<KellyMatchCount> matchCountsOfAsia = kellyMatchCountService.queryMatchCountByDateJobFlag(okUrlDate, "B");
		if(matchCountsOfAsia == null || matchCountsOfAsia.isEmpty()){
			return result;
		}
		for(KellyMatchCount matchCount : matchCountsOfAsia){
			Integer matchSeq = matchCount.getMatchSeq();
			String jobType = matchCount.getJobType();
			String avgStr = matchCount.getExtend1();
			String varStr = matchCount.getExtend2();
			if(StringUtils.isBlank(avgStr) || StringUtils.isBlank(varStr)){
				continue;
			}
			String avgPreKey = matchSeq + "_" + jobType + "_" + "AVG";
			String varPreKey = matchSeq + "_" + jobType + "_" + "DIS";
			result.put(avgPreKey + "_" + "H", avgStr.split("\\|")[0]);
			result.put(avgPreKey + "_" + "V", avgStr.split("\\|")[1]);
			result.put(varPreKey + "_" + "H", varStr.split("\\|")[0]);
			result.put(varPreKey + "_" + "V", varStr.split("\\|")[1]);
		}
		
		// 增加 KJ的值: 按照lossRatio分区间计算kelly的离散度.
		List<KellyMatchCount> matchCountsOfKj = kellyMatchCountService.queryMatchCountByDateJobFlagRule(okUrlDate, "A", "KJ");
		if(matchCountsOfKj == null || matchCountsOfKj.isEmpty()){
			return result;
		}
		for(KellyMatchCount matchCount : matchCountsOfKj){
			Integer matchSeq = matchCount.getMatchSeq();
			String jobType = matchCount.getJobType();
			String section1Str = matchCount.getExtend1();
			String section2Str = matchCount.getExtend2();
			if(StringUtils.isBlank(section1Str) || StringUtils.isBlank(section2Str)){
				continue;
			}
			String section1PreKey = matchSeq + "_" + jobType + "_" + "SEC1";
			String section2PreKey = matchSeq + "_" + jobType + "_" + "SEC2";
			// 区间
			result.put(section1PreKey + "_" + "S", section1Str.split(":")[0]);
			// 个数.
			result.put(section1PreKey + "_" + "N", section1Str.split(":")[1].split("\\|")[0].split(",")[0]);
			// HEV的离散度.
			result.put(section1PreKey + "_" + "H", section1Str.split(":")[1].split("\\|")[0].split(",")[1]);
			result.put(section1PreKey + "_" + "E", section1Str.split(":")[1].split("\\|")[1].split(",")[1]);
			result.put(section1PreKey + "_" + "V", section1Str.split(":")[1].split("\\|")[2].split(",")[1]);
			
			result.put(section2PreKey + "_" + "S", section2Str.split(":")[0]);
			result.put(section2PreKey + "_" + "N", section2Str.split(":")[1].split("\\|")[0].split(",")[0]);
			result.put(section2PreKey + "_" + "H", section2Str.split(":")[1].split("\\|")[0].split(",")[1]);
			result.put(section2PreKey + "_" + "E", section2Str.split(":")[1].split("\\|")[1].split(",")[1]);
			result.put(section2PreKey + "_" + "V", section2Str.split(":")[1].split("\\|")[2].split(",")[1]);
		}
		return result;
	}
	
	/**
	 * 构造欧亚转换map. key: {matchSeq}_{jobType}_E_H; {matchSeq}_{jobType}_T_H; {matchSeq}_{jobType}_A_H;
	 * @param okUrlDate
	 * @param beginMatchSeq
	 * @param endMatchSeq
	 * @return
	 */
	private Map<String, String> getEuroTransAsiaMap(String okUrlDate, int beginMatchSeq, int endMatchSeq){
		Map<String, String> result = new HashMap<String, String>();
		List<EuroTransAsia> euroTransAsiaList = euroTransAsiaService.queryEuroTransAsiaByOkUrlDate(okUrlDate, beginMatchSeq, endMatchSeq);
		if(euroTransAsiaList == null || euroTransAsiaList.isEmpty()){
			return result;
		}
		
		for(EuroTransAsia euroTransAsia : euroTransAsiaList){
			Integer matchSeq = euroTransAsia.getMatchSeq();
			String jobType = euroTransAsia.getJobType();
			String oddsCorpName = euroTransAsia.getOddsCorpName();
			String corpPreKey = matchSeq + "_" + jobType + "_" + oddsCorpName + "_" + "C";
			String euroPreKey = matchSeq + "_" + jobType + "_" + oddsCorpName + "_" + "E";
			String transPreKey = matchSeq + "_" + jobType + "_" + oddsCorpName + "_" + "T";
			String asiaPreKey = matchSeq + "_" + jobType + "_" + oddsCorpName + "_" + "A";
			result.put(corpPreKey, oddsCorpName);
			result.put(euroPreKey + "_" + "H", euroTransAsia.getHostOddsEuro().toString());
			result.put(euroPreKey + "_" + "E", euroTransAsia.getEvenOddsEuro().toString());
			result.put(euroPreKey + "_" + "V", euroTransAsia.getVisitingOddsEuro().toString());
			result.put(euroPreKey + "_" + "L", euroTransAsia.getLossRatioEuro().toString());
			result.put(euroPreKey + "_" + "HK", euroTransAsia.getHostKellyEuro() == null ? "NA" : euroTransAsia.getHostKellyEuro().toString());
			result.put(euroPreKey + "_" + "EK", euroTransAsia.getEvenKellyEuro() == null ? "NA" : euroTransAsia.getEvenKellyEuro().toString());
			result.put(euroPreKey + "_" + "VK", euroTransAsia.getVisitingKellyEuro() == null ? "NA" : euroTransAsia.getVisitingKellyEuro().toString());
			result.put(transPreKey + "_" + "H", euroTransAsia.getHostOddsAsiaTrans().toString());
			result.put(transPreKey + "_" + "HC", euroTransAsia.getHandicapAsiaTrans().toString());
			result.put(transPreKey + "_" + "V", euroTransAsia.getVisitingOddsAsiaTrans().toString());
			result.put(transPreKey + "_" + "T", euroTransAsia.getTotalDiscountTrans().toString());
			result.put(asiaPreKey + "_" + "H", euroTransAsia.getHostOddsAsia() == null ? "NA" : euroTransAsia.getHostOddsAsia().toString());
			result.put(asiaPreKey + "_" + "HC", euroTransAsia.getHandicapAsia() == null ? "NA" : euroTransAsia.getHandicapAsia().toString());
			result.put(asiaPreKey + "_" + "V", euroTransAsia.getVisitingOddsAsia() == null ? "NA" : euroTransAsia.getVisitingOddsAsia().toString());
			result.put(asiaPreKey + "_" + "T", euroTransAsia.getTotalDiscount() == null ? "NA" : euroTransAsia.getTotalDiscount().toString());
			result.put(asiaPreKey + "_" + "L", euroTransAsia.getLossRatioAsia() == null ? "NA" : euroTransAsia.getLossRatioAsia().toString());
			result.put(asiaPreKey + "_" + "HK", euroTransAsia.getHostKellyAsia() == null ? "NA" : euroTransAsia.getHostKellyAsia().toString());
			result.put(asiaPreKey + "_" + "VK", euroTransAsia.getVisitingKellyAsia() == null ? "NA" : euroTransAsia.getVisitingKellyAsia().toString());
		}
		return result;
	}
	
	/**
	 * 根据okUrlDate获取某场比赛的最大的jobType.(类型是B开头).
	 * @param okUrlDate
	 * @return
	 */
	private Map<Integer, String> getCurrMatchJobTypeMap(String okUrlDate){
		List<AsiaOddsTrends> trends = asiaOddsTrendsService.queryCurrMatchJobType(okUrlDate);
		if(trends == null || trends.isEmpty()){
			return null;
		}
		Map<Integer, String> result = new HashMap<Integer, String>();
		for(AsiaOddsTrends trend : trends){
			result.put(trend.getMatchSeq(), trend.getJobType());
		}
		return result;
	}
	
	/**
	 * 根据okUrlDate查询当前每场比赛当前的jobType.
	 * @param okUrlDate
	 * @return
	 */
	private Map<Integer, String> getJobTypesOfC(String okUrlDate){
		Map<Integer, String> result = new HashMap<Integer, String>();
		List<IndexStats> indexStatsList = indexStatsService.queryCurrJobTypeIndex(okUrlDate);
		if(indexStatsList == null || indexStatsList.isEmpty()){
			return result;
		}
		for(IndexStats indexStats : indexStatsList){
			result.put(indexStats.getMatchSeq(), indexStats.getJobType());
		}
		return result;
	}
	
	/**
	 * 根据okUrlDate查询当前每场比赛当前的jobType.
	 * @param okUrlDate
	 * @return
	 */
	private Map<Integer, String> getJobTypesOfD(String okUrlDate){
		Map<Integer, String> result = new HashMap<Integer, String>();
		List<EuroOddsHandicap> euroHandicapList = euroOddsHandicapService.queryCurrJobTypeEuroHandicap(okUrlDate);
		if(euroHandicapList == null || euroHandicapList.isEmpty()){
			return result;
		}
		for(EuroOddsHandicap euroOddsHandicap : euroHandicapList){
			result.put(euroOddsHandicap.getMatchSeq(), euroOddsHandicap.getJobType());
		}
		return result;
	}
	
	/**
	 * 获取排序后的matchSeqs. 这里是通过jobTypes来获取matchSeq的.
	 * @param jobTypes
	 * @return
	 */
	private List<Integer> sortMatchSeqByJobTypes(Map<Integer, String> jobTypes){
		List<Integer> sortedMatchSeqs = new ArrayList<Integer>();
		sortedMatchSeqs.addAll(jobTypes.keySet());
		Collections.sort(sortedMatchSeqs);
		return sortedMatchSeqs;
	}
	

	public KellyCorpResultService getKellyCorpResultService() {
		return kellyCorpResultService;
	}

	public void setKellyCorpResultService(
			KellyCorpResultService kellyCorpResultService) {
		this.kellyCorpResultService = kellyCorpResultService;
	}

	public CorpService getCorpService() {
		return corpService;
	}

	public void setCorpService(CorpService corpService) {
		this.corpService = corpService;
	}

	public EuroOddsService getEuroOddsService() {
		return euroOddsService;
	}

	public void setEuroOddsService(EuroOddsService euroOddsService) {
		this.euroOddsService = euroOddsService;
	}

	public EuroOddsChangeService getEuroOddsChangeService() {
		return euroOddsChangeService;
	}

	public void setEuroOddsChangeService(EuroOddsChangeService euroOddsChangeService) {
		this.euroOddsChangeService = euroOddsChangeService;
	}

	public KellyCorpCountService getKellyCorpCountService() {
		return kellyCorpCountService;
	}

	public void setKellyCorpCountService(KellyCorpCountService kellyCorpCountService) {
		this.kellyCorpCountService = kellyCorpCountService;
	}

	public KellyMatchCountService getKellyMatchCountService() {
		return kellyMatchCountService;
	}

	public void setKellyMatchCountService(
			KellyMatchCountService kellyMatchCountService) {
		this.kellyMatchCountService = kellyMatchCountService;
	}

	public ExchangeService getExchangeService() {
		return exchangeService;
	}

	public void setExchangeService(ExchangeService exchangeService) {
		this.exchangeService = exchangeService;
	}

	public SingleMatchService getSingleMatchService() {
		return singleMatchService;
	}

	public void setSingleMatchService(SingleMatchService singleMatchService) {
		this.singleMatchService = singleMatchService;
	}

	public AnalyseServiceImpl getAnalyseService() {
		return analyseService;
	}

	public void setAnalyseService(AnalyseServiceImpl analyseService) {
		this.analyseService = analyseService;
	}

	public LeaguePointsService getLeaguePointsService() {
		return leaguePointsService;
	}

	public void setLeaguePointsService(LeaguePointsService leaguePointsService) {
		this.leaguePointsService = leaguePointsService;
	}

	public MatchStatsService getMatchStatsService() {
		return matchStatsService;
	}

	public void setMatchStatsService(MatchStatsService matchStatsService) {
		this.matchStatsService = matchStatsService;
	}

	public ScoreOddsService getScoreOddsService() {
		return scoreOddsService;
	}

	public void setScoreOddsService(ScoreOddsService scoreOddsService) {
		this.scoreOddsService = scoreOddsService;
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

	public EuroTransAsiaService getEuroTransAsiaService() {
		return euroTransAsiaService;
	}

	public void setEuroTransAsiaService(EuroTransAsiaService euroTransAsiaService) {
		this.euroTransAsiaService = euroTransAsiaService;
	}

	public OkJobService getOkJobService() {
		return okJobService;
	}

	public void setOkJobService(OkJobService okJobService) {
		this.okJobService = okJobService;
	}

	public ProbAverageService getProbAverageService() {
		return probAverageService;
	}

	public void setProbAverageService(ProbAverageService probAverageService) {
		this.probAverageService = probAverageService;
	}

	public AsiaOddsChangeService getAsiaOddsChangeService() {
		return asiaOddsChangeService;
	}

	public void setAsiaOddsChangeService(AsiaOddsChangeService asiaOddsChangeService) {
		this.asiaOddsChangeService = asiaOddsChangeService;
	}
	
}
