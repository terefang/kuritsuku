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

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.ClassAnnotationMatchProcessor;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.MethodAnnotationMatchProcessor;

import terefang.krtk.KrtkEnv;
import terefang.krtk.annotation.Path;
import terefang.krtk.annotation.PathPrefix;
import terefang.krtk.provider.PageProvider;
import terefang.krtk.KrtkUtil;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by fredo on 14.04.17.
 */
public class SimplePageProvider implements PageProvider
{
	public static final String SCAN_PATH_KEY = SimplePageProvider.class.getSimpleName()+".SCAN_PATH";
	public static final String SCAN_VERBOSE_KEY = SimplePageProvider.class.getSimpleName()+".SCAN_VERBOSE";
	
	Map<String, Class> pathToClassList = new HashMap();
	Map<String, Class> prefixToClassList = new HashMap();
	Map<String, Method> methodToFunctionList = new HashMap();
	Map<String, Method> prefixToFunctionList = new HashMap();
	private List<String> methodPrefixes;
	private List<String> classPrefixes;
	
	public void init(Properties properties)
	{
		boolean scanVerbose = Boolean.parseBoolean(properties.getProperty(SCAN_VERBOSE_KEY, "false"));

		String scanPath = properties.getProperty(SCAN_PATH_KEY, SimplePageProvider.class.getPackage().getName());
		
		FastClasspathScanner scanner = new FastClasspathScanner(scanPath);
		scanner.enableFieldAnnotationIndexing()
				.enableFieldInfo()
				.enableFieldTypeIndexing()
				.enableMethodAnnotationIndexing()
				.enableMethodInfo()
				.matchClassesWithAnnotation(Path.class, new ClassAnnotationMatchProcessor()
				{
					public void processMatch(Class<?> aClass)
					{
						Path cp = aClass.getAnnotation(Path.class);
						String path = KrtkUtil.normalizePath(cp, null, null, null, (cp.applyActionSuffix() ? KrtkEnv.getActionSuffix() : null));
						SimplePageProvider.this.pathToClassList.put(path, aClass);
						//SimplePageProvider.this.log("found "+aClass.getCanonicalName()+" with path "+path);
					}
				})
				.matchClassesWithAnnotation(PathPrefix.class, new ClassAnnotationMatchProcessor()
				{
					public void processMatch(Class<?> aClass)
					{
						PathPrefix cp = aClass.getAnnotation(PathPrefix.class);
						String path = cp.value();
						if(path.endsWith("/*"))
						{
							path = path.substring(0, path.length()-1);
						}
						path = KrtkUtil.normalizePath(path, null, null, null, null);
						SimplePageProvider.this.prefixToClassList.put(path, aClass);
						//SimplePageProvider.this.log("found "+aClass.getCanonicalName()+" with path "+path);
					}
				})
				.matchClassesWithMethodAnnotation(Path.class, new MethodAnnotationMatchProcessor()
				{
					public void processMatch(Class<?> aClass, Method method)
					{
						Path cp = method.getAnnotation(Path.class);
						String path = KrtkUtil.normalizePath(aClass, method, (cp.applyActionSuffix() ? KrtkEnv.getActionSuffix() : null));
						SimplePageProvider.this.methodToFunctionList.put(path, method);
						//SimplePageProvider.this.log("found "+aClass.getCanonicalName()+"#"+method.getName()+" with path "+path);
					}
				})
				.matchClassesWithMethodAnnotation(PathPrefix.class, new MethodAnnotationMatchProcessor()
				{
					
					public void processMatch(Class<?> aClass, Method method)
					{
						Path cp = aClass.getAnnotation(Path.class);
						PathPrefix mp = method.getAnnotation(PathPrefix.class);
						String mpath = mp.value();
						if(mpath.endsWith("/*"))
						{
							mpath = mpath.substring(0, mpath.length()-1);
						}
						if(mpath.startsWith("./"))
						{
							mpath = cp.value()+mpath.substring(1);
						}
						String path = KrtkUtil.normalizePath(cp.value(), null, mpath, null, null);
						SimplePageProvider.this.prefixToFunctionList.put(path, method);
						//SimplePageProvider.this.log("found "+aClass.getCanonicalName()+"#"+method.getName()+" with path "+path);
					}
				})
				.verbose(scanVerbose);
		
		scanner.scan();
		
		this.methodPrefixes = Arrays.asList(this.prefixToFunctionList.keySet().toArray(new String[0]));
		Collections.sort(this.methodPrefixes, new Comparator<String>()
		{
			public int compare(String o1, String o2)
			{
				return o2.length()-o1.length();
			}
		});
		
		this.classPrefixes = Arrays.asList(this.prefixToClassList.keySet().toArray(new String[0]));
		Collections.sort(this.classPrefixes, new Comparator<String>()
		{
			public int compare(String o1, String o2)
			{
				return o2.length()-o1.length();
			}
		});
	}
	
	public Method resolveInvokableMethod(String path)
	{
		for(String prefix : this.methodPrefixes)
		{
			if(path.startsWith(prefix))
			{
				return this.prefixToFunctionList.get(prefix);
			}
		}
		if(this.methodToFunctionList.containsKey(path))
		{
			return this.methodToFunctionList.get(path);
		}
		return null;
	}
	
	public Class resolveInvokableClass(String path)
	{
		for(String prefix : this.classPrefixes)
		{
			if(path.startsWith(prefix))
			{
				return this.prefixToClassList.get(prefix);
			}
		}
		if(this.pathToClassList.containsKey(path))
		{
			return this.pathToClassList.get(path);
		}
		return null;
	}
	
	public Object createClass(String path, Class<?> clazz) throws IllegalAccessException, InstantiationException
	{
		Object ret = clazz.newInstance();

		return ret;
	}
}
