/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.base;


import com.saicmotor.telematics.framework.core.common.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JsonRpc的上下文管理器，保存请求参数，Request,Response等对象
 */
@SuppressWarnings("unchecked")
public class ContextManager {

	private static ThreadLocal<HttpContext> httpContext = new ThreadLocal<HttpContext>();
	private static ThreadLocal<String> currentJsonUri = new ThreadLocal<String>();

	/**
	 * 设置当前Json请求的URL
	 * @param url
	 */
	public static void setCurrentUri(String url){
		currentJsonUri.set(url);
	}
	
	/**
	 * 得到当前Json请求的URL
	 * @return
	 */
	public static String getCurrentJsonUri(){
		return currentJsonUri.get();
	}
	
	private ContextManager() {
	}

	/**
	 * 得到线程中的上下文对象
	 * @return
	 */
	public static HttpContext getContext(){
		return httpContext.get();
	}

	/**
	 * 设置上下文对象到线程
	 */
	public static void setContext(HttpServletRequest request,
			HttpServletResponse response, ModelMap param) {
		httpContext.set(new HttpContext(request, response, param));
	}

	/**
	 * 清空上下文对象
	 */
	public static void clearContext() {
		httpContext.remove();
		currentJsonUri.remove();
	}

	/**
	 * 得到Request请求对象
	 */
	public static HttpServletRequest getRequest() {
		HttpContext context = httpContext.get();
		if (null == context) {
			return null;
		}

		return context.getRequest();
	}

	/**
	 * 得到Response响应对象
	 */
	public static HttpServletResponse getResponse() {
		HttpContext context = httpContext.get();
		if (null == context) {
			return null;
		}

		return context.getResponse();
	}

	/**
	 * 得到请求参数Map对象
	 */
	public static ModelMap getModelMap() {
		HttpContext context = httpContext.get();
		if (null == context) {
			return null;
		}

		return context.getModelMap();
	}

	/**
	 * 封装request, response,和modelMap到HttpContext
	 */
	public static class HttpContext {
		private final HttpServletRequest request;
		private final HttpServletResponse response;
		private final ModelMap modelMap;

		HttpContext(HttpServletRequest request, HttpServletResponse response,
				ModelMap param) {
			this.request = request;
			this.response = response;
			this.modelMap = param;
		}

		public HttpServletRequest getRequest() {
			return request;
		}

		public HttpServletResponse getResponse() {
			return response;
		}

		public ModelMap getModelMap() {
			return modelMap;
		}
	}
}
