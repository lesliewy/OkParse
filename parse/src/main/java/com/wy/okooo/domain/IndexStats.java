package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * LOT_INDEX_STATS
 * 
 * @author leslie
 *
 */
public class IndexStats {
	private String okUrlDate;
	
	private Integer matchSeq;
	
	private String jobType;
	
	private Float initOkoooHost;
	
	private Float initOkoooEven;
	
	private Float initOkoooVisiting;
	
	private Float okoooHost;
	
	private Float okoooEven;
	
	private Float okoooVisiting;
	
	private Float initStdDevHost;
	
	private Float initStdDevEven;
	
	private Float initStdDevVisiting;
	
	private Float stdDevHost;
	
	private Float stdDevEven;
	
	private Float stdDevVisiting;
	
	private Timestamp timestamp;
	
	// 查询使用
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

	public Float getInitOkoooHost() {
		return initOkoooHost;
	}

	public void setInitOkoooHost(Float initOkoooHost) {
		this.initOkoooHost = initOkoooHost;
	}

	public Float getInitOkoooEven() {
		return initOkoooEven;
	}

	public void setInitOkoooEven(Float initOkoooEven) {
		this.initOkoooEven = initOkoooEven;
	}

	public Float getInitOkoooVisiting() {
		return initOkoooVisiting;
	}

	public void setInitOkoooVisiting(Float initOkoooVisiting) {
		this.initOkoooVisiting = initOkoooVisiting;
	}

	public Float getOkoooHost() {
		return okoooHost;
	}

	public void setOkoooHost(Float okoooHost) {
		this.okoooHost = okoooHost;
	}

	public Float getOkoooEven() {
		return okoooEven;
	}

	public void setOkoooEven(Float okoooEven) {
		this.okoooEven = okoooEven;
	}

	public Float getOkoooVisiting() {
		return okoooVisiting;
	}

	public void setOkoooVisiting(Float okoooVisiting) {
		this.okoooVisiting = okoooVisiting;
	}

	public Float getInitStdDevHost() {
		return initStdDevHost;
	}

	public void setInitStdDevHost(Float initStdDevHost) {
		this.initStdDevHost = initStdDevHost;
	}

	public Float getInitStdDevEven() {
		return initStdDevEven;
	}

	public void setInitStdDevEven(Float initStdDevEven) {
		this.initStdDevEven = initStdDevEven;
	}

	public Float getInitStdDevVisiting() {
		return initStdDevVisiting;
	}

	public void setInitStdDevVisiting(Float initStdDevVisiting) {
		this.initStdDevVisiting = initStdDevVisiting;
	}

	public Float getStdDevHost() {
		return stdDevHost;
	}

	public void setStdDevHost(Float stdDevHost) {
		this.stdDevHost = stdDevHost;
	}

	public Float getStdDevEven() {
		return stdDevEven;
	}

	public void setStdDevEven(Float stdDevEven) {
		this.stdDevEven = stdDevEven;
	}

	public Float getStdDevVisiting() {
		return stdDevVisiting;
	}

	public void setStdDevVisiting(Float stdDevVisiting) {
		this.stdDevVisiting = stdDevVisiting;
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
	
}
