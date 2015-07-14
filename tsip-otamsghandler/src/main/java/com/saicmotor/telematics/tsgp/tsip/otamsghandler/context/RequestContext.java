/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.context;

import com.saicmotor.telematics.tsgp.otaadapter.asn.utils.IntByteConvertor;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.BitReader;
import org.apache.commons.beanutils.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * OTA 请求上下文，存放请求相关参数
 * 使用initContext()初始化Context
 * 使用getContext()获取Context
 * 使用clear()清除Context
 * @author zhuxiaoyan
 */
public class RequestContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestContext.class);
    private static final ThreadLocal<RequestContext> RECORDER = new ThreadLocal<RequestContext>();

    private String aid;
    private Integer mid;
    private String uid;
    private String vin;
    private String token;
    private String platform;
    //请求字符串，不包含3位平台代码
    private String source;
    private Object requestObject;
    //请求客户端的协议版本
    private String clientVersion;
    //转换前的旧协议版本
    private String oldClientVersion;

    private String lang;
    //body里的applicationDataProtocolVersion
    private String appVersion;
    //转换前旧的app版本
    private String oldAppVersion;
    private Integer latitude;
    private Integer longitude;
    private String location;

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public Object getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(Object requestObject) {
        this.requestObject = requestObject;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Integer getLatitude() {
        return latitude;
    }

    public void setLatitude(Integer latitude) {
        this.latitude = latitude;
    }

    public Integer getLongitude() {
        return longitude;
    }

    public void setLongitude(Integer longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOldClientVersion() {
        return oldClientVersion;
    }

    public void setOldClientVersion(String oldClientVersion) {
        this.oldClientVersion = oldClientVersion;
    }

    public String getOldAppVersion() {
        return oldAppVersion;
    }

    public void setOldAppVersion(String oldAppVersion) {
        this.oldAppVersion = oldAppVersion;
    }

    /**
     * 初始化Context，封装请求数据，放在ThreadLocal中
     * @param platform 请求来源的平台代码
     * @param source 请求原始字符串，不包含3位平台代码
     * @return
     */
    public static RequestContext initContext(String platform, String source) {
        RequestContext context = new RequestContext();

        context.setSource(source);
        context.setPlatform(platform);

        String clientVersion = BitReader.getClientProtocol(source);
        context.setClientVersion(clientVersion);

        processClientVersion(context);

        Object request = AdapterHelper.adapterReceive(platform, context.getClientVersion(), source);
        context.setRequestObject(request);

        //aid/mid
        context.setAid((String) AdapterHelper.getProperty(request, "dispatcherBody.applicationID"));
        context.setMid((Integer) AdapterHelper.getProperty(request, "dispatcherBody.messageID"));

        //token/uid
        if (!Cfg.PLATFORM_TBOX.equals(platform)) {
            context.setToken((String) AdapterHelper.getProperty(request, "dispatcherBody.token"));
            context.setUid((String) AdapterHelper.getProperty(request, "dispatcherBody.uid"));
        }

        //如果请求不带token, 则生成一个随机token，只是用来追踪日志
        if (context.getToken() == null) {
            String token = UUID.randomUUID().toString().replaceAll("-", "");
            context.setToken(token);
        }

        //setup location
        if (Cfg.PLATFORM_MP.equals(platform)
                || Cfg.PLATFORM_AVN.equals(platform) || Cfg.PLATFORM_TBOX.equals(platform)) {
            Object basicPosition = AdapterHelper.getProperty(request, "dispatcherBody.basicPosition");
            if (basicPosition != null) {
                context.setLatitude((Integer) AdapterHelper.getProperty(basicPosition, "latitude"));
                context.setLongitude((Integer) AdapterHelper.getProperty(basicPosition, "longitude"));
            }
            //TODO 根据位置信息确认location
            //context.location = ...
        }

        //setup applicationDataVersion
        Integer appVersion = (Integer) AdapterHelper.getProperty(request, "dispatcherBody.applicationDataProtocolVersion");
        if (appVersion != null) {
            byte[] bytes = IntByteConvertor.intTo2Byte(appVersion);
            String versionStr = Byte.toString(bytes[0]) + "." + Byte.toString(bytes[1]);
            context.setAppVersion(versionStr);
        }

        processAppVersion(context);

        //setup language
        Object hmiLanguageType = AdapterHelper.getProperty(request, "dispatcherBody.hmiLanguage");
        if (hmiLanguageType != null) {
            try {
                context.setLang((String) MethodUtils.invokeMethod(AdapterHelper.getProperty(hmiLanguageType, "value"), "name", new Object[]{}));
            } catch (Exception e) {
                LOGGER.error("set language error", e);
            }
        }

        context.setVin((String) AdapterHelper.getProperty(request, "dispatcherBody.vin"));


        RECORDER.set(context);
        return context;
    }

    /**
     * 处理旧系统请求的错误协议版本号
     * @param context
     */
    private static void processClientVersion(RequestContext context) {
        String platform = context.getPlatform();
        String platformStr = Cfg.PL_STR_MAP.get(platform);

        String oldClientVersion = context.getClientVersion();
        String newClientVersion = Cfg.LEGACY_VERSION_MAP.get(platformStr + "-" + oldClientVersion);
        if (newClientVersion != null) {
            context.setOldClientVersion(oldClientVersion);
            context.setClientVersion(newClientVersion);
        }
    }

    /**
     * 处理旧系统请求的错误app版本号
     * @param context
     */
    private static void processAppVersion(RequestContext context) {
        String platform = context.getPlatform();
        String platformStr = Cfg.PL_STR_MAP.get(platform);

        String oldAppVersion = context.getAppVersion();
        String newAppVersion = Cfg.LEGACY_APP_VERSION_MAP.get(platformStr + "-" + oldAppVersion);
        if (newAppVersion != null) {
            context.setOldAppVersion(oldAppVersion);
            context.setAppVersion(newAppVersion);
        }

    }

    /**
     * 清除Context
     */
    public static void clear() {
        RECORDER.remove();
    }

    /**
     * 获取Context
     * @return
     */
    public static RequestContext getContext() {
        return RECORDER.get();
    }


}
