package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.MatchJob;



/**
 * 
 * @author leslie
 *
 */
public interface MatchJobDao {
	void insertJob(MatchJob job);
	
	MatchJob queryJobsById(MatchJob job);
	
	Integer queryMaxBeginSeqByOkUrlDate(MatchJob queryJob);
	
	List<MatchJob> queryJobByDateStatus(MatchJob job);
	
	void updateR2S(MatchJob job);
	
	List<MatchJob> queryOkUrlDateFromMatchJob();
	
	void deleteJobById(MatchJob matchJob);
	
	void updateR2D(MatchJob delMatchJob);
}
