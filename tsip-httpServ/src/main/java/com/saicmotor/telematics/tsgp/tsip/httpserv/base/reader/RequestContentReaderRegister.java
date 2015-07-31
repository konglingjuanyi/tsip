/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.base.reader;


import com.saicmotor.telematics.framework.core.common.SpringContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求内容读取类的注册器
 */
@Service
public class RequestContentReaderRegister {

	private static Map<String, Class<RequestContentReader>> contentReaders = new HashMap<String, Class<RequestContentReader>>(
			0);

	/**
	 * 根据http请求类型注册内容读取器
	 */
	public RequestContentReaderRegister registRequestReader(String contentType,
			Class<RequestContentReader> contentReader) {
		contentReaders.put(contentType, contentReader);
		return this;
	}

	/**
	 * 根据Http Content Type得到内容读取器
	 */
	public RequestContentReader getRequestReader(String contentType) {
		RequestContentReader bean = null;
		Class<RequestContentReader> clazz = contentReaders.get(contentType);
		if (clazz != null) {
			bean = SpringContext.getInstance().getBean(clazz);
		}
		return bean;
	}

	/**
	 * 得到注册器实例
	 */
	public static RequestContentReaderRegister getInstance() {
		return SpringContext.getInstance().getBean(RequestContentReaderRegister.class);
	}
}
