/**
 * 
 */
package com.wy.okooo.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.wy.okooo.domain.Match;

/**
 * utils.
 * 
 * @author leslie
 * 
 */
public final class OkParseUtils {

	private static Logger LOGGER = Logger
			.getLogger(OkParseUtils.class.getName());
	
	/**
	 * 亚盘盘口中文和赔率的对应关系.
	 */
	private static Map<String, Float> handicapMap = null;
	
	/**
	 * 博彩公司标识和中文名称对应关系.
	 */
	private static Map<Integer, String> corpNameMap = null;	
	
	/**
	 * 标准欧赔-亚盘换算关系. key 是 [欧洲标准赔率]_[盘口]  value 是 [水位1]_[水位2]_[水位3]   按水位升序排列.
	 */
	private static Map<String, String> standardConversionMap = null;
	
	public static ValueComparator ascMapComparator = new ValueComparator(true);
	
	public static ValueComparator descMapComparator = new ValueComparator(false);
	
	public static NumberComparator numberComparator = new NumberComparator(true);

	private static void initHandicapMap() {
		if (handicapMap == null || handicapMap.isEmpty()) {
			handicapMap = new HashMap<String, Float>();
			handicapMap.put("平手", 0f);
			// 2015-03-30 将让球改为负数， 受让为正数，与okooo保持一致. LOT_ODDS_ASIA 是修改前的; LOT_ODDS_ASIA_TRENDS 是修改后的.
			handicapMap.put("平手/半球", -0.25f);
			handicapMap.put("半球", -0.5f);
			handicapMap.put("半球/一球", -0.75f);
			handicapMap.put("一球", -1.0f);
			handicapMap.put("一球/球半", -1.25f);
			handicapMap.put("球半", -1.5f);
			handicapMap.put("球半/两球", -1.75f);
			handicapMap.put("两球", -2.0f);
			handicapMap.put("两球/两球半", -2.25f);
			handicapMap.put("两球半", -2.5f);
			handicapMap.put("两球半/三球", -2.75f);
			handicapMap.put("三球", -3.0f);
			handicapMap.put("三球/三球半", -3.25f);
			handicapMap.put("三球半", -3.5f);
			handicapMap.put("三球半/四球", -3.75f);
			handicapMap.put("四球", -4.0f);
			handicapMap.put("四球/四球半", -4.25f);
			handicapMap.put("四球半", -4.5f);
			handicapMap.put("四球半/五球", -4.75f);
			handicapMap.put("五球", -5f);
			handicapMap.put("五球/五球半", -5.25f);
			handicapMap.put("五球半", -5.5f);
			handicapMap.put("五球半/六球", -5.75f);

			handicapMap.put("受平手/半球", 0.25f);
			handicapMap.put("受半球", 0.5f);
			handicapMap.put("受半球/一球", 0.75f);
			handicapMap.put("受一球", 1.0f);
			handicapMap.put("受一球/球半", 1.25f);
			handicapMap.put("受球半", 1.5f);
			handicapMap.put("受球半/两球", 1.75f);
			handicapMap.put("受两球", 2.0f);
			handicapMap.put("受两球/两球半", 2.25f);
			handicapMap.put("受两球半", 2.5f);
			handicapMap.put("受两球半/三球", 2.75f);
			handicapMap.put("受三球", 3.0f);
			handicapMap.put("受三球/三球半", 3.25f);
			handicapMap.put("受三球半", 3.5f);
			handicapMap.put("受三球半/四球", 3.75f);
			handicapMap.put("受四球", 4.0f);
			handicapMap.put("受四球/四球半", 4.25f);
			handicapMap.put("受四球半", 4.5f);
			handicapMap.put("受四球半/五球", 4.75f);
			handicapMap.put("受五球", 5f);
			handicapMap.put("受五球/五球半", 5.25f);
			handicapMap.put("受五球半", 5.5f);
			handicapMap.put("受五球半/六球", 5.75f);
		}
	}
	
	private static void initCorpNameMap() {
		if (corpNameMap == null || corpNameMap.isEmpty()) {
			corpNameMap = new HashMap<Integer, String>();
			corpNameMap.put(24, "99家平均");
			
			corpNameMap.put(2, "竞彩官方");
			corpNameMap.put(14, "威廉.希尔");
			corpNameMap.put(82, "立博");
			corpNameMap.put(27, "Bet365");
			corpNameMap.put(43, "Interwetten");
			corpNameMap.put(25, "SNAI");
			corpNameMap.put(94, "bwin");
			corpNameMap.put(65, "伟德国际");
			corpNameMap.put(35, "易胜博");
			corpNameMap.put(37, "Expekt");
			corpNameMap.put(180, "Unibet");
			corpNameMap.put(159, "博天堂");
			corpNameMap.put(19, "必发");
			corpNameMap.put(84, "澳门彩票");
			corpNameMap.put(150, "Norway");
			corpNameMap.put(18, "Oddset");

			corpNameMap.put(49, "Paddy Power");
			corpNameMap.put(157, "Skybet");
			corpNameMap.put(168, "STS");
			corpNameMap.put(285, "Toto");
			corpNameMap.put(250, "皇冠(Singbet)");
			corpNameMap.put(220, "沙巴(IBCBET");
			corpNameMap.put(280, "利记(sbobet)");
			corpNameMap.put(131, "香港马会");
			corpNameMap.put(322, "金宝博(188bet)");
			corpNameMap.put(715, "UEDBET亚洲");
			corpNameMap.put(406, "12bet.com");
			corpNameMap.put(578, "18Bet");
			corpNameMap.put(412, "Sportsbet");
			corpNameMap.put(661, "Sportgewin");
			corpNameMap.put(523, "Skiller.it");
		}
	}
	
	public static void initStandardConversionMap(){
		if (standardConversionMap == null || standardConversionMap.isEmpty()) {
			// key 是 [欧洲标准赔率]_[盘口]  value 是 [水位1]_[水位2]_[水位3]   按水位升序排列.
			standardConversionMap = new HashMap<String, String>();
			standardConversionMap.put("1.13_3.0", "0.75_0.775_0.80_0.825_");
			standardConversionMap.put("1.14_3.0", "0.85_0.875");
			standardConversionMap.put("1.15_3.0", "0.90_0.925_0.95");
			standardConversionMap.put("1.16_3.0", "0.975");
			standardConversionMap.put("1.17_3.0", "1.00_1.025_1.05");
			standardConversionMap.put("1.18_3.0", "1.075_1.10");
			
			standardConversionMap.put("1.14_2.75", "0.75_0.775");
			standardConversionMap.put("1.15_2.75", "0.80_0.825");
			standardConversionMap.put("1.16_2.75", "0.85_0.875_0.90");
			standardConversionMap.put("1.17_2.75", "0.925_0.95");
			standardConversionMap.put("1.18_2.75", "0.975_1.00");
			standardConversionMap.put("1.19_2.75", "1.025_1.05");
			standardConversionMap.put("1.20_2.75", "1.075_1.10");
			
			standardConversionMap.put("1.15_2.5", "0.75_0.775");
			standardConversionMap.put("1.16_2.5", "0.80_0.825");
			standardConversionMap.put("1.17_2.5", "0.85_0.875");
			standardConversionMap.put("1.18_2.5", "0.90");
			standardConversionMap.put("1.19_2.5", "0.925_0.95");
			standardConversionMap.put("1.20_2.5", "0.975_1.00");
			standardConversionMap.put("1.21_2.5", "1.025_1.05");
			standardConversionMap.put("1.22_2.5", "1.075_1.10");
			
			standardConversionMap.put("1.17_2.25", "0.75_0.775");
			standardConversionMap.put("1.18_2.25", "0.80_0.825");
			standardConversionMap.put("1.19_2.25", "0.85_0.875");
			standardConversionMap.put("1.20_2.25", "0.90");
			standardConversionMap.put("1.21_2.25", "0.925_0.95");
			standardConversionMap.put("1.22_2.25", "0.975_1.00");
			standardConversionMap.put("1.23_2.25", "1.025_1.05");
			standardConversionMap.put("1.24_2.25", "1.075_1.10");
			
			standardConversionMap.put("1.19_2.0", "0.75_0.775");
			standardConversionMap.put("1.20_2.0", "0.80");
			standardConversionMap.put("1.21_2.0", "0.825_0.85");
			standardConversionMap.put("1.22_2.0", "0.875");
			standardConversionMap.put("1.23_2.0", "0.90_0.925");
			standardConversionMap.put("1.24_2.0", "0.95_0.975");
			standardConversionMap.put("1.25_2.0", "1.00");
			standardConversionMap.put("1.26_2.0", "1.025_1.05");
			standardConversionMap.put("1.27_2.0", "1.075");
			standardConversionMap.put("1.28_2.0", "1.10");
			
			standardConversionMap.put("1.21_1.75", "0.75");
			standardConversionMap.put("1.22_1.75", "0.775_0.80");
			standardConversionMap.put("1.24_1.75", "0.825_0.85");
			standardConversionMap.put("1.25_1.75", "0.875");
			standardConversionMap.put("1.26_1.75", "0.90_0.925");
			standardConversionMap.put("1.27_1.75", "0.95");
			standardConversionMap.put("1.28_1.75", "0.975");
			standardConversionMap.put("1.29_1.75", "1.00_1.025");
			standardConversionMap.put("1.30_1.75", "1.05");
			standardConversionMap.put("1.31_1.75", "1.075_1.10");
			
			standardConversionMap.put("1.25_1.5", "0.75");
			standardConversionMap.put("1.26_1.5", "0.775");
			standardConversionMap.put("1.27_1.5", "0.80");
			standardConversionMap.put("1.28_1.5", "0.825_0.85");
			standardConversionMap.put("1.29_1.5", "0.875");
			standardConversionMap.put("1.30_1.5", "0.90");
			standardConversionMap.put("1.31_1.5", "0.925");
			standardConversionMap.put("1.32_1.5", "0.95");
			standardConversionMap.put("1.33_1.5", "0.975_1.00");
			standardConversionMap.put("1.34_1.5", "1.025");
			standardConversionMap.put("1.35_1.5", "1.05");
			standardConversionMap.put("1.36_1.5", "1.075");
			standardConversionMap.put("1.37_1.5", "1.10");
			
			standardConversionMap.put("1.30_1.25", "0.75");
			standardConversionMap.put("1.31_1.25", "0.775");
			standardConversionMap.put("1.32_1.25", "0.80");
			standardConversionMap.put("1.33_1.25", "0.825");
			standardConversionMap.put("1.34_1.25", "0.85");
			standardConversionMap.put("1.35_1.25", "0.875");
			standardConversionMap.put("1.36_1.25", "0.90");
			standardConversionMap.put("1.37_1.25", "0.925");
			standardConversionMap.put("1.38_1.25", "0.95");
			standardConversionMap.put("1.39_1.25", "0.975");
			standardConversionMap.put("1.40_1.25", "1.00");
			standardConversionMap.put("1.41_1.25", "1.025");
			standardConversionMap.put("1.42_1.25", "1.05");
			standardConversionMap.put("1.43_1.25", "1.075");
			standardConversionMap.put("1.44_1.25", "1.10");
			
			standardConversionMap.put("1.38_1.0", "0.75");
			standardConversionMap.put("1.39_1.0", "0.775");
			standardConversionMap.put("1.40_1.0", "0.80");
			standardConversionMap.put("1.41_1.0", "0.825");
			standardConversionMap.put("1.43_1.0", "0.85");
			standardConversionMap.put("1.44_1.0", "0.875");
			standardConversionMap.put("1.45_1.0", "0.90");
			standardConversionMap.put("1.46_1.0", "0.925");
			standardConversionMap.put("1.48_1.0", "0.95");
			standardConversionMap.put("1.49_1.0", "0.975");
			standardConversionMap.put("1.50_1.0", "1.00");
			standardConversionMap.put("1.51_1.0", "1.025");
			standardConversionMap.put("1.53_1.0", "1.05");
			standardConversionMap.put("1.54_1.0", "1.075");
			standardConversionMap.put("1.55_1.0", "1.10");
			
			standardConversionMap.put("1.50_0.75", "0.75");
			standardConversionMap.put("1.52_0.75", "0.775");
			standardConversionMap.put("1.53_0.75", "0.80");
			standardConversionMap.put("1.55_0.75", "0.825");
			standardConversionMap.put("1.57_0.75", "0.85");
			standardConversionMap.put("1.58_0.75", "0.875");
			standardConversionMap.put("1.60_0.75", "0.90");
			standardConversionMap.put("1.62_0.75", "0.925");
			standardConversionMap.put("1.63_0.75", "0.95");
			standardConversionMap.put("1.65_0.75", "0.975");
			standardConversionMap.put("1.67_0.75", "1.00");
			standardConversionMap.put("1.68_0.75", "1.025");
			standardConversionMap.put("1.70_0.75", "1.05");
			standardConversionMap.put("1.72_0.75", "1.075");
			standardConversionMap.put("1.73_0.75", "1.10");
			
			standardConversionMap.put("1.75_0.5", "0.75");
			standardConversionMap.put("1.78_0.5", "0.775");
			standardConversionMap.put("1.80_0.5", "0.80");
			standardConversionMap.put("1.83_0.5", "0.825");
			standardConversionMap.put("1.85_0.5", "0.85");
			standardConversionMap.put("1.88_0.5", "0.875");
			standardConversionMap.put("1.90_0.5", "0.90");
			standardConversionMap.put("1.93_0.5", "0.925");
			standardConversionMap.put("1.95_0.5", "0.95");
			standardConversionMap.put("1.98_0.5", "0.975");
			standardConversionMap.put("2.00_0.5", "1.00");
			standardConversionMap.put("2.03_0.5", "1.025");
			standardConversionMap.put("2.05_0.5", "1.05");
			standardConversionMap.put("2.08_0.5", "1.075");
			standardConversionMap.put("2.10_0.5", "1.10");
			
			standardConversionMap.put("2.00_0.25", "0.75");
			standardConversionMap.put("2.03_0.25", "0.775");
			standardConversionMap.put("2.07_0.25", "0.80");
			standardConversionMap.put("2.10_0.25", "0.825");
			standardConversionMap.put("2.13_0.25", "0.85");
			standardConversionMap.put("2.17_0.25", "0.875");
			standardConversionMap.put("2.20_0.25", "0.90");
			standardConversionMap.put("2.23_0.25", "0.925");
			standardConversionMap.put("2.27_0.25", "0.95");
			standardConversionMap.put("2.30_0.25", "0.975");
			standardConversionMap.put("2.33_0.25", "1.00");
			standardConversionMap.put("2.37_0.25", "1.025");
			standardConversionMap.put("2.40_0.25", "1.05");
			standardConversionMap.put("2.43_0.25", "1.075");
			standardConversionMap.put("2.46_0.25", "1.10");
			
			standardConversionMap.put("2.50_0.0", "0.75");
			standardConversionMap.put("2.55_0.0", "0.775");
			standardConversionMap.put("2.60_0.0", "0.80");
			standardConversionMap.put("2.65_0.0", "0.825");
			standardConversionMap.put("2.70_0.0", "0.85");
			standardConversionMap.put("2.75_0.0", "0.875");
			standardConversionMap.put("2.80_0.0", "0.90");
			standardConversionMap.put("2.85_0.0", "0.925");
			standardConversionMap.put("2.90_0.0", "0.95");
			standardConversionMap.put("2.95_0.0", "0.975");
			standardConversionMap.put("3.00_0.0", "1.00");
			standardConversionMap.put("3.05_0.0", "1.025");
			standardConversionMap.put("3.10_0.0", "1.05");
			standardConversionMap.put("3.15_0.0", "1.075");
			standardConversionMap.put("3.20_0.0", "1.10");
		}
	}
	
	public String translateAsiaOdds(Float asiaOdds){
		String result = "";
		if(asiaOdds <= 0.75){
			result = "超低水";
		}else if(asiaOdds > 0.75 && asiaOdds <= 0.85){
			result = "低水";
		}else if(asiaOdds > 0.85 && asiaOdds <= 0.90){
			result = "中低水";
		}else if(asiaOdds > 0.90 && asiaOdds <= 0.95){
			result = "中水";
		}else if(asiaOdds > 0.95 && asiaOdds <= 1.00){
			result = "中高水";
		}else if(asiaOdds > 1.00 && asiaOdds <= 1.08){
			result = "高水";
		}else if(asiaOdds > 1.08){
			result = "超高水";
		}
		return result;
	}
	
	/**
	 * 遍历指定的目录，找出指定的文件名的文件列表;
	 * 
	 * @param dirPath
	 * @param fileName
	 * @return
	 */
	public static List<File> findFileByName(String dirPath, String fileName) {
		List<File> fileList = new ArrayList<File>();
		List<File> childList = new ArrayList<File>();
		File dir = new File(dirPath);
		if (dir.exists()) {
			File[] files = dir.listFiles();
			if (files.length == 0) {
				LOGGER.info(dirPath + "is empaty.");
				return fileList;
			} else {
				for (File file : files) {
					if (file.isDirectory()) {
						childList.addAll(findFileByName(file.getAbsolutePath(),
								fileName));
					} else {
						if (file.getName().equals(fileName)) {
							fileList.add(file);
						}
					}
				}
			}
		} else {
			LOGGER.info(dirPath + " not exists.");
		}
		fileList.addAll(childList);
		return fileList;
	}
	
	/**
	 * 遍历指定的目录，找出以指定的字符串开头的文件列表;
	 * 
	 * @param dirPath
	 * @param fileName
	 * @return
	 */
	public static List<File> findFileByPrefix(String dirPath, String prefix) {
		List<File> fileList = new ArrayList<File>();
		List<File> childList = new ArrayList<File>();
		File dir = new File(dirPath);
		if (dir.exists()) {
			File[] files = dir.listFiles();
			if (files.length == 0) {
				LOGGER.info(dirPath + "is empaty.");
				return fileList;
			} else {
				for (File file : files) {
					if (file.isDirectory()) {
						childList.addAll(findFileByPrefix(file.getAbsolutePath(),
								prefix));
					} else {
						if (file.getName().startsWith(prefix)) {
							fileList.add(file);
						}
					}
				}
			}
		} else {
			LOGGER.info(dirPath + " not exists.");
		}
		fileList.addAll(childList);
		return fileList;
	}
	
	/**
	 * 读取文件内容
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileContent(File file) {
		if(!file.exists()){
			LOGGER.error("file: " + file.getAbsolutePath() + " is empty.");
			return null;
		}
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				LOGGER.error(e);
			}

		}
		return sb.toString();
	}

	/**
	 * 将中文盘口转换为数字
	 * @param handicapChinese
	 * @return
	 */
	public static Float translateHandicap(String handicapChinese) {
		initHandicapMap();
		return handicapMap.get(handicapChinese);
	}
	
	public static String translateCorpName(int corpNo){
		initCorpNameMap();
		return corpNameMap.get(corpNo);
	}
	
	public static Integer translateCorpNo(String corpName){
		initCorpNameMap();
		Set<Entry<Integer, String>> entries = corpNameMap.entrySet();
		Integer result = 0;
		for(Entry<Integer, String> entry : entries){
			if(entry.getValue().equals(corpName)){
				result = entry.getKey();
				break;
			}
		}
		return result;
	}
	
	/**
	 * 获取与 matchHtml 同目录的文件.
	 * @param matchHtml
	 * @return
	 */
	public static List<File> getSameDirFilesFromMatch(File matchHtml, String baseName){
		String matchHtmlPath = matchHtml.getAbsolutePath();
		if (!matchHtml.exists()) {
			LOGGER.info("matchHtml: " + matchHtmlPath
					+ " not exists.");
			return null;
		}
		
		List<File> files = new ArrayList<File>(64);
		int matchSeq = 0;
		String htmlPath = "";
		File htmlFile = null;
		if(OkConstant.EURO_ODDS_CHANGE_FILE_NAME_BASE.equals(baseName) 
				|| OkConstant.LEAGUE_POINTS_FILE_NAME_BASE.equals(baseName)
				|| OkConstant.MATCH_STATS_FILE_NAME.equals(baseName)
				|| OkConstant.EXCHANGE_INFO_FILE_NAME_BASE.equals(baseName)){
			
/*			while (matchSeq++ < 500){
				// 开始时获取的是指定公司的数据.
				for (int corpNo : OkConstant.ODDS_CORP_TR_EURO) {
					htmlPath = matchHtmlPath.replaceFirst(
							OkConstant.MATCH_FILE_NAME, baseName + "_"
									+ corpNo + "_" + matchSeq + ".html");
					htmlFile = new File(htmlPath);
					if(!htmlFile.exists()){
						continue;
					}
					files.add(htmlFile);
				}
			}*/
			File parent = matchHtml.getParentFile();
			FileNameSelector selector = new FileNameSelector(baseName, ".html");
			File[] selectedFiles = parent.listFiles(selector);
			if(selectedFiles != null && selectedFiles.length > 0){
				files = Arrays.asList(selectedFiles);
			}
		}else if(OkConstant.ASIA_ODDS_CHANGE_FILE_NAME_BASE.equals(baseName)){
			while (matchSeq++ < 500){
				for (int corpNo : OkConstant.ODDS_CORP_TR_ASIA) {
					htmlPath = matchHtmlPath.replaceFirst(
							OkConstant.MATCH_FILE_NAME, baseName + "_"
									+ corpNo + "_" + matchSeq + ".html");
					htmlFile = new File(htmlPath);
					if(!htmlFile.exists()){
						continue;
					}
					files.add(htmlFile);
				}
			}
		}else{
			while (matchSeq++ < 800){
					htmlPath = matchHtmlPath.replaceFirst(
							OkConstant.MATCH_FILE_NAME, baseName + "_" + matchSeq + ".html");
					htmlFile = new File(htmlPath);
					if(!htmlFile.exists()){
						continue;
					}
					files.add(htmlFile);
			}
		}
		return files;
	}
	
	public static List<File> getFilesFromDir(File dir, String baseName){
		if(dir == null || !dir.exists()){
			LOGGER.error("dir is not exists.");
			return null;
		}
		if(!dir.isDirectory()){
			LOGGER.error("dir is not a direcotr: " + dir.getAbsolutePath());
			return null;
		}
		
		List<File> files = new ArrayList<File>(64);
		FileNameSelector selector = new FileNameSelector(baseName, ".html");
		File[] selectedFiles = dir.listFiles(selector);
		if(selectedFiles != null && selectedFiles.length > 0){
			files = Arrays.asList(selectedFiles);
		}
		return files;
	}
	
	/**
	 * 获取指定比赛的相关文件, 与getSameDirFilesFromMatch() 不同, getSameDirFilesFromMatch()获取同目录下的所有文件.
	 * @param match
	 * @param baseName
	 * @return
	 */
	public static List<File> getSameMatchFilesFromMatch(File matchHtml,
			Match match, String baseName) {
		String matchHtmlPath = matchHtml.getAbsolutePath();
		File htmlFile = null;
		String htmlPath = "";
		List<File> files = new ArrayList<File>(64);

		if (OkConstant.EURO_ODDS_CHANGE_FILE_NAME_BASE.equals(baseName)) {
//			for (int corpNo : OkConstant.ODDS_CORP_TR_EURO) {
//				htmlPath = matchHtmlPath.replaceFirst(
//						OkConstant.MATCH_FILE_NAME, baseName + "_" + corpNo
//								+ "_" + match.getMatchSeq() + ".html");
//				htmlFile = new File(htmlPath);
//				if (!htmlFile.exists()) {
//					continue;
//				}
//				files.add(htmlFile);
//			}
			File parent = matchHtml.getParentFile();
			FileNameSelector selector = new FileNameSelector(OkConstant.EURO_ODDS_CHANGE_FILE_NAME_BASE, "_" + match.getMatchSeq() + ".html");
			File[] euroChangeFiles = parent.listFiles(selector);
			if(euroChangeFiles != null && euroChangeFiles.length > 0){
				files = Arrays.asList(euroChangeFiles);
			}
		}else if(OkConstant.TURNOVER_DETAIL_FILE_NAME.equals(baseName) || OkConstant.EURO_ODDS_FILE_NAME_BASE.equals(baseName) || OkConstant.EXCHANGE_INFO_FILE_NAME_BASE.equals(baseName)){
			htmlPath = matchHtmlPath.replaceFirst(
					OkConstant.MATCH_FILE_NAME, baseName + "_" + match.getMatchSeq() + ".html");
			htmlFile = new File(htmlPath);
			if(!htmlFile.exists()){
				return files;
			}
			files.add(htmlFile);
		}
		return files;
	}
	
	/**
	 * 从 euroOddsChange_322_49.html 中获取corpNo 322.
	 * @param oddsChangeFile
	 * @return
	 */
	public static int getCorpNoFromOddsChangeFile(File oddsChangeFile){
		String oddsChangeFileName = oddsChangeFile.getName();
		return Integer.valueOf(StringUtils.split(oddsChangeFileName, '_')[1]);
	}
	
	/**
	 * 从 euroOdds_49.html 中获取matchSeq 49.
	 * @param oddsFile
	 * @return
	 */
	public static int getMatchSeqFromOddsFile(File oddsFile){
		String oddsFileName = oddsFile.getName();
		return Integer.valueOf(StringUtils.split(oddsFileName, '_')[1].replace(".html", ""));
	}
	
	/**
	 * 从 euroOddsChange_322_49.html 中获取matchSeq 49.
	 * @param oddsChangeFile
	 * @return
	 */
	public static int getMatchSeqFromOddsChangeFile(File oddsChangeFile){
		String oddsChangeFileName = oddsChangeFile.getName();
		return Integer.valueOf(StringUtils.split(oddsChangeFileName, '_')[2].replace(".html", ""));
	}
	
	/**
	 * 从文件路径中解析出okUrlDate, 例如: /home/leslie/MyProject/OkParse/html/daily/match/2015/05/01/match.html
	 * @param html
	 * @return
	 */
	public static String getOkUrlDateFromFile(File html){
		String day = html.getParentFile().getName();
		String month = html.getParentFile().getParentFile().getName();
		String year = html.getParentFile().getParentFile().getParentFile().getName();
		return year.substring(2,4) + month + day;
	}
	
	/**
	 * 文件存在性校验.
	 * 
	 * @param file
	 * @return
	 */
	public static boolean checkFileExists(File file) {
		return file.exists();
	}
	
	/**
	 * 文件大小校验
	 * @param file
	 * @param size
	 * @return
	 */
	public static boolean checkFileSize(File file, int size){
		return checkFileExists(file) && file.length() > size;
	}
	
	/**
	 * 将指定的url的html 保存到本地磁盘，名称为 fileName.
	 * 
	 * @param fileName
	 * @param url
	 */
	public static void persistByUrl(File file, String url, String encoding, int timeout) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			String html = getMessageFromUrl(url, encoding, timeout);
			if (!StringUtils.isEmpty(html)) {
				bw.append(html);
				bw.flush();
			}
			bw.close();
			bw = null;
		} catch (IOException e) {
			LOGGER.error("url : " + url + "   " + e);
		}
	}
	
	/**
	 * 获取指定url的报文, 异常时返回空串.
	 * 
	 * @param url
	 * @return
	 * @throws IOException 
	 */
	public static String getMessageFromUrl(String url, String encoding, int timeout) throws IOException {
		String html = StringUtils.EMPTY;
		try {
			html = JsoupUtils.getAjaxDocByHttpClient(url, encoding, timeout);
		} catch (IOException e) {
			throw e;
		}
		return html;
	}
	
	/**
	 * 将指定的
	 * 
	 * @param file
	 * @param str
	 */
	public static void persistByStr(File file, String str) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			if (!StringUtils.isEmpty(str)) {
				bw.append(str);
				bw.flush();
			}
			bw.close();
			bw = null;
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}
	
	/**
	 * 将List转为String.
	 * @param list
	 * @param sep
	 * @return
	 */
	public static <T> String transListToStr(List<T> list, String sep){
		StringBuilder sb = new StringBuilder("");
		if(list == null){
			return sb.toString();
		}
		for(T one :list){
			sb.append(one).append(sep);
		}
		
		return sb.toString();
	}
	
	/**
	 * 构造单场比赛的url, 例如: http://www.okooo.com/danchang/100901/
	 * 
	 * @param cal
	 * @return
	 */
	public static String buildUrlByDate(Calendar cal) {
		String year = String.valueOf(cal.get(Calendar.YEAR)).substring(2, 4);
		String month = StringUtils.leftPad(
				String.valueOf(cal.get(Calendar.MONTH) + 1), 2, "0");
		String dayOfMonth = StringUtils.leftPad(
				String.valueOf(cal.get(Calendar.DAY_OF_MONTH)), 2, "0");

		return "http://www.okooo.com/danchang/" + year + month + dayOfMonth;
	}
	
	public static Calendar buildCalByOkUrlDate(String okUrlDate){
		if(StringUtils.isBlank(okUrlDate)){
			return null;
		}
		int year = Integer.valueOf("20" + okUrlDate.substring(0, 2));
		int month = Integer.valueOf(okUrlDate.substring(2, 4));
		int day = Integer.valueOf(okUrlDate.substring(4, 6));
		Calendar cal = Calendar.getInstance();
		// month =  实际月份 - 1.   150102
		cal.set(year, month-1, day, 00, 00);
		return cal;
	}
	
	/**
	 * okooo一期的单场
	 * 
	 * @param url
	 */
	public static void persistMatch(File matchHtml, String url, boolean replace) {
		// 如果已经存在且非空， 则不做处理;
		if (!replace && OkParseUtils.checkFileExists(matchHtml) && OkParseUtils.checkFileSize(matchHtml, 10)) {
			return;
		}
		
		// 删除原来的
		if(replace && OkParseUtils.checkFileExists(matchHtml)){
			matchHtml.delete();
		}
		// 单场页面字符编码用 gb2312, 否则乱码.
		persistByUrl(matchHtml, url, "gb2312", 2000);
	}
	
	/**
	 * 根据 okUrlDate 来获取match.html. 存放在指定的路径下("/home/leslie/MyProject/OkParse/html/daily/match/{okUrlDate}")
	 * 返回存放match.html的目录的绝对路径.
	 * @param okUrlDate
	 * @param replace
	 */
	public static String persistMatch(String okUrlDate, boolean replace) {
		Calendar cal = OkParseUtils.buildCalByOkUrlDate(okUrlDate);
		String matchUrl = OkParseUtils.buildUrlByDate(cal);
		String dir = OkConstant.DAILY_MATCH_FILE_DIR + File.separatorChar + OkParseUtils.getDirPahtFromUrl(matchUrl);
		File parentDir = new File(dir);
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
		File matchHtml = new File(dir + File.separatorChar + OkConstant.MATCH_FILE_NAME);
		if(replace){
			OkParseUtils.persistMatch(matchHtml, matchUrl, true);
		}
		return dir;
	}
	
	/**
	 * 从url中解析获得本地存储的路径. 例如url: "http://www.okooo.com/danchang/100901/", 返回
	 * "2010/09/01";
	 * 
	 * @param url
	 * @return
	 */
	public static String getDirPahtFromUrl(String url) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		String[] urlArr = StringUtils.split(url, "/");
		String timeStr = urlArr[urlArr.length - 1];
		if (timeStr.length() != 6) {
			return null;
		}
		return "20" + timeStr.substring(0, 2) + "/" + timeStr.substring(2, 4)
				+ "/" + timeStr.substring(4, 6);
	}
	
	public static String getDirPahtFromOkUrlDate(String okUrlDate) {
		if (StringUtils.isBlank(okUrlDate)) {
			return null;
		}
		if (okUrlDate.length() != 6) {
			return null;
		}
		return "20" + okUrlDate.substring(0, 2) + "/" + okUrlDate.substring(2, 4)
				+ "/" + okUrlDate.substring(4, 6);
	}
	
	/**
	 * 删除目录.
	 * @param dir
	 */
	public static void deleteDir(File dir){
		if(!dir.exists()){
			return;
		}
		for(File file : dir.listFiles()){
			file.delete();
		}
		return;
	}

	private static class ValueComparator implements Comparator<Map.Entry<String, Integer>> {
		// 默认升序;
		private boolean asc = true;
		ValueComparator(boolean isAsc){
			asc = isAsc;
		}
		public int compare(Map.Entry<String, Integer> mp1,
				Map.Entry<String, Integer> mp2) {
			if(asc){
				return mp1.getValue() - mp2.getValue();
			}
			return mp2.getValue() - mp1.getValue();
		}
	}
	
	private static class NumberComparator implements Comparator<Number>{
		// 默认升序;
		private boolean asc = true;
		NumberComparator(boolean isAsc){
			asc = isAsc;
		}
		public int compare(Number o1, Number o2) {
			if(asc){
				if(o1.getClass().isInstance(Integer.class)){
					return o1.intValue() - o2.intValue();
				}
			}
			return o2.intValue() - o1.intValue();
		}
		
	}
	
	/**
	 * 将 jobTypeIntervals 转换成 Calendar List.
	 * @param jobTypeIntervals
	 * @return
	 */
	public static List<Calendar> buildCalFromIntervals(Set<Integer> jobTypeIntervals){
		List<Calendar> cals = new ArrayList<Calendar>();
		for(Integer jobTypeInterval : jobTypeIntervals){
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, jobTypeInterval);
			cals.add(cal);
		}
		return cals;
	}
	
	/**
	 * 删除掉指定的html文件.
	 * @param dir 待删除文件所在的目录。
	 * @param toProcessMatchSeqs 待删除html的matchSeq。
	 * @param baseName 待删除文件的前缀.
	 */
	public static void deleteExistedFiles(File dir, Set<Integer> toProcessMatchSeqs, String baseName){
		if(toProcessMatchSeqs == null){
			return;
		}
		List<File> files = OkParseUtils.getFilesFromDir(dir, baseName);
		if(files == null){
			return;
		}
		for(Integer matchSeq : toProcessMatchSeqs){
			for(File file : files){
				String filePath = file.getAbsolutePath();
				if(filePath.endsWith("_" + matchSeq + ".html")){
					file.delete();
				}
			}
		}
	}
	
    public static Float calAverage(Float[] floatArr){
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
    
    public static Float calVariance(Float[] floatArr){
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
    
    public static Float calStdVariance(Float[] floatArr){
    	return (float) Math.sqrt(calVariance(floatArr));
    }
    
	public static Float getAverageFromStr(String str){
		//转换成Float[]
		String[] arr = str.split(",");
		Float[] floatArr = new Float[arr.length];
		ArrayList<Float> list = new ArrayList<Float>();
		for(String f : arr){
			list.add(Float.valueOf(f));
		}
		return OkParseUtils.calAverage(list.toArray(floatArr));
	}
	
	public static Float getSdVarianceFromStr(String str){
		//转换成Float[]
		String[] arr = str.split(",");
		Float[] floatArr = new Float[arr.length];
		ArrayList<Float> list = new ArrayList<Float>();
		for(String f : arr){
			list.add(Float.valueOf(f));
		}
		return OkParseUtils.calStdVariance(list.toArray(floatArr));
	}
	
	public static Double transTimeBeforeMatch(String timeBeforeMatchStr){
		if(StringUtils.isBlank(timeBeforeMatchStr)){
			return null;
		}
		String decimalPartStr = timeBeforeMatchStr.split("\\.")[1];
		String integerPartStr = timeBeforeMatchStr.split("\\.")[0];
		String newDecimalPartStr = String.valueOf(Double.valueOf(decimalPartStr)/60).split("\\.")[1];
		String newTimeBeforeMatchStr = integerPartStr + "." + newDecimalPartStr;
		return Double.valueOf(newTimeBeforeMatchStr);
	}
}
