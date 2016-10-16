/**
 * 
 */
package com.wy.okooo.service.impl;

import java.util.List;

import com.wy.okooo.dao.WeightRuleDao;
import com.wy.okooo.domain.WeightRule;
import com.wy.okooo.service.WeightRuleService;

/**
 * LOT_WEIGHT_RULE service
 * 
 * @author leslie
 *
 */
public class WeightRuleServiceImpl implements WeightRuleService {

	private WeightRuleDao weightRuleDao;

	public List<WeightRule> queryWeightRulesByType(String ruleType) {
		return weightRuleDao.queryWeightRulesByType(ruleType);
	}

	public WeightRuleDao getWeightRuleDao() {
		return weightRuleDao;
	}


	public void setWeightRuleDao(WeightRuleDao weightRuleDao) {
		this.weightRuleDao = weightRuleDao;
	}
	
}
