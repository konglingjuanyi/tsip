/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.mp.poi;

import com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.OTACallback;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.MessageTemplate;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.OTATransform;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * Created with IntelliJ IDEA.
 * User: yicni
 * Date: 13-12-2
 * Time: 上午2:35
 * To change this template use File | Settings | File Templates.
 */
public class FavoritePOIQueryReqCallback extends OTACallback {

    @Override
    protected void transformServiceObj2Obj(MessageTemplate.Protocol protocol, final Object requestObj, final Object serviceObj){
        String clientVersion = RequestContext.getContext().getClientVersion();
        String serviceVersion = protocol.getServiceVersion();
        String appVersion = protocol.getAppVersion();
        //TODO 软件版本
        if ("1.1".equals(clientVersion) && "1.1".equals(serviceVersion)&& "1.0".equals(appVersion)) {
            new OTATransform.Converter() {
                @Override
                public void convert(Object requestObj, Object serverObj) {
                    byte[] serviceBytes = (byte[])AdapterHelper.getProperty(serverObj, "applicationData");
                    Object serviceResp =  decode(com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.poi.FavoritePOIQueryResp.class, serviceBytes);
                    Object clientResp = fastCopy(serviceResp, com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.poi.FavoritePOIQueryResp.class);
                    byte[] clientBytes = encode(clientResp);
                    AdapterHelper.setProperty(requestObj, "applicationData", clientBytes);
                }
            }.convert(requestObj, serviceObj);
        }
        if ("1.1".equals(clientVersion) && "1.2".equals(serviceVersion)&& "2.0".equals(appVersion)) {
            new OTATransform.Converter() {
                @Override
                public void convert(Object requestObj, Object serverObj) {
                    AdapterHelper.setProperty(requestObj, "applicationData", AdapterHelper.getProperty(serverObj, "applicationData"));
                }
            }.convert(requestObj, serviceObj);
        }
        if ("1.1".equals(clientVersion) && "1.2".equals(serviceVersion)&& "2.1".equals(appVersion)) {
            new OTATransform.Converter() {
                @Override
                public void convert(Object requestObj, Object serverObj) {
                    AdapterHelper.setProperty(requestObj, "applicationData", AdapterHelper.getProperty(serverObj, "applicationData"));
                }
            }.convert(requestObj, serviceObj);
        }

    }
}
