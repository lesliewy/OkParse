/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * @author leslie
 *
 */
public class Corp {
	private String corpNo;
	
	private String corpName;
	
	/**
	 * LOT_ODDS_EURO_CHANGE 中 ODDS_SEQ = 2时的平均距离开赛时间 例如 64.5表示 64小时30.
	 */
	private Double 	euroTimeBeforeMatch;

	private Timestamp timestamp;

	public String getCorpNo() {
		return corpNo;
	}

	public void setCorpNo(String corpNo) {
		this.corpNo = corpNo;
	}

	public String getCorpName() {
		return corpName;
	}

	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Double getEuroTimeBeforeMatch() {
		return euroTimeBeforeMatch;
	}

	public void setEuroTimeBeforeMatch(Double euroTimeBeforeMatch) {
		this.euroTimeBeforeMatch = euroTimeBeforeMatch;
	}

}
