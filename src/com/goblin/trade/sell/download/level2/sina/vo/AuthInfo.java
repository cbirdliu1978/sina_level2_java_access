package com.goblin.trade.sell.download.level2.sina.vo;

import java.io.UnsupportedEncodingException;

import org.json.JSONObject;

public class AuthInfo {
	private String token;
	private int msg_code;
	private int timeout;
	private String input;
	private long setTime = 0;

	public AuthInfo(String strHtml, long startTime) throws UnsupportedEncodingException {
		String str = strHtml.substring(strHtml.indexOf("{"), strHtml.lastIndexOf("}") + 1);

		JSONObject jsonObject = new JSONObject(str);
		token = jsonObject.optString("result");
		msg_code = jsonObject.optInt("msg_code");
		timeout = jsonObject.optInt("timeout");

		this.setTime = startTime;
	}

	@Override
	public String toString() {
		return "AuthInfo [token=" + token + ", msg_code=" + msg_code + ", timeout=" + timeout + ", input=" + input
				+ ", setTime=" + setTime + "]";
	}

	public int getMsg_code() {
		return msg_code;
	}

	public void setMsg_code(int msg_code) {
		this.msg_code = msg_code;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getSetTime() {
		return setTime;
	}

	public boolean isValid() {
		return (System.currentTimeMillis() - this.getSetTime()) < 180 * 1000;

	}

}