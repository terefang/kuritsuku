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
package terefang.krtk;

/**
 * Created by fredo on 15.04.17.
 */
public class KrtkEnv
{
	static ThreadLocal<RequestContext> requestContext = new ThreadLocal<RequestContext>();
	static String actionSuffix = ".do";
	static String templateSuffix = ".thtml";
	private static String actionPrefix;
	
	public static RequestContext getContext()
	{
		return requestContext.get();
	}
	
	public static void setContext(RequestContext requestContext)
	{
		if(requestContext==null)
		{
			KrtkEnv.requestContext.remove();
		}
		else
		{
			KrtkEnv.requestContext.set(requestContext);
		}
	}
	
	public static String getActionSuffix()
	{
		return actionSuffix;
	}
	
	public static void setActionSuffix(String actionSuffix)
	{
		KrtkEnv.actionSuffix = actionSuffix;
	}
	
	public static String getTemplateSuffix()
	{
		return templateSuffix;
	}
	
	public static void setTemplateSuffix(String templateSuffix)
	{
		KrtkEnv.templateSuffix = templateSuffix;
	}
	
	public static void setActionPrefix(String actionPrefix)
	{
		KrtkEnv.actionPrefix = actionPrefix;
	}
	
	public static String getActionPrefix()
	{
		return actionPrefix;
	}
}
