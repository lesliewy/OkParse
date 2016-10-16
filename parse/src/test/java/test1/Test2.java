/**
 * 
 */
package test1;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

/**
 * @author leslie
 *
 */
public class Test2 {

	@Test
	public void test1(){
		Map<String, List<Float>> map = new HashMap<String, List<Float>>();
		map.put("1", new ArrayList<Float>());
		int i = 0;
		while (i++ < 5){
			map.get("1").add(Float.valueOf(i));
		}
		System.out.println("map: " + map);
	}
	
	@Test
	public void test2(){
		String section1PreKey = "130_A0_SEC1";
		String section2PreKey = "130_A0_SEC2";
		String section1Str = "[0.9,0.94]:192,5.71|192,13.01|192,27.24|";
		String section2Str = "[0.95,0.99]:19,4.02|19,19.47|19,18.86|";
		
		// 区间
		System.out.println(section1PreKey + "_" + "S" + ": " + section1Str.split(":")[0]);
		// 个数.
		System.out.println(section1PreKey + "_" + "N" + ": " + section1Str.split(":")[1].split("\\|")[0].split(",")[0]);
		// HEV的离散度.
		System.out.println(section1PreKey + "_" + "H" + ": " + section1Str.split(":")[1].split("\\|")[0].split(",")[1]);
		System.out.println(section1PreKey + "_" + "E" + ": " + section1Str.split(":")[1].split("\\|")[1].split(",")[1]);
		System.out.println(section1PreKey + "_" + "V" + ": " + section1Str.split(":")[1].split("\\|")[2].split(",")[1]);
		
		System.out.println(section2PreKey + "_" + "S" + ": " + section2Str.split(":")[0]);
		System.out.println(section2PreKey + "_" + "N" + ": " + section2Str.split(":")[1].split("\\|")[0].split(",")[0]);
		System.out.println(section2PreKey + "_" + "H" + ": " + section2Str.split(":")[1].split("\\|")[0].split(",")[1]);
		System.out.println(section2PreKey + "_" + "E" + ": " + section2Str.split(":")[1].split("\\|")[1].split(",")[1]);
		System.out.println(section2PreKey + "_" + "V" + ": " + section2Str.split(":")[1].split("\\|")[2].split(",")[1]);
	}
	
	@Test
	public void test3(){
		StringBuilder sb = new StringBuilder("");
		sb.append("abc");
		sb.insert(0, "123" + "\n");
		System.out.println(sb.toString());
	}
	
	@Test
	public void test4(){
//		String url = "http://www.okooo.com/soccer/match/717391/ah/?action=euro2asia&MatchID=717391&MakerIDList=0|82,1|65,2|19,3|84,4|220,5|280,6|106,7|543,8|593,9|696";
//		URI uri = URI.create(url);
		String result = "";
//		try {
//			result = UriUtils.encodeHttpUrl(url, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.out.println("result: " + result);
	}
	
	@Test
	public void test5(){
		Set<Integer> jobTypeIntervals = new TreeSet<Integer>(Collections.reverseOrder());
		jobTypeIntervals.add(30);
		jobTypeIntervals.add(1800);
		jobTypeIntervals.add(1200);
		jobTypeIntervals.add(2400);
		System.out.println("jobTypeIntervals: " + jobTypeIntervals);
		
		List<Calendar> cals = buildCalFromIntervals(jobTypeIntervals);
		for(int i = cals.size() -1; i >= 0; i--){
			System.out.println(cals.get(i).toString());
		}
	}
	
	private static List<Calendar> buildCalFromIntervals(Set<Integer> jobTypeIntervals){
		List<Calendar> cals = new ArrayList<Calendar>();
		for(Integer jobTypeInterval : jobTypeIntervals){
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, jobTypeInterval);
			cals.add(cal);
		}
		return cals;
	}
	
	@Test
	public void test6(){
		Float a = 0.98f;
		Float b = 0.95f;
		Float d = 0.90f;
		System.out.println(b-a + " " + (Math.abs(b-a) > 0.04));
		System.out.println(d-b + " " + (Math.abs(d-b) > 0.04));
		
		List<Float> list = new ArrayList<Float>();
		list.add(1.0f);
		list.add(2.2f);
		list.add(null);
		list.add(null);
		System.out.println("list: " + list + "; size: " + list.size());
		
		System.out.println(0 - 1.0f);
	}
	
	@Test
	public void test7(){
		Double hostDouble = 0.232887438;
		Float hostFloat = 1.82f;
		Double a = hostFloat * hostDouble;
		System.out.println("a: " + a);
		Float f = (Math.round(a * 100))/100.0f;
		System.out.println(f);
	}
	
	@Test
	public void test8(){
		Integer a = 3;
		Integer b = 4;
		System.out.println(a.floatValue()/b.floatValue());
		System.out.println(Math.round(-2.58f));
	}
}
