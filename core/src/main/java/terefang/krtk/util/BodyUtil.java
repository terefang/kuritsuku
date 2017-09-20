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
import terefang.krtk.RequestBodyPart;
import terefang.krtk.annotation.BindBody;
import terefang.krtk.annotation.BindPart;
import terefang.krtk.io.IOUtils;
import terefang.krtk.provider.RequestBodyProvider;

import java.io.InputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by fredo on 15.04.17.
 */
public class BodyUtil
{
	public static void injectBody(RequestBodyProvider provider, Object object)
	{
		Class<?> clazz = object.getClass();
		for(Field field : clazz.getFields())
		{
			try
			{
				Object param = resolveBody(provider, field, null, true);
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
	
	public static void injectBody(RequestBodyProvider provider, Parameter[] parameters, Object[] values)
	{
		for(int i = 0; i < values.length; i++)
		{
			try
			{
				Object param = resolveBody(provider, null, parameters[i], true);
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
	
	public static Object resolveBody(RequestBodyProvider provider, Field field, Parameter param)
	{
		return resolveBody(provider, field, param, false);
	}
	
	public static Object resolveBody(RequestBodyProvider provider, Field field, Parameter param, boolean throwIfNotApplicable)
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
		
		return resolveBody(provider, type, annos, throwIfNotApplicable);
	}
	
	public static Object resolveBody(RequestBodyProvider provider, Class<?> type, Annotation[] annos)
	{
		return resolveBody(provider, type, annos, false);
	}
	
	public static Object resolveBody(RequestBodyProvider provider, Class<?> type, Annotation[] annos, boolean throwIfNotApplicable)
	{
		boolean matched = false;
		try
		{
			for(Annotation anno : annos)
			{
				if(anno instanceof BindBody)
				{
					matched = true;
					
					if(type.isAssignableFrom(InputStream.class))
					{
						return provider.getBodyInputStream();
					}
					else
					if(type.isAssignableFrom(Reader.class))
					{
						String charset = provider.getBodyCharset();
						if(charset==null)
						{
							charset = ((BindBody)anno).value();
						}
						if(charset==null)
						{
							charset = Charset.defaultCharset().name();
						}
						return provider.getBodyReader(Charset.forName(charset));
					}
					else
					if(type.isAssignableFrom(String.class))
					{
						String charset = provider.getBodyCharset();
						if(charset==null)
						{
							charset = ((BindBody)anno).value();
						}
						if(charset==null)
						{
							charset = Charset.defaultCharset().name();
						}
						return provider.getBodyString(Charset.forName(charset));
					}
					else
					if(type == byte[].class)
					{
						return provider.getBodyBytes();
					}
					return null;
				}
				else
				if("multipart/form-data".equalsIgnoreCase(provider.getBodyContentType()) && (anno instanceof BindPart))
				{
					matched = true;
					
					String pname = ((BindPart)anno).value();
					
					if("**".equals(pname) && type.isAssignableFrom(Map.class))
					{
						Map<String,RequestBodyPart> ret = new HashMap();
						provider.getBodyParts().forEach((part) -> {
							ret.put(part.getName(), part);
						});
						return ret;
					}
					else
					if("*".equals(pname) && type.isAssignableFrom(Collection.class))
					{
						return provider.getBodyParts();
					}
					else
					if(type.isAssignableFrom(String.class))
					{
						RequestBodyPart part = provider.getBodyPart(pname);
						return IOUtils.toString(part.getInputStream(), part.getCharset());
					}
					else
					if(type.isAssignableFrom(InputStream.class))
					{
						RequestBodyPart part = provider.getBodyPart(pname);
						return part.getInputStream();
					}
					else
					if(type.isAssignableFrom(Reader.class))
					{
						RequestBodyPart part = provider.getBodyPart(pname);
						String charset = part.getCharset();
						if(charset==null)
						{
							charset = provider.getBodyCharset();
						}
						if(charset==null)
						{
							charset = ((BindBody)anno).value();
						}
						if(charset==null)
						{
							charset = Charset.defaultCharset().name();
						}
						return provider.getBodyReader(Charset.forName(charset));
					}
					else
					if(type == byte[].class)
					{
						RequestBodyPart part = provider.getBodyPart(pname);
						return part.getBytes();
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