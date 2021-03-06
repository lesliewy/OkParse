select a.OK_MATCH_ID, a.INIT_HOST_ODDS,a.HOST_ODDS, b.HANDICAP,b.INIT_HOST_ODDS, b.HOST_ODDS, c.HOST_GOALS,c.VISITING_GOALS from LOT_ODDS_EURO a, LOT_ODDS_ASIA b, LOT_MATCH c where a.OK_MATCH_ID=b.OK_MATCH_ID and a.OK_MATCH_ID=c.OK_MATCH_ID and a.ODDS_CORP_NAME='立博' and b.ODDS_CORP_NAME='立博' and a.INIT_HOST_ODDS > 1.45 AND a.INIT_HOST_ODDS <=1.60 AND b.HANDICAP >1.0;

--=======
select OK_MATCH_ID from LOT_ODDS_EURO_CHANGE where ODDS_CORP_NAME='澳门彩票' and ODDS_TIME > '2014-08-01 01:00:00' group by        OK_MATCH_ID having count(*) < 10 and count(*) > 3;

select a.OK_URL_DATE, a.MATCH_SEQ, a.MATCH_NAME, a.HOST_GOALS, a.VISITING_GOALS, c.INIT_HOST_ODDS, c.HOST_ODDS from LOT_MATCH a, (select OK_MATCH_ID from LOT_ODDS_EURO_CHANGE where ODDS_CORP_NAME='威廉.希尔' and ODDS_TIME > '2014-08-01 01:00:00' group by OK_MATCH_ID having count(*) =2) b, LOT_ODDS_EURO c where a.OK_MATCH_ID=b.OK_MATCH_ID and a.OK_MATCH_ID=c.OK_MATCH_ID and c.ODDS_CORP_NAME='威廉.希尔'

 select a.OK_URL_DATE, a.MATCH_SEQ, a.MATCH_NAME, a.HOST_GOALS, a.VISITING_GOALS, c.INIT_HOST_ODDS, c.HOST_ODDS from LOT_MATCH a,     (select OK_MATCH_ID from LOT_ODDS_EURO_CHANGE where ODDS_CORP_NAME='威廉.希尔' and ODDS_TIME > '2014-08-01 01:00:00' group by       OK_MATCH_ID having count(*) =2) b, LOT_ODDS_EURO c where a.OK_MATCH_ID=b.OK_MATCH_ID and a.OK_MATCH_ID=c.OK_MATCH_ID and c.          ODDS_CORP_NAME='威廉.希尔' and c.HOST_ODDS < c.VISITING_ODDS and c.HOST_ODDS < c.EVEN_ODDS and c.HOST_ODDS < 2 and a.MATCH_NAME='德甲'

--===

select c.OK_URL_DATE, c.MATCH_SEQ, a.OK_MATCH_ID, a.HOST_ODDS, b.HOST_BF from LOT_ODDS_EURO a, LOT_TRANS_PROP b, LOT_MATCH c where a.OK_MATCH_ID=b.ID and a.OK_MATCH_ID=c.OK_MATCH_ID and b.HOST_BF is not null and a.ODDS_CORP_NAME='澳门彩票' and c.MATCH_NAME in('英甲','英超') and c.HOST_GOALS > c.VISITING_GOALS order by a.HOST_ODDS

-- 查看博彩公司最后一次变化的平均时间(TIME_BEFORE_MATCH);
select SUM(TIME_BEFORE_MATCH) sum, COUNT(*) count , AVG(TIME_BEFORE_MATCH) avg from LOT_ODDS_EURO_CHANGE a, LOT_MATCH b where a.OK_MATCH_ID=b.OK_MATCH_ID AND a.ODDS_SEQ=2 AND a.TIME_BEFORE_MATCH is not null AND ODDS_CORP_NAME= 'Norsk tipting' AND b.MATCH_NAME='西乙';

--
select b.HOST_GOALS, b.VISITING_GOALS from (select t2.OK_MATCH_ID, t2.ODDS_CORP_NAME, t2.HOST_KELLY HOST_KELLY2, t1.HOST_KELLY HOST_KELLY1 from LOT_ODDS_EURO_CHANGE t1, LOT_ODDS_EURO_CHANGE t2 where t1.OK_MATCH_ID=t2.OK_MATCH_ID and t1.ODDS_CORP_NAME=t2.ODDS_CORP_NAME and t1.ODDS_CORP_NAME='Optibet' and t1.ODDS_SEQ=1 and t2.ODDS_SEQ=2) a, LOT_MATCH b, LOT_ODDS_EURO c where a.       OK_MATCH_ID=b.OK_MATCH_ID and a.                 OK_MATCH_ID=c.OK_MATCH_ID and a.ODDS_CORP_NAME=c.ODDS_CORP_NAME and b.MATCH_NAME='挪甲' and a.HOST_KELLY2 <= c.LOSS_RATIO and a.HOST_KELLY1 <= a.HOST_KELLY2 and b.HOST_GOALS > b.VISITING_GOALS;

--group_concat: 列转行, 分隔符是",".
select group_concat(ODDS_CORP_NAME separator ',') from LOT_KELLY_RULE where COUNT >= 3 and RULE_TYPE='K2' and WIN_PROB >=0.7 and MATCH_NAME='德乙' order by WIN_PROB desc limit 50;

-- 指定公司的各联赛开盘次数
 select a.MATCH_NAME, count(*) from LOT_MATCH a, LOT_ODDS_EURO b where a.OK_MATCH_ID=b.OK_MATCH_ID and b.ODDS_CORP_NAME='Bet16' group by a.MATCH_NAME order by count(*) desc;

select b.ODDS_CORP_NAME, count(*) from LOT_MATCH a, LOT_ODDS_EURO b where a.OK_MATCH_ID=b.OK_MATCH_ID and a.MATCH_NAME='挪超'      group by b.ODDS_CORP_NAME order by count(*) desc;

--#################### 关于 LOT_KELLY_CORP_RESULT
-- 指定联赛的公司正确率情况.
select OK_URL_DATE, ODDS_CORP_NAME, COUNT, WIN_COUNT, EVEN_COUNT, WIN_PROB, ALL_SEQ, WIN_SEQ, EVEN_SEQ, NEGA_SEQ from LOT_KELLY_CORP_RESULT where MATCH_NAME='俄超' and OK_URL_DATE <'141201' order by ODDS_CORP_NAME, WIN_PROB desc;

-- 制定公司的联赛正确率情况.
select OK_URL_DATE, MATCH_NAME, COUNT, WIN_COUNT, EVEN_COUNT, WIN_PROB, ALL_SEQ, WIN_SEQ, EVEN_SEQ, NEGA_SEQ, RULE_TYPE from LOT_KELLY_CORP_RESULT where ODDS_CORP_NAME='Hong Kong JC' and OK_URL_DATE <'141201' order by MATCH_NAME, WIN_PROB desc;

-- 高正确率的公司预测情况.
select a.*  from LOT_KELLY_CORP_RESULT a, (select ODDS_CORP_NAME, count(*) from LOT_KELLY_CORP_RESULT WHERE MATCH_NAME='波兰甲' and WIN_PROB > 0.8 and OK_URL_DATE < '141105' group by ODDS_CORP_NAME having count(*) >=5) b WHERE MATCH_NAME='波兰甲' and a.ODDS_CORP_NAME=b.ODDS_CORP_NAME and a.OK_URL_DATE='141106' and ALL_SEQ like '%62|91|%';

-- 高正确率且开盘效率高
select a.MATCH_NAME, a.ODDS_CORP_NAME, count(*) WIN_COUNT, b.ALL_COUNT, count(*)/b.ALL_COUNT PROB from LOT_KELLY_CORP_RESULT a, (select MATCH_NAME, ODDS_CORP_NAME, count(*) ALL_COUNT from LOT_KELLY_CORP_RESULT group by MATCH_NAME, ODDS_CORP_NAME) b where a.MATCH_NAME=b.MATCH_NAME and a.ODDS_CORP_NAME=b.ODDS_CORP_NAME and a.WIN_PROB >0.8 group by a.MATCH_NAME, a.ODDS_CORP_NAME having count(*)/b.ALL_COUNT > 0.7 order by MATCH_NAME, PROB

select a.MATCH_NAME, a.ODDS_CORP_NAME, count(*) WIN_COUNT, b.ALL_COUNT, count(*)/b.ALL_COUNT PROB from LOT_KELLY_CORP_RESULT a, (select MATCH_NAME, ODDS_CORP_NAME, count(*) ALL_COUNT from LOT_KELLY_CORP_RESULT where OK_URL_DATE < '141106' group by MATCH_NAME, ODDS_CORP_NAME) b where a.MATCH_NAME=b.MATCH_NAME and a.ODDS_CORP_NAME=b.ODDS_CORP_NAME and OK_URL_DATE <'141106' and a.WIN_PROB >0.8 group by a.MATCH_NAME, a.ODDS_CORP_NAME having count(*)/b.ALL_COUNT > 0.8 order by MATCH_NAME, PROB;

-- 用高正确率且开盘效率高的公司检查某天的数据.
select a.OK_URL_DATE, a.MATCH_NAME, a.ODDS_CORP_NAME, a.COUNT, a.WIN_COUNT, a.ALL_SEQ, a.WIN_SEQ, a.EVEN_SEQ, a.NEGA_SEQ, b.         WIN_COUNT, b.ALL_COUNT, c.EURO_TIME_BEFORE_MATCH from LOT_KELLY_CORP_RESULT a, (select a.MATCH_NAME, a.ODDS_CORP_NAME, count(*) WIN_COUNT, b.ALL_COUNT, count(*)/b.ALL_COUNT PROB from LOT_KELLY_CORP_RESULT a, (select MATCH_NAME, ODDS_CORP_NAME, RULE_TYPE, count(*) ALL_COUNT from              LOT_KELLY_CORP_RESULT   where RULE_TYPE='K3' and OK_URL_DATE < '141201' group by MATCH_NAME, ODDS_CORP_NAME) b where a.MATCH_NAME=b. MATCH_NAME and a.        ODDS_CORP_NAME=b.ODDS_CORP_NAME and a.RULE_TYPE=b.RULE_TYPE and a.OK_URL_DATE <'141201' and a.WIN_PROB >   0.99 group by a.MATCH_NAME, a.ODDS_CORP_NAME having    count(*)/b.ALL_COUNT > 0.8 and count(*) > 2 order  by MATCH_NAME,  PROB) b, LOT_CORP c where a.MATCH_NAME=b.MATCH_NAME and a.ODDS_CORP_NAME=b.ODDS_CORP_NAME and a.ODDS_CORP_NAME=c.CORP_NAME and a.RULE_TYPE='K3' and a.OK_URL_DATE='141201';

select a.OK_URL_DATE, a.MATCH_NAME, a.ODDS_CORP_NAME, a.COUNT, a.WIN_COUNT, a.ALL_SEQ, a.WIN_SEQ, a.EVEN_SEQ, a.NEGA_SEQ, b.         WIN_COUNT, b.ALL_COUNT, c.EURO_TIME_BEFORE_MATCH from LOT_KELLY_CORP_RESULT a, (select a.MATCH_NAME, a.ODDS_CORP_NAME, count(*)      WIN_COUNT, b.ALL_COUNT, count(*)/b.ALL_COUNT PROB from LOT_KELLY_CORP_RESULT a, (select MATCH_NAME, ODDS_CORP_NAME, RULE_TYPE,       count(*) ALL_COUNT from              LOT_KELLY_CORP_RESULT   where RULE_TYPE='K4' and OK_URL_DATE < '141201' group by MATCH_NAME,    ODDS_CORP_NAME) b where a.MATCH_NAME=b. MATCH_NAME and a.        ODDS_CORP_NAME=b.ODDS_CORP_NAME and a.RULE_TYPE=b.RULE_TYPE and a.  OK_URL_DATE <'141201' and a.NEGA_PROB >   0.99 group by a.MATCH_NAME, a.ODDS_CORP_NAME having    count(*)/b.ALL_COUNT > 0.8 and       count(*) > 2 order  by MATCH_NAME,  PROB) b, LOT_CORP c where a.MATCH_NAME=b.MATCH_NAME and a.ODDS_CORP_NAME=b.ODDS_CORP_NAME and a. ODDS_CORP_NAME=c.CORP_NAME and a.RULE_TYPE='K4' and a.OK_URL_DATE='141201';

-- WIN EVEN 正确率高的, 指定 MATCH_NAME 和 不指定 MATCH_NAME.
select e.OK_URL_DATE, e.MATCH_NAME, e.ODDS_CORP_NAME, e.ALL_SEQ, e.WIN_SEQ, e.EVEN_SEQ, d.WIN_EVEN_COUNT, d.ALL_COUNT from (select c.MATCH_NAME, c.ODDS_CORP_NAME, c.WIN_EVEN_COUNT, c.ALL_COUNT, c.WIN_EVEN_PROB from (select a.MATCH_NAME, a.ODDS_CORP_NAME, a.WIN_EVEN_COUNT, b.ALL_COUNT, a.WIN_EVEN_COUNT/b.ALL_COUNT WIN_EVEN_PROB from (select MATCH_NAME, ODDS_CORP_NAME, count(*) WIN_EVEN_COUNT from LOT_KELLY_CORP_RESULT where MATCH_NAME='意乙' and RULE_TYPE='K3' AND OK_URL_DATE < '141204' AND NEGA_PROB=0 group by ODDS_CORP_NAME) a, (select ODDS_CORP_NAME, count(*) ALL_COUNT from LOT_KELLY_CORP_RESULT WHERE MATCH_NAME='意乙' and RULE_TYPE='K3' AND OK_URL_DATE < '141204' group by ODDS_CORP_NAME) b where a.ODDS_CORP_NAME=b.ODDS_CORP_NAME) c where c.WIN_EVEN_PROB > 0.9 and c.ALL_COUNT > 5 ) d, LOT_KELLY_CORP_RESULT e where d.MATCH_NAME=e.MATCH_NAME and d.ODDS_CORP_NAME=e.ODDS_CORP_NAME and e.RULE_TYPE='K3' and e.OK_URL_DATE='141204';

select e.OK_URL_DATE, e.MATCH_NAME, e.ODDS_CORP_NAME, e.ALL_SEQ, e.WIN_SEQ, e.EVEN_SEQ, d.WIN_EVEN_COUNT, d.ALL_COUNT from (select c.MATCH_NAME, c.ODDS_CORP_NAME, c.WIN_EVEN_COUNT, c.ALL_COUNT, c.WIN_EVEN_PROB from (select a.MATCH_NAME, a.ODDS_CORP_NAME, a.WIN_EVEN_COUNT, b.ALL_COUNT, a.WIN_EVEN_COUNT/b.ALL_COUNT WIN_EVEN_PROB from (select MATCH_NAME, ODDS_CORP_NAME, count(*) WIN_EVEN_COUNT from LOT_KELLY_CORP_RESULT where RULE_TYPE='K3' AND OK_URL_DATE < '141204' AND NEGA_PROB=0 group by MATCH_NAME, ODDS_CORP_NAME) a, (select MATCH_NAME, ODDS_CORP_NAME, count(*) ALL_COUNT from LOT_KELLY_CORP_RESULT WHERE RULE_TYPE='K3' AND OK_URL_DATE < '141204' group by MATCH_NAME, ODDS_CORP_NAME) b where a.MATCH_NAME=b.MATCH_NAME and a.ODDS_CORP_NAME=b.ODDS_CORP_NAME) c where c.WIN_EVEN_PROB > 0.9 and c.ALL_COUNT > 5 ) d, LOT_KELLY_CORP_RESULT e where d.MATCH_NAME=e.MATCH_NAME and d.ODDS_CORP_NAME=e.ODDS_CORP_NAME and e.RULE_TYPE='K3' and e.OK_URL_DATE='141204';
_
-- EVEN NEGA 正确率高的.
select e.OK_URL_DATE, e.MATCH_NAME, e.ODDS_CORP_NAME, e.ALL_SEQ, e.WIN_SEQ, d.EVEN_NEGA_COUNT, d.ALL_COUNT from (select c.MATCH_NAME, c.ODDS_CORP_NAME, c.EVEN_NEGA_COUNT, c.ALL_COUNT, c.EVEN_NEGA_PROB from (select a.MATCH_NAME, a.ODDS_CORP_NAME, a.EVEN_NEGA_COUNT, b.ALL_COUNT, a.EVEN_NEGA_COUNT/b.ALL_COUNT EVEN_NEGA_PROB from (select MATCH_NAME, ODDS_CORP_NAME, count(*) EVEN_NEGA_COUNT from LOT_KELLY_CORP_RESULT where MATCH_NAME='意乙' and RULE_TYPE='K4' AND OK_URL_DATE < '141204' AND WIN_PROB=0 group by ODDS_CORP_NAME) a, (select ODDS_CORP_NAME, count(*) ALL_COUNT from LOT_KELLY_CORP_RESULT WHERE MATCH_NAME='意乙' and RULE_TYPE='K4' AND OK_URL_DATE < '141204' group by ODDS_CORP_NAME) b where a.ODDS_CORP_NAME=b.ODDS_CORP_NAME) c where c.EVEN_NEGA_PROB > 0.9 and c.ALL_COUNT > 5 ) d, LOT_KELLY_CORP_RESULT e where d.MATCH_NAME=e.MATCH_NAME and d.ODDS_CORP_NAME=e.ODDS_CORP_NAME and e.RULE_TYPE='K4' and e.OK_URL_DATE='141204';

select e.OK_URL_DATE, e.MATCH_NAME, e.ODDS_CORP_NAME, e.ALL_SEQ, e.WIN_SEQ, d.EVEN_NEGA_COUNT, d.ALL_COUNT from (select c.MATCH_NAME, c.ODDS_CORP_NAME, c.EVEN_NEGA_COUNT, c.ALL_COUNT, c.EVEN_NEGA_PROB from (select a.MATCH_NAME, a.ODDS_CORP_NAME, a.EVEN_NEGA_COUNT, b.ALL_COUNT, a.EVEN_NEGA_COUNT/b.ALL_COUNT EVEN_NEGA_PROB from (select MATCH_NAME, ODDS_CORP_NAME, count(*) EVEN_NEGA_COUNT from LOT_KELLY_CORP_RESULT where RULE_TYPE='K4' AND OK_URL_DATE < '141204' AND WIN_PROB=0 group by MATCH_NAME, ODDS_CORP_NAME) a, (select MATCH_NAME, ODDS_CORP_NAME, count(*) ALL_COUNT from LOT_KELLY_CORP_RESULT WHERE RULE_TYPE='K4' AND OK_URL_DATE < '141204' group by MATCH_NAME, ODDS_CORP_NAME) b where a.MATCH_NAME = b.MATCH_NAME and a.ODDS_CORP_NAME=b.ODDS_CORP_NAME) c where c.EVEN_NEGA_PROB > 0.9 and c.ALL_COUNT > 5 ) d, LOT_KELLY_CORP_RESULT e where d.MATCH_NAME=e.MATCH_NAME and d.ODDS_CORP_NAME=e.ODDS_CORP_NAME and e.RULE_TYPE='K4' and e.OK_URL_DATE='141204';
--####################


--rule
select b.OK_URL_DATE, b.MATCH_SEQ, a.HOST_KELLY1, a.HOST_KELLY2, a.EVEN_KELLY1, a.EVEN_KELLY2, a.VISITING_KELLY1, a.VISITING_KELLY2, b.HOST_GOALS, b.VISITING_GOALS from (select t2.OK_MATCH_ID, t2.ODDS_CORP_NAME, t2.HOST_KELLY            HOST_KELLY2, t1.HOST_KELLY HOST_KELLY1, t1.EVEN_KELLY EVEN_KELLY1, t2.EVEN_KELLY EVEN_KELLY2, t1.VISITING_KELLY VISITING_KELLY1, t2.VISITING_KELLY VISITING_KELLY2, t1.HOST_ODDS HOST_ODDS1, t2.HOST_ODDS HOST_ODDS2 from LOT_ODDS_EURO_CHANGE t1, LOT_ODDS_EURO_CHANGE t2 where t1.OK_MATCH_ID=t2.OK_MATCH_ID and t1.ODDS_CORP_NAME=t2.ODDS_CORP_NAME and t1.ODDS_CORP_NAME='Bet16' and t1.ODDS_SEQ=2 and t2.ODDS_SEQ=10) a, LOT_MATCH   b, LOT_ODDS_EURO c where a.       OK_MATCH_ID=b.OK_MATCH_ID and a.                 OK_MATCH_ID=c.OK_MATCH_ID and a.ODDS_CORP_NAME=c. ODDS_CORP_NAME and b.MATCH_NAME='阿甲' and a.HOST_KELLY1 < a.HOST_KELLY2 and a.EVEN_KELLY1 > a.EVEN_KELLY2 and a.VISITING_KELLY1 > a.VISITING_KELLY2 and b.HOST_GOALS < b.VISITING_GOALS;

-- 观察 PROLOSS 的变化:
SELECT a.OK_URL_DATE, a.MATCH_SEQ, b.JOB_TYPE, b.PRO_LOSS, a.HOST_GOALS, a.VISITING_GOALS, c.HOST_BF, c.EVEN_BF, c.VISITING_BF FROM LOT_MATCH a, LOT_KELLY_MATCH_COUNT b, LOT_TRANS_PROP c where a.OK_URL_DATE=b.OK_URL_DATE and a.MATCH_SEQ=b.MATCH_SEQ and a.OK_MATCH_ID=c.ID and b.OK_URL_DATE='150102' and b.JOB_TYPE in ('A0', 'A1', 'A2', 'A3') and b.RULE_TYPE='K3';

-- LOT_ODDS_ASIA_TRENDS 简写.
SELECT OK_URL_DATE DATE, MATCH_SEQ SEQ, ODDS_CORP_NAME CORP, JOB_TYPE JT, MATCH_NAME, INIT_TIME I_TM, INIT_HANDICAP IHI, INIT_HOST_ODDS IH, INIT_VISITING_ODDS IV, HOST_ODDS H, HANDICAP HI, VISITING_ODDS V, HOST_KELLY HK, VISITING_KELLY VK, LOSS_RATIO LR FROM LOT_ODDS_ASIA_TRENDS WHERE OK_URL_DATE='150401' AND MATCH_SEQ=1 AND ODDS_CORP_NAME in ('平均值', '最大值', '最小值');


-- KI/Ki 分析规则. 
select a.OK_URL_DATE DATE, a.MATCH_SEQ SEQ, a.ODDS_CORP_NAME CORP, a.JOB_TYPE TYPE, a.MATCH_NAME NAME, a.INIT_TIME IT, a.INIT_HANDICAP IHC, a.INIT_HOST_ODDS IH, a.INIT_VISITING_ODDS IV,a.HOST_ODDS H, a.HANDICAP HC, a.VISITING_ODDS V, a.HOST_KELLY HK, a.VISITING_KELLY VK, a.LOSS_RATIO LR, b.JOB_TYPE TYPE, b.HOST_ODDS H, b.HANDICAP HC, b.VISITING_ODDS V, b.HOST_KELLY HK, b.VISITING_KELLY VK, b.LOSS_RATIO LR from LOT_ODDS_ASIA_TRENDS a, LOT_ODDS_ASIA_TRENDS b WHERE a.OK_URL_DATE=b.OK_URL_DATE and a.MATCH_SEQ = b.MATCH_SEQ and a.ODDS_CORP_NAME = b.ODDS_CORP_NAME and a.OK_URL_DATE='150403' AND a.MATCH_SEQ=427 AND a.JOB_TYPE='B0' and b.JOB_TYPE='B6' and b.HOST_KELLY > a.HOST_KELLY and b.VISITING_KELLY < a.VISITING_KELLY ;

-- EURO_ODDS_CHANGE_DAILY 分析.
select a.MATCH_SEQ MSEQ, a.ODDS_CORP_NAME NAME, a.ODDS_SEQ SEQ, a.TIME_BEFORE_MATCH TIME, a.HOST_ODDS H, a.EVEN_ODDS E, a.VISITING_ODDS V, a.LOSS_RATIO LR, a.HOST_KELLY HK, a.EVEN_KELLY EK, a.VISITING_KELLY VK, b.HOST_GOALS HG, b.VISITING_GOALS VG, c.SHOTS_ON_TARGET SO, c.SHOTS_OFF_TARGET SOF, c.CORNERS CO, c.POSSESSION PO from LOT_ODDS_EURO_CHANGE_DAILY a, LOT_MATCH b, LOT_MATCH_STATS c WHERE a.OK_URL_DATE = b.OK_URL_DATE AND a.MATCH_SEQ = b.MATCH_SEQ AND a.OK_URL_DATE=c.OK_URL_DATE AND a.MATCH_SEQ=c.MATCH_SEQ AND a.ODDS_CORP_NAME='Hong Kong JC' AND a.CHANGE_NUM >= 2 ORDER BY a.MATCH_SEQ, a.ODDS_SEQ;
