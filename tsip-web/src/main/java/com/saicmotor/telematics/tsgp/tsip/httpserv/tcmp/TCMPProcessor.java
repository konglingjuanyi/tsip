/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.tcmp;


import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.BaseProcessor;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.helper.ProcessorHelper;

/**
 * Json请求处理器，包括处理URL以及请求参数
 */
@SuppressWarnings("unchecked")
public class TCMPProcessor extends BaseProcessor {

    protected void initial(){
        helper = new ProcessorHelper();
        executor =  SpringContext.getInstance().getBean(
                 TCMPServiceExecutor.class);
    }
}
