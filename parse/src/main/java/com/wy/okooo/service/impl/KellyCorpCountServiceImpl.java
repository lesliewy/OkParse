/**
 * 
 */
package com.wy.okooo.service.impl;

import java.util.List;

import com.wy.okooo.dao.KellyCorpCountDao;
import com.wy.okooo.domain.KellyCorpCount;
import com.wy.okooo.service.KellyCorpCountService;

/**
 * LOT_KELLY_CORP_COUNT service
 * 
 * @author leslie
 *
 */
public class KellyCorpCountServiceImpl implements KellyCorpCountService {

	private KellyCorpCountDao kellyCorpCountDao;

	public void insert(KellyCorpCount corpCount) {
		kellyCorpCountDao.insert(corpCount);
	}

	public void insertList(List<KellyCorpCount> corpCountList) {
		kellyCorpCountDao.insertList(corpCountList);
	}
	
	public void deleteCorpCountByMatchName(String matchName) {
		kellyCorpCountDao.deleteCorpCountByMatchName(matchName);
	}

	public KellyCorpCountDao getKellyCorpCountDao() {
		return kellyCorpCountDao;
	}

	public void setKellyCorpCountDao(KellyCorpCountDao kellyCorpCountDao) {
		this.kellyCorpCountDao = kellyCorpCountDao;
	}

}
