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
package terefang.krtk.util;

import terefang.krtk.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.util.Properties;

/**
 * Created by fredo on 30.04.17.
 */
public class KrtkFileTypeDetector extends FileTypeDetector
{
	static Properties fileTypes = new Properties();
	static {
		InputStream inputStream = KrtkFileTypeDetector.class.getClassLoader().getResourceAsStream(KrtkFileTypeDetector.class.getSimpleName()+".properties");
		try
		{
			if(inputStream!=null)
			{
				KrtkFileTypeDetector.fileTypes.load(inputStream);
			}
		}
		catch(Exception xe)
		{
			xe.printStackTrace();
		}
		IOUtils.closeQuietly(inputStream);
	}
	public KrtkFileTypeDetector()
	{
		super();
	}
	
	@Override
	public String probeContentType(Path path) throws IOException
	{
		String fileName = path.toFile().getName().toLowerCase().trim();
		return resolveMimeType(fileName);
	}
	
	public static String resolveMimeType(String fileName)
	{
		if(fileName.contains("."))
		{
			String extName = fileName.substring(fileName.lastIndexOf('.'));
			if(KrtkFileTypeDetector.fileTypes.containsKey(extName))
			{
				return KrtkFileTypeDetector.fileTypes.getProperty(extName).trim();
			}
		}
		return null;
	}
}
