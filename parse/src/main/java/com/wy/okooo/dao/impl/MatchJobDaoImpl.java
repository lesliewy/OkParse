/**
 * 
 */
package com.wy.okooo.dao.impl;

import java.util.List;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.MatchJobDao;
import com.wy.okooo.domain.MatchJob;

/**
 * @author leslie
 *
 */
public class MatchJobDaoImpl extends SqlMapClientDaoSupport implements MatchJobDao {

	public void insertJob(MatchJob matchJob) {
		if (matchJob == null) {
			return;
		}
		getSqlMapClientTemplate().insert("insertJob", matchJob);
	}

	public MatchJob queryJobsById(MatchJob matchJob) {
		if (matchJob == null) {
			return null;
		}
		return (MatchJob) getSqlMapClientTemplate().queryForObject("queryJobsById", matchJob);
	}

	public Integer queryMaxBeginSeqByOkUrlDate(MatchJob queryJob) {
		if(queryJob == null){
			return null;
		}
		return  (Integer) getSqlMapClientTemplate().queryForObject("queryMaxBeginSeqByOkUrlDate", queryJob);
	}

	@SuppressWarnings("unchecked")
	public List<MatchJob> queryJobByDateStatus(MatchJob job) {
		if(job == null){
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryJobByDateStatus", job);
	}
	
	public void updateR2S(MatchJob job) {
		if(job == null){
			return;
		}
		getSqlMapClientTemplate().update("updateR2S", job);
	}
	
	public void updateR2D(MatchJob delMatchJob) {
		if(delMatchJob == null){
			return;
		}
		getSqlMapClientTemplate().update("updateR2D", delMatchJob);
	}

	@SuppressWarnings("unchecked")
	public List<MatchJob> queryOkUrlDateFromMatchJob() {
		return getSqlMapClientTemplate().queryForList("queryOkUrlDateFromMatchJob");
	}

	public void deleteJobById(MatchJob matchJob) {
		if(matchJob == null){
			return;
		}
		getSqlMapClientTemplate().delete("deleteJobById", matchJob);
	}

}
