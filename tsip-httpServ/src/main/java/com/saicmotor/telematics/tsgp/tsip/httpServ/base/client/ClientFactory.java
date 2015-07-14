/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.httpserv.base.client;


import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: szksr
 * Date: 13-8-30
 * Time: 上午9:34
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ClientFactory {
    public static final String HTTP="http";
    public static final String WEBSERVICE="webservice";
    public static final String TCP="tcp";

    private static IClient httpClient = null;

    public static IClient getClient(String type){
        if(HTTP.equalsIgnoreCase(type)){
            if (httpClient == null) {
                httpClient = new HTTPClient();
            }
            return httpClient;
        }else if(WEBSERVICE.equalsIgnoreCase(type)){
            return null;
        }else if(TCP.equalsIgnoreCase(type)){
            return null;
        }
        return null;
    }





}
