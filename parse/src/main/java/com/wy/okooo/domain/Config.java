/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * 配置参数.
 * @author leslie
 *
 */
public class Config {

	private String name;
	
	private String value;
	
	private Timestamp timestamp;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}
