/**
 * 
 */
package service;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.service.AsiaOddsService;

/**
 * @author leslie
 *
 */
public class TestAsiaOddsService {

	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}
	
	@Test
	public void testParseAsiaOdds(){

		AsiaOddsService asiaOddsService = (AsiaOddsService) applicationContext
				.getBean("asiaOddsService");
		asiaOddsService.parseAsiaOdds(3L, 2);
	
	}
}
