/**
 * 
 */
package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.Config;

/**
 * 配置参数DAO.
 * @author leslie
 *
 */
public interface ConfigDao {
	
	List<Config> queryAllConfig();
	
}
