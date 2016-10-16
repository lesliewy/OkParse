package com.wy.okooo.domain;

import java.sql.Timestamp;

public class MatchStats {
	
	private Long okMatchId;
	
	private String okUrlDate;
	
	private Integer matchSeq;
	
	private String shotsOnTarget;
	
	private String shotsOffTarget;
	
	private String freeKick;
	
	private String corners;
	
	private String throwIns;
	
	private String goalkeeperDist;
	
	private String beatOutShot;
	
	private String offside;
	
	private String foulCommitted;
	
	private String possession;
	
	private Timestamp timestamp;
	
	private String goalTime;

	public Long getOkMatchId() {
		return okMatchId;
	}

	public void setOkMatchId(Long okMatchId) {
		this.okMatchId = okMatchId;
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

	public String getShotsOnTarget() {
		return shotsOnTarget;
	}

	public void setShotsOnTarget(String shotsOnTarget) {
		this.shotsOnTarget = shotsOnTarget;
	}

	public String getShotsOffTarget() {
		return shotsOffTarget;
	}

	public void setShotsOffTarget(String shotsOffTarget) {
		this.shotsOffTarget = shotsOffTarget;
	}

	public String getFreeKick() {
		return freeKick;
	}

	public void setFreeKick(String freeKick) {
		this.freeKick = freeKick;
	}

	public String getCorners() {
		return corners;
	}

	public void setCorners(String corners) {
		this.corners = corners;
	}

	public String getThrowIns() {
		return throwIns;
	}

	public void setThrowIns(String throwIns) {
		this.throwIns = throwIns;
	}

	public String getGoalkeeperDist() {
		return goalkeeperDist;
	}

	public void setGoalkeeperDist(String goalkeeperDist) {
		this.goalkeeperDist = goalkeeperDist;
	}

	public String getBeatOutShot() {
		return beatOutShot;
	}

	public void setBeatOutShot(String beatOutShot) {
		this.beatOutShot = beatOutShot;
	}

	public String getOffside() {
		return offside;
	}

	public void setOffside(String offside) {
		this.offside = offside;
	}

	public String getFoulCommitted() {
		return foulCommitted;
	}

	public void setFoulCommitted(String foulCommitted) {
		this.foulCommitted = foulCommitted;
	}

	public String getPossession() {
		return possession;
	}

	public void setPossession(String possession) {
		this.possession = possession;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getGoalTime() {
		return goalTime;
	}

	public void setGoalTime(String goalTime) {
		this.goalTime = goalTime;
	}

}
