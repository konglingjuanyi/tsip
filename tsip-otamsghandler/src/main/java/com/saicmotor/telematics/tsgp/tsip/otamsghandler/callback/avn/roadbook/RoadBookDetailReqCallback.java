/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.avn.roadbook;

import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.MessageTemplate;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.OTACallback;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.OTATransform;

/**
 * Created with IntelliJ IDEA.
 * User: cqzzl
 * Date: 13-9-27
 * Time: 下午3:13
 * To change this template use File | Settings | File Templates.
 */
public class RoadBookDetailReqCallback extends OTACallback {

    @Override
    protected void transformServiceObj2Obj(MessageTemplate.Protocol protocol, final Object requestObj, final Object serviceObj){
        String clientVersion = RequestContext.getContext().getClientVersion();
        String serviceVersion = protocol.getServiceVersion();

        if ("1.1".equals(clientVersion) && "1.1".equals(serviceVersion)) {
            new OTATransform.Converter() {
                @Override
                public void convert(Object requestObj, Object serverObj) {
                    byte[] serviceBytes = (byte[])AdapterHelper.getProperty(serverObj, "applicationData");
                    Object serviceResp =  decode(com.saicmotor.telematics.tsgp.otaadapter.roadbook.v1_1.entity.roadbook.RoadBookDetailResp.class, serviceBytes);
                    Object clientResp = fastCopy(serviceResp, com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.roadbook.RoadBookDetailResp.class);
                    byte[] clientBytes = encode(clientResp);
                    AdapterHelper.setProperty(requestObj, "applicationData", clientBytes);
                }
            }.convert(requestObj, serviceObj);
        }
        if ("1.0".equals(clientVersion) && "1.1".equals(serviceVersion)) {
            new OTATransform.Converter() {
                @Override
                public void convert(Object requestObj, Object serverObj) {
                    byte[] serviceBytes = (byte[])AdapterHelper.getProperty(serverObj, "applicationData");
                    Object serviceResp =  decode(com.saicmotor.telematics.tsgp.otaadapter.roadbook.v1_1.entity.roadbook.RoadBookDetailResp.class, serviceBytes);
                    Object clientResp = fastCopy(serviceResp, com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.roadbook.RoadBookDetailResp.class);
                    byte[] clientBytes = encode(clientResp);
                    AdapterHelper.setProperty(requestObj, "applicationData", clientBytes);
                }
            }.convert(requestObj, serviceObj);
        }
        if ("6.4".equals(clientVersion) && "1.1".equals(serviceVersion)) {
            new OTATransform.Converter() {
                @Override
                public void convert(Object requestObj, Object serverObj) {
                    byte[] serviceBytes = (byte[])AdapterHelper.getProperty(serverObj, "applicationData");
                    Object serviceResp =  decode(com.saicmotor.telematics.tsgp.otaadapter.roadbook.v1_1.entity.roadbook.RoadBookDetailResp.class, serviceBytes);
                    Object clientResp = fastCopy(serviceResp, com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.roadbook.RoadBookDetailResp.class);
                    byte[] clientBytes = encode(clientResp);
                    AdapterHelper.setProperty(requestObj, "applicationData", clientBytes);
                }
            }.convert(requestObj, serviceObj);
        }
    }
}