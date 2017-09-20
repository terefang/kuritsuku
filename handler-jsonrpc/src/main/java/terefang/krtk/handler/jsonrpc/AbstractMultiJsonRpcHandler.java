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
package terefang.krtk.handler.jsonrpc;

import com.googlecode.jsonrpc4j.JsonRpcBasicServer;

import terefang.krtk.*;
import terefang.krtk.util.ContextUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fredo on 02.05.17.
 */
public abstract class AbstractMultiJsonRpcHandler implements RequestHandler
{
	Map<String, JsonRpcBasicServer> jsonrpcServer = new HashMap();
	Map<String, Object> jsonrpcObject = new HashMap();
	
	
	public boolean findAndInvoke(RequestContext ctx, String path) throws Exception
	{
		if(this.jsonrpcServer.containsKey(path))
		{
			Object jo = this.jsonrpcObject.get(path);
			synchronized(jo)
			{
				ContextUtil.injectAny(ctx, jo);
				ctx.setResponseBodyContentType("application/json");
				ctx.setResponseStatus(KrtkUtil.STATUS_OK, "OK");
				this.jsonrpcServer.get(path).handle(ctx.getBodyInputStream(), ctx.getResponseBodyOutputStream());
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
	
	public void registerJsonRpc(String path, Object object, Class<?> handlerInterface)
	{
		this.jsonrpcObject.put(path, object);
		this.jsonrpcServer.put(path, new JsonRpcBasicServer(object, handlerInterface));
	}
}
