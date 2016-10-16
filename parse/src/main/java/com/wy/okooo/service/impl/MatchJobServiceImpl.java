/**
 * 
 */
package com.wy.okooo.service.impl;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import com.wy.okooo.dao.MatchJobDao;
import com.wy.okooo.domain.MatchJob;
import com.wy.okooo.service.MatchJobService;
import com.wy.okooo.util.OkConstant;

/**
 * @author leslie
 *
 */
public class MatchJobServiceImpl implements MatchJobService {

	private MatchJobDao matchJobDao;
	
	public void insertJob(MatchJob job) {
		matchJobDao.insertJob(job);
	}

	public MatchJob queryJobsById(MatchJob job) {
		return matchJobDao.queryJobsById(job);
	}

	public Integer queryMaxBeginSeqByOkUrlDate(MatchJob queryJob) {
		return matchJobDao.queryMaxBeginSeqByOkUrlDate(queryJob);
	}
	
	public List<MatchJob> queryJobByDateStatus(MatchJob job) {
		return matchJobDao.queryJobByDateStatus(job);
	}
	
	public List<MatchJob> queryOkUrlDateFromMatchJob() {
		return matchJobDao.queryOkUrlDateFromMatchJob();
	}
	
	public void updateR2S(MatchJob job) {
		matchJobDao.updateR2S(job);
	}
	
	public void deleteJobById(MatchJob matchJob) {
		matchJobDao.deleteJobById(matchJob);
	}
	
	public void updateR2D(MatchJob delMatchJob) {
		Integer timeLimit = delMatchJob.getDelRUpperLimit();
		if(timeLimit == null){
			return;
		}
		matchJobDao.updateR2D(delMatchJob);
	}
	
	/**
	 * 根据okUrlDate, jobFlag查询正在执行的job.
	 * @param okUrlDate
	 * @param jobFlag
	 * @return
	 */
	public List<MatchJob> getRunningJobs(String okUrlDate, String jobFlag){
		MatchJob queryRunningJob = new MatchJob();
		queryRunningJob.setOkUrlDate(okUrlDate);
		queryRunningJob.setJobFlag(jobFlag);
		queryRunningJob.setStatus(OkConstant.JOB_STATE_RUNNING);
		return queryJobByDateStatus(queryRunningJob);
	}
	
	/**
	 * 根据okUrlDate查询是否有指定jobFlag的JOB正在运行.
	 * @param okUrlDate
	 * @param jobFlag
	 * @return
	 */
	public boolean hasRunningJob(String okUrlDate, String jobFlag){
		List<MatchJob> runningJobs = getRunningJobs(okUrlDate, jobFlag);
		if(runningJobs != null && !runningJobs.isEmpty()){
			return true;
		}
		return false;
	}
	
	/**
	 * 根据okUrlDate, jobFlag 查询最大的beginMatchSeq.
	 * @param okUrlDate
	 * @param jobFlag
	 * @return
	 */
	public Integer queryMaxBeginSeqByOkUrlDate(String okUrlDate, String jobFlag){
		MatchJob queryJob = new MatchJob();
		queryJob.setOkUrlDate(okUrlDate);
		queryJob.setJobFlag(jobFlag);
		return queryMaxBeginSeqByOkUrlDate(queryJob);
	}
	
	/**
	 * 清除掉时间过长的job. 超时时间可以指定，单位: s.
	 * @param limit
	 */
	public void cleanLongTimeJob(int limit, String jobFlag){
		MatchJob deletedRJob = new MatchJob();
		deletedRJob.setStatus(OkConstant.JOB_STATE_DELETE);
		deletedRJob.setRemark("R job exceed limit: " + limit + " s.");
		deletedRJob.setTimestamp(new Timestamp(Calendar.getInstance()
				.getTimeInMillis()));
		deletedRJob.setDelRUpperLimit(limit);
		deletedRJob.setJobFlag(jobFlag);
		updateR2D(deletedRJob);
	}

	
	public MatchJobDao getMatchJobDao() {
		return matchJobDao;
	}

	public void setMatchJobDao(MatchJobDao matchJobDao) {
		this.matchJobDao = matchJobDao;
	}

}
