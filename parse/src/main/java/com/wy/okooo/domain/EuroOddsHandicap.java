package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * LOT_ODDS_EURO_HANDICAP.
 * 
 * @author leslie
 *
 */
public class EuroOddsHandicap {
	
	private String okUrlDate;
	
	private Integer matchSeq;
	
	private String jobType;
	
	private String oddsCorpName;
	
	// 让球值，都是整数，和亚盘不一样.
	private Integer euroHandicap;
	
	// 竞彩让球值. http://www.okooo.com/soccer/match/776908/hodds/ 页面竞彩下拉框的值.
	private Integer compHandicap;
	
	private Float initHostOdds;
	
	private Float initEvenOdds;
	
	private Float initVisitingOdds;
	
	private Float hostOdds;
	
	private Float evenOdds;
	
	private Float visitingOdds;
	
	private Float hostKelly;
	
	private Float evenKelly;
	
	private Float visitingKelly;
	
	private Float lossRatio;
	
	private Timestamp timestamp;
	
	// 列转行使用.
	private String allHostOdds;
	private String allEvenOdds;
	private String allVisitingOdds;
	private String allHostKelly;
	private String allEvenKelly;
	private String allVisitingKelly;

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

	public String getOddsCorpName() {
		return oddsCorpName;
	}

	public void setOddsCorpName(String oddsCorpName) {
		this.oddsCorpName = oddsCorpName;
	}

	public Integer getEuroHandicap() {
		return euroHandicap;
	}

	public void setEuroHandicap(Integer euroHandicap) {
		this.euroHandicap = euroHandicap;
	}

	public Integer getCompHandicap() {
		return compHandicap;
	}

	public void setCompHandicap(Integer compHandicap) {
		this.compHandicap = compHandicap;
	}

	public Float getInitHostOdds() {
		return initHostOdds;
	}

	public void setInitHostOdds(Float initHostOdds) {
		this.initHostOdds = initHostOdds;
	}

	public Float getInitEvenOdds() {
		return initEvenOdds;
	}

	public void setInitEvenOdds(Float initEvenOdds) {
		this.initEvenOdds = initEvenOdds;
	}

	public Float getInitVisitingOdds() {
		return initVisitingOdds;
	}

	public void setInitVisitingOdds(Float initVisitingOdds) {
		this.initVisitingOdds = initVisitingOdds;
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

	public Float getHostKelly() {
		return hostKelly;
	}

	public void setHostKelly(Float hostKelly) {
		this.hostKelly = hostKelly;
	}

	public Float getEvenKelly() {
		return evenKelly;
	}

	public void setEvenKelly(Float evenKelly) {
		this.evenKelly = evenKelly;
	}

	public Float getVisitingKelly() {
		return visitingKelly;
	}

	public void setVisitingKelly(Float visitingKelly) {
		this.visitingKelly = visitingKelly;
	}

	public Float getLossRatio() {
		return lossRatio;
	}

	public void setLossRatio(Float lossRatio) {
		this.lossRatio = lossRatio;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getAllHostOdds() {
		return allHostOdds;
	}

	public void setAllHostOdds(String allHostOdds) {
		this.allHostOdds = allHostOdds;
	}

	public String getAllEvenOdds() {
		return allEvenOdds;
	}

	public void setAllEvenOdds(String allEvenOdds) {
		this.allEvenOdds = allEvenOdds;
	}

	public String getAllVisitingOdds() {
		return allVisitingOdds;
	}

	public void setAllVisitingOdds(String allVisitingOdds) {
		this.allVisitingOdds = allVisitingOdds;
	}

	public String getAllHostKelly() {
		return allHostKelly;
	}

	public void setAllHostKelly(String allHostKelly) {
		this.allHostKelly = allHostKelly;
	}

	public String getAllEvenKelly() {
		return allEvenKelly;
	}

	public void setAllEvenKelly(String allEvenKelly) {
		this.allEvenKelly = allEvenKelly;
	}

	public String getAllVisitingKelly() {
		return allVisitingKelly;
	}

	public void setAllVisitingKelly(String allVisitingKelly) {
		this.allVisitingKelly = allVisitingKelly;
	}
	
}
