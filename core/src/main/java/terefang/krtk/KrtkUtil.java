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

import com.google.common.base.Strings;
import terefang.krtk.ResponseAction;
import terefang.krtk.action.*;
import terefang.krtk.annotation.CSRF;
import terefang.krtk.annotation.Path;
import terefang.krtk.util.KrtkFileTypeDetector;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fredo on 11.04.17.
 */
public class KrtkUtil
{
	public static final String ACTION_SUFFIX_KEY = "Krtk.ACTION_SUFFIX";
	public static final String ACTION_PREFIX_KEY = "Krtk.ACTION_PREFIX";
	public static final String TEMPLATE_SUFFIX_KEY = "Krtk.TEMPLATE_SUFFIX";
	public static final String RESOURCE_PATH_KEY = "Krtk.RESOURCE_PATH";
	
	public static final String MEDIA_TYPE_X_WWW_FORM = "application/x-www-form-urlencoded";
	public static final String MEDIA_TYPE_TEXT_XML = "text/xml";
	public static final String MEDIA_TYPE_MULTIPART_RELATED = "multipart/related";
	public static final String MEDIA_TYPE_MULTIPART_FORM_DATA = "multipart/form-data";
	public static final String MEDIA_TYPE_APPLICATION_XML = "application/xml";
	public static final String MEDIA_TYPE_APPLICATION_SOAP_XML = "application/soap+xml";
	public static final String MEDIA_TYPE_APPLICATION_ECHO_XML = "application/echo+xml";
	
	public static final String HEADER_USER_AGENT = "User-Agent";
	
	public static final String HEADER_TRANSFER_ENCODING_CHUNKED = "chunked".intern();
	public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding".intern();
	
	public static final String HEADER_SOAP_ACTION = "SOAPAction";
	public static final String HEADER_SET_COOKIE2 = "Set-Cookie2";
	public static final String HEADER_SET_COOKIE = "Set-Cookie";
	public static final String HEADER_PROXY_AUTHORIZATION = "Proxy-Authorization";
	
	public static final String HEADER_PROTOCOL_11 = "HTTP/1.1";
	
	public static final String HEADER_PROTOCOL_10 = "HTTP/1.0";
	
	public static final String HEADER_PRAGMA = "Pragma";
	
	public static final String HEADER_POST = "POST";
	
	public static final String HEADER_PUT = "PUT";
	
	public static final String HEADER_LOCATION = "Location";
	
	public static final String HEADER_HOST = "Host";
	
	public static final String HEADER_GET = "GET";
	
	public static final String HEADER_DELETE = "DELETE";
	
	public static final String HEADER_EXPECT_100_Continue = "100-continue";
	
	public static final String HEADER_EXPECT = "Expect";
	public static final String HEADER_DEFAULT_CHAR_ENCODING = "iso-8859-1";
	
	public static final String HEADER_COOKIE2 = "Cookie2";
	public static final String HEADER_COOKIE = "Cookie";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
	public static final String HEADER_CONTENT_LOCATION = "Content-Location";
	public static final String HEADER_CONTENT_LENGTH = "Content-Length";
	public static final String HEADER_CONTENT_ID = "Content-Id";
	public static final String HEADER_CONTENT_DESCRIPTION = "Content-Description";
	public static final String HEADER_CONNECTION_KEEPALIVE = "Keep-Alive".intern();
	public static final String HEADER_CONNECTION_CLOSE = "close".intern();
	public static final String HEADER_CONNECTION = "Connection";
	public static final String HEADER_CACHE_CONTROL_NOCACHE = "no-cache";
	public static final String HEADER_CACHE_CONTROL = "Cache-Control";
	public static final String HEADER_AUTHORIZATION = "Authorization";
	
	public static final String HEADER_ACCEPT_TEXT_ALL = "text/*";
	public static final String HEADER_ACCEPT_MULTIPART_RELATED = "multipart/related";
	public static final String HEADER_ACCEPT = "Accept";
	public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
	public static final String HEADER_REFERER = "Referer";
	
	public static String CHAR_SET_ENCODING = "charset";
	public static final String HTTP_ELEMENT_CHARSET = "US-ASCII";
	
	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
	public static final String HEADER_CONTENT_ENCODING_LOWERCASE =
			HEADER_CONTENT_ENCODING.toLowerCase();
	
	public static final String COMPRESSION_GZIP = "gzip";
	
	public static final String HTTP_HEADERS = "HTTP_HEADERS";
	public static final String COOKIE_STRING = "Cookie";
	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_METHOD_POST = "POST";
	
	public static final String CONTENT_TYPE = "Content-Type";
	
	
	public static final int STATUS_OK = 200;
	public static final int STATUS_CREATED = 201;
	public static final int STATUS_ACCEPTED = 202;
	public static final int STATUS_NO_CONTENT = 204;
	public static final int STATUS_MOVED_PERMANENTLY = 301;
	public static final int STATUS_MOVED_TEMPORARELY = 302;
	public static final int STATUS_SEE_OTHER = 303;
	public static final int STATUS_NOT_MODIFIED = 304;
	public static final int STATUS_TEMPORARY_REDIRECT = 307;
	public static final int STATUS_BAD_REQUEST = 400;
	public static final int STATUS_UNAUTHORIZED = 401;
	public static final int STATUS_FORBIDDEN = 403;
	public static final int STATUS_NOT_FOUND = 404;
	public static final int STATUS_NOT_ACCEPTABLE = 406;
	public static final int STATUS_CONFLICT = 409;
	public static final int STATUS_GONE = 410;
	public static final int STATUS_PRECONDITION_FAILED = 412;
	public static final int STATUS_UNSUPPORTED_MEDIA_TYPE = 415;
	public static final int STATUS_INTERNAL_SERVER_ERROR = 500;
	public static final int STATUS_SERVICE_UNAVAILABLE = 503;
	
	public static Reader resolveResourceReader(String resourcePath, ClassLoader classLoader, String filePath, Charset charset) throws IOException
	{
		return new InputStreamReader(resolveResourceStream(resourcePath, classLoader, filePath), charset);
	}
	
	public static InputStream resolveResourceStream(String resourcePath, ClassLoader classLoader, String filePath) throws IOException
	{
		URL url = resolveResource(resourcePath, classLoader, filePath);
		if(url==null)
		{
			throw new IOException("File not found: "+filePath);
		}
		return url.openStream();
	}
	
	public static URL resolveResource(String resourcePath, ClassLoader classLoader, String filePath)
	{
		if(filePath.startsWith("/"))
		{
			filePath=filePath.substring(1);
		}
		
		for(String prefix : new String[]{"/META-INF/resources/", "/WEB-INF/resources/", "/resources/", "/"})
		{
			try
			{
				URL url = classLoader.getResource(prefix + filePath);
				
				if(url!=null)
				{
					return url;
				}
			}
			catch(Exception xe)
			{
				// ignore
			}
			
			try
			{
				File file = new File(resourcePath, prefix+filePath);
				if(file.isFile() && file.canRead())
				{
					return file.toURL();
				}
			}
			catch(Exception xe)
			{
				// ignore
			}
		}
		return null;
	}
	
	public static String resolveMimeType(String fileName)
	{
		return KrtkFileTypeDetector.resolveMimeType(fileName);
	}
	
	public static ResponseAction template(String path, Map<String,Object> context)
	{
		return new TemplateAction().setContext(context).setForwardPath(path);
	}
	
	public static ResponseAction defaultTemplate(Map<String,Object> context)
	{
		return new TemplateAction().setContext(context);
	}
	
	public static ResponseAction defaultTemplate()
	{
		return new TemplateAction();
	}
	
	public static ResponseAction forward(String path)
	{
		return new ForwardAction().setForwardPath(path);
	}
	
	public static ResponseAction forward(Class<?> clazz)
	{
		return new ForwardAction().setForwardPath(clazz);
	}
	
	public static ResponseAction redirect(String location)
	{
		return new RedirectAction().setRedirectLocation(location);
	}
	
	public static ResponseAction redirect(Class<?> clazz)
	{
		return new RedirectAction().setRedirectLocation(clazz);
	}
	
	public static ResponseAction body(String body, String contentType, String charset)
	{
		return new StringContentAction().setBody(body).setContentType(contentType).setContentCharset(charset);
	}
	
	public static ResponseAction file(byte[] fileBody, String attachment, String contentType)
	{
		return new BytesContentAction().setBody(fileBody).setContentType(contentType).setAttachment(attachment);
	}
	
	public static ResponseAction file(File file, String attachment, String contentType)
	{
		return new FileContentAction().setFile(file).setContentType(contentType).setAttachment(attachment);
	}
	
	public static ResponseAction error(int code, String content)
	{
		return ErrorAction.create(code, content);
	}
	
	public static String normalizePath(Class<?> clazz, Method method, String suffix)
	{
		Path pathToClass = clazz.getAnnotation(Path.class);
		Path pathToMethod = (method==null? null : method.getAnnotation(Path.class));
		return normalizePath((pathToClass==null ? null : pathToClass.value()), clazz, (pathToMethod==null ? null : pathToMethod.value()), method, suffix);
	}
	
	public static String normalizePath(Path pathToClass, Class<?> clazz, Path pathToMethod, Method method, String suffix)
	{
		return normalizePath((pathToClass==null ? null : pathToClass.value()), clazz, (pathToMethod==null ? null : pathToMethod.value()), method, suffix);
	}
	
	public static String normalizePath(String pathToClass, Class<?> clazz, String pathToMethod, Method method, String suffix)
	{
		StringBuilder sb = new StringBuilder();
		if(pathToClass!=null)
		{
			if(!pathToClass.startsWith("/"))
			{
				sb.append("/");
			}
			sb.append(pathToClass);
		}
		else
		{
			sb.append("/"+clazz.getCanonicalName());
		}
		
		if(pathToMethod!=null)
		{
			if(pathToMethod.startsWith("/"))
			{
				sb.setLength(0);
			}
			else
			{
				sb.append("/");
			}
			sb.append(pathToMethod);
		}
		else
		if(method!=null)
		{
			sb.append("/"+method.getName());
		}
		
		String path = sb.toString().replaceAll("//+", "/").replaceAll("/\\./", "/");
		
		if(path.endsWith("/"))
		{
			path = path.substring(0,path.length()-1);
		}

		if(suffix!=null
				&& !path.endsWith(suffix)
				&& (clazz==null || clazz.getAnnotation(Path.class)==null || clazz.getAnnotation(Path.class).applyActionSuffix())
				&& (method==null || method.getAnnotation(Path.class)==null || method.getAnnotation(Path.class).applyActionSuffix()))
		{
			path += suffix;
		}
		return path;
	}
	
	public static String normalizePath(String path)
	{
		return normalizePath(path, null,null, null, null);
	}
	
	public static void checkCSRF(Class<?> clazz) throws IllegalAccessException
	{
		checkCSRF(clazz.getAnnotation(CSRF.class));
	}
	
	public static void checkCSRF(Method method) throws IllegalAccessException
	{
		Class<?> clazz = method.getDeclaringClass();
		checkCSRF(clazz.getAnnotation(CSRF.class));
		checkCSRF(method.getAnnotation(CSRF.class));
	}
	
	public static void checkCSRF(CSRF _csrf) throws IllegalAccessException
	{
		if(_csrf!=null && _csrf.verifyIncoming())
		{
			String token = KrtkEnv.getContext().getRequestHeader(CsrfToken.HTTP_CSRF_HEADER_NAME);
			if(token==null)
			{
				token = KrtkEnv.getContext().getRequestParam(CsrfToken.HTTP_CSRF_PARAMETER_NAME);
			}
			
			if(token==null)
			{
				throw new IllegalAccessException("csrf not valid");
			}

			CsrfToken vtoken = getCsrfToken(token);
			if(vtoken!=null)
			{
				throw new IllegalAccessException("csrf not valid");
			}

			if(!Strings.isNullOrEmpty(_csrf.value()) && !_csrf.value().equalsIgnoreCase(vtoken.domain))
			{
				throw new IllegalAccessException("csrf not valid");
			}
			
			if(vtoken.validTo<System.currentTimeMillis())
			{
				throw new IllegalAccessException("csrf not valid");
			}
		}

		if(_csrf!=null && _csrf.includeOutgoing())
		{
			CsrfToken token = Strings.isNullOrEmpty(_csrf.value()) ? createCsrfToken() : createCsrfToken(_csrf.value());
			KrtkEnv.getContext().setResponseParam("_csrf", token);
			KrtkEnv.getContext().setResponseHeader(token.headerName, token.token);
		}
	}
	
	public static Map<String, CsrfToken> getCsrfTokens()
	{
		Map<String, CsrfToken> csrfToken = (Map<String, CsrfToken>) KrtkEnv.getContext().getContextAttribute(CsrfToken.HTTP_CSRF_PARAMETER_NAME);
		if(csrfToken==null)
		{
			csrfToken = new HashMap();
			KrtkEnv.getContext().setContextAttribute(CsrfToken.HTTP_CSRF_PARAMETER_NAME, csrfToken);
		}
		
		synchronized(csrfToken)
		{
			long tm = System.currentTimeMillis();
			for(CsrfToken t : csrfToken.values())
			{
				if(t.validTo<tm)
				{
					csrfToken.remove(t.token);
				}
			}
		}
		
		return csrfToken;
	}
	
	public static CsrfToken getCsrfToken(String name)
	{
		return getCsrfTokens().get(name);
	}
	
	public static CsrfToken createCsrfToken()
	{
		CsrfToken t = CsrfToken.create();
		getCsrfTokens().put(t.token, t);
		return t;
	}

	public static CsrfToken createCsrfToken(String domain)
	{
		CsrfToken t = CsrfToken.create(domain);
		getCsrfTokens().put(t.token, t);
		return t;
	}
	
}
