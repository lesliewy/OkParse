/**
 * 
 */
package com.wy.okooo.service;

import java.util.List;

import com.wy.okooo.domain.KellyRule;

/**
 * LOT_KELLY_RULE
 * 
 * @author leslie
 *
 */
public interface KellyRuleService {
	
	List<KellyRule> queryKellyRulesByMatchName(String matchName);
	
}
