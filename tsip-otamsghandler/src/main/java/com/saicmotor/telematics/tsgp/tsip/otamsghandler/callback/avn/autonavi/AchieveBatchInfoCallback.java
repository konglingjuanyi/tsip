/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.avn.autonavi;

import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTAEncoder;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.autonavi.AchieveBatchInfoReq;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.autonavi.AchieveBatchInfoResp;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.dispatcher.AVN_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.service.AVNAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.service.IAVNAdapterService;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.OTACallback;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.MessageTemplate;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.AdapterException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ApplicationException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.TSIPException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.BitReader;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.LogHelper;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 处理博泰天气服务请求的Callback
 * Author: cqzzl
 */
public class AchieveBatchInfoCallback extends OTACallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(AchieveBatchInfoCallback.class);
    //开发实录
    //1.OTA转换
    //1.查询vin 是否注册服务，否则注册
    //1.查询临时ID是否过期，否者重新获取临时ID 5 * 60 + 50 ，过期最长时间6小时
    //1.请求数据批次信息
    //TODO 1.日志 批次请求记录
    //1.异常
    //_id,vin,regTime,duration,firstTime,status,loginCount,batchCount,trafficCount
    //_id,vin,validateId,expireTime，createTime

    @Override
    protected String invokeService(MessageTemplate.Protocol protocol, String source) throws ApplicationException {
        String serverPlatform = protocol.getServicePlatform();
        //String errorCode = Cfg.PL_ERROR_CODE_MAP.get(serverPlatform);
        String platformStr = Cfg.getPlatformStrByID(serverPlatform);

        try{
            return invokeBatchInfo(source);
        }catch(Exception e){
            ApplicationException ex = new ApplicationException("32090",e);
            LogHelper.error(LOGGER,RequestContext.getContext(),ex);
            throw ex;
        }
    }
    protected Object changeClientObj2ServerObj(MessageTemplate.Protocol protocol, Object request) {
        try {
            Object serviceObj = transformRequestObj2ServiceObj(protocol, request);

            //把为tbox生成的伪token设置到服务端的请求，用于追踪日志
            if (Cfg.PLATFORM_TBOX.equals(RequestContext.getContext().getPlatform())) {
                PropertyUtils.setProperty(serviceObj, "dispatcherBody.token", RequestContext.getContext().getToken());
            }

            PropertyUtils.setNestedProperty(serviceObj, "dispatcherBody.applicationID", protocol.getSpAppID());
            PropertyUtils.setNestedProperty(serviceObj, "dispatcherBody.messageID", protocol.getSpMsgID());
            PropertyUtils.setNestedProperty(serviceObj, "dispatcherHeader.protocolVersion", BitReader.toIntegerProtocol(protocol.getServiceVersion()));
//            PropertyUtils.setNestedProperty(serviceObj, "dispatcherBody.platformID", "0000000" + RequestContext.getContext().getPlatform());
            return serviceObj;
        } catch (Exception e) {
            throw new AdapterException("转换客户端请求对象到服务器端出错", e);
        }
    }

    private String invokeBatchInfo(String source) throws IOException {
        IAVNAdapterService adapterService = new AVNAdapterServiceImpl();
        AVN_OTARequest otaRequestAVN = adapterService.receive(source.getBytes());
        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(otaRequestAVN.getApplicationData());
        OTADecoder decoder = new OTADecoder(inputStream1);
        AchieveBatchInfoReq achieveBatchInfoReq = (AchieveBatchInfoReq)decoder.decode(AchieveBatchInfoReq.class);
        String cityNum = achieveBatchInfoReq.getCityNum();
        String vin = otaRequestAVN.getDispatcherBody().getVin();
        try{
            ANValidate anValidate = AutoNaviUtil.gerANValidate(vin);
            byte[] result = AutoNaviUtil.getBatchInfo(anValidate.getValidateId(),cityNum);

            AutoNaviMongoService.updateCount4ANInfo(vin,Constant4AutoNavi.ANInfo_BatchCount);
            AchieveBatchInfoResp achieveBatchInfoResp = new AchieveBatchInfoResp();
            achieveBatchInfoResp.setBatchInfo(result);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            OTAEncoder encoder = new OTAEncoder(outputStream);
            encoder.encode(achieveBatchInfoResp);
            byte[] bytes = outputStream.toByteArray();
            otaRequestAVN.setApplicationData(bytes);
            otaRequestAVN.getDispatcherBody().setApplicationDataLength(Long.valueOf(bytes.length));
            otaRequestAVN.getDispatcherBody().setMessageID(2);
            otaRequestAVN.getDispatcherBody().setResult(0);
        }catch (TSIPException e){
            otaRequestAVN.setApplicationData(new byte[0]);
            otaRequestAVN.getDispatcherBody().setApplicationDataLength(0L);
            otaRequestAVN.getDispatcherBody().setMessageID(2);
            otaRequestAVN.getDispatcherBody().setResult(Integer.valueOf(e.getCode()));
            LogHelper.error(LOGGER,RequestContext.getContext(),e);
        }

        return new String(adapterService.getBytesData(otaRequestAVN,"1"));
    }
}
