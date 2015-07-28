package com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.mds;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.framework.core.exception.ApiException;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.dispatcher.AVN_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.MPUserInfoUpdateReq;
import com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.MPVehicleInfoUpdateReq;
import com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_0.MPFastRegisterReq;
import com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.login.v2_0.UpdatePasswordReq;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.city.CityListDownloadReq;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.feedback.MPControlGradeReq;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.feedback.MPUserFeedBackReq;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.login.SetUserMobilePhoneReq;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.myCar.ASVehicleCheckedReq;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.myCar.AddOrgVehicleReq;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.myCar.AddPersonalVehicleReq;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.simcard.UpdateSIMActiveStatusReq;
import com.saicmotor.telematics.tsgp.otaadapter.wxg4as.entity.myCar.GetVehicleDetailFromTCMPReq;
import com.saicmotor.telematics.tsgp.otaadapter.wxg4as.entity.myCar.UpdateVehicleFromTCMPReq;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.ServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common.HelperUtils;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common.MdsServiceEnum;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.zxq.iov.cloud.sp.mds.api.*;
import com.zxq.iov.cloud.sp.mds.api.dto.ActivationReq;
import com.zxq.iov.cloud.sp.mds.api.dto.ActivationSIM4B2CReq;
import com.zxq.iov.cloud.sp.mds.api.dto.CitySettingReq;
import com.zxq.iov.cloud.sp.mds.api.dto.SetPinCodeReq;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

/**
 * Created by Administrator on 2015/7/28.
 */
@Service
public class MdsServiceHelper implements ServiceHelper{

    @Override
    public String callDubboService(RequestContext context) throws ApiException {
        String requestBack = null;
        AVN_OTARequest request = (AVN_OTARequest) context.getRequestObject();
        byte[] appBytes = request.getApplicationData();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(appBytes);
        OTADecoder decoder = new OTADecoder(inputStream);

        //获取AVN激活码
        if(MdsServiceEnum.GETAVNACTIVATIONCODE.toString().equals(context.getAid())){
            IGetAvnActivationCodeService getAvnActivationCodeService = (IGetAvnActivationCodeService) SpringContext.getInstance().getBean("getAvnActivationCodeService");
            com.zxq.iov.cloud.sp.mds.api.dto.ActivationCodeReq req = new com.zxq.iov.cloud.sp.mds.api.dto.ActivationCodeReq();
            req.setVin(request.getDispatcherBody().getVin());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), getAvnActivationCodeService.getAvnActivationCode(req));
        }
        //AVN激活
        if(MdsServiceEnum.AVNACTIVATE.toString().equals(context.getAid())){
            com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.activation.ActivationReq activationReq = (com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.activation.ActivationReq)decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.activation.ActivationReq.class);
            IAvnActivateService avnActivateService = (IAvnActivateService) SpringContext.getInstance().getBean("avnActivateService");
            ActivationReq req = new ActivationReq();
            req.setVin(request.getDispatcherBody().getVin());
            req.setTboxSN(activationReq.getTboxSN());
            req.setAvnSN(activationReq.getAvnSN());
            req.setActivationCode(activationReq.getActivationCode());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), avnActivateService.activateAvn(req));
        }
        //AVN检测激活
        if(MdsServiceEnum.AVNACTIVATESTATUSCHECK.toString().equals(context.getAid())){
            IAVNActivateStatusCheckService aVNActivateStatusCheckService = (IAVNActivateStatusCheckService) SpringContext.getInstance().getBean("aVNActivateStatusCheckService");
            com.zxq.iov.cloud.sp.mds.api.dto.AVNActivationCheckReq req = new com.zxq.iov.cloud.sp.mds.api.dto.AVNActivationCheckReq();
            req.setVin(request.getDispatcherBody().getVin());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), aVNActivateStatusCheckService.checkAVNActivateStatus(req));
        }
        //车辆MES信息获取
        if(MdsServiceEnum.VEHICLEMESPART4OPEN.toString().equals(context.getAid())){
            IVehicleMesPart4OpenService vehicleMesPart4OpenService = (IVehicleMesPart4OpenService) SpringContext.getInstance().getBean("vehicleMesPart4OpenService");
            com.zxq.iov.cloud.sp.mds.api.dto.VehicleMesPart4OpenReq req = new com.zxq.iov.cloud.sp.mds.api.dto.VehicleMesPart4OpenReq();
            req.setVin(request.getDispatcherBody().getVin());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), vehicleMesPart4OpenService.getVehicleMes(req));
        }
        //城市列表下载
        if(MdsServiceEnum.CITYLISTDOWNLOAD.toString().equals(context.getAid())){
            CityListDownloadReq cityListDownloadReq = (CityListDownloadReq)decoder.decode(CityListDownloadReq.class);
            IGetCityListService getCityListService = (IGetCityListService) SpringContext.getInstance().getBean("getCityListService");
            com.zxq.iov.cloud.sp.mds.api.dto.CityListDownloadReq req = new com.zxq.iov.cloud.sp.mds.api.dto.CityListDownloadReq();
            req.setUid(request.getDispatcherBody().getUid());
            req.setCityType(cityListDownloadReq.getCityType().getIntegerForm());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), getCityListService.getCityList(req));
        }
        //关注城市添加、删除
        if(MdsServiceEnum.ADDORREMOVECONCERNCITIES.toString().equals(context.getAid())){
            com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.city.CitySettingReq citySettingReq = (com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.city.CitySettingReq)decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.city.CitySettingReq.class);
            IAddOrRemoveConcernCitiesService addOrRemoveConcernCitiesService = (IAddOrRemoveConcernCitiesService) SpringContext.getInstance().getBean("addOrRemoveConcernCitiesService");
            CitySettingReq req = new CitySettingReq();
//            req.setCityCodes(new ArrayList<CityCode>());
            req.setOperation(citySettingReq.getOperation().getIntegerForm());
            req.setUid(request.getDispatcherBody().getUid());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), addOrRemoveConcernCitiesService.addOrRemoveConcernCities(req));
        }
        //用户信息查询
        if(MdsServiceEnum.MPUSERINFOQUERY.toString().equals(context.getAid())){
            IMPUserInfoQueryService mPUserInfoQueryService = (IMPUserInfoQueryService) SpringContext.getInstance().getBean("mPUserInfoQueryService");
            com.zxq.iov.cloud.sp.mds.api.dto.MPUserInfoReq req = new com.zxq.iov.cloud.sp.mds.api.dto.MPUserInfoReq();
            req.setUid(request.getDispatcherBody().getUid());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), mPUserInfoQueryService.queryMPUserInfo(req));
        }
        //用户信息更新
        if(MdsServiceEnum.MPUSERINFOUPDATE.toString().equals(context.getAid())){
            MPUserInfoUpdateReq mpUserInfoUpdateReq = (MPUserInfoUpdateReq)decoder.decode(MPUserInfoUpdateReq.class);
            IMPUserInfoUpdateService mPUserInfoUpdateService = (IMPUserInfoUpdateService) SpringContext.getInstance().getBean("IMPUserInfoUpdateService");
            com.zxq.iov.cloud.sp.mds.api.dto.MPUserInfoUpdateReq req = new com.zxq.iov.cloud.sp.mds.api.dto.MPUserInfoUpdateReq();
            req.setUid(request.getDispatcherBody().getUid());
            req.setNickName(new String(mpUserInfoUpdateReq.getNickName()));
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), mPUserInfoUpdateService.updateMPUserInfo(req));
        }
        //车辆信息查询
        if(MdsServiceEnum.MPVEHICLEINFOQUERY.toString().equals(context.getAid())){
            IMPVehicleInfoQueryService mPVehicleInfoQueryService = (IMPVehicleInfoQueryService) SpringContext.getInstance().getBean("mPVehicleInfoQueryService");
            com.zxq.iov.cloud.sp.mds.api.dto.MPVehicleInfoReq req = new com.zxq.iov.cloud.sp.mds.api.dto.MPVehicleInfoReq();
            req.setVin(request.getDispatcherBody().getVin());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), mPVehicleInfoQueryService.queryMPVehicleInfo(req));
        }
        //车辆信息更新
        if(MdsServiceEnum.MPVEHICLEINFOUPDATE.toString().equals(context.getAid())){
            MPVehicleInfoUpdateReq mpVehicleInfoUpdateReq = (MPVehicleInfoUpdateReq)decoder.decode(MPVehicleInfoUpdateReq.class);
            IMPVehicleInfoUpdateService mPVehicleInfoUpdateService = (IMPVehicleInfoUpdateService) SpringContext.getInstance().getBean("mPVehicleInfoUpdateService");
            com.zxq.iov.cloud.sp.mds.api.dto.MPVehicleInfoUpdateReq req = new com.zxq.iov.cloud.sp.mds.api.dto.MPVehicleInfoUpdateReq();
            req.setVin(request.getDispatcherBody().getVin());
            req.setNickName(new String(mpVehicleInfoUpdateReq.getNickName()));
            req.setLicenceNumber(new String(mpVehicleInfoUpdateReq.getLicenceNumber()));
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), mPVehicleInfoUpdateService.updateMPVehicleInfo(req));
        }
        //用户使用反馈
        if(MdsServiceEnum.MPUSERFEEDBACK.toString().equals(context.getAid())){
            MPUserFeedBackReq mpResourceUploadReq = (MPUserFeedBackReq)decoder.decode(MPUserFeedBackReq.class);
            IMPUserFeedBackService iMPUserFeedBackService = (IMPUserFeedBackService) SpringContext.getInstance().getBean("iMPUserFeedBackService");
            com.zxq.iov.cloud.sp.mds.api.dto.MPUserFeedBackReq req = new com.zxq.iov.cloud.sp.mds.api.dto.MPUserFeedBackReq();
            req.setContact(mpResourceUploadReq.getContact());
            req.setContent(new String(mpResourceUploadReq.getContent()));
            req.setUserName(mpResourceUploadReq.getUsername());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), iMPUserFeedBackService.mpUserFeedBack(req));
        }
        //用户车控操作评分
        if(MdsServiceEnum.MPCONTROLGRADE.toString().equals(context.getAid())){
            MPControlGradeReq mPControlGradeReq = (MPControlGradeReq)decoder.decode(MPControlGradeReq.class);
            IMPControlGradeService mPControlGradeService = (IMPControlGradeService) SpringContext.getInstance().getBean("mPControlGradeService");
            com.zxq.iov.cloud.sp.mds.api.dto.MPControlGradeReq req = new com.zxq.iov.cloud.sp.mds.api.dto.MPControlGradeReq();
            req.setGrade(mPControlGradeReq.getGrade());
            req.setUserName(mPControlGradeReq.getUsername());
            req.setVehicleControlType(mPControlGradeReq.getVehicleControlType());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), mPControlGradeService.mpControlGrade(req));
        }
        //我的爱车/车辆官方验证
        if(MdsServiceEnum.ASVEHICLECHECKED.toString().equals(context.getAid())){
            ASVehicleCheckedReq asVehicleCheckedReq = (ASVehicleCheckedReq)decoder.decode(ASVehicleCheckedReq.class);
            IASVehicleCheckedService aSVehicleCheckedService = (IASVehicleCheckedService) SpringContext.getInstance().getBean("aSVehicleCheckedService");
            com.zxq.iov.cloud.sp.mds.api.dto.ASVehicleCheckedReq req = new com.zxq.iov.cloud.sp.mds.api.dto.ASVehicleCheckedReq();
            req.setEngineNo(asVehicleCheckedReq.getEngineNo());
            req.setVinNo(asVehicleCheckedReq.getVinNo());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), aSVehicleCheckedService.checkASVehicle(req));
        }
        // 我的爱车/用户InkaNet系统车辆列表获取
        if(MdsServiceEnum.ASGETVEHICLELIST.toString().equals(context.getAid())){
            IASGetVehicleListService aSGetVehicleListService = (IASGetVehicleListService) SpringContext.getInstance().getBean("aSGetVehicleListService");
            com.zxq.iov.cloud.sp.mds.api.dto.ASGetVehicleListReq req = new com.zxq.iov.cloud.sp.mds.api.dto.ASGetVehicleListReq();
            req.setUid(request.getDispatcherBody().getUid());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), aSGetVehicleListService.getASVehicleList(req));
        }
        //我的爱车/车辆InkaNet系统信息查询
        if(MdsServiceEnum.ASGETVEHICLEDETAIL.toString().equals(context.getAid())){
            GetVehicleDetailFromTCMPReq getVehicleFromTCMPReq = (GetVehicleDetailFromTCMPReq)decoder.decode(com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.myCar.GetVehicleDetailFromTCMPReq.class);
            IASGetVehicleDetailService aSGetVehicleDetailService = (IASGetVehicleDetailService) SpringContext.getInstance().getBean("aSGetVehicleDetailService");
            com.zxq.iov.cloud.sp.mds.api.dto.GetVehicleDetailFromTCMPReq req = new com.zxq.iov.cloud.sp.mds.api.dto.GetVehicleDetailFromTCMPReq();
            req.setUid(request.getDispatcherBody().getUid());
            req.setVin(request.getDispatcherBody().getVin());
            req.setSource(getVehicleFromTCMPReq.getSource());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), aSGetVehicleDetailService.getASVehicleDetail(req));
        }
        //我的爱车/车辆信息修改
        if(MdsServiceEnum.UPDATEVEHICLEFROMTCMP.toString().equals(context.getAid())){
            UpdateVehicleFromTCMPReq updateVehicleFromTCMPReq = (UpdateVehicleFromTCMPReq)decoder.decode(UpdateVehicleFromTCMPReq.class);
            IUpdateVehicleFromTCMPService updateVehicleFromTCMPService = (IUpdateVehicleFromTCMPService) SpringContext.getInstance().getBean("updateVehicleFromTCMPService");
            com.zxq.iov.cloud.sp.mds.api.dto.UpdateVehicleFromTCMPReq req = new com.zxq.iov.cloud.sp.mds.api.dto.UpdateVehicleFromTCMPReq();
            req.setUid(request.getDispatcherBody().getUid());
            req.setVin(request.getDispatcherBody().getVin());
            req.setVehicleNo(new String(updateVehicleFromTCMPReq.getVehicleNo()));
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), updateVehicleFromTCMPService.updateVehicleFromTCMP(req));
        }
        // 提交SIM卡激活申请
        if(MdsServiceEnum.ACTIVATESIMCARD.toString().equals(context.getAid())){
            IActivateSimCardService activateSimCardService = (IActivateSimCardService) SpringContext.getInstance().getBean("activateSimCardService");
            ActivationSIM4B2CReq req = new ActivationSIM4B2CReq();
            req.setVin(request.getDispatcherBody().getVin());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), activateSimCardService.activateSimCard(req));
        }
        //SIM卡激活状态更新
        if(MdsServiceEnum.UPDATESIMACTIVESTATUS.toString().equals(context.getAid())){
            UpdateSIMActiveStatusReq updateSIMActiveStatusReq = (UpdateSIMActiveStatusReq)decoder.decode(UpdateSIMActiveStatusReq.class);
            IUpdateSimActiveStatusService updateSimActiveStatusService = (IUpdateSimActiveStatusService) SpringContext.getInstance().getBean("updateSimActiveStatusService");
            com.zxq.iov.cloud.sp.mds.api.dto.UpdateSIMActiveStatusReq req = new com.zxq.iov.cloud.sp.mds.api.dto.UpdateSIMActiveStatusReq();
            req.setActiveStatus(updateSIMActiveStatusReq.getActiveStatus());
            req.setCode(updateSIMActiveStatusReq.getCode());
            req.setIccId(updateSIMActiveStatusReq.getIccId());
            req.setMessage(new String(updateSIMActiveStatusReq.getMessage()));
            req.setSimNo(updateSIMActiveStatusReq.getSimNo());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), updateSimActiveStatusService.updateSimActiveStatus(req));
        }
        //PIN码设置请求
        if(MdsServiceEnum.SETPINCODE.toString().equals(context.getAid())){
            SetPinCodeReq setPinCodeReq = (SetPinCodeReq)decoder.decode(SetPinCodeReq.class);
            ISetPinCodeService setPinCodeService = (ISetPinCodeService) SpringContext.getInstance().getBean("setPinCodeService");
            com.zxq.iov.cloud.sp.mds.api.dto.SetPinCodeReq req = new com.zxq.iov.cloud.sp.mds.api.dto.SetPinCodeReq();
            req.setVin(setPinCodeReq.getVin());
            req.setIdcardNo(setPinCodeReq.getIdcardNo());
            req.setOperationType(setPinCodeReq.getOperationType());
            req.setPin(setPinCodeReq.getPin());
            req.setRealName(setPinCodeReq.getRealName());
            req.setSimInfo(setPinCodeReq.getSimInfo());
            req.setUserId(setPinCodeReq.getUserId());
            req.setVerificationCode(setPinCodeReq.getVerificationCode());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), setPinCodeService.setPinCode(req));
        }
        //获取品牌车型信息
        if(MdsServiceEnum.GETVEHICLEBRANDMODEL.toString().equals(context.getAid())){
            IGetVehicleBrandModelService getVehicleBrandModelService = (IGetVehicleBrandModelService) SpringContext.getInstance().getBean("getVehicleBrandModelService");
            com.zxq.iov.cloud.sp.mds.api.dto.VehicleBrandModelReq req = new com.zxq.iov.cloud.sp.mds.api.dto.VehicleBrandModelReq();
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), getVehicleBrandModelService.getVehicleBrandModel(req));
        }
        //绑车/添加个人车辆
        if(MdsServiceEnum.ADDPERSONALVEHICLE.toString().equals(context.getAid())){
            AddPersonalVehicleReq addPersonalVehicleReq = (AddPersonalVehicleReq) decoder.decode(AddPersonalVehicleReq.class);
            IAddPersonalVehicleService addPersonalVehicleService = (IAddPersonalVehicleService) SpringContext.getInstance().getBean("addPersonalVehicleService");
            com.zxq.iov.cloud.sp.mds.api.dto.AddPersonalVehicleReq req = new com.zxq.iov.cloud.sp.mds.api.dto.AddPersonalVehicleReq();
            req.setVin(request.getDispatcherBody().getVin());
            req.setUserId(request.getDispatcherBody().getUid());
            req.setBrandName(new String(addPersonalVehicleReq.getBrandName()));
            req.setIdCardNo(addPersonalVehicleReq.getIdCardNo());
            req.setModelId(addPersonalVehicleReq.getModelId());
            req.setUserName(new String(addPersonalVehicleReq.getUserName()));
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), addPersonalVehicleService.addPersonalVehicle(req));
        }
        // 绑车/添加集团车辆(需确认)
        if(MdsServiceEnum.ADDORGVEHICLE.toString().equals(context.getAid())){
            AddOrgVehicleReq addOrgVehicleReq = (AddOrgVehicleReq) decoder.decode(AddOrgVehicleReq.class);
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
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), addOrgVehicleService.addOrgVehicle(req));
        }
        //      添加个人车辆手机验证码验证（）
        if(MdsServiceEnum.SETUSERMOBILEPHONE.toString().equals(context.getAid())){
            SetUserMobilePhoneReq setUserMobilePhoneReq = (SetUserMobilePhoneReq)decoder.decode(SetUserMobilePhoneReq.class);
            ISetUserMobilePhoneService setUserMobilePhoneService = (ISetUserMobilePhoneService) SpringContext.getInstance().getBean("ISetUserMobilePhoneService");
            com.zxq.iov.cloud.sp.mds.api.dto.SetUserMobilePhoneReq req = new com.zxq.iov.cloud.sp.mds.api.dto.SetUserMobilePhoneReq();
            req.setVerificationCode(setUserMobilePhoneReq.getVerificationCode());
            req.setUid(request.getDispatcherBody().getUid());
            req.setOperationType(setUserMobilePhoneReq.getOperationType().getIntegerForm());
            req.setSimInfo(setUserMobilePhoneReq.getSimInfo());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), setUserMobilePhoneService.setUserMobilePhone(req));
        }
        //手机用户快速注册
        if(MdsServiceEnum.MPFASTREGISTER.toString().equals(context.getAid())){
            MPFastRegisterReq mpFastRegisterReq = (MPFastRegisterReq)decoder.decode(MPFastRegisterReq.class);
            IMPFastRegisterService mPFastRegisterService = (IMPFastRegisterService) SpringContext.getInstance().getBean("mPFastRegisterService");
            com.zxq.iov.cloud.sp.mds.api.dto.MPFastRegisterReq req = new com.zxq.iov.cloud.sp.mds.api.dto.MPFastRegisterReq();
            req.setSimInfo(mpFastRegisterReq.getSimInfo());
            req.setVerificationCode(mpFastRegisterReq.getVerificationCode());
            req.setPassword(mpFastRegisterReq.getPassword());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), mPFastRegisterService.MPFastRegisterService(req));
        }
        // 手机用户更新密码
        if(MdsServiceEnum.UPDATEPASSWORD.toString().equals(context.getAid())){
            UpdatePasswordReq updatePasswordReq = (UpdatePasswordReq)decoder.decode(UpdatePasswordReq.class);
            IUpdatePasswordService updatePasswordService = (IUpdatePasswordService) SpringContext.getInstance().getBean("updatePasswordService");
            com.zxq.iov.cloud.sp.mds.api.dto.UpdatePasswordReq req = new com.zxq.iov.cloud.sp.mds.api.dto.UpdatePasswordReq();
            req.setPassword(updatePasswordReq.getPassword());
            req.setSimInfo(updatePasswordReq.getSimInfo());
            req.setVerificationCode(updatePasswordReq.getVerificationCode());
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), updatePasswordService.updatePassword(req));
        }
        return requestBack;
    }
}
