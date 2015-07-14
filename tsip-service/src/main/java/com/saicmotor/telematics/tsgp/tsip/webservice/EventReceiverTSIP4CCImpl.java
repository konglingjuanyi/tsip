/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.webservice;

import com.saicmotor.framework.context.guice.GuiceContext;
import com.saicmotor.mce550.tsgp.tsip.common.v1.EventMessage;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.EventReceiverTSIP4CC;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.SendDataRequest;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.SendDataResponse;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.TSIPException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.LogHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.service.ApplicationServiceImpl;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.service.IApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * 博泰CC WebService实现类
 *
 * @author zhuxiaoyan
 */
@WebService(wsdlLocation = "classpath:META-INF/wsdl/EventReceiverTSIP4CCService.wsdl", targetNamespace = "http://saicmotor.com/mce550/tsgp/tsip/msg/v1", name = "EventReceiverTSIP4CC", serviceName = "EventReceiverTSIP4CCService", endpointInterface = "com.saicmotor.mce550.tsgp.tsip.msg.v1.EventReceiverTSIP4CC")
public class EventReceiverTSIP4CCImpl implements EventReceiverTSIP4CC {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventReceiverTSIP4CCImpl.class);

    public SendDataResponse sendDataTSIP(
            @WebParam(partName = "parameters", name = "sendDataRequest", targetNamespace = "http://saicmotor.com/mce550/tsgp/tsip/msg/v1") SendDataRequest parameters) {
        try {
            EventMessage em = parameters.getEventMessage();
            String source = em.getValue();

            LogHelper.info(LOGGER, "PateoCC", "TSIP", "", 1, null, null, source, null, null, null);

            IApplicationService applicationService = GuiceContext.getInstance().getBean(ApplicationServiceImpl.class);
            String result = applicationService.execute(Cfg.getPlatformStrByID(Cfg.PLATFORM_CC), source, Cfg.PLATFORM_CC);

            SendDataResponse response = new SendDataResponse();
            EventMessage emResult = new EventMessage();
            emResult.setValue(result);
            response.setResult(emResult);
            return response;
        } catch (Exception e) {
            LOGGER.error("处理webservice请求失败", e);
            throw new TSIPException("The WebService of EventReceiverTSIP4CC run error!" + e);
        }
    }

}
