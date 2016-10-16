/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;
import java.util.List;

/**
 * 亚盘变化表 LOT_ODDS_ASIA_CHANGE
 * 
 * @author leslie
 *
 */
public class AsiaOddsChange {
	/**
	 * 欧陪变化表自增长主键.
	 */
	private Long id;
	
	/**
	 * okooo 的 matchId, 唯一.
	 */
	private Long okMatchId;
	
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
	 * 采集时的盘口: 受平半: -0.25 , 平: 0  平半: 0.25  半球: 0.5 半一: 0.75
	 */
	private Float handicap;
	
	/**
	 * 采集时的主队盘口赔率.
	 */
	private Float hostOdds;
	
	/**
	 * 采集时的客胜赔率..
	 */
	private Float visitingOdds;
	
	/**
	 * 时间戳
	 */
	private Timestamp timestamp;
	
	/**
	 * 变赔次数
	 */
	private Integer changeNum;
	
	private String okUrlDate;
	
	private Integer matchSeq;
	
	private String jobType;
	
	private List<Integer> matchSeqsInSql;

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

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getChangeNum() {
		return changeNum;
	}

	public void setChangeNum(Integer changeNum) {
		this.changeNum = changeNum;
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

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public List<Integer> getMatchSeqsInSql() {
		return matchSeqsInSql;
	}

	public void setMatchSeqsInSql(List<Integer> matchSeqsInSql) {
		this.matchSeqsInSql = matchSeqsInSql;
	}

}
