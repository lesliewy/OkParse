/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * 新的欧赔变化表 LOT_ODDS_EURO_CHANGE_ALL
 * 
 * @author leslie
 *
 */
public class EuropeOddsChangeAll {
	
	private String okUrlDate;
	
	private Integer matchSeq;
	
	/**
	 * 赔率公司名称.
	 */
	private String oddsCorpName;
	
	/**
	 * 开盘的序号.
	 */
	private Integer oddsSeq;
	
	/**
	 * 开盘时间
	 */
	private Timestamp oddsTime;
	
	/**
	 * 距离开赛时间， 例如  64.13:  表示距离比赛64小时13分钟.
	 */
	private String timeBeforeMatch;
	
	/**
	 * 采集时的主胜赔率.
	 */
	private Float hostOdds;
	
	/**
	 * 采集时的平陪.
	 */
	private Float evenOdds;
	
	/**
	 * 采集时的客胜赔率.
	 */
	private Float visitingOdds;
	
	/**
	 * 主胜概率
	 */
	private Float hostProb;
	
	/**
	 * 平概率
	 */
	private Float evenProb;
	
	/**
	 * 客胜概率
	 */
	private Float visitingProb;
	
	/**
	 * 主胜凯利指数
	 */
	private Float hostKelly;
	
	/**
	 * 平陪凯利指数
	 */
	private Float evenKelly;
	
	/**
	 * 客胜凯利指数
	 */
	private Float visitingKelly;
	
	/**
	 * 时间戳
	 */
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

	public String getOddsCorpName() {
		return oddsCorpName;
	}

	public void setOddsCorpName(String oddsCorpName) {
		this.oddsCorpName = oddsCorpName;
	}

	public Integer getOddsSeq() {
		return oddsSeq;
	}

	public void setOddsSeq(Integer oddsSeq) {
		this.oddsSeq = oddsSeq;
	}

	public Timestamp getOddsTime() {
		return oddsTime;
	}

	public void setOddsTime(Timestamp oddsTime) {
		this.oddsTime = oddsTime;
	}

	public String getTimeBeforeMatch() {
		return timeBeforeMatch;
	}

	public void setTimeBeforeMatch(String timeBeforeMatch) {
		this.timeBeforeMatch = timeBeforeMatch;
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

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

}
