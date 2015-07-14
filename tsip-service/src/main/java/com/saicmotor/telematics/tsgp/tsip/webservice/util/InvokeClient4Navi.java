/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.webservice.util;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTAEncoder;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.dispatcher.Navi_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.service.INaviAdapterService;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.service.NaviAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.client.ClientFactory;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.TSIPException;

import java.io.ByteArrayOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: szksr
 * Date: 13-8-29
 * Time: 上午10:00
 * To change this template use File | Settings | File Templates.
 */
public class InvokeClient4Navi {
    String name = "Navi";
    String key = "url";
    String clientType = "http";

    public InvokeClient4Navi(String name, String key){
        this.name = name;
        this.key = key;
    }

    public InvokeClient4Navi(){

    }
    public InvokeClient4Navi(String clientType){
        this.clientType = clientType;
    }

    public InvokeClient4Navi(String clientType, String name, String key){
        this.name = name;
        this.key = key;
        this.clientType = clientType;
    }

    public Navi_OTARequest invoke(String aid, Object object) {
        Navi_OTARequest otaRequest = RequestUtil4Navi.createRequest(aid);
        String source = createRequestSource(object,otaRequest);
        return invoke(source);
    }

    public Navi_OTARequest invoke(String source) {
        try {
//            String url = GuiceContext.getInstance().getConfig().getProperties().get(name).get(key).toString();
            String url = SpringContext.getInstance().getProperty(name+"."+key);
            return decoderReturn(ClientFactory.getClient(clientType).sendData(url,source));
        } catch (Exception e) {
            throw new TSIPException("调用navi异常", e);
        }
    }

    public byte[] getAppData(Object object){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OTAEncoder encoder = new OTAEncoder(outputStream);
        encoder.encode(object);
        return outputStream.toByteArray();
    }

    public String createRequestSource(Navi_OTARequest otaRequest){
        INaviAdapterService adapterService = SpringContext.getInstance().getBean(NaviAdapterServiceImpl.class);
        return Cfg.PLATFORM_BT+new String(adapterService.getBytesData(otaRequest, "1"));
    }

    private String createRequestSource(Object object,Navi_OTARequest otaRequest){
        otaRequest.setApplicationData(getAppData(object));
        return  createRequestSource(otaRequest);
    }

    private Navi_OTARequest decoderReturn(String returnSource){
        INaviAdapterService adapterService = SpringContext.getInstance().getBean(NaviAdapterServiceImpl.class);
        return adapterService.receive(returnSource.getBytes());
    }

}
