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

/**
 * Created by fredo on 02.05.17.
 */
public abstract class AbstractJsonRpcHandler implements RequestHandler
{
	JsonRpcBasicServer jsonrpcServer = null;
	Object jsonrpcObject = null;
	
	@Override
	public ResponseAction handleRequest()
	{
		RequestContext ctx = KrtkEnv.getContext();
		try
		{
			synchronized(this.jsonrpcObject)
			{
				ContextUtil.injectAny(ctx, this.jsonrpcObject);
				ctx.setResponseBodyContentType("application/json");
				ctx.setResponseStatus(KrtkUtil.STATUS_OK, "OK");
				this.jsonrpcServer.handle(ctx.getBodyInputStream(), ctx.getResponseBodyOutputStream());
			}
		}
		catch(Exception xe)
		{
			KrtkEnv.getContext().setResponseStatus(KrtkUtil.STATUS_INTERNAL_SERVER_ERROR, "OK");
		}
		return null;
	}
	
	public void registerJsonRpc(Object object, Class<?> handlerInterface)
	{
		this.jsonrpcObject = object;
		this.jsonrpcServer = new JsonRpcBasicServer(object, handlerInterface);
	}
}
