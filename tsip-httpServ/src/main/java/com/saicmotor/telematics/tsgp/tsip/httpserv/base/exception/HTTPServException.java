/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.base.exception;


import com.saicmotor.telematics.framework.core.exception.GeneralRuntimeException;

/**
 * JSON模块的异常类
 */
public class HTTPServException extends GeneralRuntimeException {

	public HTTPServException(Throwable cause) {
		super(cause);
	}

	public HTTPServException(String message) {
		super(message);
	}

	public HTTPServException(String message, Throwable cause) {
		super(message, cause);
	}
}
  
