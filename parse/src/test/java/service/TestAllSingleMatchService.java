/**
 * 
 */
package service;

import java.io.File;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.service.AllSingleMatchService;

/**
 * @author leslie
 *
 */
public class TestAllSingleMatchService {

	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}
	
	@Test
	public void testParseAllMatch(){
		AllSingleMatchService allSingleMatchService = (AllSingleMatchService) applicationContext
				.getBean("allSingleMatchService");
//		String url = "http://www.okooo.com/danchang/100901/";
//		allSingleMatchService.parseAllMatch(url);
		allSingleMatchService.parseAllMatch();
//		allSingleMatchService.parseAllMatchThread();
	}
	
	@Test
	public void testParseEuroOddsThread(){
		AllSingleMatchService allSingleMatchService = (AllSingleMatchService) applicationContext
				.getBean("allSingleMatchService");
		allSingleMatchService.parseEuroOddsThread();
	}
	
	@Test
	public void testParseEuroOddsFromFile(){
		AllSingleMatchService allSingleMatchService = (AllSingleMatchService) applicationContext
				.getBean("allSingleMatchService");
		File dir = new File("/home/leslie/MyProject/OkParse/html/2015/03/02");
		allSingleMatchService.parseEuroOddsFromFile(dir);;
	}

}
