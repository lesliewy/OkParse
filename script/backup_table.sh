# mysql 5.7 doesn't exits gtid.
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_ALL_AVERAGE > ../backup/database/tables/161014/LOT_ALL_AVERAGE.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_BF_LISTING > ../backup/database/tables/161014/LOT_BF_LISTING.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_BF_TURNOVER_DETAIL > ../backup/database/tables/161014/LOT_BF_TURNOVER_DETAIL.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_CONFIG > ../backup/database/tables/161014/LOT_CONFIG.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_CORP > ../backup/database/tables/161014/LOT_CORP.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_DAT_MATCH > ../backup/database/tables/161014/LOT_DAT_MATCH.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_EURO_ASIA_REFER > ../backup/database/tables/161014/LOT_EURO_ASIA_REFER.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_EURO_TRANS_ASIA > ../backup/database/tables/161014/LOT_EURO_TRANS_ASIA.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_EURO_CHANGE_DAILY_STATS > ../backup/database/tables/161014/LOT_EURO_CHANGE_DAILY_STATS.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_INDEX_STATS > ../backup/database/tables/161014/LOT_INDEX_STATS.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_JOB > ../backup/database/tables/161014/LOT_JOB.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_KELLY_CORP_COUNT > ../backup/database/tables/161014/LOT_KELLY_CORP_COUNT.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_KELLY_CORP_RESULT > ../backup/database/tables/161014/LOT_KELLY_CORP_RESULT.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_KELLY_MATCH_COUNT > ../backup/database/tables/161014/LOT_KELLY_MATCH_COUNT.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_KELLY_RULE > ../backup/database/tables/161014/LOT_KELLY_RULE.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_LEAGUE_POINTS > ../backup/database/tables/161014/LOT_LEAGUE_POINTS.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_MATCH > ../backup/database/tables/161014/LOT_MATCH.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_MATCH_SKIP > ../backup/database/tables/161014/LOT_MATCH_SKIP.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_MATCH_STATS > ../backup/database/tables/161014/LOT_MATCH_STATS.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_ODDS_ASIA > ../backup/database/tables/161014/LOT_ODDS_ASIA.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_ODDS_ASIA_TRENDS > ../backup/database/tables/161014/LOT_ODDS_ASIA_TRENDS.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_ODDS_ASIA_CHANGE > ../backup/database/tables/161014/LOT_ODDS_ASIA_CHANGE.sql;
# ADD LOT_ODDS_ASIA_CHANGE_DAILY
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_ODDS_ASIA_CHANGE_DAILY > ../backup/database/tables/161014/LOT_ODDS_ASIA_CHANGE_DAILY.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_ODDS_EURO > ../backup/database/tables/161014/LOT_ODDS_EURO.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_ODDS_EURO_CHANGE > ../backup/database/tables/161014/LOT_ODDS_EURO_CHANGE.sql;
# ADD LOT_ODDS_EURO_CHANGE_DAILY
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_ODDS_EURO_CHANGE_DAILY > ../backup/database/tables/161014/LOT_ODDS_EURO_CHANGE_DAILY.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_ODDS_EURO_CHANGE_ALL > ../backup/database/tables/161014/LOT_ODDS_EURO_CHANGE_ALL.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_ODDS_EURO_HANDICAP > ../backup/database/tables/161014/LOT_ODDS_EURO_HANDICAP.sql;
# ADD LOT_PROB_AVG
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_PROB_AVG > ../backup/database/tables/161014/LOT_PROB_AVG.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_SCORE_ODDS > ../backup/database/tables/161014/LOT_SCORE_ODDS.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_TRANS_PROP > ../backup/database/tables/161014/LOT_TRANS_PROP.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql LOT_WEIGHT_RULE > ../backup/database/tables/161014/LOT_WEIGHT_RULE.sql;
mysqldump -u root --password=mysql --set-gtid-purged=OFF mysql TEST1 > ../backup/database/tables/161014/TEST1.sql;
