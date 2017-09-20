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
package terefang.krtk.simpleframework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.*;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import terefang.krtk.KrtkUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Properties;

/**
 * Created by fredo on 30.04.17.
 */
public class KrtkSfServer implements Container
{
	public static final Log LOG = LogFactory.getLog("KrtkSfServer");
	Properties properties = new Properties();
	
	public static final String P_BIND_ADDRESS = KrtkSfServer.class.getSimpleName()+".BIND_ADDRESS";
	public static final String P_BIND_PORT = KrtkSfServer.class.getSimpleName()+".BIND_PORT";
	
	
	private KrtkSfHandler httpHandler;
	private ContainerSocketProcessor httpServer;
	private SocketConnection httpConnection;
	
	public KrtkSfServer setBindAddress(String addr)
	{
		this.properties.setProperty(P_BIND_ADDRESS, addr);
		return this;
	}
	
	public KrtkSfServer setBindPort(int port)
	{
		this.properties.setProperty(P_BIND_PORT, String.valueOf(port));
		return this;
	}
	
	public KrtkSfServer setResourcePath(String path)
	{
		this.properties.setProperty(KrtkUtil.RESOURCE_PATH_KEY, path);
		return this;
	}
	
	public KrtkSfServer setActionSuffix(String suffix)
	{
		this.properties.setProperty(KrtkUtil.ACTION_SUFFIX_KEY, suffix);
		return this;
	}

	public KrtkSfServer setActionPrefix(String prefix)
	{
		this.properties.setProperty(KrtkUtil.ACTION_PREFIX_KEY, prefix);
		return this;
	}
	
	public boolean start()
	{
		try
		{
			this.httpHandler = new KrtkSfHandler();
			this.httpHandler.init(this.properties);
			
			this.httpServer = new ContainerSocketProcessor(this);
			this.httpConnection = new SocketConnection(this.httpServer);
			SocketAddress address = null;
			
			String addr = this.properties.getProperty(P_BIND_ADDRESS, "*").trim();
			int port = Integer.parseInt(this.properties.getProperty(P_BIND_PORT, "9999").trim());
			if("*".equals(addr) || "0.0.0.0".equals(addr))
			{
				address = new InetSocketAddress(port);
			}
			else
			{
				address = new InetSocketAddress(addr, port);
			}
			
			this.httpConnection.connect(address);
			
			return true;
		}
		catch(Exception xe)
		{
			LOG.error(xe.getMessage(), xe);
			return false;
		}
	}
	
	public KrtkSfServer setProperty(String key, String value)
	{
		this.properties.setProperty(key, value);
		return this;
	}
	
	public void stop()
	{
		try
		{
			this.httpConnection.close();
			this.httpServer.stop();
		}
		catch(IOException e)
		{
			LOG.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void handle(Request request, Response response)
	{
		try
		{
			this.httpHandler.handle(request, response);;
		}
		catch(Throwable thr)
		{
			response.setCode(KrtkUtil.STATUS_FORBIDDEN);
		}
		try
		{
			response.close();
		}
		catch(IOException e)
		{
			LOG.error(e.getMessage(), e);
		}
	}
}
