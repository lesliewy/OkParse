/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * LOT_KELLY_RESULT
 * @author leslie
 *
 */
public class KellyCorpResult {

	private String okUrlDate;

	private String matchName;
	
	private String oddsCorpName;
	
	private Long count;
	
	private Long winCount;
	
	private Long evenCount;
	
	private Long negaCount;
	
	private String allSeq;
	
	private String winSeq;
	
	private String evenSeq;
	
	private String negaSeq;
	
	private Double winProb;
	
	private Double evenProb;
	
	private Double negaProb;
	
	private Timestamp timestamp;
	
	private String ruleType;

	public String getOkUrlDate() {
		return okUrlDate;
	}

	public void setOkUrlDate(String okUrlDate) {
		this.okUrlDate = okUrlDate;
	}

	public String getMatchName() {
		return matchName;
	}

	public void setMatchName(String matchName) {
		this.matchName = matchName;
	}

	public String getOddsCorpName() {
		return oddsCorpName;
	}

	public void setOddsCorpName(String oddsCorpName) {
		this.oddsCorpName = oddsCorpName;
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

	public String getAllSeq() {
		return allSeq;
	}

	public void setAllSeq(String allSeq) {
		this.allSeq = allSeq;
	}

	public String getWinSeq() {
		return winSeq;
	}

	public void setWinSeq(String winSeq) {
		this.winSeq = winSeq;
	}

	public String getEvenSeq() {
		return evenSeq;
	}

	public void setEvenSeq(String evenSeq) {
		this.evenSeq = evenSeq;
	}

	public String getNegaSeq() {
		return negaSeq;
	}

	public void setNegaSeq(String negaSeq) {
		this.negaSeq = negaSeq;
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

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}
	
}
