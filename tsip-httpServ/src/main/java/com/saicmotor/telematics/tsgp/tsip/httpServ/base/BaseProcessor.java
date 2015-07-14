/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.base;

import com.saicmotor.telematics.framework.core.common.ModelMap;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.exception.HTTPServException;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.helper.Helper;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.helper.ProcessorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Json请求处理器，包括处理URL以及请求参数
 */
@SuppressWarnings("unchecked")
abstract public class BaseProcessor {
	 
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseProcessor.class);
	
	/**
	 *  字符编码
	 */
	private String charset;
	
	/**
	 * 服务资源的url前缀
	 */
	private String urlPrefix;

	/**
	 * 请求处理类的帮助类
	 */
	protected ProcessorHelper helper;
    protected IBaseServiceExecutor executor;

    abstract protected void initial();

	public BaseProcessor() {
        initial();
	}

	public BaseProcessor setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
		return this;
	}

	/**
	 * 处理来自客户端的请求
	 * @param servletReqest HttpServletRequest对象
	 * @param servletResponse HttpServletResponse对象
	 */
	public void process(ServletRequest servletReqest,
			ServletResponse servletResponse)  {
		HttpServletRequest request = (HttpServletRequest) servletReqest;	

		// 获取字符编码
		this.charset = request.getCharacterEncoding();
		if (this.charset == null || "".equals(charset.trim())) {
			this.charset = "iso-8859-1";
			try {
				request.setCharacterEncoding(charset);
			} catch (Exception e) {
				throw new HTTPServException(e);
			}
		}

		this.helper.setCharset(this.charset);	
 
		String originalUrl = request.getRequestURI();
		
		LOGGER.debug("Debug: URL:" + originalUrl);
		LOGGER.debug("Debug: sessionId:"+request.getSession().getId());
        LOGGER.debug("client host:"+request.getRemoteHost());
        LOGGER.debug("client user:"+request.getRemoteUser());
        LOGGER.debug("client ip:"+request.getRemoteAddr()+",port:"+request.getRemotePort());
		
		String url = Helper.getTrimedUrl(request, this.urlPrefix);
 
		LOGGER.debug("Debug: Trimed URL :" + url);
		
		ModelMap<String, String> params = ContextManager.getContext()
				.getModelMap();
		try {
			this.processNormalCall(request, url, originalUrl, params);			 
		}catch (Exception e) {
			LOGGER.error("服务方法执行出错",e);
		}
	}


	private void processNormalCall(HttpServletRequest request, String url,
			String original_url, ModelMap<String, String> params) {

		//填充参数
		this.helper.fillParameters(request, params);

        ContextManager.setCurrentUri(url);

        //调用服务方法
        executor.execute(charset);
	}
	
}
