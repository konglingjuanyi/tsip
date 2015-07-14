/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.tap;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.BaseServiceExecutor;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.service.ApplicationServiceImpl;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.service.IApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 根据Json请求参数运行服务方法
 */
@SuppressWarnings("unchecked")
public class TAPServiceExecutor extends BaseServiceExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(TAPServiceExecutor.class);

    protected  String invokeService(String source){
        String platformID = source.substring(0,3);
        source = source.substring(3);
        LOGGER.debug("PlatformID:"+platformID);
        LOGGER.debug("OTA Message:"+source);
        IApplicationService applicationService = SpringContext.getInstance().getBean(ApplicationServiceImpl.class);
        String result = applicationService.execute(Cfg.PL_STR_MAP.get(platformID),source,platformID);
        return result;
    }


	
}
