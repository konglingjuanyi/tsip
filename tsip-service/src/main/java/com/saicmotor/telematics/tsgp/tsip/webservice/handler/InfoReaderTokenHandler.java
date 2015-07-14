/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.webservice.handler;

import com.saicmotor.mce550.tsgp.tsip.msg.v1.AttrModel;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.MessageHeader;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.SendDataRequest4TSP;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.SendDataResponse4TSP;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.login.AVNTokenVerifyResp;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_OTARequest;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.LogHelper;
import com.saicmotor.telematics.tsgp.tsip.webservice.util.InvokeClient4TCMP;
import com.saicmotor.telematics.tsgp.tsip.webservice.util.RequestUtil4TCMP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: szksr
 * Date: 13-9-16
 * Time: 下午1:42
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class InfoReaderTokenHandler extends AbstractTSPHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfoReaderTokenHandler.class);

    public SendDataResponse4TSP process(SendDataRequest4TSP parameters) {
        List<AttrModel> list = parameters.getApplicationData();
        String token = list.get(0).getValue().get(0);
        MessageHeader messageHeader = parameters.getMessageHeader();
        LogHelper.info(LOGGER, "InfoReader", "TSIP", "805", 1, null, null, "", null, null, token);
        //创建响应
        SendDataResponse4TSP sendDataResponse = new SendDataResponse4TSP();
        messageHeader.setMessageID(2);
        sendDataResponse.setMessageHeader(messageHeader);

        int result = 0;

        List<AttrModel> applicationData = null;

        try {
            //调用TCMP接口验证token
            //创建请求
            TCMP_OTARequest request = RequestUtil4TCMP.createRequest("704");
            request.getDispatcherBody().setToken(token);
            InvokeClient4TCMP invokeClient = new InvokeClient4TCMP();
            TCMP_OTARequest otaRequest = invokeClient.invoke(request);


            if (otaRequest.getDispatcherBody().getResult() != 0 ) {
                result = 28052;
                sendDataResponse.getMessageHeader().setErrorMessage("TSP系统内部错误");
                LogHelper.error(LOGGER, "805", 1, null, null, result+"TSP系统内部错误", null, token);
            } else {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(otaRequest.getApplicationData());
                OTADecoder decoder = new OTADecoder(inputStream);
                AVNTokenVerifyResp avnTokenVerifyResp = (AVNTokenVerifyResp)decoder.decode(AVNTokenVerifyResp.class);

                if (!avnTokenVerifyResp.getVerificationFlag()) {
                    result = 28050;
                    sendDataResponse.getMessageHeader().setErrorMessage("Token无效");
                    LogHelper.error(LOGGER, "805", 1, null, null, result+"Token无效", null, token);
                } else {
                    AttrModel model = new AttrModel();
                    model.setName("tokenExpiration");
                    model.getValue().add(avnTokenVerifyResp.getTokenExpiration().getSeconds().toString());
                    applicationData = new ArrayList<AttrModel>();
                    applicationData.add(model);
                }
            }
        } catch (Exception e) {
            result = 28052;
            sendDataResponse.getMessageHeader().setErrorMessage("TSP系统内部错误");

            LogHelper.error(LOGGER, "805", 1, null, null, "28052", e, token);
        }

        sendDataResponse.getMessageHeader().setResult(result);
        if (applicationData != null) {
            sendDataResponse.getApplicationData().addAll(applicationData);
        }


        return sendDataResponse;
    }
}