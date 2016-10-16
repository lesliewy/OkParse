/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * @author leslie
 *
 */
public class EuroAsiaRefer {

	private Float oddsEuro;
	
	private Float oddsAsiaTop;
	
	private Float oddsAsiaUnder;
	
	private Float handicapAsia;
	
	private Float totalDiscount;
	
	private Timestamp timestamp;

	public Float getOddsEuro() {
		return oddsEuro;
	}

	public void setOddsEuro(Float oddsEuro) {
		this.oddsEuro = oddsEuro;
	}

	public Float getOddsAsiaTop() {
		return oddsAsiaTop;
	}

	public void setOddsAsiaTop(Float oddsAsiaTop) {
		this.oddsAsiaTop = oddsAsiaTop;
	}

	public Float getOddsAsiaUnder() {
		return oddsAsiaUnder;
	}

	public void setOddsAsiaUnder(Float oddsAsiaUnder) {
		this.oddsAsiaUnder = oddsAsiaUnder;
	}

	public Float getHandicapAsia() {
		return handicapAsia;
	}

	public void setHandicapAsia(Float handicapAsia) {
		this.handicapAsia = handicapAsia;
	}

	public Float getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(Float totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}
