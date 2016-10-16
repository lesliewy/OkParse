/**
 * 
 */
package com.wy.okooo.compensate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.wy.okooo.domain.Match;
import com.wy.okooo.service.AsiaOddsChangeService;
import com.wy.okooo.service.AsiaOddsService;
import com.wy.okooo.service.EuroOddsChangeService;
import com.wy.okooo.service.EuroOddsService;
import com.wy.okooo.service.ExchangeService;
import com.wy.okooo.service.SingleMatchService;
import com.wy.okooo.util.OkConstant;

/**
 * 补偿没有插入成功的记录.
 * 
 * @author leslie
 *
 */
public class MatchComp {
	private static Logger LOGGER = Logger
			.getLogger(MatchComp.class.getName());
	
	private SingleMatchService singleMatchService;
	
	private EuroOddsService euroOddsService;
	
	private AsiaOddsService asiaOddsService;
	
	private EuroOddsChangeService euroOddsChangeService;
	
	private AsiaOddsChangeService asiaOddsChangeService;
	
	private ExchangeService exchangeService;
	
	/**
	 * LOT_MATCH 补偿;
	 * @param url http://www.okooo.com/danchang/100901/
	 */
	public void matchComp(String url){
		/*
		 * 获取单场胜平负页面所有的Match对象;
		 */
		long getAllMatchFromUrlBegin = System.currentTimeMillis();
		List<Match> matches = singleMatchService.getAllMatchFromUrl(url, 0, 0);
		if (matches == null || matches.isEmpty()) {
			LOGGER.error("matches is null or empty. return now...");
			return;
		}
		LOGGER.info("progress: find " + matches.size() + " matches, eclipsed "
				+ (System.currentTimeMillis() - getAllMatchFromUrlBegin)
				+ " ms...");

		/*
		 * 插入 LOT_MATCH: 将不存在 LOT_MATCH 中的match 插入.
		 */
		long insertMatchBatchBegin = System.currentTimeMillis();
		// url 中最后的时间不能作为okooo的比赛日期; 利用比赛时间计算.
		
//		String[] splitUrlArr = StringUtils.split(url, "/");
//		String day = splitUrlArr[splitUrlArr.length-1];
//		Timestamp beginTime = Timestamp.valueOf("20" + day.substring(0,2) + "-" + day.substring(2, 4) + "-" + day.substring(4,6) + " 10:00:00");
//		Calendar endCal = Calendar.getInstance();
//		endCal.setTimeInMillis(beginTime.getTime());
//		endCal.add(Calendar.DAY_OF_MONTH, 1);
		
		Match matchTemp = matches.get(0);
		Timestamp matchTime = matchTemp.getMatchTime();
		Calendar matchcal = Calendar.getInstance();
		matchcal.setTimeInMillis(matchTime.getTime());
		Calendar beginCal = Calendar.getInstance();
		beginCal.set(matchcal.get(Calendar.YEAR), matchcal.get(Calendar.MONTH), matchcal.get(Calendar.DAY_OF_MONTH),
				10, 0);
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(beginCal.getTimeInMillis());
		if(matchcal.after(beginCal)){
			endCal.add(Calendar.DAY_OF_MONTH, 1);
		}else{
			beginCal.add(Calendar.DAY_OF_MONTH, -1);
		}
		
		List<Match> existedMatches = singleMatchService.queryExistedMatchesByTime(new Timestamp(beginCal.getTimeInMillis()),
				new Timestamp(endCal.getTimeInMillis()));
		List<Match> toInsertMatches = new ArrayList<Match>();
		for(Match match : matches){
			if(!existedMatches.contains(match)){
				toInsertMatches.add(match);
			}
		}
		
		singleMatchService.insertMatchBatch(toInsertMatches);
		LOGGER.info("progress: insert LOT_MATCH success, eclipsed "
				+ (System.currentTimeMillis() - insertMatchBatchBegin)
				+ " ms..., toInsertMatches: " + toInsertMatches.size());
	}
	
	/**
	 * LOT_ODDS_EURO 补偿;
	 */
	public void euroOddsComp(String url){
		List<Match> matches = singleMatchService.getAllMatchFromUrl(url, 0, 0);
		/*
		 * 查询是否存在, 只有不存在时才解析;
		 */
		for(Match match : matches){
			if(!euroOddsService.isExistsByDateSeq(match.getOkUrlDate(), match.getMatchSeq())){
				euroOddsService.parseEuroOdds(match.getId(), match.getMatchSeq(), 0);
			}
		}
	}
	
	/**
	 * LOT_ODDS_ASIA 补偿
	 * @param url
	 */
	public void asiaOddsComp(String url){
		List<Match> matches = singleMatchService.getAllMatchFromUrl(url, 0, 0);
		
		/*
		 * 查询是否存在, 只有不存在时才解析;
		 */
		for(Match match : matches){
			if(!asiaOddsService.isExistsByMatchId(match.getId())){
				asiaOddsService.parseAsiaOdds(match.getId(), match.getMatchSeq());
			}
		}
	}
	
	/**
	 * LOT_ODDS_EURO_CHANGE 补偿
	 * @param url
	 */
	public void euroOddsChangeComp(String url){
		List<Match> matches = singleMatchService.getAllMatchFromUrl(url, 0, 0);
		/*
		 * 查询是否存在, 只有不存在时才解析;
		 */
		for(Match match : matches){
			for (int corpNo : OkConstant.ODDS_CORP_TR_EURO) {
				if(!euroOddsChangeService.isExistsByMatchIdAndCorpNo(match.getId(), corpNo)){
					euroOddsChangeService.parseEuroOddsChange(match.getId(),
							match.getMatchSeq(), corpNo, 0, false);
				}
			}
		}
	}
	
	/**
	 * LOT_ODDS_ASIA_CHANGE 补偿;
	 * @param url
	 */
	public void asiaOddsChangeComp(String url){
		List<Match> matches = singleMatchService.getAllMatchFromUrl(url, 0, 0);
		/*
		 * 查询是否存在, 只有不存在时才解析;
		 */
		for(Match match : matches){
			for (int corpNo : OkConstant.ODDS_CORP_TR_ASIA) {
				if(!asiaOddsChangeService.isExistsByMatchIdAndCorpNo(match.getId(), corpNo)){
					asiaOddsChangeService.parseAsiaOddsChange(match.getId(),
							match.getMatchSeq(), corpNo);
				}
			}
		}
	}
	
	/**
	 * LOT_ALL_AVERAGE LOT_BF_LISTING LOT_TRANS_PROP 补偿
	 */
	public void exchangeInfoComp(String url){
		List<Match> matches = singleMatchService.getAllMatchFromUrl(url, 0, 0);
		
		for (Match match : matches) {
			if(!exchangeService.isExistsAllAvgById(match.getId())){
				exchangeService.parseExchangeInfo(match.getId(),
						match.getMatchSeq());
			}
		}
	}
	
	/**
	 * LOT_BF_TURNOVER_DETAIL 补偿;
	 */
	public void bfTurnoverDetailComp(String url){
		List<Match> matches = singleMatchService.getAllMatchFromUrl(url, 0, 0);
		
		for (Match match : matches) {
			if(!exchangeService.isExistsTurnoverDetailById(match.getId())){
				exchangeService.parseBfTurnoverDetail(match.getId(),
						match.getMatchSeq());
			}
		}
	}

	public SingleMatchService getSingleMatchService() {
		return singleMatchService;
	}

	public void setSingleMatchService(SingleMatchService singleMatchService) {
		this.singleMatchService = singleMatchService;
	}

	public EuroOddsService getEuroOddsService() {
		return euroOddsService;
	}

	public void setEuroOddsService(EuroOddsService euroOddsService) {
		this.euroOddsService = euroOddsService;
	}

	public AsiaOddsService getAsiaOddsService() {
		return asiaOddsService;
	}

	public void setAsiaOddsService(AsiaOddsService asiaOddsService) {
		this.asiaOddsService = asiaOddsService;
	}

	public EuroOddsChangeService getEuroOddsChangeService() {
		return euroOddsChangeService;
	}

	public void setEuroOddsChangeService(EuroOddsChangeService euroOddsChangeService) {
		this.euroOddsChangeService = euroOddsChangeService;
	}

	public AsiaOddsChangeService getAsiaOddsChangeService() {
		return asiaOddsChangeService;
	}

	public void setAsiaOddsChangeService(AsiaOddsChangeService asiaOddsChangeService) {
		this.asiaOddsChangeService = asiaOddsChangeService;
	}

	public ExchangeService getExchangeService() {
		return exchangeService;
	}

	public void setExchangeService(ExchangeService exchangeService) {
		this.exchangeService = exchangeService;
	}
}
