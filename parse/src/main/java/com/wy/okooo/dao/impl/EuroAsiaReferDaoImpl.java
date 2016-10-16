/**
 * 
 */
package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.EuroAsiaReferDao;
import com.wy.okooo.domain.EuroAsiaRefer;

/**
 * @author leslie
 *
 */
public class EuroAsiaReferDaoImpl extends SqlMapClientDaoSupport implements EuroAsiaReferDao {

	private static Logger LOGGER = Logger.getLogger(EuroAsiaReferDaoImpl.class
			.getName());
	
	public void insertEuroAsiaRefer(EuroAsiaRefer euroAsiaRefer) {
		if (euroAsiaRefer == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertEuroAsiaRefer",euroAsiaRefer);
		}catch (Exception e){
			LOGGER.info("insertEuroAsiaRefer: " + e);
		}
	}

	public void insertEuroAsiaReferBatch(List<EuroAsiaRefer> euroAsiaReferList) {
		if(euroAsiaReferList == null){
			return;
		}
		for(EuroAsiaRefer euroAsiaRefer : euroAsiaReferList){
			insertEuroAsiaRefer(euroAsiaRefer);
		}
	
	}
	
	@SuppressWarnings("unchecked")
	public List<EuroAsiaRefer> queryAllEuroAsiaRefer() {
		return getSqlMapClientTemplate().queryForList("queryAllEuroAsiaRefer");
	}

}
