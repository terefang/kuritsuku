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
package terefang.krtk.provider;

import terefang.krtk.RequestContext;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Created by fredo on 15.04.17.
 */
public interface RequestContextProvider
{
	public Map<String,Object> getContextAttributes();
	public Object getContextAttribute(String attributeName);
	public void setContextAttribute(String attributeName, Object value);
	
	public Map<String,Object> getAttributes();
	public Object getAttribute(String attributeName);
	public void setAttribute(String attributeName, Object value);
	
	public String getMethod();
	public String getProtocol();
	public String getPath();
	public String getContextPath();
	
	public RequestContext getContext();
	
	public URL getResource(String path);
	public Reader getResourceReader(String path, Charset charset) throws FileNotFoundException;
	public InputStream getResourceStream(String path) throws FileNotFoundException;
}
