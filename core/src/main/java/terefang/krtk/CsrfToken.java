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

import java.util.UUID;

public class CsrfToken
{
	public static final String HTTP_CSRF_HEADER_NAME = "X-CSRF-TOKEN";
	public static final String HTTP_CSRF_PARAMETER_NAME = "_CSRF_TOKEN";
	
	public long validTo = System.currentTimeMillis()+30000L;
	public String headerName = HTTP_CSRF_HEADER_NAME;
	public String parameterName = HTTP_CSRF_PARAMETER_NAME;
	public String token = UUID.randomUUID().toString();
	public String domain = null;
	
	public static CsrfToken create()
	{
		return new CsrfToken();
	}

	public static CsrfToken create(String domain)
	{
		CsrfToken token = new CsrfToken();
		token.domain = domain;
		return token;
	}
}
