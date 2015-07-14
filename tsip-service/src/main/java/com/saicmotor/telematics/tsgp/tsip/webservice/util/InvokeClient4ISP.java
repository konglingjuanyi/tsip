package com.saicmotor.telematics.tsgp.tsip.webservice.util;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTAEncoder;
import com.saicmotor.telematics.tsgp.otaadapter.isp.entity.dispatcher.ISP_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.isp.service.IISPAdapterService;
import com.saicmotor.telematics.tsgp.otaadapter.isp.service.ISPAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.dispatcher.Navi_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.service.INaviAdapterService;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.service.NaviAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.client.ClientFactory;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;

import javax.swing.*;
import java.io.ByteArrayOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: jwdsq
 * Date: 14-3-19
 * Time: 上午8:39
 * To change this template use File | Settings | File Templates.
 */
public class InvokeClient4ISP {
    String name = "ISP";
    String key = "url";
    String clientType = "http";

    public InvokeClient4ISP(String name, String key){
        this.name = name;
        this.key = key;
    }

    public InvokeClient4ISP(){

    }
    public InvokeClient4ISP(String clientType){
        this.clientType = clientType;
    }

    public InvokeClient4ISP(String clientType, String name, String key){
        this.name = name;
        this.key = key;
        this.clientType = clientType;
    }

    public ISP_OTARequest invoke(String aid, Object object) throws Exception{
        ISP_OTARequest otaRequest = RequestUtil4ISP.createRequest(aid);
        String source = createRequestSource(object,otaRequest);
        return invoke(source);
    }

    public ISP_OTARequest invoke(String source) throws Exception{
//        String url = GuiceContext.getInstance().getConfig().getProperties().get(name).get(key).toString();
        String url = SpringContext.getInstance().getProperty(name+"."+key);
        return decoderReturn(ClientFactory.getClient(clientType).sendData(url,source));
    }

    public byte[] getAppData(Object object){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OTAEncoder encoder = new OTAEncoder(outputStream);
        encoder.encode(object);
        return outputStream.toByteArray();
    }

    public String createRequestSource(ISP_OTARequest otaRequest){
        IISPAdapterService adapterService = SpringContext.getInstance().getBean(ISPAdapterServiceImpl.class);
        return Cfg.PLATFORM_ISP+new String(adapterService.getBytesData(otaRequest, "1"));
    }

    private String createRequestSource(Object object,ISP_OTARequest otaRequest){
        otaRequest.setApplicationData(getAppData(object));
        return  createRequestSource(otaRequest);
    }

    private ISP_OTARequest decoderReturn(String returnSource){
        IISPAdapterService adapterService = SpringContext.getInstance().getBean(ISPAdapterServiceImpl.class);
        return adapterService.receive(returnSource.getBytes());
    }

}
