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

import terefang.krtk.KrtkEnv;
import terefang.krtk.KrtkUtil;
import terefang.krtk.RequestContext;
import terefang.krtk.provider.ActionProvider;
import terefang.krtk.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by n500579 on 4/11/17.
 */
public class BytesContentAction extends AbstractContentAction
{
	byte[] body = null;

	@Override
	public void executeAction(ActionProvider actionProvider)
	{
		super.executeAction(actionProvider);
		
		RequestContext ctx = KrtkEnv.getContext();

		if(this.body != null)
		{
			ctx.setResponseBodyLength(this.body.length);
			OutputStream out = null;
			try
			{
				ctx.setResponseStatus(KrtkUtil.STATUS_OK, "OK");
				out = ctx.getResponseBodyOutputStream();
				out.write(this.body);
				out.flush();
			}
			catch (IOException e)
			{
				KrtkEnv.getContext().log(e.getMessage(), e);
			}
			finally
			{
				IOUtils.closeQuietly(out);
			}
		}
		else
		{
			ctx.setResponseBodyLength(0L);
		}
	}

	public byte[] getBody()
	{
		return this.body;
	}

	public BytesContentAction setBody(byte[] body)
	{
		this.body = body;
		return this;
	}
}
