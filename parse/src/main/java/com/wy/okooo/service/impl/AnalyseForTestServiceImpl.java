/**
 * 
 */
package com.wy.okooo.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.wy.okooo.domain.EuropeOdds;
import com.wy.okooo.service.AnalyseForTestService;
import com.wy.okooo.service.EuroOddsChangeService;
import com.wy.okooo.service.EuroOddsService;

/**
 * @author leslie
 *
 */
public class AnalyseForTestServiceImpl implements AnalyseForTestService {

	private static Logger LOGGER = Logger
			.getLogger(AnalyseForTestServiceImpl.class.getName());
	
	private EuroOddsService euroOddsService;
	
	private EuroOddsChangeService euroOddsChangeService;
	
	/**
	 * 分析LOT_ODDS_EURO
	 * totalMatch: 该公司开盘的比赛场次数.
	 * winKellyMinProp: 胜的一方的kelly是最小的比例:  (主胜时kelly在胜平负中是最小的场次数 +  客胜时kelly在胜平负中是最小的场次数)/totalMatch
	 * winKellyMaxProp: 胜的一方的kelly是最大的比例:  (主胜时kelly在胜平负中是最大的场次数 + 客胜时的kelly在胜平负中是最大的场次数)/totalMatch
	 * winKellyLessThanOneProp: 胜的一方的kelly < 1 的比例:  (主胜时kelly < 1的场次数 + 客胜时kelly < 1的场次数) / totalMatch.
	 * winKellyLessThanLRProp: 胜的一方的kelly < lossRatio 的比例:  (主胜时kelly < LR的场次数 + 客胜时kelly < LR的场次数) / totalMatch.
	 * winKellyLessThanLRRangeProp: 胜的一方的kelly < lossRatio + 0.03 的比例:  (主胜时kelly < LR+0.03的场次数 + 客胜时kelly < LR+0.03的场次数) / totalMatch.
	 * winKellyLessThanLRRange2Prop: 胜的一方的kelly < lossRatio - 0.03 的比例:  (主胜时kelly < LR-0.03的场次数 + 客胜时kelly < LR-0.03的场次数) / totalMatch.
	 */
	public void analyseEuroCorpsKelly() {
		// 先找出所有的公司.
		Set<String> allCorpNames = euroOddsService.queryAllCorpNames();
		if(allCorpNames == null){
			LOGGER.info("allCorpNames is null, return now...");
			return;
		}
		
		List<EuroCorpsKelly> euroCorpsKellyList = new ArrayList<EuroCorpsKelly>();
		for(String corpName : allCorpNames){
			analyseSingleCorpKelly(corpName, euroCorpsKellyList);
		}
		EuroCorpsKellyComparator comparator1 = new EuroCorpsKellyComparator(false, "totalMatch");
		Collections.sort(euroCorpsKellyList, comparator1);
		display(euroCorpsKellyList, comparator1);
		
		EuroCorpsKellyComparator comparator2 = new EuroCorpsKellyComparator(false, "winKellyMinProp");
		Collections.sort(euroCorpsKellyList, comparator2);
		display(euroCorpsKellyList, comparator2);
		
		EuroCorpsKellyComparator comparator5 = new EuroCorpsKellyComparator(false, "winKellyMaxProp");
		Collections.sort(euroCorpsKellyList, comparator5);
		display(euroCorpsKellyList, comparator5);
		
		EuroCorpsKellyComparator comparator3 = new EuroCorpsKellyComparator(false, "winKellyLessThanOneProp");
		Collections.sort(euroCorpsKellyList, comparator3);
		display(euroCorpsKellyList, comparator3);
		
		EuroCorpsKellyComparator comparator4 = new EuroCorpsKellyComparator(false, "winKellyLessThanLRProp");
		Collections.sort(euroCorpsKellyList, comparator4);
		display(euroCorpsKellyList, comparator4);
		
		EuroCorpsKellyComparator comparator6 = new EuroCorpsKellyComparator(false, "winKellyLessThanLRRangeProp");
		Collections.sort(euroCorpsKellyList, comparator6);
		display(euroCorpsKellyList, comparator6);
		
		EuroCorpsKellyComparator comparator7 = new EuroCorpsKellyComparator(false, "winKellyLessThanLRRange2Prop");
		Collections.sort(euroCorpsKellyList, comparator7);
		display(euroCorpsKellyList, comparator7);
	}
	
	/**
	 * 分析LOT_ODDS_EURO:
	 * hostKellyMinHostProb: 主胜的kelly指数是最小时主胜的比例: 主胜的kelly指数是最小时主胜的场次数/主胜的kelly指数是最小的场次数.
	 * visitingKellyMinVisitingProb: 客胜的kelly指数是最小时客胜的比例: 客胜的kelly指数是最小时客胜的场次数/客胜的kelly指数是最小的场次数.
	 */
	public void analyseEuroCorpsResult() {
		// 先找出所有的公司.
		Set<String> allCorpNames = euroOddsService.queryAllCorpNames();
		if(allCorpNames == null){
			LOGGER.info("allCorpNames is null, return now...");
			return;
		}
		
		List<EuroCorpsKelly> euroCorpsKellyList = new ArrayList<EuroCorpsKelly>();
		for(String corpName : allCorpNames){
			analyseSingleCorpResult(corpName, euroCorpsKellyList);
		}
		EuroCorpsKellyComparator comparator1 = new EuroCorpsKellyComparator(false, "hostKellyMinHostProb");
		Collections.sort(euroCorpsKellyList, comparator1);
		display2(euroCorpsKellyList, comparator1);
		
		EuroCorpsKellyComparator comparator2 = new EuroCorpsKellyComparator(false, "visitingKellyMinVisitingProb");
		Collections.sort(euroCorpsKellyList, comparator2);
		display2(euroCorpsKellyList, comparator2);
	}
	


	
	private void analyseSingleCorpResult(String corpName, List<EuroCorpsKelly> euroCorpsKellyList){
		List<EuropeOdds> allEuroOdds = euroOddsService.queryEuroOddsByCorpName(corpName);
		Integer totalKellyMinHost = 0;
		Integer totalKellyMinVisiting = 0;
		int hostKellyMinHost = 0;
		int hostKellyMinEven = 0;
		int hostKellyMinVisiting = 0;
		
		int visitingKellyMinHost = 0;
		int visitingKellyMinEven = 0;
		int visitingKellyMinVisiting = 0;
		
		for(EuropeOdds euroOdds : allEuroOdds){
			Float hostKelly = euroOdds.getHostKelly();
			Float evenKelly = euroOdds.getEvenKelly();
			Float visitingKelly = euroOdds.getVisitingKelly();
			Integer hostGoals = euroOdds.getHostGoals();
			Integer visitingGoals = euroOdds.getVisitingGoals();
			if(hostKelly < evenKelly && hostKelly < visitingKelly){
				totalKellyMinHost++;
				if(hostGoals > visitingGoals){
					hostKellyMinHost++;
				}else if(hostGoals == visitingGoals){
					hostKellyMinEven++;
				}else if(hostGoals < visitingGoals){
					hostKellyMinVisiting++;
				}
			}
			
			if(visitingKelly < evenKelly && visitingKelly < hostKelly){
				totalKellyMinVisiting++;
				if(hostGoals > visitingGoals){
					visitingKellyMinHost++;
				}else if(hostGoals == visitingGoals){
					visitingKellyMinEven++;
				}else if(hostGoals < visitingGoals){
					visitingKellyMinVisiting++;
				}
			}
			
		}
		EuroCorpsKelly euroCorpsKelly = new EuroCorpsKelly();
		euroCorpsKelly.setCorpName(corpName);
		euroCorpsKelly.setTotalKellyMinHost(totalKellyMinHost);
		euroCorpsKelly.setTotalKellyMinVisiting(totalKellyMinVisiting);
		euroCorpsKelly.setHostKellyMinHost(hostKellyMinHost);
		euroCorpsKelly.setHostKellyMinEven(hostKellyMinEven);
		euroCorpsKelly.setHostKellyMinVisiting(hostKellyMinVisiting);
		euroCorpsKelly.setVisitingKellyMinHost(visitingKellyMinHost);
		euroCorpsKelly.setVisitingKellyMinEven(visitingKellyMinEven);
		euroCorpsKelly.setVisitingKellyMinVisiting(visitingKellyMinVisiting);
		euroCorpsKelly.setHostKellyMinHostProb(Math.round(hostKellyMinHost/totalKellyMinHost.floatValue() * 100)/100.0f);
		euroCorpsKelly.setVisitingKellyMinVisitingProb(Math.round(visitingKellyMinVisiting/totalKellyMinVisiting.floatValue() * 100) / 100.0f);
		
		euroCorpsKellyList.add(euroCorpsKelly);
	}
	
	private void analyseSingleCorpKelly(String corpName, List<EuroCorpsKelly> euroCorpsKellyList){
		List<EuropeOdds> allEuroOdds = euroOddsService.queryEuroOddsByCorpName(corpName);
		if(allEuroOdds == null){
			return;
		}
		
		// 该公司参与的所有的比赛个数.
		Integer totalMatch = 0;
		// 赢的比赛的kelly是最小的比赛个数.
		Integer winKellyMin = 0;
		// 赢的比赛的kelly < 1 的比赛个数.
		Integer winKellyLessThanOne = 0;
		// 赢的比赛的kelly < lossRatio 的比赛个数.
		Integer winKellyLessThanLR = 0;
		// 赢的比赛的kelly是最大的比赛个数.
		Integer winKellyMax = 0;
		// 赢的比赛的kelly < lossRatio + 0.3
		Integer winKellyLessThanLRRange = 0;
		// 赢的比赛的kelly < lossRatio - 0.2
		Integer winKellyLessThanLRRange2 = 0;
		
		for(EuropeOdds euroOdds : allEuroOdds){
			totalMatch++;
			Float hostKelly = euroOdds.getHostKelly();
			Float evenKelly = euroOdds.getEvenKelly();
			Float visitingKelly = euroOdds.getVisitingKelly();
			Float lossRatio = euroOdds.getLossRatio();
			Integer hostGoals = euroOdds.getHostGoals();
			Integer visitingGoals = euroOdds.getVisitingGoals();
			// 主胜情况下: kelly的取值情况.
			if(hostGoals > visitingGoals){
				if (hostKelly < evenKelly && hostKelly < visitingKelly){
					winKellyMin++;
				}
				if(hostKelly > evenKelly && hostKelly > visitingKelly){
					winKellyMax++;
				}
				if(hostKelly < 1){
					winKellyLessThanOne++;
				}
				if(hostKelly < lossRatio){
					winKellyLessThanLR++;
				}
				if(hostKelly < lossRatio + 0.03){
					winKellyLessThanLRRange++;
				}
				if(hostKelly < lossRatio - 0.03){
					winKellyLessThanLRRange2++;
				}
			}
			
			// 客胜情况下: kelly的取值情况.
			if(hostGoals < visitingGoals){
				if(visitingKelly < hostKelly && visitingKelly < evenKelly){
					winKellyMin++;
				}
				if(visitingKelly > hostKelly && visitingKelly > evenKelly){
					winKellyMax++;
				}
				if(visitingKelly < 1){
					winKellyLessThanOne++;
				}
				if(visitingKelly < lossRatio){
					winKellyLessThanLR++;
				}
				if(visitingKelly < lossRatio + 0.03){
					winKellyLessThanLRRange++;
				}
				if(visitingKelly < lossRatio - 0.03){
					winKellyLessThanLRRange2++;
				}
			}
		}
		
		EuroCorpsKelly euroCorpsKelly = new EuroCorpsKelly();
		euroCorpsKelly.setCorpName(corpName);
		euroCorpsKelly.setTotalMatch(totalMatch);
		euroCorpsKelly.setWinKellyMin(winKellyMin);
		euroCorpsKelly.setWinKellyMax(winKellyMax);
		euroCorpsKelly.setWinKellyLessThanOne(winKellyLessThanOne);
		euroCorpsKelly.setWinKellyLessThanLR(winKellyLessThanLR);
		euroCorpsKelly.setWinKellyLessThanLRRange(winKellyLessThanLRRange);
		euroCorpsKelly.setWinKellyLessThanLRRange2(winKellyLessThanLRRange2);
		
		euroCorpsKelly.setWinKellyMinProp(Math.round(winKellyMin/totalMatch.floatValue() * 100)/100.0f);
		euroCorpsKelly.setWinKellyMaxProp(Math.round(winKellyMax/totalMatch.floatValue() * 100)/100.0f);
		euroCorpsKelly.setWinKellyLessThanLRProp(Math.round(winKellyLessThanLR/totalMatch.floatValue() * 100) / 100.0f);
		euroCorpsKelly.setWinKellyLessThanOneProp(Math.round(winKellyLessThanOne/totalMatch.floatValue() * 100) / 100.0f);
		euroCorpsKelly.setWinKellyLessThanLRRangeProp(Math.round(winKellyLessThanLRRange/totalMatch.floatValue() * 100) / 100.0f);
		euroCorpsKelly.setWinKellyLessThanLRRange2Prop(Math.round(winKellyLessThanLRRange2/totalMatch.floatValue() * 100) / 100.0f);
		euroCorpsKellyList.add(euroCorpsKelly);
	}
	
	private void display(List<EuroCorpsKelly> euroCorpsKellyList, EuroCorpsKellyComparator comparator){
		StringBuilder sb = new StringBuilder("\n" + comparator.getComType() + "\n");
		int seq = 1;
		for(EuroCorpsKelly kelly : euroCorpsKellyList){
			Formatter formatter = new Formatter();
			formatter.format("%5s %25s %10s %15s %5s %15s %5s %15s %5s %15s %5s %15s %5s %15s %5s\n", 
					seq++,
					kelly.getCorpName(),
					"Total:" + kelly.getTotalMatch(),
					"winKMin:" + kelly.getWinKellyMin(),
					kelly.getWinKellyMinProp(),
					"winKMax:" + kelly.getWinKellyMax(),
					kelly.getWinKellyMaxProp(),
					"winKLTO:" + kelly.getWinKellyLessThanOne(),
					kelly.getWinKellyLessThanOneProp(),
					"winKLTLR:" + kelly.getWinKellyLessThanLR(),
					kelly.getWinKellyLessThanLRProp(),
					"winKLTLRR:" + kelly.getWinKellyLessThanLRRange(),
					kelly.getWinKellyLessThanLRRangeProp(),
					"winKLTLRR2:" + kelly.getWinKellyLessThanLRRange2(),
					kelly.getWinKellyLessThanLRRange2Prop());
			sb.append(formatter.toString());
			formatter.close();
		}
		LOGGER.info(sb.toString());
	}
	
	private void display2(List<EuroCorpsKelly> euroCorpsKellyList, EuroCorpsKellyComparator comparator){
		StringBuilder sb = new StringBuilder("\n" + comparator.getComType() + "\n");
		int seq = 1;
		for(EuroCorpsKelly kelly : euroCorpsKellyList){
			Formatter formatter = new Formatter();
			formatter.format("%5s %25s %15s %6s %6s %6s %5s %15s %6s %6s %6s %5s\n", 
					seq++,
					kelly.getCorpName(),
					"TotalKMH:" + kelly.getTotalKellyMinHost(),
					"H:" + kelly.getHostKellyMinHost(),
					"E:" + kelly.getHostKellyMinEven(),
					"V:" + kelly.getHostKellyMinVisiting(),
					kelly.getHostKellyMinHostProb(),
					"TotalKMV:" + kelly.getTotalKellyMinVisiting(),
					"H:" + kelly.getVisitingKellyMinHost(),
					"E:" + kelly.getVisitingKellyMinEven(),
					"V:" + kelly.getVisitingKellyMinVisiting(),
					kelly.getVisitingKellyMinVisitingProb()
					);
			sb.append(formatter.toString());
			formatter.close();
		}
		LOGGER.info(sb.toString());
	}
	

	
	class EuroCorpsKelly{
		private String corpName;
		private int totalMatch;
		private int winKellyMin;
		private int winKellyMax;
		private int winKellyLessThanOne;
		private int winKellyLessThanLR;
		private int winKellyLessThanLRRange;
		private int winKellyLessThanLRRange2;
		private Float winKellyMinProp = 0f;
		private Float winKellyMaxProp = 0f;
		private Float winKellyLessThanOneProp = 0f;
		private Float winKellyLessThanLRProp = 0f;
		private Float winKellyLessThanLRRangeProp = 0f;
		private Float winKellyLessThanLRRange2Prop = 0f;
		
		// for analyseSingleCorpResult begin
		private int totalKellyMinHost = 0;
		private int totalKellyMinEven = 0;
		private int totalKellyMinVisiting = 0;
		private int hostKellyMinHost = 0;
		private int hostKellyMinEven = 0;
		private int hostKellyMinVisiting = 0;
		private int visitingKellyMinHost = 0;
		private int visitingKellyMinEven = 0;
		private int visitingKellyMinVisiting = 0;
		private Float hostKellyMinHostProb = 0f;
		private Float visitingKellyMinVisitingProb = 0f;
		// for analyseSingleCorpResult end
		
		public String getCorpName() {
			return corpName;
		}
		public void setCorpName(String corpName) {
			this.corpName = corpName;
		}
		public int getTotalMatch() {
			return totalMatch;
		}
		public void setTotalMatch(int totalMatch) {
			this.totalMatch = totalMatch;
		}
		public int getWinKellyMin() {
			return winKellyMin;
		}
		public void setWinKellyMin(int winKellyMin) {
			this.winKellyMin = winKellyMin;
		}
		public int getWinKellyLessThanOne() {
			return winKellyLessThanOne;
		}
		public void setWinKellyLessThanOne(int winKellyLessThanOne) {
			this.winKellyLessThanOne = winKellyLessThanOne;
		}
		public int getWinKellyLessThanLR() {
			return winKellyLessThanLR;
		}
		public void setWinKellyLessThanLR(int winKellyLessThanLR) {
			this.winKellyLessThanLR = winKellyLessThanLR;
		}
		public Float getWinKellyMinProp() {
			return winKellyMinProp;
		}
		public void setWinKellyMinProp(Float winKellyMinProp) {
			this.winKellyMinProp = winKellyMinProp;
		}
		public Float getWinKellyLessThanOneProp() {
			return winKellyLessThanOneProp;
		}
		public void setWinKellyLessThanOneProp(Float winKellyLessThanOneProp) {
			this.winKellyLessThanOneProp = winKellyLessThanOneProp;
		}
		public Float getWinKellyLessThanLRProp() {
			return winKellyLessThanLRProp;
		}
		public void setWinKellyLessThanLRProp(Float winKellyLessThanLRProp) {
			this.winKellyLessThanLRProp = winKellyLessThanLRProp;
		}
		public int getWinKellyMax() {
			return winKellyMax;
		}
		public void setWinKellyMax(int winKellyMax) {
			this.winKellyMax = winKellyMax;
		}
		public Float getWinKellyMaxProp() {
			return winKellyMaxProp;
		}
		public void setWinKellyMaxProp(Float winKellyMaxProp) {
			this.winKellyMaxProp = winKellyMaxProp;
		}
		public int getWinKellyLessThanLRRange() {
			return winKellyLessThanLRRange;
		}
		public void setWinKellyLessThanLRRange(int winKellyLessThanLRRange) {
			this.winKellyLessThanLRRange = winKellyLessThanLRRange;
		}
		public Float getWinKellyLessThanLRRangeProp() {
			return winKellyLessThanLRRangeProp;
		}
		public void setWinKellyLessThanLRRangeProp(Float winKellyLessThanLRRangeProp) {
			this.winKellyLessThanLRRangeProp = winKellyLessThanLRRangeProp;
		}
		public int getWinKellyLessThanLRRange2() {
			return winKellyLessThanLRRange2;
		}
		public void setWinKellyLessThanLRRange2(int winKellyLessThanLRRange2) {
			this.winKellyLessThanLRRange2 = winKellyLessThanLRRange2;
		}
		public Float getWinKellyLessThanLRRange2Prop() {
			return winKellyLessThanLRRange2Prop;
		}
		public void setWinKellyLessThanLRRange2Prop(Float winKellyLessThanLRRange2Prop) {
			this.winKellyLessThanLRRange2Prop = winKellyLessThanLRRange2Prop;
		}
		public int getTotalKellyMinHost() {
			return totalKellyMinHost;
		}
		public void setTotalKellyMinHost(int totalKellyMinHost) {
			this.totalKellyMinHost = totalKellyMinHost;
		}
		public int getTotalKellyMinEven() {
			return totalKellyMinEven;
		}
		public void setTotalKellyMinEven(int totalKellyMinEven) {
			this.totalKellyMinEven = totalKellyMinEven;
		}
		public int getTotalKellyMinVisiting() {
			return totalKellyMinVisiting;
		}
		public void setTotalKellyMinVisiting(int totalKellyMinVisiting) {
			this.totalKellyMinVisiting = totalKellyMinVisiting;
		}
		public int getHostKellyMinHost() {
			return hostKellyMinHost;
		}
		public void setHostKellyMinHost(int hostKellyMinHost) {
			this.hostKellyMinHost = hostKellyMinHost;
		}
		public int getHostKellyMinEven() {
			return hostKellyMinEven;
		}
		public void setHostKellyMinEven(int hostKellyMinEven) {
			this.hostKellyMinEven = hostKellyMinEven;
		}
		public int getHostKellyMinVisiting() {
			return hostKellyMinVisiting;
		}
		public void setHostKellyMinVisiting(int hostKellyMinVisiting) {
			this.hostKellyMinVisiting = hostKellyMinVisiting;
		}
		public int getVisitingKellyMinHost() {
			return visitingKellyMinHost;
		}
		public void setVisitingKellyMinHost(int visitingKellyMinHost) {
			this.visitingKellyMinHost = visitingKellyMinHost;
		}
		public int getVisitingKellyMinEven() {
			return visitingKellyMinEven;
		}
		public void setVisitingKellyMinEven(int visitingKellyMinEven) {
			this.visitingKellyMinEven = visitingKellyMinEven;
		}
		public int getVisitingKellyMinVisiting() {
			return visitingKellyMinVisiting;
		}
		public void setVisitingKellyMinVisiting(int visitingKellyMinVisiting) {
			this.visitingKellyMinVisiting = visitingKellyMinVisiting;
		}
		public Float getHostKellyMinHostProb() {
			return hostKellyMinHostProb;
		}
		public void setHostKellyMinHostProb(Float hostKellyMinHostProb) {
			this.hostKellyMinHostProb = hostKellyMinHostProb;
		}
		public Float getVisitingKellyMinVisitingProb() {
			return visitingKellyMinVisitingProb;
		}
		public void setVisitingKellyMinVisitingProb(Float visitingKellyMinVisitingProb) {
			this.visitingKellyMinVisitingProb = visitingKellyMinVisitingProb;
		}
		
	}
	

	
	private class EuroCorpsKellyComparator implements Comparator<EuroCorpsKelly>{
		private boolean isAsc = true;
		private String comType = "totalMatch";
		EuroCorpsKellyComparator(boolean asc, String type){
			isAsc = asc;
			comType = type;
		}
		public int compare(EuroCorpsKelly o1, EuroCorpsKelly o2) {
			if(isAsc){
				if("totalMatch".equals(comType)){
					return o1.getTotalMatch() - o2.getTotalMatch();
				}else if("winKellyMin".equals(comType)){
					return o1.getWinKellyMin() - o2.getWinKellyMin();
				}else if("winKellyLessThanOne".equals(comType)){
					return o1.getWinKellyLessThanOne() - o2.getWinKellyLessThanOne();
				}else if("winKellyLessThanLR".equals(comType)){
					return o1.getWinKellyLessThanLR() - o2.getWinKellyLessThanLR();
				}else if("winKellyMinProp".equals(comType)){
					return o1.getWinKellyMinProp().compareTo(o2.getWinKellyMinProp());
				}else if("winKellyMaxProp".equals(comType)){
					return o1.getWinKellyMaxProp().compareTo(o2.getWinKellyMaxProp());
				}else if("winKellyLessThanOneProp".equals(comType)){
					return o1.getWinKellyLessThanOneProp().compareTo(o2.getWinKellyLessThanOneProp());
				}else if("winKellyLessThanLRProp".equals(comType)){
					return o1.getWinKellyLessThanLRProp().compareTo(o2.getWinKellyLessThanLRProp());
				}else if("winKellyLessThanLRRangeProp".equals(comType)){
					return o1.getWinKellyLessThanLRRangeProp().compareTo(o2.getWinKellyLessThanLRRangeProp());
				}else if("winKellyLessThanLRRange2Prop".equals(comType)){
					return o1.getWinKellyLessThanLRRange2Prop().compareTo(o2.getWinKellyLessThanLRRange2Prop());
				}else if("hostKellyMinHostProb".equals(comType)){
					return o1.getHostKellyMinHostProb().compareTo(o2.getHostKellyMinHostProb());
				}else if("visitingKellyMinVisitingProb".equals(comType)){
					return o1.getVisitingKellyMinVisitingProb().compareTo(o2.getVisitingKellyMinVisitingProb());
				}
			}else{
				if("totalMatch".equals(comType)){
					return o2.getTotalMatch() - o1.getTotalMatch();
				}else if("winKellyMin".equals(comType)){
					return o2.getWinKellyMin() - o1.getWinKellyMin();
				}else if("winKellyLessThanOne".equals(comType)){
					return o2.getWinKellyLessThanOne() - o1.getWinKellyLessThanOne();
				}else if("winKellyLessThanLR".equals(comType)){
					return o2.getWinKellyLessThanLR() - o1.getWinKellyLessThanLR();
				}else if("winKellyMinProp".equals(comType)){
					return o2.getWinKellyMinProp().compareTo(o1.getWinKellyMinProp());
				}else if("winKellyMaxProp".equals(comType)){
					return o2.getWinKellyMaxProp().compareTo(o1.getWinKellyMaxProp());
				}else if("winKellyLessThanOneProp".equals(comType)){
					return o2.getWinKellyLessThanOneProp().compareTo(o1.getWinKellyLessThanOneProp());
				}else if("winKellyLessThanLRProp".equals(comType)){
					return o2.getWinKellyLessThanLRProp().compareTo(o1.getWinKellyLessThanLRProp());
				}else if("winKellyLessThanLRRangeProp".equals(comType)){
					return o2.getWinKellyLessThanLRRangeProp().compareTo(o1.getWinKellyLessThanLRRangeProp());
				}else if("winKellyLessThanLRRange2Prop".equals(comType)){
					return o2.getWinKellyLessThanLRRange2Prop().compareTo(o1.getWinKellyLessThanLRRange2Prop());
				}else if("hostKellyMinHostProb".equals(comType)){
					return o2.getHostKellyMinHostProb().compareTo(o1.getHostKellyMinHostProb());
				}else if("visitingKellyMinVisitingProb".equals(comType)){
					return o2.getVisitingKellyMinVisitingProb().compareTo(o1.getVisitingKellyMinVisitingProb());
				}
			}
			return o1.getTotalMatch() - o2.getTotalMatch();
		}
		public String getComType() {
			return comType;
		}
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

}
