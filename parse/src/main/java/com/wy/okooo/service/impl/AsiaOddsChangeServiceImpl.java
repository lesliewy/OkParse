/**
 * 
 */
package com.wy.okooo.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.wy.okooo.dao.AsiaOddsChangeDao;
import com.wy.okooo.domain.AsiaOddsChange;
import com.wy.okooo.parse.ParseOdds;
import com.wy.okooo.parse.impl.ParseOddsImpl;
import com.wy.okooo.service.AsiaOddsChangeService;
import com.wy.okooo.util.OkConstant;
import com.wy.okooo.util.OkParseUtils;

/**
 * 解析欧盘赔率变化页面service(http://www.okooo.com/soccer/match/720252/odds/change/24/)
 * 
 * @author leslie
 *
 */
public class AsiaOddsChangeServiceImpl implements AsiaOddsChangeService {

	private static Logger LOGGER = Logger.getLogger(AsiaOddsChangeServiceImpl.class
			.getName());
	
	private AsiaOddsChangeDao asiaOddsChangeDao;
	
	private ParseOdds parser = new ParseOddsImpl();
	
	public void parseAsiaOddsChange(long matchId, int matchSeq, int corpNo) {
		asiaOddsChangeDao.insertOddsChangeBatch(parser.getAsiaOddsChange(matchId, matchSeq, corpNo));
	}
	
	public void parseAsiaOddsChangeFromFile(File asiaOddsChangeHtml) {
		asiaOddsChangeDao.insertOddsChangeBatch(parser.getAsiaOddsChangeFromFile(asiaOddsChangeHtml, false));
	}
	
	public List<AsiaOddsChange> getAsiaOddsChangeFromFile(File asiaOddsChangeHtml, boolean toGetAll) {
		return parser.getAsiaOddsChangeFromFile(asiaOddsChangeHtml, toGetAll);
	}
	
	public boolean isExistsByMatchIdAndCorpNo(long matchId, int corpNo) {
		List<AsiaOddsChange> list = queryAsiaOddsChanByCorpNo(matchId, corpNo);
		return list != null && !list.isEmpty();
	}
	
	public List<AsiaOddsChange> queryAsiaOddsChanByCorpNo(
			long matchId, int corpNo) {
		String corpName = OkParseUtils.translateCorpName(corpNo);
		return asiaOddsChangeDao.queryAsiaOddsChanByCorpName(matchId, corpName);
	}
	
	public void insertOddsChangeDaily(AsiaOddsChange asiaOddsChange) {
		asiaOddsChangeDao.insertOddsChangeDaily(asiaOddsChange);
	}

	public void insertOddsChangeDailyBatch(
			List<AsiaOddsChange> asiaOddsChangeList) {
		asiaOddsChangeDao.insertOddsChangeDailyBatch(asiaOddsChangeList);
	}
	
	public List<AsiaOddsChange> querySeqJobTypeByOkUrlDate(String okUrlDate) {
		return asiaOddsChangeDao.querySeqJobTypeByOkUrlDate(okUrlDate);
	}

	public Set<String> querySeqJobTypeInSetByOkUrlDate(String okUrlDate) {
		Set<String> result = new HashSet<String>();
		List<AsiaOddsChange> list = querySeqJobTypeByOkUrlDate(okUrlDate);
		if(list != null){
			for(AsiaOddsChange change : list){
				result.add(change.getMatchSeq() + "_" + change.getJobType());
			}
		}
		return result;
	}
	
	public List<AsiaOddsChange> queryAsiaOddsChangeDailySb(AsiaOddsChange query) {
		return asiaOddsChangeDao.queryAsiaOddsChangeDailySb(query);
	}
	
	public void deleteAsiaChangeDailyByMatchSeq(String okUrlDate,
			Integer matchSeq) {
		AsiaOddsChange deleted = new AsiaOddsChange();
		deleted.setOkUrlDate(okUrlDate);
		deleted.setMatchSeq(matchSeq);
		asiaOddsChangeDao.deleteAsiaChangeDailyByMatchSeq(deleted);
	}
	
	public void analyseAsiaOddsChangeDaily(File dir, Map<Integer, String> jobTypes, Map<String, String> cropNoNameMap,
			Set<Integer> toProcessMatchSeqs, String okUrlDate){
		List<File> asiaOddsChangeFiles = OkParseUtils.getFilesFromDir(dir, OkConstant.ASIA_ODDS_CHANGE_FILE_NAME_BASE);
		if(asiaOddsChangeFiles == null || asiaOddsChangeFiles.isEmpty()){
			return;
		}
		
		// 先删除掉现有的，保证每个matchSeq不出现2个不同的JOB_TYPE.
		for(Integer matchSeq : toProcessMatchSeqs){
			deleteAsiaChangeDailyByMatchSeq(okUrlDate, matchSeq);
		}
		List<AsiaOddsChange> changeDailyList = new ArrayList<AsiaOddsChange>();
		for(File asiaOddsChangeFile : asiaOddsChangeFiles){
			Integer matchSeq = OkParseUtils.getMatchSeqFromOddsChangeFile(asiaOddsChangeFile);
			if(!toProcessMatchSeqs.contains(matchSeq)){
				continue;
			}
			String corpNo = String.valueOf(OkParseUtils.getCorpNoFromOddsChangeFile(asiaOddsChangeFile));
			String oddsCorpName = cropNoNameMap.get(corpNo);
			List<AsiaOddsChange> asiaOddsChangeList = getAsiaOddsChangeFromFile(asiaOddsChangeFile, true);
			if(asiaOddsChangeList == null || asiaOddsChangeList.isEmpty()){
				continue;
			}
			LOGGER.info("process " + asiaOddsChangeFile.getAbsolutePath());
			// 从最初开始.
			Collections.reverse(asiaOddsChangeList);
			boolean firstAdded = false;
			Float preHandicap = 99f;
			for(AsiaOddsChange change : asiaOddsChangeList){
				Integer oddsSeq = change.getOddsSeq();
				Float handicap = change.getHandicap();
				change.setOkUrlDate(okUrlDate);
				change.setMatchSeq(matchSeq);
				change.setJobType(jobTypes.get(matchSeq));
				change.setOddsCorpName(oddsCorpName);
				// 加入最初的.
				if(!firstAdded){
					changeDailyList.add(change);
					firstAdded = true;
				}
				
				// 加入盘口不同的
				if(preHandicap < 90f && preHandicap.floatValue() != handicap.floatValue() && oddsSeq != 1){
					changeDailyList.add(change);
				}
				
				// 加入最新的, 同时确保最新和最初的不是同一个
				if(oddsSeq == 1 && asiaOddsChangeList.size() > 1){
					changeDailyList.add(change);
				}
				
				preHandicap = handicap;
			}
			insertOddsChangeDailyBatch(changeDailyList);
			changeDailyList.clear();
		}
	}

	public AsiaOddsChangeDao getAsiaOddsChangeDao() {
		return asiaOddsChangeDao;
	}

	public void setAsiaOddsChangeDao(AsiaOddsChangeDao asiaOddsChangeDao) {
		this.asiaOddsChangeDao = asiaOddsChangeDao;
	}

}
