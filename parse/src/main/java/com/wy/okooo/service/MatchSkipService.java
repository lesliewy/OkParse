/**
 * 
 */
package com.wy.okooo.service;

import java.util.List;
import java.util.Set;

import com.wy.okooo.domain.MatchSkip;

/**
 * LOT_MATCH_SKIP service
 * 
 * @author leslie
 *
 */
public interface MatchSkipService {
	
	List<MatchSkip> querySkipMatchesByOkUrlDate(String okUrlDate);
	
	Set<Integer> querySkipMatchesByOkUrlDateInSet(String okUrlDate);
}
