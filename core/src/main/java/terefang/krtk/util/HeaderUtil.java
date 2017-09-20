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
package terefang.krtk.util;

import terefang.krtk.KrtkEnv;
import terefang.krtk.RequestContext;
import terefang.krtk.annotation.BindHeader;
import terefang.krtk.provider.RequestHeaderProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Created by fredo on 15.04.17.
 */
public class HeaderUtil
{
	public static Object resolveHeader(RequestHeaderProvider provider, Field field, Parameter param)
	{
		return resolveHeader(provider, field, param, false);
	}
	
	public static Object resolveHeader(RequestHeaderProvider provider, Field field, Parameter param, boolean throwIfNotApplicable)
	{
		Class<?> type = null;
		Annotation[] annos = null;
		
		if(field != null)
		{
			type = field.getType();
			annos = field.getAnnotations();
		} else if(param != null)
		{
			type = param.getType();
			annos = param.getAnnotations();
		}
		
		return resolveHeader(provider, type, annos, throwIfNotApplicable);
	}
	
	public static Object resolveHeader(RequestHeaderProvider provider, Class<?> type, Annotation[] annos, boolean throwIfNotApplicable)
	{
		boolean matched = false;
		try
		{
			for(Annotation anno : annos)
			{
				if(anno instanceof BindHeader)
				{
					matched = true;
					String pname = ((BindHeader)anno).value();
					if("**".equals(pname) && type.isAssignableFrom(Map.class))
					{
						return provider.getRequestHeaderMap();
					}
					else
					if("*".equals(pname) && type.isAssignableFrom(Map.class))
					{
						Map<String, String> ret = new HashMap();
						provider.getRequestHeaderNames().forEach( (key) ->
						{
							ret.put(key, provider.getRequestHeader(key));
						});
						return ret;
					}
					else
					if(pname!=null && type.isAssignableFrom(String.class))
					{
						return provider.getRequestHeader(pname);
					}
					
					return null;
				}
			}
		}
		catch(Throwable t)
		{
			//TODO
		}
		
		if((!matched) && throwIfNotApplicable)
		{
			throw new IllegalArgumentException("no annotation found");
		}
		
		return null;
	}
	
	public static Object resolveHeader(RequestHeaderProvider provider, Class<?> type, Annotation[] annos)
	{
		return resolveHeader(provider, type, annos, false);
	}
	
	public static void injectHeaders(RequestHeaderProvider provider, Object object)
	{
		Class<?> clazz = object.getClass();
		for(Field field : clazz.getFields())
		{
			try
			{
				Object param = resolveHeader(provider, field, null, true);
				if(param!=null)
				{
					field.set(object, param);
				}
			}
			catch(IllegalArgumentException iae)
			{
				// set if applicable else ignore
			}
			catch(Throwable t)
			{
				KrtkEnv.getContext().log(t);
			}
		}
	}
	
	public static void injectHeaders(RequestHeaderProvider provider, Parameter[] parameters, Object[] values)
	{
		for(int i = 0; i < values.length; i++)
		{
			try
			{
				Object param = resolveHeader(provider, null, parameters[i], true);
				values[i] = param;
			}
			catch(IllegalArgumentException iae)
			{
				// set if applicable else ignore
			}
			catch(Throwable t)
			{
				KrtkEnv.getContext().log(t);
			}
		}
	}
	
}