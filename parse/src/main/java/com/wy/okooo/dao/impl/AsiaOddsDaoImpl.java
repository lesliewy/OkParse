/**
 * 
 */
package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.AsiaOddsDao;
import com.wy.okooo.domain.AsiaOdds;

/**
 * LOT_ODDS_AISA DAO.
 * 
 * @author leslie
 *
 */
public class AsiaOddsDaoImpl extends SqlMapClientDaoSupport implements AsiaOddsDao{

	private static Logger LOGGER = Logger.getLogger(AsiaOddsDaoImpl.class
			.getName());
	
	public void insertOdds(AsiaOdds asiaOdds) {
		if (asiaOdds == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertAsiaOdds",asiaOdds);
		}catch (Exception e){
			LOGGER.info("insertAsiaOdds: " + e);
		}
		
	}

	public void insertOddsBatch(List<AsiaOdds> asiaOdds) {
		if(asiaOdds == null){
			return;
		}
		for(AsiaOdds odd : asiaOdds){
			insertOdds(odd);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<AsiaOdds> queryAsiaOddsByMatchId(long matchId) {
		return getSqlMapClientTemplate().queryForList("queryAsiaOddsByMatchId", matchId);
	}

}
