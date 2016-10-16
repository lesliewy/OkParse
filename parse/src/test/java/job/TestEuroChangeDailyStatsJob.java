/**
 * 
 */
package job;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.job.EuroChangeDailyStatsJob;

/**
 * @author leslie
 *
 */
public class TestEuroChangeDailyStatsJob {
	
	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}
	
	@Test
	public void testProcessAnalyseEuroChangeDaily(){
		EuroChangeDailyStatsJob euroChangeDailyStatsJob = (EuroChangeDailyStatsJob) applicationContext
				.getBean("euroChangeDailyStatsJob");
		euroChangeDailyStatsJob.processAnalyseEuroChangeDaily();
	}
}
