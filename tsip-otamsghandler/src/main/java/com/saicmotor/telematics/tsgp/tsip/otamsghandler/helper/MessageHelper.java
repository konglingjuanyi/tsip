/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.MessageTemplate;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ApplicationException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.TSIPException;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * OTA Message帮助类.
 *
 * @author jozbt
 * @author zhuxiaoyan
 */
public final class MessageHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHelper.class);

    private static final String PROTOCOLS = "protocols";
    private static final String CALLBACK = "callback";
    private static final String APPLICATION_ID = "applicationID";
    private static final String MESSAGE_ID = "messageID";
    private static final String SP_APPLICATION_ID = "spAppID";
    private static final String SP_MESSAGE_ID = "spMsgID";
    private static final String SERVICE_PLATFORM = "servicePlatform";
    private static final String CLIENT_VERSION = "clientVersion";
    private static final String SERVER_VERSION = "serviceVersion";
    private static final String URL_FLAG = "urlFlag";
    private static final String APP_VERSION = "appVersion";
    private static final String LANGUAGE = "lang";
    private static final String LOCATION = "location";

    private static Map<String, MessageTemplate> messages = new HashMap<String, MessageTemplate>();

    static {
        try {
            LOGGER.debug("initializing MessageTemplates");

//            String location = GuiceContext.getInstance().getConfig().getProperties().get("ota_config_location").get("value").toString();
            String location = SpringContext.getInstance().getProperty("ota_config_location.value");
//            String location = "config/ota";
            List<String> fileTypes = new ArrayList<String>();
            fileTypes.add("xml");
            List<File> configFiles = FileScanner.scan(location, fileTypes);

            initByFiles(configFiles);

            LOGGER.debug("MessageTemplates initialized");
        } catch (Exception e) {
            LOGGER.error("reading ota.xml error", e);
            throw new ApplicationException("解析ota.xml失败", e);
        }
    }

    private MessageHelper() {

    }

    private static void initByFiles(List<File> configFiles) {
        try {
            for (File file : configFiles) {
                initByFile(file);
            }
        } catch (Exception e) {
            LOGGER.error("初始化ota协议配置失败", e);
        }
    }



    /**
     * Get message Object by ApplicationID & MessageID
     *
     * @param applicationID 应用ID
     * @param messageID     消息ID
     * @return 消息对象
     */
    public static MessageTemplate getMessage(String applicationID, Integer messageID) {
        return messages.get(applicationID + Cfg.CONNECTOR + messageID);
    }

    //根据文件初始化
    private static void initByFile(File file) {
        try {
            LOGGER.debug("reading messages from file: " + file.getAbsolutePath());
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(file);
            Element root = document.getRootElement();
            // 解析Message
            if (root != null) {
                List<Element> messageElements = root.elements();
                for (Element element : messageElements) {
                    String applicationID = element.attributeValue(APPLICATION_ID);
                    Integer messageID = new Integer(element.attributeValue(MESSAGE_ID));
                    String messageKey = applicationID + Cfg.CONNECTOR + messageID;

                    MessageTemplate message = messages.get(messageKey);
                    //初次创建messageTemplate
                    if (message == null) {
                        message = new MessageTemplate();
                        message.setApplicationID(applicationID);
                        message.setMessageID(messageID);
                        message.setServicePlatform(element.attributeValue(SERVICE_PLATFORM));
                        String spAppID = element.attributeValue(SP_APPLICATION_ID);
                        if (spAppID != null) {
                            message.setSpAppID(spAppID);
                        }
                        String spMsgID = element.attributeValue(SP_MESSAGE_ID);

                        if (spMsgID != null) {
                            message.setSpMsgID(new Integer(spMsgID));
                        }
                    }
                    //end

                    //配置callback, 如果有多个xml配置文件, 则后面的callback会覆盖之前的
                    if (element.element(CALLBACK) != null) {
                        String callbackClassName = element.element(CALLBACK).getText().trim();
                        message.setCallbackClassName(callbackClassName);
                        message.setCallBack(callbackClassName);
                    } else {
                        message.setDefaultCallBack();
                    }

                    //使用LinkedHashMap，保证在迭代keyset的时候出来顺序和ota.xml里添加的顺序一致
                    //优先选择先匹配中的protocol
                    if (element.element(PROTOCOLS) != null) {
                        addProtocol(element, message);
                    }

                    messages.put(messageKey, message);
                }
            }
        } catch (Exception e) {
            throw new TSIPException("从文件中加载OTA配置出错", e);
        }
    }

    //创建并添加协议到messageTemplate
    private static void addProtocol(Element messageEle, MessageTemplate message) throws ClassNotFoundException {
        Iterator<Element> it = messageEle.element(PROTOCOLS).elementIterator();
        while (it.hasNext()) {
            Element ele = it.next();
            MessageTemplate.Protocol protocol = message.new Protocol();

            protocol.setClientVersion(ele.attributeValue(CLIENT_VERSION));
            protocol.setServiceVersion(ele.attributeValue(SERVER_VERSION));
            if (ele.element(URL_FLAG) != null) {
                protocol.setUrlFlag(ele.element(URL_FLAG).getText().trim());
            }

            String protocolKey = protocol.getClientVersion() + "-";
            //每个protocol也可以提供单独的callback，覆盖上一级设置的callback
            if (ele.element(CALLBACK) != null) {
                protocol.setCallbackClassName(ele.element(CALLBACK).getText().trim());
                protocol.setCallback(ele.element(CALLBACK).getText().trim());
            }
            if (ele.attribute(APP_VERSION) != null) {
                protocol.setAppVersion(ele.attributeValue(APP_VERSION));
                protocolKey += protocol.getAppVersion() + "-";
            } else {
                protocolKey += "*-";
            }
            if (ele.attribute(LANGUAGE) != null) {
                protocol.setLang(ele.attributeValue(LANGUAGE));
                protocolKey += protocol.getLang() + "-";
            } else {
                protocolKey += "*-";
            }
            if (ele.attribute(LOCATION) != null) {
                protocol.setLocation(ele.attributeValue(LOCATION));
                protocolKey += protocol.getLocation();
            } else {
                protocolKey += "*";
            }

            //protocolKey的形式为clientVersion-appVersion-lang-location
            //转成正则字符串， 作为protocol的key
            protocolKey = protocolKey.replaceAll("\\.", "\\\\.").replaceAll("\\*", "\\.\\*").replaceAll("\\-", "\\\\-");

            message.addProtocol(protocolKey, protocol);
        }
    }
}
