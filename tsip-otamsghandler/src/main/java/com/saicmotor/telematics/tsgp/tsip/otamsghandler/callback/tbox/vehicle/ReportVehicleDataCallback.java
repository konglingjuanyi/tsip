/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.tbox.vehicle;

import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTAEncoder;
import com.saicmotor.telematics.tsgp.otaadapter.tbox.v1_1.entity.dispatcher.TBOX_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.tbox.v1_1.entity.vehicle.RVMReq;
import com.saicmotor.telematics.tsgp.otaadapter.tbox.v1_1.entity.vehicle.RVMResp;
import com.saicmotor.telematics.tsgp.otaadapter.tbox.v1_1.service.ITBOXAdapterService;
import com.saicmotor.telematics.tsgp.otaadapter.tbox.v1_1.service.TBOXAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.OTACallback;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.MessageTemplate;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ApplicationException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ExceptionHandler;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.LogHelper;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * 上报车辆数据打印日志
 */
public class ReportVehicleDataCallback extends OTACallback{
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportVehicleDataCallback.class);
    public String invoke(MessageTemplate.Protocol protocol, Object requestObject) {
        if(true){
            ITBOXAdapterService tboxAdapterServices = new TBOXAdapterServiceImpl();
            TBOX_OTARequest tbox_otaRequest = tboxAdapterServices.receive(RequestContext.getContext().getSource().getBytes());
            byte[] appBytes = tbox_otaRequest.getApplicationData();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(appBytes);
            OTADecoder decoder = new OTADecoder(inputStream);
            RVMReq rvmReq = (RVMReq)decoder.decode(RVMReq.class);
        }
        //输出数据到日志
        LogHelper.reportDataInfo(RequestContext.getContext().getAid(), RequestContext.getContext().getMid(), RequestContext.getContext().getVin(), RequestContext.getContext().getSource());
        RVMResp rvmResp = new RVMResp();
        rvmResp.setIsNext(true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OTAEncoder encoder = new OTAEncoder(outputStream);
        encoder.encode(rvmResp);
        byte[] appBytes = outputStream.toByteArray();
        try {
            PropertyUtils.setNestedProperty(requestObject, "dispatcherBody.result", 0);
            PropertyUtils.setProperty(requestObject, "applicationData", appBytes);
            PropertyUtils.setNestedProperty(requestObject, "dispatcherBody.messageID", 2);
        } catch (Exception e) {
            return ExceptionHandler.processException(LOGGER, new ApplicationException("702", e));
        }

        //请求对象编码为字符串
        String returnString = changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), requestObject);
        return returnString;
    }
}
