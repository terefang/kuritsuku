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
package terefang.krtk.simpleframework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import terefang.krtk.*;
import terefang.krtk.io.IOUtils;
import terefang.krtk.provider.*;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by fredo on 15.04.17.
 */
public class KrtkSfContextImpl extends RequestContextImpl implements RequestContext
{
	Request httpRequest;
	Response httpResponse;
	public static final Log LOG = LogFactory.getLog("KrtkSf");
	File resourcePath = null;
	boolean headerSent = false;
	Map<String, Object> attributes = new HashMap();
	
	public static RequestContext create(File resourcePath, Request request, Response response, List<PageProvider> pageProvider, List<TemplateProvider> templateProvider)
	{
		final KrtkSfContextImpl rci = new KrtkSfContextImpl();
		rci.resourcePath = resourcePath;
		rci.templateProvider = templateProvider;
		rci.pageProvider = pageProvider;
		
		rci.httpRequest = request;
		rci.httpResponse = response;
		
		rci.loggingProvider = new LoggingProvider()
		{
			@Override
			public void log(String message)
			{
				rci.LOG.warn(message);
			}
			
			@Override
			public void log(String message, Throwable th)
			{
				rci.LOG.warn(message, th);
			}
			
			@Override
			public void log(Throwable th)
			{
				rci.LOG.warn(th.getMessage(), th);
			}
		};
		
		rci.requestBodyProvider = new RequestBodyProvider()
		{
			public byte[] getBodyBytes()
			{
				try
				{
					return IOUtils.toByteArray(this.getBodyInputStream());
				}
				catch(IOException e)
				{
					rci.log(e.getMessage(), e);
				}
				return new byte[0];
			}
			
			public InputStream getBodyInputStream()
			{
				try
				{
					return rci.httpRequest.getInputStream();
				}
				catch(IOException e)
				{
					rci.log(e.getMessage(), e);
					return null;
				}
			}
			
			public String getBodyCharset()
			{
				try
				{
					String result = rci.httpRequest.getContentType().getCharset();
					if(result==null)
					{
						return StandardCharsets.US_ASCII.name();
					}
					return result;
				}
				catch(Exception xe)
				{
					return StandardCharsets.US_ASCII.name();
				}
			}
			
			public String getBodyContentType()
			{
				try
				{
					return rci.httpRequest.getContentType().getType();
				}
				catch(Exception xe)
				{
					return "";
				}
			}
			
			public List<RequestBodyPart> getBodyParts()
			{
				return null;
			}
			
			public RequestBodyPart getBodyPart(String pname)
			{
				return null;
			}
		};
		
		rci.responseBodyProvider = new ResponseBodyProvider()
		{
			public void setResponseBodyContentType(String type)
			{
				rci.httpResponse.setContentType(type);
				if(this.getResponseBodyCharset()!=null)
				{
					rci.httpResponse.getContentType().setCharset(this.getResponseBodyCharset());
				}
			}
			
			public void setResponseBodyCharset(String charset)
			{
				rci.httpResponse.setValue("X-Content-Encoding", charset);
				if(rci.httpResponse.getContentType()!=null)
				{
					rci.httpResponse.getContentType().setCharset(charset);
				}
			}
			
			public String getResponseBodyCharset()
			{
				return rci.httpResponse.getValue("X-Content-Encoding");
			}
			
			public void setResponseBodyLength(long len)
			{
				rci.httpResponse.setContentLength(len);
			}
			
			public OutputStream getResponseBodyOutputStream()
			{
				if(!rci.headerSent)
				{
					rci.headerSent=true;
					rci.setResponseStatus(KrtkUtil.STATUS_OK, "OK");
				}
				try
				{
					return rci.httpResponse.getOutputStream();
				}
				catch(IOException e)
				{
					rci.log(e.getMessage(), e);
					return null;
				}
			}
		};
		
		rci.requestHeaderProvider = new RequestHeaderProvider()
		{
			public String getRequestHeader(String headerName)
			{
				return rci.httpRequest.getValue(headerName);
			}
			
			public List<String> getRequestHeaders(String headerName)
			{
				return rci.httpRequest.getValues(headerName);
			}
			
			public List<String> getRequestHeaderNames()
			{
				return rci.httpRequest.getNames();
			}
			
			public Map<String, List<String>> getRequestHeaderMap()
			{
				Map<String, List<String>> ret = new HashMap();
				this.getRequestHeaderNames().forEach( (pkey) -> {
					ret.put(pkey, this.getRequestHeaders(pkey));
				});
				return ret;
			}
		};
		
		rci.responseHeaderProvider = new ResponseHeaderProvider()
		{
			public void setResponseHeader(String key, String value)
			{
				rci.httpResponse.setValue(key, value);
			}
			
			@Override
			public void setResponseStatus(int status, String value)
			{
				rci.headerSent = true;
				if(status < 400)
				{
					rci.httpResponse.setCode(status);
				}
				else
				{
					rci.httpResponse.setCode(status);
				}
			}
		};
		
		rci.requestParamProvider = new RequestParamProvider()
		{
			public String getRequestParam(String headerName)
			{
				return rci.httpRequest.getQuery().get(headerName);
			}
			
			public List<String> getRequestParams(String headerName)
			{
				return rci.httpRequest.getQuery().getAll(headerName);
			}
			
			public List<String> getRequestParamNames()
			{
				return Arrays.asList(rci.httpRequest.getQuery().keySet().toArray(new String[0]));
			}
			
			public Map<String, List<String>> getRequestParamMap()
			{
				Map<String, List<String>> ret = new HashMap();
				this.getRequestParamNames().forEach((key) -> {
					ret.put(key, rci.httpRequest.getQuery().getAll(key));
				});
				return ret;
			}
		};
		
		
		rci.responseParamProvider = new ResponseParamProvider()
		{
			public void setResponseParam(String key, Object value)
			{
				rci.attributes.put(key, value);
			}
		};
		
		rci.requestContextProvider = new RequestContextProvider()
		{
			public Map<String, Object> getContextAttributes()
			{
				return rci.attributes;
			}
			
			public Object getContextAttribute(String attributeName)
			{
				return rci.attributes.get(attributeName);
			}
			
			public void setContextAttribute(String attributeName, Object value)
			{
				rci.attributes.put(attributeName, value);
			}
			
			public Map<String, Object> getAttributes()
			{
				return rci.attributes;
			}
			
			public Object getAttribute(String attributeName)
			{
				return rci.attributes.get(attributeName);
			}
			
			public void setAttribute(String attributeName, Object value)
			{
				rci.attributes.put(attributeName, value);
			}
			
			public String getMethod()
			{
				return rci.httpRequest.getMethod();
			}
			
			@Override
			public String getProtocol()
			{
				return "HTTP/"+(rci.httpRequest.getMajor())+"."+(rci.httpRequest.getMinor());
			}
			
			public String getPath()
			{
				String path = rci.httpRequest.getPath().getPath();
				String contextPath = this.getContextPath();
				if(path.startsWith(contextPath))
				{
					path = path.substring(contextPath.length());
				}
				return path;
			}
			
			@Override
			public String getContextPath()
			{
				return "";
			}
			
			public RequestContext getContext()
			{
				return rci;
			}
			
			@Override
			public URL getResource(String path)
			{
				return KrtkUtil.resolveResource(rci.resourcePath.getAbsolutePath(), rci.getClass().getClassLoader(), path);
			}
			
			@Override
			public Reader getResourceReader(String path, Charset charset) throws FileNotFoundException
			{
				try
				{
					return KrtkUtil.resolveResourceReader(rci.resourcePath.getAbsolutePath(), rci.getClass().getClassLoader(), path, charset);
				}
				catch(Exception xe)
				{
					throw new FileNotFoundException(path);
				}
			}
			
			@Override
			public InputStream getResourceStream(String path) throws FileNotFoundException
			{
				try
				{
					return KrtkUtil.resolveResourceStream(rci.resourcePath.getAbsolutePath(), rci.getClass().getClassLoader(), path);
				}
				catch(Exception xe)
				{
					throw new FileNotFoundException(path);
				}
			}
		};
		
		rci.actionProvider = new ActionProvider()
		{
			public void doRedirect(String url)
			{
				rci.httpResponse.setValue(KrtkUtil.HEADER_LOCATION, url);
				rci.httpResponse.setCode(KrtkUtil.STATUS_MOVED_TEMPORARELY);
			}
			
			public void doForward(String path)
			{
				this.doRedirect(path);
			}
			
			public void doTemplate(String path)
			{
				this.doForward(path);
			}
		};
		
		return rci;
	}
	
}
