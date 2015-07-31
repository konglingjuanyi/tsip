///*
// * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
// * This software is published under the terms of the SAIC IT Dept.
// *
// * @Project: Telematics
// */
//
//package com.saicmotor.telematics.tsgp.tsip.httpserv.base.provider;
//
//import com.google.inject.Provider;
//import com.saicmotor.telematics.tsgp.tsip.httpserv.base.ContextManager;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//
///**
// * HttpSession的提供者类
// */
//public class HttpSessionProvider implements Provider<HttpSession> {
//
//	public HttpSession get() {
//		HttpServletRequest request = ContextManager.getRequest();
//		if(request == null){
//			return null;
//		}
//		return request.getSession();
//	}
//}
