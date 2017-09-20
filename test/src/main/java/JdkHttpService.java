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

import terefang.krtk.jdkhttp.KrtkJdkHttpServer;

/**
 * Created by fredo on 16.04.17.
 */
public class JdkHttpService
{
	public static void main(String[] argv) throws Exception
	{
		KrtkJdkHttpServer srv = new KrtkJdkHttpServer()
				.setBindAddress("*")
				.setBindPort(8080)
				.setActionPrefix("/krtk/")
				.setActionSuffix(".do")
				.setResourcePath("./test/src/test/webapp")
				.setProperty("SimplePageProvider.SCAN_PATH","terefang.krtk.test")
				.setProperty("SimplePageProvider.SCAN_VERBOSE","true")
				;
		srv.start();
	}
}
