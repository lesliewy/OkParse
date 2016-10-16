/**
 * 
 */
package com.wy.okooo.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.wy.okooo.dao.ExchangeBfListingDao;
import com.wy.okooo.domain.ExchangeBfListing;

/**
 * LOT_BF_LISTING
 * 
 */
public class ExchangeBfListingDaoImpl extends SqlMapClientDaoSupport implements
		ExchangeBfListingDao {

	private static Logger LOGGER = Logger.getLogger(ExchangeBfListingDaoImpl.class
			.getName());
	
	public void insert(ExchangeBfListing bfListing) {
		if (bfListing == null) {
			return;
		}
		try{
			getSqlMapClientTemplate().insert("insertBfListing", bfListing);
		}catch (Exception e){
			LOGGER.info("insertBfListing: " + e);
		}
	}

	public void insertBatch(List<ExchangeBfListing> bfListings) {
		if (bfListings == null) {
			return;
		}
		for (ExchangeBfListing one : bfListings) {
			insert(one);
		}

	}

}
