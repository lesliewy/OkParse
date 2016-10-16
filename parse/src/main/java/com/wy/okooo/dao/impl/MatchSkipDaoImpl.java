package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.MatchSkipDao;
import com.wy.okooo.domain.MatchSkip;

public class MatchSkipDaoImpl extends SqlMapClientDaoSupport implements MatchSkipDao {

	private static Logger LOGGER = Logger.getLogger(MatchSkipDaoImpl.class
			.getName());

	@SuppressWarnings("unchecked")
	public List<MatchSkip> querySkipMatchesByOkUrlDate(String okUrlDate) {
		if(StringUtils.isBlank(okUrlDate)){
			LOGGER.info("okUrlDate is blank, return null.");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("querySkipMatchesByOkUrlDate", okUrlDate);
	}
}
