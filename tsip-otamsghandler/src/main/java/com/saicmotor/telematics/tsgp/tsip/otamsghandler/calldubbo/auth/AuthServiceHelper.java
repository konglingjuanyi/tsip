package com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.auth;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.framework.core.exception.ApiException;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.dispatcher.AVN_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.dispatcher.MP_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.login.MPVerificationCheckReq;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.login.TCMP_VerificationReq;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.ServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common.AuthServiceEnum;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common.HelperUtils;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.zxq.iov.cloud.sec.tvowner.api.ITAvnAuthApi;
import com.zxq.iov.cloud.sec.tvowner.api.dto.*;
import com.zxq.iov.cloud.sp.mds.tcmp.api.IGetMobileDynamicPasswordApi;
import com.zxq.iov.cloud.sp.mds.tcmp.api.IMobileVerificationAuthApi;
import com.zxq.iov.cloud.sp.mds.tcmp.api.dto.MPVerificationCheckResp;
import com.zxq.iov.cloud.sp.mds.tcmp.api.dto.MPVerificationReq;
import com.zxq.iov.cloud.sp.mds.tcmp.api.dto.MPVerificationResp;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by Administrator on 2015/7/28.
 */
@Service
public class AuthServiceHelper implements ServiceHelper {

    @Override
    public String callDubboService(RequestContext context) throws ApiException {
        String aid = context.getAid();
        String vin = context.getVin();
        String uid = context.getUid();
        String token = context.getToken();
        String requestBack = null;
        if(StringUtils.isNotEmpty(uid))
            uid = uid.replaceAll("^(0+)", "");

        //AVN用户登录验证
        if(AuthServiceEnum.USERLOGINAUTH.getAid().equals(aid)){
            AVN_OTARequest request = (AVN_OTARequest) context.getRequestObject();
            byte[] appBytes = request.getApplicationData();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(appBytes);
            OTADecoder decoder = new OTADecoder(inputStream);

            com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.login.AVN_UserLoggingInReq avnUserLoggingInReq = (com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.login.AVN_UserLoggingInReq)decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.login.AVN_UserLoggingInReq.class);
            com.zxq.iov.cloud.sec.tvowner.api.ITAvnAuthApi avnAuthApi = (ITAvnAuthApi) SpringContext.getInstance().getBean("avnAuthApi");
            AvnUserLoggingInReq req = new AvnUserLoggingInReq();
            req.setPassword(avnUserLoggingInReq.getPassword());
            req.setToken(token);
            req.setUid(uid);
            req.setVin(vin);
            AvnUserLoggingInResp resp = avnAuthApi.avnUserLoginAuth(req);
            com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.login.AVN_UserLoggingInResp avnUserLoggingInResp = new com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.login.AVN_UserLoggingInResp();
            avnUserLoggingInResp.setToken(resp.getToken());
            Date tokenExpiration = resp.getTokenExpiration();
            if(null != tokenExpiration) {
                com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.Timestamp tp = new com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.Timestamp();
                tp.setSeconds(tokenExpiration.getTime()/1000);
                avnUserLoggingInResp.setTokenExpiration(tp);
            }
            String newUid = resp.getUid();
            int lenth = newUid.length();
            if(StringUtils.isNotEmpty(newUid)){
                for(int i=0;i<(50-lenth);i++){
                    newUid = "0"+newUid;
                }
            }
            request.getDispatcherBody().setUid(newUid);
            request = HelperUtils.enCode_AVN_OTARequest(avnUserLoggingInResp, request);
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), request);
        }
        //AVN设备登陆验证
        if(AuthServiceEnum.EQUIPLOGINAUTH.getAid().equals(aid)){
            AVN_OTARequest request = (AVN_OTARequest) context.getRequestObject();
            byte[] appBytes = request.getApplicationData();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(appBytes);
            OTADecoder decoder = new OTADecoder(inputStream);

            com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.login.AVN_LoggingInReq avnLoggingInReq = (com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.login.AVN_LoggingInReq)decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.login.AVN_LoggingInReq.class);
            com.zxq.iov.cloud.sec.tvowner.api.ITAvnAuthApi avnAuthApi = (ITAvnAuthApi) SpringContext.getInstance().getBean("avnAuthApi");
            AvnLoggingInReq req = new AvnLoggingInReq();
            req.setVin(vin);
            req.setAvnSN(avnLoggingInReq.getAvnSN());
            if(null != avnLoggingInReq.getCurrentKM())
                req.setCurrentKM(avnLoggingInReq.getCurrentKM().toString());
            req.setSignalBookVer(avnLoggingInReq.getSignalBookVer());
            req.setTboxSN(avnLoggingInReq.getTboxSN());
            req.setTboxVer(avnLoggingInReq.getTboxVer());
            req.setWcdmaVer(avnLoggingInReq.getWcdmaVer());
            AvnLoggingInResp resp = avnAuthApi.avnEquipLoginAuth(req);
            com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.login.AVN_LoggingInResp avnLoggingInResp = new com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.login.AVN_LoggingInResp();
            avnLoggingInResp.setToken(resp.getToken());
            avnLoggingInResp.setMaintenanceFlag(resp.isMaintenanceFlag());
            avnLoggingInResp.setSignalBookFlag(resp.isSignalBookFlag());
            avnLoggingInResp.setTboxFlag(resp.isTboxFlag());
            Date tspServiceExpiration = resp.getTspServiceExpiration();
            if(null != tspServiceExpiration) {
                com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.Timestamp tp = new com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.Timestamp();
                tp.setSeconds(tspServiceExpiration.getTime()/1000);
                String dd = tp.toString();
                avnLoggingInResp.setTspServiceExpiration(tp);
            }
            avnLoggingInResp.setWcdmaFlag(resp.isWcdmaFlag());
            request = HelperUtils.enCode_AVN_OTARequest(avnLoggingInResp, request);
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), request);

        }
        //手机登录验证
        if(AuthServiceEnum.MOBILELOGINVERIFY.getAid().equals(aid)){
            MP_OTARequest request = (MP_OTARequest) context.getRequestObject();
            byte[] appBytes = request.getApplicationData();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(appBytes);
            OTADecoder decoder = new OTADecoder(inputStream);

            com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_1.MP_UserLoggingInReq mpUserLoggingInReq = (com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_1.MP_UserLoggingInReq)decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_1.MP_UserLoggingInReq.class);
            com.zxq.iov.cloud.sec.tvowner.api.ITMPAuthApi mPAuthApi = (com.zxq.iov.cloud.sec.tvowner.api.ITMPAuthApi) SpringContext.getInstance().getBean("mPAuthApi");
            MPUserLoggingInReq req = new MPUserLoggingInReq();
            req.setUid(uid);
            req.setDeviceId(mpUserLoggingInReq.getDeviceId());
            req.setPassword(mpUserLoggingInReq.getPassword());
            MPUserLoggingInResp resp = mPAuthApi.mobileLoginAuth(req);
            com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_1.MP_UserLoggingInResp mpUserLoggingInResp = new com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_1.MP_UserLoggingInResp();
            mpUserLoggingInResp.setToken(resp.getToken());
            Date tokenExpiration = resp.getTokenExpiration();
            if(null != tokenExpiration) {
                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.Timestamp tp = new com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.Timestamp();
                tp.setSeconds(tokenExpiration.getTime()/1000);
                mpUserLoggingInResp.setTokenExpiration(tp);
            }
            Collection<com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_1.VinInfo> vinList = new ArrayList<com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_1.VinInfo>();
            List<Vin> vins = resp.getVinList();
            if(null != vins) {
                for (Vin v : vins) {
                    com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_1.VinInfo vinInfo = new com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_1.VinInfo();
                    vinInfo.setIsAcivate(v.isAcivate());
                    try {
                        vinInfo.setName(v.getName().getBytes("UTF-8"));
                        vinInfo.setModelName(v.getModelName().getBytes("UTF-8"));
                        vinInfo.setBrandName(v.getBrandName().getBytes("UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    vinInfo.setSeries(v.getSeries());
                    vinInfo.setVin(v.getVin());
                    vinInfo.setVehiclePhoto(v.getVehiclePhoto());
                    vinList.add(vinInfo);
                }
            }
            if(null != vinList && vinList.size()>0)
                mpUserLoggingInResp.setVinList(vinList);
            mpUserLoggingInResp.setUserName(resp.getUserName());
            mpUserLoggingInResp.setUserPhoto(String.valueOf(resp.getUserPhoto()));
            String newUid = resp.getUid();
            int lenth = newUid.length();
            if(StringUtils.isNotEmpty(newUid)){
                for(int i=0;i<(50-lenth);i++){
                    newUid = "0"+newUid;
                }
            }
            request.getDispatcherBody().setUid(newUid);
            request = HelperUtils.enCode_MP_OTARequest(mpUserLoggingInResp, request);
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(),request);

        }
        //获取手机动态密码
        if(AuthServiceEnum.GETMOBILEDYNAMICPASSWORD.getAid().equals(aid)){

            MP_OTARequest request = (MP_OTARequest) context.getRequestObject();
            byte[] appBytes = request.getApplicationData();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(appBytes);
            OTADecoder decoder = new OTADecoder(inputStream);

            TCMP_VerificationReq tcmpVerificationReq = (TCMP_VerificationReq)decoder.decode(TCMP_VerificationReq.class);
            IGetMobileDynamicPasswordApi getMobileDynamicPasswordApi = (IGetMobileDynamicPasswordApi) SpringContext.getInstance().getBean("getMobileDynamicPasswordApi");
            MPVerificationReq req = new MPVerificationReq();

            int operationTypeValue = getOperationTypeValue(tcmpVerificationReq);
            req.setOperationType(operationTypeValue);
            String simnum = tcmpVerificationReq.getSimInfo();
            if(StringUtils.isNotEmpty(simnum) && simnum.length() > 8)
                simnum = simnum.substring(8);
            req.setSimInfo(simnum);
            req.setUid(uid);
            MPVerificationResp resp = getMobileDynamicPasswordApi.getMobileDynamicPassword(req);
            com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.login.MPVerificationResp mpVerificationResp = new com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.login.MPVerificationResp();
            mpVerificationResp.setVerificationCode(resp.getVerificationCode());
            request = HelperUtils.enCode_MP_OTARequest(mpVerificationResp, request);
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(),request);
        }
        //手机验证码验证
        if(AuthServiceEnum.MOBILEVERIFICATIONAUTH.getAid().equals(aid)){
            MP_OTARequest request = (MP_OTARequest) context.getRequestObject();
            byte[] appBytes = request.getApplicationData();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(appBytes);
            OTADecoder decoder = new OTADecoder(inputStream);
            MPVerificationCheckReq tcmpVerificationCheckReq = (MPVerificationCheckReq)decoder.decode(MPVerificationCheckReq.class);
            IMobileVerificationAuthApi mobileVerificationAuthApi = (IMobileVerificationAuthApi) SpringContext.getInstance().getBean("mobileVerificationAuthApi");
            com.zxq.iov.cloud.sp.mds.tcmp.api.dto.MPVerificationCheckReq req  = new com.zxq.iov.cloud.sp.mds.tcmp.api.dto.MPVerificationCheckReq();
            req.setVerificationCode(tcmpVerificationCheckReq.getVerificationCode());
            String simnum = tcmpVerificationCheckReq.getSimInfo();
            if(StringUtils.isNotEmpty(simnum) && simnum.length() > 8)
                simnum = simnum.substring(8);
            req.setSimInfo(simnum);
            int operationTypeValue = getOperationTypeValue(tcmpVerificationCheckReq);
            req.setOperationType(operationTypeValue);
            MPVerificationCheckResp resp = mobileVerificationAuthApi.mobileVerificationAuth(req);
            com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.login.MPVerificationCheckResp mpVerificationCheckResp = new com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.login.MPVerificationCheckResp();
            mpVerificationCheckResp.setVerificationFlag(resp.isVerificationFlag());
            request = HelperUtils.enCode_MP_OTARequest(mpVerificationCheckResp, request);
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(),request);
        }

        return requestBack;
    }

    /**
     *
     * @param tcmpVerificationReq
     * @return
     */
    private int getOperationTypeValue(TCMP_VerificationReq tcmpVerificationReq) {
        Map<String,Integer> map = HelperUtils.getOperationTypeMap();
        return map.get(tcmpVerificationReq.getOperationType().getValue().name());
    }

    /**
     *
     * @param tcmpVerificationCheckReq
     * @return
     */
    private int getOperationTypeValue(MPVerificationCheckReq tcmpVerificationCheckReq) {
        Map<String,Integer> map = HelperUtils.getOperationTypeMap();
        return map.get(tcmpVerificationCheckReq.getOperationType().getValue().name());
    }
}
