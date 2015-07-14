/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.webservice.util;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTAEncoder;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.service.ITCMPAdapterService;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.service.TCMPAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.client.ClientFactory;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.TSIPException;

import java.io.ByteArrayOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: szksr
 * Date: 13-9-16
 * Time: 下午1:14
 * To change this template use File | Settings | File Templates.
 */
public class InvokeClient4TCMP {
    String name = "TCMP";
    String key = "url";
    String clientType = "http";

    public InvokeClient4TCMP(String name, String key){
        this.name = name;
        this.key = key;
    }

    public InvokeClient4TCMP(){

    }
    public InvokeClient4TCMP(String clientType){
        this.clientType = clientType;
    }

    public InvokeClient4TCMP(String clientType, String name, String key){
        this.name = name;
        this.key = key;
        this.clientType = clientType;
    }

    public TCMP_OTARequest invoke(String source) {
        try {
//            String url = GuiceContext.getInstance().getConfig().getProperties().get(name).get(key).toString();
            String url = SpringContext.getInstance().getProperty(name+"."+key);
            return decoderReturn(ClientFactory.getClient(clientType).sendData(url,source));
        } catch (Exception e) {
            throw new TSIPException("调用tcmp异常", e);
        }
    }

    public TCMP_OTARequest invoke(TCMP_OTARequest otaRequest) {
        try {
//            String url = GuiceContext.getInstance().getConfig().getProperties().get(name).get(key).toString();
            String url = SpringContext.getInstance().getProperty(name+"."+key);
            String source = createRequestSource(otaRequest);
            return decoderReturn(ClientFactory.getClient(clientType).sendData(url,source));
        } catch (Exception e) {
            throw new TSIPException("调用tcmp异常", e);
        }
    }

    public byte[] getAppData(Object object){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OTAEncoder encoder = new OTAEncoder(outputStream);
        encoder.encode(object);
        return outputStream.toByteArray();
    }

    public String createRequestSource(TCMP_OTARequest otaRequest){
        ITCMPAdapterService adapterService = SpringContext.getInstance().getBean(TCMPAdapterServiceImpl.class);
        return Cfg.PLATFORM_BT+new String(adapterService.getBytesData(otaRequest, "1"));
    }

    private TCMP_OTARequest decoderReturn(String returnSource){
        ITCMPAdapterService adapterService = SpringContext.getInstance().getBean(TCMPAdapterServiceImpl.class);
        return adapterService.receive(returnSource.getBytes());
    }


}

