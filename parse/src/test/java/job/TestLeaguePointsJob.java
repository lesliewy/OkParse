/**
 * 
 */
package job;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.job.LeaguePointsJob;

/**
 * @author leslie
 *
 */
public class TestLeaguePointsJob {

	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}
	
	@Test
	public void testProcessLeaguePoints(){
		LeaguePointsJob leaguePointsJob = (LeaguePointsJob) applicationContext
				.getBean("leaguePointsJob");
		leaguePointsJob.processLeaguePoints();
	}
    
}
