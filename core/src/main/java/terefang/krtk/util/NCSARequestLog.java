/*
 * Copyright (c) 1997-2009 Mort Bay Consulting Pty. Ltd.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import terefang.krtk.KrtkUtil;
import terefang.krtk.RequestContext;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * This {@link RequestLog} implementation outputs logs in the pseudo-standard
 * NCSA common log format. Configuration options allow a choice between the
 * standard Common Log Format (as used in the 3 log format) and the Combined Log
 * Format (single log format). This log format can be output by most web
 * servers, and almost all web log analysis software can understand these
 * formats.
 */

/* ------------------------------------------------------------ */
/**
 */
public class NCSARequestLog
{
	private static final Log LOG = LogFactory.getLog("nsca-request-log");
	
	private boolean _extended;
	private boolean _append;
	private int _retainDays;
	private boolean _preferProxiedForAddress;
	private String _logDateFormat = "yyyy-MM-dd HH:mm:ss Z";
	SimpleDateFormat _sfmt = new SimpleDateFormat(_logDateFormat);
	private Locale _logLocale = Locale.getDefault();
	private String _logTimeZone = "GMT";
	private String[] _ignorePaths;
	private boolean _logLatency = false;
	private boolean _logCookies = false;
	private boolean _logServer = false;
	private boolean _logDispatch = false;
	
    /* ------------------------------------------------------------ */
	/**
	 * Create request log object with default settings.
	 */
	public NCSARequestLog()
	{
		_extended = true;
		_append = true;
		_logLatency = true;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Set the timestamp format for request log entries in the file.
	 * If this is not set, the pre-formated request timestamp is used.
	 *
	 * @param format timestamp format string
	 */
	public void setLogDateFormat(String format)
	{
		_logDateFormat = format;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Retrieve the timestamp format string for request log entries.
	 *
	 * @return timestamp format string.
	 */
	public String getLogDateFormat()
	{
		return _logDateFormat;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Set the locale of the request log.
	 *
	 * @param logLocale locale object
	 */
	public void setLogLocale(Locale logLocale)
	{
		_logLocale = logLocale;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Retrieve the locale of the request log.
	 *
	 * @return locale object
	 */
	public Locale getLogLocale()
	{
		return _logLocale;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Set the timezone of the request log.
	 *
	 * @param tz timezone string
	 */
	public void setLogTimeZone(String tz)
	{
		_logTimeZone = tz;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Retrieve the timezone of the request log.
	 *
	 * @return timezone string
	 */
	public String getLogTimeZone()
	{
		return _logTimeZone;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Set the number of days before rotated log files are deleted.
	 *
	 * @param retainDays number of days to keep a log file
	 */
	public void setRetainDays(int retainDays)
	{
		_retainDays = retainDays;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Retrieve the number of days before rotated log files are deleted.
	 *
	 * @return number of days to keep a log file
	 */
	public int getRetainDays()
	{
		return _retainDays;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Set the extended request log format flag.
	 *
	 * @param extended true - log the extended request information,
	 *                 false - do not log the extended request information
	 */
	public void setExtended(boolean extended)
	{
		_extended = extended;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Retrieve the extended request log format flag.
	 *
	 * @return value of the flag
	 */
	public boolean isExtended()
	{
		return _extended;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Set append to log flag.
	 *
	 * @param append true - request log file will be appended after restart,
	 *               false - request log file will be overwritten after restart
	 */
	public void setAppend(boolean append)
	{
		_append = append;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Retrieve append to log flag.
	 *
	 * @return value of the flag
	 */
	public boolean isAppend()
	{
		return _append;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Set request paths that will not be logged.
	 *
	 * @param ignorePaths array of request paths
	 */
	public void setIgnorePaths(String[] ignorePaths)
	{
		_ignorePaths = ignorePaths;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Retrieve the request paths that will not be logged.
	 *
	 * @return array of request paths
	 */
	public String[] getIgnorePaths()
	{
		return _ignorePaths;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Controls logging of the request cookies.
	 *
	 * @param logCookies true - values of request cookies will be logged,
	 *                   false - values of request cookies will not be logged
	 */
	public void setLogCookies(boolean logCookies)
	{
		_logCookies = logCookies;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Retrieve log cookies flag
	 *
	 * @return value of the flag
	 */
	public boolean getLogCookies()
	{
		return _logCookies;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Controls logging of the request hostname.
	 *
	 * @param logServer true - request hostname will be logged,
	 *                  false - request hostname will not be logged
	 */
	public void setLogServer(boolean logServer)
	{
		_logServer = logServer;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Retrieve log hostname flag.
	 *
	 * @return value of the flag
	 */
	public boolean getLogServer()
	{
		return _logServer;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Controls logging of request processing time.
	 *
	 * @param logLatency true - request processing time will be logged
	 *                   false - request processing time will not be logged
	 */
	public void setLogLatency(boolean logLatency)
	{
		_logLatency = logLatency;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Retrieve log request processing time flag.
	 *
	 * @return value of the flag
	 */
	public boolean getLogLatency()
	{
		return _logLatency;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Controls whether the actual IP address of the connection or
	 * the IP address from the X-Forwarded-For header will be logged.
	 *
	 * @param preferProxiedForAddress true - IP address from header will be logged,
	 *                                false - IP address from the connection will be logged
	 */
	public void setPreferProxiedForAddress(boolean preferProxiedForAddress)
	{
		_preferProxiedForAddress = preferProxiedForAddress;
	}
    
    /* ------------------------------------------------------------ */
	/**
	 * Retrieved log X-Forwarded-For IP address flag.
	 *
	 * @return value of the flag
	 */
	public boolean getPreferProxiedForAddress()
	{
		return _preferProxiedForAddress;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Controls logging of the request dispatch time
	 *
	 * @param value true - request dispatch time will be logged
	 *              false - request dispatch time will not be logged
	 */
	public void setLogDispatch(boolean value)
	{
		_logDispatch = value;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Retrieve request dispatch time logging flag
	 *
	 * @return value of the flag
	 */
	public boolean isLogDispatch()
	{
		return _logDispatch;
	}

    /* ------------------------------------------------------------ */
	/**
	 * Writes the request and response information to the output stream.
	 */
	public void log(RequestContext context)
	{
		try
		{
			StringBuilder buf= new StringBuilder(256);
			
			if (_logServer)
			{
				String hostName = context.getRequestHeader(KrtkUtil.HEADER_HOST);
				buf.append(hostName==null ? "null": hostName);
				buf.append(' ');
			}
			
			String addr = null;
			if (_preferProxiedForAddress)
			{
				addr = context.getRequestHeader(KrtkUtil.HEADER_X_FORWARDED_FOR);
			}
			
			if (addr == null)
			{
				addr = "null";// TODO request.getRemoteAddr();
			}
			
			buf.append(addr);
			buf.append(" - ");
			
			String authentication = null; // TODO request.getAuthentication();
			if (authentication!=null)
			{
				buf.append(authentication);
			}
			else
			{
				buf.append(" - ");
			}
			
			buf.append(" [");
			buf.append(_sfmt.format(new Date(context.getTimeStamp())));
			
			buf.append("] \"");
			buf.append(context.getMethod());
			buf.append(' ');
			buf.append(context.getPath());
			buf.append(' ');
			buf.append(context.getProtocol());
			buf.append("\" ");
			
			if(true)
			{
				int status = 100 ; // TODO context.getStatus();
				if (status <= 0)
					status = 404;
				buf.append((char)('0' + ((status / 100) % 10)));
				buf.append((char)('0' + ((status / 10) % 10)));
				buf.append((char)('0' + (status % 10)));
			}
			else
			{
				buf.append("Async");
			}
			
			long responseLength = -1 ;// TODO context.getResponseBodyContentCount();
			if (responseLength >= 0)
			{
				buf.append(' ');
				if (responseLength > 99999)
					buf.append(responseLength);
				else
				{
					if (responseLength > 9999)
						buf.append((char)('0' + ((responseLength / 10000) % 10)));
					if (responseLength > 999)
						buf.append((char)('0' + ((responseLength / 1000) % 10)));
					if (responseLength > 99)
						buf.append((char)('0' + ((responseLength / 100) % 10)));
					if (responseLength > 9)
						buf.append((char)('0' + ((responseLength / 10) % 10)));
					buf.append((char)('0' + (responseLength) % 10));
				}
				buf.append(' ');
			}
			else
			{
				buf.append(" - ");
			}
			
			if (_extended)
				logExtended(context, buf);
			
			if (_logCookies)
			{
				/* TODO ----------------------------------
				Cookie[] cookies = request.getCookies();
				if (cookies == null || cookies.length == 0)
					buf.append(" -");
				else
				{
					buf.append(" \"");
					for (int i = 0; i < cookies.length; i++)
					{
						if (i != 0)
							buf.append(';');
						buf.append(cookies[i].getName());
						buf.append('=');
						buf.append(cookies[i].getValue());
					}
					buf.append('\"');
				}
				*/
			}
			
			if (_logDispatch || _logLatency)
			{
				long now = System.currentTimeMillis();
				/* TODO ----------------------------------
				
				if (_logDispatch)
				{
					long d = request.getDispatchTime();
					buf.append(' ');
					buf.append(now - (d==0 ? request.getTimeStamp():d));
				}
				*/
				
				if (_logLatency)
				{
					buf.append(" latency=");
					buf.append(now - context.getTimeStamp());
					buf.append("ms");
				}
			}
			
			String log = buf.toString();
			synchronized(this)
			{
				LOG.info(log);
			}
		}
		catch (IOException e)
		{
			LOG.warn(e);
		}
		
	}

    /* ------------------------------------------------------------ */
	/**
	 * Writes extended request and response information to the output stream.
	 */
	protected void logExtended(RequestContext context,
							   StringBuilder b) throws IOException
	{
		String referer = context.getRequestHeader(KrtkUtil.HEADER_REFERER);
		if (referer == null)
			b.append("\"-\" ");
		else
		{
			b.append('"');
			b.append(referer);
			b.append("\" ");
		}
		
		String agent = context.getRequestHeader(KrtkUtil.HEADER_USER_AGENT);
		if (agent == null)
			b.append("\"-\" ");
		else
		{
			b.append('"');
			b.append(agent);
			b.append('"');
		}
	}

    /* ------------------------------------------------------------ */
	/**
	 * Set up request logging and open log file.
	 *
	 */
	public void init() throws Exception
	{
	}

}
