/**
 * 
 */
package com.wy.okooo.data;

import java.util.Calendar;

import org.apache.log4j.Logger;

/**
 * @author leslie
 * 
 */
public class HtmlPersistThread implements Runnable {

	private static Logger LOGGER = Logger.getLogger(HtmlPersistThread.class
			.getName());

	private HtmlPersist persist = new HtmlPersist();

	private Calendar cal = null;

	private int year = -1;

	private int month = -1;
	
	private String dir = "";

	private String threadName = "default thread name";

	/**
	 * 每天一个线程.
	 */
	 public void run() {
	 long begin = System.currentTimeMillis();
	 persist.persistAll(cal, 1, 1000);
	 LOGGER.info("thread " + threadName + " total time: "
	 + (System.currentTimeMillis() - begin) / (1000 * 60) + " min.");
	 }

	/**
	 * 每月一个线程.
	 */
//	public void run() {
//		LOGGER.info("thread begin." + Thread.currentThread().getName());
//		long begin = System.currentTimeMillis();
//
//		int index = 0;
//		int persitMax = 32;
//		Calendar cal = Calendar.getInstance();
//		cal.set(year, month, 1);
//		while (index++ < persitMax) {
//			persist.persistAll(cal);
//			cal.add(Calendar.DAY_OF_MONTH, 1);
//			if (cal.get(Calendar.MONTH) != month
//					|| cal.get(Calendar.YEAR) != year) {
//				break;
//			}
//		}
//		LOGGER.info("thread " + threadName + " total time: "
//				+ (System.currentTimeMillis() - begin) / (1000 * 60) + " min.");
//	}
	
	/**
	 * 每天一个线程, 获取 euroOddsChange 页面.
	 */
//	 public void run() {
//		 long begin = System.currentTimeMillis();
//		 persist.persistAllCorpEuroOddsChange(dir);
//		 LOGGER.info("thread " + threadName + " total time: "
//		 + (System.currentTimeMillis() - begin) / (1000 * 60) + " min.");
//	 }

	public Calendar getCal() {
		return cal;
	}

	public void setCal(Calendar cal) {
		this.cal = cal;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}
	
}
