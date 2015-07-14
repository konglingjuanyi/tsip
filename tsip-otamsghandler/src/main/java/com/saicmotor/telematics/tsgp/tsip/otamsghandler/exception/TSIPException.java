/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception;


import com.saicmotor.telematics.framework.core.exception.GeneralRuntimeException;

/**
 * TSIP通用异常
 *
 * @author: zhuxiaoyan
 */
public class TSIPException extends GeneralRuntimeException {
    public TSIPException(Throwable cause) {
        super(cause);
    }

    public TSIPException(String message, Throwable cause) {
        super(message, cause);
    }

    public TSIPException(String message) {
        super(message);
    }
}
