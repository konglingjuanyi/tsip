/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.avn.autonavi;

import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTAEncoder;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.autonavi.AchieveTrafficInfoReq;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.autonavi.AchieveTrafficInfoResp;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.autonavi.CityNum;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.autonavi.TrafficInfo;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 处理博泰天气服务请求的Callback
 * Author: cqzzl
 */
public class AchieveTrafficInfoCallback extends OTACallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(AchieveTrafficInfoCallback.class);

    @Override
    protected String invokeService(MessageTemplate.Protocol protocol, String source) throws ApplicationException {
        String serverPlatform = protocol.getServicePlatform();
        //String errorCode = Cfg.PL_ERROR_CODE_MAP.get(serverPlatform);
        String platformStr = Cfg.getPlatformStrByID(serverPlatform);
        String url = "";
        String token = RequestContext.getContext().getToken();
        LogHelper.info(LOGGER, RequestContext.getContext().getAid(), RequestContext.getContext().getMid(), RequestContext.getContext().getVin(),
                RequestContext.getContext().getUid(), source, "TSIP_AutoNavi", "", url, platformStr, token);
        try{
            return invokeTrafficInfo(source);
        }catch(Exception e){
            ApplicationException ex = new ApplicationException(32100,e);
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

    private String invokeTrafficInfo(String source) throws IOException {
        IAVNAdapterService adapterService = new AVNAdapterServiceImpl();
        AVN_OTARequest otaRequestAVN = adapterService.receive(source.getBytes());
        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(otaRequestAVN.getApplicationData());
        OTADecoder decoder = new OTADecoder(inputStream1);
        AchieveTrafficInfoReq achieveTrafficInfoReq = (AchieveTrafficInfoReq)decoder.decode(AchieveTrafficInfoReq.class);
        Collection<CityNum> cityNumCollection = achieveTrafficInfoReq.getCityNums();
        CityNum cityNums[] = cityNumCollection.toArray(new CityNum[cityNumCollection.size()]);
        String achieveType = achieveTrafficInfoReq.getAchieveType();
        String achieveCode = achieveTrafficInfoReq.getAchieveCode();
        String vin = otaRequestAVN.getDispatcherBody().getVin();
        try{
        ANValidate anValidate = AutoNaviUtil.gerANValidate(vin);

        byte[] result = new byte[0];
        //增加其他类型信息获取
        //TODO 增加异常码处理
        //traffictype, eventtype, locationtype ,intercitytype
        if("traffictype".equals(achieveType)){
            result = AutoNaviUtil.getTrafficInfo(anValidate.getValidateId(),cityNums[0].getCityNum(),achieveCode);

            AutoNaviMongoService.updateCount4ANInfo(vin,achieveCode.equals("0") ? Constant4AutoNavi.ANInfo_TrafficCount : Constant4AutoNavi.ANInfo_TrafficCount2);
        }else if("eventtype".equals(achieveType)){
            result = AutoNaviUtil.getEventType(anValidate.getValidateId(),cityNums[0].getCityNum(),achieveCode);
            AutoNaviMongoService.updateCount4ANInfo(vin,Constant4AutoNavi.ANInfo_EventTypeCount);
        }else if("locationtype".equals(achieveType)){
            result = AutoNaviUtil.getLocationType(anValidate.getValidateId(),cityNums[0].getCityNum(),achieveCode);
            AutoNaviMongoService.updateCount4ANInfo(vin,Constant4AutoNavi.ANInfo_LocationTypeCount);
        }else if("intercitytype".equals(achieveType)){
            result = AutoNaviUtil.getInterCityType(anValidate.getValidateId(),cityNums,achieveCode);
            AutoNaviMongoService.updateCount4ANInfo(vin,achieveCode.equals("0") ? Constant4AutoNavi.ANInfo_IntercityTypeCount : Constant4AutoNavi.ANInfo_IntercityTypeCount2);
        }else{
            //TODO 获取服务不存在
            throw new TSIPException("32101");
        }

        int maxLen = 1024 * 16 - 1;
        int len = result.length;
        AchieveTrafficInfoResp achieveTrafficInfoResp = new AchieveTrafficInfoResp();
        List<TrafficInfo> trafficInfoList = new ArrayList<TrafficInfo>();
        int listSize = len/maxLen + (len%maxLen == 0 ? 0 : 1);
        for(int i = 0;i<listSize;i++){
            TrafficInfo trafficInfo = new TrafficInfo();
            if((i+ 1)*maxLen>=len){
                trafficInfo.setInfoPart(AutoNaviUtil.subBytes(result,i * maxLen,len - i * maxLen));
            }else{
                trafficInfo.setInfoPart(AutoNaviUtil.subBytes(result,i * maxLen,maxLen));
            }
            trafficInfoList.add(trafficInfo);
        }
        achieveTrafficInfoResp.setTrafficInfoList(trafficInfoList);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OTAEncoder encoder = new OTAEncoder(outputStream);
        encoder.encode(achieveTrafficInfoResp);
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
