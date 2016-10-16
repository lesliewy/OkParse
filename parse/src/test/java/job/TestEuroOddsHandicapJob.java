/**
 * 
 */
package job;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.job.EuroOddsHandicapJob;

/**
 * @author leslie
 *
 */
public class TestEuroOddsHandicapJob {
	
	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}
	
	@Test
	public void testProcessEuroOddsHandicap(){
		EuroOddsHandicapJob euroOddsHandicapJob = (EuroOddsHandicapJob) applicationContext
				.getBean("euroOddsHandicapJob");
		euroOddsHandicapJob.processEuroOddsHandicap();
	}
}
