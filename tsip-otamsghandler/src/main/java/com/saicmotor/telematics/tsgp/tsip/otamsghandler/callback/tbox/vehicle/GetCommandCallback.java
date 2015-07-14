/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.tbox.vehicle;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTAEncoder;
import com.saicmotor.telematics.tsgp.otaadapter.tbox.v1_1.entity.dispatcher.TBOX_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.tbox.v1_1.entity.login.TBOX_Authentication;
import com.saicmotor.telematics.tsgp.otaadapter.tbox.v1_1.service.TBOXAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.DataEncodingType;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.MessageCounter;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_DispatcherBody;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_DispatcherHeader;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.login.TCMP_Authentication;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.service.ITCMPAdapterService;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.service.TCMPAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.client.ClientFactory;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.client.IClient;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.OTACallback;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.MessageTemplate;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ErrorMessageHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.TSIPException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * User: zhuxiaoyan
 * TBOX获取指令前先去TCMP走一遍验证
 */
public class GetCommandCallback extends OTACallback{
    private static final Logger LOGGER = LoggerFactory.getLogger(GetCommandCallback.class);
    private static final String TBOX_AUTH_AID = "707";
    public static final int INNER_ERROR_CODE = 26005;

    public String invoke(MessageTemplate.Protocol protocol, Object requestObject) {
        //模拟101接口调TCMP验证接口
        int result = invokeTCMP(protocol, requestObject);
        if (result == 0) {
            return super.invoke(protocol, requestObject);
        } else {
            return processAuthError(protocol, (TBOX_OTARequest)requestObject, INNER_ERROR_CODE);
        }
    }

    //TODO 处理验证失败的情况, 暂时先返回111接口公共的内部错误码
    private String processAuthError(MessageTemplate.Protocol protocol, TBOX_OTARequest requestObject, int resultCode) {
        try {
            int errorCode = Integer.valueOf(ErrorMessageHelper.getErrorMapping(String.valueOf(resultCode), RequestContext.getContext().getAid()));
            requestObject.getDispatcherBody().setResult(errorCode);
            requestObject.setApplicationData(new byte[0]);

            requestObject.getDispatcherBody().setMessageID(2);
            requestObject.getDispatcherBody().getMessageCounter()
                    .setDownlinkCounter(requestObject.getDispatcherBody().getMessageCounter().getDownlinkCounter() + 1);

            TBOXAdapterServiceImpl adapterService = new TBOXAdapterServiceImpl();
            byte[] bytes = adapterService.getBytesData(requestObject, "1");
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
            throw new TSIPException(e);
        }
    }

    private int invokeTCMP(MessageTemplate.Protocol protocol, Object requestObject) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream((byte[])AdapterHelper.getProperty(requestObject,"applicationData"));
            OTADecoder decoder = new OTADecoder(inputStream);
            TBOX_Authentication tboxAuthentication = (TBOX_Authentication)decoder.decode(TBOX_Authentication.class);

            String iccID = (String) AdapterHelper.getProperty(requestObject, "dispatcherBody.iccID");
            String simInfo = (String) AdapterHelper.getProperty(requestObject, "dispatcherBody.simInfo");
            String vin = (String) AdapterHelper.getProperty(requestObject, "dispatcherBody.vin");

            TCMP_Authentication tcmpAuthentication = new TCMP_Authentication();
            tcmpAuthentication.setIccID(iccID);
            tcmpAuthentication.setSimInfo(simInfo);
            tcmpAuthentication.setSeed(tboxAuthentication.getSeed());
            tcmpAuthentication.setTcuSN(tboxAuthentication.getTcuSN());

            TCMP_OTARequest tcmpRequest = createTCMPRequest(TBOX_AUTH_AID, vin);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            OTAEncoder encoder = new OTAEncoder(outputStream);
            encoder.encode(tcmpAuthentication);
            byte[] appBytes = outputStream.toByteArray();
            tcmpRequest.setApplicationData(appBytes);

//            ITCMPAdapterService tcmpAdapterService =  GuiceContext.getInstance().getBean(TCMPAdapterServiceImpl.class);
            ITCMPAdapterService tcmpAdapterService =  SpringContext.getInstance().getBean(TCMPAdapterServiceImpl.class);
            byte[] bytes = tcmpAdapterService.getBytesData(tcmpRequest,"1");
            String source= Cfg.PLATFORM_TCMP+new String(bytes, "UTF-8");
//            String url = (String) GuiceContext.getInstance().getConfig().getProperties().get("TCMP").get("url");
            String url = SpringContext.getInstance().getProperty("TCMP.url");
            IClient client = ClientFactory.getClient(ClientFactory.HTTP);
            String returnSource = client.sendData(url, source);
            TCMP_OTARequest tcmpResult = tcmpAdapterService.receive(returnSource.getBytes());

            return tcmpResult.getDispatcherBody().getResult();
        } catch (Exception e) {
            throw new TSIPException(e);
        }
    }

    private static TCMP_OTARequest createTCMPRequest(String aid, String vin){
        TCMP_OTARequest tcmp_otaRequest =new TCMP_OTARequest();
        tcmp_otaRequest.setDispatcherBody(createBody(aid, vin));
        tcmp_otaRequest.setDispatcherHeader(createHeader());
        return tcmp_otaRequest;
    }

    private static TCMP_DispatcherHeader createHeader(){
        TCMP_DispatcherHeader tcmp_dispatcherHeader = new TCMP_DispatcherHeader();
        tcmp_dispatcherHeader.setDispatcherBodyEncoding(0);
        tcmp_dispatcherHeader.setProtocolVersion(17);
        tcmp_dispatcherHeader.setSecurityContext(0);
        tcmp_dispatcherHeader.setDispatcherMessageLength(0);
        return tcmp_dispatcherHeader;
    }

    private static TCMP_DispatcherBody createBody(String aid, String vin){
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
        return tcmp_dispatcherBody;
    }
}
