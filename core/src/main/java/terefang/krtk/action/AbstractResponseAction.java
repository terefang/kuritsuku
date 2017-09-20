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
package terefang.krtk.action;

import terefang.krtk.ResponseAction;
import terefang.krtk.provider.ActionProvider;
import terefang.krtk.KrtkEnv;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fredo on 11.04.17.
 */
public abstract class AbstractResponseAction implements ResponseAction
{
	Map<String,Object> requestAttributes = new HashMap();
	Map<String,String> responseHeaders = new HashMap();
	
	public void executeAction(ActionProvider provider)
	{
		if(this.requestAttributes!=null && this.requestAttributes.size()>0)
		{
			for(Map.Entry<String, Object> entry : this.requestAttributes.entrySet())
			{
				KrtkEnv.getContext().setAttribute(entry.getKey(), entry.getValue());
			}
		}
		
		if(this.responseHeaders!=null && this.responseHeaders.size()>0)
		{
			for(Map.Entry<String, String> entry : this.responseHeaders.entrySet())
			{
				KrtkEnv.getContext().setResponseHeader(entry.getKey(), entry.getValue());
			}
		}
	}
	
	public Map<String, Object> getRequestAttributes()
	{
		return requestAttributes;
	}
	
	public AbstractResponseAction setRequestAttributes(Map<String, Object> requestAttributes)
	{
		this.requestAttributes = requestAttributes;
		return this;
	}

	public AbstractResponseAction addRequestAttribute(String key, Object val)
	{
		this.requestAttributes.put(key, val);
		return this;
	}
	
	public Map<String, String> getResponseHeaders()
	{
		return responseHeaders;
	}
	
	public AbstractResponseAction setResponseHeaders(Map<String, String> responseHeaders)
	{
		this.responseHeaders = responseHeaders;
		return this;
	}
	
	public AbstractResponseAction addResponseHeader(String key, String val)
	{
		this.responseHeaders.put(key, val);
		return this;
	}
}
