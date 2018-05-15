package com.goblin.trade.sell.download.level2.sina.vo;

public class Level2_index extends SinaLevel2Data {
	public Level2_index(MsgKey key) {
		super(key);
	}

	public String symbol;
	public String name;

	public double price;
	public double tradecount;
	public double amount;
	public double volume;
	public long time;

	public double high;
	public double low;
	public double open;
	public double lastclose;

	public double getPercent() {
		return (price - lastclose) * 1.0 / lastclose;
	}

}
