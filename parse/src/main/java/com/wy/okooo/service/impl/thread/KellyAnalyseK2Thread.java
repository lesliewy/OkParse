/**
 * 
 */
package com.wy.okooo.service.impl.thread;

import java.util.List;
import java.util.Set;

import com.wy.okooo.domain.Match;
import com.wy.okooo.service.AnalyseService;

/**
 * @author leslie
 *
 */
public class KellyAnalyseK2Thread implements Runnable{

	private AnalyseService analyseService;
	
	private int beginMatchSeq = 1;
	
	private int endMatchSeq = 500;
	
	private String matchDir = "/home/leslie/MyProject/OkParse/html/daily/match";
	
	private Set<Integer> limitedMatchSeqs;
	
	private List<Match> matches;
	
	public void run() {
		analyseService.kellyAnalyseK2(matches, matchDir, beginMatchSeq, endMatchSeq, limitedMatchSeqs);
	}

	public AnalyseService getAnalyseService() {
		return analyseService;
	}

	public void setAnalyseService(AnalyseService analyseService) {
		this.analyseService = analyseService;
	}

	public int getBeginMatchSeq() {
		return beginMatchSeq;
	}

	public void setBeginMatchSeq(int beginMatchSeq) {
		this.beginMatchSeq = beginMatchSeq;
	}

	public int getEndMatchSeq() {
		return endMatchSeq;
	}

	public void setEndMatchSeq(int endMatchSeq) {
		this.endMatchSeq = endMatchSeq;
	}

	public String getMatchDir() {
		return matchDir;
	}

	public void setMatchDir(String matchDir) {
		this.matchDir = matchDir;
	}

	public Set<Integer> getLimitedMatchSeqs() {
		return limitedMatchSeqs;
	}

	public void setLimitedMatchSeqs(Set<Integer> limitedMatchSeqs) {
		this.limitedMatchSeqs = limitedMatchSeqs;
	}

	public List<Match> getMatches() {
		return matches;
	}

	public void setMatches(List<Match> matches) {
		this.matches = matches;
	}

}
