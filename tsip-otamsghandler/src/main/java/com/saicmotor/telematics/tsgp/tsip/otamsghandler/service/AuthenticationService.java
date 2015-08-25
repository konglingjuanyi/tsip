/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.service;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.framework.core.exception.ApiException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.TSIPException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.service.intercepter.InterceptorService;
import com.zxq.iov.cloud.sec.tvowner.api.ITAsUserLoginApi;
import com.zxq.iov.cloud.sec.tvowner.api.ITAvnAuthApi;
import com.zxq.iov.cloud.sec.tvowner.api.ITMPAuthApi;
import com.zxq.iov.cloud.sec.tvowner.api.dto.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

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
        String aid = convertAid(context, (byte[]) AdapterHelper.getProperty(context.getRequestObject(), "applicationData"), context.getClientVersion());
        String uid = context.getUid();
        String tiken =context.getToken();
        ITAsUserLoginApi iTASLoginVerifyService = (ITAsUserLoginApi) SpringContext.getInstance().getBean("iTASLoginVerifyService");
        AsLoginVerifyReq req  = new AsLoginVerifyReq();
        req.setAid(aid);
        req.setToken(tiken);
        req.setUid(uid);
        AsLoginVerifyResp resp = iTASLoginVerifyService.asLoginVerify(req);
        return resp.getResult();
    }

    private int checkAVNAuthority(RequestContext context) throws IOException {
        String aid = convertAid(context, (byte[]) AdapterHelper.getProperty(context.getRequestObject(), "applicationData"), context.getClientVersion());
        String uid = context.getUid();
        String token =context.getToken();
        if(StringUtils.isNotEmpty(uid))
            uid = uid.replaceAll("^(0+)", "");
        ITAvnAuthApi iTAvnLoginVerifyService = (ITAvnAuthApi) SpringContext.getInstance().getBean("iTAvnLoginVerifyService");
        AvnLoginVerifyReq req = new AvnLoginVerifyReq();
        req.setAid(aid);
        req.setToken(token);
        req.setUid(uid);
        AvnLoginVerifyResp resp = null;
        try {
            resp = iTAvnLoginVerifyService.avnLoginVerify(req);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return resp.getResult();
    }

    private int checkMPAuthority(RequestContext context) throws IOException {
        String aid = convertAid(context, (byte[]) AdapterHelper.getProperty(context.getRequestObject(), "applicationData"), context.getClientVersion());
        String uid = context.getUid();
        String token =context.getToken();
        if(StringUtils.isNotEmpty(uid))
            uid = uid.replaceAll("^(0+)", "");
        ITMPAuthApi iTMPLoginVerifyService = (ITMPAuthApi) SpringContext.getInstance().getBean("iTMPLoginVerifyService");
        com.zxq.iov.cloud.sec.tvowner.api.dto.MPLoginVerifyReq req = new com.zxq.iov.cloud.sec.tvowner.api.dto.MPLoginVerifyReq();
        req.setAid(aid);
        req.setToken(token);
        req.setUid(uid);
        MPLoginVerifyResp resp = null;
        try {
                resp = iTMPLoginVerifyService.mobileLoginVerify(req);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return resp.getResult();
    }

    private String convertAid(RequestContext context,byte[] appData,String version){

        InterceptorService service = SpringContext.getInstance().getBean(InterceptorService.class);
        return service.convertAid(context.getAid(), version, appData);
    }
}
