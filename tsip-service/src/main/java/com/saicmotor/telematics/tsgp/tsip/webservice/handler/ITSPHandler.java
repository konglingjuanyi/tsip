/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.webservice.handler;

import com.saicmotor.mce550.tsgp.tsip.msg.v1.SendDataRequest4TSP;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.SendDataResponse4TSP;

/**
 * TSP服务handler接口
 *
 * @author zhuxiaoyan
 */
public interface ITSPHandler {
    /**
     * 处理TSP请求
     * @param parameters TSP请求参数
     * @return 返回TSP请求处理结果
     */
    SendDataResponse4TSP process(SendDataRequest4TSP parameters);
}
