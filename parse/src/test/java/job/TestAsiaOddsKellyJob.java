/**
 * 
 */
package job;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.job.AsiaOddsKellyJob;

/**
 * @author leslie
 *
 */
public class TestAsiaOddsKellyJob {
	
	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}
	
	@Test
	public void testProcessAsiaKelly(){
		AsiaOddsKellyJob asiaOddsKellyJob = (AsiaOddsKellyJob) applicationContext
				.getBean("asiaOddsKellyJob");
		asiaOddsKellyJob.processAsiaKelly();
	}
}
