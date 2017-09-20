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
import terefang.krtk.annotation.BindParam;
import terefang.krtk.provider.RequestParamProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Created by fredo on 15.04.17.
 */
public class ParamUtil
{
	public static Object resolveParam(RequestParamProvider provider, Field field, Parameter param)
	{
		return resolveParam(provider, field, param, false);
	}
	
	public static Object resolveParam(RequestParamProvider provider, Field field, Parameter param, boolean throwIfNotApplicable)
	{
		Class<?> type = null;
		Annotation[] annos = null;
		
		if(field!=null)
		{
			type = field.getType();
			annos = field.getAnnotations();
		}
		else
		if(param!=null)
		{
			type = param.getType();
			annos = param.getAnnotations();
		}
		
		return resolveParam(provider, type, annos, throwIfNotApplicable);
	}
	
	public static Object resolveParam(RequestParamProvider provider, Class<?> type, Annotation[] annos)
	{
		return resolveParam(provider, type, annos, false);
	}
	
	public static Object resolveParam(RequestParamProvider provider, Class<?> type, Annotation[] annos, boolean throwIfNotApplicable)
	{
		boolean matched = false;
		try
		{
			for(Annotation anno : annos)
			{
				if(anno instanceof BindParam)
				{
					matched = true;
					
					String pname = ((BindParam)anno).value();
					
					if(type.isAssignableFrom(String.class))
					{
						return provider.getRequestParam(pname);
					}
					else
					if(type.isAssignableFrom(Integer.class))
					{
						return Integer.parseInt(provider.getRequestParam(pname));
					}
					else
					if(type.isAssignableFrom(Long.class))
					{
						return Long.parseLong(provider.getRequestParam(pname));
					}
					if(type.isAssignableFrom(Collection.class))
					{
						if(provider.getRequestParam(pname)!=null)
						{
							return provider.getRequestParams(pname);
						}
						else
						if(provider.getRequestParam(pname+"[]")!=null)
						{
							return provider.getRequestParams(pname+"[]");
						}
						else
						{
							return Collections.EMPTY_LIST;
						}
					}
					else
					if("**".equals(pname) && type.isAssignableFrom(Map.class))
					{
						return provider.getRequestParamMap();
					}
					else
					if(pname.endsWith("**") && type.isAssignableFrom(Map.class))
					{
						String xname = pname.substring(0, pname.length()-2);
						Map<String, List<String>> ret = new HashMap();
						provider.getRequestParamMap().forEach((key, value) -> {
							if(key.startsWith(xname))
							{
								ret.put(key, value);
							}
						});
						return ret;
					}
					else
					if("*".equals(pname) && type.isAssignableFrom(Map.class))
					{
						Map<String, String> ret = new HashMap();
						provider.getRequestParamMap().forEach((key, value) -> {
							ret.put(key, value.get(0));
						});
						return ret;
					}
					else
					if(pname.endsWith("*") && type.isAssignableFrom(Map.class))
					{
						String xname = pname.substring(0, pname.length()-1);
						Map<String, String> ret = new HashMap();
						provider.getRequestParamMap().forEach((key, value) -> {
							if(key.startsWith(xname))
							{
								ret.put(key, value.get(0));
							}
						});
						return ret;
					}
					else
					if(pname.endsWith("{}") && type.isAssignableFrom(Map.class))
					{
						String xname = pname.substring(0, pname.length()-2);
						Map<String,String> val = new HashMap();
						provider.getRequestParamNames().forEach((key) -> {
							if(key.startsWith(xname+"{") && key.endsWith("}"))
							{
								String hKey = key.substring(xname.length()+1, key.lastIndexOf('}'));
								val.put(hKey, provider.getRequestParam(key));
							}
						});
						return val;
					}
					else
					if(pname.endsWith("[]") && type.isAssignableFrom(Map.class))
					{
						String xname = pname.substring(0, pname.length()-2);
						Map<Integer,String> val = new HashMap();
						provider.getRequestParamNames().forEach((key) -> {
							if(key.startsWith(xname+"[") && key.endsWith("]"))
							{
								Integer hKey = Integer.parseInt(key.substring(xname.length()+1, key.lastIndexOf(']')));
								val.put(hKey, provider.getRequestParam(key));
							}
						});
						return val;
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
	
	
	public static void injectParams(RequestParamProvider provider, Object object)
	{
		Class<?> clazz = object.getClass();
		for(Field field : clazz.getFields())
		{
			try
			{
				Object param = resolveParam(provider, field, null, true);
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
	
	public static void injectParams(RequestParamProvider provider, Parameter[] parameters, Object[] values)
	{
		for(int i = 0; i < values.length; i++)
		{
			try
			{
				Object param = resolveParam(provider, null, parameters[i], true);
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
