package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.KellyRule;

/**
 * LOT_KELLY_RULE DAO
 * 
 * @author leslie
 *
 */
public interface KellyRuleDao {
	
	List<KellyRule> queryKellyRulesByMatchName(String matchName);
}
