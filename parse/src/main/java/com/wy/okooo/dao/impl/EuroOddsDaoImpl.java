/**
 * 
 */
package com.wy.okooo.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.EuroOddsDao;
import com.wy.okooo.domain.EuropeOdds;

/**
 * LOT_ODDS_EURO DAO.
 * @author leslie
 *
 */
public class EuroOddsDaoImpl extends SqlMapClientDaoSupport implements EuroOddsDao{

	private static Logger LOGGER = Logger.getLogger(EuroOddsDaoImpl.class
			.getName());
	
	public void insertOdds(EuropeOdds europeOdds) {
		if(europeOdds == null){
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertEuroOdds",europeOdds);  
		}catch (Exception e){
			LOGGER.info("insertEuroOdds: " + e);
		}
	}

	public void insertOddsBatch(List<EuropeOdds> europeOdds) {
		if(europeOdds == null){
			return;
		}
		for(EuropeOdds odd : europeOdds){
			insertOdds(odd);  
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<EuropeOdds> queryAllCorpNames() {
		return getSqlMapClientTemplate().queryForList("queryAllCorpNames");  
	}

	@SuppressWarnings("unchecked")
	public List<EuropeOdds> queryEuropeOddsByKey(EuropeOdds europeOdds) {
		return getSqlMapClientTemplate().queryForList("queryEuropeOddsByKey", europeOdds);
	}
	
	public EuropeOdds queryEuropeOddsByOkId(String okUrlDate,
			Integer matchSeq) {
		if(StringUtils.isBlank(okUrlDate) || matchSeq == null){
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("okUrlDate", okUrlDate);
		map.put("matchSeq", matchSeq);
		return (EuropeOdds) getSqlMapClientTemplate().queryForObject("queryEuropeOddsByOkId", map);
	}

	@SuppressWarnings("unchecked")
	public List<EuropeOdds> queryEuroOddsByCorpName(String corpName) {
		return getSqlMapClientTemplate().queryForList("queryEuroOddsByCorpName", corpName);
	}
}
