/**
 * 
 */
package com.wy.okooo.service.impl;

import java.util.List;

import com.wy.okooo.dao.EuroAsiaReferDao;
import com.wy.okooo.domain.EuroAsiaRefer;
import com.wy.okooo.service.EuroAsiaReferService;

/**
 * @author leslie
 *
 */
public class EuroAsiaReferServiceImpl implements EuroAsiaReferService {
	
	private EuroAsiaReferDao euroAsiaReferDao;
	
	public void insertEuroAsiaRefer(EuroAsiaRefer euroAsiaRefer) {
		euroAsiaReferDao.insertEuroAsiaRefer(euroAsiaRefer);
	}

	public void insertEuroAsiaReferBatch(List<EuroAsiaRefer> euroAsiaReferList) {
		euroAsiaReferDao.insertEuroAsiaReferBatch(euroAsiaReferList);
	}

	public EuroAsiaReferDao getEuroAsiaReferDao() {
		return euroAsiaReferDao;
	}

	public void setEuroAsiaReferDao(EuroAsiaReferDao euroAsiaReferDao) {
		this.euroAsiaReferDao = euroAsiaReferDao;
	}

	public List<EuroAsiaRefer> queryAllEuroAsiaRefer() {
		return euroAsiaReferDao.queryAllEuroAsiaRefer();
	}

}
