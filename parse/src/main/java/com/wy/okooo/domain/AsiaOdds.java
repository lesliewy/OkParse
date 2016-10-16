/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * 亚盘赔率(http://www.okooo.com/soccer/match/680757/ah/)
 * 
 * @author leslie
 *
 */
public class AsiaOdds {
	/**
	 * 自增长主键.
	 */
	private Long id;
	
	/**
	 * okooo 的 matchId, 唯一.
	 */
	private Long okMatchId;
	
	/**
	 * 博彩公司名称
	 */
	private String oddsCorpName;
	
	/**
	 * 采集时的盘口: 受平半: -0.25 , 平: 0  平半: 0.25  半球: 0.5 半一: 0.75
	 */
	private Float handicap;
	
	/**
	 * 采集时的主队盘口赔率
	 */
	private Float hostOdds;
	
	/**
	 * 采集时的客队盘口赔率.
	 */
	private Float visitingOdds;
	
	/**
	 * 初始开盘的时间, 用距离比赛的时间来表示.
	 */
	private String initTime;
	
	/**
	 * 初始盘口
	 */
	private Float initHandicap;
	
	/**
	 * 初始主胜盘口.
	 */
	private Float initHostOdds;
	
	/**
	 * 初始客胜盘口
	 */
	private Float initVisitingOdds;
	
	/**
	 * 赔付率.
	 */
	private Float lossRatio;
	
	/**
	 * 时间戳.
	 */
	private Timestamp timestamp;
	
	private Float hostKelly;
	
	private Float visitingKelly;
	
	private Integer matchSeq;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOkMatchId() {
		return okMatchId;
	}

	public void setOkMatchId(Long okMatchId) {
		this.okMatchId = okMatchId;
	}

	public String getOddsCorpName() {
		return oddsCorpName;
	}

	public void setOddsCorpName(String oddsCorpName) {
		this.oddsCorpName = oddsCorpName;
	}

	public Float getHandicap() {
		return handicap;
	}

	public void setHandicap(Float handicap) {
		this.handicap = handicap;
	}

	public Float getHostOdds() {
		return hostOdds;
	}

	public void setHostOdds(Float hostOdds) {
		this.hostOdds = hostOdds;
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

	public Float getInitHandicap() {
		return initHandicap;
	}

	public void setInitHandicap(Float initHandicap) {
		this.initHandicap = initHandicap;
	}

	public Float getInitHostOdds() {
		return initHostOdds;
	}

	public void setInitHostOdds(Float initHostOdds) {
		this.initHostOdds = initHostOdds;
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

	public Float getHostKelly() {
		return hostKelly;
	}

	public void setHostKelly(Float hostKelly) {
		this.hostKelly = hostKelly;
	}

	public Float getVisitingKelly() {
		return visitingKelly;
	}

	public void setVisitingKelly(Float visitingKelly) {
		this.visitingKelly = visitingKelly;
	}

	public Integer getMatchSeq() {
		return matchSeq;
	}

	public void setMatchSeq(Integer matchSeq) {
		this.matchSeq = matchSeq;
	}
	
}
