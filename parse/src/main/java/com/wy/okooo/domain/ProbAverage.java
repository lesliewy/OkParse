package com.wy.okooo.domain;

import java.sql.Timestamp;
import java.util.List;

/**
 * LOT_PROB_AVG.
 * 
 * @author leslie
 *
 */
public class ProbAverage {
	
	private String okUrlDate;
	
	private Integer matchSeq;
	
	private String jobType;
	
	private Float hostProb;
	
	private Float evenProb;
	
	private Float visitingProb;
	
	private Timestamp timestamp;

	/**
	 * 用于sql查询.
	 */
	private List<Integer> matchSeqsInSql;
	
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

	public Float getHostProb() {
		return hostProb;
	}

	public void setHostProb(Float hostProb) {
		this.hostProb = hostProb;
	}

	public Float getEvenProb() {
		return evenProb;
	}

	public void setEvenProb(Float evenProb) {
		this.evenProb = evenProb;
	}

	public Float getVisitingProb() {
		return visitingProb;
	}

	public void setVisitingProb(Float visitingProb) {
		this.visitingProb = visitingProb;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public List<Integer> getMatchSeqsInSql() {
		return matchSeqsInSql;
	}

	public void setMatchSeqsInSql(List<Integer> matchSeqsInSql) {
		this.matchSeqsInSql = matchSeqsInSql;
	}

}
