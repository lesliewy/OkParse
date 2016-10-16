/**
 * 
 */
package com.wy.okooo.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wy.okooo.dao.ConfigDao;
import com.wy.okooo.domain.Config;
import com.wy.okooo.service.ConfigService;

/**
 * 配置参数 service
 * 
 * @author leslie
 *
 */
public class ConfigServiceImpl implements ConfigService {

	private ConfigDao configDao;

	public List<Config> queryAllConfig() {
		return configDao.queryAllConfig();
	}
	
	public Map<String, String> queryAllConfigInMap() {
		List<Config> configList = queryAllConfig();
		if(configList == null){
			return null;
		}
		Map<String, String> result = new HashMap<String, String>();
		for(Config config : configList){
			result.put(config.getName(), config.getValue());
		}
		return result;
	}

	public ConfigDao getConfigDao() {
		return configDao;
	}

	public void setConfigDao(ConfigDao configDao) {
		this.configDao = configDao;
	}

}
