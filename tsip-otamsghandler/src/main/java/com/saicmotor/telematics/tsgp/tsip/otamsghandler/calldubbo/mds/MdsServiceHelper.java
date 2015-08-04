package com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.mds;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.framework.core.exception.ApiException;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTAEncoder;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.dispatcher.AVN_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.dispatcher.MP_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.myCar.*;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.ServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common.HelperUtils;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common.MdsServiceEnum;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.zxq.iov.cloud.sp.mds.api.*;
import com.zxq.iov.cloud.sp.mds.api.dto.*;
import com.zxq.iov.cloud.sp.mds.api.dto.AddOrgVehicleReq;
import com.zxq.iov.cloud.sp.mds.api.dto.AddPersonalVehicleMPAuthReq;
import com.zxq.iov.cloud.sp.mds.api.dto.AddPersonalVehicleReq;
import com.zxq.iov.cloud.sp.mds.api.dto.AddPersonalVehicleResp;
import com.zxq.iov.cloud.sp.mds.api.dto.VehicleBrandModelResp;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2015/7/28.
 */
@Service
public class MdsServiceHelper implements ServiceHelper{

    @Override
    public String callDubboService(RequestContext context) throws ApiException {
        String aid = context.getAid();
        String requestBack = null;
        //AVN
        if(aid.startsWith("2")){
            AVN_OTARequest request = (AVN_OTARequest) context.getRequestObject();
            byte[] appBytes = request.getApplicationData();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(appBytes);
            OTADecoder decoder = new OTADecoder(inputStream);

            //AVN激活
            if(MdsServiceEnum.AVNACTIVATE.toString().equals(context.getAid())){
                com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.activation.ActivationReq activationReq = (com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.activation.ActivationReq)decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.activation.ActivationReq.class);
                IAvnActivateService avnActivateService = (IAvnActivateService) SpringContext.getInstance().getBean("avnActivateService");
                ActivationReq req = new ActivationReq();
                req.setVin(request.getDispatcherBody().getVin());
                req.setTboxSN(activationReq.getTboxSN());
                req.setAvnSN(activationReq.getAvnSN());
                req.setActivationCode(activationReq.getActivationCode());
                ActivationResp resp = avnActivateService.activateAvn(req);
                com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.activation.ActivationResp activationResp = new com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.activation.ActivationResp();
                activationResp.setSeed(resp.getSeed());
                activationResp.setSimNo(resp.getSimNo());
                request = enCode_AVN_OTARequest(activationResp,request);
                //请求对象编码为字符串
                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), request);
            }
            //城市列表下载
            if(MdsServiceEnum.CITYLISTDOWNLOAD.toString().equals(context.getAid())){
                com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.city.CityListDownloadReq cityListDownloadReq = (com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.city.CityListDownloadReq)decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.city.CityListDownloadReq.class);
                IGetCityListService getCityListService = (IGetCityListService) SpringContext.getInstance().getBean("getCityListService");
                com.zxq.iov.cloud.sp.mds.api.dto.CityListDownloadReq req = new com.zxq.iov.cloud.sp.mds.api.dto.CityListDownloadReq();
                req.setUid(request.getDispatcherBody().getUid());
                req.setCityType(cityListDownloadReq.getCityType().getIntegerForm());
                CityListDownloadResp resp = getCityListService.getCityList(req);
                com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.city.CityListDownloadResp cityListDownloadResp = new com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.city.CityListDownloadResp();
                Collection cl = resp.getCityList();
                cityListDownloadResp.setCityList(cl);
                request = enCode_AVN_OTARequest(cityListDownloadResp,request);
                //请求对象编码为字符串
                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(),request);
            }
            //关注城市添加、删除
            if(MdsServiceEnum.ADDORREMOVECONCERNCITIES.toString().equals(context.getAid())){
                com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.city.CitySettingReq citySettingReq = (com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.city.CitySettingReq)decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.city.CitySettingReq.class);
                IAddOrRemoveConcernCitiesService addOrRemoveConcernCitiesService = (IAddOrRemoveConcernCitiesService) SpringContext.getInstance().getBean("addOrRemoveConcernCitiesService");
                CitySettingReq req = new CitySettingReq();
                Collection cl = citySettingReq.getCityCodes();
                req.setCityCodes((List<CityCode>) cl);
                req.setOperation(citySettingReq.getOperation().getIntegerForm());
                req.setUid(request.getDispatcherBody().getUid());
                CitySettingResp resp = addOrRemoveConcernCitiesService.addOrRemoveConcernCities(req);
                request = enCode_AVN_OTARequest(resp,request);
                //请求对象编码为字符串
                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(),request);
            }
        }
//        //B2C
//        if(aid.startsWith("4")){
//            B2C_OTARequest request = (B2C_OTARequest) context.getRequestObject();
//            byte[] appBytes = request.getApplicationData();
//            ByteArrayInputStream inputStream = new ByteArrayInputStream(appBytes);
//            OTADecoder decoder = new OTADecoder(inputStream);
//
//            //B2C获取AVN激活码
//            if(MdsServiceEnum.B2C_GETAVNACTIVATIONCODE.toString().equals(context.getAid())){
//                IGetAvnActivationCodeService getAvnActivationCodeService = (IGetAvnActivationCodeService) SpringContext.getInstance().getBean("getAvnActivationCodeService");
//                com.zxq.iov.cloud.sp.mds.api.dto.ActivationCodeReq req = new com.zxq.iov.cloud.sp.mds.api.dto.ActivationCodeReq();
//                req.setVin(request.getDispatcherBody().getVin());
//                //请求对象编码为字符串
//                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), getAvnActivationCodeService.getAvnActivationCode(req));
//            }
//            //AVN检测激活
//            if(MdsServiceEnum.AVNACTIVATESTATUSCHECK.toString().equals(context.getAid())){
//                IAVNActivateStatusCheckService aVNActivateStatusCheckService = (IAVNActivateStatusCheckService) SpringContext.getInstance().getBean("aVNActivateStatusCheckService");
//                com.zxq.iov.cloud.sp.mds.api.dto.AVNActivationCheckReq req = new com.zxq.iov.cloud.sp.mds.api.dto.AVNActivationCheckReq();
//                req.setVin(request.getDispatcherBody().getVin());
//                //请求对象编码为字符串
//                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), aVNActivateStatusCheckService.checkAVNActivateStatus(req));
//            }
//            // 提交SIM卡激活申请
//            if(MdsServiceEnum.ACTIVATESIMCARD.toString().equals(context.getAid())){
//                IActivateSimCardService activateSimCardService = (IActivateSimCardService) SpringContext.getInstance().getBean("activateSimCardService");
//                ActivationSIM4B2CReq req = new ActivationSIM4B2CReq();
//                req.setVin(request.getDispatcherBody().getVin());
//                //请求对象编码为字符串
//                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), activateSimCardService.activateSimCard(req));
//            }
//        }
        //MP
        if(aid.startsWith("5")){
            MP_OTARequest request = (MP_OTARequest) context.getRequestObject();
            byte[] appBytes = request.getApplicationData();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(appBytes);
            OTADecoder decoder = new OTADecoder(inputStream);

            //MP获取AVN激活码
            if(MdsServiceEnum.MP_GETAVNACTIVATIONCODE.toString().equals(context.getAid())){
                IGetAvnActivationCodeService getAvnActivationCodeService = (IGetAvnActivationCodeService) SpringContext.getInstance().getBean("getAvnActivationCodeService");
                com.zxq.iov.cloud.sp.mds.api.dto.ActivationCodeReq req = new com.zxq.iov.cloud.sp.mds.api.dto.ActivationCodeReq();
                req.setVin(request.getDispatcherBody().getVin());
                ActivationCodeResp resp = getAvnActivationCodeService.getAvnActivationCode(req);
                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.activation.ActivationCodeResp activationCodeResp = new com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.activation.ActivationCodeResp();
                activationCodeResp.setActivationCode(resp.getActivationCode());
                request = enCode_MP_OTARequest(activationCodeResp,request);
                //请求对象编码为字符串
                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), request);
            }
            //用户信息查询
            if(MdsServiceEnum.MPUSERINFOQUERY.toString().equals(context.getAid())){
                IMPUserInfoQueryService mPUserInfoQueryService = (IMPUserInfoQueryService) SpringContext.getInstance().getBean("mPUserInfoQueryService");
                com.zxq.iov.cloud.sp.mds.api.dto.MPUserInfoReq req = new com.zxq.iov.cloud.sp.mds.api.dto.MPUserInfoReq();
                req.setUid(request.getDispatcherBody().getUid());
                MPUserInfoResp resp = mPUserInfoQueryService.queryMPUserInfo(req);
                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.MPUserInfoResp mPUserInfoResp = new com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.MPUserInfoResp();
                mPUserInfoResp.setEmergencyMobile(resp.getEmergencyMobile());
                mPUserInfoResp.setMobilePhone(resp.getMobilePhone());
                try {
                    mPUserInfoResp.setNickName(resp.getNickName().getBytes("UTF-8"));
                    mPUserInfoResp.setEmergencyName(resp.getEmergencyName().getBytes("UTF-8"));
                    mPUserInfoResp.setAddress(resp.getAddress().getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                mPUserInfoResp.setUserPhoto(resp.getUserPhoto());

                request = enCode_MP_OTARequest(mPUserInfoResp, request);
                //请求对象编码为字符串
                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), request);
            }
            //用户信息更新
            if(MdsServiceEnum.MPUSERINFOUPDATE.toString().equals(context.getAid())){
                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.MPUserInfoUpdateReq mpUserInfoUpdateReq = (com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.MPUserInfoUpdateReq)decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.MPUserInfoUpdateReq.class);
                IMPUserInfoUpdateService mPUserInfoUpdateService = (IMPUserInfoUpdateService) SpringContext.getInstance().getBean("IMPUserInfoUpdateService");
                com.zxq.iov.cloud.sp.mds.api.dto.MPUserInfoUpdateReq req = new com.zxq.iov.cloud.sp.mds.api.dto.MPUserInfoUpdateReq();
                req.setUid(request.getDispatcherBody().getUid());
                req.setNickName(new String(mpUserInfoUpdateReq.getNickName()));
                MPUserInfoUpdateResp resp = mPUserInfoUpdateService.updateMPUserInfo(req);
                request = enCode_MP_OTARequest(resp,request);
                //请求对象编码为字符串
                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), request);
            }
            //车辆信息查询
            if(MdsServiceEnum.MPVEHICLEINFOQUERY.toString().equals(context.getAid())){
                IMPVehicleInfoQueryService mPVehicleInfoQueryService = (IMPVehicleInfoQueryService) SpringContext.getInstance().getBean("mPVehicleInfoQueryService");
                com.zxq.iov.cloud.sp.mds.api.dto.MPVehicleInfoReq req = new com.zxq.iov.cloud.sp.mds.api.dto.MPVehicleInfoReq();
                req.setVin(request.getDispatcherBody().getVin());
                MPVehicleInfoResp resp= mPVehicleInfoQueryService.queryMPVehicleInfo(req);
                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.MPVehicleInfoResp mpVehicleInfoResp = new com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.MPVehicleInfoResp();
                try {
                    mpVehicleInfoResp.setBrand(resp.getBrand().getBytes("UTF-8"));
                    mpVehicleInfoResp.setLicenceNumber(resp.getLicenceNumber().getBytes("UTF-8"));
                    mpVehicleInfoResp.setModelName(resp.getModelName().getBytes("UTF-8"));
                    mpVehicleInfoResp.setVehicleName(resp.getVehicleName().getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                mpVehicleInfoResp.setModelYear(resp.getModelYear());
                mpVehicleInfoResp.setVehiclePhoto(resp.getVehiclePhoto());
                request = enCode_MP_OTARequest(mpVehicleInfoResp,request);
                //请求对象编码为字符串
                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), request);
            }
            //车辆信息更新
            if(MdsServiceEnum.MPVEHICLEINFOUPDATE.toString().equals(context.getAid())){
                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.MPVehicleInfoUpdateReq mpVehicleInfoUpdateReq = (com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.MPVehicleInfoUpdateReq)decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.MPVehicleInfoUpdateReq.class);
                IMPVehicleInfoUpdateService mPVehicleInfoUpdateService = (IMPVehicleInfoUpdateService) SpringContext.getInstance().getBean("mPVehicleInfoUpdateService");
                com.zxq.iov.cloud.sp.mds.api.dto.MPVehicleInfoUpdateReq req = new com.zxq.iov.cloud.sp.mds.api.dto.MPVehicleInfoUpdateReq();
                req.setVin(request.getDispatcherBody().getVin());
                req.setNickName(new String(mpVehicleInfoUpdateReq.getNickName()));
                req.setLicenceNumber(new String(mpVehicleInfoUpdateReq.getLicenceNumber()));
                MPVehicleInfoUpdateResp resp = mPVehicleInfoUpdateService.updateMPVehicleInfo(req);
                request = enCode_MP_OTARequest(resp,request);
                //请求对象编码为字符串
                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), request);
            }
            //用户使用反馈
            if(MdsServiceEnum.MPUSERFEEDBACK.toString().equals(context.getAid())){
                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.feedback.MPUserFeedBackReq mpResourceUploadReq = (com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.feedback.MPUserFeedBackReq)decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.feedback.MPUserFeedBackReq.class);
                IMPUserFeedBackService mPUserFeedBackService = (IMPUserFeedBackService) SpringContext.getInstance().getBean("mPUserFeedBackService");
                com.zxq.iov.cloud.sp.mds.api.dto.MPUserFeedBackReq req = new com.zxq.iov.cloud.sp.mds.api.dto.MPUserFeedBackReq();
                req.setContact(mpResourceUploadReq.getContact());
                req.setContent(new String(mpResourceUploadReq.getContent()));
                req.setUserName(mpResourceUploadReq.getUsername());
                MPUserFeedBackResp resp = mPUserFeedBackService.mpUserFeedBack(req);
                request = enCode_MP_OTARequest(resp,request);
                //请求对象编码为字符串
                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), request);
            }
            //用户车控操作评分
            if(MdsServiceEnum.MPCONTROLGRADE.toString().equals(context.getAid())){
                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.feedback.MPControlGradeReq mPControlGradeReq = (com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.feedback.MPControlGradeReq)decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.feedback.MPControlGradeReq.class);
                IMPControlGradeService mPControlGradeService = (IMPControlGradeService) SpringContext.getInstance().getBean("mPControlGradeService");
                com.zxq.iov.cloud.sp.mds.api.dto.MPControlGradeReq req = new com.zxq.iov.cloud.sp.mds.api.dto.MPControlGradeReq();
                req.setGrade(mPControlGradeReq.getGrade());
                req.setUserName(mPControlGradeReq.getUsername());
                req.setVehicleControlType(mPControlGradeReq.getVehicleControlType());
                MPControlGradeResp resp = mPControlGradeService.mpControlGrade(req);
                request = enCode_MP_OTARequest(resp,request);
                //请求对象编码为字符串
                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), request);
            }
            //PIN码设置请求
            if(MdsServiceEnum.SETPINCODE.toString().equals(context.getAid())){
                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.SetPinCodeReq setPinCodeReq = (com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.SetPinCodeReq)decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.SetPinCodeReq.class);
                ISetPinCodeService setPinCodeService = (ISetPinCodeService) SpringContext.getInstance().getBean("setPinCodeService");
                com.zxq.iov.cloud.sp.mds.api.dto.SetPinCodeReq req = new com.zxq.iov.cloud.sp.mds.api.dto.SetPinCodeReq();
                req.setVin(request.getDispatcherBody().getVin());
                req.setIdcardNo(setPinCodeReq.getIdCardNo());
                req.setOperationType(setPinCodeReq.getOperationType().getIntegerForm());
                req.setPin(setPinCodeReq.getPin());
                req.setRealName(new String(setPinCodeReq.getRealName()));
                req.setSimInfo(setPinCodeReq.getSimInfo());
                req.setUserId(request.getDispatcherBody().getUid());
                req.setVerificationCode(setPinCodeReq.getVerificationCode());
                SetPinCodeResp resp = setPinCodeService.setPinCode(req);
                request = enCode_MP_OTARequest(resp,request);
                //请求对象编码为字符串
                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), request);
            }
            //获取品牌车型信息
            if(MdsServiceEnum.GETVEHICLEBRANDMODEL.toString().equals(context.getAid())){
                IGetVehicleBrandModelService getVehicleBrandModelService = (IGetVehicleBrandModelService) SpringContext.getInstance().getBean("getVehicleBrandModelService");
                com.zxq.iov.cloud.sp.mds.api.dto.VehicleBrandModelReq req = new com.zxq.iov.cloud.sp.mds.api.dto.VehicleBrandModelReq();
                VehicleBrandModelResp vehicleBrandModelResp = getVehicleBrandModelService.getVehicleBrandModel(req);
                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.VehicleBrandModelResp resp = new com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.VehicleBrandModelResp();
                Collection cl = vehicleBrandModelResp.getBrandList();
                resp.setBrandList(cl);
                request = enCode_MP_OTARequest(resp,request);
                //请求对象编码为字符串
                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), request);
            }
            //绑车/添加个人车辆
            if(MdsServiceEnum.ADDPERSONALVEHICLE.toString().equals(context.getAid())){
                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.AddPersonalVehicleReq addPersonalVehicleReq = (com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.AddPersonalVehicleReq) decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.AddPersonalVehicleReq.class);
                IAddPersonalVehicleService addPersonalVehicleService = (IAddPersonalVehicleService) SpringContext.getInstance().getBean("addPersonalVehicleService");
                com.zxq.iov.cloud.sp.mds.api.dto.AddPersonalVehicleReq req = new com.zxq.iov.cloud.sp.mds.api.dto.AddPersonalVehicleReq();
                req.setVin(request.getDispatcherBody().getVin());
                req.setUserId(request.getDispatcherBody().getUid());
                req.setBrandName(new String(addPersonalVehicleReq.getBrandName()));
                req.setIdCardNo(addPersonalVehicleReq.getIdCardNo());
                req.setModelId(addPersonalVehicleReq.getModelId());
                req.setUserName(new String(addPersonalVehicleReq.getUserName()));
                AddPersonalVehicleResp resp = addPersonalVehicleService.addPersonalVehicle(req);
                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.AddPersonalVehicleResp addPersonalVehicleResp = new com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.AddPersonalVehicleResp();
                addPersonalVehicleResp.setAddFlag(resp.isAddFlag());
                addPersonalVehicleResp.setSimInfo(resp.getSimInfo());
                addPersonalVehicleResp.setIsFirstAdd(resp.isFirstAdd());
                Collection cl = resp.getVinList();
                addPersonalVehicleResp.setVinList(cl);
                request = enCode_MP_OTARequest(addPersonalVehicleResp,request);
                //请求对象编码为字符串
                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(),request);
            }
            // 绑车/添加集团车辆(需确认)
            if(MdsServiceEnum.ADDORGVEHICLE.toString().equals(context.getAid())){
                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.AddOrgVehicleReq addOrgVehicleReq = (com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.AddOrgVehicleReq) decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.AddOrgVehicleReq.class);
                IAddOrgVehicleService addOrgVehicleService = (IAddOrgVehicleService) SpringContext.getInstance().getBean("addOrgVehicleService");
                com.zxq.iov.cloud.sp.mds.api.dto.AddOrgVehicleReq req = new com.zxq.iov.cloud.sp.mds.api.dto.AddOrgVehicleReq();
                req.setUserName(new String(addOrgVehicleReq.getUserName()));
                req.setVin(request.getDispatcherBody().getVin());
                req.setModelId(addOrgVehicleReq.getModelId());
                req.setIdCardNo(addOrgVehicleReq.getIdCardNo());
                req.setBrandName(new String(addOrgVehicleReq.getBrandName()));
                req.setOperationType(addOrgVehicleReq.getOperationType().getIntegerForm());
                req.setOrgName(new String(addOrgVehicleReq.getOrgName()));
                req.setOrgNo(addOrgVehicleReq.getOrgNo());
                req.setSafeSimInfo(addOrgVehicleReq.getSafeSimInfo());
                req.setTempSimInfo(addOrgVehicleReq.getTempSimInfo());
                req.setVerificationCode(addOrgVehicleReq.getVerificationCode());
                req.setUserId(request.getDispatcherBody().getUid());
                AddOrgVehicleResp resp = addOrgVehicleService.addOrgVehicle(req);
                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.AddOrgVehicleResp addOrgVehicleResp = new com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.AddOrgVehicleResp();
                Collection cl = resp.getVinList();
                addOrgVehicleResp.setVinList(cl);
                request = enCode_MP_OTARequest(addOrgVehicleResp,request);
                //请求对象编码为字符串
                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), request);
            }
            //添加个人车辆手机验证码验证
//            if(MdsServiceEnum.ADDPERSONALVEHICLEMPAUTH.toString().equals(context.getAid())){
//                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.AddPersonalVehicleMPAuthReq req = (com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.AddPersonalVehicleMPAuthReq)decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.AddPersonalVehicleMPAuthReq.class);
//                ISetUserMobilePhoneService setUserMobilePhoneService = (ISetUserMobilePhoneService) SpringContext.getInstance().getBean("ISetUserMobilePhoneService");
//            }
            //      用户主手机号码设置
            if(MdsServiceEnum.SETUSERMOBILEPHONE.toString().equals(context.getAid())){
                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.SetUserMobilePhoneReq setUserMobilePhoneReq = (com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.SetUserMobilePhoneReq)decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.vehicle.SetUserMobilePhoneReq.class);
                ISetUserMobilePhoneService setUserMobilePhoneService = (ISetUserMobilePhoneService) SpringContext.getInstance().getBean("ISetUserMobilePhoneService");
                com.zxq.iov.cloud.sp.mds.api.dto.SetUserMobilePhoneReq req = new com.zxq.iov.cloud.sp.mds.api.dto.SetUserMobilePhoneReq();
                req.setVerificationCode(setUserMobilePhoneReq.getVerificationCode());
                req.setUid(request.getDispatcherBody().getUid());
                req.setOperationType(setUserMobilePhoneReq.getOperationType().getIntegerForm());
                req.setSimInfo(setUserMobilePhoneReq.getSimInfo());
                SetUserMobilePhoneResp setUserMobilePhoneResp = setUserMobilePhoneService.setUserMobilePhone(req);
                request = enCode_MP_OTARequest(setUserMobilePhoneResp,request);
                //请求对象编码为字符串
                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), request);
            }
            //手机用户快速注册
            if(MdsServiceEnum.MPFASTREGISTER.toString().equals(context.getAid())){
                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_0.MPFastRegisterReq mpFastRegisterReq = (com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_0.MPFastRegisterReq)decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_0.MPFastRegisterReq.class);
                IMPFastRegisterService mPFastRegisterService = (IMPFastRegisterService) SpringContext.getInstance().getBean("mPFastRegisterService");
                com.zxq.iov.cloud.sp.mds.api.dto.MPFastRegisterReq req = new com.zxq.iov.cloud.sp.mds.api.dto.MPFastRegisterReq();
                req.setSimInfo(mpFastRegisterReq.getSimInfo());
                req.setVerificationCode(mpFastRegisterReq.getVerificationCode());
                req.setPassword(mpFastRegisterReq.getPassword());
                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_0.MPFastRegisterResp mpFastRegisterResp = new com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_0.MPFastRegisterResp();
                MPFastRegisterResp resp = mPFastRegisterService.MPFastRegisterService(req);
                mpFastRegisterResp.setUsername(resp.getUsername());
                request = enCode_MP_OTARequest(mpFastRegisterResp,request);
                //请求对象编码为字符串
                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(),request);
            }
            // 手机用户更新密码
            if(MdsServiceEnum.UPDATEPASSWORD.toString().equals(context.getAid())){
                com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_0.UpdatePasswordReq updatePasswordReq = (com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_0.UpdatePasswordReq)decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_0.UpdatePasswordReq.class);
                IUpdatePasswordService updatePasswordService = (IUpdatePasswordService) SpringContext.getInstance().getBean("updatePasswordService");
                com.zxq.iov.cloud.sp.mds.api.dto.UpdatePasswordReq req = new com.zxq.iov.cloud.sp.mds.api.dto.UpdatePasswordReq();
                req.setPassword(updatePasswordReq.getPassword());
                req.setSimInfo(updatePasswordReq.getSimInfo());
                req.setVerificationCode(updatePasswordReq.getVerificationCode());
                UpdatePasswordResp resp = updatePasswordService.updatePassword(req);
                request = enCode_MP_OTARequest(resp,request);

                //请求对象编码为字符串
                requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(),request);
            }
        }
        return requestBack;
    }

    /**
     *
     * @param o
     * @param request
     * @return
     */
    private MP_OTARequest enCode_MP_OTARequest(Object o,MP_OTARequest request){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OTAEncoder encoder = new OTAEncoder(outputStream);
        encoder.encode(o);
        byte[] resultBytes = outputStream.toByteArray();
        request.setApplicationData(resultBytes);
        request.getDispatcherBody().setApplicationDataLength(Long.valueOf(resultBytes.length));
        request.getDispatcherBody().setMessageID(2);
        //return success status
        request.getDispatcherBody().setResult(0);
        return request;
    }

    /**
     *
     * @param o
     * @param request
     * @return
     */
    private AVN_OTARequest enCode_AVN_OTARequest(Object o,AVN_OTARequest request){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OTAEncoder encoder = new OTAEncoder(outputStream);
        encoder.encode(o);
        byte[] resultBytes = outputStream.toByteArray();
        request.setApplicationData(resultBytes);
        request.getDispatcherBody().setApplicationDataLength(Long.valueOf(resultBytes.length));
        request.getDispatcherBody().setMessageID(2);
        //return success status
        request.getDispatcherBody().setResult(0);
        return request;
    }
}
