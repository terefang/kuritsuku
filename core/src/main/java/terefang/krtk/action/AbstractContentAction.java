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
package terefang.krtk.action;

import com.google.common.net.HttpHeaders;
import terefang.krtk.KrtkEnv;
import terefang.krtk.RequestContext;
import terefang.krtk.provider.ActionProvider;


/**
 * Created by fredo on 11.04.17.
 */
public abstract class AbstractContentAction extends AbstractResponseAction
{
	String contentType = null;
	String contentCharset = null;
	String attachment = null;

	@Override
	public void executeAction(ActionProvider provider)
	{
		super.executeAction(provider);
		
		RequestContext ctx = KrtkEnv.getContext();
		
		if(this.contentType!=null)
		{
			ctx.setResponseBodyContentType(this.contentType);
		}

		if(this.contentCharset!=null)
		{
			ctx.setResponseBodyCharset(this.contentCharset);
		}

		if(this.attachment!=null)
		{
			ctx.setResponseHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + this.attachment + "\"");
		}
	}

	public String getContentCharset()
	{
		return contentCharset;
	}

	public AbstractContentAction setContentCharset(String contentCharset)
	{
		this.contentCharset = contentCharset;
		return this;
	}

	public String getContentType()
	{
		return contentType;
	}

	public AbstractContentAction setContentType(String contentType)
	{
		this.contentType = contentType;
		return this;
	}

	public String getAttachment()
	{
		return attachment;
	}

	public AbstractContentAction setAttachment(String attachment)
	{
		this.attachment = attachment;
		return this;
	}
}
