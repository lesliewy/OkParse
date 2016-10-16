/**
 * 
 */
package com.wy.okooo.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.wy.okooo.domain.Corp;
import com.wy.okooo.domain.EuroAsiaRefer;
import com.wy.okooo.domain.EuropeChangeDailyStats;
import com.wy.okooo.domain.KellyRule;
import com.wy.okooo.domain.Match;
import com.wy.okooo.service.ConfigService;
import com.wy.okooo.service.CorpService;
import com.wy.okooo.service.EuroAsiaReferService;
import com.wy.okooo.service.EuroChangeDailyStatsService;
import com.wy.okooo.service.MatchJobService;
import com.wy.okooo.service.OkJobService;
import com.wy.okooo.service.SingleMatchService;
import com.wy.okooo.util.OkConstant;

/**
 * @author leslie
 *
 */
public class OkJobServiceImpl implements OkJobService {

	private static Logger LOGGER = Logger
			.getLogger(OkJobServiceImpl.class.getName());
	
	private MatchJobService matchJobService;
	
	private SingleMatchService singleMatchService;
	
	private EuroAsiaReferService euroAsiaReferService;
	
	private CorpService corpService;
	
	private EuroChangeDailyStatsService euroChangeDailyStatsService;
	
	private ConfigService configService;
	
	private static final Map<String, String> corpNamesMap = new HashMap<String, String>();
	
	private static final Map<String, String> corpNameNoMap = new HashMap<String, String>();
	
	private static final Set<Integer> allCorpsNo = new HashSet<Integer>();
	
	private static final Map<String, EuropeChangeDailyStats> euroChangeDailyStatsMap = new HashMap<String, EuropeChangeDailyStats>();
	
	/**
	 * 获取matchHtmlFiles中的Match对象. 
	 * okUrlDate 和 jobFlag 用于查询某个job的最大的beginMatchSeq, 然后从该matchSeq解析至最后。
	 * @param matchHtmlFiles
	 * @param okUrlDate
	 * @param jobFlag
	 * @return
	 */
	public List<Match> getMatchesFromHtml(List<File> matchHtmlFiles, String okUrlDate, String jobFlag){
		Integer queryBeginSeq = matchJobService.queryMaxBeginSeqByOkUrlDate(okUrlDate, jobFlag);
		Integer queryEndSeq = 1000;
		if(queryBeginSeq != null){
			queryEndSeq = queryBeginSeq + 1000;
		}else{
			queryBeginSeq = 1;
		}
		return singleMatchService.getAllMatchFromFiles(matchHtmlFiles, queryBeginSeq, queryEndSeq);
	}
	
	/**
	 * 获取欧赔亚盘关系信息, 存入map key: oddsEuro  value: euroAsiaRefer
	 * @return
	 */
	public Map<Float, EuroAsiaRefer> getEuroAsiaReferMap(){
		Map<Float, EuroAsiaRefer> result = new HashMap<Float, EuroAsiaRefer>();
		List<EuroAsiaRefer> euroAsiaReferList = euroAsiaReferService.queryAllEuroAsiaRefer();
		if(euroAsiaReferList != null && !euroAsiaReferList.isEmpty()){
			for(EuroAsiaRefer euroAsiaRefer : euroAsiaReferList){
				result.put(euroAsiaRefer.getOddsEuro(), euroAsiaRefer);
			}
		}
		return result;
	}
	
	public Map<String, String> queryCorpsNames(){
		if(corpNamesMap != null && !corpNamesMap.isEmpty()){
			return corpNamesMap;
		}
		List<Corp> corps = corpService.queryAllCorp();
		if(corps != null && !corps.isEmpty()){
			for(Corp corp : corps){
				corpNamesMap.put(corp.getCorpNo(), corp.getCorpName());
			}
		}
		LOGGER.info("corpNamesMap size: " + corpNamesMap.size());
		return corpNamesMap;
	}
	
	public Map<String, String> getCorpNameCorpNoMap(){
		if(corpNameNoMap != null && !corpNameNoMap.isEmpty()){
			return corpNameNoMap;
		}
		List<Corp> corps = corpService.queryAllCorp();
		if(corps != null && !corps.isEmpty()){
			for(Corp corp : corps){
				corpNameNoMap.put(corp.getCorpName(), corp.getCorpNo());
			}
		}
		LOGGER.info("corpNameNoMap size: " + corpNameNoMap.size());
		return corpNameNoMap;
	}
	
	public Set<Integer> queryAllCorpsNo(){
		if(allCorpsNo != null && !allCorpsNo.isEmpty()){
			return allCorpsNo;
		}
		List<Corp> corps = corpService.queryAllCorp();
		if(corps != null && !corps.isEmpty()){
			for(Corp corp : corps){
				allCorpsNo.add(Integer.valueOf(corp.getCorpNo()));
			}
		}
		LOGGER.info("allCorpsNo size: " + allCorpsNo.size());
		return allCorpsNo;
	
	}
	
	public List<KellyRule> getGeneralKellyRules(){
		List<KellyRule> result = new ArrayList<KellyRule>();
		Map<String, String> corpsMap = new HashMap<String, String>();
		corpsMap.put("Betstar", "399");
		corpsMap.put("申博138", "713");
		corpsMap.put("10Bet", "202");
		corpsMap.put("1xbet", "744");
		corpsMap.put("88asia88", "626");
		corpsMap.put("Bet-at-home.uk", "733");
		corpsMap.put("BetaAdonis", "758");
		corpsMap.put("Betdaq", "72");
		corpsMap.put("Betrally", "589");
		corpsMap.put("BetRedKings", "495");
		corpsMap.put("BetVictor", "560");
		corpsMap.put("Bovada.lv", "577");
		corpsMap.put("CBCX", "755");
		corpsMap.put("ComeOn", "530");
		corpsMap.put("Dashbet", "727");
		corpsMap.put("Fantasticwin", "570");
		corpsMap.put("HollywoodBets", "756");
		corpsMap.put("Jetbull", "324");
		corpsMap.put("Ladbrokes.au", "725");
		corpsMap.put("NetBet", "585");
		corpsMap.put("noxwin", "423");
		corpsMap.put("Partypoker", "751");
		corpsMap.put("Redbet", "205");
		corpsMap.put("smarkets", "601");
		corpsMap.put("Sportsbook.ag", "724");
		corpsMap.put("Starbet.be", "732");
		corpsMap.put("Teambet", "546");
		corpsMap.put("Titanbet", "440");
		corpsMap.put("Vernons", "726");
		corpsMap.put("Winmasters", "731");
		corpsMap.put("Winner", "592");
		corpsMap.put("英国约翰G", "753");
		corpsMap.put("18luck", "752");
		corpsMap.put("Diamond Sportsbook Int.", "668");
		corpsMap.put("Instant Action Sports", "136");
		corpsMap.put("Matchbook", "206");
		corpsMap.put("Netbet.it", "734");
		corpsMap.put("The Greek", "723");
		corpsMap.put("WBX", "367");
		corpsMap.put("Winamax.com", "256");
		corpsMap.put("申博娱乐", "750");
		corpsMap.put("Bet90", "754");
		corpsMap.put("BetRebels", "722");
		corpsMap.put("Completesportsbetting", "321");
		corpsMap.put("Gazzabet", "730");
		corpsMap.put("Heritage Sports", "695");
		corpsMap.put("Sportium", "717");
		corpsMap.put("优德", "757");
		corpsMap.put("Coliseumbet", "728");
		
		Set<Entry<String, String>> entrys = corpsMap.entrySet();
		for(Entry<String, String> entry : entrys){
			KellyRule rule = new KellyRule();
			rule.setOddsCorpName(entry.getKey());
			rule.setCorpNo(entry.getValue());
			result.add(rule);
		}
		return result;
	}
	
	/**
	 * 获取分析 LOT_ODDS_EURO_CHANGE 后的结果信息 LOT_ODDS_EURO_DAILY_STATS, 如果当前的okUrlDate没有查询到数据, 使用最初的150505;
	 * 如果没有配置prob, 使用 0.80|0.80|0.80|0.80|
	 * @return
	 */
	public Map<String, EuropeChangeDailyStats> getEuroChangeDailyStatsMap(){
		if(euroChangeDailyStatsMap != null && !euroChangeDailyStatsMap.isEmpty()){
			return euroChangeDailyStatsMap;
		}
		
		// 查询配置的okUrlDate 和 prob. 如果prob没有配置, H HE V VE均使用0.8
		Map<String, String> configMap = getOkUrlDateProbFromConfig();
		
		List<EuropeChangeDailyStats> euroChangeHList = euroChangeDailyStatsService.queryDailyStatsByStatsTypeProb(configMap.get("okUrlDate"), "H", Float.valueOf(configMap.get("H_PROB")));
		// 如果当前的okUrlDate没有查询到，使用最初的，即 150505
		if(euroChangeHList == null || euroChangeHList.isEmpty()){
			configMap.put("okUrlDate", "150505");
			euroChangeHList = euroChangeDailyStatsService.queryDailyStatsByStatsTypeProb(configMap.get("okUrlDate"), "H", Float.valueOf(configMap.get("H_PROB")));
			LOGGER.info("euroChangeHList is empty, use okUrlDate: 150505");
		}
		List<EuropeChangeDailyStats> euroChangeHEList = euroChangeDailyStatsService.queryDailyStatsByStatsTypeProb(configMap.get("okUrlDate"), "HE", Float.valueOf(configMap.get("HE_PROB")));
		List<EuropeChangeDailyStats> euroChangeVList = euroChangeDailyStatsService.queryDailyStatsByStatsTypeProb(configMap.get("okUrlDate"), "V", Float.valueOf(configMap.get("V_PROB")));
		List<EuropeChangeDailyStats> euroChangeVEList = euroChangeDailyStatsService.queryDailyStatsByStatsTypeProb(configMap.get("okUrlDate"), "VE", Float.valueOf(configMap.get("VE_PROB")));
		LOGGER.info("okUrlDate: " + configMap.get("okUrlDate") + "; H size:" + euroChangeHList.size() + "; HE size:" + euroChangeHEList.size() + "; V size:" + euroChangeVList.size() + "; VE size:" + euroChangeVEList.size());
		
		transDailyStatsMapFromList(euroChangeDailyStatsMap, euroChangeHList, "H");
		transDailyStatsMapFromList(euroChangeDailyStatsMap, euroChangeHEList, "HE");
		transDailyStatsMapFromList(euroChangeDailyStatsMap, euroChangeVList, "V");
		transDailyStatsMapFromList(euroChangeDailyStatsMap, euroChangeVEList, "VE");
		LOGGER.info("euroChangeDailyStatsMap size: " + euroChangeDailyStatsMap.size());
		return euroChangeDailyStatsMap;
	}
	
	public void emptyEuroChangeDailyStatsMap(){
		if(euroChangeDailyStatsMap != null && !euroChangeDailyStatsMap.isEmpty()){
			euroChangeDailyStatsMap.clear();
		}
	}
	
	private void transDailyStatsMapFromList(Map<String, EuropeChangeDailyStats> map, List<EuropeChangeDailyStats> dailyStatsList, String type){
		if(dailyStatsList == null || dailyStatsList.isEmpty() || map == null){
			return;
		}
		for(EuropeChangeDailyStats dailyStats : dailyStatsList){
			map.put(type + "_" + dailyStats.getOddsCorpName(), dailyStats);
		}
	}
	
	/**
	 * 获取配置参数.
	 * @return
	 */
	private Map<String, String> getOkUrlDateProbFromConfig(){
		Map<String, String> result = new HashMap<String, String>();
		// 查询配置参数LOT_CONFIG
		Map<String, String> configs = configService.queryAllConfigInMap();
		String okUrlDate = configs.get(OkConstant.CONFIG_CURR_OK_URL_DATE);
		if(StringUtils.isBlank(okUrlDate)){
			LOGGER.error("config CONFIG_CURR_OK_URL_DATE is null, set 150505");
			okUrlDate = "150505";
		}
		String probStrs = configs.get(OkConstant.PROB_EURO_CHANGE_DAILY_STATS);
		if(StringUtils.isBlank(probStrs)){
			probStrs = "0.80|0.80|0.80|0.80|";
		}
		String[] probArr = probStrs.split("\\|");
		
		result.put("okUrlDate", okUrlDate);
		result.put("H_PROB", probArr[0]);
		result.put("HE_PROB", probArr[1]);
		result.put("V_PROB", probArr[2]);
		result.put("VE_PROB", probArr[3]);
		
		return result;
	}
	
	
	public MatchJobService getMatchJobService() {
		return matchJobService;
	}
	public void setMatchJobService(MatchJobService matchJobService) {
		this.matchJobService = matchJobService;
	}
	public SingleMatchService getSingleMatchService() {
		return singleMatchService;
	}
	public void setSingleMatchService(SingleMatchService singleMatchService) {
		this.singleMatchService = singleMatchService;
	}

	public EuroAsiaReferService getEuroAsiaReferService() {
		return euroAsiaReferService;
	}

	public void setEuroAsiaReferService(EuroAsiaReferService euroAsiaReferService) {
		this.euroAsiaReferService = euroAsiaReferService;
	}

	public CorpService getCorpService() {
		return corpService;
	}

	public void setCorpService(CorpService corpService) {
		this.corpService = corpService;
	}

	public Map<String, String> getCorpNamesMap() {
		return corpNamesMap;
	}

	public EuroChangeDailyStatsService getEuroChangeDailyStatsService() {
		return euroChangeDailyStatsService;
	}

	public void setEuroChangeDailyStatsService(
			EuroChangeDailyStatsService euroChangeDailyStatsService) {
		this.euroChangeDailyStatsService = euroChangeDailyStatsService;
	}

	public ConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

}
