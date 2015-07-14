/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.tbox.login;

import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTAEncoder;
import com.saicmotor.telematics.tsgp.otaadapter.tbox.v1_1.entity.login.TBOX_Authentication;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.login.TCMP_Authentication;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.MessageTemplate;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.OTACallback;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.OTATransform;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: szksr
 * Date: 13-10-28
 * Time: 上午9:42
 * To change this template use File | Settings | File Templates.
 */
public class TBOXAuthenticationCallback extends OTACallback{
    @Override
    protected Object transformRequestObj2ServiceObj(MessageTemplate.Protocol protocol, Object obj){
        Class sourceClass = AdapterHelper.getRequestObjClass(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion());
        Class targetClass = AdapterHelper.getRequestObjClass(protocol.getServicePlatform(), protocol.getServiceVersion());

        ByteArrayInputStream inputStream = new ByteArrayInputStream((byte[])AdapterHelper.getProperty(obj,"applicationData"));
        OTADecoder decoder = new OTADecoder(inputStream);
        TBOX_Authentication tbox_authentication = (TBOX_Authentication)decoder.decode(TBOX_Authentication.class);

        TCMP_Authentication tcmp_authentication = new TCMP_Authentication();
        tcmp_authentication.setIccID((String)AdapterHelper.getProperty(obj,"dispatcherBody.iccID"));
        tcmp_authentication.setSimInfo((String) AdapterHelper.getProperty(obj, "dispatcherBody.simInfo"));
        tcmp_authentication.setSeed(tbox_authentication.getSeed());
        tcmp_authentication.setTcuSN(tbox_authentication.getTcuSN());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OTAEncoder encoder = new OTAEncoder(outputStream);
        encoder.encode(tcmp_authentication);
        byte[] appBytes = outputStream.toByteArray();
        AdapterHelper.setProperty(obj,"applicationData",appBytes);
        return OTATransform.transform(sourceClass, targetClass, obj);
    }
}
