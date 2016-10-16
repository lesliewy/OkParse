package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.MatchSkip;

/**
 * LOT_MATCH_SKIP DAO
 * 
 * @author leslie
 *
 */
public interface MatchSkipDao {
	List<MatchSkip> querySkipMatchesByOkUrlDate(String okUrlDate);
}
