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
package terefang.krtk.velocity;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.io.VelocityWriter;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import terefang.krtk.KrtkEnv;
import terefang.krtk.RequestContext;
import terefang.krtk.io.IOUtils;
import terefang.krtk.provider.TemplateProvider;
import terefang.krtk.util.LruHashMap;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * Created by fredo on 29.04.17.
 */
public class VelocityTemplateProvider implements TemplateProvider
{
	private final String inputCharset;
	private final String layoutTemplate;
	private final String templateSuffix;
	private final String contentType;
	private LruHashMap<String, Template> cache = new LruHashMap(2000);
	
	public VelocityTemplateProvider(Properties properties)
	{
		this.properties.putAll(properties);

		this.inputCharset = properties.getProperty(VelocityTemplateProvider.class.getSimpleName()+".CHARSET", "UTF-8");
		
		this.templateSuffix = properties.getProperty(VelocityTemplateProvider.class.getSimpleName()+".TEMPLATE_SUFFIX", ".vhtml");

		this.layoutTemplate = properties.getProperty(VelocityTemplateProvider.class.getSimpleName()+".LAYOUT_TEMPLATE", null);
		
		this.contentType = properties.getProperty(VelocityTemplateProvider.class.getSimpleName()+".CONTENT_TYPE", "text/html");
	}

	public Properties properties = new Properties();
	
	public VelocityEngine velocityEngine = null;
	
	
	public boolean executeTemplate(final String path, final RequestContext requestContext)
	{
		this.initEngine(requestContext);
		if(path.endsWith(this.templateSuffix))
		{
			try
			{
				Context context = this.prepareContext(path, requestContext);
				Template template = this.prepareTemplate(path, requestContext);
				Writer writer = null;
				if(this.contentType!=null)
				{
					requestContext.setResponseBodyContentType(this.contentType);
				}
				if(this.inputCharset!=null)
				{
					requestContext.setResponseBodyCharset(this.inputCharset);
				}
				if(this.layoutTemplate!=null)
				{
					StringWriter swriter = new StringWriter();
					writer = new VelocityWriter(swriter, 2048, true);
					template.merge(context, writer);
					writer.flush();
					writer.close();
					
					context.put("screen_content", swriter.toString());
					
					template = this.prepareTemplate(this.layoutTemplate, requestContext);
				}
				writer = requestContext.getResponseBodyWriter();
				template.merge(context, writer);
				writer.flush();
				writer.close();
			}
			catch(Exception xe)
			{
				requestContext.log(xe);
			}
			return true;
		}
		return false;
	}
	
	public synchronized void initEngine(RequestContext requestContext)
	{
		if(this.velocityEngine==null)
		{
			this.velocityEngine = new VelocityEngine();
			Properties properties = getInitProperties();
			
			// Initialize VelocityEngine
			this.velocityEngine.init(properties);
		}
	}
	
	private Properties getInitProperties()
	{
		final Properties velProps = new Properties();
		
		// Set default velocity runtime properties.
		
		velProps.setProperty(RuntimeConstants.RESOURCE_LOADER, "webapp, class");
		velProps.setProperty("webapp.resource.loader.class",
				VtpResourceLoader.class.getName());
		velProps.setProperty("class.resource.loader.class",
				ClasspathResourceLoader.class.getName());
		
		velProps.put("webapp.resource.loader.cache", "true");
		velProps.put("webapp.resource.loader.modificationCheckInterval", "0");
		velProps.put("class.resource.loader.cache", "true");
		velProps.put("class.resource.loader.modificationCheckInterval", "0");
		velProps.put("velocimacro.library.autoreload", "false");
			
		velProps.put("directive.if.tostring.nullcheck", "false");
		
		velProps.put("input.encoding", this.inputCharset);

		return velProps;
	}
	
	Context prepareContext(String path, RequestContext requestContext)
	{
		VelocityContext icontext = new VelocityContext();
		for(Map.Entry<String, Object> entry : requestContext.getContextAttributes().entrySet())
		{
			icontext.put(entry.getKey(), entry.getValue());
		}
		
		VelocityContext context = new VelocityContext(icontext);
		for(Map.Entry<String, Object> entry : requestContext.getAttributes().entrySet())
		{
			context.put(entry.getKey(), entry.getValue());
		}
		return context;
	}
	
	Template prepareTemplate(String path, RequestContext requestContext)
	{
		try
		{
			if(this.cache.containsKey(path))
			{
				return this.cache.get(path);
			}
			
			Template template = this.velocityEngine.getTemplate(path);
			this.cache.put(path, template);
			
			return template;
		}
		catch(Exception xe)
		{
			requestContext.log(xe);
		}
		return null;
	}
	
	public static class VtpResourceLoader extends ResourceLoader
	{
		
		@Override
		public void init(ExtendedProperties extendedProperties)
		{
			return;
		}
		
		@Override
		public InputStream getResourceStream(String s) throws ResourceNotFoundException
		{
			try
			{
				InputStream inputStream = KrtkEnv.getContext().getResourceStream(s);
				if(inputStream!=null)
				{
					return inputStream;
				}
			}
			catch(Exception xe)
			{
				throw new ResourceNotFoundException(xe);
			}
			throw new ResourceNotFoundException("not found "+s);
		}
		
		@Override
		public boolean isSourceModified(Resource resource)
		{
			try
			{
				URL url = KrtkEnv.getContext().getResource(resource.getName());
				File file = new File(url.toURI());
				if(file.lastModified()>resource.getLastModified())
				{
					return true;
				}
			}
			catch(Exception xe)
			{
				// ignore
			}
			return false;
		}
		
		@Override
		public long getLastModified(Resource resource)
		{
			try
			{
				URL url = KrtkEnv.getContext().getResource(resource.getName());
				File file = new File(url.toURI());
				return file.lastModified();
			}
			catch(Exception xe)
			{
				// ignore
			}
			return 0;
		}
	}
}
