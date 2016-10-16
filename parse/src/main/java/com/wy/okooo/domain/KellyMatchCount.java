/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * LOT_KELLY_CORP_COUNT
 * 
 * @author leslie
 *
 */
public class KellyMatchCount {
	private String okUrlDate;
	
	private Integer matchSeq;
	
	private String jobType;
	
	private String ruleType;
	
	private Integer corpCount;
	
	private Timestamp timestamp;
	
	private String proLoss;
	
	private String extend1;
	
	private String extend2;
	
	private String jobFlag;

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

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public Integer getCorpCount() {
		return corpCount;
	}

	public void setCorpCount(Integer corpCount) {
		this.corpCount = corpCount;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getProLoss() {
		return proLoss;
	}

	public void setProLoss(String proLoss) {
		this.proLoss = proLoss;
	}

	public String getExtend1() {
		return extend1;
	}

	public void setExtend1(String extend1) {
		this.extend1 = extend1;
	}

	public String getExtend2() {
		return extend2;
	}

	public void setExtend2(String extend2) {
		this.extend2 = extend2;
	}

	public String getJobFlag() {
		return jobFlag;
	}

	public void setJobFlag(String jobFlag) {
		this.jobFlag = jobFlag;
	}
	
}
