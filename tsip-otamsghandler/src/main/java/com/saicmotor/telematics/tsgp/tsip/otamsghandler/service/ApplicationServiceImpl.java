/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.otamsghandler.service;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.ServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common.DubboHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.MessageTemplate;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.AdapterException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ErrorMessageHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ExceptionHandler;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ProtocolException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;


/**
 * OTA请求控制类，负责响应客户端发出的请求
 * @author jozbt
 * @author cqzzl
 */
@Service
public class ApplicationServiceImpl implements IApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    public String execute(String from, String source, String platform) {
        LOGGER.debug("enter ApplicationService.execute()...");
        long start = System.currentTimeMillis();
        String requestBack = null;
        RequestContext context = null;
        try {
            context = RequestContext.initContext(platform, source);
            String aid = context.getAid();

            MessageTemplate messageTemplate = null;
            //针对message,vp,navi处理
            if(DubboHelper.isMessage(aid) || DubboHelper.isVp(aid) || DubboHelper.isNavi(aid)){
                messageTemplate = MessageHelper.getMessage(context.getAid(), context.getMid());
                if (messageTemplate == null) {
                    throw new ProtocolException("没有配置callback. AID:" + context.getAid() + ",MID:" + context.getMid());
                }
            }


            if(!"113".equals(context.getAid())){
            //记录请求日志
                //LogHelper.reportDataInfo(context.getAid(), context.getMid(), context.getVin(), source);
                LogHelper.info(LOGGER, context.getAid(), context.getMid(), context.getVin(), context.getUid(), source, from, context.getToken());
            }

            int authResultCode = 0;
            Integer testFlag = (Integer) (AdapterHelper.getProperty(context.getRequestObject(), "dispatcherBody.testFlag") == null ? 0 : AdapterHelper.getProperty(context.getRequestObject(), "dispatcherBody.testFlag"));
            if (testFlag != 1) {//如果非测试请求，则调用验证服务进行权限验证
                //TODO call dubbo to do 权限验证
                AuthenticationService authenticationService = SpringContext.getInstance().getBean(AuthenticationService.class);
                authResultCode = authenticationService.check(context);
            }
            //验证失败
            if (authResultCode != 0) {
                Object request = context.getRequestObject();
                AdapterHelper.setProperty(request, "applicationData", new byte[0]);
                AdapterHelper.setProperty(request, "dispatcherBody.result", authResultCode);
                //设置errorMessage
                String errorMessage = ErrorMessageHelper.getErrorMessage(authResultCode);
                if (errorMessage != null && !Cfg.PLATFORM_TBOX.equals(RequestContext.getContext().getPlatform())) {
                    PropertyUtils.setNestedProperty(request, "dispatcherBody.errorMessage", errorMessage.getBytes("utf-8"));
                }
                requestBack = new String(AdapterHelper.adapterGetBytesData(platform, context.getClientVersion(), context.getRequestObject()));
            }
            //验证通过
            else {
                //调用服务时将token设置为新token
                if((!"201".equals(context.getAid())) && (!Cfg.PLATFORM_TBOX.equals(RequestContext.getContext().getPlatform()))){
                    AdapterHelper.setProperty(context.getRequestObject(), "dispatcherBody.token", context.getToken());
                }
                //init serviceHelper
                ServiceHelper serviceHelper = DubboHelper.initDubboService(aid);
                if(DubboHelper.isMessage(aid) || DubboHelper.isVp(aid) || DubboHelper.isNavi(aid)) {
                    //根据各种条件组合获取protocol，通过protocol获取callback
                    //传protocol入callback执行invoke
                    MessageTemplate.Protocol protocol = messageTemplate.findProtocol(context);
                    //OTACallback callback = protocol.getCallback();
                    //requestBack = callback.invoke(protocol, context.getRequestObject());
                    Object requestObject = context.getRequestObject();
                    //转换请求对象为服务端对象
                    Object serviceObj = changeClientObj2ServerObj(protocol, requestObject);

                    //编码服务端请求对象为字符串
                    String serviceSource = changeObj2String(protocol.getServicePlatform(), protocol.getServiceVersion(), serviceObj);
                    context.setSource(serviceSource);
                    if(null != serviceHelper){
                        requestBack = serviceHelper.callDubboService(context);
                    }
                    //解码服务端的响应字符串为服务端响应对象
                    Object serviceResult = adapterReceive(protocol, requestBack);

                    //处理服务端响应对象，复制返回值到原始请求对象
                    processServiceResult(protocol, requestObject, serviceResult);
                    //请求对象编码为字符串
                    requestBack = changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), requestObject);
                }else {
                    if(null != serviceHelper){
                        requestBack = serviceHelper.callDubboService(context);
                    }
                }

            }
            if(!"113".equals(context.getAid())){
                //记录返回的日志
                LogHelper.returnResultInfo(context.getAid(), context.getMid(), context.getVin(), context.getUid(), requestBack, "TSIP", "", "", from, context.getToken());
            }
        } catch (Exception e) {
            requestBack = ExceptionHandler.processException(LOGGER,e,context.getAid());
        } finally {
            RequestContext.clear();
            LOGGER.debug("exit ApplicationService.execute(), spend time:" + (System.currentTimeMillis() - start));
        }
        return requestBack;
    }

    /**
     * 处理errorCode and errorMessage
     *
     * @param request
     * @param result
     * @param errorCode
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws UnsupportedEncodingException
     */
    void processError(Object request, Object result, Integer errorCode) throws Exception {
        if (errorCode > 10000 && Cfg.PLATFORM_NEED_CONVERT_ERROR_CODE.contains(RequestContext.getContext().getPlatform())) {
            errorCode = Integer.valueOf(ErrorMessageHelper.getErrorMapping(String.valueOf(errorCode), RequestContext.getContext().getAid()));
        }
        PropertyUtils.setNestedProperty(request, "dispatcherBody.result", errorCode);

        //处理服务端返回的errorMessage
        if (errorCode != 0 && !Cfg.PLATFORM_TBOX.equals(RequestContext.getContext().getPlatform())) {
            Object serviceErrorMessage = PropertyUtils.getNestedProperty(result, "dispatcherBody.errorMessage");

            if (serviceErrorMessage == null) {
                PropertyUtils.setNestedProperty(request, "dispatcherBody.errorMessage", ErrorMessageHelper.getErrorMessage(errorCode).getBytes("utf-8"));
            } else {
                PropertyUtils.setNestedProperty(request, "dispatcherBody.errorMessage", serviceErrorMessage);
            }
        }
    }

    /**
     * 将服务端对象里的应用数据转换为客户端需要的格式，子类可覆盖此方法提供定制的应用数据转换
     *
     * @param protocol
     * @param requestObj
     * @param serviceObj
     */
    void transformServiceObj2Obj(MessageTemplate.Protocol protocol, Object requestObj, Object serviceObj) {
        try {
            PropertyUtils.setProperty(requestObj, "applicationData", PropertyUtils.getProperty(serviceObj, "applicationData"));
        } catch (Exception e) {
            throw new AdapterException(e);
        }
    }

    /**
     * 处理服务返回的响应对象, 把响应内容填充到原始请求
     *
     * @param protocol
     * @param requestObj
     * @param result
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws UnsupportedEncodingException
     */
    void processServiceResult(MessageTemplate.Protocol protocol, Object requestObj, Object result) {
        try {
            Integer resultCode = (Integer) PropertyUtils.getNestedProperty(result, "dispatcherBody.result");
            if (resultCode != 0) {
                processError(requestObj, result, resultCode);
                PropertyUtils.setProperty(requestObj, "applicationData", new byte[0]);
            } else {
                PropertyUtils.setNestedProperty(requestObj, "dispatcherBody.result", 0);
                PropertyUtils.setNestedProperty(requestObj, "dispatcherBody.eventID", PropertyUtils.getNestedProperty(result, "dispatcherBody.eventID"));

                transformServiceObj2Obj(protocol, requestObj, result);
            }
            PropertyUtils.setNestedProperty(requestObj, "dispatcherBody.messageID", 2);
            PropertyUtils.setNestedProperty(requestObj, "dispatcherBody.messageCounter.downlinkCounter", (Integer) PropertyUtils.getNestedProperty(requestObj, "dispatcherBody.messageCounter.downlinkCounter") + 1);
        } catch (Exception e) {
            throw new AdapterException("处理服务端接口异常", e);
        }
    }


    /**
     * 接受服务端返回的字符串，解码成响应对象
     *
     * @param protocol
     * @param result
     * @return
     */
    Object adapterReceive(MessageTemplate.Protocol protocol, String result) {
        return AdapterHelper.adapterReceive(protocol.getServicePlatform(), protocol.getServiceVersion(), result);
    }

    /**
     * 转换客户端请求对象为服务端请求对象
     * @param protocol
     * @param request
     * @return
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     */
    Object changeClientObj2ServerObj(MessageTemplate.Protocol protocol, Object request) {
        try {
            Object serviceObj = transformRequestObj2ServiceObj(protocol, request);

            //把为tbox生成的伪token设置到服务端的请求，用于追踪日志
            if (Cfg.PLATFORM_TBOX.equals(RequestContext.getContext().getPlatform())) {
                PropertyUtils.setProperty(serviceObj, "dispatcherBody.token", RequestContext.getContext().getToken());
            }

            PropertyUtils.setNestedProperty(serviceObj, "dispatcherBody.applicationID", protocol.getSpAppID());
            PropertyUtils.setNestedProperty(serviceObj, "dispatcherBody.messageID", protocol.getSpMsgID());
            PropertyUtils.setNestedProperty(serviceObj, "dispatcherHeader.protocolVersion", BitReader.toIntegerProtocol(protocol.getServiceVersion()));
            PropertyUtils.setNestedProperty(serviceObj, "dispatcherBody.platformID", "0000000" + RequestContext.getContext().getPlatform());
            return serviceObj;
        } catch (Exception e) {
            throw new AdapterException("转换客户端请求对象到服务器端出错", e);
        }
    }

    /**
     * 将请求对象转换为服务端对象
     * 子类可以覆盖此方法，用于自定义的数据转换，如adapter格式的转换
     * @param protocol
     * @param obj
     * @return
     */
    Object transformRequestObj2ServiceObj(MessageTemplate.Protocol protocol, Object sourceObject) {
        Class sourceClz = AdapterHelper.getRequestObjClass(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion());
        Class targetClz = AdapterHelper.getRequestObjClass(protocol.getServicePlatform(), protocol.getServiceVersion());
//        OTATransform ots = null;
//        try{
//            ots = new OTATransform();
//        }catch (Exception e){
//            System.out.println("++++++++++++++++++++++++++++++++++++++++++");
//        }

        if (targetClz == null || sourceClz == null || sourceObject == null) {
            throw new IllegalArgumentException("arguments cannot be null!");
        }
        return OTATransform.transform(sourceClz, targetClz, sourceObject, null);

//        return ots.transform(sourceClass, targetClass, obj);
    }



    /**
     * 编码请求对象为字符串
     *
     * @param platform
     * @param version
     * @param requestObject
     * @return
     */
    String changeObj2String(String platform, String version, Object requestObject) {
        return new String(AdapterHelper.adapterGetBytesData(platform, version, requestObject));
    }
}


