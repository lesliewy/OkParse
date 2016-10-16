package parse;

import org.junit.Test;

import com.wy.okooo.parse.ParseOdds;
import com.wy.okooo.parse.impl.ParseOddsImpl;

public class TestParseOdds {

	@Test
	public void testGetEuropeOddsChange() {
		ParseOdds parseOdds = new ParseOddsImpl();
		int matchId = 74;
		int matchSeq = 4;
		int corpNo = 14;
		parseOdds.getEuropeOddsChange(matchId, matchSeq, corpNo, 0, false);
	}
	
	@Test
	public void testGetAsiaOddsChange() {
		ParseOdds parseOdds = new ParseOddsImpl();
		int matchId = 74;
		int matchSeq = 4;
		int corpNo = 322;
		parseOdds.getAsiaOddsChange(matchId, matchSeq, corpNo);
	}

}
