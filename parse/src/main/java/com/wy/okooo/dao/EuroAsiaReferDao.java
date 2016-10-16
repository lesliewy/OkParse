/**
 * 
 */
package com.wy.okooo.dao;

import java.util.List;

import com.wy.okooo.domain.EuroAsiaRefer;

/**
 * @author leslie
 *
 */
public interface EuroAsiaReferDao {
	
	void insertEuroAsiaRefer(EuroAsiaRefer euroAsiaRefer);
	
	void insertEuroAsiaReferBatch(List<EuroAsiaRefer> euroAsiaReferList);
	
	List<EuroAsiaRefer> queryAllEuroAsiaRefer();
}
