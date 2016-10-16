/**
 * 
 */
package com.wy.okooo.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.wy.okooo.dao.MatchSkipDao;
import com.wy.okooo.domain.MatchSkip;
import com.wy.okooo.service.MatchSkipService;

/**
 * LOT_MATCH_SKIP service
 * 
 * @author leslie
 *
 */
public class MatchSkipServiceImpl implements MatchSkipService {

	private MatchSkipDao matchSkipDao;

	public List<MatchSkip> querySkipMatchesByOkUrlDate(String okUrlDate) {
		return matchSkipDao.querySkipMatchesByOkUrlDate(okUrlDate);
	}
	
	public Set<Integer> querySkipMatchesByOkUrlDateInSet(String okUrlDate) {
		if(StringUtils.isBlank(okUrlDate)){
			return null;
		}
		List<MatchSkip> skipMatches = querySkipMatchesByOkUrlDate(okUrlDate);
		if(skipMatches == null){
			return null;
		}
		
		Set<Integer> result = new HashSet<Integer>();
		for(MatchSkip matchSkip : skipMatches){
			Integer matchSeq = matchSkip.getMatchSeq();
			result.add(matchSeq);
		}
		return result;
	}

	public MatchSkipDao getMatchSkipDao() {
		return matchSkipDao;
	}

	public void setMatchSkipDao(MatchSkipDao matchSkipDao) {
		this.matchSkipDao = matchSkipDao;
	}

}
