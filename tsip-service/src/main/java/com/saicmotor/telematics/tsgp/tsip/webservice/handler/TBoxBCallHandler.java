/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.webservice.handler;

import com.saicmotor.framework.context.guice.GuiceContext;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.AttrModel;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.MessageHeader;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.SendDataRequest4TSP;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.SendDataResponse4TSP;
import com.saicmotor.telematics.tsgp.otaadapter.asn.utils.BinaryAndHexUtil;
import com.saicmotor.telematics.tsgp.otaadapter.asn.utils.IntByteConvertor;
import com.saicmotor.telematics.tsgp.otaadapter.tbox.v1_1.entity.DataEncodingType;
import com.saicmotor.telematics.tsgp.otaadapter.tbox.v1_1.entity.MessageCounter;
import com.saicmotor.telematics.tsgp.otaadapter.tbox.v1_1.entity.dispatcher.TBOX_DispatcherBody;
import com.saicmotor.telematics.tsgp.otaadapter.tbox.v1_1.entity.dispatcher.TBOX_DispatcherHeader;
import com.saicmotor.telematics.tsgp.otaadapter.tbox.v1_1.entity.dispatcher.TBOX_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.tbox.v1_1.service.TBOXAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.TSIPException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.BitReader;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.LogHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.service.ApplicationServiceImpl;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.service.IApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Date;
import java.util.List;

/**
 * 处理TBOX短信推送请求的handler
 *
 * @author zhuxiaoyan
 */
@Singleton
public class TBoxBCallHandler extends AbstractTSPHandler{
    private static final Logger LOGGER = LoggerFactory.getLogger(TBoxBCallHandler.class);


    public SendDataResponse4TSP process(SendDataRequest4TSP parameters) {
        LogHelper.info(LOGGER, "SMS", "TSIP", "802", 1, null, null, "接收短信推送", null, null, null);

        MessageHeader messageHeader = parameters.getMessageHeader();
        //创建响应
        SendDataResponse4TSP sendDataResponse = new SendDataResponse4TSP();

        messageHeader.setMessageID(2);
        sendDataResponse.setMessageHeader(messageHeader);
        Integer result = 0;

        try{
            List<AttrModel> list = parameters.getApplicationData();
            String phoneNumber = list.get(1).getValue().get(0);
            String smsString = list.get(0).getValue().get(0);

            LOGGER.debug("Phone Number:" + phoneNumber);
            LOGGER.debug("SMS Content:" + smsString);

            LogHelper.info(LOGGER, "SMS", "TSIP", "802", 1, null, null, "短信推送内容, phone: " + phoneNumber + ", sms: " + smsString, null, null, null);

            //车况数据转发给SP
            byte[] smsBytes = BinaryAndHexUtil.hexStringToByte(smsString);
            String aid = new String(subArray(smsBytes, 0, 2));
            String vin = new String(subArray(smsBytes, 7, 23));
            int appProtocolVersion = IntByteConvertor.byte2ToInt(subArray(smsBytes, 5, 6));

            byte[] appBytes = subArray(smsBytes, 24, smsBytes.length - 1);

            //AID 901 对应的协议版本为1.1
            if ("901".equals(aid)) {
                //模拟TBOX请求，固定协议版本1.1
                String ProtocolVersion = "1.1";

                TBOX_OTARequest request = createTBOXRequest();
                request.getDispatcherHeader().setProtocolVersion(BitReader.toIntegerProtocol(ProtocolVersion));

                TBOX_DispatcherBody dispatcherBody = request.getDispatcherBody();

                dispatcherBody.setApplicationID(aid);
                dispatcherBody.setApplicationDataProtocolVersion(appProtocolVersion);
                dispatcherBody.setVin(vin);

                request.setApplicationData(appBytes);

                byte[] sourceBytes = new TBOXAdapterServiceImpl().getBytesData(request, "1");
                IApplicationService applicationService = GuiceContext.getInstance().getBean(ApplicationServiceImpl.class);
                applicationService.execute("SMS", new String(sourceBytes), Cfg.PLATFORM_TBOX);
            } else {
                throw new TSIPException("BCALL请求错误的aid: " + aid);
            }
        }catch (Exception e){
            result = 28020;
            sendDataResponse.getMessageHeader().setErrorMessage("短信接收错误");

            LogHelper.error(LOGGER, "802", 1, null, null, "28020", e, null);
        }
        sendDataResponse.getMessageHeader().setResult(result);
        return sendDataResponse;
    }

    private TBOX_OTARequest createTBOXRequest() {
        TBOX_OTARequest requestServer =new TBOX_OTARequest();

        //transform header
        TBOX_DispatcherHeader headerServer =new TBOX_DispatcherHeader();
        headerServer.setDispatcherBodyEncoding(0);
        headerServer.setProtocolVersion(17);
        headerServer.setSecurityContext(0);

        //transform body
        TBOX_DispatcherBody bodyServer = new TBOX_DispatcherBody();
        bodyServer.setMessageID(1);
        bodyServer.setEventCreationTime(new Date().getTime() / 1000l);

        MessageCounter counter = new MessageCounter();
        counter.setDownlinkCounter(0);
        counter.setUplinkCounter(1);
        bodyServer.setMessageCounter(counter);

        DataEncodingType encodingType= new DataEncodingType();
        encodingType.setValue(DataEncodingType.EnumType.perUnaligned);
        bodyServer.setApplicationDataEncoding(encodingType);

        bodyServer.setApplicationDataProtocolVersion(1);
        bodyServer.setTestFlag(2);
        bodyServer.setAckRequired(false);
        bodyServer.setIccID("00000000000000000000");

        requestServer.setDispatcherHeader(headerServer);
        requestServer.setDispatcherBody(bodyServer);

        return requestServer;
    }

    private byte[] subArray(byte[] src, int start, int end) {
        byte[] result = new byte[end - start + 1];
        System.arraycopy(src, start, result, 0, result.length);

        return result;
    }
}
