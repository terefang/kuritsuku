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
package terefang.krtk.undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import terefang.krtk.KrtkEnv;
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
public class KrtkUtHandler implements HttpHandler
{
	public static final NCSARequestLog NCSALOG = new NCSARequestLog();
	public static final Log LOG = LogFactory.getLog("KrtkJdkHttpHandler");

	List<PageProvider> pageProvider = null;
	
	List<TemplateProvider> templateProvider = null;
	
	String errorPage = "/Error.vm";
	
	private File resourcePath;
	
	public void init(Properties properties)
	{
		KrtkUtEnv.setActionSuffix(properties.getProperty(KrtkUtil.ACTION_SUFFIX_KEY, ".do"));
		KrtkUtEnv.setActionPrefix(properties.getProperty(KrtkUtil.ACTION_PREFIX_KEY, null));
		KrtkUtEnv.setTemplateSuffix(properties.getProperty(KrtkUtil.TEMPLATE_SUFFIX_KEY, ".thtml"));
		
		this.pageProvider = PageProviderService.resolveProvider(properties);
		this.templateProvider = TemplateProviderService.resolveProvider(properties);
		
		this.resourcePath = new File(properties.getProperty(KrtkUtil.RESOURCE_PATH_KEY, null));
	}
	
	@Override
	public void handleRequest(HttpServerExchange httpExchange) throws Exception
	{
		if (httpExchange.isInIoThread()) {
			httpExchange.dispatch(this);
			return;
		}
		
		String path = httpExchange.getRequestPath();

		RequestContext ctx = KrtkUtContextImpl.create(this.resourcePath, httpExchange, this.pageProvider, this.templateProvider);
		KrtkUtEnv.setContext(ctx);

		try
		{
			if(KrtkUtEnv.getActionSuffix()!=null && path.endsWith(KrtkUtEnv.getActionSuffix()))
			{
				this.handleInvoke(httpExchange);
			}
			else
			if(KrtkUtEnv.getActionPrefix()!=null && path.startsWith(KrtkUtEnv.getActionPrefix()))
			{
				this.handleInvoke(httpExchange);
			}
			else
			if(KrtkUtEnv.getContext().checkPathForInvoke(path))
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
			KrtkUtEnv.setContext(null);
		}
	}
	
	public void handleInvoke(HttpServerExchange httpExchange) throws IOException
	{
		RequestContext ctx = KrtkEnv.getContext();
		
		String lPath = KrtkUtil.normalizePath(ctx.getPath(), null, null, null, KrtkUtEnv.getActionSuffix());
		if(ctx.tryInvoke(lPath))
		{
			return;
		}
		
		httpExchange.setStatusCode(KrtkUtil.STATUS_INTERNAL_SERVER_ERROR);
	}

	public void handleResource(HttpServerExchange httpExchange) throws IOException
	{
		String path = httpExchange.getRequestPath();
	
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
					httpExchange.getResponseHeaders().add(HttpString.tryFromString(KrtkUtil.HEADER_CONNECTION), "close");
					String contentType = KrtkUtil.resolveMimeType(safePath);
					httpExchange.getResponseHeaders().add(HttpString.tryFromString(KrtkUtil.HEADER_CONTENT_TYPE), (contentType == null ? "application/octetstream" : contentType));
					httpExchange.setStatusCode(KrtkUtil.STATUS_OK);
					httpExchange.startBlocking();
					os = httpExchange.getOutputStream();
					IOUtils.copy(is, os);
					return;
				}
			}
			catch(IOException ioe)
			{
				httpExchange.setStatusCode(KrtkUtil.STATUS_NO_CONTENT);
				return;
			}
			catch(Exception xe)
			{
				LOG.error(xe.getMessage(), xe);
				httpExchange.setStatusCode(KrtkUtil.STATUS_SERVICE_UNAVAILABLE);
				return;
			}
			finally
			{
				IOUtils.closeQuietly(is);
				IOUtils.closeQuietly(os);
			}
		}
		httpExchange.setStatusCode(KrtkUtil.STATUS_NOT_FOUND);
	}
	
}
