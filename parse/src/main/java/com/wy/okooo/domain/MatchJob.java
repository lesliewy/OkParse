/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * LOT_JOB
 * @author leslie
 *
 */
public class MatchJob {

	private String okUrlDate;
	
	private Integer beginMatchSeq;
	
	private Integer endMatchSeq;
	
	private String jobType;
	
	/*
	 * S: 成功;   F: 失败;  R: 正在执行; M: 手工添加;
	 */
	private String status;
	
	private String remark;
	
	private Timestamp beginTime;
	
	private Timestamp timestamp;
	
	private String timeType;
	
	// 删除超过该时间(s)的正在运行的JOB, 用于数据库操作的查询条件.
	private int delRUpperLimit;
	
	// JOB标志. 例如: A-ParseAndMailJob; B-AsiaOddsKellyJob; C-IndexStatsJob;
	private String jobFlag;

	public String getOkUrlDate() {
		return okUrlDate;
	}

	public void setOkUrlDate(String okUrlDate) {
		this.okUrlDate = okUrlDate;
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

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Timestamp getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Timestamp beginTime) {
		this.beginTime = beginTime;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getTimeType() {
		return timeType;
	}

	public void setTimeType(String timeType) {
		this.timeType = timeType;
	}

	public int getDelRUpperLimit() {
		return delRUpperLimit;
	}

	public void setDelRUpperLimit(int delRUpperLimit) {
		this.delRUpperLimit = delRUpperLimit;
	}

	public String getJobFlag() {
		return jobFlag;
	}

	public void setJobFlag(String jobFlag) {
		this.jobFlag = jobFlag;
	}

	@Override
	public String toString(){
		return "[ okUrlDate = " + okUrlDate + ", beginMatchSeq = " + beginMatchSeq + ", endMatchSeq = " + endMatchSeq 
				+ ", jobType = " + jobType + ", status = " + status + ", remark = " + remark + ", beginTime = " + beginTime
				+ ", timestamp = " + timestamp  + ", timeType = " + timeType
				+ " ]";
	}
	
}
