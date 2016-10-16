package com.wy.okooo.parse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

public interface ParseUrl {
	
	String findMatchUrl();
	
	String findEuroOddsUrl(Document doc, int matchSeq);
	
	String findAsiaOddsUrl(Document doc, int matchSeq);
	
	String findEuroOddsChangeUrl(Document doc, int matchSeq, int corpNo);
	
	String findAsiaOddsChangeUrl(Document doc, int matchSeq, int corpNo);
	
	String findExchangeUrl(Document doc, int matchSeq);
	
	String findExchangeDetailUrl(Document doc, int matchSeq);

	String findOneMatchUrl(int matchId) throws IOException;
	
	List<String> findOneMatchUrl() throws IOException;
	
	String findProfitLossUrl(String matchUrl);
	
	Map<String, String> findProbability(String profitLossUrl);
}
