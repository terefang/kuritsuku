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

/**
 * Created by fredo on 02.05.17.
 */
public abstract class AbstractXmlRpcHandler implements RequestHandler
{
	XmlRpcServer rpcServer = null;
	Object rpcObject = null;
	
	@Override
	public ResponseAction handleRequest()
	{
		RequestContext ctx = KrtkEnv.getContext();
		try
		{
			synchronized(this.rpcObject)
			{
				ContextUtil.injectAny(ctx, this.rpcObject);
				ctx.setResponseBodyContentType("text/xml");
				ctx.setResponseStatus(KrtkUtil.STATUS_OK, "OK");
				this.rpcServer.execute(ctx.getBodyInputStream(), new OutputStreamWriter(ctx.getResponseBodyOutputStream()));
			}
		}
		catch(Exception xe)
		{
			KrtkEnv.getContext().setResponseStatus(KrtkUtil.STATUS_INTERNAL_SERVER_ERROR, "OK");
		}
		return null;
	}
	
	public void registerXmlRpc(Object object, Class<?> handlerInterface)
	{
		Method[] methods = handlerInterface.getMethods();
		String[] methodNames = new String[methods.length];
		
		for(int i=0; i<methods.length; i++)
		{
			methodNames[i] = methods[i].getName();
		}
		
		this.registerXmlRpc(object, handlerInterface.getSimpleName(), methodNames);
	}

	public void registerXmlRpc(Object object, String clazzName, String[] methodNames)
	{
		this.rpcObject = object;
		this.rpcServer = new XmlRpcServer();
		
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
