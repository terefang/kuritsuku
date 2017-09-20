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

import com.google.common.io.Files;

import terefang.krtk.KrtkEnv;
import terefang.krtk.KrtkUtil;
import terefang.krtk.RequestContext;
import terefang.krtk.provider.ActionProvider;
import terefang.krtk.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by n500579 on 4/11/17.
 */
public class FileContentAction extends AbstractContentAction
{
	File file = null;

	@Override
	public void executeAction(ActionProvider actionProvider)
	{
		super.executeAction(actionProvider);
		
		RequestContext ctx = KrtkEnv.getContext();

		if(this.file != null && this.file.exists() && this.file.isFile())
		{
			ctx.setResponseBodyLength(file.length());
			OutputStream out = null;
			try
			{
				String mimeType = KrtkUtil.resolveMimeType(this.file.getName());
				if(mimeType!=null)
				{
					ctx.setResponseBodyContentType(mimeType);
				}
				else
				{
					ctx.setResponseBodyContentType("application/octetstream");
				}
				ctx.setResponseStatus(KrtkUtil.STATUS_OK, "OK");
				out = ctx.getResponseBodyOutputStream();
				out.write(Files.toByteArray(this.file));
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
	}

	public File getFile()
	{
		return file;
	}

	public FileContentAction setFile(File file)
	{
		this.file = file;
		return this;
	}
}
