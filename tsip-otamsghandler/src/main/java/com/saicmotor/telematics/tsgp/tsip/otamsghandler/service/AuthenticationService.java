/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.service;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTAEncoder;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.login.ASLoginVerifyReq;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.login.AVNLoginVerifyReq;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.login.MPLoginVerifyReq;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.service.ITCMPAdapterService;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.service.TCMPAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.client.ClientFactory;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.client.IClient;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.TSIPException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.RequestUtil4TCMP;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.service.intercepter.InterceptorService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 权限校验Service
 * @author : zhuxiaoyan
 */
@Service
public class AuthenticationService {

    /**
     * 权限验证，暂时只验证AVN和MP的权限
     * yicni 添加售后的权限校验
     * @param context 请求上下文
     * @return 返回调用TCMP验证的resultCode，非0即为验证失败
     */
    public int check(RequestContext context) {
        try {
            if(Cfg.PLATFORM_AVN.equals(context.getPlatform())){
                return checkAVNAuthority(context);
            }
            if(Cfg.PLATFORM_MP.equals(context.getPlatform())){
                return checkMPAuthority(context);
            }
            if(Cfg.PLATFORM_WXG4AS.equals(context.getPlatform())){
                return checkWXG4ASAuthority(context);
            }else {
                //其他平台一律通过
                return 0;
            }

        } catch (Exception e) {
            throw new TSIPException("权限验证异常", e);
        }
    }

    private int checkWXG4ASAuthority(RequestContext context) throws IOException {
        TCMP_OTARequest request = RequestUtil4TCMP.createRequest(Cfg.WXG4AS_VERIFY_AID, context);
        ASLoginVerifyReq asLoginVerifyReq = new ASLoginVerifyReq();
        asLoginVerifyReq.setAid(convertAid(context, (byte[]) AdapterHelper.getProperty(context.getRequestObject(), "applicationData"), context.getClientVersion()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OTAEncoder encoder = new OTAEncoder(outputStream);
        encoder.encode(asLoginVerifyReq);
        byte[] appBytes = outputStream.toByteArray();
        request.setApplicationData(appBytes);
        TCMP_OTARequest verify = invokeTCMP(request);
        return verify.getDispatcherBody().getResult();
    }

    private int checkAVNAuthority(RequestContext context) throws IOException {
        TCMP_OTARequest request = RequestUtil4TCMP.createRequest(Cfg.AVN_VERIFY_AID, context);
        AVNLoginVerifyReq avnLoginVerifyReq = new AVNLoginVerifyReq();
        avnLoginVerifyReq.setAid(convertAid(context, (byte[]) AdapterHelper.getProperty(context.getRequestObject(), "applicationData"), context.getClientVersion()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OTAEncoder encoder = new OTAEncoder(outputStream);
        encoder.encode(avnLoginVerifyReq);
        byte[] appBytes = outputStream.toByteArray();
        request.setApplicationData(appBytes);
        TCMP_OTARequest verify = invokeTCMP(request);
        return verify.getDispatcherBody().getResult();
    }

    private int checkMPAuthority(RequestContext context) throws IOException {
        TCMP_OTARequest request = RequestUtil4TCMP.createRequest(Cfg.MP_VERIFY_AID, context);
        MPLoginVerifyReq mpLoginVerifyReq = new MPLoginVerifyReq();
        mpLoginVerifyReq.setAid(convertAid(context, (byte[]) AdapterHelper.getProperty(context.getRequestObject(), "applicationData"), context.getClientVersion()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OTAEncoder encoder = new OTAEncoder(outputStream);
        encoder.encode(mpLoginVerifyReq);
        byte[] appBytes = outputStream.toByteArray();
        request.setApplicationData(appBytes);
        TCMP_OTARequest verify = invokeTCMP(request);
        return verify.getDispatcherBody().getResult();
    }

    private TCMP_OTARequest invokeTCMP(TCMP_OTARequest request)throws IOException{
        ITCMPAdapterService adapterService =  SpringContext.getInstance().getBean(TCMPAdapterServiceImpl.class);
        byte[] bytes = adapterService.getBytesData(request, "1");
        String source=Cfg.PLATFORM_TCMP+new String(bytes, "UTF-8");
//        String url = (String) SpringContext.getInstance().getConfig().getProperties().get("TCMP").get("url");
        String url = SpringContext.getInstance().getProperty("TCMP.url");
        IClient client = ClientFactory.getClient(ClientFactory.HTTP);
        String returnSource = client.sendData(url,source);
        return adapterService.receive(returnSource.getBytes());
    }

    private String convertAid(RequestContext context,byte[] appData,String version){

        InterceptorService service = SpringContext.getInstance().getBean(InterceptorService.class);
        return service.convertAid(context.getAid(), version, appData);
    }
}
