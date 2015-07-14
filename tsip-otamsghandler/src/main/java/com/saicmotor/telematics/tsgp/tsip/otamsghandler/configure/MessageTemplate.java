/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.OTACallback;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 客户端请求消息对象模型
 * @author jozbt
 * @author cqzzl
 */
public class MessageTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageTemplate.class);
    private String applicationID;
    private Integer messageID;
    private String spAppID;
    private Integer spMsgID;
    private String callbackClassName;
    private OTACallback callBack;
    private String servicePlatform;
    private Map<String , Protocol> protocols;

    private Map<String, Pattern> patternMap = new HashMap<String, Pattern>();

    public Map<String, Protocol> getProtocols() {
        return protocols;
    }

    public void setProtocols(Map<String, Protocol> protocols) {
        this.protocols = protocols;
    }

    public String getServicePlatform() {
        return servicePlatform;
    }

    public void setServicePlatform(String serverPlatform) {
        this.servicePlatform = serverPlatform;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public Integer getMessageID() {
        return messageID;
    }

    public void setMessageID(Integer messageID) {
        this.messageID = messageID;
    }

    public String getSpAppID() {
        return spAppID;
    }

    public void setSpAppID(String spAppID) {
        this.spAppID = spAppID;
    }

    public Integer getSpMsgID() {
        return spMsgID;
    }

    public void setSpMsgID(Integer spMsgID) {
        this.spMsgID = spMsgID;
    }

    public String getCallbackClassName() {
        return callbackClassName;
    }

    public void setCallbackClassName(String callbackClassName) {
        this.callbackClassName = callbackClassName;
    }

    //设置Callback实例
    public void setCallBack(String callbackClassName){
        try {
            Class<?> clazz = Class.forName(callbackClassName);
            this.callBack = (OTACallback) SpringContext.getInstance().getBean(clazz);
        } catch (ClassNotFoundException e) {
            LOGGER.error("ClassNotFoundException:" + callbackClassName, e);
        }
    }

    public void setDefaultCallBack(){
        this.callBack = SpringContext.getInstance().getBean(OTACallback.class);
    }

    public OTACallback getCallback() {
        return this.callBack;
    }

    /**
     * 添加服务协议
     * @param protocolKey
     * @param protocol
     */
    public void addProtocol(String protocolKey, Protocol protocol) {
        if (this.protocols == null) {
            this.protocols = new LinkedHashMap<String, Protocol>();
        }
        this.protocols.put(protocolKey, protocol);
    }

    /**
     * 根据请求条件获取服务协议
     * @param context
     * @return
     */
    public Protocol findProtocol(RequestContext context) {

        //组装用来匹配的protocolKey的requestKey
        String requestKey = context.getClientVersion()
                + "-" + context.getAppVersion()
                + "-" + context.getLang()
                + "-" + context.getLocation();

        Set<Map.Entry<String, Protocol>> protocolEntries = getProtocols().entrySet();

        for (Map.Entry<String, Protocol> entry : protocolEntries) {
            String protocolKey = entry.getKey();
            Pattern p = getPattern(protocolKey);
            Matcher m = p.matcher(requestKey);
            if (m.matches()) {
                return entry.getValue();
            }
        }

        throw new ProtocolException("找不到对应的服务协议");
    }

    /**
     * 获取并缓存pattern
     * @param protocolKey
     * @return
     */
    private Pattern getPattern(String protocolKey) {
        Pattern p = patternMap.get(protocolKey);

        if (p == null) {
            p = Pattern.compile(protocolKey);
            patternMap.put(protocolKey, p);
        }
        return p;
    }

    /**
     * 封装了一组请求与服务协议的转换规则
      */
    public class Protocol {
        private String clientVersion;
        private String serviceVersion;
        private String urlFlag;

        private String callbackClassName;
        private String appVersion;
        private String lang;
        private String location;
        private OTACallback callback;

        public String getUrlFlag() {
            return urlFlag;
        }

        public void setUrlFlag(String urlFlag) {
            this.urlFlag = urlFlag;
        }

        public String getClientVersion() {
            return clientVersion;
        }

        public void setClientVersion(String clientVersion) {
            this.clientVersion = clientVersion;
        }

        public String getServiceVersion() {
            return serviceVersion;
        }

        public void setServiceVersion(String serviceVersion) {
            this.serviceVersion = serviceVersion;
        }

        public String getCallbackClassName() {
            return callbackClassName;
        }

        //加载Callback类并实例化
        public void setCallback(String callbackClassName) {
            try {
                Class<?> clazz = Class.forName(callbackClassName);
                this.callback = (OTACallback) SpringContext.getInstance().getBean(clazz);
            } catch (ClassNotFoundException e) {
                LOGGER.error("ClassNotFoundException:" + callbackClassName, e);
            }
        }

        public void setCallbackClassName(String callbackClassName) {
            this.callbackClassName = callbackClassName;
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

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getServicePlatform() {
            return MessageTemplate.this.servicePlatform;
        }

        public String getSpAppID() {
            return MessageTemplate.this.spAppID;
        }

        public Integer getSpMsgID() {
            return MessageTemplate.this.spMsgID;
        }

        public String getApplicationID() {
            return MessageTemplate.this.applicationID;
        }

        public Integer getMessageID() {
            return MessageTemplate.this.messageID;
        }

        //获取Callback，如果没有则返回MessageTemplate的Callback
        public OTACallback getCallback() {
            if (this.callback != null) {
                return this.callback;
            } else {
                return MessageTemplate.this.getCallback();
            }
        }
    }
}
