/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper;

import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.DataEncodingType;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.MessageCounter;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_DispatcherBody;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_DispatcherHeader;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_OTARequest;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;

/**
 * Created with IntelliJ IDEA.
 * User: szksr
 * Date: 13-9-10
 * Time: 下午3:18
 * To change this template use File | Settings | File Templates.
 */
public class RequestUtil4TCMP {
    private RequestUtil4TCMP() {

    }

    public static TCMP_OTARequest createRequest(String aid,RequestContext context){
        TCMP_OTARequest tcmp_otaRequest =new TCMP_OTARequest();
        tcmp_otaRequest.setDispatcherBody(createBody(aid, context.getUid(), context.getVin(), context.getToken()));
        tcmp_otaRequest.setDispatcherHeader(createHeader());
        return tcmp_otaRequest;
    }

    public static TCMP_DispatcherHeader createHeader(){
        TCMP_DispatcherHeader tcmp_dispatcherHeader = new TCMP_DispatcherHeader();
        tcmp_dispatcherHeader.setDispatcherBodyEncoding(0);
        tcmp_dispatcherHeader.setProtocolVersion(17);
        tcmp_dispatcherHeader.setSecurityContext(0);
        tcmp_dispatcherHeader.setDispatcherMessageLength(0);
        return tcmp_dispatcherHeader;
    }

    public static TCMP_DispatcherBody createBody(String aid,String uid,String vin,String token){
        TCMP_DispatcherBody tcmp_dispatcherBody = new TCMP_DispatcherBody();
        tcmp_dispatcherBody.setApplicationID(aid);
        tcmp_dispatcherBody.setAckRequired(false);
        DataEncodingType encodingType= new DataEncodingType();
        encodingType.setValue(DataEncodingType.EnumType.perUnaligned);
        tcmp_dispatcherBody.setApplicationDataEncoding(encodingType);
        tcmp_dispatcherBody.setApplicationDataProtocolVersion(257);
        tcmp_dispatcherBody.setMessageID(1);
        MessageCounter counter = new MessageCounter();
        counter.setDownlinkCounter(1);
        counter.setUplinkCounter(0);
        tcmp_dispatcherBody.setMessageCounter(counter);
        tcmp_dispatcherBody.setTestFlag(2);
        tcmp_dispatcherBody.setEventCreationTime(System.currentTimeMillis()/1000l);
        tcmp_dispatcherBody.setVin(vin);
        tcmp_dispatcherBody.setUid(uid);
        tcmp_dispatcherBody.setToken(token);
        return tcmp_dispatcherBody;
    }
}
