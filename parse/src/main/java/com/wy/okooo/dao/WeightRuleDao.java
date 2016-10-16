package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.WeightRule;

/**
 * LOT_WEIGHT_RULE DAO
 * 
 * @author leslie
 *
 */
public interface WeightRuleDao {
	
	List<WeightRule> queryWeightRulesByType(String ruleType);
}
