/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.webservice.util;


import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.DataEncodingType;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.MessageCounter;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_DispatcherBody;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_DispatcherHeader;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_OTARequest;

/**
 * Created with IntelliJ IDEA.
 * User: szksr
 * Date: 13-9-16
 * Time: 下午1:10
 * To change this template use File | Settings | File Templates.
 */
public class RequestUtil4TCMP {
    public static TCMP_OTARequest createRequest(String aid){
        TCMP_OTARequest otaRequest = new TCMP_OTARequest();
        otaRequest.setDispatcherHeader(createDispatcherHeader());
        otaRequest.setDispatcherBody(createDispatcherBody(aid));
        otaRequest.setApplicationData(new byte[0]);
        return otaRequest;
    }



    private static TCMP_DispatcherBody createDispatcherBody(String aid){
        TCMP_DispatcherBody dispatcherBody = new TCMP_DispatcherBody();
        dispatcherBody.setApplicationID(aid);
        dispatcherBody.setAckRequired(false);
        DataEncodingType encodingType= new DataEncodingType();
        encodingType.setValue(DataEncodingType.EnumType.perUnaligned);
        dispatcherBody.setApplicationDataEncoding(encodingType);
        dispatcherBody.setApplicationDataProtocolVersion(257);
        dispatcherBody.setMessageID(1);
        MessageCounter counter = new MessageCounter();
        counter.setDownlinkCounter(1);
        counter.setUplinkCounter(0);
        dispatcherBody.setMessageCounter(counter);
        dispatcherBody.setTestFlag(2);
        dispatcherBody.setEventCreationTime(System.currentTimeMillis()/1000l);
        return   dispatcherBody;
    }



    private static TCMP_DispatcherHeader createDispatcherHeader(){
        TCMP_DispatcherHeader dispatcherHeader = new TCMP_DispatcherHeader();
        dispatcherHeader.setDispatcherBodyEncoding(0);
        dispatcherHeader.setProtocolVersion(17);
        dispatcherHeader.setSecurityContext(0);
        dispatcherHeader.setDispatcherMessageLength(0);
        return dispatcherHeader;
    }
}
