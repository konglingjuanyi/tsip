/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.framework.core.exception.GeneralRuntimeException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ErrorMessageHelper;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * 日志帮助类
 *
 * @author dongzehao
 */
public class LogHelper {
    private static final Logger RETURN_RESULT_LOGGER = LoggerFactory.getLogger("returnResultInfo");
    private static final Logger VEHICLE_REPORT_DATA = LoggerFactory.getLogger("vehicleReportDataInfo");
    private static final Logger LOGGER = LoggerFactory.getLogger(LogHelper.class);

    private LogHelper() {
    }

    /**
     * 记录返回信息，屏蔽异常
     */
    public static void returnResultInfo(String aid, Integer mid,  String vin, String uid,String content, String from,String clientIP, String serviceUrl,String target,String token){
        try{
            info(RETURN_RESULT_LOGGER,aid,mid, vin, uid, content, from, clientIP, serviceUrl,target,token);
        }catch(Exception e){
            LOGGER.error("记录返回日志错误", e);
        }
    }
    /**
     * 记录返回信息，屏蔽异常
     */
    public static void reportDataInfo(String aid, Integer mid, String vin,String content){
        try{
            info(VEHICLE_REPORT_DATA,aid,mid, vin, "", content, "TBOX", "", "","","");
        }catch(Exception e){
            LOGGER.error("记录返回日志错误", e);
        }
    }

    /**
     * 格式化tsip日志
     * @param aid
     * @param mid
     * @param vin
     * @param uid
     * @param content
     * @param from
     * @param target
     * @param clientIP
     * @param serviceUrl
     * @param token
     * @return
     */
    public static String formatTSIPLog(String aid, Integer mid, String vin, String uid, String content, String from,String target,String clientIP, String serviceUrl,String token ){
        return String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss SSS")
                ,from, target,aid,mid,vin,uid,convertByAid(aid,content),
                 clientIP,serviceUrl,token);
    }

    /**
     *
     * @param logger
     * @param aid
     * @param mid
     * @param vin
     * @param uid
     * @param content
     * @param from
     * @param token
     */
    public static void info(Logger logger, String aid, Integer mid, String vin, String uid, String content, String from,String token){
        logger.info(formatTSIPLog(aid,mid,vin,uid,content,from,"TSIP","","",token));
    }

    /**
     * 记录info日志
     * @param logger
     * @param from
     * @param target
     * @param aid
     * @param mid
     * @param vin
     * @param uid
     * @param message
     * @param clientIp
     * @param serviceUrl
     * @param token
     */
    public static void info(Logger logger, String from, String target,
                            String aid, Integer mid, String vin, String uid, String message,
                            String clientIp, String serviceUrl, String token) {
        logger
                .info(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                        new Object[]{
                                DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss SSS"), from, target,
                                aid, mid, vin, uid, getUtf8String(message),
                                clientIp, serviceUrl,
                                token
                        }));
    }

    private static String getUtf8String(String str){
        try {
            return new String(str.getBytes("utf-8"),"utf-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.debug("编码错误",e);
            return str;
        }
    }

    private static String convertByAid(String aid, String message){
        String aids =  SpringContext.getInstance().getProperty("convertLongMessage.aids");
        if(aids.contains(aid)){
            return SpringContext.getInstance().getProperty("convertLongMessage.logMessage");
//            return (String) GuiceContext.getInstance().getConfig().getProperties().get("convertLongMessage").get("logMessage");
        }
        return message;
    }


    /**
     *
     * @param logger
     * @param aid
     * @param mid
     * @param vin
     * @param uid
     * @param content
     * @param from
     * @param clientIP
     * @param serviceUrl
     * @param target
     * @param token
     */
    public static void info(Logger logger, String aid, Integer mid,  String vin, String uid,String content, String from,String clientIP, String serviceUrl,String target,String token){
        logger.info(formatTSIPLog(aid,mid,vin,uid,content,from,target,clientIP,serviceUrl,token));
    }

    /**
     * 记录异常日志
     * @param logger
     * @param context
     * @param exception
     */
    public static void error(Logger logger, RequestContext context, GeneralRuntimeException exception){

        String stackTrace = "";
        String message = "";
        if (exception != null) {
            //从errorMessageService获取
            message = ErrorMessageHelper.getErrorMessage(exception.getCode());//ErrorMessage em = errorMessageService.findByErrorCode(Integer.valueOf(exception.getErrorCode()));
            StringWriter writer = new StringWriter();
            exception.printStackTrace(new PrintWriter(writer));
            writer.flush();
            stackTrace = "\n" + writer.toString();
            try {
                writer.close();
            } catch (Exception e1) {
                LOGGER.error("关闭流失败", e1);
            }
        }

        logger.error(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%s",
                DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss SSS"), Thread.currentThread().getId(),
                exception.getCode(), message, context.getAid(), context.getMid(), context.getVin(), context.getUid(),
                context.getToken(), stackTrace));
    }

    /**
     * 记录异常日志
     * @param logger
     * @param aid
     * @param mid
     * @param vin
     * @param uid
     * @param errorCode
     * @param exception
     * @param token
     */
    public static void error(Logger logger, String aid, Integer mid, String vin, String uid, String errorCode, Throwable exception,String token){
        String stackTrace = "";
        String message = "";
        if (exception != null) {
            //从errorMessageService获取
            message = ErrorMessageHelper.getErrorMessage(errorCode);
            StringWriter writer = new StringWriter();
            exception.printStackTrace(new PrintWriter(writer));
            writer.flush();
            stackTrace = "\n" + writer.toString();
            try {
                writer.close();
            } catch (Exception e1) {
                LOGGER.error("关闭流失败", e1);
            }
        }

        logger.error(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%s",
                DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss SSS"),Thread.currentThread().getId(),
                errorCode, message, aid, mid, vin, uid,
                token, stackTrace));
    }
}
