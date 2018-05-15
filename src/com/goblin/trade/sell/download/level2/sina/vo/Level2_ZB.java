package com.goblin.trade.sell.download.level2.sina.vo;

import java.util.ArrayList;
import java.util.List;

public class Level2_ZB extends SinaLevel2Data {
	private List<ZB> lst = new ArrayList<>();

	public Level2_ZB(MsgKey key) {
		super(key);
	}

	public Level2_ZB(boolean isZero, String code) {
		super(isZero ? SinaMsgType.ZB_0 : SinaMsgType.ZB_1, code);
	}

	public void addDatas(List<ZB> list) {
		this.lst.addAll(list);
	}

	public List<ZB> getLst() {
		return lst;
	}

}
