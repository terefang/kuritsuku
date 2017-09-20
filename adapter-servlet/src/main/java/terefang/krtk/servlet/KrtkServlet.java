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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.io.IOException;

import java.util.*;

import terefang.krtk.KrtkUtil;
import terefang.krtk.RequestContext;
import terefang.krtk.provider.PageProvider;
import terefang.krtk.provider.PageProviderService;
import terefang.krtk.provider.TemplateProvider;
import terefang.krtk.provider.TemplateProviderService;

/**
 * Created by fredo on 10.04.17.
 */
public class KrtkServlet extends HttpServlet
{
	List<PageProvider> pageProvider = null;
	
	List<TemplateProvider> templateProvider = null;
	
	String errorPage = "/Error.vm";
	
	public static final String ACTION_SUFFIX_KEY = KrtkServlet.class.getSimpleName()+".action-suffix";
	public static final String TEMPLATE_SUFFIX_KEY = KrtkServlet.class.getSimpleName()+".template-suffix";
	
	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		
		Properties properties = new Properties();
		Enumeration<String> en = config.getInitParameterNames();
		while(en.hasMoreElements())
		{
			String key = en.nextElement();
			properties.setProperty(key, config.getInitParameter(key));
		}
		
		KrtkServletEnv.setActionSuffix(properties.getProperty(ACTION_SUFFIX_KEY, ".do"));
		KrtkServletEnv.setTemplateSuffix(properties.getProperty(TEMPLATE_SUFFIX_KEY, ".thtml"));
		
		this.pageProvider = PageProviderService.resolveProvider(properties);
		this.templateProvider = TemplateProviderService.resolveProvider(properties);
		
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		RequestContext ctx = ServletRequestContextImpl.create(req, resp, this.pageProvider, this.templateProvider);
		KrtkServletEnv.setContext(ctx);
		
		String lPath = KrtkUtil.normalizePath(ctx.getPath(), null, null, null, KrtkServletEnv.getActionSuffix());
		if(ctx.tryInvoke(lPath))
		{
			return;
		}
		
		KrtkServletEnv.setContext(null);
		req.getRequestDispatcher(this.errorPage).forward(req, resp);
	}
	
}
