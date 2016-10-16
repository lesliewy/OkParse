/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * 欧赔转换为亚盘.(http://www.okooo.com/soccer/match/713907/ah/?action=euro2asia&MatchID=713907&MakerIDList=0|82,1|65,2|19,3|84,4|220,5|280,6|106,7|543,8|593,9|696)
 * 
 * @author leslie
 *
 */
public class EuroTransAsia {
	
	private String okUrlDate;
	
	private Integer matchSeq;
	
	private String jobType;
	
	private String oddsCorpName;
	
	/*
	 * 实际的欧赔
	 */
	private Float hostOddsEuro;
	
	private Float evenOddsEuro;
	
	private Float visitingOddsEuro;
	
	private Float lossRatioEuro;
	
	/*
	 * 转换后的亚盘 
	 */
	private Float hostOddsAsiaTrans;
	
	private Float handicapAsiaTrans;
	
	private Float visitingOddsAsiaTrans;
	
	private Float totalDiscountTrans;
	
	/*
	 * 实际的亚盘
	 */
	private Float hostOddsAsia;
	
	private Float handicapAsia;
	
	private Float visitingOddsAsia;
	
	private Float totalDiscount;
	
	private Float lossRatioAsia;
	
	private Timestamp timestamp;
	
	private Float hostKellyAsia;
	
	private Float visitingKellyAsia;
	
	private Float hostKellyEuro;
	
	private Float evenKellyEuro;
	
	private Float visitingKellyEuro;
	
	// 查询使用.
	private Integer beginMatchSeq;
	
	private Integer endMatchSeq;

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

	public String getOddsCorpName() {
		return oddsCorpName;
	}

	public void setOddsCorpName(String oddsCorpName) {
		this.oddsCorpName = oddsCorpName;
	}

	public Float getHostOddsEuro() {
		return hostOddsEuro;
	}

	public void setHostOddsEuro(Float hostOddsEuro) {
		this.hostOddsEuro = hostOddsEuro;
	}

	public Float getEvenOddsEuro() {
		return evenOddsEuro;
	}

	public void setEvenOddsEuro(Float evenOddsEuro) {
		this.evenOddsEuro = evenOddsEuro;
	}

	public Float getVisitingOddsEuro() {
		return visitingOddsEuro;
	}

	public void setVisitingOddsEuro(Float visitingOddsEuro) {
		this.visitingOddsEuro = visitingOddsEuro;
	}

	public Float getLossRatioEuro() {
		return lossRatioEuro;
	}

	public void setLossRatioEuro(Float lossRatioEuro) {
		this.lossRatioEuro = lossRatioEuro;
	}

	public Float getHostOddsAsiaTrans() {
		return hostOddsAsiaTrans;
	}

	public void setHostOddsAsiaTrans(Float hostOddsAsiaTrans) {
		this.hostOddsAsiaTrans = hostOddsAsiaTrans;
	}

	public Float getHandicapAsiaTrans() {
		return handicapAsiaTrans;
	}

	public void setHandicapAsiaTrans(Float handicapAsiaTrans) {
		this.handicapAsiaTrans = handicapAsiaTrans;
	}

	public Float getVisitingOddsAsiaTrans() {
		return visitingOddsAsiaTrans;
	}

	public void setVisitingOddsAsiaTrans(Float visitingOddsAsiaTrans) {
		this.visitingOddsAsiaTrans = visitingOddsAsiaTrans;
	}

	public Float getTotalDiscountTrans() {
		return totalDiscountTrans;
	}

	public void setTotalDiscountTrans(Float totalDiscountTrans) {
		this.totalDiscountTrans = totalDiscountTrans;
	}

	public Float getHostOddsAsia() {
		return hostOddsAsia;
	}

	public void setHostOddsAsia(Float hostOddsAsia) {
		this.hostOddsAsia = hostOddsAsia;
	}

	public Float getHandicapAsia() {
		return handicapAsia;
	}

	public void setHandicapAsia(Float handicapAsia) {
		this.handicapAsia = handicapAsia;
	}

	public Float getVisitingOddsAsia() {
		return visitingOddsAsia;
	}

	public void setVisitingOddsAsia(Float visitingOddsAsia) {
		this.visitingOddsAsia = visitingOddsAsia;
	}

	public Float getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(Float totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public Float getLossRatioAsia() {
		return lossRatioAsia;
	}

	public void setLossRatioAsia(Float lossRatioAsia) {
		this.lossRatioAsia = lossRatioAsia;
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

	public Float getHostKellyAsia() {
		return hostKellyAsia;
	}

	public void setHostKellyAsia(Float hostKellyAsia) {
		this.hostKellyAsia = hostKellyAsia;
	}

	public Float getVisitingKellyAsia() {
		return visitingKellyAsia;
	}

	public void setVisitingKellyAsia(Float visitingKellyAsia) {
		this.visitingKellyAsia = visitingKellyAsia;
	}

	public Float getHostKellyEuro() {
		return hostKellyEuro;
	}

	public void setHostKellyEuro(Float hostKellyEuro) {
		this.hostKellyEuro = hostKellyEuro;
	}

	public Float getEvenKellyEuro() {
		return evenKellyEuro;
	}

	public void setEvenKellyEuro(Float evenKellyEuro) {
		this.evenKellyEuro = evenKellyEuro;
	}

	public Float getVisitingKellyEuro() {
		return visitingKellyEuro;
	}

	public void setVisitingKellyEuro(Float visitingKellyEuro) {
		this.visitingKellyEuro = visitingKellyEuro;
	}

}
