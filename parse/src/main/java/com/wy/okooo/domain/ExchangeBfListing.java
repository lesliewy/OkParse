/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * 交易盈亏页面的必发挂牌信息, 包括买家、卖家(http://www.okooo.com/soccer/match/734052/exchanges/)
 * 
 * @author leslie
 *
 */
public class ExchangeBfListing {
	/**
	 * 主键id, 即 LOT_MATCH 的自增长主键.
	 */
	private Long id;
	
	/**
	 * 买家主胜挂牌价位
	 */
	private Float hostBuyersPrice;
	
	/**
	 * 买家主胜挂牌量
	 */
	private Integer hostBuyersQuantity;
	
	/**
	 * 买家平赔挂牌价位
	 */
	private Float evenBuyersPrice;
	
	/**
	 * 买家平赔挂牌量
	 */
	private Integer evenBuyersQuantity;
	
	/**
	 * 买家客胜挂牌价位
	 */
	private Float visitingBuyersPrice;
	
	/**
	 * 买家客胜挂牌量
	 */
	private Integer visitingBuyersQuantity;
	
	/**
	 * 卖家主胜挂牌价位
	 */
	private Float hostSellersPrice;
	
	/**
	 * 卖家主胜挂牌量
	 */
	private Integer hostSellersQuantity;
	
	/**
	 * 卖家平赔挂牌价位
	 */
	private Float evenSellersPrice;
	
	/**
	 * 卖家平赔挂牌量
	 */
	private Integer evenSellersQuantity;
	
	/**
	 * 卖家客胜挂牌价位
	 */
	private Float visitingSellersPrice;
	
	/**
	 * 卖家客胜挂牌量
	 */
	private Integer visitingSellersQuantity;
	
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

	public Float getHostBuyersPrice() {
		return hostBuyersPrice;
	}

	public void setHostBuyersPrice(Float hostBuyersPrice) {
		this.hostBuyersPrice = hostBuyersPrice;
	}

	public Integer getHostBuyersQuantity() {
		return hostBuyersQuantity;
	}

	public void setHostBuyersQuantity(Integer hostBuyersQuantity) {
		this.hostBuyersQuantity = hostBuyersQuantity;
	}

	public Float getEvenBuyersPrice() {
		return evenBuyersPrice;
	}

	public void setEvenBuyersPrice(Float evenBuyersPrice) {
		this.evenBuyersPrice = evenBuyersPrice;
	}

	public Integer getEvenBuyersQuantity() {
		return evenBuyersQuantity;
	}

	public void setEvenBuyersQuantity(Integer evenBuyersQuantity) {
		this.evenBuyersQuantity = evenBuyersQuantity;
	}

	public Float getVisitingBuyersPrice() {
		return visitingBuyersPrice;
	}

	public void setVisitingBuyersPrice(Float visitingBuyersPrice) {
		this.visitingBuyersPrice = visitingBuyersPrice;
	}

	public Integer getVisitingBuyersQuantity() {
		return visitingBuyersQuantity;
	}

	public void setVisitingBuyersQuantity(Integer visitingBuyersQuantity) {
		this.visitingBuyersQuantity = visitingBuyersQuantity;
	}

	public Float getHostSellersPrice() {
		return hostSellersPrice;
	}

	public void setHostSellersPrice(Float hostSellersPrice) {
		this.hostSellersPrice = hostSellersPrice;
	}

	public Integer getHostSellersQuantity() {
		return hostSellersQuantity;
	}

	public void setHostSellersQuantity(Integer hostSellersQuantity) {
		this.hostSellersQuantity = hostSellersQuantity;
	}

	public Float getEvenSellersPrice() {
		return evenSellersPrice;
	}

	public void setEvenSellersPrice(Float evenSellersPrice) {
		this.evenSellersPrice = evenSellersPrice;
	}

	public Integer getEvenSellersQuantity() {
		return evenSellersQuantity;
	}

	public void setEvenSellersQuantity(Integer evenSellersQuantity) {
		this.evenSellersQuantity = evenSellersQuantity;
	}

	public Float getVisitingSellersPrice() {
		return visitingSellersPrice;
	}

	public void setVisitingSellersPrice(Float visitingSellersPrice) {
		this.visitingSellersPrice = visitingSellersPrice;
	}

	public Integer getVisitingSellersQuantity() {
		return visitingSellersQuantity;
	}

	public void setVisitingSellersQuantity(Integer visitingSellersQuantity) {
		this.visitingSellersQuantity = visitingSellersQuantity;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

}
