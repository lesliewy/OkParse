package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.KellyCorpCountDao;
import com.wy.okooo.domain.KellyCorpCount;

public class KellyCorpCountDaoImpl extends SqlMapClientDaoSupport implements KellyCorpCountDao {

	private static Logger LOGGER = Logger.getLogger(KellyCorpCountDaoImpl.class
			.getName());

	public void insert(KellyCorpCount corpCount) {
		if (corpCount == null) {
			return;
		}
//		try{
			getSqlMapClientTemplate().insert("insertCorpCount", corpCount);
//		}catch (Exception e){
//			LOGGER.error(e);
//		}
	}

	public void insertList(List<KellyCorpCount> corpCountList) {
		if (corpCountList == null) {
			LOGGER.error("corpCountList is null, return.");
			return;
		}
		for (KellyCorpCount corpCount : corpCountList) {
			insert(corpCount);
		}
	}

	public void deleteCorpCountByMatchName(String matchName) {
		if (StringUtils.isBlank(matchName)) {
			LOGGER.error("matchName is null, return.");
			return;
		}
		getSqlMapClientTemplate().delete("deleteCorpCountByMatchName", matchName);
	}

}
