/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.base;

import com.saicmotor.telematics.framework.core.common.ModelMap;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.exception.HTTPServException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * AVN请求的过滤器
 */
public abstract class BaseFilter implements Filter {
 
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseFilter.class);

	/**
	 * 服务资源的url前缀
	 */
	protected String urlPrefix;

	/**
	 * 忽略处理的资源扩展名
	 */
	protected static Set<String> extNameExcludes = new HashSet<String>();
	
	static {
		extNameExcludes.add("js");
		extNameExcludes.add("jsp");
		extNameExcludes.add("jspa");
		extNameExcludes.add("do");
		extNameExcludes.add("action");
		extNameExcludes.add("html");
		extNameExcludes.add("htm");
		extNameExcludes.add("jpg");
		extNameExcludes.add("gif");
		extNameExcludes.add("png");
		extNameExcludes.add("bmp");
		extNameExcludes.add("swf");
		extNameExcludes.add("css");
		extNameExcludes.add("htc");
		extNameExcludes.add("jsf");
	}
	
	protected long sessionTimeOut = 15;
	

	/**
	 * 初始化过滤器
	 */
	public void init(FilterConfig config) throws ServletException {
		// 初始化需要被过滤器忽略的资源扩展名
		String _extNameExcludes = config.getInitParameter("extNameExcludes");
		if (_extNameExcludes != null && !_extNameExcludes.trim().equals("")) {
			String[] exts = _extNameExcludes.split(",");
			for (String ext : exts){
				extNameExcludes.add(ext);
			}	
		}
 
		// 初始化url前缀
		this.initUrlPrefix(config);
	}

	/**
	 * 过滤方法
	 */
	public void doFilter(ServletRequest servletReqest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {

		HttpServletRequest hRequest = (HttpServletRequest) servletReqest;
		HttpServletResponse hResponse = (HttpServletResponse) servletResponse;

		LOGGER.debug("doFilter:"+hRequest.getParameterMap());
		
		String uri = hRequest.getRequestURI();
		uri = uri.replace(hRequest.getContextPath(), "");

		// 忽略以下的文件不处理
		String _uri = uri.trim().toLowerCase();
		int index = _uri.lastIndexOf(".");
		if (index != -1) {
			String ext_name = _uri.substring(index + 1);
			if (extNameExcludes.contains(ext_name)) {
				filterChain.doFilter(hRequest, hResponse);
				return;
			}
		}

		if (uri == null || "".equals(uri) || "/".equals(uri)) {
			filterChain.doFilter(hRequest, hResponse);
			return;
		}

		this.executeFilter(hRequest, hResponse, filterChain, uri);
	}

    abstract protected void callProcess(ServletRequest servletReqest,
                                        ServletResponse servletResponse);

	protected void executeFilter(HttpServletRequest hRequest,
			HttpServletResponse hResponse, FilterChain filterChain, String uri)
			throws IOException, ServletException {
		// 请求参数
		ModelMap<String, String> params = new ModelMap<String, String>();
		try {
			// 设置上下文中的环境变量
//			ContextManager.setContext(hRequest, hResponse, params);
            callProcess(hRequest,hResponse);
		} catch (Exception e) {
            LOGGER.error("BaseFilter Running Error",e);
            throw new ServletException("BaseFilter Running Error",e);
		}
//		finally {
			// 清除上下文中的环境变量				
//			ContextManager.clearContext();
//		}
	}
	/**
	 * 初始化URL
	 */
	private void initUrlPrefix(FilterConfig config) {
		try {
			FilterInfoParser servletInfoParser = new FilterInfoParser();
			servletInfoParser.parse(config.getServletContext());
			
			Map<String, String> filterInfos = servletInfoParser.getFilterInfos();
			
			this.sessionTimeOut = Integer.parseInt(servletInfoParser.getSessionTimeout());
			
			String urlPattern = filterInfos.get(config.getFilterName());
			this.urlPrefix = urlPattern;

			if (urlPattern.endsWith("*")){
				this.urlPrefix = urlPattern.substring(0,
						urlPattern.length() - 1);
			}
			if ("/".equals(this.urlPrefix)){
				this.urlPrefix = null;
			}	
		} catch (Exception e) {
			throw new HTTPServException(e);
		}
	}

	/*
	 * Destroy方法
	 */
	public void destroy() {
	}

	/**
	 * 解析web.xml中filter的定义
	 */
	class FilterInfoParser extends DefaultHandler {

		private boolean startParse;

		private String name;
		private String urlPattern;
		private StringBuffer content;

		private String sessionTimeout = "15";
		private Map<String, String> filterInfos;
		private Map<String, String> servletInfos;

		public FilterInfoParser() {
			this.content = new StringBuffer();
			this.filterInfos = new HashMap<String, String>(0);
			this.servletInfos = new HashMap<String, String>(0);
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			this.clearContent();

			if ("filter-mapping".equalsIgnoreCase(qName)
					|| "servlet-mapping".equalsIgnoreCase(qName)) {
				startParse = true;
			}

			if (startParse
					&& ("filter-name".equalsIgnoreCase(qName) || "servlet-name"
							.equalsIgnoreCase(qName))) {
				name = null;
			}

			if (startParse && qName.equalsIgnoreCase("url-pattern")) {
				urlPattern = null;
			}
			
			if ("session-timeout".equalsIgnoreCase(qName)) {
				this.clearContent();
			}
		}

		private void clearContent() {
			content.delete(0, content.length());
		}

		public void characters(char ch[], int start, int length)
				throws SAXException {
			content.append(ch, start, length);
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if ("filter-mapping".equalsIgnoreCase(qName)) {
				startParse = false;
				this.filterInfos.put(this.name, this.urlPattern);
			}

			if ("servlet-mapping".equalsIgnoreCase(qName)) {
				startParse = false;
				this.servletInfos.put(this.name, this.urlPattern);
			}

			if (startParse
					&& ("filter-name".equalsIgnoreCase(qName) || "servlet-name"
							.equalsIgnoreCase(qName))) {
				name = content.toString();
				this.clearContent();
			}

			if (startParse && "url-pattern".equalsIgnoreCase(qName)) {
				urlPattern = content.toString();
				this.clearContent();
			}

			if ("session-timeout".equalsIgnoreCase(qName)) {
				this.sessionTimeout = content.toString();
				this.clearContent();
			}
		}
		
		public final Map<String, String> getFilterInfos() {
			return filterInfos;
		}

		public final Map<String, String> getServletInfos() {
			return servletInfos;
		}

		public String getSessionTimeout() {
			return sessionTimeout;
		}

		/**
		 * 解析方法
		 */
		public void parse(ServletContext servletContext){
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			InputStream resourceAsStream = null;
			try {
			SAXParser parser = factory.newSAXParser();
			//servletContext.getContextPath();
			resourceAsStream = new FileInputStream(servletContext
						.getRealPath("WEB-INF/web.xml"));
				parser.parse(resourceAsStream, this);
			}catch(Exception e){
				throw new HTTPServException("文件解析出错",e);
			}
			finally{
				if(resourceAsStream != null){
					try {
						resourceAsStream.close();
					} catch (IOException e) {
						throw new HTTPServException("文件解析出错",e);
					}
				}	
			}
			
		}
	}
}
