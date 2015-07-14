/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.httpserv.base.client;

import java.io.IOException;

/**
 * OTA 请求客户端接口
 * @author xujunjie
 */
public interface IClient {

    /**
     * 发送数据
     * @param url 目标url
     * @param source OTA编码字符串
     * @return
     * @throws IOException
     */
    public String sendData(String url, String source)throws IOException;

    /**
     * 发送数据, 设定超时时间
     * @param url 目标url
     * @param source OTA编码字符串
     * @param connectionTimeoutMillis 连接超时设置
     * @param socketTimeoutMillis socket读取超时设置
     * @return
     * @throws IOException
     */
    public String sendData(String url, String source, Integer connectionTimeoutMillis, Integer socketTimeoutMillis )throws IOException;
}
