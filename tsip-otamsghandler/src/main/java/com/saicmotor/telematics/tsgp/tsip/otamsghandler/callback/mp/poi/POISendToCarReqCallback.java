/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.mp.poi;

import com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.poi.POISendToCarReq;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.OTACallback;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.MessageTemplate;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.OTATransform;

/**
 * Created with IntelliJ IDEA.
 * User: yicni
 * Date: 13-12-2
 * Time: 上午2:35
 * To change this template use File | Settings | File Templates.
 */
public class POISendToCarReqCallback extends OTACallback {

    protected Object transformRequestObj2ServiceObj(MessageTemplate.Protocol protocol, Object obj){
        Class sourceClass = AdapterHelper.getRequestObjClass(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion());
        Class targetClass = AdapterHelper.getRequestObjClass(protocol.getServicePlatform(), protocol.getServiceVersion());
        String clientVersion = RequestContext.getContext().getClientVersion();
        String serviceVersion = protocol.getServiceVersion();
        String appVersion = protocol.getAppVersion();

        if("1.1".equals(clientVersion) && "1.1".equals(serviceVersion)&& "1.0".equals(appVersion)){
            return OTATransform.transform(sourceClass, targetClass, obj, new OTATransform.Converter(){
                @Override
                public void convert(Object sourceRequest, Object targetRequest) {
                    byte[] sourceBytes = (byte[])AdapterHelper.getProperty(sourceRequest, "applicationData");
                    Object sourceReq =  decode(POISendToCarReq.class, sourceBytes);
                    Object serviceReq = fastCopy(sourceReq, com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.poi.POISendToCarReq.class);
                    byte[] serviceBytes = encode(serviceReq);
                    AdapterHelper.setProperty(targetRequest, "applicationData", serviceBytes);
                }
            });
        }
        return OTATransform.transform(sourceClass, targetClass, obj);
    }
}
