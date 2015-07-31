/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.base.reader;


import com.saicmotor.telematics.framework.core.common.ModelMap;

import javax.servlet.http.HttpServletRequest;


/**
 * 请求读取类的接口
 */
public abstract class RequestContentReader {	
	
	/**
	 * 从客户端读取数据
	 */
	abstract void read(HttpServletRequest request, ModelMap params, String charset);
	
	/**
	 * 从客户端读取数据，并可以做一些额外的动作，比如从会话中去数据等
	 */
	public void readData(HttpServletRequest request, ModelMap params, String charset){
		read(request,params,charset);
	}
		
	/**
	 * 返回当前Reader所对应的内容类型
	 */
	public abstract String getContentType();
}