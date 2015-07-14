package com.saicmotor.telematics.tsgp.tsip.autonavi;

import java.io.*;
import java.net.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: yicni
 * Date: 14-11-18
 * Time: 下午10:02
 * To change this template use File | Settings | File Templates.
 */
public class AutoNaviUtil {

    static String PROXY_HOST = "10.90.1.232";
    static int PROXY_PORT = 80;

    public static String authenSync(String urls,String content,String charset) {
        StringBuilder strResponse = new StringBuilder();
        try {
            URL url = new URL(urls);
            Proxy proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress(PROXY_HOST,PROXY_PORT));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection(proxy);
            //HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(60 * 1000 );//设置连接超时时间 1分钟
            connection.setRequestMethod("POST");
            connection.addRequestProperty("Content-Encoding", "gzip");
            connection.setRequestProperty("Content-Type", "application/json");

            // 读取json文件并压缩
            ByteArrayOutputStream outJson = new ByteArrayOutputStream();
            GZIPOutputStream gout = new GZIPOutputStream(outJson);
            gout.write(content.getBytes(charset));
            gout.close();
            //connection.addRequestProperty("Content-Length", outJson.size() + "");
            BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());
            InputStream fileInputStream = new ByteArrayInputStream(outJson.toByteArray());
            byte[] bytes = new byte[1024];
            int numReadByte = 0;
            while ((numReadByte = fileInputStream.read(bytes, 0, 1024)) > 0) {
                out.write(bytes, 0, numReadByte);
            }
            out.flush();
            fileInputStream.close();

            // 读取URLConnection的响应
            System.out.println("HTTP STATUS:" + connection.getResponseCode());
            BufferedInputStream in;
            if(connection.getResponseCode() == 200){
                in = new BufferedInputStream(connection.getInputStream());
            }else{
                in = new BufferedInputStream(connection.getErrorStream());
            }

            // 创建一个新的 byte 数组输出流
            ByteArrayOutputStream outReturn = new ByteArrayOutputStream();
            // 使用默认缓冲区大小创建新的输入流
            GZIPInputStream ginput = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n = 0;
            while ((n = ginput.read(buffer)) >= 0) {// 将未压缩数据读入字节数组
                // 将指定 byte 数组中从偏移量 off 开始的 len 个字节写入此 byte数组输出流
                outReturn.write(buffer, 0, n);
            }
            // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
            strResponse.append(outReturn.toString(charset));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return strResponse.toString();
    }


    public static String logonService(String uri) throws IOException {
        URL url = new URL(uri);
        Proxy proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress(PROXY_HOST,PROXY_PORT));
        HttpURLConnection connection = (HttpURLConnection)url.openConnection(proxy);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(60 * 1000 );//设置连接超时时间 1分钟
        connection.setRequestMethod("GET");
        InputStream inputStream = connection.getInputStream();
//        System.out.println(inputStream.read());
        ByteUtil byteUtil = new ByteUtil(inputStream);
        byte[] byte1 = new byte[2];
        byteUtil.read(byte1);
        System.out.println(ByteUtil.bytesToInt16(byte1,0));
        byte []byte2 = new byte[1];
        byteUtil.read(byte2);
        System.out.println(BitUtil.getServerProtocol(byte2[0]));

        byte []byte3 = new byte[1];
        byteUtil.read(byte3);
        System.out.println((int)byte3[0]);

        byte []byte4 = new byte[8];
        byteUtil.read(byte4);
        System.out.println(ByteUtil.byteToLong(byte4,0));


        return null;
    }

    public static String getData(String uri) throws IOException {
        URL url = new URL(uri);
        Proxy proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress(PROXY_HOST,PROXY_PORT));
        HttpURLConnection connection = (HttpURLConnection)url.openConnection(proxy);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(60 * 1000 );//设置连接超时时间 1分钟
        connection.setRequestMethod("GET");
        InputStream inputStream = connection.getInputStream();

        BufferedInputStream bis = new BufferedInputStream(inputStream);
        java.io.ByteArrayOutputStream bos=new java.io.ByteArrayOutputStream();

        byte[] buff=new byte[1024];
        int len=0;
        while((len=inputStream.read(buff))!=-1){
            bos.write(buff,0,len);
        }
        byte[] result=bos.toByteArray();
        String str=byte2HexStr(result);
        System.out.println(str);
        try {
            inputStream.close();
            bis.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static byte[] getOTAData(String uri) throws IOException {
        URL url = new URL(uri);
        Proxy proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress(PROXY_HOST,PROXY_PORT));
        HttpURLConnection connection = (HttpURLConnection)url.openConnection(proxy);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(60 * 1000 );//设置连接超时时间 1分钟
        connection.setRequestMethod("GET");
        InputStream inputStream = connection.getInputStream();

        BufferedInputStream bis = new BufferedInputStream(inputStream);
        java.io.ByteArrayOutputStream bos=new java.io.ByteArrayOutputStream();

        byte[] buff=new byte[1024];
        int len=0;
        while((len=inputStream.read(buff))!=-1){
            bos.write(buff,0,len);
        }
        byte[] result=bos.toByteArray();
        String str=byte2HexStr(result);
        System.out.println(str);
        try {
            inputStream.close();
            bis.close();
            bos.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    *  实现字节数组向十六进制的转换方法一
    */
    public static String byte2HexStr(byte[] b) {
        String hs="";
        String stmp="";
        for (int n=0;n< b.length;n++) {
            stmp=(Integer.toHexString(b[n] & 0XFF));
            if (stmp.length()==1) hs=hs+"0"+stmp;
            else hs=hs+stmp;
        }
        return hs.toUpperCase();
    }

}
