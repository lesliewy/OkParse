package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.EuroOddsHandicapDao;
import com.wy.okooo.domain.EuroOddsHandicap;

public class EuroOddsHandicapDaoImpl extends SqlMapClientDaoSupport implements EuroOddsHandicapDao {

	private static Logger LOGGER = Logger.getLogger(EuroOddsHandicapDaoImpl.class
			.getName());

	public void insertEuroOddsHandicap(EuroOddsHandicap euroOddsHandicap) {
		if (euroOddsHandicap == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertEuroOddsHandicap", euroOddsHandicap);
		}catch (Exception e){
			LOGGER.error(e);
		}
	}

	public void insertEuroOddsHandicapBatch(List<EuroOddsHandicap> euroOddsHandicapList) {
		if (euroOddsHandicapList == null) {
			LOGGER.error("euroOddsHandicapList is null, return.");
			return;
		}
		for (EuroOddsHandicap euroOddsHandicap : euroOddsHandicapList) {
			insertEuroOddsHandicap(euroOddsHandicap);
		}
	}

	public EuroOddsHandicap queryTransByDateJobType(EuroOddsHandicap query) {
		if(query == null){
			LOGGER.error("query is null, return.");
			return null;
		}
		return (EuroOddsHandicap) getSqlMapClientTemplate().queryForObject("queryTransByDateJobType", query);
	}

	@SuppressWarnings("unchecked")
	public List<EuroOddsHandicap> queryCurrJobTypeEuroHandicap(String okUrlDate) {
		if(StringUtils.isBlank(okUrlDate)){
			LOGGER.error("okUrlDate is null, return.");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryCurrJobTypeEuroHandicap", okUrlDate);
	}

}
