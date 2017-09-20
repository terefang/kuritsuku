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

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by fredo on 15.04.17.
 */
public interface ResponseBodyProvider
{
	public void setResponseBodyContentType(String type);
	
	public void setResponseBodyCharset(String charset);
	
	public String getResponseBodyCharset();
	
	public void setResponseBodyLength(long len);
	
	public OutputStream getResponseBodyOutputStream();
	
	public default Writer getResponseBodyWriter() throws UnsupportedEncodingException
	{
		String charset = this.getResponseBodyCharset();
		if(charset==null)
		{
			charset = Charset.defaultCharset().name();
		}
		return new OutputStreamWriter(this.getResponseBodyOutputStream(), charset);
	}
}
