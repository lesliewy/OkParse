/**
 * 
 */
package service;

import java.io.File;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.service.AnalyseCorpUtilService;
import com.wy.okooo.util.OkConstant;

/**
 * @author leslie
 *
 */
public class TestAnalyseCorpUtilService {

	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}
	
	@Test
	public void testPersistEuroOddsChange(){
		AnalyseCorpUtilService analyseCorpUtilService = (AnalyseCorpUtilService) applicationContext
				.getBean("analyseCorpUtilService");
		String dirPath = OkConstant.FILE_PATH_BASE;
//		String[] okUrlPaths = {"2015/01/01/", "2015/01/02/", "2015/01/03/", "2015/01/04/", "2015/01/05/"};
		String[] okUrlPaths = {"2015/02/05/"};
		String[] corpNames = {"澳门彩票", "威廉.希尔", "立博", "99家平均", "Bet365", "Interwetten", "伟德国际", 
				"易胜博", "Oddset", "STS", "12bet.com"
				};
		
		// test begin
//		String[] okUrlPaths = {"2015/01/01/"};
//		String[] corpNames = {"澳门彩票", "威廉.希尔", "立博", "Interwetten", "伟德国际", 
//				"易胜博", "Oddset", "STS", "12bet.com"
//				};
//		File dir = new File(dirPath + "2015/01/01/");
//		analyseCorpUtilService.persistEuroOddsChangeAll(dir, "威廉.希尔");
		// test end
		
		for(String okUrlPath : okUrlPaths){
			File dir = new File(dirPath + okUrlPath);
			for(String corpName : corpNames){
				analyseCorpUtilService.persistEuroOddsChangeAll(dir, corpName);
			}
		}
	}
	
}
