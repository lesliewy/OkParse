/**
 * 
 */
package com.wy.okooo.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wy.okooo.dao.CorpDao;
import com.wy.okooo.domain.Corp;
import com.wy.okooo.service.CorpService;

/**
 * LOT_WEIGHT_RULE service
 * 
 * @author leslie
 *
 */
public class CorpServiceImpl implements CorpService {

	private CorpDao corpDao;

	public void insertList(List<Corp> corps) {
		corpDao.insertList(corps);
	}
	
	public Map<String, String> initialCorpMap() {
		List<Corp> corps = corpDao.queryAllCorp();
		if(corps == null || corps.isEmpty()){
			return null;
		}
		Map<String, String> map = new HashMap<String, String>();
		for(Corp corp : corps){
			map.put(corp.getCorpName(), corp.getCorpNo());
		}
		return map;
	}
	
	public List<Corp> queryAllCorp() {
		return corpDao.queryAllCorp();
	}
	
	public void updateTimeBeforeMatchList(List<Corp> corps) {
		corpDao.updateTimeBeforeMatchList(corps);
	}

	public CorpDao getCorpDao() {
		return corpDao;
	}

	public void setCorpDao(CorpDao corpDao) {
		this.corpDao = corpDao;
	}

}
