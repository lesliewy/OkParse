/**
 * 
 */
package service;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.service.AnalyseTeamService;

/**
 * @author leslie
 *
 */
public class TestAnalyseTeamService {
	
	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}
	
	@Test
	public void testAnalyseTeamStrength(){
		AnalyseTeamService analyseTeamService = (AnalyseTeamService) applicationContext
				.getBean("analyseTeamService");
//		String teamName1 = "赫塔菲";
//		String teamName2 = "维拉利尔";
		String teamName1 = "莎索罗";
		String teamName2 = "国际米兰";
		analyseTeamService.analyseTeamStrength(teamName1);
		analyseTeamService.analyseTeamStrength(teamName2);
	}
}
