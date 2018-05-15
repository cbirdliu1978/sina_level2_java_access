package com.goblin.trade.sell.download.level2.sina.vo;

import com.bmtech.utils.Misc;

public abstract class SinaLevel2Data {
	protected final MsgKey key;

	public SinaLevel2Data(MsgKey key) {
		this.key = key;
	}

	public SinaLevel2Data(SinaMsgType type, String code) {
		this(new MsgKey(code, type));
	}

	public SinaMsgType getMsgType() {
		return key.getType();
	}

	public String getCode() {
		return key.getCode();
	}

	@Override
	public String toString() {
		return Misc.toString(this);
	}

	public final void print() {
		String str = this.toString();
		System.out.println(str);
	}
}
