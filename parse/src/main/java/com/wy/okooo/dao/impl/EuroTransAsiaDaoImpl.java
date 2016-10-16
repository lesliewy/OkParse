/**
 * 
 */
package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.EuroTransAsiaDao;
import com.wy.okooo.domain.EuroTransAsia;

/**
 * LOT_EURO_TRANS_ASIA DAO.
 * 
 * @author leslie
 *
 */
public class EuroTransAsiaDaoImpl extends SqlMapClientDaoSupport implements EuroTransAsiaDao{

	private static Logger LOGGER = Logger.getLogger(EuroTransAsiaDaoImpl.class
			.getName());
	
	public void insertEuroTransAsia(EuroTransAsia euroTransAsia) {
		if (euroTransAsia == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertEuroTransAsia",euroTransAsia);
		}catch (Exception e){
			LOGGER.info("insertAsiaOdds: " + e);
		}
		
	}

	public void insertEuroTransAsiaBatch(List<EuroTransAsia> euroTransAsiaList) {
		if(euroTransAsiaList == null){
			return;
		}
		for(EuroTransAsia euroTransAsia : euroTransAsiaList){
			insertEuroTransAsia(euroTransAsia);
		}
	}

	@SuppressWarnings("unchecked")
	public List<EuroTransAsia> queryEuroTransAsiaByOkUrlDate(EuroTransAsia query) {
		if(query == null){
			LOGGER.error("query is null, return null");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryEuroTransAsiaByOkUrlDate",query);
	}

	@SuppressWarnings("unchecked")
	public List<EuroTransAsia> queryEuroTransAsiaByDateType(EuroTransAsia query) {
		if(query == null){
			LOGGER.error("query is null, return null");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryEuroTransAsiaByDateType",query);
	}

}
