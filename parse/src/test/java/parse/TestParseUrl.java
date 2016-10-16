package parse;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.wy.okooo.parse.ParseMatches;
import com.wy.okooo.parse.impl.ParseMatchesImpl;
import com.wy.okooo.parse.impl.ParseOkoooUrl;

public class TestParseUrl {

	@Test
	public void testFindMatchUrlByMatchId() {
		ParseOkoooUrl betFairParser = new ParseOkoooUrl();
		int matchId = 74;
		String matchUrl = betFairParser.findOneMatchUrl(matchId);
		System.out.println("matchUrl: " + matchUrl);
	}

	@Test
	public void testFindMatchUrl() {
		ParseOkoooUrl betFairParser = new ParseOkoooUrl();
		List<String> matchUrls = betFairParser.findOneMatchUrl();
		System.out.println("matchUrls length: " + matchUrls.size()
				+ "; matchUrls: " + matchUrls);
	}

	@Test
	public void testFindProfitLossUrl() {
		ParseOkoooUrl betFairParser = new ParseOkoooUrl();
		String matchUrl = "http://www.okooo.com/soccer/match/725671/odds/";
		String profitLossUrl = betFairParser.findProfitLossUrl(matchUrl);
		System.out.println("profitLossUrl: " + profitLossUrl);
	}

	@Test
	public void testFindProbability() {
		ParseOkoooUrl betFairParser = new ParseOkoooUrl();
		String profitLossUrl = "http://www.okooo.com/soccer/match/725671/exchanges/";
		Map<String, String> probability = betFairParser
				.findProbability(profitLossUrl);
		System.out.println("probability: " + probability);
	}

	@Test
	public void testFindAllMatchProbability() {
		long begin = System.currentTimeMillis();
		ParseOkoooUrl betFairParser = new ParseOkoooUrl();
		List<Map<String, String>> allProbability = betFairParser
				.findAllMatchProbability();
		System.out.println("size: " + allProbability.size());
		System.out.println("allProbability: " + allProbability);
		System.out.println("eclipsed time: "
				+ (System.currentTimeMillis() - begin) + " ms.");

	}
	
	@Test
	public void testGetAllMatchFromUrl(){
		ParseMatches parser = new ParseMatchesImpl();
		parser.getAllMatchFromUrl(0, 0);
	}

}
