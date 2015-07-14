/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.cc;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.OTACallback;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.MessageTemplate;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.AdapterException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ApplicationException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.TSIPException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.LogHelper;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.*;
import java.util.Map;

/**
 * 处理博泰天气服务请求的Callback
 * Author: cqzzl
 */
public class PateoWeatherCallback extends OTACallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(PateoWeatherCallback.class);

    @Override
    protected String invokeService(MessageTemplate.Protocol protocol, String source) throws ApplicationException {
        String serverPlatform = protocol.getServicePlatform();
        String errorCode = Cfg.PL_ERROR_CODE_MAP.get(serverPlatform);
        String platformStr = Cfg.getPlatformStrByID(serverPlatform);
        String url;
        if (protocol.getUrlFlag() != null) {
            url = Cfg.getUrlByFlag(protocol.getUrlFlag());
        } else {
            url = Cfg.getUrlByFlag(platformStr);
        }
        String token = RequestContext.getContext().getToken();
        LogHelper.info(LOGGER, RequestContext.getContext().getAid(), RequestContext.getContext().getMid(), RequestContext.getContext().getVin(),
                RequestContext.getContext().getUid(), source, "TSIP", "", url, platformStr, token);
        try{
            return invokePateo(source, url);
        }catch(Exception e){
            ApplicationException ex = new ApplicationException(errorCode,e);
            LogHelper.error(LOGGER,RequestContext.getContext(),ex);
            throw ex;
        }
    }

    /**
     * 处理服务返回的响应对象, 把响应内容填充到原始请求
     *
     * @param protocol
     * @param requestObj
     * @param result
     * @throws java.lang.reflect.InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws UnsupportedEncodingException
     */
    protected void processServiceResult(MessageTemplate.Protocol protocol, Object requestObj, Object result) {
        try {
            Integer resultCode = (Integer) PropertyUtils.getNestedProperty(result, "dispatcherBody.result");
            if (resultCode != 0) {
                processError(requestObj, result, resultCode);
                PropertyUtils.setProperty(requestObj, "applicationData", new byte[0]);

                LogHelper.info(LOGGER, RequestContext.getContext().getAid(), 2, RequestContext.getContext().getVin(),
                        RequestContext.getContext().getUid(), "博泰天气返回result: " + result, "CC", "", "", "TSIP", RequestContext.getContext().getToken());

            } else {
                PropertyUtils.setNestedProperty(requestObj, "dispatcherBody.result", 0);
                PropertyUtils.setNestedProperty(requestObj, "dispatcherBody.eventID", PropertyUtils.getNestedProperty(result, "dispatcherBody.eventID"));

                transformServiceObj2Obj(protocol, requestObj, result);
            }
            PropertyUtils.setNestedProperty(requestObj, "dispatcherBody.messageID", 2);
            PropertyUtils.setNestedProperty(requestObj, "dispatcherBody.messageCounter.downlinkCounter", (Integer) PropertyUtils.getNestedProperty(requestObj, "dispatcherBody.messageCounter.downlinkCounter") + 1);
        } catch (Exception e) {
            throw new AdapterException("处理服务端接口异常", e);
        }
    }


    /**
     * 调用博泰提供的天气服务WebService
     * @return
     */
    private String invokePateo(String source, String url){

        HttpURLConnection conn = null;
        BufferedReader bufferedReader = null;
        try{
            URL url1 = new URL(url);

//            Map<String,String> map = SpringContext.getInstance().getConfig().getProperties().get("ccProxy");

            String user = SpringContext.getInstance().getProperty("ccProxy.user");
            String password = SpringContext.getInstance().getProperty("ccProxy.password");
            String witch =SpringContext.getInstance().getProperty("ccProxy.switch");
            String curl =SpringContext.getInstance().getProperty("ccProxy.url");
            String port = SpringContext.getInstance().getProperty("ccProxy.port");
            String connectionTimeout =SpringContext.getInstance().getProperty("ccProxy.connectionTimeout");
            String receiveTimeout =SpringContext.getInstance().getProperty("ccProxy.receiveTimeout");

            if(StringUtils.isNotEmpty(user)){
                Authenticator.setDefault(new DefaultAuthenticator(user, password));
            }
            InputStream is = null;
            if("true".equals(witch)){
                Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(curl, Integer.parseInt(port)));
                conn = (HttpURLConnection)url1.openConnection(proxy);
            }else {
                conn = (HttpURLConnection)url1.openConnection();
            }
            conn.setRequestProperty("Content-Type","text/xml;charset=utf-8");
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setConnectTimeout(Integer.parseInt(connectionTimeout));
            conn.setReadTimeout(Integer.parseInt(receiveTimeout));

            StringBuffer sb = new StringBuffer();
            sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            sb.append("	<soapenv:Header/>");
            sb.append("		<soapenv:Body>");
            sb.append("			<param>"+source+"</param>");
            sb.append("		</soapenv:Body>");
            sb.append("</soapenv:Envelope>");

            OutputStream os = conn.getOutputStream();
            os.write(sb.toString().getBytes());
            os.close();

            is = conn.getInputStream();
            if(conn.getResponseCode() != 200){
                throw new TSIPException("调用博泰天气服务出错，http返回码: " + conn.getResponseMessage());
            }

            bufferedReader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            StringBuffer str = new StringBuffer();
            String inputLine = "";
            while((inputLine = bufferedReader.readLine()) != null){
                str.append(inputLine);
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream xmlinput = new ByteArrayInputStream(str.toString().getBytes())  ;

            Document doc = builder.parse(xmlinput);
            Element root = doc.getDocumentElement();


            return root.getTextContent();
        }catch (Exception e){
            throw new ApplicationException("507",e);
        }finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException ioe) {
                LOGGER.error("closing bufferedReader error", ioe);
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

    }

    private class DefaultAuthenticator extends Authenticator {
        String username;
        String password;

        public DefaultAuthenticator(String username,String password){
            this.username =username;
            this.password=password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username,password.toCharArray());
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
