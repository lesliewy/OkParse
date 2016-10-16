/**
 * 
 */
package com.wy.okooo.service;

import java.util.List;

import com.wy.okooo.domain.EuroAsiaRefer;

/**
 * @author leslie
 *
 */
public interface EuroAsiaReferService {
	
	void insertEuroAsiaRefer(EuroAsiaRefer euroAsiaRefer);
	
	void insertEuroAsiaReferBatch(List<EuroAsiaRefer> euroAsiaReferList);
	
	List<EuroAsiaRefer> queryAllEuroAsiaRefer();
}
