/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.webservice.util;


import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.dispatcher.Navi_DispatcherBody;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.dispatcher.Navi_DispatcherHeader;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.dispatcher.Navi_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.DataEncodingType;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.MessageCounter;


/**
 * Created with IntelliJ IDEA.
 * User: szksr
 * Date: 13-9-11
 * Time: 下午2:20
 * To change this template use File | Settings | File Templates.
 */
public class RequestUtil4Navi {

    public static Navi_OTARequest createRequest(String aid){
        Navi_OTARequest otaRequest = new Navi_OTARequest();
        otaRequest.setDispatcherHeader(createDispatcherHeader());
        otaRequest.setDispatcherBody(createDispatcherBody(aid));
        otaRequest.setApplicationData(new byte[0]);
        return otaRequest;
    }



    private static Navi_DispatcherBody createDispatcherBody(String aid){
        Navi_DispatcherBody dispatcherBody = new Navi_DispatcherBody();
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



    private static Navi_DispatcherHeader createDispatcherHeader(){
        Navi_DispatcherHeader dispatcherHeader = new Navi_DispatcherHeader();
        dispatcherHeader.setDispatcherBodyEncoding(0);
        dispatcherHeader.setProtocolVersion(18);
        dispatcherHeader.setSecurityContext(0);
        dispatcherHeader.setDispatcherMessageLength(0);
        return dispatcherHeader;
    }



}
