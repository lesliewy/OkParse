package com.wy.okooo.domain;

import java.sql.Timestamp;

public class Match {
	private Long id;
	
	private Long okMatchId;
	
	private String matchName;
	
	private Integer matchSeq;
	
	private Timestamp matchTime;
	
	private Timestamp closeTime;
	
	private String hostTeamName;
	
	private String visitingTeamName;
	
	private Integer hostGoals;
	
	private Integer visitingGoals;
	
	private String okUrlDate;
	
	private Timestamp timestamp;
	
	// 比赛开始时间, 用于查询;
	private Timestamp beginTime;
	
	// 比赛结束时间, 用于查询;
	private Timestamp endTime;
	
	// 队名, 用于查询;
	private String teamName;
	
	// 限制数量;
	private Integer limitedNum;
	
	// 让球数。没有让球为0, 不存入数据库.
	private Integer handicap;
	
	// 99家平均胜赔率.
	private Float hostOdds;
	// 99家平均平局赔率
	private Float evenOdds;
	// 99家平均客胜赔率.
	private Float visitingOdds;
	

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

	public String getMatchName() {
		return matchName;
	}

	public void setMatchName(String matchName) {
		this.matchName = matchName;
	}

	public Integer getMatchSeq() {
		return matchSeq;
	}

	public void setMatchSeq(Integer matchSeq) {
		this.matchSeq = matchSeq;
	}

	public Timestamp getMatchTime() {
		return matchTime;
	}

	public void setMatchTime(Timestamp matchTime) {
		this.matchTime = matchTime;
	}

	public Timestamp getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Timestamp closeTime) {
		this.closeTime = closeTime;
	}

	public String getHostTeamName() {
		return hostTeamName;
	}

	public void setHostTeamName(String hostTeamName) {
		this.hostTeamName = hostTeamName;
	}

	public String getVisitingTeamName() {
		return visitingTeamName;
	}

	public void setVisitingTeamName(String visitingTeamName) {
		this.visitingTeamName = visitingTeamName;
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
	
	public String getOkUrlDate() {
		return okUrlDate;
	}

	public void setOkUrlDate(String okUrlDate) {
		this.okUrlDate = okUrlDate;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	public Timestamp getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Timestamp beginTime) {
		this.beginTime = beginTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public Integer getLimitedNum() {
		return limitedNum;
	}

	public void setLimitedNum(Integer limitedNum) {
		this.limitedNum = limitedNum;
	}
	
	public Integer getHandicap() {
		return handicap;
	}

	public void setHandicap(Integer handicap) {
		this.handicap = handicap;
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

	@Override
	public boolean equals(Object o){
		return (o instanceof Match) && ((Match)o).getOkMatchId().equals(okMatchId);
	}
	
	@Override
	public String toString(){
		return "[ matchSeq:" + matchSeq + "; handicap:" + handicap + "; hostOdds:" + hostOdds + "; evenOdds:" + evenOdds + "; visitingOdds:" + visitingOdds + " ]";
	}

}
