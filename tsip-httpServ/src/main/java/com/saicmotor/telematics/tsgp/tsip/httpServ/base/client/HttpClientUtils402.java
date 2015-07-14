package com.saicmotor.telematics.tsgp.tsip.httpserv.base.client;

/**
 * Created with IntelliJ IDEA.
 * User: rhnfv
 * Date: 15-6-29
 * Time: 下午1:53
 * To change this template use File | Settings | File Templates.
 */

import com.saicmotor.telematics.tsgp.tsip.httpserv.base.exception.HTTPServException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class HttpClientUtils402 {
    private static final Log log = LogFactory.getLog(HttpClientUtils402.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils402.class);
    private static ThreadSafeClientConnManager cm = null;
    private static HttpClient httpClient = null;

    /**
     * 适合多线程的HttpClient,用httpClient4.2.1实现
     *
     * @return DefaultHttpClient
     */
    public static HttpClient getHttpClient() {
        if (null != httpClient) {
            return httpClient;
        }
        // 设置组件参数, HTTP协议的版本,1.1/1.0/0.9
        HttpParams params = new BasicHttpParams();
        //设置连接超时时间
        int REQUEST_TIMEOUT = 20 * 1000;    //设置请求超时10秒钟
        int SO_TIMEOUT = 20 * 1000;         //设置等待数据超时时间10秒钟
        HttpConnectionParams.setConnectionTimeout(params, REQUEST_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, REQUEST_TIMEOUT);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);

        //设置访问协议
        //多连接的线程安全的管理器
//        Scheme http = new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);
//        SchemeRegistry sr = new SchemeRegistry();
//        sr.register(http);

// Increase max total connection to 200
        ConnManagerParams.setMaxTotalConnections(params, 50);
// Increase default max connection per route to 20
        ConnPerRouteBean connPerRoute = new ConnPerRouteBean(20);
// Increase max connections for localhost:80 to 50
        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(
                new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);

        httpClient = new DefaultHttpClient(cm, params);
        return httpClient;
    }


    public static String sendPostRequest(String url, String source) throws Exception {
        String result = null;
        HttpClient client = getHttpClient();
        HttpPost httppost = new HttpPost(url);
        if (null != source && !source.isEmpty()) {
            StringEntity stringEntity = new StringEntity(source);
            stringEntity.setContentEncoding("iso-8859-1");
            stringEntity.setContentType("text/html");//发送json数据需要设置contentType
            httppost.setEntity(stringEntity);
        }
        HttpResponse response = client.execute(httppost);
        HttpEntity entity = response.getEntity();
        int statusCode = response.getStatusLine().getStatusCode();
        if (200 != statusCode) {
            LOGGER.debug("http client receive data: status code=" + statusCode + ", url: " + url);
            throw new HTTPServException("status code=" + statusCode + ", url: " + url);
        }
        if (entity != null) {
            result = new String(readInputStream(entity.getContent()), "iso-8859-1");
//            result = EntityUtils.toString(entity);
        }
        EntityUtils.consume(response.getEntity()); //会自动释放连接
        return result;
    }

    public static byte[] readInputStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = null;
        byte[] data = null;
        try {
            outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            data = outStream.toByteArray();//网页的二进制数据
            outStream.close();
            inStream.close();
        } catch (IOException e) {
            LOGGER.error("read input stream error", e);
            throw e;
        } finally {
            if (outStream != null) {
                outStream.close();
            }
            if (inStream != null) {
                inStream.close();
            }
        }
        return data;
    }


    public static void main(String[] args) throws Exception {
        while (true) {
            // TODO Auto-generated method stub
            String url = "http://10.91.225.61/TAP.Web/TestStartPage.jsp";
//POST的URL
            String result = sendPostRequest(url, "1234");
//发送Post,并返回一个HttpResponse对象
//得到返回的字符串
            System.out.println(result);
//打印输出
        }
    }
}
