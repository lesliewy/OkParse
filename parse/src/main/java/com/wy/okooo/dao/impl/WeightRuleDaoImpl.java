package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.WeightRuleDao;
import com.wy.okooo.domain.WeightRule;

public class WeightRuleDaoImpl extends SqlMapClientDaoSupport implements WeightRuleDao {

	private static Logger LOGGER = Logger.getLogger(WeightRuleDaoImpl.class
			.getName());

	/**
	 * 查询所有的 WeightRule
	 */
	@SuppressWarnings("unchecked")
	public List<WeightRule> queryWeightRulesByType(String ruleType) {
		LOGGER.info("begin...");
		return getSqlMapClientTemplate().queryForList("queryWeightRulesByType", ruleType);
	}
}
