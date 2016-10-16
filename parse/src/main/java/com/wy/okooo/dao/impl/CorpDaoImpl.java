package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.CorpDao;
import com.wy.okooo.domain.Corp;

public class CorpDaoImpl extends SqlMapClientDaoSupport implements CorpDao {

	private static Logger LOGGER = Logger.getLogger(CorpDaoImpl.class
			.getName());

	/**
	 * 将map 中的corp信息插入数据库.
	 */
	public void insertList(List<Corp> corps) {
		if(corps == null || corps.isEmpty()){
			return;
		}
		for( Corp corp: corps){
			insert(corp);
		}
	}
	
	public void insert(Corp corp){
		if(corp == null){
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertCorp", corp);
		}catch (Exception e){
//			LOGGER.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Corp> queryAllCorp() {
		return getSqlMapClientTemplate().queryForList("queryAllCorp");
	}

	public void updateTimeBeforeMatch(Corp corp) {
		if(corp == null){
			return;
		}
		try{
			getSqlMapClientTemplate().update("updateTimeBeforeMatch", corp);
		}catch (Exception e){
			LOGGER.error(e);
		}
		
	}

	public void updateTimeBeforeMatchList(List<Corp> corps) {
		if(corps == null || corps.isEmpty()){
			return;
		}
		for( Corp corp: corps){
			updateTimeBeforeMatch(corp);
		}
	}
}
