/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * 交易必发成交数据明细(http://www.okooo.com/soccer/match/734052/exchanges/detail/)
 * 
 * @author leslie
 *
 */
public class ExchangeBfTurnoverDetail {
	/**
	 * 联合主键id, 即 LOT_MATCH 的自增长主键.
	 */
	private Long id;
	
	/**
	 * 联合主键seq, 每场比赛有多条数据.
	 */
	private Integer seq;
	
	/**
	 * 成交时间
	 */
	private Timestamp toTime;
	
	/**
	 * 主队成交价位
	 */
	private Float hostPrice;
	
	/**
	 * 主胜总成交量
	 */
	private Integer hostTotal;
	
	/**
	 * 主胜单笔成交量
	 */
	private Integer hostSingle;
	
	/**
	 * 属性: B-买   S-卖
	 */
	private String hostBuySell;
	
	/**
	 * 平局成交价位.
	 */
	private Float evenPrice;
	
	/**
	 * 平局总成交量
	 */
	private Integer evenTotal;
	
	/**
	 * 平局单笔成交量
	 */
	private Integer evenSingle;
	
	/**
	 * 属性: B-买   S-卖
	 */
	private String evenBuySell;
	
	/**
	 * 客胜成交价位.
	 */
	private Float visitingPrice;
	
	/**
	 * 客胜总成交量
	 */
	private Integer visitingTotal;
	
	/**
	 * 客胜单笔成交量
	 */
	private Integer visitingSingle;
	
	/**
	 * 属性: B-买   S-卖
	 */
	private String visitingBuySell;
	
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

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Timestamp getToTime() {
		return toTime;
	}

	public void setToTime(Timestamp toTime) {
		this.toTime = toTime;
	}

	public Float getHostPrice() {
		return hostPrice;
	}

	public void setHostPrice(Float hostPrice) {
		this.hostPrice = hostPrice;
	}

	public Integer getHostTotal() {
		return hostTotal;
	}

	public void setHostTotal(Integer hostTotal) {
		this.hostTotal = hostTotal;
	}

	public Integer getHostSingle() {
		return hostSingle;
	}

	public void setHostSingle(Integer hostSingle) {
		this.hostSingle = hostSingle;
	}

	public String getHostBuySell() {
		return hostBuySell;
	}

	public void setHostBuySell(String hostBuySell) {
		this.hostBuySell = hostBuySell;
	}

	public Float getEvenPrice() {
		return evenPrice;
	}

	public void setEvenPrice(Float evenPrice) {
		this.evenPrice = evenPrice;
	}

	public Integer getEvenTotal() {
		return evenTotal;
	}

	public void setEvenTotal(Integer evenTotal) {
		this.evenTotal = evenTotal;
	}

	public Integer getEvenSingle() {
		return evenSingle;
	}

	public void setEvenSingle(Integer evenSingle) {
		this.evenSingle = evenSingle;
	}

	public String getEvenBuySell() {
		return evenBuySell;
	}

	public void setEvenBuySell(String evenBuySell) {
		this.evenBuySell = evenBuySell;
	}

	public Float getVisitingPrice() {
		return visitingPrice;
	}

	public void setVisitingPrice(Float visitingPrice) {
		this.visitingPrice = visitingPrice;
	}

	public Integer getVisitingTotal() {
		return visitingTotal;
	}

	public void setVisitingTotal(Integer visitingTotal) {
		this.visitingTotal = visitingTotal;
	}

	public Integer getVisitingSingle() {
		return visitingSingle;
	}

	public void setVisitingSingle(Integer visitingSingle) {
		this.visitingSingle = visitingSingle;
	}

	public String getVisitingBuySell() {
		return visitingBuySell;
	}

	public void setVisitingBuySell(String visitingBuySell) {
		this.visitingBuySell = visitingBuySell;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

}
