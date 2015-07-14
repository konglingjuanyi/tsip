/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.base.reader;

import com.saicmotor.telematics.framework.core.common.MimeType;
import com.saicmotor.telematics.framework.core.common.ModelMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 *  读取http content类型为application/x-www-form-urlencoded的请求
 */
@SuppressWarnings( { "unchecked" })
public class ParameterPairContentReader extends RequestContentReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParameterPairContentReader.class);

	public void read(HttpServletRequest request, ModelMap params,
			String charset) {
 
		Enumeration names = request.getAttributeNames();
		String name;
		while (names.hasMoreElements()) {
			name = names.nextElement().toString();
			params.put(name, request.getAttribute(name));
		}

		// url中的参数
		names = request.getParameterNames();
		String value;
		while (names.hasMoreElements()) {
			name = names.nextElement().toString();
			try {
				//TODO url中的参数乱码
				if ("get".equalsIgnoreCase(request.getMethod())){
//					if (RESTful.METHOD_OF_GET.equalsIgnoreCase(request.getMethod())){
					value = new String(request.getParameter(name).getBytes("ISO-8859-1"), charset);
				}	
				else{
					value = request.getParameter(name);
				}	
				params.put(name, value);
			} catch (Exception e) {
                LOGGER.error("参数处理出错", e);
				params.put(name, request.getParameter(name));
			}
		}
	}

	public String getContentType() {
		return MimeType.CONTENT_OF_APPLICATION_X_WWW_FORM_URLENCODED;
	}
}
