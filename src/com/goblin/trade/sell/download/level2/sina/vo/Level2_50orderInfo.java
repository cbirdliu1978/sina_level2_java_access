package com.goblin.trade.sell.download.level2.sina.vo;

public class Level2_50orderInfo extends SinaLevel2Data {
	public Level2_50orderInfo(String code) {
		super(SinaMsgType.Order, code);
	}

	public Level2_50orderInfo(MsgKey key) {
		super(key);
	}

	public double buy_price;
	public long buy_count;
	public int buy_bi;
	public long[] buyvol;

	public double sell_price;
	public long sell_count;
	public int sell_bi;
	public long[] sellvol;

}
