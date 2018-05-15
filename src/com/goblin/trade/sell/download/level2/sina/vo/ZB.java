package com.goblin.trade.sell.download.level2.sina.vo;

public class ZB {

	public long sortid;
	public long time;
	public double price;
	public long vol;
	public long buynum;
	public long sellnum;

	public int op;

	static public final int OP_BUY = 1;
	static public final int OP_SELL = -1;
	static public final int OP_PING = 0;

	public void print() {
		new Exception("").printStackTrace();
	}
}
