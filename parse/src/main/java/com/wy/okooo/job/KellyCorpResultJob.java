/**
 * 
 */
package com.wy.okooo.job;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.wy.okooo.domain.KellyCorpResult;
import com.wy.okooo.domain.MatchJob;
import com.wy.okooo.service.AnalyseService;
import com.wy.okooo.service.KellyCorpResultService;
import com.wy.okooo.service.MatchJobService;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * 
 * 定时更新已经结束的比赛的LOT_KELLY_CORP_RESULT信息.
 * 
 * @author leslie
 * 
 */
public class KellyCorpResultJob {
	
	private static Logger LOGGER = Logger.getLogger(KellyCorpResultJob.class
			.getName());
	
	private AnalyseService analyseService;
	
	private MatchJobService matchJobService;
	
	private KellyCorpResultService kellyCorpResultService;
	
	public void processKellyCorpResult(){
		LOGGER.info("processKellyCorpResult job begin...");
		long begin = System.currentTimeMillis();
		

		// 获取 LOT_JOB 中已经完场的okUrlDate(okUrlDate递减排序中从第二个开始)
		List<MatchJob> matchJobsList = matchJobService.queryOkUrlDateFromMatchJob();
		List<KellyCorpResult> kellyMatchResultList = kellyCorpResultService.queryLatestOkUrlDateFromKellyCorpResult();
		List<String> okUrlDateMatchJobs = new ArrayList<String>();
		List<String> okUrlDateKellyCorpResult = new ArrayList<String>();
		if(matchJobsList != null){
			for(MatchJob matchJob : matchJobsList){
				okUrlDateMatchJobs.add(matchJob.getOkUrlDate());
			}
		}
		if(kellyMatchResultList != null){
			for(KellyCorpResult kellyCorpResult : kellyMatchResultList){
				okUrlDateKellyCorpResult.add(kellyCorpResult.getOkUrlDate());
			}
		}
		// 去掉当前正在执行的okUrlDate, 因为不是所有比赛都结束了.
		if(!okUrlDateMatchJobs.isEmpty()){
			okUrlDateMatchJobs.remove(0);
		}
		
		int beginMatchSeq = 0;
		int endMatchSeq = 1000;
		int numOfThread = 10;
		for(String okUrlDate : okUrlDateMatchJobs){
			Calendar cal = OkParseUtils.buildCalByOkUrlDate(okUrlDate);
			if(cal == null){
				continue;
			}
			if(okUrlDateKellyCorpResult.contains(okUrlDate)){
				continue;
			}
			
			LOGGER.info("process OkUrlDate: " + okUrlDate);
			long beginOkUrlDate = System.currentTimeMillis();
			// persistCorpEuroOddsChangeKellyThread 会先获取最新的match.html
			analyseService.persistCorpEuroOddsChangeKellyThread(OkConstant.FILE_PATH_BASE, cal, numOfThread, beginMatchSeq, endMatchSeq, null, null, false, true);
			// 再次执行，确保文件都已下载.
			analyseService.persistCorpEuroOddsChangeKellyThread(OkConstant.FILE_PATH_BASE, cal, numOfThread, beginMatchSeq, endMatchSeq, null, null, false, true);
			analyseService.persistCorpEuroOddsChangeKelly(OkConstant.FILE_PATH_BASE, cal, beginMatchSeq, endMatchSeq, null, null, false, true);
			analyseService.persistCorpEuroOddsChangeKelly(OkConstant.FILE_PATH_BASE, cal, beginMatchSeq, endMatchSeq, null, null, false, true);
			
			analyseService.kellyAnalyseK23Thread(null, OkConstant.FILE_PATH_BASE + OkParseUtils.getDirPahtFromOkUrlDate(okUrlDate) + "/", beginMatchSeq, endMatchSeq, null, null, null);
			LOGGER.info("process OkUrlDate: " + okUrlDate + " end. total time: " + (System.currentTimeMillis() - beginOkUrlDate)/1000 + " s.");
		}
		LOGGER.info("processKellyCorpResult job end. total time: " + (System.currentTimeMillis() - begin)/1000 + " s.");
	}

	public AnalyseService getAnalyseService() {
		return analyseService;
	}

	public void setAnalyseService(AnalyseService analyseService) {
		this.analyseService = analyseService;
	}

	public MatchJobService getMatchJobService() {
		return matchJobService;
	}

	public void setMatchJobService(MatchJobService matchJobService) {
		this.matchJobService = matchJobService;
	}

	public KellyCorpResultService getKellyCorpResultService() {
		return kellyCorpResultService;
	}

	public void setKellyCorpResultService(
			KellyCorpResultService kellyCorpResultService) {
		this.kellyCorpResultService = kellyCorpResultService;
	}

}
