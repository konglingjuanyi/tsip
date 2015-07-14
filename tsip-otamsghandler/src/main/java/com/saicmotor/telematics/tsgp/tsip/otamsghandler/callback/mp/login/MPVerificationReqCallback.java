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
 * Time: 上午10:08
 * To change this template use File | Settings | File Templates.
 */
public class MPVerificationReqCallback extends OTACallback {

    @Override
    protected void transformServiceObj2Obj(MessageTemplate.Protocol protocol, final Object requestObj, final Object serviceObj){
        String clientVersion = RequestContext.getContext().getClientVersion();
        String serviceVersion = protocol.getServiceVersion();

        if ("1.1".equals(clientVersion) && "1.1".equals(serviceVersion)) {
            new OTATransform.Converter() {
                @Override
                public void convert(Object requestObj, Object serverObj) {

                    AdapterHelper.setProperty(requestObj, "applicationData", new byte[0]);

                }
            }.convert(requestObj, serviceObj);
        }
    }
}
