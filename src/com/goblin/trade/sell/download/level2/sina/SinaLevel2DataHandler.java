package com.goblin.trade.sell.download.level2.sina;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.bmtech.utils.DaemonLoopThread;
import com.bmtech.utils.io.LineWriter;
import com.bmtech.utils.log.LogHelper;
import com.goblin.trade.sell.download.level2.sina.vo.SinaLevel2Data;

public abstract class SinaLevel2DataHandler {

	DaemonLoopThread savor = new DaemonLoopThread("saveThread", 100) {

		private boolean isInit = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS__");
		LineWriter lw;

		private void init() throws IOException {
			isInit = true;
			File f = new File("level2/" + new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()) + ".txt");
			File par = f.getParentFile();
			if (!par.exists())
				par.mkdirs();
			lw = new LineWriter(f, true);
			lw.writeLine("");

		}

		@Override
		protected boolean singleRound() throws Exception {
			if (!isInit) {
				this.init();
			}
			while (true) {

				String str = saveQueue.poll(1, TimeUnit.SECONDS);
				// log.debug("recv data %s", str);
				if (lw != null) {
					if (str == null) {
						lw.flush();
						break;
					} else {
						lw.write(sdf.format(System.currentTimeMillis()));
						lw.writeLine(str);
					}
				}
			}
			if (this.isStop()) {
				saveQueue = null;
			}
			return false;
		}

	};

	DaemonLoopThread consumer = new DaemonLoopThread("MsgConsumer", 1) {

		@Override
		protected boolean singleRound() throws Exception {
			while (true) {
				String str = msgQueue.poll(1, TimeUnit.SECONDS);
				if (str == null) {
					break;
				} else {
					List<SinaLevel2Data> vx = MessageAna.parseMsg(str);
					consume(vx);
				}
			}
			if (this.isStop()) {
				msgQueue = null;
			}
			return false;
		}
	};

	LogHelper log = new LogHelper(this.getClass().getSimpleName());
	private LinkedBlockingQueue<String> msgQueue = new LinkedBlockingQueue<>();
	private LinkedBlockingQueue<String> saveQueue = new LinkedBlockingQueue<>();

	public SinaLevel2DataHandler() {
		LogHelper.log.info("connected %s", this);
		this.consumer.start();
		this.savor.start();
	}

	public abstract void consume(List<SinaLevel2Data> vx);

	public synchronized void onMessage(String msg) {
		try {
			msgQueue.put(msg);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		try {
			saveQueue.put(msg);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void onConnect() {

	}

	public void onClose(int statusCode, String reason) {
	}

}
