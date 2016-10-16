/**
 * 
 */
package job;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.job.ParseAndMailJob;
import com.wy.okooo.util.MailUtils;

/**
 * @author leslie
 *
 */
public class TestParseAndMailJob {
	
	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}
	
	@Test
	public void testProcessMatch(){
		ParseAndMailJob parseAndMailJob = (ParseAndMailJob) applicationContext
				.getBean("parseAndMailService");
		parseAndMailJob.processMatch();
	}
	
	@Test
	public void testSendMail() throws MessagingException{
		Map<String, String> mailMap = new HashMap<String, String>();
		mailMap.put("jobType", "A0");
		mailMap.put("okUrlDate", "14");
		mailMap.put("beginMatchSeq", String.valueOf(12));
		mailMap.put("endMatchSeq", String.valueOf(14));
		MailUtils.sendMailTo163("AAA".toString(), mailMap);
	}
}
