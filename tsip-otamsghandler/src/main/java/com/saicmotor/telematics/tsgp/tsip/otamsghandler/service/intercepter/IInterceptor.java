/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.service.intercepter;

/**
 * Created with IntelliJ IDEA.
 * User: szksr
 * Date: 13-8-8
 * Time: 下午1:51
 * To change this template use File | Settings | File Templates.
 */
public interface IInterceptor {
    public String change(String aid, byte[] appData);

}
