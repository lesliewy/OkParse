/**
 * 
 */
package service;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.compensate.MatchComp;

/**
 * @author leslie
 *
 */
public class TestMatchComp {
	
	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}
	
	@Test
	public void testMatchComp(){
		MatchComp matchComp = (MatchComp) applicationContext
				.getBean("matchComp");
		String url = "http://www.okooo.com/danchang/100901/";
		matchComp.matchComp(url);
	}
}
