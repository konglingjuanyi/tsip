/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.base.helper;
 
import javax.servlet.http.HttpServletRequest;

public class Helper {
	private static long maxBodyPayloadSize = 1024*1024;
 

	
	public static String getTrimedUrl(HttpServletRequest request,String urlPrefix){
		String url = request.getRequestURI();
		
		String contextPath = request.getContextPath();
		if (!"/".equals(contextPath.trim()) && url.startsWith(contextPath)) {
			url = url.substring(contextPath.length());
		}
		
		if (urlPrefix != null){
			if(urlPrefix.startsWith("*")){
				urlPrefix=urlPrefix.substring(1,urlPrefix.length());
			}			
			url = url.substring(1,url.indexOf(urlPrefix));
		}
		return url;
	}
	
	
}