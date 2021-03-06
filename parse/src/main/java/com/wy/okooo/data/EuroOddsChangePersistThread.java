/**
 * 
 */
package com.wy.okooo.data;

import org.apache.log4j.Logger;

/**
 * @author leslie
 *
 */
public class EuroOddsChangePersistThread implements Runnable{

	private static Logger LOGGER = Logger
			.getLogger(EuroOddsChangePersistThread.class.getName());
	
	private HtmlPersist persist = new HtmlPersist();
	
	private String dir = null;
	
	public void run() {
		long begin = System.currentTimeMillis();
		persist.persistEuroOddsChangeBatch(dir);
		LOGGER.info("total time: " + (System.currentTimeMillis() - begin)/(1000*60) + " min.");
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}


}
