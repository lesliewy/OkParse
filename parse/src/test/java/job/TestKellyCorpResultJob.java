/**
 * 
 */
package job;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.job.KellyCorpResultJob;

/**
 * @author leslie
 *
 */
public class TestKellyCorpResultJob {

	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}
	
	@Test
	public void testProcessKellyCorpResult(){
		KellyCorpResultJob kellyCorpResultJob = (KellyCorpResultJob) applicationContext
				.getBean("kellyCorpResultJob");
		kellyCorpResultJob.processKellyCorpResult();
	}
    
}
