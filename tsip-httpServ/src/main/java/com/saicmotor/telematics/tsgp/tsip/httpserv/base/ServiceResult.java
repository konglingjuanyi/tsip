/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.base;

/**
 * 封装服务运行返回结果
 */
public class ServiceResult {

    private ServiceResult() {

    }

	public static String createHttpResult(String result,Throwable e) {
         if(e!=null){
              //todo: 返回异常消息

         }
         return result;
	}
}
