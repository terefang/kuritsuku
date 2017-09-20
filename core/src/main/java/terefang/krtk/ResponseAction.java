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

import terefang.krtk.provider.ActionProvider;

/**
 * Created by fredo on 11.04.17.
 */
public interface ResponseAction
{
	/** The plain text content type constant: <tt>text/plain</tt>. */
	public static final String TEXT = "text/plain";

	/** The html content type constant: <tt>text/html</tt>. */
	public static final String HTML = "text/html";

	/** The xhtml content type constant: <tt>application/xhtml+xml</tt>. */
	public static final String XHTML = "application/xhtml+xml";

	/** The json content type constant: <tt>text/json</tt>. */
	public static final String JSON = "application/json";

	/** The javascript content type constant: <tt>text/javascript</tt>. */
	public static final String JAVASCRIPT = "text/javascript";

	/** The xml content type constant: <tt>text/xml</tt>. */
	public static final String XML = "text/xml";

	/** The content type constant: <tt>application/octetstream</tt>. */
	public static final String STREAM = "application/octetstream";

	public void executeAction(ActionProvider actionProvider);
}
