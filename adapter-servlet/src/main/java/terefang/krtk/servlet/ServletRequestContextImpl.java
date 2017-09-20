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
package terefang.krtk.servlet;

import terefang.krtk.*;
import terefang.krtk.provider.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by fredo on 15.04.17.
 */
public class ServletRequestContextImpl extends RequestContextImpl implements RequestContext
{
	HttpServletRequest httpServletRequest;
	HttpServletResponse httpServletResponse;
	
	public static RequestContext create(HttpServletRequest req, HttpServletResponse resp, List<PageProvider> pageProvider, List<TemplateProvider> templateProvider)
	{
		final ServletRequestContextImpl rci = new ServletRequestContextImpl();
		
		rci.templateProvider = templateProvider;
		rci.pageProvider = pageProvider;
		
		rci.httpServletRequest = req;
		rci.httpServletResponse = resp;
		
		rci.loggingProvider = new LoggingProvider()
		{
			@Override
			public void log(String message)
			{
				rci.httpServletRequest.getServletContext().log(message);
			}
			
			@Override
			public void log(String message, Throwable th)
			{
				rci.httpServletRequest.getServletContext().log(message, th);
			}
			
			@Override
			public void log(Throwable th)
			{
				rci.httpServletRequest.getServletContext().log(th.getMessage(), th);
			}
		};
		
		rci.requestBodyProvider = new RequestBodyProvider()
		{
			public byte[] getBodyBytes()
			{
				return new byte[0];
			}
			
			public InputStream getBodyInputStream()
			{
				try
				{
					return rci.httpServletRequest.getInputStream();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				return null;
			}
			
			public String getBodyCharset()
			{
				return rci.httpServletRequest.getCharacterEncoding();
			}
			
			public String getBodyContentType()
			{
				return rci.httpServletRequest.getContentType();
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
				rci.httpServletResponse.setContentType(type);
			}
			
			public void setResponseBodyCharset(String charset)
			{
				rci.httpServletResponse.setCharacterEncoding(charset);
			}
			
			public String getResponseBodyCharset()
			{
				return rci.httpServletResponse.getCharacterEncoding();
			}
			
			public void setResponseBodyLength(long len)
			{
				rci.httpServletResponse.setContentLengthLong(len);
			}
			
			public OutputStream getResponseBodyOutputStream()
			{
				try
				{
					return rci.httpServletResponse.getOutputStream();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				return null;
			}
		};
		
		rci.requestHeaderProvider = new RequestHeaderProvider()
		{
			public String getRequestHeader(String headerName)
			{
				return rci.httpServletRequest.getHeader(headerName);
			}
			
			public List<String> getRequestHeaders(String headerName)
			{
				return Collections.list(rci.httpServletRequest.getHeaders(headerName));
			}
			
			public List<String> getRequestHeaderNames()
			{
				return Collections.list(rci.httpServletRequest.getHeaderNames());
			}
			
			public Map<String, List<String>> getRequestHeaderMap()
			{
				Map<String, List<String>> ret = new HashMap<String, List<String>>();
				Collections.list(rci.httpServletRequest.getHeaderNames()).forEach( (pkey) -> {
					ret.put(pkey, this.getRequestHeaders(pkey));
				});
				return ret;
			}
		};
		
		rci.responseHeaderProvider = new ResponseHeaderProvider()
		{
			public void setResponseHeader(String key, String value)
			{
				rci.httpServletResponse.setHeader(key, value);
			}
			
			@Override
			public void setResponseStatus(int status, String value)
			{
				if(status < 400)
				{
					rci.httpServletResponse.setStatus(status);
				}
				else
				{
					try
					{
						rci.httpServletResponse.sendError(status, value);
					}
					catch(IOException e)
					{
						rci.log(e);
					}
				}
			}
		};
		
		rci.requestParamProvider = new RequestParamProvider()
		{
			public String getRequestParam(String headerName)
			{
				return rci.httpServletRequest.getParameter(headerName);
			}
			
			public List<String> getRequestParams(String headerName)
			{
				return Arrays.asList(rci.httpServletRequest.getParameterValues(headerName));
			}
			
			public List<String> getRequestParamNames()
			{
				return Collections.list(rci.httpServletRequest.getParameterNames());
			}
			
			public Map<String, List<String>> getRequestParamMap()
			{
				Map<String, List<String>> ret = new HashMap<String, List<String>>();
				rci.httpServletRequest.getParameterMap().forEach( (pkey, pvalue) -> {
					ret.put(pkey, Arrays.asList(pvalue));
				});
				return ret;
			}
		};
		
		rci.responseParamProvider = new ResponseParamProvider()
		{
			public void setResponseParam(String key, Object value)
			{
				rci.httpServletRequest.setAttribute(key, value);
			}
		};
		
		rci.requestContextProvider = new RequestContextProvider()
		{
			public Map<String, Object> getContextAttributes()
			{
				Map<String, Object> ret = new HashMap();
				Enumeration<String> en = rci.httpServletRequest.getServletContext().getAttributeNames();
				while(en.hasMoreElements())
				{
					String aKey = en.nextElement();
					ret.put(aKey, rci.httpServletRequest.getServletContext().getAttribute(aKey));
				}
				return ret;
			}
			
			public Object getContextAttribute(String attributeName)
			{
				return rci.httpServletRequest.getServletContext().getAttribute(attributeName);
			}
			
			public void setContextAttribute(String attributeName, Object value)
			{
				rci.httpServletRequest.getServletContext().setAttribute(attributeName, value);
			}
			
			public Map<String, Object> getAttributes()
			{
				Map<String, Object> ret = new HashMap();
				Enumeration<String> en = rci.httpServletRequest.getAttributeNames();
				while(en.hasMoreElements())
				{
					String aKey = en.nextElement();
					ret.put(aKey, rci.httpServletRequest.getAttribute(aKey));
				}
				return ret;
			}
			
			public Object getAttribute(String attributeName)
			{
				return rci.httpServletRequest.getAttribute(attributeName);
			}
			
			public void setAttribute(String attributeName, Object value)
			{
				rci.httpServletRequest.setAttribute(attributeName, value);
			}
			
			public String getMethod()
			{
				return rci.httpServletRequest.getMethod();
			}
			
			@Override
			public String getProtocol()
			{
				return rci.httpServletRequest.getProtocol();
			}
			
			public String getPath()
			{
				String path = rci.httpServletRequest.getRequestURI();
				
				if(path.startsWith(rci.httpServletRequest.getContextPath()))
				{
					path = path.substring(rci.httpServletRequest.getContextPath().length());
				}
				
				return path;
			}
			
			@Override
			public String getContextPath()
			{
				return rci.httpServletRequest.getContextPath();
			}
			
			public RequestContext getContext()
			{
				return rci;
			}
			
			@Override
			public URL getResource(String path)
			{
				return KrtkUtil.resolveResource(rci.httpServletRequest.getServletContext().getRealPath("/"), rci.httpServletRequest.getServletContext().getClassLoader(), path);
			}
			
			@Override
			public Reader getResourceReader(String path, Charset charset) throws FileNotFoundException
			{
				try
				{
					return KrtkUtil.resolveResourceReader(rci.httpServletRequest.getServletContext().getRealPath("/"), rci.httpServletRequest.getServletContext().getClassLoader(), path, charset);
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
					return KrtkUtil.resolveResourceStream(rci.httpServletRequest.getServletContext().getRealPath("/"), rci.httpServletRequest.getServletContext().getClassLoader(), path);
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
				try
				{
					rci.httpServletResponse.sendRedirect(url);
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			
			public void doForward(String path)
			{
				try
				{
					rci.httpServletRequest.getRequestDispatcher(path).forward(rci.httpServletRequest, rci.httpServletResponse);
				}
				catch(ServletException e)
				{
					e.printStackTrace();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			
			public void doTemplate(String path)
			{
				this.doForward(path);
			}
			
			@Override
			public void doError(int code, String text)
			{
				try
				{
					rci.httpServletResponse.sendError(code, text);
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		};
		
		return rci;
	}
	
	@Override
	public void doError(int code, String text)
	{
		this.actionProvider.doError(code, text);
	}
}
