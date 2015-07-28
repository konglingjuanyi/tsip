/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception;


import com.saicmotor.telematics.framework.core.exception.ApiException;
import com.saicmotor.telematics.framework.core.exception.ServLayerException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.LogHelper;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

/**
 * 异常处理类
 * User: iwskd
 * Date: 13-2-19
 * Time: 下午1:38
 * To change this template use File | Settings | File Templates.
 */
public class ExceptionHandler {

    private ExceptionHandler() {
    }

//    /**
//     * 处理异常
//     */
//    public static String processException(Logger logger, ApplicationException e) {
//        try{
//            LogHelper.error(logger, RequestContext.getContext()==null?new RequestContext():RequestContext.getContext(), e);
//            e.getCode();
//            //从ErrorMessageHelper获取异常信息
//            Integer errorCode = Integer.valueOf(e.getCode());
//            String errorMessage = ErrorMessageHelper.getErrorMessage(e.getCode());
//            PropertyUtils.setNestedProperty(RequestContext.getContext().getRequestObject(), "dispatcherBody.result", errorCode);
//            if(!Cfg.PLATFORM_TBOX.equals(RequestContext.getContext().getPlatform()) && StringUtils.isNotBlank(errorMessage)){
//                PropertyUtils.setNestedProperty(RequestContext.getContext().getRequestObject(), "dispatcherBody.errorMessage", errorMessage.getBytes("utf-8"));
//            }
//            PropertyUtils.setProperty(RequestContext.getContext().getRequestObject(),"applicationData",new byte[0]);
//            return new String(AdapterHelper.adapterGetBytesData(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), RequestContext.getContext().getRequestObject()));
//        }catch (Exception ex){
//            throw new TSIPException("处理异常结果出错。",ex);
//        }
//    }

    /**
     * 处理异常
     */
    public static String processException(Logger logger, Exception e) {
        if(e.getClass().equals(ServLayerException.class)){
            ServLayerException sle = (ServLayerException)e;
            try{
//                LogHelper.error(logger, RequestContext.getContext()==null?new RequestContext():RequestContext.getContext(), e);
                //从ErrorMessageHelper获取异常信息
                Integer errorCode = Integer.valueOf(sle.getCode());
                String errorMessage = ErrorMessageHelper.getErrorMessage(sle.getCode());
                PropertyUtils.setNestedProperty(RequestContext.getContext().getRequestObject(), "dispatcherBody.result", errorCode);
                if(!Cfg.PLATFORM_TBOX.equals(RequestContext.getContext().getPlatform()) && StringUtils.isNotBlank(errorMessage)){
                    PropertyUtils.setNestedProperty(RequestContext.getContext().getRequestObject(), "dispatcherBody.errorMessage", errorMessage.getBytes("utf-8"));
                }
                PropertyUtils.setProperty(RequestContext.getContext().getRequestObject(),"applicationData",new byte[0]);
            }catch (Exception ex){
                throw new TSIPException("处理异常结果出错。",ex);
            }
        }
        return new String(AdapterHelper.adapterGetBytesData(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), RequestContext.getContext().getRequestObject()));

//        try{
//            LogHelper.error(logger, RequestContext.getContext()==null?new RequestContext():RequestContext.getContext(), e);
//            e.getCode();
//            //从ErrorMessageHelper获取异常信息
//            Integer errorCode = Integer.valueOf(e.getCode());
//            String errorMessage = ErrorMessageHelper.getErrorMessage(e.getCode());
//            PropertyUtils.setNestedProperty(RequestContext.getContext().getRequestObject(), "dispatcherBody.result", errorCode);
//            if(!Cfg.PLATFORM_TBOX.equals(RequestContext.getContext().getPlatform()) && StringUtils.isNotBlank(errorMessage)){
//                PropertyUtils.setNestedProperty(RequestContext.getContext().getRequestObject(), "dispatcherBody.errorMessage", errorMessage.getBytes("utf-8"));
//            }
//            PropertyUtils.setProperty(RequestContext.getContext().getRequestObject(),"applicationData",new byte[0]);
//            return new String(AdapterHelper.adapterGetBytesData(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), RequestContext.getContext().getRequestObject()));
//        }catch (Exception ex){
//            throw new TSIPException("处理异常结果出错。",ex);
//        }
    }
}

