/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * OTA Mapping初始化启动类
 * @author: dongzehao
 */
public class OTAMappingInitStarter implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(OTAMappingInitStarter.class);

    public void contextDestroyed(ServletContextEvent event) {
        return;
    }
    /**
     * 在系统启动时调用，加载ota mapping信息
     * @param event
     */
    public void contextInitialized(ServletContextEvent event) {
        try {
            Class.forName("com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.MessageHelper");
            Class.forName("com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper");
        } catch (ClassNotFoundException e) {
            LOGGER.error("MessageHelper or AdapterHelper initialization failed", e);
        }
    }


}
