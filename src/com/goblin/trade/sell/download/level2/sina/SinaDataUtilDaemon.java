package com.goblin.trade.sell.download.level2.sina;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.bmtech.utils.Consoler;
import com.bmtech.utils.DaemonLoopThread;
import com.bmtech.utils.Misc;
import com.bmtech.utils.io.ConfigReader;
import com.bmtech.utils.io.FileGet;

public class SinaDataUtilDaemon extends DaemonLoopThread {
	public static SinaDataUtilDaemon daemon = new SinaDataUtilDaemon();
	ConfigReader config = new ConfigReader("config/Goblin.conf", "sellor");

	private SinaDataUtilDaemon() {
		super("cookieDaemon", 1000);
		this.setDaemon(true);

	}

	public String authUrl = "";

	public class ProxyHandler extends AbstractHandler {

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException {
			// System.out.println(target);
			Map<String, Object> ret = new HashMap<>();
			response.setStatus(200);
			response.addHeader("Access-Control-Allow-Origin", "*");

			if (target.equals("/authUrl")) {
				String queryStr = request.getQueryString();
				log.warn("querying authUrl, para is %s", queryStr);
				ServletOutputStream ops = response.getOutputStream();
				ops.write(authUrl.getBytes());
				ops.flush();
			} else if (target.equals("/file")) {
				String queryStr = request.getQueryString();
				log.warn("querying file, para is %s", queryStr);
				if (queryStr == null) {
					response.setStatus(404);
					return;
				}

				File f = new File("config/jetty/webServer/" + queryStr);
				if (!f.exists()) {
					log.warn("querying dir NOT exist file %s", f);
					response.setStatus(404);
					return;
				}
				if (f.isDirectory()) {
					log.warn("querying dir rejected %s", f);
					response.setStatus(403);
					return;
				}
				log.warn("querying dir file %s", f);
				byte[] bs = FileGet.getBytes(f);
				ServletOutputStream ops = response.getOutputStream();
				ops.write(bs);
				ops.flush();

			} else if (target.equals("/putToken")) {
				String userAgent = request.getHeader("User-Agent");
				SinaDataUtils.getInstance().setUserAgent(userAgent);
				String queryStr = request.getQueryString();

				try {
					SinaDataUtils.getInstance().putTicket(URLDecoder.decode(queryStr, "gbk"));
					ServletOutputStream ops = response.getOutputStream();
					ret.put("status", 200);
					ret.put("data", queryStr);
					ops.write(Misc.toJson(ret).getBytes());
					ops.flush();
				} catch (Exception e) {
					ServletOutputStream ops = response.getOutputStream();
					ret.put("status", 200);
					ret.put("data", queryStr);
					ops.write(e.toString().getBytes());
					ops.flush();
				}
			} else {
				response.setStatus(404);
				ServletOutputStream ops = response.getOutputStream();
				ret.put("status", 404);
				ret.put("data", "not found url");
				ops.flush();
			}
		}
	}

	public Server bootServer() throws Exception {

		Server server = null;
		try {
			int port = config.getInt("proxy_port", 34580);
			server = new Server(port);
			Connector[] cc = server.getConnectors();

			for (Connector scc : cc) {
				@SuppressWarnings("resource")
				ServerConnector sc = (ServerConnector) scc;
				sc.setReuseAddress(true);
				sc.setIdleTimeout(config.getInt("proxy_timeout", 10) * 1000);
				sc.setSoLingerTime(config.getInt("proxy_soLingerTime", 10) * 1000);
				sc.setAcceptQueueSize(config.getInt("proxy_backlog", 2));
			}

			ProxyHandler handler = new ProxyHandler();
			server.setHandler(handler);

			server.start();
			log.info("httproxy started at port %s", port);

		} catch (Exception e) {
			if (server != null) {
				server.stop();
			}
			throw e;
		}
		return server;

	}

	@Override
	protected boolean singleRound() throws Exception {

		log.info("starting server");
		Server s = bootServer();
		log.info("server started %s", s);
		int rotateServerIntervalSeconds = config.getInt("rotateServerIntervalSeconds", 5 * 60 * 60);
		Misc.sleep(rotateServerIntervalSeconds * 1000);
		log.info("stoping", s);
		s.stop();
		log.info("server stop %s", s);
		return false;

	}

	public static void main(String[] args) {
		SinaDataUtilDaemon daemon = new SinaDataUtilDaemon();
		daemon.start();
		Consoler.block();
	}
}
