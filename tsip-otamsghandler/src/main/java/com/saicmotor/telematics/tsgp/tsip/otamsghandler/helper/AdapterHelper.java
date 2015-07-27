/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.otaadapter.as.entity.dispatcher.AS_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.as.service.ASAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.dispatcher.AVN_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.service.AVNAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.otaadapter.b2c.entity.dispatcher.B2C_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.b2c.service.B2CAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.otaadapter.cc.entity.dispatcher.CC_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.cc.service.CCAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.otaadapter.isp.entity.dispatcher.ISP_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.isp.service.ISPAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.service.TCMPAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.otaadapter.wxg4as.entity.dispatcher.WXG4AS_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.wxg4as.service.WXG4ASAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.otaaddpter.weather.entity.dispatcher.Weather_OTARequest;
import com.saicmotor.telematics.tsgp.otaaddpter.weather.service.WeatherAdapterServiceImpl;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.AdapterException;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 编解码AdapterService相关的帮助类
 * 从OTA_Request对象获取属性的帮助类
 * @author : jozbt
 * @author : cqzzl
 */
public class AdapterHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdapterHelper.class);

    /**
     * 存放各版本对应的AdapterService实例
     */
    private static Map<String, Object> adapterMap = new HashMap<String, Object>();
    /**
     * 存放各版本对应的OTA_Request类
     */
    private static Map<String, Class> requestObjMap = new HashMap<String, Class>();

    static {
        adapterMap.put(Cfg.PLATFORM_AVN + Cfg.CONNECTOR + Cfg.DEFAULT_VERSION, AVNAdapterServiceImpl.class);
        adapterMap.put(Cfg.PLATFORM_B2C + Cfg.CONNECTOR + Cfg.DEFAULT_VERSION, B2CAdapterServiceImpl.class);
        adapterMap.put(Cfg.PLATFORM_CC + Cfg.CONNECTOR + Cfg.DEFAULT_VERSION, CCAdapterServiceImpl.class);
        adapterMap.put(Cfg.PLATFORM_TCMP + Cfg.CONNECTOR + Cfg.DEFAULT_VERSION, TCMPAdapterServiceImpl.class);
        adapterMap.put(Cfg.PLATFORM_WXG4AS + Cfg.CONNECTOR + Cfg.DEFAULT_VERSION, WXG4ASAdapterServiceImpl.class);
        adapterMap.put(Cfg.PLATFORM_ASM + Cfg.CONNECTOR + Cfg.DEFAULT_VERSION, WXG4ASAdapterServiceImpl.class);
        adapterMap.put(Cfg.PLATFORM_AS + Cfg.CONNECTOR + Cfg.DEFAULT_VERSION, ASAdapterServiceImpl.class);
        adapterMap.put(Cfg.PLATFORM_ISP + Cfg.CONNECTOR + Cfg.DEFAULT_VERSION, ISPAdapterServiceImpl.class);
        adapterMap.put(Cfg.PLATFORM_WEATHER + Cfg.CONNECTOR + Cfg.DEFAULT_VERSION, WeatherAdapterServiceImpl.class);

        requestObjMap.put(Cfg.PLATFORM_AVN + Cfg.CONNECTOR + Cfg.DEFAULT_VERSION, AVN_OTARequest.class);
        requestObjMap.put(Cfg.PLATFORM_B2C + Cfg.CONNECTOR + Cfg.DEFAULT_VERSION, B2C_OTARequest.class);
        requestObjMap.put(Cfg.PLATFORM_CC + Cfg.CONNECTOR + Cfg.DEFAULT_VERSION, CC_OTARequest.class);
        requestObjMap.put(Cfg.PLATFORM_TCMP + Cfg.CONNECTOR + Cfg.DEFAULT_VERSION, TCMP_OTARequest.class);
        requestObjMap.put(Cfg.PLATFORM_WXG4AS + Cfg.CONNECTOR + Cfg.DEFAULT_VERSION, WXG4AS_OTARequest.class);
        requestObjMap.put(Cfg.PLATFORM_ASM + Cfg.CONNECTOR + Cfg.DEFAULT_VERSION, WXG4AS_OTARequest.class);
        requestObjMap.put(Cfg.PLATFORM_AS + Cfg.CONNECTOR + Cfg.DEFAULT_VERSION, AS_OTARequest.class);
        requestObjMap.put(Cfg.PLATFORM_ISP + Cfg.CONNECTOR + Cfg.DEFAULT_VERSION, ISP_OTARequest.class);
        requestObjMap.put(Cfg.PLATFORM_WEATHER + Cfg.CONNECTOR + Cfg.DEFAULT_VERSION, Weather_OTARequest.class);

        if (Cfg.VERSION_PATH_MAP != null) {
            Iterator it = Cfg.VERSION_PATH_MAP.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
                String key = entry.getKey();
                String platform = key.split("\\" + Cfg.CONNECTOR)[0];
                String path = entry.getValue();
                adapterMap.put(key, createObjectByName(String.format(Cfg.PL_ADAPTER_MAP.get(platform), path)));
                requestObjMap.put(key, createClass(String.format(Cfg.PL_REQUEST_MAP.get(platform), path)));
            }
        }
    }

    private AdapterHelper() {
    }

    /**
     * 获取对象的属性
     * @param obj 目标对象
     * @param name 对象属性名，可以嵌套获取，使用xxx.xxx.xx的形式
     * @return
     */
    public static Object getProperty(Object obj, String name) {
        try {
            return PropertyUtils.getNestedProperty(obj, name);
        } catch (Exception e) {
            LOGGER.error("获取属性失败", e);
            throw new AdapterException("获取属性失败", e);
        }
    }

    /**
     * 设置对象的属性
     * @param obj 目标对象
     * @param name 属性名称，key嵌套设置， 使用xxx.xxx.xx的形式
     * @param object
     */
    public static void setProperty(Object obj, String name, Object object) {
        try {
            PropertyUtils.setNestedProperty(obj, name, object);
        } catch (Exception e) {
            LOGGER.error("设置属性失败", e);
            throw new AdapterException("设置属性失败", e);
        }
    }

    private static Object createObjectByName(String className) {
        try {
            return Class.forName(className).newInstance();
        } catch (Exception e) {
            LOGGER.error("初始化adapter(" + className + ")异常", e);
            throw new AdapterException("初始化adapter(" + className + ")异常", e);
        }
    }

    private static Class createClass(String className) {
        try {
            return Class.forName(className);
        } catch (Exception e) {
            LOGGER.error("初始化class(" + className + ")异常", e);
            throw new AdapterException("初始化class(" + className + ")异常", e);
        }
    }

    /**
     * 获取特定平台对应某协议的AdapterService实现类
     * @param platform 平台代码
     * @param version 协议版本，X.X形式
     * @return AdapterService实现类
     */
    public static Object getAdapter(String platform, String version) {
        String key = platform + Cfg.CONNECTOR + version;
        Object adapterImpl = adapterMap.get(key);
        if (adapterImpl == null) {
            throw new AdapterException("Adapter不支持:" + key);
        }
        return adapterImpl;
    }

    /**
     * 获取特定平台对应协议的OTA_Request类
     * @param platform 平台代号
     * @param version 协议版本，X.X的形式
     * @return
     */
    public static Class getRequestObjClass(String platform, String version) {
        String key = platform + Cfg.CONNECTOR + version;
        Class adapterImpl = requestObjMap.get(key);
        if (adapterImpl == null) {
            throw new AdapterException("Request Obj不支持:" + key);
        }
        return adapterImpl;
    }

    /**
     * 对编码后的字符串进行解码
     * @param platform 平台代号
     * @param version 协议版本，X.X的形式
     * @param source 源字符串
     * @return 编码后的OTA_Request对象
     */
    public static Object adapterReceive(String platform, String version, String source) {
        Object adapterImpl = getAdapter(platform, version);
        try {
           // MethodUtils.setCacheMethods(true);
            Object obj = MethodUtils.invokeMethod(adapterImpl, "receive",
                    source.getBytes("UTF-8"));
            return obj;
        } catch (Exception e) {
            LOGGER.error("调用解码方法失败", e);
            throw new AdapterException("调用解码方法失败", e);
        }
    }

    /**
     * 编码OTA_Request对象
     *
     * @param platform 平台代码
     * @param version  协议版本
     * @param obj      OTA_Request对象
     * @return
     */
    public static byte[] adapterGetBytesData(String platform, String version, Object obj) {
        //获取adapterService实现类
        Object adapterImpl = getAdapter(platform, version);
        try {
//            MethodUtils.setCacheMethods(true);
            byte[] ret = (byte[]) MethodUtils.invokeMethod(adapterImpl, "getBytesData",
                    new Object[]{obj, "1"});
            return ret;
        } catch (Exception e) {
            LOGGER.error("调用编码方法失败", e);
            throw new AdapterException("调用编码方法失败", e);
        }
    }


}


