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
import org.simpleframework.http.core.Container;

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
public class KrtkSfHandler implements Container
{
	public static final Log LOG = LogFactory.getLog("KrtkSfHandler");
	public static final NCSARequestLog NCSALOG = new NCSARequestLog();
	
	List<PageProvider> pageProvider = null;
	
	List<TemplateProvider> templateProvider = null;
	
	String errorPage = "/Error.vm";
	
	private File resourcePath;
	
	public void init(Properties properties)
	{
		KrtkSfEnv.setActionSuffix(properties.getProperty(KrtkUtil.ACTION_SUFFIX_KEY, ".do"));
		KrtkSfEnv.setActionPrefix(properties.getProperty(KrtkUtil.ACTION_PREFIX_KEY, null));
		KrtkSfEnv.setTemplateSuffix(properties.getProperty(KrtkUtil.TEMPLATE_SUFFIX_KEY, ".thtml"));
		
		this.pageProvider = PageProviderService.resolveProvider(properties);
		this.templateProvider = TemplateProviderService.resolveProvider(properties);
		
		this.resourcePath = new File(properties.getProperty(KrtkUtil.RESOURCE_PATH_KEY, null));
	}
	
	@Override
	public void handle(Request request, Response response)
	{
		RequestContext ctx = KrtkSfContextImpl.create(this.resourcePath, request, response, this.pageProvider, this.templateProvider);
		KrtkSfEnv.setContext(ctx);

		String path = request.getPath().getPath();
		try
		{
			if(KrtkSfEnv.getActionSuffix()!=null && path.endsWith(KrtkSfEnv.getActionSuffix()))
			{
				this.handleInvoke(request, response);
			}
			else
			if(KrtkSfEnv.getActionPrefix()!=null && path.startsWith(KrtkSfEnv.getActionPrefix()))
			{
				this.handleInvoke(request, response);
			}
			else
			if(KrtkSfEnv.getContext().checkPathForInvoke(path))
			{
				this.handleInvoke(request, response);
			}
			else
			{
				this.handleResource(request, response);
			}
		}
		catch(Exception xe)
		{
			LOG.error(xe.getMessage(), xe);
			response.setCode(KrtkUtil.STATUS_INTERNAL_SERVER_ERROR);
		}
		finally
		{
			NCSALOG.log(ctx);
			KrtkSfEnv.setContext(null);
		}
	}
	
	public void handleInvoke(Request request, Response response) throws Exception
	{
		RequestContext ctx = KrtkSfEnv.getContext();
		
		String lPath = KrtkUtil.normalizePath(ctx.getPath(), null, null, null, KrtkSfEnv.getActionSuffix());
		if(ctx.tryInvoke(lPath))
		{
			return;
		}
		
		lPath = KrtkUtil.normalizePath(ctx.getPath(), null, null, null, null);
		if(ctx.tryInvoke(lPath))
		{
			return;
		}
		
		response.setCode(KrtkUtil.STATUS_INTERNAL_SERVER_ERROR);
	}

	public void handleResource(Request request, Response response) throws Exception
	{
		String path = request.getPath().getPath();
	
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
					response.setValue(KrtkUtil.HEADER_CONNECTION, "close");
					String contentType = KrtkUtil.resolveMimeType(safePath);
					response.setValue(KrtkUtil.HEADER_CONTENT_TYPE, (contentType == null ? "application/octetstream" : contentType));
					response.setCode(KrtkUtil.STATUS_OK);
					os = response.getOutputStream();
					IOUtils.copy(is, os);
					return;
				}
			}
			catch(IOException ioe)
			{
				if(ioe.getMessage().toLowerCase().startsWith("file not found"))
				{
					response.setCode(KrtkUtil.STATUS_NOT_FOUND);
				}
				else
				{
					response.setCode(KrtkUtil.STATUS_NO_CONTENT);
				}
				return;
			}
			catch(Exception xe)
			{
				LOG.error(xe.getMessage(), xe);
				response.setCode(KrtkUtil.STATUS_SERVICE_UNAVAILABLE);
				return;
			}
			finally
			{
				IOUtils.closeQuietly(is);
				IOUtils.closeQuietly(os);
			}
		}
		response.setCode(KrtkUtil.STATUS_NOT_FOUND);
	}
	
}
