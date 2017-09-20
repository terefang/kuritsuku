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
package terefang.krtk.jdkhttp;

import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import terefang.krtk.*;
import terefang.krtk.io.IOUtils;
import terefang.krtk.provider.*;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by fredo on 15.04.17.
 */
public class KrtkJdkHttpContextImpl extends RequestContextImpl implements RequestContext
{
	HttpExchange httpExchange;
	public static final Log LOG = LogFactory.getLog("KrtkJdkHttp");
	File resourcePath = null;
	boolean headerSent = false;
	public static RequestContext create(File resourcePath, HttpExchange httpExchange, List<PageProvider> pageProvider, List<TemplateProvider> templateProvider)
	{
		final KrtkJdkHttpContextImpl rci = new KrtkJdkHttpContextImpl();
		rci.resourcePath = resourcePath;
		rci.templateProvider = templateProvider;
		rci.pageProvider = pageProvider;
		
		rci.httpExchange = httpExchange;
		
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
				return rci.httpExchange.getRequestBody();
			}
			
			public String getBodyCharset()
			{
				String result = rci.httpExchange.getRequestHeaders().getFirst("content-type");
				if(result.contains("charset="))
				{
					return result.substring(result.lastIndexOf('=')).trim();
				}
				return StandardCharsets.US_ASCII.name();
			}
			
			public String getBodyContentType()
			{
				String result = rci.httpExchange.getRequestHeaders().getFirst("content-type");
				if(result!=null)
				{
					if(result.contains(";"))
					{
						return result.substring(0, result.indexOf(';')).trim().toLowerCase();
					}
					return result.trim().toLowerCase();
				}
				return ResponseAction.STREAM.toLowerCase();
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
				rci.httpExchange.getResponseHeaders().put("X-Content-Type", Collections.singletonList(type));
				if(rci.httpExchange.getResponseHeaders().containsKey("X-Content-Encoding"))
				{
					String charset = rci.httpExchange.getResponseHeaders().getFirst("X-Content-Encoding");
					if(charset!=null)
					{
						charset = charset.trim().toLowerCase();
						rci.httpExchange.getResponseHeaders().put("Content-Type", Collections.singletonList(type+"; charset="+charset));
					}
				}
			}
			
			public void setResponseBodyCharset(String charset)
			{
				rci.httpExchange.getResponseHeaders().put("X-Content-Encoding", Collections.singletonList(charset));
				if(rci.httpExchange.getResponseHeaders().containsKey("X-Content-Type"))
				{
					String ct = rci.httpExchange.getResponseHeaders().getFirst("X-Content-Type");
					if(ct!=null)
					{
						ct = ct.trim().toLowerCase();
						rci.httpExchange.getResponseHeaders().put("Content-Type", Collections.singletonList(ct+"; charset="+charset));
					}
				}
			}
			
			public String getResponseBodyCharset()
			{
				return rci.httpExchange.getResponseHeaders().getFirst("X-Content-Encoding");
			}
			
			public void setResponseBodyLength(long len)
			{
				rci.httpExchange.getResponseHeaders().put("Content-Length", Collections.singletonList(String.valueOf(len)));
			}
			
			public OutputStream getResponseBodyOutputStream()
			{
				if(!rci.headerSent)
				{
					rci.headerSent=true;
					rci.setResponseStatus(KrtkUtil.STATUS_OK, "OK");
				}
				return rci.httpExchange.getResponseBody();
			}
		};
		
		rci.requestHeaderProvider = new RequestHeaderProvider()
		{
			public String getRequestHeader(String headerName)
			{
				return rci.httpExchange.getRequestHeaders().getFirst(headerName);
			}
			
			public List<String> getRequestHeaders(String headerName)
			{
				return rci.httpExchange.getRequestHeaders().get(headerName);
			}
			
			public List<String> getRequestHeaderNames()
			{
				return Arrays.asList(rci.httpExchange.getRequestHeaders().keySet().toArray(new String[0]));
			}
			
			public Map<String, List<String>> getRequestHeaderMap()
			{
				Map<String, List<String>> ret = new HashMap<String, List<String>>();
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
				rci.httpExchange.getResponseHeaders().put(key, Collections.singletonList(value));
			}
			
			@Override
			public void setResponseStatus(int status, String value)
			{
				try
				{
					rci.headerSent = true;
					if(status < 400)
					{
						rci.httpExchange.sendResponseHeaders(status, 0);
					}
					else
					{
						rci.httpExchange.sendResponseHeaders(status, -1);
					}
				}
				catch(IOException ioe)
				{
					rci.log(ioe);
				}
			}
		};
		
		rci.requestParamProvider = new RequestParamProvider()
		{
			Map<String, List<String>> parameter = null;
			
			public synchronized void parseRequestParam() {
				if(this.parameter==null)
				{
					this.parameter=new HashMap();
					String rawQuery = rci.httpExchange.getRequestURI().getRawQuery();
					for(String queryPart : rawQuery.split("&"))
					{
						try
						{
							if(queryPart.contains("="))
							{
								int off = queryPart.indexOf('=');
								String keyPart = URLDecoder.decode(queryPart.substring(0,off), StandardCharsets.ISO_8859_1.name());
								String valuePart = URLDecoder.decode(queryPart.substring(off+1), StandardCharsets.ISO_8859_1.name());
								if(!this.parameter.containsKey(keyPart))
								{
									this.parameter.put(keyPart, new Vector());
								}
								this.parameter.get(keyPart).add(valuePart);
							}
							else
							{
								String decodedPart = URLDecoder.decode(queryPart, StandardCharsets.ISO_8859_1.name());
								this.parameter.put(decodedPart, Collections.singletonList(decodedPart));
							}
						}
						catch(Exception xe)
						{
							if(!this.parameter.containsKey("__DECODE_ERROR"))
							{
								this.parameter.put("__DECODE_ERROR", new Vector());
							}
							this.parameter.get("__DECODE_ERROR").add(queryPart);
						}
					}
				}
			}
			
			public String getRequestParam(String headerName)
			{
				this.parseRequestParam();
				if(this.parameter.containsKey(headerName))
				{
					return this.parameter.get(headerName).get(0);
				}
				return null;
			}
			
			public List<String> getRequestParams(String headerName)
			{
				this.parseRequestParam();
				if(this.parameter.containsKey(headerName))
				{
					return this.parameter.get(headerName);
				}
				return Collections.EMPTY_LIST;
			}
			
			public List<String> getRequestParamNames()
			{
				this.parseRequestParam();
				return Arrays.asList(this.parameter.keySet().toArray(new String[0]));
			}
			
			public Map<String, List<String>> getRequestParamMap()
			{
				this.parseRequestParam();
				return new HashMap<>(this.parameter);
			}
		};
		
		
		rci.responseParamProvider = new ResponseParamProvider()
		{
			public void setResponseParam(String key, Object value)
			{
				if(rci.httpExchange.getAttribute("_KRTK_REQ_ATTR")==null)
				{
					rci.httpExchange.setAttribute("_KRTK_REQ_ATTR", new HashMap());
				}
				((Map)rci.httpExchange.getAttribute("_KRTK_REQ_ATTR")).put(key, value);
			}
		};
		
		rci.requestContextProvider = new RequestContextProvider()
		{
			public Map<String, Object> getContextAttributes()
			{
				if(rci.httpExchange.getHttpContext().getAttributes().get(this.getClass().getName())==null)
				{
					rci.httpExchange.getHttpContext().getAttributes().put(this.getClass().getName(), new HashMap());
				}
				
				return (Map<String, Object>) rci.httpExchange.getHttpContext().getAttributes().get(this.getClass().getName());
			}
			
			public Object getContextAttribute(String attributeName)
			{
				return this.getContextAttributes().get(attributeName);
			}
			
			public void setContextAttribute(String attributeName, Object value)
			{
				this.getContextAttributes().put(attributeName, value);
			}
			
			public Map<String, Object> getAttributes()
			{
				if(rci.httpExchange.getAttribute("_KRTK_REQ_ATTR")==null)
				{
					rci.httpExchange.setAttribute("_KRTK_REQ_ATTR", new HashMap());
				}
				return ((Map)rci.httpExchange.getAttribute("_KRTK_REQ_ATTR"));
			}
			
			public Object getAttribute(String attributeName)
			{
				if(rci.httpExchange.getAttribute("_KRTK_REQ_ATTR")==null)
				{
					rci.httpExchange.setAttribute("_KRTK_REQ_ATTR", new HashMap());
				}
				return ((Map)rci.httpExchange.getAttribute("_KRTK_REQ_ATTR")).get(attributeName);
			}
			
			public void setAttribute(String attributeName, Object value)
			{
				if(rci.httpExchange.getAttribute("_KRTK_REQ_ATTR")==null)
				{
					rci.httpExchange.setAttribute("_KRTK_REQ_ATTR", new HashMap());
				}
				((Map)rci.httpExchange.getAttribute("_KRTK_REQ_ATTR")).put(attributeName, value);
			}
			
			public String getMethod()
			{
				return rci.httpExchange.getRequestMethod();
			}
			
			@Override
			public String getProtocol()
			{
				return rci.httpExchange.getProtocol();
			}
			
			public String getPath()
			{
				String path = rci.httpExchange.getRequestURI().getPath();
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
				String contextPath = rci.httpExchange.getHttpContext().getPath();
				if(contextPath.equals("/"))
				{
					return "";
				}
				return contextPath;
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
				try
				{
					rci.httpExchange.getResponseHeaders().put(KrtkUtil.HEADER_LOCATION, Collections.singletonList(url));
					rci.httpExchange.sendResponseHeaders(KrtkUtil.STATUS_MOVED_TEMPORARELY, -1);
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			
			public void doForward(String path)
			{
				this.doRedirect(path);
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
					rci.httpExchange.sendResponseHeaders(code, -1);
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
