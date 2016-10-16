package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.ScoreOddsDao;
import com.wy.okooo.domain.ScoreOdds;

public class ScoreOddsDaoImpl extends SqlMapClientDaoSupport implements ScoreOddsDao {

	private static Logger LOGGER = Logger.getLogger(ScoreOddsDaoImpl.class
			.getName());

	public void insertScoreOdds(ScoreOdds scoreOdds) {
		if (scoreOdds == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertScoreOdds", scoreOdds);
		}catch (Exception e){
			LOGGER.error(e);
		}
	}

	public void deleteScoreOdds(Long okMatchId, String intervalType) {
		if(okMatchId == null || (StringUtils.isBlank(intervalType))){
			LOGGER.info("okMatchId is null or intervalType is blank, return now.");
			return;
		}
		ScoreOdds scoreOdds = new ScoreOdds();
		scoreOdds.setOkMatchId(okMatchId);
		scoreOdds.setIntervalType(intervalType);
		getSqlMapClientTemplate().delete("deleteScoreOdds", scoreOdds);
	}
	
	public void deleteScoreOdds(List<ScoreOdds> deletedScoreOddsList) {
		if(deletedScoreOddsList == null){
			LOGGER.info("deletedScoreOddsList is null, return now.");
			return;
		}
		for(ScoreOdds scoreOdds : deletedScoreOddsList){
			deleteScoreOdds(scoreOdds.getOkMatchId(), scoreOdds.getIntervalType());
		}
		
	}

	public void insertScoreOddsBatch(List<ScoreOdds> scoreOddsList) {
		if (scoreOddsList == null) {
			LOGGER.error("scoreOddsList is null, return.");
			return;
		}
		for (ScoreOdds scoreOdds : scoreOddsList) {
			insertScoreOdds(scoreOdds);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ScoreOdds> queryScoreOddsByOkUrlDate(String okUrlDate) {
		if(StringUtils.isBlank(okUrlDate)){
			LOGGER.info("okUrlDate is blank, return now.");
			return null;
		}
		return getSqlMapClientTemplate().queryForList("queryScoreOddsByOkUrlDate", okUrlDate);
	}


}
