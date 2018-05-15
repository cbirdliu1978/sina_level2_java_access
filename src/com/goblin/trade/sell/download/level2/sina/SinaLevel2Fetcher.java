package com.goblin.trade.sell.download.level2.sina;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import com.bmtech.utils.log.LogHelper;
import com.goblin.trade.sell.download.level2.sina.vo.AuthInfo;

@WebSocket(maxTextMessageSize = 4 * 1024 * 1024)
public class SinaLevel2Fetcher {

	private List<String> symbols = new ArrayList<>();

	private WebSocketClient client = null;
	private SinaLevel2DataHandler handler;
	private LogHelper log = new LogHelper(this.getClass().getSimpleName());
	private boolean isConnected = false;
	private long lastHeartbeat = 0;
	private AuthInfo status;
	private int heartBeatItv = 55000;

	public boolean isConnected() {
		return isConnected;
	}

	public SinaLevel2Fetcher(List<String> symbols, SinaLevel2DataHandler handler) throws IOException {
		this.symbols.addAll(symbols);

		this.handler = handler;

	}

	private void doHeartBeat(String token) throws Exception {
		try {
			log.warn("heartbeat using ticket '%s'", token);
			this.send(token);
			this.lastHeartbeat = System.currentTimeMillis();
		} catch (Exception e) {
			this.stopWebSocket("heartBeatFail");
			throw e;
		}

	}

	public void feedAuthInfo(AuthInfo status) throws Exception {
		if (status == null) {
			log.error("got null status when getAuthToken");
			return;
		}
		if (status == this.status) {
			if (System.currentTimeMillis() - this.lastHeartbeat >= this.heartBeatItv && session != null
					&& session.isOpen()) {
				this.doHeartBeat("");
			}
			return;
		}

		log.warn("new ticket got %s", status);
		if (status.getMsg_code() != 1) {
			throw new Exception("AuthFailed stop WS, ret is " + status);
		}
		if (client == null) {
			client = startWebSocket(status.getToken());
		} else {
			doHeartBeat(status.getToken());
		}
		this.status = status;
	}

	private void send(String authen_code) {

		String msg;
		if (authen_code.length() > 0) {
			msg = "*" + authen_code;
		} else {
			msg = "";
		}
		sendMsg(msg);
	}

	private Session session;

	public Future<Void> sendMsg(String msg) {
		return session.getRemote().sendStringByFuture(msg);
	}

	public void stopWebSocket(String closer) {
		closeInner(closer);
	}

	private synchronized void closeInner(String why) {
		log.fatal("closing webSocket, beacouse %s", why);
		try {
			if (client != null) {
				if (!client.isStopped() && client.isStarted()) {
					client.stop();
				}
			}
		} catch (Exception e) {
			log.error(e, "when close client");
		} finally {
			client = null;
		}
		log.fatal("webSocket close");
	}

	private WebSocketClient startWebSocket(String token) {
		WebSocketClient client = null;
		try {
			String list = SinaDataUtils.makeLevel2QueryString(symbols);
			String url = "ws://ff.sinajs.cn/wskt?token=" + token + "&list=" + list;
			URI uri = new URI(url);
			client = new WebSocketClient();
			HttpClient hc = client.getHttpClient();

			HttpField ua = new HttpField(HttpHeader.USER_AGENT, SinaDataUtils.getInstance().getUserAgent());
			hc.setUserAgentField(ua);

			client.start();
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			client.connect(this, uri, request);

		} catch (Exception e) {
			if (client != null) {
				try {
					client.stop();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				client = null;
			}
		}

		return client;
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		isConnected = false;
		log.fatal("onClose called, status %s, reason %s", statusCode, reason);
		try {
			this.closeInner("onClose:" + reason);
		} finally {
			handler.onClose(statusCode, reason);
		}
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.isConnected = true;
		this.session = session;
		this.handler.onConnect();
	}

	@OnWebSocketMessage
	public void onMessage(String msg) {
		handler.onMessage(msg);
	}

	public void cleanUp() {
		this.onClose(100, "clean up");
	}

}
