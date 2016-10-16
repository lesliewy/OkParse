/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * 欧盘赔率(http://www.okooo.com/soccer/match/686923/odds/)
 * 
 * @author leslie
 *
 */
public class EuropeOdds {
	
	String okUrlDate;
	
	Integer matchSeq;
	
	/**
	 * 博彩公司的编号.
	 */
	private String oddsCorpNo;
	
	/**
	 * 博彩公司名称
	 */
	private String oddsCorpName;
	
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
	 * 初始开盘的时间, 用距离比赛的时间来表示.
	 */
	private String initTime;
	
	/**
	 * 初始主胜赔率.
	 */
	private Float initHostOdds;
	
	/**
	 * 初始平陪
	 */
	private Float initEvenOdds;
	
	/**
	 * 初始客胜赔率
	 */
	private Float initVisitingOdds;
	
	private Float hostKelly;
	
	private Float evenKelly;
	
	private Float visitingKelly;
	
	/**
	 * 赔付率.
	 */
	private Float lossRatio;
	
	/**
	 * 时间戳.
	 */
	private Timestamp timestamp;
	
	/**
	 * 用于联表查询结果返回
	 */
	private Integer hostGoals;
	private Integer visitingGoals;
	
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

	public String getOddsCorpNo() {
		return oddsCorpNo;
	}

	public void setOddsCorpNo(String oddsCorpNo) {
		this.oddsCorpNo = oddsCorpNo;
	}

	public String getOddsCorpName() {
		return oddsCorpName;
	}

	public void setOddsCorpName(String oddsCorpName) {
		this.oddsCorpName = oddsCorpName;
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

	public String getInitTime() {
		return initTime;
	}

	public void setInitTime(String initTime) {
		this.initTime = initTime;
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

	public Integer getHostGoals() {
		return hostGoals;
	}

	public void setHostGoals(Integer hostGoals) {
		this.hostGoals = hostGoals;
	}

	public Integer getVisitingGoals() {
		return visitingGoals;
	}

	public void setVisitingGoals(Integer visitingGoals) {
		this.visitingGoals = visitingGoals;
	}
	
}
