/*
 * Copyright (c) 2017. terefang@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package terefang.krtk.jdkhttp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import terefang.krtk.KrtkUtil;
import terefang.krtk.io.IOUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Properties;

/**
 * Created by fredo on 30.04.17.
 */
public class KrtkJdkHttpServer implements HttpHandler
{
	public static final Log LOG = LogFactory.getLog("KrtkJdkHttpServer");
	Properties properties = new Properties();
	
	public static final String P_BIND_ADDRESS = KrtkJdkHttpServer.class.getSimpleName()+".BIND_ADDRESS";
	public static final String P_BIND_PORT = KrtkJdkHttpServer.class.getSimpleName()+".BIND_PORT";
	
	private HttpServer httpServer;
	private KrtkJdkHttpHandler httpHandler;
	
	public KrtkJdkHttpServer setBindAddress(String addr)
	{
		this.properties.setProperty(P_BIND_ADDRESS, addr);
		return this;
	}
	
	public KrtkJdkHttpServer setBindPort(int port)
	{
		this.properties.setProperty(P_BIND_PORT, String.valueOf(port));
		return this;
	}
	
	public KrtkJdkHttpServer setResourcePath(String path)
	{
		this.properties.setProperty(KrtkUtil.RESOURCE_PATH_KEY, path);
		return this;
	}
	
	public KrtkJdkHttpServer setActionSuffix(String suffix)
	{
		this.properties.setProperty(KrtkUtil.ACTION_SUFFIX_KEY, suffix);
		return this;
	}

	public KrtkJdkHttpServer setActionPrefix(String prefix)
	{
		this.properties.setProperty(KrtkUtil.ACTION_PREFIX_KEY, prefix);
		return this;
	}
	
	public boolean start()
	{
		try
		{
			this.httpHandler = new KrtkJdkHttpHandler();
			this.httpHandler.init(this.properties);
			
			this.httpServer =  HttpServer.create();
			this.httpServer.createContext("/", this);
			
			String addr = this.properties.getProperty(P_BIND_ADDRESS, "*").trim();
			int port = Integer.parseInt(this.properties.getProperty(P_BIND_PORT, "9999").trim());
			if("*".equals(addr) || "0.0.0.0".equals(addr))
			{
				this.httpServer.bind(new InetSocketAddress(port), 20);
			}
			else
			{
				this.httpServer.bind(new InetSocketAddress(addr, port), 20);
			}
			
			this.httpServer.start();
			return true;
		}
		catch(Exception xe)
		{
			LOG.error(xe.getMessage(), xe);
			return false;
		}
	}
	
	public KrtkJdkHttpServer setProperty(String key, String value)
	{
		this.properties.setProperty(key, value);
		return this;
	}
	
	public void stop()
	{
		this.httpServer.stop(1);
	}
	
	@Override
	public void handle(HttpExchange httpExchange) throws IOException
	{
		try
		{
			this.httpHandler.handle(httpExchange);
		}
		catch(Throwable thr)
		{
			httpExchange.sendResponseHeaders(KrtkUtil.STATUS_FORBIDDEN, -1);
		}
	}
}
