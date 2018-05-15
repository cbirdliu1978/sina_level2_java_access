package com.goblin.trade.sell.download.level2.sina.vo;

public class MsgKey {
	private final String code;
	private final SinaMsgType type;

	public MsgKey(String code, SinaMsgType type) {
		super();
		if (code.startsWith("sh") || code.startsWith("sz")) {
			code = code.substring(2);
		}
		code = code.trim();
		if (code.length() != 6) {
			throw new RuntimeException("unknown stock code " + code);
		}
		this.code = code;
		this.type = type;
	}

	@Override
	public String toString() {
		return "MsgSk [code=" + getCode() + ", type=" + type + "]";
	}

	public SinaMsgType getType() {
		return type;
	}

	public String getCode() {
		return code;
	}

}
