/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.base.writer;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应数据回写类注册器，该类为单例
 */
@Service
public class ResponseWriterRegister {

	private static Map<String, Class<IResponseWriter>> responseWriters = new HashMap<String, Class<IResponseWriter>>(
			0);

	/**
	 * 根据MIME类型注册回写类
	 */
	public ResponseWriterRegister registResponseWriter(String mimeType,
			Class<IResponseWriter> responseWriter) {
		responseWriters.put(mimeType, responseWriter);
		return this;
	}

	/**
	 * 根据MIME类型得到回写类
	 */
	public IResponseWriter getResponseWriter(String mimeType) {
		IResponseWriter bean = null;
		Class<IResponseWriter> clazz = responseWriters.get(mimeType);
		if (clazz != null) {
			bean = SpringContext.getInstance().getBean(clazz);
		}
		return bean;
	}

	/**
	 * 得到回写注册器示例
	 */
	public static ResponseWriterRegister getInstance() {
		return SpringContext.getInstance().getBean(ResponseWriterRegister.class);
	}
}
