package com.goblin.trade.sell.download.level2.sina;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;

import com.bmtech.utils.KeyValuePair;
import com.bmtech.utils.Misc;
import com.bmtech.utils.http.HttpCrawler;
import com.bmtech.utils.log.LogHelper;
import com.goblin.trade.sell.download.level2.sina.vo.AuthInfo;

public class SinaDataUtils {
	private static SinaDataUtils instance;
	private final LogHelper log = new LogHelper("SinaDataUtils");
	private String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36";

	static synchronized public SinaDataUtils getInstance() {
		if (instance == null) {
			instance = new SinaDataUtils();
		}
		return instance;
	}

	private SinaDataUtils() {
		SinaDataUtilDaemon.daemon.start();
	}

	public static BasicCookieStore parseFromTxt(List<String> tokens) throws UnsupportedEncodingException {
		BasicCookieStore st = new BasicCookieStore();
		for (String xx : tokens) {
			KeyValuePair<String, String> pair = KeyValuePair.parse(xx);
			BasicClientCookie cookie = new BasicClientCookie(pair.key, URLEncoder.encode(pair.value, "gbk"));
			cookie.setDomain(".sina.com.cn");

			cookie.setPath("/");
			cookie.setAttribute("domain", ".sina.com.cn");
			cookie.setAttribute("path", "/");
			cookie.setExpiryDate(new Date(System.currentTimeMillis() + 1000 * 36 * 60 * 60));
			st.addCookie(cookie);
		}
		return st;
	}

	private static String makeLevelQueryStr(String symbol) {
		String str = "";
		String daimas = generateCode(symbol);
		str = "2cn_" + daimas + ",2cn_" + daimas + "_orders" + ",2cn_" + daimas + "_0" + ",2cn_" + daimas + "_1";
		return str;
	}

	private static String _clientIp = null;
	private static long _lastGot;

	public static String getLevel2ClientIpInfo(boolean refreshIp) throws IOException {
		boolean reGain = false;
		if (refreshIp) {
			reGain = true;
		} else {
			if (_clientIp == null || _lastGot == 0) {
				reGain = true;
			}
			if (System.currentTimeMillis() - _lastGot > 10 * 60 * 1000) {
				reGain = true;
			}
		}
		if (reGain) {
			String str = HttpCrawler.getString("http://ff.sinajs.cn/?list=sys_clientip");
			String clientIp = Misc.substring(str, "\"", "\"");
			_clientIp = clientIp;
			_lastGot = System.currentTimeMillis();
			LogHelper.iDebug("ff.sinajs.cn tell me, my ip is %s", _clientIp);
			return clientIp;
		} else {
			// LogHelper.iDebug("using cache ip %s", _clientIp);
			return _clientIp;
		}
	}

	public static String makeLevel2QueryString(List<String> symbols) {
		String list = "";
		if (symbols != null) {
			for (String symbol : symbols) {
				list += "," + makeLevelQueryStr(symbol);
			}
		}
		// System.out.println(list);
		list = list.substring(list.indexOf(","));
		return list;
	}

	public static String generateCode(String id) {
		if (id == null || id.equals(""))
			return "";

		String code = "";
		String flag = "sh";

		// String list =
		// "sh000001_zdp,sz399001_zdp,sz399300_zdp,sz399005_zdp,sz399102_zdp";

		if (id.startsWith("50")) {
			flag = "sh";
		} else if (id.startsWith("160") || id.startsWith("150")) {

			flag = "sz";
		} else if (id.equals("000001") || id.equals("000300")) {// 2cn_sh000001=锟斤拷证指锟斤拷,//
			flag = "sh";
		} else if (id.startsWith("399")) {

			flag = "sz";
		} else if (id.startsWith("600") || id.startsWith("60")) {
			flag = "sh";
		} else if (id.startsWith("002") || id.startsWith("00")) {
			flag = "sz";
		} else if (id.startsWith("300")) {
			flag = "sz";
		} else {
			flag = "sz";
		}
		code += flag + id;
		return code;
	}

	public static List<NameValuePair> getParam(Map<String, String> parameterMap) {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		for (String key : parameterMap.keySet()) {
			param.add(new BasicNameValuePair(key, parameterMap.get(key)));
		}

		return param;
	}

	public static String msToShortStr(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String str = format.format(time);
		return str;
	}

	public static String getTodayStr() {
		return msToShortStr(System.currentTimeMillis());
	}

	public static Date longstrToDate(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 
	 * @param str
	 * @return date
	 */
	public static Date strToDate(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	public void putTicket(String queryStr) throws IOException {
		log.info("new ticket %s", queryStr);
		AuthInfo ticket = new AuthInfo(queryStr, System.currentTimeMillis());
		if (this.ticket != null) {
			String crtToken = this.ticket.getToken();
			if (crtToken != null) {
				if (crtToken.equals(ticket.getToken())) {
					log.error("re-put same ticket! reject %s vs %s", this.ticket, ticket);
					return;
				}
			}
		}
		this.ticket = ticket;
	}

	AuthInfo ticket;

	public AuthInfo getTicket() {
		return ticket;
	}

	public static String getAuthUrl(List<String> symbols, boolean refreshIp) throws IOException {
		String query = "A_hq";
		String ip = SinaDataUtils.getLevel2ClientIpInfo(refreshIp);
		String list = SinaDataUtils.makeLevel2QueryString(symbols);

		int kick = 1;

		double random = Math.random();
		Map<String, String> map = new HashMap<String, String>();
		map.put("query", query);
		map.put("ip", ip);
		map.put("_", random + "");
		map.put("list", list);
		// kick
		map.put("kick", kick + "");

		List<NameValuePair> para = SinaDataUtils.getParam(map);
		String u = "https://current.sina.com.cn/auth/api/jsonp.php/var%20KKE_auth_hLEGVjyxJ=/AuthSign_Service.getSignCode?";
		StringBuilder sb = new StringBuilder();
		for (NameValuePair p : para) {
			if (sb.length() > 0)
				sb.append('&');
			sb.append(p.getName() + "=" + URLEncoder.encode(p.getValue(), "utf8"));
		}
		return (u + sb);
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		log.info("setting userAgent %s", userAgent);
		if (userAgent != null && userAgent.length() > 0)
			this.userAgent = userAgent;
		else {
			log.error("reject bad userAgent %s", userAgent);
		}
	}
}
