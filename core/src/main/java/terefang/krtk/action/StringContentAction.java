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

import terefang.krtk.KrtkUtil;
import terefang.krtk.RequestContext;
import terefang.krtk.provider.ActionProvider;
import terefang.krtk.KrtkEnv;
import terefang.krtk.io.IOUtils;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by n500579 on 4/11/17.
 */
public class StringContentAction extends AbstractContentAction
{
	String body = null;

	@Override
	public void executeAction(ActionProvider actionProvider)
	{
		super.executeAction(actionProvider);
		
		RequestContext ctx = KrtkEnv.getContext();
		if(this.body != null)
		{
			ctx.setResponseStatus(KrtkUtil.STATUS_OK, "OK");
			PrintWriter out = null;
			try
			{
				out = new PrintWriter(KrtkEnv.getContext().getResponseBodyWriter());
				out.print(this.body);
				out.flush();
			}
			catch (IOException e)
			{
				ctx.log(e.getMessage(), e);
			}
			finally
			{
				IOUtils.closeQuietly(out);
			}
		}
		else
		{
			ctx.setResponseBodyLength(0L);
			ctx.setResponseStatus(KrtkUtil.STATUS_NO_CONTENT, "OK");
		}
	}

	public String getBody()
	{
		return this.body;
	}

	public StringContentAction setBody(String body)
	{
		this.body = body;
		return this;
	}
}
