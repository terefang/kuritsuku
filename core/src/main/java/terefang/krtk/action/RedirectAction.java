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

import terefang.krtk.provider.ActionProvider;
import terefang.krtk.KrtkUtil;
import terefang.krtk.KrtkEnv;

/**
 * Created by fredo on 11.04.17.
 */
public class RedirectAction extends AbstractResponseAction
{
	String redirectLocation = null;
	
	@Override
	public void executeAction(ActionProvider actionProvider)
	{
		super.executeAction(actionProvider);
		
		if(redirectLocation!=null)
		{
			actionProvider.doRedirect(redirectLocation);
		}
	}
	
	public String getRedirectLocation()
	{
		return redirectLocation;
	}
	
	public RedirectAction setRedirectLocation(String redirectLocation)
	{
		this.redirectLocation = redirectLocation;
		return this;
	}
	
	public RedirectAction setRedirectLocation(Class<?> clazz)
	{
		this.redirectLocation = KrtkUtil.normalizePath(clazz, null, KrtkEnv.getActionSuffix());
		return this;
	}
}
