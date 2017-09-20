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
import terefang.krtk.annotation.BindContext;
import terefang.krtk.annotation.Path;
import terefang.krtk.annotation.PathPrefix;
import terefang.krtk.handler.jsonrpc.AbstractJsonRpcHandler;
import terefang.krtk.handler.jsonrpc.AbstractMultiJsonRpcHandler;

/**
 * Created by fredo on 02.05.17.
 */
@PathPrefix("/jsonrpc2/")
public class TestMultiJsonRpc extends AbstractMultiJsonRpcHandler implements TestJsonRpcInterface
{
	@BindContext
	public RequestContext context = null;
	
	public TestMultiJsonRpc()
	{
		super();
		this.registerJsonRpc("/jsonrpc2/left", this, TestJsonRpcInterface.class);
		this.registerJsonRpc("/jsonrpc2/right", this, TestJsonRpcInterface.class);
	}
	
	@Override
	public String test(String val)
	{
		return "test: "+val+" "+context.getPath();
	}
}
