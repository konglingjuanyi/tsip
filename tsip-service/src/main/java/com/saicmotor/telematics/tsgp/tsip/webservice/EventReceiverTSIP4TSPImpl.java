/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.webservice;

import com.saicmotor.framework.context.guice.GuiceContext;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.EventReceiverTSIP4TSP;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.MessageHeader;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.SendDataRequest4TSP;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.SendDataResponse4TSP;
import com.saicmotor.telematics.tsgp.tsip.webservice.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * 8XX 外部服务的WebService实现类
 *
 * @author zhuxiaoyan
 * @author xujunjie
 */
@WebService(wsdlLocation = "classpath:META-INF/wsdl/EventReceiverTSIP4TSPService.wsdl", targetNamespace = "http://saicmotor.com/mce550/tsgp/tsip/msg/v1", name = "EventReceiverTSIP4TSP", serviceName = "EventReceiverTSIP4TSPService", endpointInterface = "com.saicmotor.mce550.tsgp.tsip.msg.v1.EventReceiverTSIP4TSP")
public class EventReceiverTSIP4TSPImpl implements EventReceiverTSIP4TSP {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventReceiverTSIP4TSP.class);

    public SendDataResponse4TSP sendDataTSIP(@WebParam(partName = "parameters", name = "sendDataRequest4TSP", targetNamespace = "http://saicmotor.com/mce550/tsgp/tsip/msg/v1") SendDataRequest4TSP parameters) {
        MessageHeader messageHeader = parameters.getMessageHeader();
        String applicationID = messageHeader.getApplicationID();
        int messageID = messageHeader.getMessageID();

        LOGGER.debug("EventReceiverTSIP4TSPImpl - AID: " + applicationID);
        LOGGER.debug("EventReceiverTSIP4TSPImpl - MID: " + messageID);

        //处理世纪高通token认证
        if ("801".equals(messageHeader.getApplicationID())) {
            ITSPHandler handler = GuiceContext.getInstance().getBean(CenNaviTokenHandler.class);
            return handler.process(parameters);
        }
        //处理BCALL短信推送
        if ("802".equals(messageHeader.getApplicationID())) {
            ITSPHandler handler = GuiceContext.getInstance().getBean(TBoxBCallHandler.class);
            return handler.process(parameters);
        }
        if ("803".equals(messageHeader.getApplicationID())) {
            ITSPHandler handler = GuiceContext.getInstance().getBean(IntercomTokenHandler.class);
            return handler.process(parameters);
        }
        if ("804".equals(messageHeader.getApplicationID())) {
            ITSPHandler handler = GuiceContext.getInstance().getBean(IvokaTokenHandler.class);
            return handler.process(parameters);
        }
        if ("805".equals(messageHeader.getApplicationID())) {
            ITSPHandler handler = GuiceContext.getInstance().getBean(InfoReaderTokenHandler.class);
            return handler.process(parameters);
        }
        if ("806".equals(messageHeader.getApplicationID())) {
            ITSPHandler handler = GuiceContext.getInstance().getBean(RoadConditionTokenHandler.class);
            return handler.process(parameters);
        }
        if ("807".equals(messageHeader.getApplicationID())) {
            ITSPHandler handler = GuiceContext.getInstance().getBean(ActiveInfoDownloadHandler.class);
            return handler.process(parameters);
        }
        if ("810".equals(messageHeader.getApplicationID())) {
            ITSPHandler handler = GuiceContext.getInstance().getBean(InkaNetPOIHandler.class);
            return handler.process(parameters);
        }
        if ("811".equals(messageHeader.getApplicationID())) {
            ITSPHandler handler = GuiceContext.getInstance().getBean(VehicleMesPart4OpenHandler.class);
            return handler.process(parameters);
        }

        return null;
    }
}
