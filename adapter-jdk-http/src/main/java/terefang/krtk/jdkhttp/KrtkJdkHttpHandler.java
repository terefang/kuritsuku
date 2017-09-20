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
import com.sun.net.httpserver.HttpHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import terefang.krtk.KrtkUtil;
import terefang.krtk.RequestContext;
import terefang.krtk.io.IOUtils;
import terefang.krtk.provider.PageProvider;
import terefang.krtk.provider.PageProviderService;
import terefang.krtk.provider.TemplateProvider;
import terefang.krtk.provider.TemplateProviderService;
import terefang.krtk.util.NCSARequestLog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by fredo on 10.04.17.
 */
public class KrtkJdkHttpHandler implements HttpHandler
{
	public static final NCSARequestLog NCSALOG = new NCSARequestLog();
	public static final Log LOG = LogFactory.getLog("KrtkJdkHttpHandler");

	List<PageProvider> pageProvider = null;
	
	List<TemplateProvider> templateProvider = null;
	
	String errorPage = "/Error.vm";
	
	private File resourcePath;
	
	public void init(Properties properties)
	{
		KrtkJdkHttpEnv.setActionSuffix(properties.getProperty(KrtkUtil.ACTION_SUFFIX_KEY, ".do"));
		KrtkJdkHttpEnv.setActionPrefix(properties.getProperty(KrtkUtil.ACTION_PREFIX_KEY, null));
		KrtkJdkHttpEnv.setTemplateSuffix(properties.getProperty(KrtkUtil.TEMPLATE_SUFFIX_KEY, ".thtml"));
		
		this.pageProvider = PageProviderService.resolveProvider(properties);
		this.templateProvider = TemplateProviderService.resolveProvider(properties);
		
		this.resourcePath = new File(properties.getProperty(KrtkUtil.RESOURCE_PATH_KEY, null));
	}
	
	@Override
	public void handle(HttpExchange httpExchange) throws IOException
	{
		RequestContext ctx = KrtkJdkHttpContextImpl.create(this.resourcePath, httpExchange, this.pageProvider, this.templateProvider);
		KrtkJdkHttpEnv.setContext(ctx);
		try
		{
			String path = httpExchange.getRequestURI().getPath();
			
			if(KrtkJdkHttpEnv.getActionSuffix()!=null && path.endsWith(KrtkJdkHttpEnv.getActionSuffix()))
			{
				this.handleInvoke(httpExchange);
			}
			else
			if(KrtkJdkHttpEnv.getActionPrefix()!=null && path.startsWith(KrtkJdkHttpEnv.getActionPrefix()))
			{
				this.handleInvoke(httpExchange);
			}
			else
			if(KrtkJdkHttpEnv.getContext().checkPathForInvoke(path))
			{
				this.handleInvoke(httpExchange);
			}
			else
			{
				this.handleResource(httpExchange);
			}
		}
		finally
		{
			NCSALOG.log(ctx);
			KrtkJdkHttpEnv.setContext(null);
		}
	}
	
	public void handleInvoke(HttpExchange httpExchange) throws IOException
	{
		RequestContext ctx = KrtkJdkHttpEnv.getContext();
		
		String lPath = KrtkUtil.normalizePath(ctx.getPath(), null, null, null, KrtkJdkHttpEnv.getActionSuffix());
		if(ctx.tryInvoke(lPath))
		{
			return;
		}
		
		httpExchange.sendResponseHeaders(KrtkUtil.STATUS_INTERNAL_SERVER_ERROR, -1);
	}

	public void handleResource(HttpExchange httpExchange) throws IOException
	{
		String path = httpExchange.getRequestURI().getPath();
	
		if(!path.contains("/WEB-INF/") && !path.contains("/META-INF/"))
		{
			String safePath = path.replaceAll("\\.+/", "/").replaceAll("/+", "/");
			InputStream is = null;
			OutputStream os = null;
			try
			{
				is = KrtkUtil.resolveResourceStream(this.resourcePath.getAbsolutePath(), this.getClass().getClassLoader(), safePath);
				if(is != null)
				{
					httpExchange.getResponseHeaders().add(KrtkUtil.HEADER_CONNECTION, "close");
					String contentType = KrtkUtil.resolveMimeType(safePath);
					httpExchange.getResponseHeaders().add(KrtkUtil.HEADER_CONTENT_TYPE, (contentType == null ? "application/octetstream" : contentType));
					httpExchange.sendResponseHeaders(KrtkUtil.STATUS_OK, -1);
					os = httpExchange.getResponseBody();
					IOUtils.copy(is, os);
					return;
				}
			}
			catch(IOException ioe)
			{
				httpExchange.sendResponseHeaders(KrtkUtil.STATUS_NO_CONTENT, -1);
				return;
			}
			catch(Exception xe)
			{
				LOG.error(xe.getMessage(), xe);
				httpExchange.sendResponseHeaders(KrtkUtil.STATUS_SERVICE_UNAVAILABLE, -1);
				return;
			}
			finally
			{
				IOUtils.closeQuietly(is);
				IOUtils.closeQuietly(os);
			}
		}
		httpExchange.sendResponseHeaders(KrtkUtil.STATUS_NOT_FOUND, -1);
	}
}
