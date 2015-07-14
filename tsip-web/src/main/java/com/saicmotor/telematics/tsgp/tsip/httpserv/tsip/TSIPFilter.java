/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.tsip;

import com.saicmotor.telematics.tsgp.tsip.httpserv.base.BaseFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


/**
 * AVN请求的过滤器
 */
public class TSIPFilter extends BaseFilter {

    public void callProcess(ServletRequest servletReqest,
                            ServletResponse servletResponse){
        new TSIPProcessor().setUrlPrefix(this.urlPrefix).process(
                servletReqest, servletResponse);
    }


}
