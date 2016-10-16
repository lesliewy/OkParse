/**
 * 
 */
package com.wy.okooo.service;

import java.util.List;

import com.wy.okooo.domain.WeightRule;

/**
 * LOT_WEIGHT_RULE
 * 
 * @author leslie
 *
 */
public interface WeightRuleService {
	
	List<WeightRule> queryWeightRulesByType(String ruleType);
	
}
