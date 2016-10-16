package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.KellyMatchCountDao;
import com.wy.okooo.domain.KellyMatchCount;

public class KellyMatchCountDaoImpl extends SqlMapClientDaoSupport implements KellyMatchCountDao {

	private static Logger LOGGER = Logger.getLogger(KellyMatchCountDaoImpl.class
			.getName());

	public void insertMatchCount(KellyMatchCount kellyMatchCount) {
		if (kellyMatchCount == null) {
			return;
		}
		deleteByKey(kellyMatchCount);
		getSqlMapClientTemplate().insert("insertMatchCount", kellyMatchCount);
	}

	public void insertMatchCountBatch(List<KellyMatchCount> kellyMatchCounts) {
		if (kellyMatchCounts == null) {
			LOGGER.error("kellyMatchCounts is null, return.");
			return;
		}
		for (KellyMatchCount matchCount : kellyMatchCounts) {
			insertMatchCount(matchCount);
		}
	}

	@SuppressWarnings("unchecked")
	public List<KellyMatchCount> queryExistsMatchCount(
			KellyMatchCount kellyMatchCount) {
		return getSqlMapClientTemplate().queryForList("queryExistsMatchCount", kellyMatchCount);
	}

	@SuppressWarnings("unchecked")
	public List<KellyMatchCount> queryExistsMatchCountByDate(String okUrlDate) {
		if(StringUtils.isBlank(okUrlDate)){
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryExistsMatchCountByDate", okUrlDate);
	}
	
	@SuppressWarnings("unchecked")
	public List<KellyMatchCount> queryAllMatchCount() {
		return getSqlMapClientTemplate().queryForList("queryAllMatchCount");
	}

	public void deleteByKey(KellyMatchCount kellyMatchCount) {
		if (kellyMatchCount == null) {
			return;
		}
		getSqlMapClientTemplate().delete("deleteByKey", kellyMatchCount);
	}

	@SuppressWarnings("unchecked")
	public List<KellyMatchCount> querySeqAndJobTypeByOkUrlDate(String okUrlDate) {
		if(StringUtils.isBlank(okUrlDate)){
			LOGGER.error("okUrlDate is blank, return now.");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("querySeqAndJobTypeByOkUrlDate", okUrlDate);
	}

	@SuppressWarnings("unchecked")
	public List<KellyMatchCount> queryMatchCountByDateJobFlag(
			KellyMatchCount query) {
		return getSqlMapClientTemplate().queryForList("queryMatchCountByDateJobFlag", query);
	}

	@SuppressWarnings("unchecked")
	public List<KellyMatchCount> queryMatchCountByDateJobFlagRule(
			KellyMatchCount query) {
		return getSqlMapClientTemplate().queryForList("queryMatchCountByDateJobFlagRule", query);
	}

}
