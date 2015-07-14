package com.saicmotor.telematics.tsgp.tsip.httpserv.base.client;

import com.saicmotor.telematics.tsgp.tsip.httpserv.base.exception.HTTPServException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: rhnfv
 * Date: 15-7-3
 * Time: 下午1:35
 * To change this template use File | Settings | File Templates.
 */
public class HttpClientUtils {

    private static CloseableHttpClient httpClient = null;
    private static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(30, TimeUnit.SECONDS); //  timeToLive;
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);
    private static final int REQUEST_TIMEOUT = 20 * 1000;    //设置请求超时10秒钟
    private static final int CO_TIMEOUT = 20 * 1000;         //设置等待数据超时时间10秒钟
    private static final int MAX_CONN_NUM = 50;     //总连接数
    private static final Long CONN_MANAGER_TIMEOUT = 500L;

    static {
        //设置连接超时时间
        //将目标主机的最大连接数增加到50
        HttpHost localhost = new HttpHost("localhost", 80);
        cm.setMaxPerRoute(new HttpRoute(localhost), 100);
        cm.setMaxTotal(MAX_CONN_NUM);
        // 将每个路由基础的连接增加到20
        cm.setDefaultMaxPerRoute(20);
        new IdleConnectionMonitorThread(cm);
    }


    public static CloseableHttpClient getHttpClient() {
        if (null != httpClient) {
            return httpClient;
        }

        //创建http request的配置信息
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(REQUEST_TIMEOUT).setConnectTimeout(CO_TIMEOUT).build();
        //初始化httpclient客户端
        //DefaultHttpRequestRetryHandler重试次数  默认3次数
        httpClient = HttpClients.custom().setConnectionManager(cm).setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).setDefaultRequestConfig(requestConfig).setMaxConnTotal(MAX_CONN_NUM)
                .setConnectionTimeToLive(20, TimeUnit.SECONDS).build();
        return httpClient;
    }


    public static String sendPostRequest(String url, String source) throws Exception {
        String result = null;
        HttpContext context = HttpClientContext.create();
        CloseableHttpClient client = getHttpClient();

        HttpPost httppost = new HttpPost(url);
        if (null != source && !source.isEmpty()) {
            StringEntity stringEntity = new StringEntity(source);
            stringEntity.setContentEncoding("iso-8859-1");
            stringEntity.setContentType("text/html");//发送json数据需要设置contentType
            httppost.setEntity(stringEntity);
        }
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httppost, context);
            int statusCode = response.getStatusLine().getStatusCode();
            if (200 != statusCode) {
                LOGGER.debug("http client receive data: status code=" + statusCode + ", url: " + url);
                throw new HTTPServException("status code=" + statusCode + ", url: " + url);
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = new String(readInputStream(entity.getContent()), "iso-8859-1");
//            result = EntityUtils.toString(entity);
            }
        } finally {
            if (null != response) {
                EntityUtils.consume(response.getEntity()); //会自动释放连接
//                response.close();
            }
        }
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

class IdleConnectionMonitorThread extends Thread {
    private final PoolingHttpClientConnectionManager connMgr;
    private volatile boolean shutdown;

    public IdleConnectionMonitorThread(PoolingHttpClientConnectionManager connMgr) {
        super();
        this.setName("idle-connection-monitor");
        this.setDaemon(true);
        this.connMgr = connMgr;
        this.start();
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(60 * 1000);
                    // Close expired connections
                    connMgr.closeExpiredConnections();
                    // Optionally, close connections
                    // that have been idle longer than 30 sec
                    connMgr.closeIdleConnections(60, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
            // terminate
        }
    }

    public void shutdown() {
        synchronized (this) {
            shutdown = true;
            notifyAll();
        }
    }
}


