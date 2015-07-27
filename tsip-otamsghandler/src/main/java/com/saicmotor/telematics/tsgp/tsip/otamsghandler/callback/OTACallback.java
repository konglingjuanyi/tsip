/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.client.ClientFactory;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.MessageTemplate;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.AdapterException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ApplicationException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ErrorMessageHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ExceptionHandler;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.BitReader;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.LogHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.OTATransform;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

/**
 * OTA请求回调处理通用类
 *
 * @author jozbt
 * @author zhuxioayan
 */
public class OTACallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(OTACallback.class);

    /**
     * 外部调用入口方法
     *
     * @param protocol
     * @param requestObject
     * @return
     */
    public String invoke(MessageTemplate.Protocol protocol, Object requestObject) {
        try {
            //转换请求对象为服务端对象
            Object serviceObj = changeClientObj2ServerObj(protocol, requestObject);
//
            //编码服务端请求对象为字符串
            String serviceSource = changeObj2String(protocol.getServicePlatform(), protocol.getServiceVersion(), serviceObj);

            //TODO CALL DUBBO SERVICE
            //调用服务
            String serviceResponse = invokeService(protocol, serviceSource);

            //解码服务端的响应字符串为服务端响应对象
            Object serviceResult = adapterReceive(protocol, serviceResponse);

            //处理服务端响应对象，复制返回值到原始请求对象
            processServiceResult(protocol, requestObject, serviceResult);

            //请求对象编码为字符串
            String returnString = changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), requestObject);

            return returnString;
        } catch (ApplicationException e) {
            return ExceptionHandler.processException(LOGGER, e);
        } catch (RuntimeException re) {
            if (re.getCause() != null && re.getCause() instanceof ApplicationException) {
                return ExceptionHandler.processException(LOGGER, (ApplicationException) re.getCause());
            } else {
                return ExceptionHandler.processException(LOGGER, new ApplicationException(702, re));
            }
        } catch (Exception ex) {
            return ExceptionHandler.processException(LOGGER, new ApplicationException(702, ex));
        }

    }

    /**
     * 接受服务端返回的字符串，解码成响应对象
     *
     * @param protocol
     * @param result
     * @return
     */
    protected Object adapterReceive(MessageTemplate.Protocol protocol, String result) {
        return AdapterHelper.adapterReceive(protocol.getServicePlatform(), protocol.getServiceVersion(), result);
    }

    /**
     * 调用服务, 返回服务方响应的字符串
     *
     * @param protocol
     * @param source
     * @return
     * @throws ApplicationException
     */
    protected String invokeService(MessageTemplate.Protocol protocol, String source) throws ApplicationException {
        String serverPlatform = protocol.getServicePlatform();
        //获取服务平台的公共错误码
        int errorCode = Integer.parseInt(Cfg.PL_ERROR_CODE_MAP.get(serverPlatform));
        //获取平台字符串描述
        String platformStr = Cfg.getPlatformStrByID(serverPlatform);
        String url;
        if (protocol.getUrlFlag() != null) {
            url = Cfg.getUrlByFlag(protocol.getUrlFlag());
        } else {
            url = Cfg.getUrlByFlag(platformStr);
        }
        String token = RequestContext.getContext().getToken();

        //记录转换后调用后台的请求
        LogHelper.info(LOGGER, protocol.getSpAppID(), protocol.getSpMsgID(), RequestContext.getContext().getVin(),
                RequestContext.getContext().getUid(), source, "TSIP", "", url, platformStr, token);
        try {
            //设置http请求的超时时间
//            Integer connectTimeout = Integer.valueOf((String) GuiceContext.getInstance().getConfig().getProperties().get("wsTimeout").get("connectTimeout"));
//            Integer receiveTimeout = Integer.valueOf((String) GuiceContext.getInstance().getConfig().getProperties().get("wsTimeout").get("receiveTimeout"));
            Integer connectTimeout = Integer.parseInt(SpringContext.getInstance().getProperty("wsTimeout.connectTimeout"));
            Integer receiveTimeout = Integer.parseInt(SpringContext.getInstance().getProperty("wsTimeout.receiveTimeout"));
            source = RequestContext.getContext().getPlatform() + source;
            return ClientFactory.getClient("http").sendData(url, source, connectTimeout, receiveTimeout);
        } catch (IOException e) {
            ApplicationException ex = new ApplicationException(errorCode, e);
            LogHelper.error(LOGGER, RequestContext.getContext(), ex);
            throw ex;
        }
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
    protected Object changeClientObj2ServerObj(MessageTemplate.Protocol protocol, Object request) {
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
    protected Object transformRequestObj2ServiceObj(MessageTemplate.Protocol protocol, Object obj) {
        Class sourceClass = AdapterHelper.getRequestObjClass(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion());
        Class targetClass = AdapterHelper.getRequestObjClass(protocol.getServicePlatform(), protocol.getServiceVersion());
        return OTATransform.transform(sourceClass, targetClass, obj);
    }

    /**
     * 将服务端对象里的应用数据转换为客户端需要的格式，子类可覆盖此方法提供定制的应用数据转换
     *
     * @param protocol
     * @param requestObj
     * @param serviceObj
     */
    protected void transformServiceObj2Obj(MessageTemplate.Protocol protocol, Object requestObj, Object serviceObj) {
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
    protected void processServiceResult(MessageTemplate.Protocol protocol, Object requestObj, Object result) {
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
    protected void processError(Object request, Object result, Integer errorCode) throws Exception {
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
     * 编码请求对象为字符串
     *
     * @param platform
     * @param version
     * @param requestObject
     * @return
     */
    protected String changeObj2String(String platform, String version, Object requestObject) {
        return new String(AdapterHelper.adapterGetBytesData(platform, version, requestObject));
    }

}
