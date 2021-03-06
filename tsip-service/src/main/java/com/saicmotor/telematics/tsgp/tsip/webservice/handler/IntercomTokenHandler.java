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
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.login.AVN_UserTokenVerifyResp;
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
 * Time: 下午1:20
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class IntercomTokenHandler extends AbstractTSPHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntercomTokenHandler.class);

    public SendDataResponse4TSP process(SendDataRequest4TSP parameters) {
        LogHelper.info(LOGGER, "Intercom", "TSIP", "803", 1, null, null, "", null, null, null);
        int result = 0;
        SendDataResponse4TSP sendDataResponse =  new SendDataResponse4TSP();;
        List<AttrModel> applicationData = null;
        String token = null,uid;
        try {
            List<AttrModel> list = parameters.getApplicationData();
            token = list.get(0).getValue().get(0);
            uid = list.get(1).getValue().get(0);
            MessageHeader messageHeader = parameters.getMessageHeader();
            messageHeader.setMessageID(2);
            sendDataResponse.setMessageHeader(messageHeader);
            //调用TCMP接口验证token
            //创建请求
            TCMP_OTARequest request = RequestUtil4TCMP.createRequest("702");
            request.getDispatcherBody().setToken(token);
            request.getDispatcherBody().setUid(convert(uid));
            InvokeClient4TCMP invokeClient = new InvokeClient4TCMP();
            TCMP_OTARequest otaRequest = invokeClient.invoke(request);

            if (otaRequest.getDispatcherBody().getResult() != 0 ) {
                result = 28032;
                sendDataResponse.getMessageHeader().setErrorMessage("TSP系统内部错误");
            } else {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(otaRequest.getApplicationData());
                OTADecoder decoder = new OTADecoder(inputStream);
                AVN_UserTokenVerifyResp avnUserTokenVerifyReq = (AVN_UserTokenVerifyResp)decoder.decode(AVN_UserTokenVerifyResp.class);

                if (!avnUserTokenVerifyReq.getVerificationFlag()) {
                    result = 28030;
                    sendDataResponse.getMessageHeader().setErrorMessage("Token无效");
                } else {
                    AttrModel model = new AttrModel();
                    model.setName("tokenExpiration");
                    model.getValue().add(avnUserTokenVerifyReq.getTokenExpiration().getSeconds().toString());
                    applicationData = new ArrayList<AttrModel>();
                    applicationData.add(model);
                }
            }
        } catch (Exception e) {
            LogHelper.error(LOGGER, "803", 1, null, null, "28032", e, token);
            result = 28032;
            sendDataResponse.getMessageHeader().setErrorMessage("TSP系统内部错误");
        }

        sendDataResponse.getMessageHeader().setResult(result);
        if (applicationData != null) {
            sendDataResponse.getApplicationData().addAll(applicationData);
        }

        return sendDataResponse;
    }
    private String convert(String uid){
        String appendStr= "00000000000000000000000000000000000000000000000000";
        int length = uid.length();
        return appendStr.substring(length)+uid;
    }
}
