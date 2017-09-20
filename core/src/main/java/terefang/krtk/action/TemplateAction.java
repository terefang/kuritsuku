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
import terefang.krtk.provider.ActionProvider;

import java.util.Map;

/**
 * Created by fredo on 12.04.17.
 */
public class TemplateAction extends AbstractResponseAction
{
	@Override
	public void executeAction(ActionProvider actionProvider)
	{
		super.executeAction(actionProvider);
		
		actionProvider.doTemplate(forwardPath);
	}
	
	String forwardPath;
	
	public String getForwardPath()
	{
		return forwardPath;
	}
	
	public TemplateAction setForwardPath(String forwardPath)
	{
		this.forwardPath = forwardPath;
		return this;
	}
	
	public TemplateAction setForwardPath(Class<?> clazz)
	{
		this.forwardPath = KrtkUtil.normalizePath(clazz, null, KrtkEnv.getActionSuffix());
		return this;
	}
	
	public TemplateAction setContext(Map<String, Object> context)
	{
		this.getRequestAttributes().putAll(context);
		return this;
	}

	public TemplateAction addParam(String key, Object value)
	{
		this.getRequestAttributes().put(key, value);
		return this;
	}
}
