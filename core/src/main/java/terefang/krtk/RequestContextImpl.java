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

import terefang.krtk.action.TemplateAction;
import terefang.krtk.annotation.*;
import terefang.krtk.provider.*;
import terefang.krtk.util.BodyUtil;
import terefang.krtk.util.ContextUtil;
import terefang.krtk.util.HeaderUtil;
import terefang.krtk.util.ParamUtil;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fredo on 15.04.17.
 */
public class RequestContextImpl implements RequestContext
{
	public RequestBodyProvider requestBodyProvider;
	public RequestHeaderProvider requestHeaderProvider;
	public RequestParamProvider requestParamProvider;
	
	public ResponseBodyProvider responseBodyProvider;
	public ResponseHeaderProvider responseHeaderProvider;
	public ResponseParamProvider responseParamProvider;
	
	public RequestContextProvider requestContextProvider;
	
	public ActionProvider actionProvider;
	
	public List<PageProvider> pageProvider;
	
	public List<TemplateProvider> templateProvider;
	
	public LoggingProvider loggingProvider;
	
	private long timeStamp = System.currentTimeMillis();
	
	public static RequestContext create(RequestBodyProvider qBP, RequestHeaderProvider qHP, RequestParamProvider qPP, ResponseBodyProvider sBP, ResponseHeaderProvider sHP, ResponseParamProvider sPP, RequestContextProvider cP, ActionProvider aP, List<PageProvider> pageProvider, List<TemplateProvider> templateProvider, LoggingProvider loggingProvider)
	{
		RequestContextImpl rci = new RequestContextImpl();
		
		rci.requestBodyProvider = qBP;
		rci.responseBodyProvider = sBP;

		rci.requestHeaderProvider = qHP;
		rci.responseHeaderProvider = sHP;

		rci.requestParamProvider = qPP;
		rci.responseParamProvider = sPP;
		
		rci.requestContextProvider = cP;
		rci.actionProvider = aP;
		
		rci.pageProvider = pageProvider;

		rci.templateProvider = templateProvider;

		rci.loggingProvider = loggingProvider;
		
		return rci;
	}
	
	@Override
	public void log(String message)
	{
		this.loggingProvider.log(message);
	}
	
	@Override
	public void log(String message, Throwable th)
	{
		this.loggingProvider.log(message, th);
	}
	
	@Override
	public void log(Throwable th)
	{
		this.loggingProvider.log(th);
	}

	@Override
	public void setResponseParam(String key, Object value)
	{
		this.responseParamProvider.setResponseParam(key, value);
	}
	
	@Override
	public void setResponseHeader(String key, String value)
	{
		this.responseHeaderProvider.setResponseHeader(key, value);
	}
	
	@Override
	public void setResponseStatus(int status, String value)
	{
		this.responseHeaderProvider.setResponseStatus(status, value);
	}

	@Override
	public String getRequestHeader(String headerName)
	{
		return this.requestHeaderProvider.getRequestHeader(headerName);
	}
	
	@Override
	public void setResponseBodyContentType(String type)
	{
		this.responseBodyProvider.setResponseBodyContentType(type);
	}
	
	@Override
	public Map<String, Object> getContextAttributes()
	{
		return this.requestContextProvider.getContextAttributes();
	}
	
	@Override
	public List<String> getRequestHeaders(String headerName)
	{
		return this.requestHeaderProvider.getRequestHeaders(headerName);
	}
	
	@Override
	public void setResponseBodyCharset(String charset)
	{
		this.responseBodyProvider.setResponseBodyCharset(charset);
	}
	
	@Override
	public Object getContextAttribute(String attributeName)
	{
		return this.requestContextProvider.getContextAttribute(attributeName);
	}
	
	@Override
	public String getResponseBodyCharset()
	{
		return this.responseBodyProvider.getResponseBodyCharset();
	}
	
	@Override
	public List<String> getRequestHeaderNames()
	{
		return this.requestHeaderProvider.getRequestHeaderNames();
	}
	
	@Override
	public void setContextAttribute(String attributeName, Object value)
	{
		this.requestContextProvider.setContextAttribute(attributeName, value);
	}
	
	@Override
	public void setResponseBodyLength(long len)
	{
		this.responseBodyProvider.setResponseBodyLength(len);
	}
	
	@Override
	public String getRequestParam(String headerName)
	{
		return this.requestParamProvider.getRequestParam(headerName);
	}
	
	@Override
	public Map<String, List<String>> getRequestHeaderMap()
	{
		return this.requestHeaderProvider.getRequestHeaderMap();
	}
	
	@Override
	public Map<String, Object> getAttributes()
	{
		return this.requestContextProvider.getAttributes();
	}
	
	@Override
	public OutputStream getResponseBodyOutputStream()
	{
		return this.responseBodyProvider.getResponseBodyOutputStream();
	}
	
	@Override
	public List<String> getRequestParams(String headerName)
	{
		return this.requestParamProvider.getRequestParams(headerName);
	}
	
	@Override
	public byte[] getBodyBytes()
	{
		return this.requestBodyProvider.getBodyBytes();
	}
	
	@Override
	public Object getAttribute(String attributeName)
	{
		return this.requestContextProvider.getAttribute(attributeName);
	}
	
	@Override
	public List<String> getRequestParamNames()
	{
		return this.requestParamProvider.getRequestParamNames();
	}
	
	@Override
	public void setAttribute(String attributeName, Object value)
	{
		this.requestContextProvider.setContextAttribute(attributeName, value);
	}
	
	@Override
	public Map<String, List<String>> getRequestParamMap()
	{
		return this.requestParamProvider.getRequestParamMap();
	}
	
	@Override
	public String getMethod()
	{
		return this.requestContextProvider.getMethod();
	}
	
	@Override
	public String getProtocol()
	{
		return this.requestContextProvider.getProtocol();
	}
	
	@Override
	public String getPath()
	{
		return this.requestContextProvider.getPath();
	}
	
	@Override
	public String getContextPath()
	{
		return this.requestContextProvider.getContextPath();
	}
	
	@Override
	public InputStream getBodyInputStream()
	{
		return this.requestBodyProvider.getBodyInputStream();
	}
	
	@Override
	public RequestContext getContext()
	{
		return this;
	}
	
	@Override
	public URL getResource(String path)
	{
		return this.requestContextProvider.getResource(path);
	}
	
	@Override
	public Reader getResourceReader(String path, Charset charset) throws FileNotFoundException
	{
		return this.requestContextProvider.getResourceReader(path, charset);
	}
	
	@Override
	public InputStream getResourceStream(String path) throws FileNotFoundException
	{
		return this.requestContextProvider.getResourceStream(path);
	}
	
	@Override
	public String getBodyCharset()
	{
		return this.requestBodyProvider.getBodyCharset();
	}
	
	@Override
	public String getBodyContentType()
	{
		return this.requestBodyProvider.getBodyContentType();
	}
	
	@Override
	public List<RequestBodyPart> getBodyParts()
	{
		return this.requestBodyProvider.getBodyParts();
	}
	
	@Override
	public RequestBodyPart getBodyPart(String pname)
	{
		return this.requestBodyProvider.getBodyPart(pname);
	}
	
	@Override
	public void executeAction(ResponseAction action)
	{
		this.actionProvider.executeAction(action);
	}
	
	@Override
	public void doRedirect(String url)
	{
		this.actionProvider.doRedirect(url);
	}
	
	@Override
	public void doForward(String path)
	{
		this.actionProvider.doForward(path);
	}
	
	@Override
	public void doTemplate(String path)
	{
		for(TemplateProvider templatep : this.templateProvider)
		{
			if(templatep.executeTemplate(path, this) == true)
			{
				return;
			}
		}
		this.actionProvider.doTemplate(path);
	}
	
	@Override
	public void doError(int code, String text)
	{
		this.actionProvider.doError(code, text);
	}
	
	static void executeAction(ResponseAction responseAction, Class<?> clazz, Object object, Method method) throws Exception
	{
		if(responseAction!=null)
		{
			retrieveResponseParameter(clazz, object);
			
			if(responseAction instanceof TemplateAction)
			{
				ResponseTemplate dt = method.getAnnotation(ResponseTemplate.class);
				if(dt==null)
				{
					dt = clazz.getAnnotation(ResponseTemplate.class);
				}
				
				if(dt!=null && ((TemplateAction)responseAction).getForwardPath()==null)
				{
					((TemplateAction)responseAction).setForwardPath(dt.value());
				}
			}
			
			resolveRenderableAttributes(KrtkEnv.getContext());
			
			responseAction.executeAction(KrtkEnv.getContext());
		}
	}
	
	static void resolveRenderableAttributes(RequestContext context)
	{
		Map<String, Object> resolved = new HashMap();
		for(Map.Entry<String, Object> attribute : context.getContextAttributes().entrySet())
		{
			if(attribute.getValue() instanceof Renderable)
			{
				StringWriter wr = new StringWriter();
				if(((Renderable)attribute.getValue()).render(context, wr))
				{
					resolved.put(attribute.getKey(), wr.getBuffer().toString());
				}
			}
		}
		
		for(Map.Entry<String, Object> attribute : context.getAttributes().entrySet())
		{
			if(attribute.getValue() instanceof Renderable)
			{
				StringWriter wr = new StringWriter();
				if(((Renderable)attribute.getValue()).render(context, wr))
				{
					resolved.put(attribute.getKey(), wr.getBuffer().toString());
				}
			}
		}
		
		for(Map.Entry<String, Object> attribute : resolved.entrySet())
		{
			context.setAttribute(attribute.getKey(), attribute.getValue());
		}
		resolved.clear();
	}
	
	
	static void retrieveResponseHeaderParam(Class<?> clazz, Object object, Field field)
	{
		try
		{
			ResponseHeaderParam rp = field.getAnnotation(ResponseHeaderParam.class);
			if(rp!=null && "*".equals(rp.value()) && field.getType().isAssignableFrom(Map.class))
			{
				Map<String, String> vars = (Map) field.get(object);
				if(vars!=null)
				{
					for(Map.Entry<String, String> entry : vars.entrySet())
					{
						KrtkEnv.getContext().setResponseHeader(entry.getKey(), entry.getValue());
					}
				}
			}
			else
			if(rp!=null && rp.value()!=null)
			{
				Object robject = field.get(object);
				if(robject!=null)
				{
					KrtkEnv.getContext().setResponseHeader(rp.value(), robject.toString());
				}
			}
		}
		catch(IllegalAccessException e)
		{
			KrtkEnv.getContext().log(e);
		}
	}
	
	static void retrieveResponseParam(Class<?> clazz, Object object, Field field)
	{
		try
		{
			ResponseParam rp = field.getAnnotation(ResponseParam.class);
			if(rp!=null && "*".equals(rp.value()) && field.getType().isAssignableFrom(Map.class))
			{
				Map<String, Object> vars = (Map) field.get(object);
				if(vars!=null)
				{
					for(Map.Entry<String, Object> entry : vars.entrySet())
					{
						KrtkEnv.getContext().setAttribute(entry.getKey(), entry.getValue());
					}
				}
			}
			else
			if(rp!=null && rp.value()!=null)
			{
				Object robject = field.get(object);
				if(object != null)
				{
					KrtkEnv.getContext().setAttribute(rp.value(), robject);
				}
			}
			else
			if(rp!=null)
			{
				Object robject = field.get(object);
				if(object != null)
				{
					KrtkEnv.getContext().setAttribute(field.getName(), robject);
				}
			}
		}
		catch(IllegalAccessException e)
		{
			KrtkEnv.getContext().log(e);
		}
	}
	
	static void retrieveResponseParameter(Class<?> clazz, Object object)
	{
		for(Field field : clazz.getFields())
		{
			retrieveResponseParam(clazz, object, field);
			retrieveResponseHeaderParam(clazz, object, field);
		}
	}
	
	static Object retrieveConvertParameter(Class<?> type, Annotation[] annos)
	{
		return retrieveConvertParameter(type, annos, false);
	}
	
	static Object retrieveConvertParameter(Class<?> type, Annotation[] annos, boolean throwIfNotApplicable)
	{
		IllegalArgumentException iae = null;
		Object object = null;
		try
		{
			object = ParamUtil.resolveParam(KrtkEnv.getContext(), type, annos, throwIfNotApplicable);
		}
		catch(IllegalArgumentException e)
		{
			iae = e;
		}
		if(object==null)
		{
			iae = null;
			try
			{
				object = HeaderUtil.resolveHeader(KrtkEnv.getContext(), type, annos, throwIfNotApplicable);
			}
			catch(IllegalArgumentException e)
			{
				iae = e;
			}
		}
		
		if(object==null)
		{
			iae = null;
			try
			{
				object = ContextUtil.resolveContext(KrtkEnv.getContext(), type, annos, throwIfNotApplicable);
			}
			catch(IllegalArgumentException e)
			{
				iae = e;
			}
		}
		
		if(object==null)
		{
			iae = null;
			try
			{
				object = BodyUtil.resolveBody(KrtkEnv.getContext(), type, annos, throwIfNotApplicable);
			}
			catch(IllegalArgumentException e)
			{
				iae = e;
			}
		}
		
		if((iae!=null) && throwIfNotApplicable)
		{
			throw iae;
		}
		
		return object;
	}
	
	static ResponseAction invokeMethod(Object subject, Method method) throws InvocationTargetException, IllegalAccessException, IOException
	{
		Object[] params = new Object[method.getParameterCount()];
		Class<?>[] types = method.getParameterTypes();
		if(params.length>0)
		{
			Annotation[][] annos = method.getParameterAnnotations();
			for(int i=0; i<params.length; i++)
			{
				Annotation[] pannos = annos[i];
				try
				{
					Object value = retrieveConvertParameter(types[i], pannos);
					params[i] = value;
				}
				catch(IllegalArgumentException iae)
				{
					// ignore if not applicable
				}
			}
		}
		return (ResponseAction) method.invoke(subject, params);
	}
	
	protected boolean tryInvoke(PageProvider pp, String path, Class<?> clazz, Method method) throws Exception
	{
		try
		{
			// TODO KrtkServlet.this.log("invoke class "+clazz.getCanonicalName()+" method "+method.getName()+" with path "+path);
			Object obj = instantiateClass(pp, path, clazz);
			ResponseAction responseAction = invokeMethod(obj, method);
			executeAction(responseAction, clazz, obj, method);
			return true;
		}
		catch(Throwable e)
		{
			if(e instanceof Exception)
			{
				throw (Exception)e;
			}
			else
			{
				throw new Exception(e.getMessage(), e);
			}
		}
	}
	
	protected boolean tryInvoke(PageProvider pp, String path, Class<?> clazz) throws Exception
	{
		try
		{
			if(RequestHandler.class.isAssignableFrom(clazz))
			{
				// TODO KrtkServlet.this.log("invoke class "+clazz.getCanonicalName()+" with path "+path);
				Object obj = instantiateClass(pp, path, clazz);
				ResponseAction responseAction = ((RequestHandler)obj).handleRequest();
				executeAction(responseAction, clazz, obj, null);
				return true;
			}
			else
			if(clazz.getMethod("handleRequest")!=null)
			{
				return this.tryInvoke(pp, path, clazz, clazz.getMethod("handleRequest"));
			}
			else
			{
				throw new Exception("cannot execute "+clazz.getCanonicalName()+" from "+path);
			}
		}
		catch(Throwable e)
		{
			if(e instanceof Exception)
			{
				throw (Exception)e;
			}
			else
			{
				throw new Exception(e);
			}
		}
	}
	
	public boolean checkPathForInvoke(String path)
	{
		try
		{
			for(PageProvider pp : this.pageProvider)
			{
				Method method = pp.resolveInvokableMethod(path);
				if(method != null)
				{
					return true;
				}
				Class clazz = pp.resolveInvokableClass(path);
				if(clazz != null)
				{
					return true;
				}
			}
		}
		catch(Throwable e)
		{
			KrtkEnv.getContext().log(e);
		}
		return false;
	}
	
	@Override
	public long getTimeStamp()
	{
		return this.timeStamp;
	}
	
	public boolean tryInvoke(String path)
	{
		try
		{
			for(PageProvider pp : this.pageProvider)
			{
				Method method = pp.resolveInvokableMethod(path);
				if(method != null)
				{
					Class clazz = method.getDeclaringClass();
					KrtkUtil.checkCSRF(method);
					return this.tryInvoke(pp, path, clazz, method);
				}
				Class clazz = pp.resolveInvokableClass(path);
				if(clazz != null)
				{
					KrtkUtil.checkCSRF(clazz);
					return this.tryInvoke(pp, path, clazz);
				}
			}
		}
		catch(IllegalAccessException iae)
		{
			try
			{
				executeAction(KrtkUtil.error(KrtkUtil.STATUS_UNAUTHORIZED, iae.getMessage()), null, null, null);
			}
			catch(Exception e)
			{
				KrtkEnv.getContext().log(e);
			}
		}
		catch(Throwable e)
		{
			KrtkEnv.getContext().log(e);
		}
		return false;
	}
	
	static Object inject(Class<?> clazz, Object obj)
	{
		for(Field field : clazz.getFields())
		{
			try
			{
				Object value = retrieveConvertParameter(field.getType(), field.getAnnotations());
				if(value!=null)
				{
					field.set(obj, value);
				}
			}
			catch(Exception xe)
			{
				KrtkEnv.getContext().log(xe);
			}
		}
		return obj;
	}
	
	static Object instantiateClass(PageProvider pp, String path, Class<?> clazz) throws IllegalAccessException, InstantiationException, IOException
	{
		Object ret = pp.createClass(path, clazz);
		
		return inject(clazz, ret);
	}
	
}
