/**
 * 
 */
package job;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.job.ScoreOddsJob;

/**
 * @author leslie
 *
 */
public class TestScoreOddsJob {

	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}
	
	@Test
	public void testProcessScoreOdds(){
		ScoreOddsJob scoreOddsJob = (ScoreOddsJob) applicationContext
				.getBean("scoreOddsJob");
		scoreOddsJob.processScoreOdds();
	}
    
}
