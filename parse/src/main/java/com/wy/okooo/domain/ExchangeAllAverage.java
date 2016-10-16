/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * 交易盈亏页面的99家平均(http://www.okooo.com/soccer/match/734052/exchanges/)
 * 
 * @author leslie
 *
 */
public class ExchangeAllAverage {
	/**
	 * 主键id, 即 LOT_MATCH 的自增长主键.
	 */
	private Long id;
	
	/**
	 * 99家平均赔率: 主胜.
	 */
	private Float hostOdds;
	
	/**
	 * 99家平均概率: 主胜
	 */
	private Float hostProb;
	
	/**
	 * 99家平均赔率: 平局.
	 */
	private Float evenOdds;
	
	/**
	 * 99家平均概率: 平局
	 */
	private Float evenProb;
	
	/**
	 * 99家平均赔率: 客胜.
	 */
	private Float visitingOdds;
	
	/**
	 * 99家平均概率: 客胜
	 */
	private Float visitingProb;
	
	/**
	 * 时间戳.
	 */
	private Timestamp timestamp;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Float getHostOdds() {
		return hostOdds;
	}

	public void setHostOdds(Float hostOdds) {
		this.hostOdds = hostOdds;
	}

	public Float getHostProb() {
		return hostProb;
	}

	public void setHostProb(Float hostProb) {
		this.hostProb = hostProb;
	}

	public Float getEvenOdds() {
		return evenOdds;
	}

	public void setEvenOdds(Float evenOdds) {
		this.evenOdds = evenOdds;
	}

	public Float getEvenProb() {
		return evenProb;
	}

	public void setEvenProb(Float evenProb) {
		this.evenProb = evenProb;
	}

	public Float getVisitingOdds() {
		return visitingOdds;
	}

	public void setVisitingOdds(Float visitingOdds) {
		this.visitingOdds = visitingOdds;
	}

	public Float getVisitingProb() {
		return visitingProb;
	}

	public void setVisitingProb(Float visitingProb) {
		this.visitingProb = visitingProb;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

}
