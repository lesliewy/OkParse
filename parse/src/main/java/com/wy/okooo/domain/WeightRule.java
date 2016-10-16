/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * LOT_WEIGHT_RULE  用于计算比赛的得分, 得分越高, 对应结果的概率越大;  
 * 
 * @author leslie
 */
public class WeightRule {
	
	private String rowNames;
	
	private Long id;
	
	private String oddsCorpName;
	
	private String matchName;
	
	private Double multiple;
	
	private Long count;
	
	private Long winCount;
	
	private Long evenCount;
	
	private Long negaCount;
	
	private Double winProb;
	
	private Double evenProb;
	
	private Double negaProb;
	
	private String ruleType;
	
	private Timestamp timestamp;

	public String getRowNames() {
		return rowNames;
	}

	public void setRowNames(String rowNames) {
		this.rowNames = rowNames;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOddsCorpName() {
		return oddsCorpName;
	}

	public void setOddsCorpName(String oddsCorpName) {
		this.oddsCorpName = oddsCorpName;
	}

	public String getMatchName() {
		return matchName;
	}

	public void setMatchName(String matchName) {
		this.matchName = matchName;
	}

	public Double getMultiple() {
		return multiple;
	}

	public void setMultiple(Double multiple) {
		this.multiple = multiple;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Long getWinCount() {
		return winCount;
	}

	public void setWinCount(Long winCount) {
		this.winCount = winCount;
	}

	public Long getEvenCount() {
		return evenCount;
	}

	public void setEvenCount(Long evenCount) {
		this.evenCount = evenCount;
	}

	public Long getNegaCount() {
		return negaCount;
	}

	public void setNegaCount(Long negaCount) {
		this.negaCount = negaCount;
	}

	public Double getWinProb() {
		return winProb;
	}

	public void setWinProb(Double winProb) {
		this.winProb = winProb;
	}

	public Double getEvenProb() {
		return evenProb;
	}

	public void setEvenProb(Double evenProb) {
		this.evenProb = evenProb;
	}

	public Double getNegaProb() {
		return negaProb;
	}

	public void setNegaProb(Double negaProb) {
		this.negaProb = negaProb;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}
