/**
 * 
 */
package service;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.service.EuroOddsService;

/**
 * @author leslie
 *
 */
public class TestEuroOddsService {

	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}
	
	@Test
	public void testParseEuroOdds(){

		EuroOddsService euroOddsService = (EuroOddsService) applicationContext
				.getBean("euroOddsService");
		euroOddsService.parseEuroOdds(3L, 2, 0);
	
	}
}
