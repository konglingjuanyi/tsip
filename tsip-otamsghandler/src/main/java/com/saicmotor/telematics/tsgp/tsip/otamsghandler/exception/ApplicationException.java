/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception;

import com.saicmotor.telematics.framework.core.exception.GeneralRuntimeException;

/**
 * 异常信息类
 * User: iwskd
 * Date: 12-10-25
 * Time: 下午1:26
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationException extends GeneralRuntimeException {

    public ApplicationException(String errorCode) {
        super(errorCode);
    }

    /**
     * Create the exception.
     */
    public ApplicationException(int errorCode, Throwable rootCause) {
        super(rootCause);
        this.code = errorCode;
    }

    /**
     * Create the exception.
     */
    public ApplicationException(int errorCode, Exception e) {
        super(e);
        this.code = errorCode;
    }

    /**
     * Create the exception.
     */
    public ApplicationException(Throwable rootCause) {
        super(rootCause);
    }

    public ApplicationException(String s, Exception e) {
        super(e);
    }
}
