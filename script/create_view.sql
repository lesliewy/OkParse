CREATE OR REPLACE VIEW LOT_DAT_MATCH AS
SELECT a.OK_MATCH_ID, a.MATCH_NAME, a.MATCH_SEQ, a.MATCH_TIME, a.CLOSE_TIME, a.HOST_TEAM_NAME, a.VISITING_TEAM_NAME, a.HOST_GOALS, a.VISITING_GOALS, a.OK_URL_DATE, b.HOST_ODDS, b.EVEN_ODDS, b.VISITING_ODDS, b.HOST_PROB, b.EVEN_PROB, b.VISITING_PROB, c.HOST_BF, c.EVEN_BF, c.VISITING_BF, c.HOST_COMP, c.EVEN_COMP, c.VISITING_COMP, c.HOST_BJ_SINGLE, c.EVEN_BJ_SINGLE, c.VISITING_BJ_SINGLE, c.HOST_BF_PROLOSS_INDEX, c.EVEN_BF_PROLOSS_INDEX, c.VISITING_BF_PROLOSS_INDEX, c.HOST_COMP_PROLOSS_INDEX, c.EVEN_COMP_PROLOSS_INDEX, c.VISITING_COMP_PROLOSS_INDEX, d.HOST_BUYERS_PRICE, d.HOST_BUYERS_QUANTITY, d.EVEN_BUYERS_PRICE, d.EVEN_BUYERS_QUANTITY, d.VISITING_BUYERS_PRICE, d.VISITING_BUYERS_QUANTITY, d.HOST_SELLERS_PRICE, d.HOST_SELLERS_QUANTITY, d.EVEN_SELLERS_PRICE, d.EVEN_SELLERS_QUANTITY, d.VISITING_SELLERS_PRICE, d.VISITING_SELLERS_QUANTITY, a.TIMESTAMP
FROM LOT_MATCH a, LOT_ALL_AVERAGE b, LOT_TRANS_PROP c, LOT_BF_LISTING d 
WHERE a.OK_MATCH_ID=b.ID
      and a.OK_MATCH_ID=c.ID
      and a.OK_MATCH_ID=d.ID
