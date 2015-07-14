/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.mp.login;

import com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.OTACallback;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.MessageTemplate;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ApplicationException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ErrorMessageHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ExceptionHandler;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.OTATransform;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: szksr
 * Date: 13-10-22
 * Time: 上午10:08
 * To change this template use File | Settings | File Templates.
 */
public class MPUserLoggingInReqCallback extends OTACallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(MPUserLoggingInReqCallback.class);

    private static final int APP_UPGRADE_ERROR_CODE = 7;

    public String invoke(MessageTemplate.Protocol protocol, Object requestObject) {
        try {
            String appVersion = RequestContext.getContext().getAppVersion();
            if ("1.0".equals(appVersion)) {
                PropertyUtils.setNestedProperty(requestObject, "dispatcherBody.result", APP_UPGRADE_ERROR_CODE);
                PropertyUtils.setNestedProperty(requestObject, "dispatcherBody.errorMessage", ErrorMessageHelper.getErrorMessage(APP_UPGRADE_ERROR_CODE).getBytes("utf-8"));
                PropertyUtils.setProperty(requestObject, "applicationData", new byte[0]);
                PropertyUtils.setNestedProperty(requestObject, "dispatcherBody.messageID", 2);
                PropertyUtils.setNestedProperty(requestObject, "dispatcherBody.messageCounter.downlinkCounter", (Integer) PropertyUtils.getNestedProperty(requestObject, "dispatcherBody.messageCounter.downlinkCounter") + 1);
            } else {
                //转换请求对象为服务端对象
                Object serviceObj = changeClientObj2ServerObj(protocol, requestObject);

                //编码服务端请求对象为字符串
                String serviceSource = changeObj2String(protocol.getServicePlatform(), protocol.getServiceVersion(), serviceObj);

                //调用服务
                String serviceResponse = invokeService(protocol, serviceSource);

                //解码服务端的响应字符串为服务端响应对象
                Object serviceResult = adapterReceive(protocol, serviceResponse);

                //处理服务端响应对象，复制返回值到原始请求对象
                processServiceResult(protocol, requestObject, serviceResult);
            }
            //请求对象编码为字符串
            String returnString = changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), requestObject);

            return returnString;
        } catch (ApplicationException e) {
            return ExceptionHandler.processException(LOGGER, e);
        } catch (RuntimeException re) {
            if (re.getCause() != null && re.getCause() instanceof ApplicationException) {
                return ExceptionHandler.processException(LOGGER, (ApplicationException) re.getCause());
            } else {
                return ExceptionHandler.processException(LOGGER, new ApplicationException("702", re));
            }
        } catch (Exception ex) {
            return ExceptionHandler.processException(LOGGER, new ApplicationException("702", ex));
        }

    }

    @Override
    protected void transformServiceObj2Obj(MessageTemplate.Protocol protocol, final Object requestObj, final Object serviceObj){
        String appVersion = RequestContext.getContext().getAppVersion();

        if ("2.0".equals(appVersion)) {
            new OTATransform.Converter() {
                @Override
                public void convert(Object requestObj, Object serverObj) {
                    AdapterHelper.setProperty(requestObj, "dispatcherBody.uid", AdapterHelper.getProperty(serverObj,"dispatcherBody.uid"));
                    AdapterHelper.setProperty(requestObj, "applicationData", AdapterHelper.getProperty(serverObj, "applicationData"));
                }
            }.convert(requestObj, serviceObj);
        }
    }
}
