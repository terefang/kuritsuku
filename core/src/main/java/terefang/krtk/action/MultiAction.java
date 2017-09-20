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

import terefang.krtk.ResponseAction;
import terefang.krtk.provider.ActionProvider;

import java.util.List;
import java.util.Vector;

/**
 * Created by fredo on 11.04.17.
 */
public class MultiAction implements ResponseAction
{
	List<ResponseAction> responses = new Vector();
	
	public void executeAction(ActionProvider actionProvider)
	{
		for(ResponseAction ra : responses)
		{
			ra.executeAction(actionProvider);
		}
	}
	
	public List<ResponseAction> getResponses()
	{
		return responses;
	}
	
	public MultiAction setResponses(List<ResponseAction> responses)
	{
		this.responses = responses;
		return this;
	}
	
	public MultiAction addResponse(ResponseAction action)
	{
		this.responses.add(action);
		return this;
	}
}
