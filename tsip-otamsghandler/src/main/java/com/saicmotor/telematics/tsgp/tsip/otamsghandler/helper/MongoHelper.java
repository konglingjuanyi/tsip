/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.TSIPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB的帮助类
 *
 * @author zhuxiaoyan
 */
public class MongoHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoHelper.class);
//    public static final String MONGO_KEY = "mongoclient";
//    public static final String HOST_KEY = "host";

    private static MongoClient client;

    private MongoHelper() {

    }

    /**
     * 初始化MongoClient
     */
    public static void initMongoClient() {
        try {
            LOGGER.debug("initializing MongoClient");

//            String str = GuiceContext.getInstance().getConfig().getProperties().get(MONGO_KEY).get(HOST_KEY).toString();
            String str = SpringContext.getInstance().getProperty("mongoclient.host");
            String[] array = str.split(",");
            List<ServerAddress> list = new ArrayList<ServerAddress>();
            for(int i=0;i<array.length;i++) {
                String tmp = array[i];
                ServerAddress address = new ServerAddress(tmp.split(":")[0],Integer.parseInt(tmp.split(":")[1]));
                list.add(address);
            }
            MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
            builder.alwaysUseMBeans(false);
            client = new MongoClient(list, builder.build());
            LOGGER.debug("MongoClient initialized");
        } catch (Exception e) {
            LOGGER.error("MongoClient initialization failure!", e);
            throw new TSIPException("MongoClient initialization failure!", e);
        }
    }

    /**
     * 获取MongoClient
     * @return
     */
    public static MongoClient getClient() {
        if (client == null) {
            initMongoClient();
        }

        return client;
    }
}
