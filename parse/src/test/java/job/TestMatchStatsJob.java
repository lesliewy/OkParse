/**
 * 
 */
package job;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.job.MatchStatsJob;

/**
 * @author leslie
 *
 */
public class TestMatchStatsJob {

	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}
	
	@Test
	public void testProcessMatchStats(){
		MatchStatsJob matchStatsJob = (MatchStatsJob) applicationContext
				.getBean("matchStatsJob");
		matchStatsJob.processMatchStats();
	}
    
}
