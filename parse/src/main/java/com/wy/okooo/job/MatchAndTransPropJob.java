/**
 * 
 */
package com.wy.okooo.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.wy.okooo.data.HtmlPersist;
import com.wy.okooo.domain.Match;
import com.wy.okooo.domain.MatchJob;
import com.wy.okooo.service.AllSingleMatchService;
import com.wy.okooo.service.MatchJobService;
import com.wy.okooo.service.SingleMatchService;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * 定时获取已经结束的okUrlDate的比赛信息(match.html && exchangeInfo_11.html), 插入 LOT_MATCH 和 LOT_TRANS_PROP.
 * 
 * @author leslie
 * 
 */
public class MatchAndTransPropJob {
	
	private static Logger LOGGER = Logger.getLogger(MatchAndTransPropJob.class
			.getName());
	
	private SingleMatchService singleMatchService;
	
	private AllSingleMatchService allSingleMatchService;
	
	private MatchJobService matchJobService;
	
	private HtmlPersist persist = new HtmlPersist();
	
	public void processMatchAndTransProp(){
		LOGGER.info("processMatchAndTransProp job begin...");
		long begin = System.currentTimeMillis();
		// 构造路径， 对于 LOT_JOB 中已经完场的okUrlDate(okUrlDate递减排序中从第二个开始), 且在 LOT_MATCH 中不存在的.
		List<MatchJob> matchJobsList = matchJobService.queryOkUrlDateFromMatchJob();
		List<Match> matchList = singleMatchService.queryOkUrlDateFromMatch("141201");
		List<String> okUrlDateMatchJobs = new ArrayList<String>();
		List<String> okUrlDateMatch = new ArrayList<String>();
		if(matchJobsList != null){
			for(MatchJob matchJob : matchJobsList){
				okUrlDateMatchJobs.add(matchJob.getOkUrlDate());
			}
		}
		if(matchList != null){
			for(Match match : matchList){
				okUrlDateMatch.add(match.getOkUrlDate());
			}
		}

		// 去掉当前正在执行的okUrlDate, 因为不是所有比赛都结束了.
		if(!okUrlDateMatchJobs.isEmpty()){
			okUrlDateMatchJobs.remove(0);
		}
		
		for(String okUrlDate : okUrlDateMatchJobs){
			if(okUrlDateMatch.contains(okUrlDate)){
				continue;
			}
			
			LOGGER.info("process okUrlDate: " + okUrlDate);
			// 首先获取最新的match.html
			persist.persistMatchByOkUrlDate(okUrlDate);
			// 插入 LOT_MATCH
			String htmlDir = OkConstant.FILE_PATH_BASE + "/" + OkParseUtils.getDirPahtFromOkUrlDate(okUrlDate);
			allSingleMatchService.parseSingleMatch(htmlDir);
			
			// 获取exchangeInfo_10.html
			persist.persistExchangeInfoBatch(htmlDir, 0, 1000, null, false);
			// 再次执行
			persist.persistExchangeInfoBatch(htmlDir, 0, 1000, null, false);
			persist.persistExchangeInfoBatch(htmlDir, 0, 1000, null, false);
			// 插入 LOT_ALL_AVERAGE LOT_BF_LISTING LOT_TRANS_PROP
			allSingleMatchService.parseExchangeInfo(htmlDir);
		}
		LOGGER.info("processMatchAndTransProp total time: " + (System.currentTimeMillis() - begin)/1000 + " s.");
	}
	
	public SingleMatchService getSingleMatchService() {
		return singleMatchService;
	}

	public void setSingleMatchService(SingleMatchService singleMatchService) {
		this.singleMatchService = singleMatchService;
	}

	public MatchJobService getMatchJobService() {
		return matchJobService;
	}

	public void setMatchJobService(MatchJobService matchJobService) {
		this.matchJobService = matchJobService;
	}

	public AllSingleMatchService getAllSingleMatchService() {
		return allSingleMatchService;
	}

	public void setAllSingleMatchService(AllSingleMatchService allSingleMatchService) {
		this.allSingleMatchService = allSingleMatchService;
	}
	
}
