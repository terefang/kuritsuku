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

public class GenericMultiJsonRpcHandler extends AbstractMultiJsonRpcHandler
{
	public static GenericMultiJsonRpcHandler create(Object... objects)
	{
		if(objects == null)
		{
			return null;
			//throw new IllegalArgumentException("no args");
		}
		
		if(objects.length == 0)
		{
			return null;
			//throw new IllegalArgumentException("no args");
		}
		
		if((objects.length % 3) != 0)
		{
			return null;
			//throw new IllegalArgumentException("invalid number of args");
		}
		
		GenericMultiJsonRpcHandler genericMultiJsonRpcHandler = new GenericMultiJsonRpcHandler();
		
		for(int i = 0; i < objects.length; i+=3)
		{
			genericMultiJsonRpcHandler.registerJsonRpc((String)objects[i], (Object)objects[i+1], (Class)objects[i+2]);
		}
		
		return genericMultiJsonRpcHandler;
	}
}
