package com.saicmotor.telematics.tsgp.tsip.webservice.util;


import com.saicmotor.telematics.tsgp.otaadapter.isp.entity.common.DataEncodingType;
import com.saicmotor.telematics.tsgp.otaadapter.isp.entity.common.MessageCounter;
import com.saicmotor.telematics.tsgp.otaadapter.isp.entity.dispatcher.ISP_DispatcherBody;
import com.saicmotor.telematics.tsgp.otaadapter.isp.entity.dispatcher.ISP_DispatcherHeader;
import com.saicmotor.telematics.tsgp.otaadapter.isp.entity.dispatcher.ISP_OTARequest;

/**
 * Created with IntelliJ IDEA.
 * User: jwdsq
 * Date: 14-3-19
 * Time: 上午8:50
 * To change this template use File | Settings | File Templates.
 */
public class RequestUtil4ISP {
    public static ISP_OTARequest createRequest(String aid){
        ISP_OTARequest otaRequest = new ISP_OTARequest();
        otaRequest.setDispatcherHeader(createDispatcherHeader());
        otaRequest.setDispatcherBody(createDispatcherBody(aid));
        otaRequest.setApplicationData(new byte[0]);
        return otaRequest;
    }



    private static ISP_DispatcherBody createDispatcherBody(String aid){
        ISP_DispatcherBody dispatcherBody = new ISP_DispatcherBody();
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



    private static ISP_DispatcherHeader createDispatcherHeader(){
        ISP_DispatcherHeader dispatcherHeader = new ISP_DispatcherHeader();
        dispatcherHeader.setDispatcherBodyEncoding(0);
        dispatcherHeader.setProtocolVersion(17);
        dispatcherHeader.setSecurityContext(0);
        dispatcherHeader.setDispatcherMessageLength(0);
        return dispatcherHeader;
    }
}
