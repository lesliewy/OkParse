/**
 * 
 */
package com.wy.okooo.service;

import java.util.List;
import java.util.Map;

import com.wy.okooo.domain.Config;

/**
 * 配置参数 Service
 * @author leslie
 *
 */
public interface ConfigService {

	List<Config> queryAllConfig();
	
	Map<String, String> queryAllConfigInMap();
	
}
