/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * LOT_KELLY_RULE;  
 * 
 * @author leslie
 */
public class KellyRule {
	
	private String rowNames;
	
	private String corpNo;
	
	private String oddsCorpName;
	
	private String matchName;
	
	private Long count;
	
	private Long winCount;
	
	private Long evenCount;
	
	private Long negaCount;
	
	private Double winProb;
	
	private Double evenProb;
	
	private Double negaProb;
	
	private String ruleType;
	
	private Timestamp timestamp;
	
	private String timeFlag;

	public String getRowNames() {
		return rowNames;
	}

	public void setRowNames(String rowNames) {
		this.rowNames = rowNames;
	}

	public String getCorpNo() {
		return corpNo;
	}

	public void setCorpNo(String corpNo) {
		this.corpNo = corpNo;
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

	public String getTimeFlag() {
		return timeFlag;
	}

	public void setTimeFlag(String timeFlag) {
		this.timeFlag = timeFlag;
	}
	
}
