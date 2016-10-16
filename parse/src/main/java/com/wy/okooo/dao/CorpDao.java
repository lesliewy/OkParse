package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.Corp;

/**
 * LOT_CORPS DAO
 * 
 * @author leslie
 *
 */
public interface CorpDao {
	void insert(Corp corp);
	
	void insertList(List<Corp> corps);
	
	List<Corp> queryAllCorp();
	
	void updateTimeBeforeMatch(Corp corp);
	
	void updateTimeBeforeMatchList(List<Corp> corp);
}
