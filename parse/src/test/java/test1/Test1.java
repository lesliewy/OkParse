/**
 * 
 */
package test1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

/**
 * @author leslie
 *
 */
public class Test1 {

	@Test
	public void test1(){
		Map<Integer, String> map = new TreeMap<Integer, String>();
		map.put(2, "aaa");
		map.put(22, "jj");
		map.put(11, "ccc");
		map.put(102, "ddd");
		System.out.println(map);
	}
	
	@Test
	public void test2(){
		StringBuilder sb = new StringBuilder("");
		sb.append("abc");
		Formatter formatter = new Formatter(sb);
		formatter.format("%3s %3d", "nn", 2);
		formatter.close();
		System.out.println(sb);
	}
	
	@Test
	public void test3() throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost method = null;
		StringBuilder sb = new StringBuilder("");
		String encoding = "gb2312";
		String url = "http://www.okooo.com/soccer/match/736914/okoooexponent/xmlData/";
		try {
			method = new HttpPost(url);

//			method.addHeader("connection", "keep-alive");

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("type", "okooo"));
//			nvps.add(new BasicNameValuePair("type", "okoooexponent"));
			method.setEntity(new UrlEncodedFormEntity(nvps, "gb2312"));
			
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectionRequestTimeout(4000)
					.setConnectTimeout(4000).setSocketTimeout(4 * 1000)
//					.setProxy(new HttpHost("127.0.0.1", 8087))
					.build();
			method.setConfig(requestConfig);
			
			CloseableHttpResponse response = (CloseableHttpResponse) httpclient
					.execute(method);
			HttpEntity entity = response.getEntity();

			// If the response does not enclose an entity, there is no need
			// to bother about connection release
			if (entity != null) {
				InputStream instream = entity.getContent();
				instream.read();
				// 使用GB2312, 因为返回的 html 的 CONTENT 中设置了.
				BufferedReader in = new BufferedReader(new InputStreamReader(
						instream, encoding));
				String line = "";
				while ((line = in.readLine()) != null) {
					sb.append(line);
				}
				instream.close();
			}
		} catch (ClientProtocolException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != method) {
				method.releaseConnection();
			}
			httpclient.close();
		}
		System.out.println(sb.toString());
	}
	
	@Test
	public void test4(){
		BufferedReader okoooReader = null;
		BufferedReader okoooExpReader = null;
		try {
			okoooReader = new BufferedReader(new FileReader("/home/leslie/MyProject/OkParse/parse/target/test-classes/okooo.txt")); 
			String okooo = okoooReader.readLine();
			Map<String, Float> okoooMap = toMap(okooo, "okooo");
			System.out.println(okoooMap);
			
			System.out.println("===========");
			
			okoooExpReader = new BufferedReader(new FileReader("/home/leslie/MyProject/OkParse/parse/target/test-classes/okoooexponent.txt"));
			String okoooExp = okoooExpReader.readLine();
			Map<String, Float> okoooExpMap = toMap(okoooExp, "okoooexponent");
			System.out.println(okoooExpMap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    @SuppressWarnings("unchecked")
	private Map<String, Float> toMap(String jsonString, String type) throws JSONException {
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
    
    @Test
    public void test5(){
    	Float[] floatArr = {1f, 3f, 3f, 29f, 78f};
    	System.out.println("avg: " + calAverage(floatArr));
    	System.out.println("var: " + calVariance(floatArr));
    	System.out.println("stdvar: " + calStdVariance(floatArr));
    }
    
    @Test
    public void test6(){
    	Float avgHost = 1.3763343f;
    	System.out.println((Math.round(avgHost * 100))/100.0);
    }
    
    private Float calAverage(Float[] floatArr){
    	if(floatArr == null || floatArr.length == 0){
    		return null;
    	}
    	Float sum = 0f;
    	Float avg = 0f;
    	int length = floatArr.length;
    	for(Float f : floatArr){
    		sum += f;
    	}
    	avg = sum/length;
    	
    	return avg;
    }
    
    private Float calVariance(Float[] floatArr){
    	if(floatArr == null || floatArr.length == 0){
    		return null;
    	}
    	Float avg = 0f;
    	int length = floatArr.length;
    	avg = calAverage(floatArr);
    	
    	// 计算修正样本方差.
    	double sumvar = 0f;
    	for(Float f : floatArr){
    		sumvar += Math.pow((f - avg), 2);
    	}
    	
    	Float var = (float)sumvar/(length - 1);
    	return var;
    }
    
    private Float calStdVariance(Float[] floatArr){
    	return (float) Math.sqrt(calVariance(floatArr));
    }
    
    @Test
    public void test7(){
    	Float hostProp = 0.3263f;
    	Float evenProp = 0.1322f;
        Float visitingProp = 0.5415f;
        
        Float lossRatio = 0.88f;
    	Float[] hostOddsArr = {3.3f, 3.50f, 3.60f, 3.7f, 3.15f};
    	Float[] evenOddsArr = {3.30f, 3.25f, 3.3f, 3.3f, 3.2f};
    	Float[] visitingOddsArr = {1.95f, 1.85f, 1.83f, 1.83f, 2f};
    	int length = hostOddsArr.length;
    	for(int i = 0; i < length; i++){
    		Float hostOdds = hostOddsArr[i];
    		Float evenOdds = evenOddsArr[i];
    		Float visitingOdds = visitingOddsArr[i];
            Map<String, Float> resultOld = this.myOldCal(lossRatio, hostOdds, hostProp, evenOdds, evenProp, visitingOdds, visitingProp);
            System.out.println("Old " + "H:" + resultOld.get("H")  + " E:" + resultOld.get("E") + " V:" + resultOld.get("V"));
    	}
    	
    	for(int i = 0; i < length; i++){
    		Float hostOdds = hostOddsArr[i];
    		Float evenOdds = evenOddsArr[i];
    		Float visitingOdds = visitingOddsArr[i];
            Map<String, Float> resultNew = this.myNewCal(lossRatio, hostOdds, hostProp, evenOdds, evenProp, visitingOdds, visitingProp);
            System.out.println("New " + "H:" + resultNew.get("H")  + " E:" + resultNew.get("E") + " V:" + resultNew.get("V"));
    	}
    }
    
    private Map<String, Float> myOldCal(Float lossRatio, Float hostOdds, Float hostProp, Float evenOdds, Float evenProp, 
    		Float visitingOdds, Float visitingProp){
		Float hostIndex = lossRatio * (evenProp + visitingProp) - hostProp * (hostOdds -1);
		Float evenIndex = lossRatio * (hostProp + visitingProp) - evenProp * (evenOdds -1);
		Float visitingIndex = lossRatio * (hostProp + evenProp) - visitingProp * (visitingOdds -1);
		Map<String, Float> result = new HashMap<String, Float>();
		result.put("H", hostIndex);
		result.put("E", evenIndex);
		result.put("V", visitingIndex);
		return result;
    }
    
    private Map<String, Float> myNewCal(Float lossRatio, Float hostOdds, Float hostProp, Float evenOdds, Float evenProp, 
    		Float visitingOdds, Float visitingProp){
		Float hostIndex = lossRatio * (hostProp + evenProp + visitingProp) - hostProp * hostOdds;
		Float evenIndex = lossRatio * (hostProp + evenProp + visitingProp) - evenProp * evenOdds;
		Float visitingIndex = lossRatio * (hostProp + evenProp + visitingProp) - visitingProp * visitingOdds;
		Map<String, Float> result = new HashMap<String, Float>();
		result.put("H", hostIndex);
		result.put("E", evenIndex);
		result.put("V", visitingIndex);
		return result;
    }
}
