package com.goblin.trade.sell.download.level2.sina.vo;

public class Level2_10Cand extends SinaLevel2Data {
	public Level2_10Cand(String code) {
		super(SinaMsgType.Dang_10, code);
	}

	public Level2_10Cand(MsgKey key) {
		super(key);
	}

	public String name;

	public double lastclose;
	public double open;
	public double high;
	public double low;

	public double price;
	public double tradecount;
	public double amount;
	public long volume;
	public long time;
	public int op;

	public long[] sellVolumn = new long[10];
	public double[] sellPrices = new double[10];
	public long[] buyVolumns = new long[10];
	public double[] buyPrices = new double[10];

	static public final int OP_BUY = 0;
	static public final int OP_SELL = 1;

	public double getSpread() {
		return (sellPrices[0] - buyPrices[0]);
	}

	public double getSpreadRate() {
		if (buyPrices[0] > 0) {
			return (sellPrices[0] - buyPrices[0]) / buyPrices[0];
		}
		return Double.MAX_VALUE;
	}

}
