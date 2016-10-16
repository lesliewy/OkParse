/**
 * 
 */
package com.wy.okooo.service.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.wy.okooo.dao.IndexStatsDao;
import com.wy.okooo.domain.IndexStats;
import com.wy.okooo.parse.ParseOdds;
import com.wy.okooo.parse.impl.ParseOddsImpl;
import com.wy.okooo.service.IndexStatsService;
import com.wy.okooo.util.JsoupUtils;

/**
 * @author leslie
 *
 */
public class IndexStatsServiceImpl implements IndexStatsService {

	private IndexStatsDao indexStatsDao;
	
	private ParseOdds parser = new ParseOddsImpl();
	
	private static Logger LOGGER = Logger
			.getLogger(IndexStatsServiceImpl.class.getName());
	
	public void insertIndexStats(IndexStats indexStats) {
		indexStatsDao.insertIndexStats(indexStats);
	}

	public void insertIndexStatsBatch(List<IndexStats> indexStatsList){
		indexStatsDao.insertIndexStatsBatch(indexStatsList);
	}

	public Set<String> querySeqJobTypeByOkUrlDate(String okUrlDate) {
		Set<String> result = new HashSet<String>();
		List<IndexStats> list = queryAllByOkUrlDate(okUrlDate);
		if(list != null){
			for(IndexStats indexStats : list){
				result.add(indexStats.getMatchSeq() + "_" + indexStats.getJobType());
			}
		}
		return result;
	}
	
	public List<IndexStats> queryAllByOkUrlDate(String okUrlDate){
		return indexStatsDao.queryAllByOkUrlDate(okUrlDate);
	}
	
	public void parseIndexStatsFromFile(File indexStatsHtml, IndexStats indexStatsInit) {
		indexStatsDao.insertIndexStats(parser.getIndexStatsFromFile(indexStatsHtml, indexStatsInit));
	}
	
	public void parseIndexStats(String indexStatsUrl, String encoding, IndexStats indexStatsInit) {
		Map<String, String> params = new HashMap<String, String>();
		// 获取okooo指数ajax请求的内容.
		String okoooInfo = "";
		String okoooExpInfo = "";
		if(StringUtils.isBlank(okoooInfo) || okoooInfo.contains("错误")){
			try {
				params.put("type", "okooo");
				okoooInfo = JsoupUtils.getAjaxDocByHttpClient(indexStatsUrl, encoding, params);
			} catch (IOException e) {
				LOGGER.error("info is blank, okooo: " + e);
			}
		}
		if(StringUtils.isBlank(okoooExpInfo) || okoooExpInfo.contains("错误")){
			try{
				params.put("type", "okoooexponent");
				okoooExpInfo = JsoupUtils.getAjaxDocByHttpClient(indexStatsUrl, encoding, params);
			}catch (IOException e) {
				LOGGER.error("info is blank, okooo: " + e);
			}
		}
		// 再次执行.
		if(StringUtils.isBlank(okoooInfo) || okoooInfo.contains("错误")){
			try {
				params.put("type", "okooo");
				okoooInfo = JsoupUtils.getAjaxDocByHttpClient(indexStatsUrl, encoding, params);
			} catch (IOException e) {
				LOGGER.error("info is blank, okooo: " + e);
			}
		}
		if(StringUtils.isBlank(okoooExpInfo) || okoooExpInfo.contains("错误")){
			try{
				params.put("type", "okoooexponent");
				okoooExpInfo = JsoupUtils.getAjaxDocByHttpClient(indexStatsUrl, encoding, params);
			}catch (IOException e) {
				LOGGER.error("info is blank, okooo: " + e);
			}
		}
		// 再次执行.
		if(StringUtils.isBlank(okoooInfo) || okoooInfo.contains("错误")){
			try {
				params.put("type", "okooo");
				okoooInfo = JsoupUtils.getAjaxDocByHttpClient(indexStatsUrl, encoding, params);
			} catch (IOException e) {
				LOGGER.error("info is blank, okooo: " + e);
			}
		}
		if(StringUtils.isBlank(okoooExpInfo) || okoooExpInfo.contains("错误")){
			try{
				params.put("type", "okoooexponent");
				okoooExpInfo = JsoupUtils.getAjaxDocByHttpClient(indexStatsUrl, encoding, params);
			}catch (IOException e) {
				LOGGER.error("info is blank, okooo: " + e);
			}
		}
		
		IndexStats indexStats = new IndexStats();
		indexStats.setOkUrlDate(indexStatsInit.getOkUrlDate());
		indexStats.setMatchSeq(indexStatsInit.getMatchSeq());
		indexStats.setJobType(indexStatsInit.getJobType());
		if(StringUtils.isBlank(okoooInfo) || StringUtils.isBlank(okoooExpInfo) 
				|| okoooInfo.contains("错误") || okoooExpInfo.contains("错误")
				|| okoooInfo.length() < 10 || okoooExpInfo.length() < 10){
			return;
		}
		
		Map<String, Float> okoooMap = indexStatsJsonToMap("{" + okoooInfo + "}", "okooo");
		indexStats.setInitOkoooHost(okoooMap.get("initOkoooHost"));
		indexStats.setInitOkoooEven(okoooMap.get("initOkoooEven"));
		indexStats.setInitOkoooVisiting(okoooMap.get("initOkoooVisiting"));
		indexStats.setOkoooHost(okoooMap.get("okoooHost"));
		indexStats.setOkoooEven(okoooMap.get("okoooEven"));
		indexStats.setOkoooVisiting(okoooMap.get("okoooVisiting"));
		
		Map<String, Float> okoooExpMap = indexStatsJsonToMap("{" + okoooExpInfo + "}", "okoooexponent");
		indexStats.setInitStdDevHost(okoooExpMap.get("initStdDevHost"));
		indexStats.setInitStdDevEven(okoooExpMap.get("initStdDevEven"));
		indexStats.setInitStdDevVisiting(okoooExpMap.get("initStdDevVisiting"));
		indexStats.setStdDevHost(okoooExpMap.get("stdDevHost"));
		indexStats.setStdDevEven(okoooExpMap.get("stdDevEven"));
		indexStats.setStdDevVisiting(okoooExpMap.get("stdDevVisiting"));
		indexStats.setTimestamp(new Timestamp(Calendar.getInstance()
				.getTimeInMillis()));
		indexStatsDao.insertIndexStats(indexStats);
	}
	
	/**
	 * 这里使用org.json,简单点， 没有用json-lib. json-lib方便和bean互转.
	 * okooo: {"data":{"home":[[1429257600000,0.66],[1429261200000,0.66],[1429264800000,0.66],[1429268400000,0.66],[1429272000000,0.66],[1429275600000,0.66],[1429279200000,0.66],[1429282800000,0.66],[1429286400000,0.67],[1429290000000,0.67],[1429293600000,0.67],[1429297200000,0.67],[1429300800000,0.66],[1429304400000,0.66],[1429308000000,0.66],[1429311600000,0.66],[1429315200000,0.67],[1429318800000,0.67],[1429322400000,0.67],[1429326000000,0.67],[1429329600000,0.67],[1429333200000,0.67],[1429336800000,0.67],[1429339200000,0.67]],"draw":[[1429257600000,0.87],[1429261200000,0.87],[1429264800000,0.86],[1429268400000,0.87],[1429272000000,0.87],[1429275600000,0.87],[1429279200000,0.87],[1429282800000,0.87],[1429286400000,0.87],[1429290000000,0.87],[1429293600000,0.87],[1429297200000,0.87],[1429300800000,0.86],[1429304400000,0.86],[1429308000000,0.86],[1429311600000,0.86],[1429315200000,0.86],[1429318800000,0.86],[1429322400000,0.86],[1429326000000,0.86],[1429329600000,0.85],[1429333200000,0.85],[1429336800000,0.85],[1429339200000,0.87]],"away":[[1429257600000,0.25],[1429261200000,0.25],[1429264800000,0.25],[1429268400000,0.25],[1429272000000,0.25],[1429275600000,0.25],[1429279200000,0.25],[1429282800000,0.25],[1429286400000,0.25],[1429290000000,0.25],[1429293600000,0.25],[1429297200000,0.25],[1429300800000,0.24],[1429304400000,0.24],[1429308000000,0.24],[1429311600000,0.24],[1429315200000,0.25],[1429318800000,0.25],[1429322400000,0.25],[1429326000000,0.25],[1429329600000,0.25],[1429333200000,0.25],[1429336800000,0.25],[1429339200000,0.25]]},"odds":{"start":{"home":"0.50","draw":"0.92","away":"0.39"},"end":{"home":"0.67","draw":"0.87","away":"0.25"}},"min":0}
	 * okoooexponent: {"data":{"home":[[1429257600000,16.12],[1429261200000,16.16],[1429264800000,16.19],[1429268400000,16.22],[1429272000000,16.22],[1429275600000,16.22],[1429279200000,16.22],[1429282800000,16.22],[1429286400000,16.22],[1429290000000,16.22],[1429293600000,16.19],[1429297200000,16.22],[1429300800000,16.16],[1429304400000,16.16],[1429308000000,16.16],[1429311600000,16.16],[1429315200000,16.12],[1429318800000,16.12],[1429322400000,16.12],[1429326000000,16.12],[1429329600000,16],[1429333200000,16],[1429336800000,16],[1429339200000,16.03]],"draw":[[1429257600000,3.46],[1429261200000,3.46],[1429264800000,3.46],[1429268400000,3.46],[1429272000000,3.46],[1429275600000,3.46],[1429279200000,3.46],[1429282800000,3.46],[1429286400000,3.46],[1429290000000,3.46],[1429293600000,3.46],[1429297200000,3.46],[1429300800000,3.46],[1429304400000,3.46],[1429308000000,3.46],[1429311600000,3.46],[1429315200000,3.46],[1429318800000,3.46],[1429322400000,3.46],[1429326000000,3.46],[1429329600000,3.61],[1429333200000,3.61],[1429336800000,3.61],[1429339200000,3.61]],"away":[[1429257600000,13.64],[1429261200000,13.67],[1429264800000,13.71],[1429268400000,13.71],[1429272000000,13.71],[1429275600000,13.71],[1429279200000,13.71],[1429282800000,13.71],[1429286400000,13.67],[1429290000000,13.67],[1429293600000,13.67],[1429297200000,13.67],[1429300800000,13.67],[1429304400000,13.67],[1429308000000,13.78],[1429311600000,13.78],[1429315200000,13.67],[1429318800000,13.71],[1429322400000,13.71],[1429326000000,13.71],[1429329600000,13.71],[1429333200000,13.75],[1429336800000,13.71],[1429339200000,13.71]]},"odds":{"start":{"home":"7.42","draw":"1.73","away":"9.11"},"end":{"home":"16.03","draw":"3.61","away":"13.71"}},"min":0}
	 * @param jsonString
	 * @return
	 * @throws JSONException
	 */
    @SuppressWarnings("unchecked")
	private Map<String, Float> indexStatsJsonToMap(String jsonString, String type) throws JSONException {
    	String homeStart = "";
    	String drawStart = "";
    	String awayStart = "";
    	String homeEnd = "";
    	String drawEnd = "";
    	String awayEnd = "";
    	if("okooo".equalsIgnoreCase(type)){
    		homeStart = "initOkoooHost";
    		drawStart = "initOkoooEven";
    		awayStart = "initOkoooVisiting";
    		homeEnd = "okoooHost";
    		drawEnd = "okoooEven";
    		awayEnd = "okoooVisiting";
    	}else if("okoooexponent".equalsIgnoreCase(type)){
    		homeStart = "initStdDevHost";
    		drawStart = "initStdDevEven";
    		awayStart = "initStdDevVisiting";
    		homeEnd = "stdDevHost";
    		drawEnd = "stdDevEven";
    		awayEnd = "stdDevVisiting";
    	}
    	
        JSONObject jsonObject = new JSONObject(jsonString);
        Map<String, Float> result = new HashMap<String, Float>();
        Iterator<String> iterator1 = jsonObject.keys();
        
		// "data":{"home":[[1429264800000,0.66],[1429268400000,0.66],[1429272000000,0.66],[1429275600000,0.66],[1429279200000,0.66],[1429282800000,0.66],[1429286400000,0.67],[1429290000000,0.67],[1429293600000,0.67],[1429297200000,0.67],[1429300800000,0.66],[1429304400000,0.66],[1429308000000,0.66],[1429311600000,0.66],[1429315200000,0.67],[1429318800000,0.67],[1429322400000,0.67],[1429326000000,0.67],[1429329600000,0.67],[1429333200000,0.67],[1429336800000,0.67],[1429340400000,0.67],[1429344000000,0.67],[1429348176000,0.67]],"draw":[[1429264800000,0.86],[1429268400000,0.87],[1429272000000,0.87],[1429275600000,0.87],[1429279200000,0.87],[1429282800000,0.87],[1429286400000,0.87],[1429290000000,0.87],[1429293600000,0.87],[1429297200000,0.87],[1429300800000,0.86],[1429304400000,0.86],[1429308000000,0.86],[1429311600000,0.86],[1429315200000,0.86],[1429318800000,0.86],[1429322400000,0.86],[1429326000000,0.86],[1429329600000,0.85],[1429333200000,0.85],[1429336800000,0.85],[1429340400000,0.85],[1429344000000,0.87],[1429348176000,0.87]],"away":[[1429264800000,0.25],[1429268400000,0.25],[1429272000000,0.25],[1429275600000,0.25],[1429279200000,0.25],[1429282800000,0.25],[1429286400000,0.25],[1429290000000,0.25],[1429293600000,0.25],[1429297200000,0.25],[1429300800000,0.24],[1429304400000,0.24],[1429308000000,0.24],[1429311600000,0.24],[1429315200000,0.25],[1429318800000,0.25],[1429322400000,0.25],[1429326000000,0.25],[1429329600000,0.25],[1429333200000,0.25],[1429336800000,0.25],[1429340400000,0.25],[1429344000000,0.25],[1429348176000,0.25]]},
		// "odds":{"start":{"home":"0.50","draw":"0.92","away":"0.39"},"end":{"home":"0.67","draw":"0.87","away":"0.25"}},
		// "min":0}
        while (iterator1.hasNext()) {
            String key1 = (String) iterator1.next();
            // odds
            if("odds".equalsIgnoreCase(key1)){
            	// {"start":{"home":"0.50","draw":"0.92","away":"0.39"},"end":{"home":"0.67","draw":"0.87","away":"0.25"}}
            	JSONObject jsonObject1 = jsonObject.getJSONObject(key1);
            	Iterator<String> iterator2 = jsonObject1.keys();
            	while(iterator2.hasNext()){
            		String key2 = (String) iterator2.next();
            		// start
            		if("start".equalsIgnoreCase(key2)){
            			// {"home":"0.50","draw":"0.92","away":"0.39"}
            			JSONObject jsonObject2 = jsonObject1.getJSONObject(key2);
            			Iterator<String> iterator3 = jsonObject2.keys();
            			while(iterator3.hasNext()){
            				String key3 = (String)iterator3.next();
            				String value3 = jsonObject2.getString(key3);
            				Float valueFloat = StringUtils.isBlank(value3) ? -1f : Float.valueOf(value3);
            				if("home".equalsIgnoreCase(key3)){
            					result.put(homeStart, valueFloat);
            				}else if("draw".equalsIgnoreCase(key3)){
            					result.put(drawStart, valueFloat);
            				}else if("away".equalsIgnoreCase(key3)){
            					result.put(awayStart, valueFloat);
            				}
            			}
            		}else if("end".equalsIgnoreCase(key2)){
            			// {"home":"0.67","draw":"0.87","away":"0.25"}
            			JSONObject jsonObject2 = jsonObject1.getJSONObject(key2);
            			Iterator<String> iterator3 = jsonObject2.keys();
            			while(iterator3.hasNext()){
            				String key3 = (String)iterator3.next();
            				String value3 = jsonObject2.getString(key3);
            				Float valueFloat = StringUtils.isBlank(value3) ? -1f : Float.valueOf(value3);
            				if("home".equalsIgnoreCase(key3)){
            					result.put(homeEnd, valueFloat);
            				}else if("draw".equalsIgnoreCase(key3)){
            					result.put(drawEnd, valueFloat);
            				}else if("away".equalsIgnoreCase(key3)){
            					result.put(awayEnd, valueFloat);
            				}
            			}
            		
            		}
            	}
            }
        }
        return result;
    }
    
	public List<IndexStats> queryCurrJobTypeIndex(String okUrlDate) {
		return indexStatsDao.queryCurrJobTypeIndex(okUrlDate);
	}

	public List<IndexStats> queryIndexStatsByRange(IndexStats queryIndexStats) {
		return indexStatsDao.queryIndexStatsByRange(queryIndexStats);
	}
	
	public IndexStatsDao getIndexStatsDao() {
		return indexStatsDao;
	}

	public void setIndexStatsDao(IndexStatsDao indexStatsDao) {
		this.indexStatsDao = indexStatsDao;
	}

}
