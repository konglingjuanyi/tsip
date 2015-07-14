/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 异常信息初始化启动类.
 * @author dongzehao
 */
public class ErrorInitStarter implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorInitStarter.class);

    /**
     * @see ServletContextListener
     * @param event
     */
    public void contextDestroyed(ServletContextEvent event) {

    }
    /**
     * 在系统启动时调用，加载异常信息
     * @param event
     */
    public void contextInitialized(ServletContextEvent event) {
        try {
            Class.forName("com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ErrorMessageHelper");
        } catch (ClassNotFoundException e) {
            LOGGER.error("加载ErrorMessageHelper失败", e);
        }
    }


}
