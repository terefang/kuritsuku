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
package terefang.krtk.undertow;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import terefang.krtk.KrtkUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

/**
 * Created by fredo on 30.04.17.
 */
public class KrtkUtServer implements HttpHandler
{
	public static final Log LOG = LogFactory.getLog("KrtkUtServer");
	Properties properties = new Properties();
	
	public static final String P_BIND_ADDRESS = KrtkUtServer.class.getSimpleName()+".BIND_ADDRESS";
	public static final String P_BIND_PORT = KrtkUtServer.class.getSimpleName()+".BIND_PORT";
	
	
	private KrtkUtHandler httpHandler;
	private Undertow httpServer;
	
	public KrtkUtServer setBindAddress(String addr)
	{
		this.properties.setProperty(P_BIND_ADDRESS, addr);
		return this;
	}
	
	public KrtkUtServer setBindPort(int port)
	{
		this.properties.setProperty(P_BIND_PORT, String.valueOf(port));
		return this;
	}
	
	public KrtkUtServer setResourcePath(String path)
	{
		this.properties.setProperty(KrtkUtil.RESOURCE_PATH_KEY, path);
		return this;
	}
	
	public KrtkUtServer setActionSuffix(String suffix)
	{
		this.properties.setProperty(KrtkUtil.ACTION_SUFFIX_KEY, suffix);
		return this;
	}

	public KrtkUtServer setActionPrefix(String prefix)
	{
		this.properties.setProperty(KrtkUtil.ACTION_PREFIX_KEY, prefix);
		return this;
	}
	
	public boolean start()
	{
		try
		{
			this.httpHandler = new KrtkUtHandler();
			this.httpHandler.init(this.properties);
			
			Undertow.Builder sbuilder = Undertow.builder();
			String addr = this.properties.getProperty(P_BIND_ADDRESS, "*").trim();
			int port = Integer.parseInt(this.properties.getProperty(P_BIND_PORT, "9999").trim());
			if("*".equals(addr) || "0.0.0.0".equals(addr))
			{
				sbuilder.addHttpListener(port, "0.0.0.0");
			}
			else
			{
				sbuilder.addHttpListener(port, addr);
			}
			sbuilder.setHandler(this);
			
			this.httpServer = sbuilder.build();
			
			this.httpServer.start();
			return true;
		}
		catch(Exception xe)
		{
			LOG.error(xe.getMessage(), xe);
			return false;
		}
	}
	
	public KrtkUtServer setProperty(String key, String value)
	{
		this.properties.setProperty(key, value);
		return this;
	}
	
	public void stop()
	{
		this.httpServer.stop();
	}
	
	@Override
	public void handleRequest(HttpServerExchange httpExchange) throws Exception
	{
		try
		{
			this.httpHandler.handleRequest(httpExchange);
		}
		catch(Throwable thr)
		{
			httpExchange.setStatusCode(KrtkUtil.STATUS_FORBIDDEN);
		}
	}
}
