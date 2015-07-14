package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.avn.autonavi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.autonavi.CityNum;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.TSIPException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
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

//    static String PROXY_HOST =  (String) GuiceContext.getInstance().getConfig().getProperties().get("auto_navi").get("proxyHost");
//    static int PROXY_PORT = Integer.valueOf((String) GuiceContext.getInstance().getConfig().getProperties().get("auto_navi").get("proxyPort"));
//    static Long duration = Long.valueOf((String) GuiceContext.getInstance().getConfig().getProperties().get("auto_navi").get("duration"));
      static String PROXY_HOST =  SpringContext.getInstance().getProperty("auto_navi.proxyHost");
      static int PROXY_PORT =  Integer.parseInt(SpringContext.getInstance().getProperty("auto_navi.proxyPort"));
      static Long duration =  Long.parseLong(SpringContext.getInstance().getProperty("auto_navi.duration"));

    public static ANValidate gerANValidate(String vin) throws IOException {

        ANInfo anInfo = AutoNaviMongoService.findANInfo(vin);
        if(null == anInfo){
            //注册服务
            //是否控制车型访问
            if(!authSync(vin,String.valueOf(duration))){
                //服务注册失败
                throw new TSIPException("32091");
            }
            anInfo = new ANInfo();
            anInfo.setBatchCount(0L);
            anInfo.setDuration(duration);
            anInfo.setLoginCount(0L);
            anInfo.setRegTime(System.currentTimeMillis());
            anInfo.setStatus("1");
            anInfo.setTrafficCount(0L);
            anInfo.setTrafficCount2(0L);
            anInfo.setEventTypeCount(0L);
            anInfo.setLocationTypeCount(0L);
            anInfo.setInterCityTypeCount(0L);
            anInfo.setInterCityTypeCount2(0L);
            anInfo.setVin(vin);
            anInfo = AutoNaviMongoService.createANInfo(anInfo);
        }
        Long nowTime = System.currentTimeMillis();
        if(!"1".equals(anInfo.getStatus())){
            //TODO 服务已停止
            throw new TSIPException("32092");
        }
        if(nowTime <= anInfo.getRegTime() || (nowTime - anInfo.getRegTime())/1000/60 > anInfo.getDuration()){
            //TODO 服务已到期
            throw new TSIPException("32093");
        }
        ANValidate anValidate = AutoNaviMongoService.findANValidate(vin);
        if(null==anValidate || nowTime > anValidate.getExpireTime()){
            //TODO login
            byte[] bytes =  loginService(vin);
            byte[] byteVersion = subBytes(bytes,2,1);
            String version = BitUtil.getServerProtocol(byteVersion[0]);
            byte[] byteStatus = subBytes(bytes,3,1);
            int loginStatus = (int)byteStatus[0];
            if(loginStatus != 0){
                String errorCode = "26970";
                switch (loginStatus){
                    //高德返回错误码
                    //非法请求
                    case 1: errorCode = "32094";break;
                    //厂商ID过期
                    case 2: errorCode = "32095";break;
                    //用户ID过期
                    case 3: errorCode = "32096";break;
                    //厂商密码错误
                    case 4: errorCode = "32097";break;
                }
                throw new TSIPException(errorCode);
            }
            byte[] byteValidateId = subBytes(bytes,4,8);
            Long validateId = byteToLong(byteValidateId,0);

            anValidate = new ANValidate();
            Long nowTimeV = System.currentTimeMillis();
            anValidate.setVin(vin);
            anValidate.setCreateTime(nowTimeV);
            anValidate.setExpireTime(nowTimeV + 5L * 60L * 60L * 1000L);
            anValidate.setValidateId(validateId.toString());
            anValidate = AutoNaviMongoService.createANValidate(anValidate);
            AutoNaviMongoService.updateCount4ANInfo(vin,Constant4AutoNavi.ANInfo_LoginCount);
        }
        return anValidate;
    }

    public static byte[] getTrafficInfo(String validateId,String cityNum,String achieveCode) throws IOException {
        //http://211.151.75.46:8080/trafficplat/trafficservice?validateid=3190542550110404&citynum=110000&former_batchtime=0
        String uri = "http://testinfo.tinfochina.com/trafficplat/trafficservice?validateid=%s&citynum=%s&former_batchtime=%s";
        String url = String.format(uri,validateId,cityNum,achieveCode);
        return httpGET(url);
    }

    public static byte[] getEventType(String validateId,String cityNum,String achieveCode) throws IOException {
        //http://server:port/trafficplat/eventtypeservice?validateid=123456789&citynum=110000&eventtype=0
        String uri = "http://testinfo.tinfochina.com/trafficplat/eventtypeservice?validateid=%s&citynum=%s&eventtype=%s";
        String url = String.format(uri,validateId,cityNum,achieveCode);
        return httpGET(url);
    }

    public static byte[] getLocationType(String validateId,String cityNum,String achieveCode) throws IOException {
        //http://server:port/trafficplat/locationtypeservice?validateid=123456789&citynum=110000&loccodetype=0
        String uri = "http://testinfo.tinfochina.com/trafficplat/locationtypeservice?validateid=%s&citynum=%s&eventtype=%s";
        String url = String.format(uri,validateId,cityNum,achieveCode);
        return httpGET(url);
    }
    public static byte[] getInterCityType(String validateId,CityNum[] cityNums,String achieveCode) throws IOException {
        //http://server:port/trafficplat/intercityservice?validateid=1004537739573077&citynum=110000,120000,130000&former_batchtime=082402
        String cityNum = "";
        for(int i=0;i<cityNums.length && i<8 ;i++){
            cityNum = cityNum + cityNums[i].getCityNum();
            if(i != 7 && i != (cityNums.length - 1)) cityNum = cityNum + ",";
        }

        String uri = "http://testinfo.tinfochina.com/trafficplat/intercityservice?validateid=%s&citynum=%s&former_batchtime=%s";
        String url = String.format(uri,validateId,cityNum,achieveCode);
        return httpGET(url);
    }

    public static byte[] getBatchInfo(String validateId,String cityNum) throws IOException {
        //hhttp://server:port/trafficplat/batchinfoservice?validateid=123456789&citynum=110000
        String uri = "http://testinfo.tinfochina.com/trafficplat/batchinfoservice?validateid=%s&citynum=%s";
        String  url = String.format(uri,validateId,cityNum);
        return httpGET(url);
    }

    private static boolean authSync(String vin,String duration){
        String url = "http://211.151.75.11:80/authen/sync?type=register";
//        String provider = (String) GuiceContext.getInstance().getConfig().getProperties().get("auto_navi").get("provider");
//        String userid = (String) GuiceContext.getInstance().getConfig().getProperties().get("auto_navi").get("userid");
//        String pwd = (String) GuiceContext.getInstance().getConfig().getProperties().get("auto_navi").get("pwd");
        String provider = SpringContext.getInstance().getProperty("auto_navi.provider");
        String userid = SpringContext.getInstance().getProperty("auto_navi.userid");
        String pwd = SpringContext.getInstance().getProperty("auto_navi.pwd");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String time = simpleDateFormat.format(new Date());

        /*String _content = "{\"userinfo\":[{\"provider\":\"AntestE\",\"userid\":\"AntestE\",\"pwd\":\"AntestE\",\"sid\":\"VN350100000000001\",\"registime\":\"2014-11-01"+
                " 00:00:00\",\"duration\":\""+60*24*365+"\"}]}";*/
        String _content = "{\"userinfo\":[{\"provider\":\"%s\",\"userid\":\"%s\",\"pwd\":\"%s\",\"sid\":\"%s\",\"registime\":\"%s 00:00:00\",\"duration\":\"%s\"}]}";
        String content = String.format(_content,provider,userid,pwd,vin,time,duration);
        String json = httpPost(url, content);
//        JSONObject jsonObject = JSONObject.fromObject(json);
//        JSONArray jsonArray = jsonObject.optJSONArray("failinfo");
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("failinfo");
        return jsonArray.size() == 0;
    }

    private static byte[] loginService(String vin) throws IOException {
        String uri = "http://testinfo.tinfochina.com/trafficplat/logonservice?provider=AntestE&userid=AntestE&sid=%s&pwd=AntestE";
        String url = String.format(uri,vin);
        return httpGET(url);
    }

    private static String httpPost(String urls,String content) {
        StringBuilder strResponse = new StringBuilder();
        try {
            URL url = new URL(urls);
            Proxy proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress(PROXY_HOST,PROXY_PORT));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection(proxy);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(60 * 1000 );//设置连接超时时间 1分钟
            connection.setRequestMethod("POST");
            connection.addRequestProperty("Content-Encoding", "gzip");
            connection.setRequestProperty("Content-Type", "application/json");

            // 读取json文件并压缩
            ByteArrayOutputStream outJson = new ByteArrayOutputStream();
            GZIPOutputStream gout = new GZIPOutputStream(outJson);
            gout.write(content.getBytes("UTF-8"));
            gout.close();
            connection.addRequestProperty("Content-Length", outJson.size() + "");
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
            //System.out.println("HTTP STATUS:" + connection.getResponseCode());
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
            strResponse.append(outReturn.toString("UTF-8"));

        } catch (Exception e) {
            //TODO Throw exception
            e.printStackTrace();
        }
        return strResponse.toString();
    }

    public static byte[] httpGET(String uri) throws IOException {
        URL url = new URL(uri);
        Proxy proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress(PROXY_HOST,PROXY_PORT));
        HttpURLConnection connection = (HttpURLConnection)url.openConnection(proxy);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(60 * 1000 );//设置连接超时时间 1分钟
        connection.setRequestMethod("GET");
        InputStream inputStream = connection.getInputStream();

        BufferedInputStream bis = new BufferedInputStream(inputStream);
        ByteArrayOutputStream bos=new ByteArrayOutputStream();

        byte[] buff=new byte[1024];
        int len=0;
        while((len=inputStream.read(buff))!=-1){
            bos.write(buff,0,len);
        }
        byte[] result=bos.toByteArray();
        inputStream.close();
        bis.close();
        bos.close();
        return result;
    }

    /**
     * 工具添加
     * @param bytes
     * @return
     */
    private static int bytesToInt16(byte[] bytes,int offset){
        int value;
        value = (int)(((bytes[offset + 0] << 8) & 0xff00)
                | ((bytes[offset + 1] & 0xff)));
        return value;
    }

    private static byte[] int16ToBytes(int i){
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (i >> 8);
        bytes[1] = (byte)i;
        return bytes;
    }

    private static long byteToLong(byte[] array,int offset ){
        return ((((long) array[offset + 0] & 0xff) << 56)
                | (((long) array[offset + 1] & 0xff) << 48)
                | (((long) array[offset + 2] & 0xff) << 40)
                | (((long) array[offset + 3] & 0xff) << 32)
                | (((long) array[offset + 4] & 0xff) << 24)
                | (((long) array[offset + 5] & 0xff) << 16)
                | (((long) array[offset + 6] & 0xff) << 8)
                | (((long) array[offset + 7] & 0xff) << 0));
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i=begin; i<begin+count; i++) bs[i-begin] = src[i];
        return bs;
    }
}
