/**
 * 
 */
package service;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.service.AnalyseForTestService;

/**
 * @author leslie
 *
 */
public class TestAnalyseForTestService {

	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}
	
	@Test
	public void testAnalyseEuroCorpsKelly(){
		AnalyseForTestService analyseForTestService = (AnalyseForTestService) applicationContext
				.getBean("analyseForTestService");
		analyseForTestService.analyseEuroCorpsKelly();
	}
	
	@Test
	public void testAnalyseEuroCorpsResult(){
		AnalyseForTestService analyseForTestService = (AnalyseForTestService) applicationContext
				.getBean("analyseForTestService");
		analyseForTestService.analyseEuroCorpsResult();
	}
	
}
