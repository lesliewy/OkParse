/**
 * 
 */
package com.wy.okooo.service;

import java.util.List;

import com.wy.okooo.domain.MatchJob;

/**
 * @author leslie
 *
 */
public interface MatchJobService {
	
	void insertJob(MatchJob job);
	
	MatchJob queryJobsById(MatchJob job);
	
	Integer queryMaxBeginSeqByOkUrlDate(MatchJob queryJob);
	
	Integer queryMaxBeginSeqByOkUrlDate(String okUrlDate, String jobFlag);
	
	List<MatchJob> queryJobByDateStatus(MatchJob job);
	
	void updateR2S(MatchJob job);
	
	void updateR2D(MatchJob delMatchJob);
	
	List<MatchJob> queryOkUrlDateFromMatchJob();
	
	void deleteJobById(MatchJob matchJob);
	
	List<MatchJob> getRunningJobs(String okUrlDate, String jobFlag);
	
	boolean hasRunningJob(String okUrlDate, String jobFlag);
	
	void cleanLongTimeJob(int limit, String jobFlag);
}
