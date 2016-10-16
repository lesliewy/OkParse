/**
 * 
 */
package service;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.wy.okooo.data.HtmlPersistThread;

/**
 * @author leslie
 * 
 */
public class TestHtmlPersistThread {

	private static Logger LOGGER = Logger
			.getLogger(TestHtmlPersistThread.class.getName());
	
	/**
	 * 每天一个线程.
	 */
	@Test
	public void testOneDayPerThread() {
		ExecutorService service = Executors.newFixedThreadPool(32);

		// 设置初始日期
		int beginYear = 2014;
		int beginMonth = 9;
		int beginDay = 1;

		int persitMax = 40;
		int index = 0;
		while (index++ < persitMax) {
			Calendar cal = Calendar.getInstance();
			// month 实际月份要 +1.
			cal.set(beginYear, beginMonth, beginDay++, 00, 00);
			
			if (cal.get(Calendar.MONTH) > beginMonth) {
				break;
			}

			HtmlPersistThread thread = new HtmlPersistThread();
			thread.setCal(cal);
			thread.setThreadName("thread" + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH));
			service.execute(thread);
		}
			
			try {
				Thread.currentThread().join();
			} catch (InterruptedException e) {
				LOGGER.error(e);
			}
	}
	
	@Test
	public void testOneMonthPerThread(){
		ExecutorService service = Executors.newFixedThreadPool(32);
		
		// 设置初始日期, 2010-09-01, 之前的就没有数据了.
		int year = 2014;
		int month = 0;
		
		int index = 0;
		int persitMax = 9;
		while(index++ < persitMax){
			HtmlPersistThread thread = new HtmlPersistThread();
			thread.setYear(year);
			thread.setMonth(month++);
			thread.setThreadName("thread" + "-" + month);
			service.execute(thread);
		}
		
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}
	}
	
	@Test
	public void testOneDayPerThreadAllEuroOddsChange(){
		ExecutorService service = Executors.newFixedThreadPool(32);

		String parentDir = "/home/leslie/MyProject/OkParse/html/2014/09/";
		
		int persitMax = 31;
		int index = 0;
		while (index++ < persitMax) {
			
			String dir = parentDir + StringUtils.leftPad(String.valueOf(index), 2, '0');
			LOGGER.info("dir: " + dir);
			HtmlPersistThread thread = new HtmlPersistThread();
			thread.setDir(dir);
			thread.setThreadName("thread" + "-" + index);
			service.execute(thread);
		}
			
			try {
				Thread.currentThread().join();
			} catch (InterruptedException e) {
				LOGGER.error(e);
			}
	
	}
}
