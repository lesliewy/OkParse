/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * 比分赔率(http://www.okooo.com/danchang/bifen/)
 * 
 * @author leslie
 *
 */
public class ScoreOdds {
	/**
	 * okooo 的 matchId, 唯一.
	 */
	private Long okMatchId;
	
	private String okUrlDate;
	
	private Integer matchSeq;
	
	private String winOdds;
	
	private String evenOdds;
	
	private String negaOdds;
	
	private String intervalType;
	
	private Timestamp timestamp;

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

	public String getWinOdds() {
		return winOdds;
	}

	public void setWinOdds(String winOdds) {
		this.winOdds = winOdds;
	}

	public String getEvenOdds() {
		return evenOdds;
	}

	public void setEvenOdds(String evenOdds) {
		this.evenOdds = evenOdds;
	}

	public String getNegaOdds() {
		return negaOdds;
	}

	public void setNegaOdds(String negaOdds) {
		this.negaOdds = negaOdds;
	}

	public String getIntervalType() {
		return intervalType;
	}

	public void setIntervalType(String intervalType) {
		this.intervalType = intervalType;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

}
