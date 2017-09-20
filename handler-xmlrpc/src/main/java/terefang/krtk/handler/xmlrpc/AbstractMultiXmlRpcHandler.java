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
package terefang.krtk.handler.xmlrpc;

import redstone.xmlrpc.XmlRpcServer;
import redstone.xmlrpc.handlers.ReflectiveInvocationHandler;
import terefang.krtk.*;
import terefang.krtk.util.ContextUtil;

import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fredo on 02.05.17.
 */
public abstract class AbstractMultiXmlRpcHandler implements RequestHandler
{
	XmlRpcServer rpcServer = null;
	Map<String, Object> rpcObject = new HashMap();
	
	
	public boolean findAndInvoke(RequestContext ctx, String path) throws Exception
	{
		if(this.rpcObject.containsKey(path))
		{
			Object jo = this.rpcObject.get(path);
			synchronized(jo)
			{
				ContextUtil.injectAny(ctx, jo);
				ctx.setResponseBodyContentType("text/xml");
				ctx.setResponseStatus(KrtkUtil.STATUS_OK, "OK");
				this.rpcServer.execute(ctx.getBodyInputStream(), new OutputStreamWriter(ctx.getResponseBodyOutputStream()));
				return true;
			}
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public ResponseAction handleRequest()
	{
		boolean retryPath = true;
		
		RequestContext ctx = KrtkEnv.getContext();
		try
		{
			String path = ctx.getPath();
			
			path = KrtkUtil.normalizePath(path);
			
			while(retryPath)
			{
				retryPath = false;

				path = KrtkUtil.normalizePath(path);
				
				if(this.findAndInvoke(ctx, path+"/*"))
				{
					return null;
				}
				
				if(this.findAndInvoke(ctx, path))
				{
					return null;
				}

				int offset = 0;
				if((offset = path.lastIndexOf('/')) > 0)
				{
					path = path.substring(0, offset);
					retryPath = true;
				}
			}
			ctx.setResponseStatus(KrtkUtil.STATUS_NOT_FOUND, "OK");
		}
		catch(Exception xe)
		{
			KrtkEnv.getContext().setResponseStatus(KrtkUtil.STATUS_INTERNAL_SERVER_ERROR, "OK");
		}
		return null;
	}
	
	public void registerXmlRpc(String path, Object object, Class<?> handlerInterface)
	{
		Method[] methods = handlerInterface.getMethods();
		String[] methodNames = new String[methods.length];
		
		for(int i=0; i<methods.length; i++)
		{
			methodNames[i] = methods[i].getName();
		}
		
		this.registerXmlRpc(path, object, handlerInterface.getSimpleName(), methodNames);
	}
	
	public void registerXmlRpc(String path, Object object, String clazzName, String[] methodNames)
	{
		if(this.rpcServer == null)
		{
			this.rpcServer = new XmlRpcServer();
		}

		this.rpcObject.put(path, object);
		
		if(methodNames!=null)
		{
			this.rpcServer.addInvocationHandler(clazzName, new ReflectiveInvocationHandler(object, methodNames));
		}
		else
		{
			this.rpcServer.addInvocationHandler(clazzName, new ReflectiveInvocationHandler(object));
		}
	}
}
