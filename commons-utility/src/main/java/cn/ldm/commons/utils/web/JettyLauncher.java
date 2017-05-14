package cn.ldm.commons.utils.web;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.util.ResourceUtils;

public class JettyLauncher {

	public static Server createServer(int port, String webappPath, String ctxPath) {
		Server server = new Server();// 默认8-200线程
		server.setStopAtShutdown(true);

		ServerConnector connector = new ServerConnector(server);// nio
		connector.setPort(port);
		connector.setReuseAddress(false);
		connector.setAcceptQueueSize(8192);

		server.addConnector(connector);

		WebAppContext webContext = new WebAppContext(webappPath, ctxPath);
		webContext.setDescriptor(webappPath + "/WEB-INF/web.xml");
		webContext.setResourceBase(webappPath);
		webContext.setClassLoader(Thread.currentThread().getContextClassLoader());

		// 禁用文件缓存？
		webContext.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
		webContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

		server.setHandler(webContext);
		return server;
	}

	public static void main(String[] args) throws IOException {
		// Server server = ServerInstance.getServer();

		int port = Integer.parseInt(System.getProperty("jetty.port", "8080"));
		String ctxPath = System.getProperty("jetty.ctx.path", "/");
		String appPath = ResourceUtils.getFile("classpath:webapp").getAbsolutePath().replace("\\", "/");
		Server server = createServer(port, appPath, ctxPath);
		try {
			// server.stop();
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}