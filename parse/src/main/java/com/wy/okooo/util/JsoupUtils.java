/**
 * 
 */
package com.wy.okooo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.util.StringUtils;

import com.wy.okooo.parse.ParseUrl;
import com.wy.okooo.parse.impl.ParseOkoooUrl;

/**
 * 工具类
 * 
 * @author leslie
 * 
 */
public class JsoupUtils {
	// log4j
	private static Logger LOGGER = Logger.getLogger(JsoupUtils.class.getName());

	// 对应 http://www.okooo.com/danchang/
	private static Document singleMatchDoc;

	// 解析okooo的url
	private static ParseUrl parseOkoooUrl = new ParseOkoooUrl();

	// 连接网络的次数.
	private static int numOfConn = 0;

	/**
	 * 获取单场胜平负url(http://www.okooo.com/danchang/) 的 Document.
	 * 
	 */
	public static Document getMatchesDoc() {
		if (singleMatchDoc != null) {
			return singleMatchDoc;
		}
		URL url;
		try {
			url = new URL(parseOkoooUrl.findMatchUrl()); // 连接一个页面
			singleMatchDoc = Jsoup.parse(url, 5 * 1000); // 解析获取Document对象
			numOfConn++;
		} catch (MalformedURLException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}

		return singleMatchDoc;
	}

	/**
	 * 获取指定url的doc.(http://www.okooo.com/danchang/140912/) 用于往期的match doc.
	 * 
	 * @param url
	 * @return
	 */
	public static Document getMatchesDoc(String matchUrl) {
		if (singleMatchDoc != null) {
			return singleMatchDoc;
		}
		try {
			// singleMatchDoc = Jsoup.parse(url, 5 * 1000); // 解析获取Document对象
			String html = getDocByHttpClient(matchUrl);
			if (!StringUtils.isEmpty(html)) {
				singleMatchDoc = Jsoup.parse(html);
			}
			LOGGER.info("leslie html:" + html);

			numOfConn++;
		} catch (MalformedURLException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}

		return singleMatchDoc;
	}

	/**
	 * 获取某场具体比赛赔率的url(http://www.okooo.com/soccer/match/686923/odds/); 根据
	 * docType 区分欧赔，亚盘;
	 * 
	 * @param matchSeq
	 *            比赛序号.
	 * @param docType
	 *            类型, 见OkConstant中的 DOC_TYPE_
	 * @return
	 */
	public static Document getOddsDoc(int matchSeq, String docType) {
		String matchUrl = "";

		// 欧赔页面
		if (OkConstant.DOC_TYPE_EURO_ODDS.equals(docType)) {
			matchUrl = parseOkoooUrl.findEuroOddsUrl(getMatchesDoc(), matchSeq);
		}

		// 亚盘页面
		if (OkConstant.DOC_TYPE_ASIA_ODDS.equals(docType)) {
			matchUrl = parseOkoooUrl.findAsiaOddsUrl(getMatchesDoc(), matchSeq);
		}
		LOGGER.debug("match url: " + matchUrl);

		return getDocByUrl(matchUrl);
	}

	/**
	 * 获取某场具体比赛某个博彩公司赔率变化的url(http://www.okooo.com/soccer/match/686923/odds/
	 * change/24/); 根据 docType 区分欧赔，亚盘;
	 * 
	 * @param matchSeq
	 * @param docType
	 * @param corpNo
	 *            公司序号.
	 * @return
	 */
	public static Document getOddsChangeDoc(int matchSeq, String docType,
			int corpNo) {
		String matchUrl = "";
		// 欧赔变化页面
		if (OkConstant.DOC_TYPE_EURO_ODDS_CHANGE.equals(docType)) {
			matchUrl = parseOkoooUrl.findEuroOddsChangeUrl(getMatchesDoc(),
					matchSeq, corpNo);
		}

		// 亚盘变化页面
		if (OkConstant.DOC_TYPE_ASIA_ODDS_CHANGE.equals(docType)) {
			matchUrl = parseOkoooUrl.findAsiaOddsChangeUrl(getMatchesDoc(),
					matchSeq, corpNo);
		}

		LOGGER.debug("match url: " + matchUrl);

		return getDocByUrl(matchUrl);
	}

	/**
	 * 获取交易盈亏页面Document(http://www.okooo.com/soccer/match/734052/exchanges/);
	 * 
	 * @param matchSeq
	 * @return
	 */
	public static Document getExchangeDoc(int matchSeq) {
		String matchUrl = parseOkoooUrl.findExchangeUrl(getMatchesDoc(),
				matchSeq);
		return getDocByUrl(matchUrl);
	}

	public static Document getExchangeDetailDoc(int matchSeq) {
		String matchUrl = parseOkoooUrl.findExchangeDetailUrl(getMatchesDoc(),
				matchSeq);
		return getDocByUrl(matchUrl);
	}

	private static Document getDocByUrl(String url) {
		Document matchDoc = null;
		/*
		 * 这里不使用 jsoup 的 connect 和 parse, 这里访问okooo的url, 经常返回 502. 使用
		 * httpclient的代理功能获取html.
		 */
		try {
			/*
			 * URL url = new URL(matchUrl); matchDoc = Jsoup.parse(url, 5 *
			 * 1000); matchDoc = Jsoup.connect(matchUrl).get();
			 */
			String html = getDocByHttpClient(url);
			if (!StringUtils.isEmpty(html)) {
				matchDoc = Jsoup.parse(html);
			}
			numOfConn++;
		} catch (MalformedURLException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return matchDoc;
	}

	/**
	 * 使用httpclient 获取 html.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static String getDocByHttpClient(String url) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		StringBuffer sb = new StringBuffer();
		try {
			HttpGet httpget = new HttpGet(url);
			// HttpPost httppost = new
			// HttpPost("http://www.okooo.com/soccer/match/686923/odds/");

			// 这里必须使用代理，否则 okooo的返回 502, 使用 GAE 作为代理.
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectionRequestTimeout(10000)
					.setConnectTimeout(10000).setSocketTimeout(10 * 1000)
					.setProxy(new HttpHost("127.0.0.1", 8087)).build();
			httpget.setConfig(requestConfig);

			CloseableHttpResponse response = (CloseableHttpResponse) httpclient
					.execute(httpget);
			try {
				LOGGER.debug("----------------------------------------");
				LOGGER.debug("response.getStatusLine(): "
						+ response.getStatusLine());

				// Get hold of the response entity
				// 这里的返回不是 chunked, entity.isChunked() 为 false.
				HttpEntity entity = response.getEntity();

				// If the response does not enclose an entity, there is no need
				// to bother about connection release
				if (entity != null) {
					InputStream instream = entity.getContent();
					try {
						instream.read();
						// 使用GB2312, 因为返回的 html 的 CONTENT 中设置了.
						BufferedReader in = new BufferedReader(
								new InputStreamReader(instream, "GB2312"));
						String line = "";
						while ((line = in.readLine()) != null) {
							sb.append(line);
						}
					} catch (IOException ex) {
						// In case of an IOException the connection will be
						// released
						// back to the connection manager automatically
						throw ex;
					} finally {
						// Closing the input stream will trigger connection
						// release
						instream.close();
					}
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			LOGGER.error(e);
			throw e;
		} catch (IOException e) {
			LOGGER.error(e);
			throw e;
		} finally {
			httpclient.close();
		}
		return sb.toString();
	}

	public static String getAjaxDocByHttpClient(String url, String encoding, int timeout) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost method = null;
		StringBuilder sb = new StringBuilder("");
		try {
			method = new HttpPost(url);

//			method.addHeader("connection", "keep-alive");

//			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//			nvps.add(new BasicNameValuePair("IDToken1", "username"));
//			nvps.add(new BasicNameValuePair("IDToken2", "password"));
//			method.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			
			// 2017.09.09: 对于欧赔、亚盘的公司赔率详情,添加对应的referer, 否则无法下载.
			if(url.matches(".*/soccer/match/[0-9]{6}/.*")){		
				method.addHeader("Referer", url.replaceAll("change/[0-9]*/", ""));
			}
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectionRequestTimeout(timeout)
					.setConnectTimeout(timeout).setSocketTimeout(timeout)
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
			LOGGER.error(e);
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != method) {
				method.releaseConnection();
			}
			httpclient.close();
		}
		return sb.toString();
	}
	
	public static String getAjaxDocByHttpClient(String url, String encoding, Map<String, String> params) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost method = null;
		StringBuilder sb = new StringBuilder("");
		try {
			method = new HttpPost(url);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			if(params != null && !params.isEmpty()){
				for(Entry<String, String> entry : params.entrySet()){
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			method.setEntity(new UrlEncodedFormEntity(nvps, encoding));
			
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectionRequestTimeout(10000)
					.setConnectTimeout(10000).setSocketTimeout(2 * 1000)
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
			LOGGER.error(e);
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != method) {
				method.releaseConnection();
			}
			httpclient.close();
		}
		return sb.toString();
	}

	public static int getNumOfConn() {
		return numOfConn;
	}

}
