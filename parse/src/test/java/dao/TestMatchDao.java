package dao;

import java.sql.Timestamp;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.okooo.dao.MatchDao;
import com.wy.okooo.dao.impl.MatchDaoImpl;
import com.wy.okooo.domain.Match;

public class TestMatchDao {

	private static ApplicationContext applicationContext = null; // 提供静态ApplicationContext
	static {
		applicationContext = new ClassPathXmlApplicationContext(
				"conf/applicationContext.xml"); // 实例化
	}

	@Test
	public void testInsertMatch() {
		MatchDao matchDAO = (MatchDaoImpl) applicationContext
				.getBean("matchDao");
		Match match = new Match();
		match.setMatchName("欧洲杯");
		match.setMatchSeq(3);
		match.setMatchTime(new Timestamp(System.currentTimeMillis()));
		match.setCloseTime(new Timestamp(System.currentTimeMillis()));
		match.setHostTeamName("AC米兰");
		match.setVisitingTeamName("皇家马德里");
		match.setTimestamp(new Timestamp(System.currentTimeMillis()));
		matchDAO.insertMatch(match);
	}
	
	@Test
	public void testDeleteMatch() {
		MatchDao matchDAO = (MatchDaoImpl) applicationContext
				.getBean("matchDao");
		matchDAO.deleteMatch(0);
	}
}
