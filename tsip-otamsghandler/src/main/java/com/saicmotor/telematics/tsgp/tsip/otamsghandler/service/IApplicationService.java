/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.service;

/**
 * OTA请求控制接口，定义响应客户端请求以及主动发起请求的操作
 * @author cqzzl
 */
public interface IApplicationService {
    /**
     * 处理OTA请求
     * @param from 来源平台字符串
     * @param source 源字符串
     * @param platform 来源平台代码
     * @return
     */
    String execute(String from,String source,String platform);
}