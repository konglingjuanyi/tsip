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
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTAEncoder;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.resource.*;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.TSIPException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.LogHelper;
import com.saicmotor.telematics.tsgp.tsip.webservice.util.InvokeClient4TCMP;
import com.saicmotor.telematics.tsgp.tsip.webservice.util.RequestUtil4TCMP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: szksr
 * Date: 13-9-29
 * Time: 上午8:30
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ActiveInfoDownloadHandler extends AbstractTSPHandler{
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveInfoDownloadHandler.class);

    public SendDataResponse4TSP process(SendDataRequest4TSP parameters) {
        LogHelper.info(LOGGER, "ActiveInfoDownload", "TSIP", "807", 1, null, null, "", null, null, null);

        List<AttrModel> list = parameters.getApplicationData();
        SendDataResponse4TSP sendDataResponse = new SendDataResponse4TSP();
        int year=0,month=0;
        int result = 0;

        try{
            String fileDate = list.get(0).getValue().get(0);
            year = Integer.parseInt(fileDate.substring(0,4));
            month = Integer.parseInt(fileDate.substring(4));
        }catch (Exception e){
            LogHelper.error(LOGGER, "807", 1, null, null, "28070", e, null);
            sendDataResponse.getMessageHeader().setResult(28070);
            sendDataResponse.getMessageHeader().setErrorMessage("日期格式错误");
            return sendDataResponse;
        }
        MessageHeader messageHeader = parameters.getMessageHeader();
        //创建响应
        messageHeader.setMessageID(2);
        sendDataResponse.setMessageHeader(messageHeader);
        try {
            String resourceId = getFileResourceId(year,month);
            if(resourceId != null){
                String dataUrl = getDownloadUrl(resourceId);
                int pos = dataUrl.lastIndexOf("=");
                String downloadId =  dataUrl.substring(pos + 1);
                String url= (String) GuiceContext.getInstance().getConfig().getProperties().get("downloadUrl").get("value")+downloadId;
                AttrModel model =createModel("downloadUrl",url);
                List<AttrModel> applicationData = new ArrayList<AttrModel>();
                applicationData.add(model);
                sendDataResponse.getApplicationData().addAll(applicationData);
            }else{
                result = 28071;
                sendDataResponse.getMessageHeader().setErrorMessage("文件不存在");
            }
        } catch (Exception e) {
            LogHelper.error(LOGGER, "807", 1, null, null, "28070", e, null);
            result = 28072;
            sendDataResponse.getMessageHeader().setErrorMessage("TSP系统内部错误");
        }
        sendDataResponse.getMessageHeader().setResult(result);
        return sendDataResponse;
    }

    private byte[] encode(Object object){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OTAEncoder encoder = new OTAEncoder(outputStream);
        encoder.encode(object);
        return outputStream.toByteArray();
    }

    private ResourceDownloadReq prepareDownloadInfoReq(String resourceId){
        ResourceDownloadReq resourceDownloadReq = new ResourceDownloadReq();
        ResourceDownloadType resourceDownloadType = new ResourceDownloadType();
        resourceDownloadType.setValue(ResourceDownloadType.EnumType.file);
        resourceDownloadReq.setDownloadType(resourceDownloadType);
        resourceDownloadReq.setResourceId(resourceId);
        return resourceDownloadReq;
    }

    private Object decoder(Class clazz,byte[] appData) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(appData);
        OTADecoder decoder = new OTADecoder(inputStream);
        return decoder.decode(clazz);
    }

    private AttrModel createModel(String name,String value){
        AttrModel model = new AttrModel();
        model.setName(name);
        model.getValue().add(value);
        return model;
    }

    private ActiveInfoDownloadReq prepareFileInfoReq(int year,int month){
        ActiveInfoDownloadReq activeInfoDownloadReq = new ActiveInfoDownloadReq();
        activeInfoDownloadReq.setYear(String.valueOf(year));
        activeInfoDownloadReq.setMonth(String.valueOf(month));
        return activeInfoDownloadReq;
    }


    private String getFileResourceId(int year,int month) {
        InvokeClient4TCMP invokeClient = new InvokeClient4TCMP();
        ActiveInfoDownloadReq activeInfoDownloadReq = prepareFileInfoReq(year, month);
        byte[] appData = encode(activeInfoDownloadReq);
        TCMP_OTARequest request = RequestUtil4TCMP.createRequest("785");
        request.setApplicationData(appData);
        TCMP_OTARequest otaRequest = invokeClient.invoke(request);
        if(otaRequest.getDispatcherBody().getResult()==0){
            ActiveInfoDownloadResp activeInfoDownloadResp = (ActiveInfoDownloadResp)decoder(ActiveInfoDownloadResp.class,otaRequest.getApplicationData());
            return activeInfoDownloadResp.getResourceId();
        } else{
            return null;
        }
    }


    private String getDownloadUrl(String resourceId) {
        InvokeClient4TCMP invokeClient = new InvokeClient4TCMP();
        ResourceDownloadReq resourceDownloadReq = prepareDownloadInfoReq(resourceId);
        byte[] appData = encode(resourceDownloadReq);
        TCMP_OTARequest request = RequestUtil4TCMP.createRequest("761");
        request.setApplicationData(appData);
        TCMP_OTARequest otaRequest = invokeClient.invoke(request);
        if(otaRequest.getDispatcherBody().getResult()==0){
            ResourceDownloadResp resourceDownloadResp = (ResourceDownloadResp)decoder(ResourceDownloadResp.class,otaRequest.getApplicationData());
            return resourceDownloadResp.getDataUrl();
        } else{
            throw new TSIPException("获取下载url异常");
        }

    }
}
