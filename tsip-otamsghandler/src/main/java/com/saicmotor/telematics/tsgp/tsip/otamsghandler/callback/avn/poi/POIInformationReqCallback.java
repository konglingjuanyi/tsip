/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.avn.poi;

import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.poi.POIInformationResp;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.OTACallback;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.MessageTemplate;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.OTATransform;

/**
 * poi下发callback
 * @author wangjingbo
 */
public class POIInformationReqCallback extends OTACallback {

    @Override
    protected void transformServiceObj2Obj(MessageTemplate.Protocol protocol, final Object requestObj, final Object serviceObj){
        new OTATransform.Converter() {
            @Override
            public void convert(Object requestObj, Object serverObj) {
                byte[] serviceBytes = (byte[])AdapterHelper.getProperty(serverObj, "applicationData");
                Object serviceResp =  decode(POIInformationResp.class, serviceBytes);
                Object clientResp = fastCopy(serviceResp, com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.poi.POIInformationResp.class);
                byte[] clientBytes = encode(clientResp);
                AdapterHelper.setProperty(requestObj, "applicationData", clientBytes);
            }
        }.convert(requestObj, serviceObj);
    }
}
