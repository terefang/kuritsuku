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
package terefang.krtk.test;

import terefang.krtk.RequestContext;
import terefang.krtk.RequestHandler;
import terefang.krtk.ResponseAction;
import terefang.krtk.annotation.BindContext;
import terefang.krtk.annotation.PathPrefix;
import terefang.krtk.handler.jsonrpc.AbstractMultiJsonRpcHandler;
import terefang.krtk.handler.jsonrpc.GenericMultiJsonRpcHandler;

/**
 * Created by fredo on 02.05.17.
 */
@PathPrefix("/jsonrpc3/")
public class TestMultiJsonRpcGeneric implements TestJsonRpcInterface, RequestHandler
{
	GenericMultiJsonRpcHandler jh = GenericMultiJsonRpcHandler.create(
			"/jsonrpc3/left", this, TestJsonRpcInterface.class,
			"/jsonrpc3/right", this, TestJsonRpcInterface.class);
	
	@BindContext
	public RequestContext context = null;
	
	public TestMultiJsonRpcGeneric()
	{
		super();
	}
	
	@Override
	public String test(String val)
	{
		return "test: "+val+" "+context.getPath();
	}
	
	@Override
	public ResponseAction handleRequest()
	{
		return jh.handleRequest();
	}
}
