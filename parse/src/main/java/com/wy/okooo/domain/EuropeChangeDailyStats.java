/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * LOT_EURO_CHANGE_DAILY_STATS
 * 
 * @author leslie
 *
 */
public class EuropeChangeDailyStats {
	
	private String okUrlDate;
	
	private String statsType;
	
	private String oddsCorpName;
	
	private Integer rank;
	
	private Integer totalMatches;
	
	private Integer hostMatches;
	
	private Integer evenMatches;
	
	private Integer visitingMatches;
	
	private Float prob;
	
	private Timestamp timestamp;

	public String getOkUrlDate() {
		return okUrlDate;
	}

	public void setOkUrlDate(String okUrlDate) {
		this.okUrlDate = okUrlDate;
	}

	public String getStatsType() {
		return statsType;
	}

	public void setStatsType(String statsType) {
		this.statsType = statsType;
	}

	public String getOddsCorpName() {
		return oddsCorpName;
	}

	public void setOddsCorpName(String oddsCorpName) {
		this.oddsCorpName = oddsCorpName;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public Integer getTotalMatches() {
		return totalMatches;
	}

	public void setTotalMatches(Integer totalMatches) {
		this.totalMatches = totalMatches;
	}

	public Integer getHostMatches() {
		return hostMatches;
	}

	public void setHostMatches(Integer hostMatches) {
		this.hostMatches = hostMatches;
	}

	public Integer getEvenMatches() {
		return evenMatches;
	}

	public void setEvenMatches(Integer evenMatches) {
		this.evenMatches = evenMatches;
	}

	public Integer getVisitingMatches() {
		return visitingMatches;
	}

	public void setVisitingMatches(Integer visitingMatches) {
		this.visitingMatches = visitingMatches;
	}

	public Float getProb() {
		return prob;
	}

	public void setProb(Float prob) {
		this.prob = prob;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}
