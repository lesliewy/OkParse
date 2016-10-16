/**
 * 
 */
package com.wy.okooo.service.impl.thread;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.wy.okooo.domain.Match;
import com.wy.okooo.service.AnalyseService;

/**
 * 获取用于 kelly 指数预测的 euroOddsChange html. 
 * 
 * @author leslie
 *
 */
public class PersistCorpEuroOddsChangeKellyThread implements Runnable{

	private static Logger LOGGER = Logger.getLogger(PersistCorpEuroOddsChangeKellyThread.class
			.getName());
	
	private AnalyseService analyseService;
	
	private Calendar cal;
	
	private int beginMatchSeq = 1;
	
	private int endMatchSeq = 500;
	
	private String baseDir = "/home/leslie/MyProject/OkParse/html/";
	
	private List<Match> matches;
	
	private Set<Integer> limitedMatchSeqs;
	
	private boolean replace = false;
	
	private boolean reGetMatchHtml = false;
	
	public void run() {
		long parseAsiaOddsChangeBegin = System.currentTimeMillis();
		LOGGER.info("process: " + beginMatchSeq + " - " + endMatchSeq);
		analyseService.persistCorpEuroOddsChangeKelly(baseDir, cal, beginMatchSeq, endMatchSeq, matches, limitedMatchSeqs, replace, reGetMatchHtml);
		
		LOGGER.info("progress success: " + beginMatchSeq + " - " + endMatchSeq + "; eclipsed "
				+ (System.currentTimeMillis() - parseAsiaOddsChangeBegin)
				+ " ms...");
	}

	public AnalyseService getAnalyseService() {
		return analyseService;
	}

	public void setAnalyseService(AnalyseService analyseService) {
		this.analyseService = analyseService;
	}

	public Calendar getCal() {
		return cal;
	}

	public void setCal(Calendar cal) {
		this.cal = cal;
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

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public List<Match> getMatches() {
		return matches;
	}

	public void setMatches(List<Match> matches) {
		this.matches = matches;
	}

	public Set<Integer> getLimitedMatchSeqs() {
		return limitedMatchSeqs;
	}

	public void setLimitedMatchSeqs(Set<Integer> limitedMatchSeqs) {
		this.limitedMatchSeqs = limitedMatchSeqs;
	}

	public boolean isReplace() {
		return replace;
	}

	public void setReplace(boolean replace) {
		this.replace = replace;
	}

	public boolean isReGetMatchHtml() {
		return reGetMatchHtml;
	}

	public void setReGetMatchHtml(boolean reGetMatchHtml) {
		this.reGetMatchHtml = reGetMatchHtml;
	}

}
