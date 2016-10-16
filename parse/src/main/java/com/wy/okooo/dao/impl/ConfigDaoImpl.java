/**
 * 
 */
package com.wy.okooo.dao.impl;

import java.util.List;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.ConfigDao;
import com.wy.okooo.domain.Config;

/**
 * @author leslie
 *
 */
public class ConfigDaoImpl extends SqlMapClientDaoSupport implements ConfigDao {

	@SuppressWarnings("unchecked")
	public List<Config> queryAllConfig() {
		return getSqlMapClientTemplate().queryForList("queryAllConfig");
	}

}
