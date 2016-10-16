package com.wy.okooo.dao.impl;

import java.util.List;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.KellyRuleDao;
import com.wy.okooo.domain.KellyRule;

public class KellyRuleDaoImpl extends SqlMapClientDaoSupport implements KellyRuleDao {

	/**
	 * 根据 matchName 查询 LOT_KELLY_RULE.
	 */
	@SuppressWarnings("unchecked")
	public List<KellyRule> queryKellyRulesByMatchName(String matchName) {
		return getSqlMapClientTemplate().queryForList("queryKellyRulesByMatchName", matchName);
	}
}
