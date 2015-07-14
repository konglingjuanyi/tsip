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
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.DataEncodingType;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.MessageCounter;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_DispatcherBody;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_DispatcherHeader;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.login.AVNTokenVerifyResp;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.service.ITCMPAdapterService;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.service.TCMPAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.client.ClientFactory;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.client.IClient;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.TSIPException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.LogHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 世纪高通token认证处理handler
 *
 * @author zhuxiaoyan
 */
@Singleton
public class CenNaviTokenHandler extends AbstractTSPHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CenNaviTokenHandler.class);

    private static final String CLIENT_TYPE = "http";
    private static final String SERVICE_KEY = "TCMP";
    private static final String URL_KEY = "url";

    public SendDataResponse4TSP process(SendDataRequest4TSP parameters) {
        List<AttrModel> list = parameters.getApplicationData();
        String token = list.get(0).getValue().get(0);

        LogHelper.info(LOGGER, "CenNavi", "TSIP", "801", 1, null, null, "世纪高通调用TOKEN认证接口", null, null, token);

        MessageHeader messageHeader = parameters.getMessageHeader();

        //创建响应
        SendDataResponse4TSP sendDataResponse = new SendDataResponse4TSP();
        messageHeader.setMessageID(2);
        sendDataResponse.setMessageHeader(messageHeader);

        int result = 0;

        List<AttrModel> applicationData = null;

        try {
            if (token == null || token.length() < 32) {
                result = 28010;
                sendDataResponse.getMessageHeader().setErrorMessage("TSP系统内部错误");

                LogHelper.error(LOGGER, "801", 1, null, null, "28010",
                        new TSIPException("无效的token: " + token)
                        , token);
            } else {
                //调用TCMP接口验证token
                //创建请求
                TCMP_OTARequest requestServer = createTCMPOTARequest("704");

                requestServer.getDispatcherBody().setToken(token);

                TCMP_OTARequest otaResultServer = invokeTCMP(requestServer);

                if (otaResultServer.getDispatcherBody().getResult() != 0 ) {
                    result = 28012;
                    sendDataResponse.getMessageHeader().setErrorMessage("TSP系统内部错误");
                    LogHelper.error(LOGGER, "801", 1, null, null, "28012",
                            new TSIPException("调用TCMP验证错误, 返回result: " + otaResultServer.getDispatcherBody().getResult())
                            , token);
                } else {
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(otaResultServer.getApplicationData());
                    OTADecoder decoder = new OTADecoder(inputStream);
                    AVNTokenVerifyResp avnTokenVerifyResp = (AVNTokenVerifyResp)decoder.decode(AVNTokenVerifyResp.class);

                    if (!avnTokenVerifyResp.getVerificationFlag()) {
                        result = 28010;
                        sendDataResponse.getMessageHeader().setErrorMessage("Token无效");
                        LogHelper.error(LOGGER, "801", 1, null, null, "28010",
                                new TSIPException("无效的token: " + token)
                                , token);
                    } else {
                        AttrModel model = new AttrModel();
                        model.setName("tokenExpiration");
                        model.getValue().add(avnTokenVerifyResp.getTokenExpiration().getSeconds().toString());
                        applicationData = new ArrayList<AttrModel>();
                        applicationData.add(model);
                    }
                }
            }
        } catch (Exception e) {
            result = 28012;
            sendDataResponse.getMessageHeader().setErrorMessage("TSP系统内部错误");

            LogHelper.error(LOGGER, "801", 1, null, null, "28012", e, token);
        }

        sendDataResponse.getMessageHeader().setResult(result);
        if (applicationData != null) {
            sendDataResponse.getApplicationData().addAll(applicationData);
        }

        return sendDataResponse;
    }

    private TCMP_OTARequest createTCMPOTARequest(String aid) {
        TCMP_OTARequest requestServer =new TCMP_OTARequest();

        //build header
        TCMP_DispatcherHeader headerServer =new TCMP_DispatcherHeader();
        headerServer.setDispatcherBodyEncoding(0);
        headerServer.setProtocolVersion(17);
        headerServer.setSecurityContext(0);

        //build body
        TCMP_DispatcherBody bodyServer = new TCMP_DispatcherBody();
        bodyServer.setPlatformID("0000000005");
        bodyServer.setApplicationID(aid);
        bodyServer.setVin("00000000000000000");
        bodyServer.setMessageID(1);
        bodyServer.setEventCreationTime(new Date().getTime() / 1000l);
        MessageCounter counter = new MessageCounter();
        counter.setDownlinkCounter(1);
        counter.setUplinkCounter(0);
        bodyServer.setMessageCounter(counter);
        DataEncodingType encodingType= new DataEncodingType();
        encodingType.setValue(DataEncodingType.EnumType.perUnaligned);
        bodyServer.setApplicationDataEncoding(encodingType);
        bodyServer.setApplicationDataProtocolVersion(1);
        bodyServer.setTestFlag(2);
        bodyServer.setApplicationDataLength(0l);

        requestServer.setApplicationData(new byte[]{});

        requestServer.setDispatcherHeader(headerServer);
        requestServer.setDispatcherBody(bodyServer);

        return requestServer;
    }

    private TCMP_OTARequest invokeTCMP(TCMP_OTARequest requestServer){
        try{

            ITCMPAdapterService tcmpAdapterService = new TCMPAdapterServiceImpl();
            String source = Cfg.PLATFORM_BT+new String(tcmpAdapterService.getBytesData(requestServer,"1"));

            LogHelper.info(LOGGER, "TSIP", "TCMP", "704", 1, null, null, source, null, null, null);

            String url = (String) GuiceContext.getInstance().getConfig().getProperties().get(SERVICE_KEY).get(URL_KEY);

            IClient client = ClientFactory.getClient(CLIENT_TYPE);
            String returnSource =  client.sendData(url, source);

            return tcmpAdapterService.receive(returnSource.getBytes());
        }catch (Exception e){
            throw new TSIPException(e);
        }
    }
}
