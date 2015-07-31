//package com.saicmotor.telematics.tsgp.tsip.autonavi;
//
//
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//import org.junit.Test;
//
//import java.io.*;
//import java.security.KeyManagementException;
//import java.security.NoSuchAlgorithmException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
///**
// * Created with IntelliJ IDEA.
// * User: iwskd
// * Date: 13-1-21
// * Time: 下午2:48
// * To change this template use File | Settings | File Templates.
// */
//public class AutoNaviClientTest {
//    @Test
//    public void testAuthenSync() throws IOException, NoSuchAlgorithmException, KeyManagementException {
//        /*支持HTTP POST 方式。
//        type  必选
//        “register”：注册订购信息
//        “delete”： 删除订购信息*/
//        /*[{“provider”:”bcd”,”userid”:”bcd”,”pwd”:”abc”,”sid”:”BC823456”,”registime”:”2014-05-03
//            12:04:00”,”starttime”:””,”duration”:”21900” ,”citycodes”:”110000”;}]*/
//        //VN350100000000001 -- 4
//        /*http://server:port/authen/sync?type=register*/
//        //http://211.151.75.11:80
//        //System.setProperty("javax.net.debug","all");
//        String url = "http://211.151.75.11:80/authen/sync?type=register";
//        String content = "{\"userinfo\":[{\"provider\":\"antestc\",\"userid\":\"antestc\",\"pwd\":\"antestc\",\"sid\":\"VN350100000002002\",\"registime\":\"2014-11-01"+
//                " 00:00:00\",\"duration\":\""+60*24*365+"\"}]}";
//
//        String jsonSrcCom = AutoNaviUtil.authenSync(url, content, "UTF-8");
//        System.out.println(jsonSrcCom);
//
//    }
//
//
//    @Test
//    public void testLogonService() throws IOException, NoSuchAlgorithmException, KeyManagementException {
//        /*支持HTTP GET 方式
//        VN350100000000001
//        http://server:port/trafficplat/logonservice?provider=tinfochina&userid=tinfochina&sid=abcdefgh&pwd=GPRSTMC*/
//        //http://testinfo.tinfochina.com
//        String url = "http://testinfo.tinfochina.com/trafficplat/logonservice?provider=antestc&userid=antestc&sid=VN350100000000001&pwd=antestc";
//        String jsonSrcCom = AutoNaviUtil.logonService(url);
//        System.out.println(jsonSrcCom);
//    }
//
//    @Test
//    public void testGetData() throws IOException {
//        /*支持HTTP GET 方式
//        VN350100000000001
//        http://server:port/trafficplat/trafficservice?validateid=123456789&citynum=110000&former_batchtime=0*/
//
//        //former_batchtime
//        /*历史批次时间(格式：长度为 6个字符的字符串，“HHMMSS” HH为小时（采用 24 小时制），MM为分钟，
//        SS 为秒（小时、分钟、秒均采用两位表示示例：8 时 24 分 2 秒表示为：“082402。
//        终端第一次请求的时候，时间为 0，返回全量数据。*/
//        //40514字节 40KB
//        String url = "http://testinfo.tinfochina.com/trafficplat/trafficservice?validateid=3190542550110404&citynum=110000&former_batchtime=0";
//        String jsonSrcCom = AutoNaviUtil.getData(url);
//        System.out.println(jsonSrcCom);
//    }
//
//    @Test
//    public void testFormat(){
//        String validateId = "222";
//        String cityNum = "123456";
//        String uri = "http://211.151.75.46:8080/trafficplat/batchinfoservice?validateid=%s&citynum=%s";
//        String  url = String.format(uri,validateId,cityNum);
//        System.out.println(url);
//    }
//
//    @Test
//    public void testJSON(){
//        JSONObject jsonObject = JSONObject.fromObject("{\"failinfo\":[]}");
//        JSONArray jsonArray = jsonObject.optJSONArray("failinfo");
//        System.out.println("2222");
//        System.out.println(jsonArray.size() == 0);
//    }
//
//    @Test
//    public void testTime(){
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String time = simpleDateFormat.format(new Date());
//        System.out.println(time);
//    }
//
//
//}
