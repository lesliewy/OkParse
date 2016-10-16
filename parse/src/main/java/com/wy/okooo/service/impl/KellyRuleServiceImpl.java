/**
 * 
 */
package com.wy.okooo.service.impl;

import java.util.List;

import com.wy.okooo.dao.KellyRuleDao;
import com.wy.okooo.domain.KellyRule;
import com.wy.okooo.service.KellyRuleService;

/**
 * LOT_KELLY_RULE service
 * 
 * @author leslie
 *
 */
public class KellyRuleServiceImpl implements KellyRuleService {

	private KellyRuleDao kellyRuleDao;

	public List<KellyRule> queryKellyRulesByMatchName(String matchName) {
		return kellyRuleDao.queryKellyRulesByMatchName(matchName);
	}

	public KellyRuleDao getKellyRuleDao() {
		return kellyRuleDao;
	}

	public void setKellyRuleDao(KellyRuleDao kellyRuleDao) {
		this.kellyRuleDao = kellyRuleDao;
	}
}
