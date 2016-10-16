/**
 * 
 */
package com.wy.okooo.job;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.wy.okooo.domain.EuropeChangeDailyStats;
import com.wy.okooo.domain.EuropeOddsChange;
import com.wy.okooo.service.ConfigService;
import com.wy.okooo.service.EuroChangeDailyStatsService;
import com.wy.okooo.service.EuroOddsChangeService;
import com.wy.okooo.service.EuroOddsService;
import com.wy.okooo.util.OkConstant;

/**
 * 定时分析LOT_ODDS_EURO_CHANGE_DAILY, 并将结果存入LOT_EURO_CHANGE_DAILY_STATS
 * 
 * @author leslie
 * 
 */
public class EuroChangeDailyStatsJob {
	
	private static Logger LOGGER = Logger.getLogger(EuroChangeDailyStatsJob.class
			.getName());
	
	private ConfigService configService;
	
	private EuroOddsService euroOddsService;
	
	private EuroOddsChangeService euroOddsChangeService;
	
	private EuroChangeDailyStatsService euroChangeDailyStatsService;
	
	private String currOkUrlDate = null;
	
	public void processAnalyseEuroChangeDaily(){
		LOGGER.info("processAnalyseEuroChangeDaily job begin...");
		long begin = System.currentTimeMillis();
		
		// 查询配置参数LOT_CONFIG
		if(!initFromConfig()){
			return;
		}
		
		analyseEuroChangeDaily();
		
		LOGGER.info("processAnalyseEuroChangeDaily total time: " + (System.currentTimeMillis() - begin)/1000 + " s.");
	}
	
	private boolean initFromConfig(){
		Map<String, String> configs = configService.queryAllConfigInMap();
		currOkUrlDate = configs.get(OkConstant.CONFIG_CURR_OK_URL_DATE);
		
		if(StringUtils.isBlank(currOkUrlDate)){
			LOGGER.error("config CONFIG_CURR_OK_URL_DATE is blank, return now.");
			return false;
		}
		return true;
	}
	
	/**
	 * 分析 LOT_ODDS_EURO_CHANGE_DAILY
	 * LOT_ODDS_EURO_CHANGE_DAILY 中保存的是变化次数 <= 5(即ODDS_SEQ <= 5) 且kelly与lossRatio的差 > 0.05的euroOddsChange.
	 * 初始赔率赔率小的一方, 其kelly指数大于赔率大的一方的kelly指数, 我认为是正常的: 例如
	 *         1.72 |  3.65 |  4.75 | 0.94 | 0.95 | 0.95 | 0.89 |    后三个是胜平负的kelly, 倒数第四个是lossRatio
	 * 
	 * hostOddsMinProb: 主胜赔率最小 && 主胜kelly < 客胜kelly && 客胜kelly > lossRatio + 0.7 : 主胜的比例.
	 * hostOddsMinNotLoseProb: 主胜赔率最小 && 主胜kelly < 客胜kelly && 客胜kelly > lossRatio + 0.3 : 主胜平的比例.
	 * visitingOddsMinProb: 客胜赔率最小 && 客胜kelly < 主胜kelly && 主胜kelly > lossRatio + 0.7 : 客胜的比例.
	 * visitingOddsMinNotLoseProb: 客胜赔率最小 && 客胜kelly < 主胜kelly && 主胜kelly > lossRatio + 0.3 : 客胜平的比例.
	 */
	private void analyseEuroChangeDaily() {
		// 先找出所有的公司.
		Set<String> allCorpNames = euroOddsService.queryAllCorpNames();
		if(allCorpNames == null){
			LOGGER.info("allCorpNames is null, return now...");
			return;
		}
		
		List<EuropeOddsChangeDaily> europeOddsChangeDailyList = new ArrayList<EuropeOddsChangeDaily>();
		for(String corpName : allCorpNames){
			analyseSingleCorpEuroChangeDaily(corpName, europeOddsChangeDailyList);
		}
		// 主胜
		EuropeOddsChangeDailyComparator comparator1 = new EuropeOddsChangeDailyComparator(false, "hostOddsMinProb");
		Collections.sort(europeOddsChangeDailyList, comparator1);
		insertEuroChangeDailyStats(europeOddsChangeDailyList, "H");
		display3(europeOddsChangeDailyList, comparator1);
		
		// 主胜平
		EuropeOddsChangeDailyComparator comparator2 = new EuropeOddsChangeDailyComparator(false, "hostOddsMinNotLoseProb");
		Collections.sort(europeOddsChangeDailyList, comparator2);
		insertEuroChangeDailyStats(europeOddsChangeDailyList, "HE");
		display4(europeOddsChangeDailyList, comparator2);
		
		// 客胜
		EuropeOddsChangeDailyComparator comparator3 = new EuropeOddsChangeDailyComparator(false, "visitingOddsMinProb");
		Collections.sort(europeOddsChangeDailyList, comparator3);
		insertEuroChangeDailyStats(europeOddsChangeDailyList, "V");
		display3(europeOddsChangeDailyList, comparator3);
		
		// 客胜平
		EuropeOddsChangeDailyComparator comparator4 = new EuropeOddsChangeDailyComparator(false, "visitingOddsMinNotLoseProb");
		Collections.sort(europeOddsChangeDailyList, comparator4);
		insertEuroChangeDailyStats(europeOddsChangeDailyList, "VE");
		display4(europeOddsChangeDailyList, comparator4);
	}
	
	
	private void analyseSingleCorpEuroChangeDaily(String corpName, List<EuropeOddsChangeDaily> europeOddsChangeDailyList){
		List<EuropeOddsChange> europeOddsChangeList = euroOddsChangeService.queryDailyInitialWithResult(corpName);
		if(europeOddsChangeList == null || europeOddsChangeList.isEmpty()){
			return;
		}
		// 主胜
		Integer totalHostOddsMin = 0;
		Integer hostOddsMinHost = 0;
		Integer hostOddsMinEven = 0;
		Integer hostOddsMinVisiting = 0;
		
		// 主胜平
		Integer totalHostOddsMinNotLose = 0;
		Integer hostOddsMinHostNotLose = 0;
		Integer hostOddsMinEvenNotLose = 0;
		Integer hostOddsMinVisitingNotLose = 0;
		
		// 客胜
		Integer totalVisitingOddsMin = 0;
		Integer visitingOddsMinHost = 0;
		Integer visitingOddsMinEven = 0;
		Integer visitingOddsMinVisiting = 0;
		
		// 客胜平
		Integer totalVisitingOddsMinNotLose = 0;
		Integer visitingOddsMinHostNotLose = 0;
		Integer visitingOddsMinEvenNotLose = 0;
		Integer visitingOddsMinVisitingNotLose = 0;
		for(EuropeOddsChange change : europeOddsChangeList){
			Float hostOdds = change.getHostOdds();
			Float evenOdds = change.getEvenOdds();
			Float visitingOdds = change.getVisitingOdds();
			Float lossRatio = change.getLossRatio();
			Float hostKelly = change.getHostKelly();
			Float visitingKelly = change.getVisitingKelly();
			Integer hostGoals = change.getHostGoals();
			Integer visitingGoals = change.getVisitingGoals();
			if(hostOdds < evenOdds && hostOdds < visitingOdds
					&& hostKelly < visitingKelly){
				// 主胜
				if(visitingKelly >= lossRatio + 0.06){
					totalHostOddsMin++;
					if(hostGoals > visitingGoals){
						hostOddsMinHost++;
					}else if(hostGoals.intValue() == visitingGoals.intValue()){
						hostOddsMinEven++;
					}else if(hostGoals < visitingGoals){
						hostOddsMinVisiting++;
					}
				}
				// 主胜平
				if(visitingKelly >= lossRatio + 0.03){
					totalHostOddsMinNotLose++;
					if(hostGoals > visitingGoals){
						hostOddsMinHostNotLose++;
					}else if(hostGoals.intValue() == visitingGoals.intValue()){
						hostOddsMinEvenNotLose++;
					}else if(hostGoals < visitingGoals){
						hostOddsMinVisitingNotLose++;
					}
				}
			}
					
			if(visitingOdds < evenOdds && visitingOdds < hostOdds
					&& visitingKelly < hostKelly){
				// 客胜
				if(hostKelly >= lossRatio + 0.06){
					totalVisitingOddsMin++;
					if(hostGoals > visitingGoals){
						visitingOddsMinHost++;
					}else if(hostGoals.intValue() == visitingGoals.intValue()){
						visitingOddsMinEven++;
					}else if(hostGoals < visitingGoals){
						visitingOddsMinVisiting++;
					}
				}
				// 客胜平
				if(hostKelly >= lossRatio + 0.03){
					totalVisitingOddsMinNotLose++;
					if(hostGoals > visitingGoals){
						visitingOddsMinHostNotLose++;
					}else if(hostGoals.intValue() == visitingGoals.intValue()){
						visitingOddsMinEvenNotLose++;
					}else if(hostGoals < visitingGoals){
						visitingOddsMinVisitingNotLose++;
					}
				}
			}
		}
		EuropeOddsChangeDaily daily = new EuropeOddsChangeDaily();
		daily.setCorpName(corpName);
		// 主胜
		daily.setTotalHostOddsMin(totalHostOddsMin);
		daily.setHostOddsMinHost(hostOddsMinHost);
		daily.setHostOddsMinEven(hostOddsMinEven);
		daily.setHostOddsMinVisiting(hostOddsMinVisiting);
		daily.setHostOddsMinProb(Math.round(hostOddsMinHost/totalHostOddsMin.floatValue() * 100)/100.0f);
		
		// 主胜平
		daily.setTotalHostOddsMinNotLose(totalHostOddsMinNotLose);
		daily.setHostOddsMinHostNotLose(hostOddsMinHostNotLose);
		daily.setHostOddsMinEvenNotLose(hostOddsMinEvenNotLose);
		daily.setHostOddsMinVisitingNotLose(hostOddsMinVisitingNotLose);
		daily.setHostOddsMinNotLoseProb(Math.round((hostOddsMinHostNotLose+hostOddsMinEvenNotLose)/totalHostOddsMinNotLose.floatValue() * 100)/100.0f);
		
		// 客胜
		daily.setTotalVisitingOddsMin(totalVisitingOddsMin);
		daily.setVisitingOddsMinHost(visitingOddsMinHost);
		daily.setVisitingOddsMinEven(visitingOddsMinEven);
		daily.setVisitingOddsMinVisiting(visitingOddsMinVisiting);
		daily.setVisitingOddsMinProb(Math.round(visitingOddsMinVisiting/totalVisitingOddsMin.floatValue() * 100)/100.0f);
		
		// 客胜平
		daily.setTotalVisitingOddsMinNotLose(totalVisitingOddsMinNotLose);
		daily.setVisitingOddsMinHostNotLose(visitingOddsMinHostNotLose);
		daily.setVisitingOddsMinEvenNotLose(visitingOddsMinEvenNotLose);
		daily.setVisitingOddsMinVisitingNotLose(visitingOddsMinVisitingNotLose);
		daily.setVisitingOddsMinNotLoseProb(Math.round((visitingOddsMinVisitingNotLose + visitingOddsMinEvenNotLose)/totalVisitingOddsMinNotLose.floatValue() * 100)/100.0f);
		europeOddsChangeDailyList.add(daily);
	}
	
	private void insertEuroChangeDailyStats(List<EuropeOddsChangeDaily> europeOddsChangeDailyList, String statsType){
		if(europeOddsChangeDailyList == null || europeOddsChangeDailyList.isEmpty()){
			return;
		}
		List<EuropeChangeDailyStats> dailyStatsList = new ArrayList<EuropeChangeDailyStats>();
		int rank = 1;
		for(EuropeOddsChangeDaily daily : europeOddsChangeDailyList){
			EuropeChangeDailyStats dailyStats = new EuropeChangeDailyStats();
			dailyStats.setOkUrlDate(currOkUrlDate);
			dailyStats.setStatsType(statsType);
			dailyStats.setOddsCorpName(daily.getCorpName());
			dailyStats.setRank(rank++);
			dailyStats.setTimestamp(new Timestamp(Calendar.getInstance()
					.getTimeInMillis()));
			// 主胜
			if("H".equals(statsType)){
				dailyStats.setTotalMatches(daily.getTotalHostOddsMin());
				dailyStats.setHostMatches(daily.getHostOddsMinHost());
				dailyStats.setEvenMatches(daily.getHostOddsMinEven());
				dailyStats.setVisitingMatches(daily.getHostOddsMinVisiting());
				dailyStats.setProb(daily.getHostOddsMinProb());
			}else if("HE".equals(statsType)){ // 主胜平
				dailyStats.setTotalMatches(daily.getTotalHostOddsMinNotLose());
				dailyStats.setHostMatches(daily.getHostOddsMinHostNotLose());
				dailyStats.setEvenMatches(daily.getHostOddsMinEvenNotLose());
				dailyStats.setVisitingMatches(daily.getHostOddsMinVisitingNotLose());
				dailyStats.setProb(daily.getHostOddsMinNotLoseProb());
			}else if("V".equals(statsType)){
				dailyStats.setTotalMatches(daily.getTotalVisitingOddsMin());
				dailyStats.setHostMatches(daily.getVisitingOddsMinHost());
				dailyStats.setEvenMatches(daily.getVisitingOddsMinEven());
				dailyStats.setVisitingMatches(daily.getVisitingOddsMinVisiting());
				dailyStats.setProb(daily.getVisitingOddsMinProb());
			}else if("VE".equals(statsType)){
				dailyStats.setTotalMatches(daily.getTotalVisitingOddsMinNotLose());
				dailyStats.setHostMatches(daily.getVisitingOddsMinHostNotLose());
				dailyStats.setEvenMatches(daily.getVisitingOddsMinEvenNotLose());
				dailyStats.setVisitingMatches(daily.getVisitingOddsMinVisitingNotLose());
				dailyStats.setProb(daily.getVisitingOddsMinNotLoseProb());
			}
			dailyStatsList.add(dailyStats);
		}
		// 先删除
		euroChangeDailyStatsService.deleteDailyStatsByStatsType(currOkUrlDate, statsType);
		euroChangeDailyStatsService.insertDailyStatsBatch(dailyStatsList);
	}
	
	private void display3(List<EuropeOddsChangeDaily> europeOddsChangeDailyList, EuropeOddsChangeDailyComparator comparator){
		StringBuilder sb = new StringBuilder("\n" + comparator.getComType() + "\n");
		int seq = 1;
		for(EuropeOddsChangeDaily daily : europeOddsChangeDailyList){
			Formatter formatter = new Formatter();
			formatter.format("%5s %25s %15s %6s %6s %6s %5s %15s %6s %6s %6s %5s\n", 
					seq++,
					daily.getCorpName(),
					"TotalHOM:" + daily.getTotalHostOddsMin(),
					"H:" + daily.getHostOddsMinHost(),
					"E:" + daily.getHostOddsMinEven(),
					"V:" + daily.getHostOddsMinVisiting(),
					daily.getHostOddsMinProb(),
					"TotalVOM:" + daily.getTotalVisitingOddsMin(),
					"H:" + daily.getVisitingOddsMinHost(),
					"E:" + daily.getVisitingOddsMinEven(),
					"V:" + daily.getVisitingOddsMinVisiting(),
					daily.getVisitingOddsMinProb()
					);
			sb.append(formatter.toString());
			formatter.close();
		}
		LOGGER.info(sb.toString());
	}
	
	private void display4(List<EuropeOddsChangeDaily> europeOddsChangeDailyList, EuropeOddsChangeDailyComparator comparator){
		StringBuilder sb = new StringBuilder("\n" + comparator.getComType() + "\n");
		int seq = 1;
		for(EuropeOddsChangeDaily daily : europeOddsChangeDailyList){
			Formatter formatter = new Formatter();
			formatter.format("%5s %25s %15s %6s %6s %6s %5s %15s %6s %6s %6s %5s\n", 
					seq++,
					daily.getCorpName(),
					"TotalHOMNL:" + daily.getTotalHostOddsMinNotLose(),
					"H:" + daily.getHostOddsMinHostNotLose(),
					"E:" + daily.getHostOddsMinEvenNotLose(),
					"V:" + daily.getHostOddsMinVisitingNotLose(),
					daily.getHostOddsMinNotLoseProb(),
					"TotalVOMNL:" + daily.getTotalVisitingOddsMinNotLose(),
					"H:" + daily.getVisitingOddsMinHostNotLose(),
					"E:" + daily.getVisitingOddsMinEvenNotLose(),
					"V:" + daily.getVisitingOddsMinVisitingNotLose(),
					daily.getVisitingOddsMinNotLoseProb()
					);
			sb.append(formatter.toString());
			formatter.close();
		}
		LOGGER.info(sb.toString());
	}
	
	class EuropeOddsChangeDaily{
		String corpName;
		// 主胜
		Integer totalHostOddsMin = 0;
		Integer hostOddsMinHost = 0;
		Integer hostOddsMinEven = 0;
		Integer hostOddsMinVisiting = 0;
		Float hostOddsMinProb = 0f;
		
		// 客胜
		Integer totalVisitingOddsMin = 0;
		Integer visitingOddsMinHost = 0;
		Integer visitingOddsMinEven = 0;
		Integer visitingOddsMinVisiting = 0;
		Float visitingOddsMinProb = 0f;
		
		// 主胜平
		Integer totalHostOddsMinNotLose = 0;
		Integer hostOddsMinHostNotLose = 0;
		Integer hostOddsMinEvenNotLose = 0;
		Integer hostOddsMinVisitingNotLose = 0;
		Float hostOddsMinNotLoseProb = 0f;
		
		// 客胜平
		Integer totalVisitingOddsMinNotLose = 0;
		Integer visitingOddsMinHostNotLose = 0;
		Integer visitingOddsMinEvenNotLose = 0;
		Integer visitingOddsMinVisitingNotLose = 0;
		Float visitingOddsMinNotLoseProb = 0f;
		public String getCorpName() {
			return corpName;
		}
		public void setCorpName(String corpName) {
			this.corpName = corpName;
		}
		public Integer getTotalHostOddsMin() {
			return totalHostOddsMin;
		}
		public void setTotalHostOddsMin(Integer totalHostOddsMin) {
			this.totalHostOddsMin = totalHostOddsMin;
		}
		public Integer getHostOddsMinHost() {
			return hostOddsMinHost;
		}
		public void setHostOddsMinHost(Integer hostOddsMinHost) {
			this.hostOddsMinHost = hostOddsMinHost;
		}
		public Integer getHostOddsMinEven() {
			return hostOddsMinEven;
		}
		public void setHostOddsMinEven(Integer hostOddsMinEven) {
			this.hostOddsMinEven = hostOddsMinEven;
		}
		public Integer getHostOddsMinVisiting() {
			return hostOddsMinVisiting;
		}
		public void setHostOddsMinVisiting(Integer hostOddsMinVisiting) {
			this.hostOddsMinVisiting = hostOddsMinVisiting;
		}
		public Float getHostOddsMinProb() {
			return hostOddsMinProb;
		}
		public void setHostOddsMinProb(Float hostOddsMinProb) {
			this.hostOddsMinProb = hostOddsMinProb;
		}
		public Integer getTotalVisitingOddsMin() {
			return totalVisitingOddsMin;
		}
		public void setTotalVisitingOddsMin(Integer totalVisitingOddsMin) {
			this.totalVisitingOddsMin = totalVisitingOddsMin;
		}
		public Integer getVisitingOddsMinHost() {
			return visitingOddsMinHost;
		}
		public void setVisitingOddsMinHost(Integer visitingOddsMinHost) {
			this.visitingOddsMinHost = visitingOddsMinHost;
		}
		public Integer getVisitingOddsMinEven() {
			return visitingOddsMinEven;
		}
		public void setVisitingOddsMinEven(Integer visitingOddsMinEven) {
			this.visitingOddsMinEven = visitingOddsMinEven;
		}
		public Integer getVisitingOddsMinVisiting() {
			return visitingOddsMinVisiting;
		}
		public void setVisitingOddsMinVisiting(Integer visitingOddsMinVisiting) {
			this.visitingOddsMinVisiting = visitingOddsMinVisiting;
		}
		public Float getVisitingOddsMinProb() {
			return visitingOddsMinProb;
		}
		public void setVisitingOddsMinProb(Float visitingOddsMinProb) {
			this.visitingOddsMinProb = visitingOddsMinProb;
		}
		public Integer getTotalHostOddsMinNotLose() {
			return totalHostOddsMinNotLose;
		}
		public void setTotalHostOddsMinNotLose(Integer totalHostOddsMinNotLose) {
			this.totalHostOddsMinNotLose = totalHostOddsMinNotLose;
		}
		public Integer getHostOddsMinHostNotLose() {
			return hostOddsMinHostNotLose;
		}
		public void setHostOddsMinHostNotLose(Integer hostOddsMinHostNotLose) {
			this.hostOddsMinHostNotLose = hostOddsMinHostNotLose;
		}
		public Integer getHostOddsMinEvenNotLose() {
			return hostOddsMinEvenNotLose;
		}
		public void setHostOddsMinEvenNotLose(Integer hostOddsMinEvenNotLose) {
			this.hostOddsMinEvenNotLose = hostOddsMinEvenNotLose;
		}
		public Integer getHostOddsMinVisitingNotLose() {
			return hostOddsMinVisitingNotLose;
		}
		public void setHostOddsMinVisitingNotLose(Integer hostOddsMinVisitingNotLose) {
			this.hostOddsMinVisitingNotLose = hostOddsMinVisitingNotLose;
		}
		public Integer getTotalVisitingOddsMinNotLose() {
			return totalVisitingOddsMinNotLose;
		}
		public void setTotalVisitingOddsMinNotLose(Integer totalVisitingOddsMinNotLose) {
			this.totalVisitingOddsMinNotLose = totalVisitingOddsMinNotLose;
		}
		public Integer getVisitingOddsMinHostNotLose() {
			return visitingOddsMinHostNotLose;
		}
		public void setVisitingOddsMinHostNotLose(Integer visitingOddsMinHostNotLose) {
			this.visitingOddsMinHostNotLose = visitingOddsMinHostNotLose;
		}
		public Integer getVisitingOddsMinEvenNotLose() {
			return visitingOddsMinEvenNotLose;
		}
		public void setVisitingOddsMinEvenNotLose(Integer visitingOddsMinEvenNotLose) {
			this.visitingOddsMinEvenNotLose = visitingOddsMinEvenNotLose;
		}
		public Integer getVisitingOddsMinVisitingNotLose() {
			return visitingOddsMinVisitingNotLose;
		}
		public void setVisitingOddsMinVisitingNotLose(
				Integer visitingOddsMinVisitingNotLose) {
			this.visitingOddsMinVisitingNotLose = visitingOddsMinVisitingNotLose;
		}
		public Float getHostOddsMinNotLoseProb() {
			return hostOddsMinNotLoseProb;
		}
		public void setHostOddsMinNotLoseProb(Float hostOddsMinNotLoseProb) {
			this.hostOddsMinNotLoseProb = hostOddsMinNotLoseProb;
		}
		public Float getVisitingOddsMinNotLoseProb() {
			return visitingOddsMinNotLoseProb;
		}
		public void setVisitingOddsMinNotLoseProb(Float visitingOddsMinNotLoseProb) {
			this.visitingOddsMinNotLoseProb = visitingOddsMinNotLoseProb;
		}
		
	}
	
	private class EuropeOddsChangeDailyComparator implements Comparator<EuropeOddsChangeDaily>{
		private boolean isAsc = true;
		private String comType = "totalMatch";
		EuropeOddsChangeDailyComparator(boolean asc, String type){
			isAsc = asc;
			comType = type;
		}
		public int compare(EuropeOddsChangeDaily o1, EuropeOddsChangeDaily o2) {
			if(isAsc){
				if("hostOddsMinProb".equals(comType)){
					// prop 相同时使用 totalMatch 排序.
					if(o1.getHostOddsMinProb().compareTo(o2.getHostOddsMinProb()) == 0){
						return o1.getTotalHostOddsMin() - o2.getTotalHostOddsMin();
					}
					return o1.getHostOddsMinProb().compareTo(o2.getHostOddsMinProb());
				}else if("visitingOddsMinProb".equals(comType)){
					if(o1.getVisitingOddsMinProb().compareTo(o2.getVisitingOddsMinProb()) == 0){
						return o1.getTotalVisitingOddsMin() - o2.getTotalVisitingOddsMin();
					}
					return o1.getVisitingOddsMinProb().compareTo(o2.getVisitingOddsMinProb());
				}else if("hostOddsMinNotLoseProb".equals(comType)){
					if(o1.getHostOddsMinNotLoseProb().compareTo(o2.getHostOddsMinNotLoseProb()) == 0){
						return o1.getTotalHostOddsMinNotLose() - o2.getTotalHostOddsMinNotLose();
					}
					return o1.getHostOddsMinNotLoseProb().compareTo(o2.getHostOddsMinNotLoseProb());
				}else if("visitingOddsMinNotLoseProb".equals(comType)){
					if(o1.getVisitingOddsMinNotLoseProb().compareTo(o2.getVisitingOddsMinNotLoseProb()) == 0){
						return o1.getTotalVisitingOddsMinNotLose() - o2.getTotalVisitingOddsMinNotLose();
					}
					return o1.getVisitingOddsMinNotLoseProb().compareTo(o2.getVisitingOddsMinNotLoseProb());
				}
			}else{
				if("hostOddsMinProb".equals(comType)){
					if(o2.getHostOddsMinProb().compareTo(o1.getHostOddsMinProb()) == 0){
						return o2.getTotalHostOddsMin() - o1.getTotalHostOddsMin();
					}
					return o2.getHostOddsMinProb().compareTo(o1.getHostOddsMinProb());
				}else if("visitingOddsMinProb".equals(comType)){
					if(o2.getVisitingOddsMinProb().compareTo(o1.getVisitingOddsMinProb()) == 0){
						return o2.getTotalVisitingOddsMin() - o1.getTotalVisitingOddsMin();
					}
					return o2.getVisitingOddsMinProb().compareTo(o1.getVisitingOddsMinProb());
				}else if("hostOddsMinNotLoseProb".equals(comType)){
					if(o2.getHostOddsMinNotLoseProb().compareTo(o1.getHostOddsMinNotLoseProb()) == 0){
						return o2.getTotalHostOddsMinNotLose() - o1.getTotalHostOddsMinNotLose();
					}
					return o2.getHostOddsMinNotLoseProb().compareTo(o1.getHostOddsMinNotLoseProb());
				}else if("visitingOddsMinNotLoseProb".equals(comType)){
					if(o2.getVisitingOddsMinNotLoseProb().compareTo(o1.getVisitingOddsMinNotLoseProb()) == 0){
						return o2.getTotalVisitingOddsMinNotLose() - o1.getTotalVisitingOddsMinNotLose();
					}
					return o2.getVisitingOddsMinNotLoseProb().compareTo(o1.getVisitingOddsMinNotLoseProb());
				}
			}
			return o2.getHostOddsMinProb().compareTo(o1.getHostOddsMinProb());
		}
		public String getComType() {
			return comType;
		}
	}

	public ConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
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

	public EuroChangeDailyStatsService getEuroChangeDailyStatsService() {
		return euroChangeDailyStatsService;
	}

	public void setEuroChangeDailyStatsService(
			EuroChangeDailyStatsService euroChangeDailyStatsService) {
		this.euroChangeDailyStatsService = euroChangeDailyStatsService;
	}
	
}
