/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * 交易盈亏页面的交易量比例(http://www.okooo.com/soccer/match/734052/exchanges/)
 * 
 * @author leslie
 *
 */
public class ExchangeTransactionProp {
	/**
	 * 主键id, 即 LOT_MATCH 的自增长主键.
	 */
	private Long id;
	
	/**
	 * 必发主胜交易量比例,  如 13.86%
	 */
	private Float hostBf;
	
	/**
	 * 必发平交易量比例. 如: 35.82%
	 */
	private Float evenBf;
	
	/**
	 * 必发客胜交易量比例,  如 13.86%
	 */
	private Float visitingBf;
	
	/**
	 * 竟彩主胜交易量比例,  如 13.86%
	 */
	private Float hostComp;
	
	/**
	 * 竟彩平交易量比例. 如: 35.82%
	 */
	private Float evenComp;
	
	/**
	 * 竟彩客胜交易量比例,  如 13.86%
	 */
	private Float visitingComp;
	
	/**
	 * 北单主胜交易量比例,  如 13.86%
	 */
	private Float hostBjSingle;
	
	/**
	 * 北单平交易量比例. 如: 35.82%
	 */
	private Float evenBjSingle;
	
	/**
	 * 北单客胜交易量比例,  如 13.86%
	 */
	private Float visitingBjSingle;
	
	/**
	 * 必发主胜盈亏指数.
	 */
	private Integer hostBfProlossIndex;
	
	/**
	 * 必发平盈亏指数.
	 */
	private Integer evenBfProlossIndex;
	
	/**
	 * 必发客胜盈亏指数.
	 */
	private Integer visitingBfProlossIndex;
	
	/**
	 * 竟彩主胜盈亏指数.
	 */
	private Integer hostCompProlossIndex;
	
	/**
	 * 竟彩平局盈亏指数.
	 */
	private Integer evenCompProlossIndex;
	
	/**
	 * 竟彩客胜盈亏指数.
	 */
	private Integer visitingCompProlossIndex;
	
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

	public Float getHostBf() {
		return hostBf;
	}

	public void setHostBf(Float hostBf) {
		this.hostBf = hostBf;
	}

	public Float getEvenBf() {
		return evenBf;
	}

	public void setEvenBf(Float evenBf) {
		this.evenBf = evenBf;
	}

	public Float getVisitingBf() {
		return visitingBf;
	}

	public void setVisitingBf(Float visitingBf) {
		this.visitingBf = visitingBf;
	}

	public Float getHostComp() {
		return hostComp;
	}

	public void setHostComp(Float hostComp) {
		this.hostComp = hostComp;
	}

	public Float getEvenComp() {
		return evenComp;
	}

	public void setEvenComp(Float evenComp) {
		this.evenComp = evenComp;
	}

	public Float getVisitingComp() {
		return visitingComp;
	}

	public void setVisitingComp(Float visitingComp) {
		this.visitingComp = visitingComp;
	}

	public Float getHostBjSingle() {
		return hostBjSingle;
	}

	public void setHostBjSingle(Float hostBjSingle) {
		this.hostBjSingle = hostBjSingle;
	}

	public Float getEvenBjSingle() {
		return evenBjSingle;
	}

	public void setEvenBjSingle(Float evenBjSingle) {
		this.evenBjSingle = evenBjSingle;
	}

	public Float getVisitingBjSingle() {
		return visitingBjSingle;
	}

	public void setVisitingBjSingle(Float visitingBjSingle) {
		this.visitingBjSingle = visitingBjSingle;
	}

	public Integer getHostBfProlossIndex() {
		return hostBfProlossIndex;
	}

	public void setHostBfProlossIndex(Integer hostBfProlossIndex) {
		this.hostBfProlossIndex = hostBfProlossIndex;
	}

	public Integer getEvenBfProlossIndex() {
		return evenBfProlossIndex;
	}

	public void setEvenBfProlossIndex(Integer evenBfProlossIndex) {
		this.evenBfProlossIndex = evenBfProlossIndex;
	}

	public Integer getVisitingBfProlossIndex() {
		return visitingBfProlossIndex;
	}

	public void setVisitingBfProlossIndex(Integer visitingBfProlossIndex) {
		this.visitingBfProlossIndex = visitingBfProlossIndex;
	}

	public Integer getHostCompProlossIndex() {
		return hostCompProlossIndex;
	}

	public void setHostCompProlossIndex(Integer hostCompProlossIndex) {
		this.hostCompProlossIndex = hostCompProlossIndex;
	}

	public Integer getEvenCompProlossIndex() {
		return evenCompProlossIndex;
	}

	public void setEvenCompProlossIndex(Integer evenCompProlossIndex) {
		this.evenCompProlossIndex = evenCompProlossIndex;
	}

	public Integer getVisitingCompProlossIndex() {
		return visitingCompProlossIndex;
	}

	public void setVisitingCompProlossIndex(Integer visitingCompProlossIndex) {
		this.visitingCompProlossIndex = visitingCompProlossIndex;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

}
