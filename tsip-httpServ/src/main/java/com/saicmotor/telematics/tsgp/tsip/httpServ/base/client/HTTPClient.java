/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.httpserv.base.client;

import com.saicmotor.telematics.tsgp.tsip.httpserv.base.exception.HTTPServException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * OTA http请求客户端
 * @author xujunjie
 */
public class HTTPClient implements IClient{
    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPClient.class);
    public static final String HTTP_CLIENT_EXCEPTION_MSG = "http连接错误：";

    public static void main(String[] args) throws Exception {
        String poiInformation="00D311000620081262C99B46AD9BB872C18B266D1AB66EE1CB062C99B46AD9BB872C18B266D1AB66EE1CB062C99B46AD9BB872C1947635A4CE62C99B46AD9BB872C593368D53EA7372404040044844C44844D911A2B3C4D11A2B3C4D11000000028000200003B9ACA0F";

        String source = poiInformation;
        String url ="http://localhost:80/TA11P.Web/ota.avn";
        String result = null;
        HTTPClient client = new HTTPClient();
        result = client.sendData(url,source);
        LOGGER.info("收到："+result);
    }

    /**
     * @see IClient
     * @param url 目标url
     * @param source OTA编码字符串
     * @return
     * @throws IOException
     */
    public String sendData( String url,String source) throws IOException{
        return sendData(url,source,null,null);
    }

    /**
     * @see IClient
     * @param address
     * @param source OTA编码字符串
     * @param connectionTimeoutMillis 连接超时设置
     * @param socketTimeoutMillis socket超时设置
     * @return
     * @throws IOException
     */
    public String sendData(String address,String source,Integer connectionTimeoutMillis, Integer socketTimeoutMillis ) throws IOException {
        HttpURLConnection urlConn=null;
        try{
            URL url = new URL(address);
            urlConn = (HttpURLConnection) url.openConnection();

            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);
            urlConn.setUseCaches(false);

            urlConn.setRequestProperty("Content-type","text/html");
            urlConn.setRequestProperty("Connection","close");
            urlConn.setRequestMethod("POST");
//            if(connectionTimeoutMillis!=null){
//                urlConn.setConnectTimeout(connectionTimeoutMillis);
//            }else {
//                urlConn.setConnectTimeout(30000);
//            }
//            if(socketTimeoutMillis!=null){
//                urlConn.setReadTimeout(socketTimeoutMillis);
//            }else {
//                urlConn.setReadTimeout(30000);
//            }

            urlConn.connect();

            byte[] bypes = source.getBytes();
            urlConn.getOutputStream().write(bypes);// 输入参数
            if(urlConn.getResponseCode()!=200){
                LOGGER.debug("http client receive data: status code=" + urlConn.getResponseCode() + ", url: " + url);
                throw new HTTPServException(HTTP_CLIENT_EXCEPTION_MSG + "status code=" + urlConn.getResponseCode() + ", url: " + url);
            }
            InputStream inStream=urlConn.getInputStream();
            String result = new String(readInputStream(inStream), "iso-8859-1");
            LOGGER.debug("http client receive data: url=" + url + ", result=" + result);
            return result;
        }
        finally{
            if(urlConn!=null){
                urlConn.disconnect();
            }
        }
    }



//    /**
//     * @see IClient
//     * @param address
//     * @param source OTA编码字符串
//     * @param connectionTimeoutMillis 连接超时设置
//     * @param socketTimeoutMillis socket超时设置
//     * @return
//     * @throws IOException
//     */
//    public String sendData(String address,String source,Integer connectionTimeoutMillis, Integer socketTimeoutMillis ) throws IOException {
//        String result = null;
//        try{
//            result = HttpClientUtils.sendPostRequest(address, source);
//        }catch (Exception ee){
//            LOGGER.debug("http client receive data: url=" + address + ", result=" + result);
//        }
//         return result;
//    }

    public static byte[] readInputStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream=null;
        byte[] data=null;
        try{
            outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while( (len = inStream.read(buffer)) !=-1 ){
                outStream.write(buffer, 0, len);
            }
            data = outStream.toByteArray();//网页的二进制数据
            outStream.close();
            inStream.close();
        }catch(IOException e){
            LOGGER.error("read input stream error", e);
            throw e;
        }finally {
            if(outStream!=null){
                outStream.close();
            }
            if(inStream!=null){
                inStream.close();
            }
        }
        return data;
    }
}
