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

import terefang.krtk.RequestBodyPart;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by fredo on 15.04.17.
 */
public interface RequestBodyProvider
{
	public default String getBodyString(Charset charset)
	{
		return new String(getBodyBytes(), charset);
	}

	public byte[] getBodyBytes();

	public default Reader getBodyReader(Charset charset)
	{
		return new InputStreamReader(getBodyInputStream(), charset);
	}

	public InputStream getBodyInputStream();
	
	public String getBodyCharset();
	
	public String getBodyContentType();
	
	List<RequestBodyPart> getBodyParts();
	
	RequestBodyPart getBodyPart(String pname);
}
