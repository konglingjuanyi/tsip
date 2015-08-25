/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper;

import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTAEncoder;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.AdapterException;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * OTA_Request的转换工具
 *
 * @author zhuxiaoyan
 */
public class OTATransform {
    private static final Logger LOGGER = LoggerFactory.getLogger(OTATransform.class);

    private static DozerBeanMapper mapper = new DozerBeanMapper();

    /**
     * 转换不同平台的OTA_Request
     * @param sourceClz 源类型
     * @param targetClz 目标类型
     * @param sourceObject 源对象
     * @return
     */
    public static Object transform(Class sourceClz, Class targetClz,  Object sourceObject) {
        if (targetClz == null || sourceClz == null || sourceObject == null) {
            throw new IllegalArgumentException("arguments cannot be null!");
        }
        return transform(sourceClz, targetClz, sourceObject, null);
    }

    /**
     * 转换不同平台的OTA_Request
     * @param sourceClz 源类型
     * @param targetClz 目标类型
     * @param sourceObject 源对象
     * @param converter 请求/响应model的转换器
     * @return
     */
    public static Object transform(final Class sourceClz, final Class targetClz, Object sourceObject, Converter converter) {
        if (targetClz == null || sourceClz == null || sourceObject == null) {
            throw new IllegalArgumentException("the first three arguments cannot be null!");
        }

        try {
            Object targetObject = mapper.map(sourceObject, targetClz);

            //调用converter方法进行请求或响应的model转换
            if (converter != null) {
                converter.convert(sourceObject, targetObject);
            }

            return targetObject;
        } catch (Exception e) {
            LOGGER.error("OTARequest转换出错， from: " + sourceClz.getName()
                    + " to: " + targetClz.getName(), e);
            throw new AdapterException("OTARequest转换出错， from: " + sourceClz.getName()
                    + " to: " + targetClz.getName(), e);
        }
    }

    /**
     * 请求/响应model的转换器
     * 在转换ota request中的applicationData时候使用转换器修改Adapter的值
     */
    public static abstract class Converter {

        /**
         * @param sourceRequest 源OTARequest对象
         * @param targetRequest 目标OTARequest对象
         * @return
         */
        public abstract void convert(Object sourceRequest, Object targetRequest);

        /**
         * 帮助方法，快速把源目标对象的属性拷贝至目标对象
         * 对于复杂属性的对象可能会抛异常
         * @param source 源对象
         * @param targetClz 目标类型
         * @return
         */
        protected Object fastCopy(Object source, Class targetClz) {
            return mapper.map(source, targetClz);
        }

        /**
         * 编码帮助方法
         * @param obj
         * @return
         */
        protected static byte[] encode(Object obj) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            OTAEncoder encoder = new OTAEncoder(outputStream);
            encoder.encode(obj);
            return outputStream.toByteArray();
        }

        /**
         *解码的帮助类
         * @param clz
         * @param applicationData
         * @return
         */
        protected static Object decode(Class clz, byte[] applicationData) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(applicationData);
            OTADecoder decoder = new OTADecoder(inputStream);
            return decoder.decode(clz);
        }
    }
}