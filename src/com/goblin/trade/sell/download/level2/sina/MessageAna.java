package com.goblin.trade.sell.download.level2.sina;

import java.util.ArrayList;
import java.util.List;

import com.bmtech.utils.ForEach;
import com.bmtech.utils.KeyValuePair;
import com.bmtech.utils.Misc;
import com.bmtech.utils.log.LogHelper;
import com.goblin.trade.sell.download.level2.sina.vo.Level2_10Cand;
import com.goblin.trade.sell.download.level2.sina.vo.Level2_50orderInfo;
import com.goblin.trade.sell.download.level2.sina.vo.Level2_ZB;
import com.goblin.trade.sell.download.level2.sina.vo.Level2_index;
import com.goblin.trade.sell.download.level2.sina.vo.MsgKey;
import com.goblin.trade.sell.download.level2.sina.vo.SinaLevel2Data;
import com.goblin.trade.sell.download.level2.sina.vo.SinaMsgType;
import com.goblin.trade.sell.download.level2.sina.vo.ZB;

public class MessageAna {
	static LogHelper log = new LogHelper("MessageAna");

	public static List<SinaLevel2Data> parseMsg(String txt) {
		String lines[] = txt.split("\n");
		List<SinaLevel2Data> ret = new ArrayList<>(4);
		ForEach.asc(lines, (line, lineI) -> {
			line = line.trim();
			if (line.length() > 0) {
				try {
					SinaLevel2Data data = parseSingleLine(line);
					if (data != null) {
						ret.add(data);
					} else {
						if (line.endsWith("=")) {
							log.debug("can not parse line ends with '=', its %s", line);
						} else {
							log.warn("parse data line fail, can not parse! line is '%s'", line);
						}
					}
				} catch (Exception e) {
					log.error(e, "when parse data line '%s'", line);
				}
			}
		});
		return ret;
	}

	public static SinaLevel2Data parseSingleLine(String msgLine) {
		msgLine = msgLine.trim();
		if (msgLine.length() == 0) {
			return null;
		}
		KeyValuePair<String, String> pair = KeyValuePair.parse(msgLine);
		if (pair.getKeyString().length() == 0) {
			log.debug("got null key from message %s", msgLine);
			return null;
		}
		if (pair.value.length() == 0) {
			log.debug("got null value from message %s", msgLine);
			return null;
		}
		MsgKey msgKey = guessLineTypeFromKey(pair.getKeyString());
		if (msgKey == null) {
			log.debug("got no MsgKey from message %s", msgLine);
			return null;
		}

		SinaLevel2Data ret;
		switch (msgKey.getType()) {
		case Dang_10:
			ret = parseLevel2_10Top(msgKey, pair.getValueString());
			break;
		case Order:
			ret = parseLevel2_50orders(msgKey, pair.getValueString());
			break;
		case ZB_0:
		case ZB_1:
			ret = parseLevel2_ZB(msgKey, pair.getValueString());
			break;
		default:

			throw new RuntimeException("can not parse message from the orgMsg " + msgLine);
		}
		return ret;

	}

	public static MsgKey guessLineTypeFromKey(final String key) {
		if (!key.startsWith("2cn_")) {
			return null;
		}

		SinaMsgType type;
		String keyStr = key.substring("2cn_".length(), key.length());
		if (keyStr.endsWith("_orders")) {
			keyStr = keyStr.substring(0, keyStr.length() - "_orders".length());
			type = SinaMsgType.Order;
		} else if (keyStr.endsWith("_0")) {
			keyStr = keyStr.substring(0, keyStr.length() - "_0".length());
			type = SinaMsgType.ZB_0;
		} else if (keyStr.endsWith("_1")) {
			keyStr = keyStr.substring(0, keyStr.length() - "_1".length());
			type = SinaMsgType.ZB_1;
		} else {
			type = SinaMsgType.Dang_10;
		}
		String code = keyStr;
		if (keyStr.length() != 8) {
			throw new RuntimeException("unknown code info " + code);
		}
		return new MsgKey(code, type);

	}

	private static Level2_index parseLevel2_Indexinfo(MsgKey key, String[] tokens) {

		if (tokens.length != 12)
			return null;

		Level2_index l = new Level2_index(key);

		l.name = tokens[0];
		l.time = SinaDataUtils.strToDate(tokens[2] + " " + tokens[1]).getTime();
		l.price = Double.valueOf(tokens[7]);
		l.lastclose = Double.valueOf(tokens[3]);
		l.open = Double.valueOf(tokens[4]);
		l.high = Double.valueOf(tokens[5]);
		l.low = Double.valueOf(tokens[6]);

		l.volume = Long.valueOf(tokens[10]);
		l.amount = Double.valueOf(tokens[11]);

		return l;
	}

	private static SinaLevel2Data parseLevel2_10Top(MsgKey key, String value) {

		String[] tokens = value.split(",");
		if (tokens.length < 57) {
			if (tokens.length == 12) {
				key = new MsgKey(key.getCode(), SinaMsgType.Index);
				return parseLevel2_Indexinfo(key, tokens);
			} else {
				System.out.println("can not parse top10 from " + value);
			}
			return null;
		}

		Level2_10Cand l = new Level2_10Cand(key);
		l.name = tokens[0];
		l.time = SinaDataUtils.strToDate(tokens[2] + " " + tokens[1]).getTime();
		l.price = Double.valueOf(tokens[7]);

		l.lastclose = Double.valueOf(tokens[3]);
		l.open = Double.valueOf(tokens[4]);
		l.high = Double.valueOf(tokens[5]);
		l.low = Double.valueOf(tokens[6]);

		for (int i = 0; i < 10; i++) {
			int sell_start = 46 + i;
			int buy_start = 26 + i;

			l.buyPrices[i] = Double.valueOf(tokens[buy_start]);
			l.buyVolumns[i] = Long.valueOf(tokens[buy_start + 10]);

			l.sellPrices[i] = Double.valueOf(tokens[sell_start]);
			l.sellVolumn[i] = Long.valueOf(tokens[sell_start + 10]);
		}

		return l;

	}

	private static Level2_50orderInfo parseLevel2_50orders(MsgKey key, String value) {

		String[] v = value.split(",");

		Level2_50orderInfo info = new Level2_50orderInfo(key);

		info.buy_price = Double.valueOf(v[2]);
		info.buy_count = Long.valueOf(v[3]);
		info.buy_bi = Integer.valueOf(v[4]);
		info.sell_price = Double.valueOf(v[5]);
		info.sell_count = Long.valueOf(v[6]);
		info.sell_bi = Integer.valueOf(v[7]);

		if (info.sell_bi > 0) {
			String[] sellvol_str = v[10].split("\\|");
			info.sellvol = new long[sellvol_str.length];
			for (int i = 0; i < info.sellvol.length; i++) {
				info.sellvol[i] = Long.valueOf(sellvol_str[i]);
			}

		}

		if (info.buy_bi > 0) {
			String[] buyvol_str = v[8].split("\\|");
			info.buyvol = new long[buyvol_str.length];
			for (int i = 0; i < info.buyvol.length; i++) {
				info.buyvol[i] = Long.valueOf(buyvol_str[i]);
			}
		}

		return info;

	}

	private static Level2_ZB parseLevel2_ZB(MsgKey key, String value) {

		String[] v = value.split(",");
		String date = SinaDataUtils.getTodayStr();
		List<ZB> zbs = new ArrayList<>();

		for (int i = 0; i < v.length; i++) {
			String[] item = v[i].split("\\|");
			if (item.length < 9) {
				System.out.println("can not parse zb " + Misc.toString(item));
				continue;
			}

			ZB zb = new ZB();
			zb.sortid = Long.valueOf(item[0]);
			String time = date + " " + item[1];
			zb.time = SinaDataUtils.longstrToDate(time).getTime();
			zb.price = Double.valueOf(item[2]);
			zb.vol = Long.valueOf(item[3]);
			zb.op = item[7].equals("2") ? ZB.OP_BUY : (item[7].equals("0") ? ZB.OP_SELL : ZB.OP_PING);

			zbs.add(zb);

		}
		Level2_ZB ret = new Level2_ZB(key);
		ret.addDatas(zbs);
		return ret;

	}
}
