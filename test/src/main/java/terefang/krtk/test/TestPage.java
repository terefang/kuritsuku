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
package terefang.krtk.test;

import terefang.krtk.*;
import terefang.krtk.action.TemplateAction;
import terefang.krtk.annotation.*;

import java.io.File;
import java.util.*;

/**
 * Created by fredo on 11.04.17.
 */
@Path(value="/krtk/", applyActionSuffix = true)
@ResponseTemplate("/template.vhtml")
public class TestPage implements RequestHandler
{
	public TestPage() {}
	
	@BindParam("t")
	public String test = null;
	
	@ResponseParam //@ResponseParam("*")
	public Map<String, Object> responseMap = new HashMap();
	
	@BindParam //@BindParam("*")
	public Map<String, String> parameterMap = null;
	
	@BindPart //@BindPart("*")
	public Collection<RequestBodyPart> partList = null;
	
	public ResponseAction handleRequest()
	{
		return KrtkUtil.body(""+(new Date()), "text/plain", "UTF-8");
	}
	
	@Path("./index")
	public ResponseAction doGet()
	{
		return KrtkUtil.forward("/index.html");
	}
	
	@Path("/krtk/redirect")
	public ResponseAction doRedirect()
	{
		return KrtkUtil.redirect("/index.html" + (test != null ? "?" + test : ""));
	}

	@Path("./test")
	public ResponseAction doTest()
	{
		return KrtkUtil.redirect("/index.html?Test");
	}
	
	@Path("./file")
	public ResponseAction doFile()
	{
		return KrtkUtil.file(new File("/etc/hosts"), "hosts.txt", ResponseAction.TEXT);
	}
	
	@Path("./class")
	public ResponseAction doClassRedirect()
	{
		return KrtkUtil.redirect(TestPage.class);
	}
	
	@Path("./class/forward")
	public ResponseAction doClassForward()
	{
		return KrtkUtil.forward(TestPage.class);
	}
	
	@Path("./default")
	public ResponseAction doDefault()
	{
		this.responseMap.put("testVar","Hola Mundo! - "+new Date());
		return ((TemplateAction)KrtkUtil.defaultTemplate()).addParam("hello", "world!");
	}
	
	@Path("./context")
	public ResponseAction doContext(@BindContext RequestContext context)
	{
		return KrtkUtil.body("req="+context, "text/plain", "UTF-8");
	}

	@Path("./param")
	public ResponseAction doParam1(@BindParam("q") String qString, @BindParam/*("*")*/Map pMap)
	{
		return KrtkUtil.body("q="+qString+" *="+pMap, "text/plain", "UTF-8");
	}
	
	@Path("./template")
	@ResponseTemplate("/t2.ftl")
	public ResponseAction doTemplate()
	{
		return ((TemplateAction)KrtkUtil.defaultTemplate()).addParam("thatis", "ftl!");
	}
	
	@PathPrefix("/krtk/prefix/*")
	public ResponseAction doPrefix(@BindContext RequestContext context)
	{
		return KrtkUtil.body("hello prefix: "+context.getPath(), "text/plain", "UTF-8");
	}
	
	@PathPrefix("./prefix2/*")
	public ResponseAction doPrefix2(@BindContext RequestContext context)
	{
		return KrtkUtil.body("hello prefix2: "+context.getPath(), "text/plain", "UTF-8");
	}
}
