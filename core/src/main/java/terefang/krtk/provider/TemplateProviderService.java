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

import java.util.*;

/**
 * Created by fredo on 13.04.17.
 */
public class TemplateProviderService
{
	public static List<TemplateProvider> resolveProvider(Properties initProps)
	{
		List<TemplateProvider> pps = new Vector();
		
		ServiceLoader<TemplateProviderFactory> serviceLoader = ServiceLoader.load(TemplateProviderFactory.class);
		Iterator<TemplateProviderFactory> en = serviceLoader.iterator();
		while(en.hasNext())
		{
			TemplateProvider pv = en.next().resolveProvider(initProps);
			if(pv!=null)
			{
				pps.add(pv);
			}
		}
		return pps;
	}
}
