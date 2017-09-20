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
package terefang.krtk.provider.simple;

import terefang.krtk.provider.PageProvider;
import terefang.krtk.provider.PageProviderFactory;

import java.util.Properties;

/**
 * Created by fredo on 14.04.17.
 */
public class SimplePageProviderFactory implements PageProviderFactory
{
	public PageProvider resolveProvider(Properties properties)
	{
		SimplePageProvider simplePageProvider = new SimplePageProvider();
		simplePageProvider.init(properties);
		return simplePageProvider;
	}
}