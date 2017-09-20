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

public class ErrorAction extends AbstractResponseAction
{
	private int code;
	private String text;
	
	public static ErrorAction create(int code, String text)
	{
		ErrorAction errorAction = new ErrorAction();
		errorAction.code = code;
		errorAction.text = text;
		return errorAction;
	}
	
	@Override
	public void executeAction(ActionProvider provider)
	{
		provider.doError(this.code, this.text);
	}
}
