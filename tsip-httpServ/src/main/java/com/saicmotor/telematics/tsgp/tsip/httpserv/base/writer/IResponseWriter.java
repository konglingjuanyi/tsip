/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.base.writer;

import java.io.ByteArrayOutputStream;


/**
 * 响应数据回写类接口
 */
public interface IResponseWriter {

	/**
	 * 向客户端写回服务端的输出结果
	 */
	void writeResult(ByteArrayOutputStream out, String result, Throwable e, String charset);
	
	/**
	 * 返回当前Writer所对应的Mime类型
	 */
	String getMimeType();
}