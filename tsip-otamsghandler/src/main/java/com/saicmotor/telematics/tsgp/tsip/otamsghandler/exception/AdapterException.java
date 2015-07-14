/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception;


import com.saicmotor.telematics.framework.core.exception.GeneralRuntimeException;

/**
 * Adapter编解码相关的异常
 *
 * @author: zhuxiaoyan
 */
public class AdapterException extends GeneralRuntimeException {
    public AdapterException(Throwable cause) {
        super(cause);
    }

    public AdapterException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdapterException(String message) {
        super(message);
    }
}
