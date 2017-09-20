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
package terefang.krtk.freemarker;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;

import terefang.krtk.KrtkEnv;
import terefang.krtk.RequestContext;
import terefang.krtk.provider.TemplateProvider;
import terefang.krtk.util.LruHashMap;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by fredo on 29.04.17.
 */
public class FreemarkerTemplateProvider implements TemplateProvider, TemplateLoader
{
	private final String inputCharset;
	private final String templateSuffix;
	private final String layoutTemplate;
	private final String contentType;
	private Configuration configuration;
	private LruHashMap<String, Template> cache = new LruHashMap(2000);
	
	public FreemarkerTemplateProvider(Properties properties)
	{
		this.properties.putAll(properties);
		
		this.inputCharset = properties.getProperty(FreemarkerTemplateProvider.class.getSimpleName()+".CHARSET", "UTF-8");
				
		this.templateSuffix = properties.getProperty(FreemarkerTemplateProvider.class.getSimpleName()+".TEMPLATE_SUFFIX", ".ftl");
		
		this.layoutTemplate = properties.getProperty(FreemarkerTemplateProvider.class.getSimpleName()+".LAYOUT_TEMPLATE", null);

		this.contentType = properties.getProperty(FreemarkerTemplateProvider.class.getSimpleName()+".CONTENT_TYPE", "text/html");
	}

	public Properties properties = new Properties();
	
	public boolean executeTemplate(final String path, final RequestContext requestContext)
	{
		this.initEngine(requestContext);
		if(path.endsWith(this.templateSuffix))
		{
			try
			{
				Map<String, Object> context = this.prepareContext(path, requestContext);
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
					template.process(context, swriter);
					swriter.flush();
					swriter.close();
					
					context.put("screen_content", swriter.toString());
					
					template = this.prepareTemplate(this.layoutTemplate, requestContext);
				}
				writer = requestContext.getResponseBodyWriter();
				template.process(context, writer);
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
		if(this.configuration==null)
		{
			this.configuration = new Configuration();
			this.configuration.setTemplateLoader(this);
			this.configuration.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
			this.configuration.setDefaultEncoding(this.inputCharset);
			this.configuration.setOutputEncoding(this.inputCharset);
		}
	}
	
	Map<String, Object> prepareContext(String path, RequestContext requestContext)
	{
		Map<String, Object> context = new HashMap();
		for(Map.Entry<String, Object> entry : requestContext.getContextAttributes().entrySet())
		{
			context.put(entry.getKey(), entry.getValue());
		}
		
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
			
			Template template = this.configuration.getTemplate(path);
			this.cache.put(path, template);
			
			return template;
		}
		catch(Exception xe)
		{
			requestContext.log(xe);
		}
		return null;
	}
	
	@Override
	public Object findTemplateSource(String s) throws IOException
	{
		URL url = KrtkEnv.getContext().getResource(s);
		if(url!=null)
		{
			return new URLTemplateSource(url, null);
		}
		return null;
	}
	
	@Override
	public long getLastModified(Object o)
	{
		return ((URLTemplateSource)o).lastModified();
	}
	
	@Override
	public Reader getReader(Object o, String s) throws IOException
	{
		return new InputStreamReader(((URLTemplateSource)o).getInputStream(), s);
	}
	
	@Override
	public void closeTemplateSource(Object o) throws IOException
	{
		((URLTemplateSource)o).close();
	}
	
	class URLTemplateSource {
		private final URL url;
		private URLConnection conn;
		private InputStream inputStream;
		private Boolean useCaches;
		
		/**
		 * @param useCaches {@code null} if this aspect wasn't set in the parent {@link TemplateLoader}.
		 */
		URLTemplateSource(URL url, Boolean useCaches) throws IOException {
			this.url = url;
			this.conn = url.openConnection();
			this.useCaches = useCaches;
			if (useCaches != null) {
				conn.setUseCaches(useCaches.booleanValue());
			}
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof URLTemplateSource) {
				return url.equals(((URLTemplateSource) o).url);
			} else {
				return false;
			}
		}
		
		@Override
		public int hashCode() {
			return url.hashCode();
		}
		
		@Override
		public String toString() {
			return url.toString();
		}
		
		long lastModified() {
			if (conn instanceof JarURLConnection) {
				// There is a bug in sun's jar url connection that causes file handle leaks when calling getLastModified()
				// (see https://bugs.openjdk.java.net/browse/JDK-6956385).
				// Since the time stamps of jar file contents can't vary independent from the jar file timestamp, just use
				// the jar file timestamp
				URL jarURL = ((JarURLConnection) conn).getJarFileURL();
				if (jarURL.getProtocol().equals("file")) {
					// Return the last modified time of the underlying file - saves some opening and closing
					return new File(jarURL.getFile()).lastModified();
				} else {
					// Use the URL mechanism
					URLConnection jarConn = null;
					try {
						jarConn = jarURL.openConnection();
						return jarConn.getLastModified();
					} catch (IOException e) {
						return -1;
					} finally {
						try {
							if (jarConn != null) jarConn.getInputStream().close();
						} catch (IOException e) { }
					}
				}
			} else {
				long lastModified = conn.getLastModified();
				if (lastModified == -1L && url.getProtocol().equals("file")) {
					// Hack for obtaining accurate last modified time for
					// URLs that point to the local file system. This is fixed
					// in JDK 1.4, but prior JDKs returns -1 for file:// URLs.
					return new File(url.getFile()).lastModified();
				} else {
					return lastModified;
				}
			}
		}
		
		InputStream getInputStream() throws IOException {
			if (inputStream != null) {
				// Ensure that the returned InputStream reads from the beginning of the resource when getInputStream()
				// is called for the second time:
				try {
					inputStream.close();
				} catch (IOException e) {
					// Ignore; this is maybe because it was closed for the 2nd time now
				}
				this.conn = url.openConnection();
			}
			inputStream = conn.getInputStream();
			return inputStream;
		}
		
		void close() throws IOException {
			try {
				if (inputStream != null) {
					inputStream.close();
				} else {
					conn.getInputStream().close();
				}
			} finally {
				inputStream = null;
				conn = null;
			}
		}
		
		Boolean getUseCaches() {
			return useCaches;
		}
		
		void setUseCaches(boolean useCaches) {
			if (this.conn != null) {
				conn.setUseCaches(useCaches);
				this.useCaches = Boolean.valueOf(useCaches);
			}
		}
		
	}
}
