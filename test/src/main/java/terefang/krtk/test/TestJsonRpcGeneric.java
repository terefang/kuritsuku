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

import terefang.krtk.RequestHandler;
import terefang.krtk.ResponseAction;
import terefang.krtk.annotation.Path;
import terefang.krtk.handler.jsonrpc.AbstractJsonRpcHandler;
import terefang.krtk.handler.jsonrpc.GenericJsonRpcHandler;

/**
 * Created by fredo on 02.05.17.
 */
@Path("/jsonrpc/generic")
public class TestJsonRpcGeneric implements TestJsonRpcInterface, RequestHandler
{
	GenericJsonRpcHandler<TestJsonRpcInterface> jh = GenericJsonRpcHandler.create(this, TestJsonRpcInterface.class);
	
	public TestJsonRpcGeneric()
	{
		super();
	}
	
	@Override
	public String test(String val)
	{
		return "test: "+val;
	}
	
	@Override
	public ResponseAction handleRequest()
	{
		return jh.handleRequest();
	}
}