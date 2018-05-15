package com.goblin.trade.sell.download.level2.sina;

import java.io.IOException;
import java.util.List;

import com.bmtech.utils.DaemonLoopThread;
import com.bmtech.utils.Misc;
import com.goblin.trade.sell.download.level2.sina.vo.AuthInfo;

public class WebSocketWatcher extends DaemonLoopThread {
	public static WebSocketWatcher watcher = new WebSocketWatcher();
	private WebSocketFactory fac;

	private SinaLevel2Fetcher fetcher;

	private WebSocketWatcher() {
		super.setCheckItvMs(1000);
		this.start();
	}

	public void setWebSocketFactory(WebSocketFactory fac) {
		this.fac = fac;
	}

	@Override
	protected boolean singleRound() throws IOException {
		// LogHelper.setPrintException(true);
		if (fac == null) {
			log.warn("SinaLevel2Fetcher factory not set yet!");
			return false;
		}
		List<String> codes = this.fac.getWatchCodes();
		SinaDataUtilDaemon.daemon.authUrl = SinaDataUtils.getAuthUrl(codes, fetcher == null);
		AuthInfo aInfo = SinaDataUtils.getInstance().getTicket();
		if (aInfo == null || !aInfo.isValid()) {
			log.warn("ticket is not ready now, AuthInfo = %s ", aInfo);
			Misc.sleep(5000);
			return false;
		}

		try {
			if (this.fetcher == null) {

				if (codes.size() == 0) {
					log.warn("no stock need tobe watched!");
					return false;
				}

				log.warn("starting new fetcher");

				fetcher = this.fac.newFetcher();
				fetcher.feedAuthInfo(aInfo);
				Misc.sleep(5000);
				if (!fetcher.isConnected()) {
					throw new Exception("connect timeout!");
				}

			} else {
				if (!fetcher.isConnected()) {
					log.error("websocket disconnected");
					cleanUp();
				} else {
					log.debug("websocket is alive still");
					fetcher.feedAuthInfo(aInfo);
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
			log.error(e, "when start fetcher ");
			cleanUp();
		}
		return false;
	}

	private void cleanUp() {
		log.warn("cleaning up the consumner and fetcher %s ", this.fetcher);
		if (fetcher != null) {
			fetcher.cleanUp();
			fetcher = null;
		}
	}
}