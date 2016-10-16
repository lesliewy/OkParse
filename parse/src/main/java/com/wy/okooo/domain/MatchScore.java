/**
 * 
 */
package com.wy.okooo.domain;

import java.util.Map;


/**
 * 用于计算比赛的得分, 得分越高, 对应结果的概率越大;  
 * 
 * @author leslie
 */
public class MatchScore {
	
	private String matchName;
	
	private Long okMatchId;
	
	private String okUrlDate;
	
	private Integer matchSeq;
	
	// 规则A的总分数
	private Double totalScoreA;
	
	// 规则A的分数的平均分;
	private Double averageA;
	
	// 规则B的分数
	private Double scoreB;
	
	private String ruleType;
	
	private String matchResult;
	
	private Integer hostGoals;
	
	private Integer visitingGoals;
	
	private Float hostOdds;
	
	private Float evenOdds;
	
	private Float visitingOdds;
	
	// 规则 C 中用到.
	private Map<String, Float> compIndexs;
	
	// 规则 K1 中用到.
	private KellyRule kellyRule;
	
	private String oddsCorpName;

	public String getMatchName() {
		return matchName;
	}

	public void setMatchName(String matchName) {
		this.matchName = matchName;
	}

	public Long getOkMatchId() {
		return okMatchId;
	}

	public void setOkMatchId(Long okMatchId) {
		this.okMatchId = okMatchId;
	}

	public String getOkUrlDate() {
		return okUrlDate;
	}

	public void setOkUrlDate(String okUrlDate) {
		this.okUrlDate = okUrlDate;
	}

	public Integer getMatchSeq() {
		return matchSeq;
	}

	public void setMatchSeq(Integer matchSeq) {
		this.matchSeq = matchSeq;
	}

	public Double getTotalScoreA() {
		return totalScoreA;
	}

	public void setTotalScoreA(Double totalScoreA) {
		this.totalScoreA = totalScoreA;
	}

	public Double getAverageA() {
		return averageA;
	}

	public void setAverageA(Double averageA) {
		this.averageA = averageA;
	}

	public Double getScoreB() {
		return scoreB;
	}

	public void setScoreB(Double scoreB) {
		this.scoreB = scoreB;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getMatchResult() {
		return matchResult;
	}

	public void setMatchResult(String matchResult) {
		this.matchResult = matchResult;
	}

	public Integer getHostGoals() {
		return hostGoals;
	}

	public void setHostGoals(Integer hostGoals) {
		this.hostGoals = hostGoals;
	}

	public Integer getVisitingGoals() {
		return visitingGoals;
	}

	public void setVisitingGoals(Integer visitingGoals) {
		this.visitingGoals = visitingGoals;
	}

	public Float getHostOdds() {
		return hostOdds;
	}

	public void setHostOdds(Float hostOdds) {
		this.hostOdds = hostOdds;
	}

	public Float getEvenOdds() {
		return evenOdds;
	}

	public void setEvenOdds(Float evenOdds) {
		this.evenOdds = evenOdds;
	}

	public Float getVisitingOdds() {
		return visitingOdds;
	}

	public void setVisitingOdds(Float visitingOdds) {
		this.visitingOdds = visitingOdds;
	}

	public Map<String, Float> getCompIndexs() {
		return compIndexs;
	}

	public void setCompIndexs(Map<String, Float> compIndexs) {
		this.compIndexs = compIndexs;
	}

	public KellyRule getKellyRule() {
		return kellyRule;
	}

	public void setKellyRule(KellyRule kellyRule) {
		this.kellyRule = kellyRule;
	}

	public String getOddsCorpName() {
		return oddsCorpName;
	}

	public void setOddsCorpName(String oddsCorpName) {
		this.oddsCorpName = oddsCorpName;
	}
	
}
