/**
 * 
 */
package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.AsiaOddsTrendsDao;
import com.wy.okooo.domain.AsiaOddsTrends;

/**
 * LOT_ODDS_AISA_TRENDS DAO.
 * 
 * @author leslie
 *
 */
public class AsiaOddsTrendsDaoImpl extends SqlMapClientDaoSupport implements AsiaOddsTrendsDao{

	private static Logger LOGGER = Logger.getLogger(AsiaOddsTrendsDaoImpl.class
			.getName());
	
	public void insertAsiaOddsTrends(AsiaOddsTrends asiaOddsTrends) {
		if (asiaOddsTrends == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertAsiaOddsTrends",asiaOddsTrends);
		}catch (Exception e){
			LOGGER.info("insertAsiaOddsTrends: " + e);
		}
	}

	public void insertAsiaOddsTrendsBatch(List<AsiaOddsTrends> asiaOddsTrendsList) {
		if(asiaOddsTrendsList == null){
			return;
		}
		for(AsiaOddsTrends odd : asiaOddsTrendsList){
			insertAsiaOddsTrends(odd);
		}
	}

	@SuppressWarnings("unchecked")
	public List<AsiaOddsTrends> queryAsiaTrendsByRange(AsiaOddsTrends queryTrends) {
		if(queryTrends == null){
			LOGGER.info("queryTrends is null, return now...");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryAsiaTrendsByRange", queryTrends);
	}

	@SuppressWarnings("unchecked")
	public List<AsiaOddsTrends> queryAsiaTrendsByJobType(
			AsiaOddsTrends queryTrends) {
		if(queryTrends == null){
			LOGGER.info("queryTrends is null, return now...");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryAsiaTrendsByJobType", queryTrends);
	}

	@SuppressWarnings("unchecked")
	public List<AsiaOddsTrends> queryCurrMatchJobType(String okUrlDate) {
		if(StringUtils.isBlank(okUrlDate)){
			LOGGER.info("okUrlDate is blank, return null...");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryCurrMatchJobType", okUrlDate);
	}

	public AsiaOddsTrends queryKellyTrendsByDateJobType(AsiaOddsTrends query) {
		if(query == null){
			LOGGER.info("query is null, return now...");
			return null;
		}
		return (AsiaOddsTrends) getSqlMapClientTemplate().queryForObject("queryKellyTrendsByDateJobType", query);
	}
	
	@SuppressWarnings("unchecked")
	public List<AsiaOddsTrends> queryAsiaOddsTrendsByOkUrlDate(AsiaOddsTrends query){
		if(query == null){
			LOGGER.info("query is null, return now...");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryAsiaOddsTrendsByOkUrlDate", query);
	}
}
