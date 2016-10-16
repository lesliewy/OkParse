/**
 * 
 */
package job;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.job.ProbAverageJob;

/**
 * @author leslie
 *
 */
public class TestProbAverageJob {
	
	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}
	
	@Test
	public void testProcessProbAverage(){
		ProbAverageJob probAverageJob = (ProbAverageJob) applicationContext
				.getBean("probAverageJob");
		probAverageJob.processProbAverage();
	}
	
}
