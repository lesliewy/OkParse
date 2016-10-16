/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * 按时间段分析亚盘赔率(http://www.okooo.com/soccer/match/680757/ah/)
 * 
 * @author leslie
 *
 */
public class AsiaOddsTrends {
	
	private String okUrlDate;
	
	private Integer matchSeq;
	
	/**
	 * 博彩公司名称
	 */
	private String oddsCorpName;
	
	private String jobType;
	
	private String matchName;
	
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
	 * 采集时的主队盘口赔率
	 */
	private Float hostOdds;
	
	/**
	 * 采集时的盘口: 受平半: -0.25 , 平: 0  平半: 0.25  半球: 0.5 半一: 0.75
	 */
	private Float handicap;
	
	/**
	 * 采集时的客队盘口赔率.
	 */
	private Float visitingOdds;
	
	/**
	 * 主胜凯利指数
	 */
	private Float hostKelly;
	
	/**
	 * 客胜凯利指数
	 */
	private Float visitingKelly;
	
	/**
	 * 赔付率.
	 */
	private Float lossRatio;
	
	/**
	 * 时间戳.
	 */
	private Timestamp timestamp;
	
	// 用于查询
	private Integer beginMatchSeq;
	
	// 用于查询
	private Integer endMatchSeq;
	
	// 列转行使用.
	private String allHostKelly;
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

	public String getOddsCorpName() {
		return oddsCorpName;
	}

	public void setOddsCorpName(String oddsCorpName) {
		this.oddsCorpName = oddsCorpName;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getMatchName() {
		return matchName;
	}

	public void setMatchName(String matchName) {
		this.matchName = matchName;
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

	public Float getHostOdds() {
		return hostOdds;
	}

	public void setHostOdds(Float hostOdds) {
		this.hostOdds = hostOdds;
	}

	public Float getHandicap() {
		return handicap;
	}

	public void setHandicap(Float handicap) {
		this.handicap = handicap;
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

	public Integer getBeginMatchSeq() {
		return beginMatchSeq;
	}

	public void setBeginMatchSeq(Integer beginMatchSeq) {
		this.beginMatchSeq = beginMatchSeq;
	}

	public Integer getEndMatchSeq() {
		return endMatchSeq;
	}

	public void setEndMatchSeq(Integer endMatchSeq) {
		this.endMatchSeq = endMatchSeq;
	}

	public String getAllHostKelly() {
		return allHostKelly;
	}

	public void setAllHostKelly(String allHostKelly) {
		this.allHostKelly = allHostKelly;
	}

	public String getAllVisitingKelly() {
		return allVisitingKelly;
	}

	public void setAllVisitingKelly(String allVisitingKelly) {
		this.allVisitingKelly = allVisitingKelly;
	}

}
