/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.base.helper;


import com.saicmotor.telematics.framework.core.common.MimeType;

import javax.servlet.http.HttpServletRequest;

/**
 * Http请求帮助类
 */
public class RequestHelper {
	
	public static String getContentType(HttpServletRequest request) {
		// 获取客户端中的请求数据类
		String contentType = request.getContentType();
		if(contentType != null){
			String[] cTypes = contentType.split(";");
			contentType = cTypes[0];
		}
		
		if (contentType == null || "".equals(contentType.trim())){
			contentType = MimeType.CONTENT_OF_APPLICATION_X_WWW_FORM_URLENCODED;
		}	
		return contentType;
	}

	public static String getAccepte(HttpServletRequest request) {
		// 获取客户端中的请求数据类型
		String accept = request.getHeader("accept");
		if (accept == null || accept.indexOf(MimeType.MIME_OF_ALL) != -1){
			accept = "*/*";
		}	
		accept = accept.toLowerCase();
		return accept;
	}

	public static String getMimeType(HttpServletRequest request) {
		// 获取客户端中的请求数据类型
		String accept = RequestHelper.getAccepte(request);
		// 缺省的数据返回类
		String mimeType = accept.split(",")[0];
		if (mimeType.equals(MimeType.MIME_OF_ALL)){
			mimeType = MimeType.MIME_OF_TEXT_HTML;
		}	
		return mimeType;
	}
}
