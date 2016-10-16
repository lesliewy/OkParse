/**
 * 
 */
package com.wy.okooo.domain;

import java.sql.Timestamp;

/**
 * @author leslie
 *
 */
public class KellyCorpCount {

		private String matchName;
		
		private Double beginHostOdds;
		
		private Double endHostOdds;

		private Integer corpCountWin;
		
		private String ruleType;
		
		private Timestamp timestamp;

		public String getMatchName() {
			return matchName;
		}

		public void setMatchName(String matchName) {
			this.matchName = matchName;
		}

		public Double getBeginHostOdds() {
			return beginHostOdds;
		}

		public void setBeginHostOdds(Double beginHostOdds) {
			this.beginHostOdds = beginHostOdds;
		}

		public Double getEndHostOdds() {
			return endHostOdds;
		}

		public void setEndHostOdds(Double endHostOdds) {
			this.endHostOdds = endHostOdds;
		}

		public Integer getCorpCountWin() {
			return corpCountWin;
		}

		public void setCorpCountWin(Integer corpCountWin) {
			this.corpCountWin = corpCountWin;
		}

		public String getRuleType() {
			return ruleType;
		}

		public void setRuleType(String ruleType) {
			this.ruleType = ruleType;
		}

		public Timestamp getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(Timestamp timestamp) {
			this.timestamp = timestamp;
		}
		
}
