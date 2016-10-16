package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.IndexStatsDao;
import com.wy.okooo.domain.IndexStats;

public class IndexStatsDaoImpl extends SqlMapClientDaoSupport implements IndexStatsDao {

	private static Logger LOGGER = Logger.getLogger(IndexStatsDaoImpl.class
			.getName());

	public void insertIndexStats(IndexStats indexStats) {
		if (indexStats == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertIndexStats", indexStats);
		}catch (Exception e){
			LOGGER.error(e);
		}
	}

	public void insertIndexStatsBatch(List<IndexStats> indexStatsList) {
		if (indexStatsList == null) {
			LOGGER.error("indexStatsList is null, return.");
			return;
		}
		for (IndexStats indexStats : indexStatsList) {
			insertIndexStats(indexStats);
		}
	}

	@SuppressWarnings("unchecked")
	public List<IndexStats> queryAllByOkUrlDate(String okUrlDate) {
		if(StringUtils.isBlank(okUrlDate)){
			LOGGER.error("okUrlDate is blank, return null...");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryAllByOkUrlDate", okUrlDate);
	}

	@SuppressWarnings("unchecked")
	public List<IndexStats> queryIndexStatsByRange(IndexStats queryIndexStats) {
		if(queryIndexStats == null){
			LOGGER.info("queryIndexStats is null, return now...");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryIndexStatsByRange", queryIndexStats);
	}

	@SuppressWarnings("unchecked")
	public List<IndexStats> queryCurrJobTypeIndex(String okUrlDate) {
		if(StringUtils.isBlank(okUrlDate)){
			LOGGER.error("okUrlDate is blank, return null...");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryCurrJobTypeIndex", okUrlDate);
	}

}
