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
import terefang.krtk.annotation.BindContext;
import terefang.krtk.provider.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;


/**
 * Created by fredo on 15.04.17.
 */
public class ContextUtil
{
	public static void injectAny(Object object)
	{
		injectAny(KrtkEnv.getContext(), object);
	}
	
	public static void injectAny(RequestContextProvider provider, Object object)
	{
		RequestContext context = provider.getContext();
		
		ParamUtil.injectParams(context, object);
		
		HeaderUtil.injectHeaders(context, object);
		
		ContextUtil.injectContexts(context, object);
		
		BodyUtil.injectBody(context, object);
	}
	
	public static void injectContexts(RequestContextProvider provider, Object object)
	{
		Class<?> clazz = object.getClass();
		for(Field field : clazz.getFields())
		{
			try
			{
				Object param = resolveContext(provider, field, null, true);
				field.set(object, param);
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
	
	public static void injectContexts(RequestContextProvider provider, Parameter[] parameters, Object[] values)
	{
		for(int i = 0; i < values.length; i++)
		{
			try
			{
				Object param = resolveContext(provider, null, parameters[i], true);
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
	
	public static Object resolveContext(RequestContextProvider provider, Field field, Parameter param)
	{
		return resolveContext(provider, field, param, false);
	}
	
	public static Object resolveContext(RequestContextProvider provider, Field field, Parameter param, boolean throwIfNotApplicable)
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
		
		return resolveContext(provider, type, annos, throwIfNotApplicable);
	}
	
	public static Object resolveContext(RequestContextProvider provider, Class<?> type, Annotation[] annos)
	{
		return resolveContext(provider, type, annos, false);
	}
	
	public static Object resolveContext(RequestContextProvider provider, Class<?> type, Annotation[] annos, boolean throwIfNotApplicable)
	{
		boolean matched = false;
		try
		{
			for(Annotation anno : annos)
			{
				if(anno instanceof BindContext)
				{
					matched = true;
					
					if(type.isAssignableFrom(RequestContext.class))
					{
						return provider.getContext();
					}
					else
					if(type.isAssignableFrom(RequestBodyProvider.class))
					{
						return provider.getContext();
					}
					else
					if(type.isAssignableFrom(RequestHeaderProvider.class))
					{
						return provider.getContext();
					}
					else
					if(type.isAssignableFrom(RequestParamProvider.class))
					{
						return provider.getContext();
					}
					else
					if(type.isAssignableFrom(ResponseBodyProvider.class))
					{
						return provider.getContext();
					}
					else
					if(type.isAssignableFrom(ResponseHeaderProvider.class))
					{
						return provider.getContext();
					}
					else
					if(type.isAssignableFrom(ResponseParamProvider.class))
					{
						return provider.getContext();
					}
					
					return null;
				}
			}
		}
		catch(Throwable t)
		{
			KrtkEnv.getContext().log(t);
		}
		
		if((!matched) && throwIfNotApplicable)
		{
			throw new IllegalArgumentException("no annotation found");
		}
		
		return null;
	}
	
}