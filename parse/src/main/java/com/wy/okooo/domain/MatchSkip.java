/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * LOT_MATCH_SKIP
 * @author leslie
 *
 */
public class MatchSkip {
	private String okUrlDate;
	
	private Integer matchSeq;

	private Timestamp timestamp;

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

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}
