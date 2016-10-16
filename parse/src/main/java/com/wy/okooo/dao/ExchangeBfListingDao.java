/**
 * 
 */
package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.ExchangeBfListing;

/**
 * LOT_BF_LISTING
 * 
 * @author leslie
 *
 */
public interface ExchangeBfListingDao {
	void insert(ExchangeBfListing bfListing);
	
	void insertBatch(List<ExchangeBfListing> bfListings);
}
