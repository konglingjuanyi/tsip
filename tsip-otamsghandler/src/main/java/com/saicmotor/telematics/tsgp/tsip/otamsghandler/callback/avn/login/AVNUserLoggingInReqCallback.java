/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.avn.login;

import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.MessageTemplate;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.OTACallback;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.OTATransform;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: yicni
 * Date: 13-10-21
 * Time: 上午2:03
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AVNUserLoggingInReqCallback extends OTACallback {

    @Override
    protected void transformServiceObj2Obj(MessageTemplate.Protocol protocol, final Object requestObj, final Object serviceObj){
        String clientVersion = RequestContext.getContext().getClientVersion();
        String serviceVersion = protocol.getServiceVersion();

        if ("1.1".equals(clientVersion) && "1.1".equals(serviceVersion)) {
            new OTATransform.Converter() {
                @Override
                public void convert(Object requestObj, Object serverObj) {

                    AdapterHelper.setProperty(requestObj, "dispatcherBody.uid", AdapterHelper.getProperty(serverObj,"dispatcherBody.uid"));
                    AdapterHelper.setProperty(requestObj, "applicationData", AdapterHelper.getProperty(serverObj,"applicationData"));

                }
            }.convert(requestObj, serviceObj);
        }
        if ("1.0".equals(clientVersion) && "1.1".equals(serviceVersion)) {
            new OTATransform.Converter() {
                @Override
                public void convert(Object requestObj, Object serverObj) {

                    AdapterHelper.setProperty(requestObj, "dispatcherBody.uid", AdapterHelper.getProperty(serverObj,"dispatcherBody.uid"));
                    AdapterHelper.setProperty(requestObj, "applicationData", AdapterHelper.getProperty(serverObj,"applicationData"));

                }
            }.convert(requestObj, serviceObj);
        }
        if ("6.4".equals(clientVersion) && "1.1".equals(serviceVersion)) {
            new OTATransform.Converter() {
                @Override
                public void convert(Object requestObj, Object serverObj) {

                    AdapterHelper.setProperty(requestObj, "dispatcherBody.uid", AdapterHelper.getProperty(serverObj,"dispatcherBody.uid"));
                    AdapterHelper.setProperty(requestObj, "applicationData", AdapterHelper.getProperty(serverObj,"applicationData"));

                }
            }.convert(requestObj, serviceObj);
        }
    }
}
