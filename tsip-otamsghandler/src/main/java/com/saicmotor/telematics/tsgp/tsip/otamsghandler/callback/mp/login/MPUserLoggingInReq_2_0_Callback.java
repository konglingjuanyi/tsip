/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.mp.login;

import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.login.MP_UserLoggingInResp;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.OTACallback;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.MessageTemplate;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.OTATransform;

/**
 * Created with IntelliJ IDEA.
 * User: szksr
 * Date: 13-10-22
 * Time: ����10:08
 * To change this template use File | Settings | File Templates.
 */
public class MPUserLoggingInReq_2_0_Callback extends OTACallback {

    @Override
    protected void transformServiceObj2Obj(MessageTemplate.Protocol protocol, final Object requestObj, final Object serviceObj){
        String clientVersion = RequestContext.getContext().getClientVersion();
        String serviceVersion = protocol.getServiceVersion();
        String appVersion = RequestContext.getContext().getAppVersion();

        if ("2.0".equals(appVersion)) {
            new OTATransform.Converter() {
                @Override
                public void convert(Object requestObj, Object serverObj) {
                    byte[] serviceBytes = (byte[])AdapterHelper.getProperty(serverObj, "applicationData");
                    Object serviceResp =  decode(MP_UserLoggingInResp.class, serviceBytes);
                    Object clientResp = fastCopy(serviceResp, com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_0.MP_UserLoggingInResp.class);
                    byte[] clientBytes = encode(clientResp);

                    AdapterHelper.setProperty(requestObj, "dispatcherBody.uid", AdapterHelper.getProperty(serverObj,"dispatcherBody.uid"));
                    AdapterHelper.setProperty(requestObj, "applicationData", clientBytes);

                }
            }.convert(requestObj, serviceObj);
        }
        if ("2.1".equals(appVersion)) {
            new OTATransform.Converter() {
                @Override
                public void convert(Object requestObj, Object serverObj) {
                    AdapterHelper.setProperty(requestObj, "dispatcherBody.uid", AdapterHelper.getProperty(serverObj,"dispatcherBody.uid"));
                    AdapterHelper.setProperty(requestObj, "applicationData", AdapterHelper.getProperty(serverObj, "applicationData"));
                }
            }.convert(requestObj, serviceObj);
        }
    }
}
