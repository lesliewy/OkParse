/**
 * 
 */
package com.wy.okooo.util;

/**
 * 常量类
 * 
 * @author leslie
 *
 */
public interface OkConstant {

	/**
	 * 站点url
	 */
	String mainUrl = "http://www.okooo.com";
	
	/**
	 * 欧赔，亚盘页面，博彩公司的tr是固定，例如 #tr24 是99家平均, #tr14 是威廉
	 * 2-竞彩官方 24-99家平均;  14-威廉.希尔;  82-立博;  43-Interwetten;  159-博天堂;  84-澳门彩票; 322-金宝博(188bet)
	 * 406-12bet.com; 
	 */
	int[] ODDS_CORP_TR_EURO = new int[]{2, 24, 14, 82, 43, 159, 84, 322, 406};
	
	/**
	 * 亚盘
	 */
	int[] ODDS_CORP_TR_ASIA = new int[]{13, 14, 19, 24, 27, 35, 36, 43, 65, 78, 82, 84, 106, 108, 131, 159, 220, 270, 280, 322, 331, 340, 344,
			355, 405, 406, 496, 543, 560, 561, 585, 586, 593, 614, 634, 668, 672, 677, 696, 723, 724, 733, 734};
	
	/**
	 * 欧亚转换中使用的公司.
	 */
	int[] ODDS_CORP_EURO_TRANS_ASIA = {13, 14, 19, 27, 36, 65, 78, 82, 84, 106, 108, 131, 220, 270, 280, 331, 340, 344, 355, 373, 405,
			496, 543, 560, 561, 585, 586, 593, 614, 634, 677, 668, 672, 696, 706, 723, 724, 733, 734};
	
	/**
	 * 欧亚转换中使用的公司的名称
	 */
    String[] CORP_EURO_TRANS_ASIA_NAME = {"必发", "威廉.希尔", "Bet365", "立博", "澳门彩票", "伟德国际","BoDog", "沙巴(IBCBET)", "利记(sbobet)",
    		"Bodog.eu", "Luckia.es", "PlanetWin365", "Betinternet", "Boylesports", "betcity", "stoiximan", "Bet3000", "BetVictor",
    		"Bet-at-home.it", "Bet-at-home.uk", "bet-at-home", "Eurobet", "Eurobet.it", "Europe-bet.com", "Europe-bet", "Intralot Italia", "Intralot",
    		"Netbet.it", "NetBet", "Sportsbook.ag", "Sportsbook.com", "The Greek", "TheGreek.com", "Diamond Sportsbook Int.","DiamondSportsBook",
    		"Hong Kong JC", "香港马会"};
    
    String[] CORP_EURO_TRANS_ASIA_NAME_HISTORY = {"威廉.希尔", "立博", "伟德国际",  "Bet365", "澳门彩票", "沙巴(IBCBET)", "利记(sbobet)"};
    
	/**
	 * 初始赔率信息
	 */
	int[] ODDS_EURO_ASIA_INIT = new int[]{14, 19, 27, 43, 65, 82, 84, 131, 202, 220, 270, 280, 322, 405};
	
	/**
	 * document 的类型标识. E - 欧赔页面(http://www.okooo.com/soccer/match/680757/odds/).
	 */
	String DOC_TYPE_EURO_ODDS = "E";
	
	/**
	 * document 的类型标识. A - 亚盘页面(http://www.okooo.com/soccer/match/680757/ah/).
	 */
	String DOC_TYPE_ASIA_ODDS = "A";
	
	/**
	 * document 的类型标识. EC - 欧赔变化页面(http://www.okooo.com/soccer/match/680757/odds/change/24/).
	 */
	String DOC_TYPE_EURO_ODDS_CHANGE = "EC";
	
	/**
	 * document 的类型标识. AC - 亚盘变化页面(http://www.okooo.com/soccer/match/680757/ah/change/24/).
	 */
	String DOC_TYPE_ASIA_ODDS_CHANGE = "AC";
	
	/**
	 * 获取的文件存储的本地路径.
	 */
	String FILE_PATH_BASE = "/home/leslie/MyProject/OkParse/html/";
	
	/**
	 * 单场比赛的本地html名称; http://www.okooo.com/danchang/100901/
	 */
	String MATCH_FILE_NAME = "match.html";
	
	/**
	 * 欧赔的本地html名称: http://www.okooo.com/soccer/match/153028/odds/
	 */
	String EURO_ODDS_FILE_NAME_BASE = "euroOdds";
	
	String EURO_ODDS_CHANGE_FILE_NAME_BASE = "euroOddsChange";

	/**
	 * 亚盘的本地html名称: http://www.okooo.com/soccer/match/148916/ah/
	 */
	String ASIA_ODDS_FILE_NAME_BASE = "asiaOdds";
	
	String ASIA_ODDS_CHANGE_FILE_NAME_BASE = "asiaOddsChange";

	/**
	 * 交易盈亏页面本地html名称: http://www.okooo.com/soccer/match/151692/exchanges/
	 */
	String EXCHANGE_INFO_FILE_NAME_BASE = "exchangeInfo";
	
	/**
	 * 赛事一览页面本地html名称: http://www.okooo.com/soccer/league/34/
	 */
	String LEAGUE_POINTS_FILE_NAME_BASE = "leaguePoints";

	/**
	 * 成交明细页面本地html名称: http://www.okooo.com/soccer/match/151692/exchanges/detail/
	 */
	String TURNOVER_DETAIL_FILE_NAME = "turnoverDetail";
	
	/**
	 * 球员阵容页面本地html名称: http://www.okooo.com/soccer/match/768266/
	 */
	String MATCH_STATS_FILE_NAME = "matchStats";
	
	/**
	 * okooo指数页面本地html名称: http://www.okooo.com/soccer/match/776375/okoooexponent/#lstu
	 */
	String INDEX_STATS_FILE_NAME = "indexStats";
	
	/**
	 * 让球页面本地html名称: http://www.okooo.com/soccer/match/776908/hodds/
	 */
	String EURO_HANDICAP_FILE_NAME_BASE = "euroHandicap";
	
	/**
	 * 欧赔转换为亚盘页面本地html名称: http://www.okooo.com/soccer/match/713907/ah/?action=euro2asia&MatchID=713907&MakerIDList=0|82,1|65,2|19,3|84,4|220,5|280,6|106,7|543,8|593,9|696
	 */
	String EURO_TRANS_ASIA_FILE_NAME_BASE = "euroTransAsia";
	
	/**
	 * 主胜
	 */
	Integer HOST_WIN = 3;
	
	/**
	 * 平局
	 */
	Integer HOST_EVEN = 1;
	
	/**
	 * 主负
	 */
	Integer HOST_NEGA = 0;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 99家平均, 主胜, 挪甲的加权得分;
	 */
	Float LATEST_EURO_ODDS_NORWAYA_WIN_99 = 72.34F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 99家平均, 主胜平, 挪甲的加权得分;
	 */
	Float LATEST_EURO_ODDS_NORWAYA_WINEVEN_99 = 85.10f;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 99家平均, 主胜, 欧冠的加权得分;
	 */
	Float LATEST_EURO_ODDS_EUROCP_WIN_99 = 75.00F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 99家平均, 主胜平, 欧冠的加权得分;
	 */
	Float LATEST_EURO_ODDS_EUROCP_WINEVEN_99 = 83.33f;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 澳门彩票, 主胜, 欧洲杯的加权得分;
	 */
	Float LATEST_EURO_ODDS_EUROCUP_WIN_MACAU = 73.80F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 澳门彩票, 主胜平, 欧洲杯的加权得分;
	 */
	Float LATEST_EURO_ODDS_EUROCUP_WINEVEN_MACAU = 95.23f;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 威廉.希尔, 主胜, 欧洲杯的加权得分;
	 */
	Float LATEST_EURO_ODDS_EUROCUP_WIN_WILLIAM = 78.18F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 威廉.希尔, 主胜平, 欧洲杯的加权得分;
	 */
	Float LATEST_EURO_ODDS_EUROCUP_WINEVEN_WILLIAM = 96.36F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 威廉.希尔, 主胜, 世界杯的加权得分;
	 */
	Float LATEST_EURO_ODDS_WORLDCUP_WIN_WILLIAM = 83.33F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 威廉.希尔, 主胜平, 世界杯的加权得分;
	 */
	Float LATEST_EURO_ODDS_WORLDCUP_WINEVEN_WILLIAM = 1.00F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 立博, 主胜, 葡超的加权得分;
	 */
	Float LATEST_EURO_ODDS_PORA_WIN_LADBROKES = 71.55F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 立博, 主胜平, 葡超的加权得分;
	 */
	Float LATEST_EURO_ODDS_PORA_WINEVEN_LADBROKES = 88.07F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 立博, 主胜, 欧冠的加权得分;
	 */
	Float LATEST_EURO_ODDS_EUROCP_WIN_LADBROKES = 74.69F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 立博, 主胜平, 欧冠的加权得分;
	 */
	Float LATEST_EURO_ODDS_EUROCP_WINEVEN_LADBROKES = 93.97F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 博天堂, 主胜, 葡超的加权得分;
	 */
	Float LATEST_EURO_ODDS_PORA_WIN_SPORTINGBET = 70.06F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 博天堂, 主胜平, 葡超的加权得分;
	 */
	Float LATEST_EURO_ODDS_PORA_WINEVEN_SPORTINGBET = 90.44F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 博天堂, 主胜, 欧洲杯的加权得分;
	 */
	Float LATEST_EURO_ODDS_EUROCUP_WIN_SPORTINGBET = 79.31F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 博天堂, 主胜平, 欧洲杯的加权得分;
	 */
	Float LATEST_EURO_ODDS_EUROCUP_WINEVEN_SPORTINGBET = 93.10F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, Interwetten, 主胜, 英联杯的加权得分;
	 */
	Float LATEST_EURO_ODDS_ENGLCUP_WIN_INTERWETTEN = 73.07F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, Interwetten, 主胜平, 英联杯的加权得分;
	 */
	Float LATEST_EURO_ODDS_ENGLCUP_WINEVEN_INTERWETTEN = 88.46F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, Interwetten, 主胜, 葡超的加权得分;
	 */
	Float LATEST_EURO_ODDS_PORA_WIN_INTERWETTEN = 73.68F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, Interwetten, 主胜平, 葡超的加权得分;
	 */
	Float LATEST_EURO_ODDS_PORA_WINEVEN_INTERWETTEN = 84.21F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 金宝博(188bet), 主胜, 欧洲杯的加权得分;
	 */
	Float LATEST_EURO_ODDS_EUROCUP_WIN_188BET = 73.58F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 金宝博(188bet), 主胜平, 欧洲杯的加权得分;
	 */
	Float LATEST_EURO_ODDS_EUROCUP_WINEVEN_188BET = 88.67F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 12bet.com, 主胜, 欧洲杯的加权得分;
	 */
	Float LATEST_EURO_ODDS_EUROCUP_WIN_12BET = 74.19F;

	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 12bet.com, 主胜平, 欧洲杯的加权得分;
	 */
	Float LATEST_EURO_ODDS_EUROCUP_WINEVEN_12BET = 90.32F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 12bet.com, 主胜, 欧冠的加权得分;
	 */
	Float LATEST_EURO_ODDS_EUROCP_WIN_12BET = 74.35F;
	
	/**
	 * 主胜赔率<2.0, 最近欧赔降低, 12bet.com, 主胜平, 欧冠的加权得分;
	 */
	Float LATEST_EURO_ODDS_EUROCP_WINEVEN_12BET = 89.74F;
	
	String JOB_HTML_FILE_BASE_DIR = "/home/leslie/MyProject/OkParse/html/daily/match/";
//	JOB_HTML_FILE_BASE_DIR = "/home/leslie/MyProject/OkParse/html/";
	
	/**
	 * 每日执行的JOB的文件存放路径，每次执行前都会先删除.
	 */
	String DAILY_MATCH_FILE_DIR = "/home/leslie/MyProject/OkParse/html/daily/match";
	
	/**
	 * 每日执行的JOB的执行结果文件存放路径，内容同邮件的内容.
	 */
	String DAILY_LOG_FILE_DIR = "/home/leslie/MyProject/OkParse/html/daily/logs";
	
	/**
	 * LOT_CONFIG 中的参数名称.
	 */
	String CONFIG_CURR_OK_URL_DATE = "CONFIG_CURR_OK_URL_DATE";
	String CONFIG_JOB_A3_UPPER = "CONFIG_JOB_A3_UPPER";
	String CONFIG_JOB_A3_LOWER = "CONFIG_JOB_A3_LOWER";
	String CONFIG_JOB_A2_UPPER = "CONFIG_JOB_A2_UPPER";
	String CONFIG_JOB_A2_LOWER = "CONFIG_JOB_A2_LOWER";
	String CONFIG_JOB_A1_UPPER = "CONFIG_JOB_A1_UPPER";
	String CONFIG_JOB_A1_LOWER = "CONFIG_JOB_A1_LOWER";
	String CONFIG_JOB_A0_UPPER = "CONFIG_JOB_A0_UPPER";
	String CONFIG_JOB_A0_LOWER = "CONFIG_JOB_A0_LOWER";
	
	String CONFIG_LATEST_2_BEFORE = "CONFIG_LATEST_2_BEFORE";
	String CONFIG_LATEST_1_BEFORE = "CONFIG_LATEST_1_BEFORE";
	String CONFIG_NUM_OF_THREAD_MAIL_JOB = "CONFIG_NUM_OF_THREAD_MAIL_JOB";
	String CONFIG_JOB_A3_MATCH_NUM = "CONFIG_JOB_A3_MATCH_NUM";
	String CONFIG_JOB_A2_MATCH_NUM = "CONFIG_JOB_A2_MATCH_NUM";
	String CONFIG_JOB_A1_MATCH_NUM = "CONFIG_JOB_A1_MATCH_NUM";
	String CONFIG_JOB_A0_MATCH_NUM = "CONFIG_JOB_A0_MATCH_NUM";
	String CONFIG_DEL_R_UPPER_LIMIT = "CONFIG_DEL_R_UPPER_LIMIT";
	String ASIA_KELLY_JOB_TYPE_INTERVAL = "ASIA_KELLY_JOB_TYPE_INTERVAL";
	String PARSE_MAIL_JOB_TYPE_INTERVAL = "PARSE_MAIL_JOB_TYPE_INTERVAL";
	String INDEX_STATS_JOB_TYPE_INTERVAL = "INDEX_STATS_JOB_TYPE_INTERVAL";
	String CONFIG_JOB_A_MATCH_NUM = "CONFIG_JOB_A_MATCH_NUM";
	String DEL_JOB_UPPER_LIMIT_SEC = "DEL_JOB_UPPER_LIMIT_SEC";
	String EURO_HANDICAP_JOB_TYPE_INTERVAL = "EURO_HANDICAP_JOB_TYPE_INTERVAL";
	String PROB_AVERAGE_JOB_TYPE_INTERVAL = "PROB_AVERAGE_JOB_TYPE_INTERVAL";
	String JOB_F_INTERVAL = "JOB_F_INTERVAL";
	
	// 各个阶段的比赛个数, 需要与CONFIG_JOB_A_INTERVAL对应.
	String CONFIG_JOB_A_MATCH_NUM_DEFAULT = "150,110,80,50,10";
	
	/**
	 *  时间间隔(min) 默认值.
	 */
	String JOB_A_INTERVAL_DEFAULT = "2400,1200,300,180,60";
	
	/**
	 * 默认超时时间间隔(SECONDS)，超过后删除该job.
	 */
	int DEL_JOB_UPPER_LIMIT_SEC_DEFALUT = 2400;
	
	int DEL_JOB_UPPER_LIMIT_SEC_DEFALUT2 = 7200;
	
	/**
	 * 配置的LOT_EURO_CHANGE_DAILY_STATS 中使用的prob, 格式为: H|HE|V|VE
	 */
	String PROB_EURO_CHANGE_DAILY_STATS = "PROB_EURO_CHANGE_DAILY_STATS";
	/**
	 * job 的状态
	 */
	String JOB_STATE_RUNNING = "R";
	String JOB_STATE_SUCCESS = "S";
	String JOB_STATE_FAILED = "F";
	String JOB_STATE_DELETE = "D";
}
