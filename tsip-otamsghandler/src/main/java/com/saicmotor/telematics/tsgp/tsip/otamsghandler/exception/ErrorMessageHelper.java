/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception;

import com.mongodb.*;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.MongoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * 异常信息初始化类.
 * User: jozbt
 * Date: 13-4-1
 * Time: 上午10:29
 * To change this template use File | Settings | File Templates.
 */
public class ErrorMessageHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorMessageHelper.class);

    private static Map<String, String> errorMessages = new HashMap<String, String>();
    private static Map<String, String> errorMappings = new HashMap<String, String>();
    private static final String SERVER_INTERNAL_ERROR = "6";
    static {
        try {
            // 读取monggodb，加载消息对象
            fetchErrorMessage();
            fetchErrorMapping();
        } catch (Exception e) {
            LOGGER.error("fetching error message/mapping failure", e);
            throw new TSIPException("初始化错误码/错误码映射异常："+e.getMessage(),e);
        }
    }

    private ErrorMessageHelper() {

    }

    /**
     * 从MongoDB中获取异常消息
     * @throws UnknownHostException
     * @throws MongoException
     */
    private static void fetchErrorMessage() throws UnknownHostException, MongoException {
        LOGGER.debug("fetching Error Message from MongoDB");
        errorMessages.clear();
        MongoClient mongoClient = MongoHelper.getClient();
        //chose db
        DB db = mongoClient.getDB("AuthManager");
        //chose collection
        DBCollection collection = db.getCollection("errorMessage");
        DBCursor cursor = collection.find();
        while(cursor.hasNext()){
            DBObject dbObject =  cursor.next();
            errorMessages.put(String.valueOf(dbObject.get("errorCode")),(String)dbObject.get("errorMessage")) ;
        }
        cursor.close();
    }

    /**
     * 从MongoDB中获取异常映射
     * @throws UnknownHostException
     * @throws MongoException
     */
    private static void fetchErrorMapping() throws UnknownHostException, MongoException {
        LOGGER.debug("fetching Error Mapping from MongoDB");
        errorMappings.clear();
        MongoClient mongoClient = MongoHelper.getClient();
        //chose db
        DB db = mongoClient.getDB("AuthManager");
        //chose collection
        DBCollection collection = db.getCollection("errorMapping");
        DBCursor cursor = collection.find();
        while(cursor.hasNext()){
            DBObject dbObject =  cursor.next();
            errorMappings.put(String.valueOf(dbObject.get("innerErrorCode"))+"||"+String.valueOf(dbObject.get("aid")),String.valueOf(dbObject.get("outerErrorCode"))) ;
        }
        cursor.close();
    }

    public static String getErrorMessage(String errorCode) {
        return errorMessages.get(errorCode);
    }
    public static String getErrorMessage(Integer errorCode) {
        return errorMessages.get(String.valueOf(errorCode));
    }

    /**
     * 进行异常转换
     */
    public static String getErrorMapping(String errorCode, String aid) {
        //对于没有配置的code，直接返回6，服务端内部错误
        String ret = errorMappings.get(errorCode+"||"+aid);
        return ret==null?SERVER_INTERNAL_ERROR:ret;
    }

}

