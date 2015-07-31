//package com.saicmotor.telematics.tsgp.tsip.autonavi;
//
//
//import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTAEncoder;
//import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.autonavi.AchieveTrafficInfoResp;
//import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.autonavi.TrafficInfo;
//import org.junit.Test;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.security.KeyManagementException;
//import java.security.NoSuchAlgorithmException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * Created with IntelliJ IDEA.
// * User: iwskd
// * Date: 13-1-21
// * Time: 下午2:48
// * To change this template use File | Settings | File Templates.
// */
//public class AutoNaviClientOTATest {
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
//        String url = "http://211.151.75.11:80/authen/sync?type=register";
//        String content = "{\"userinfo\":[{\"provider\":\"AntestE\",\"userid\":\"AntestE\",\"pwd\":\"AntestE\",\"sid\":\"VN350100000000001\",\"registime\":\"2014-11-01"+
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
//        String url = "http://testinfo.tinfochina.com/trafficplat/logonservice?provider=AntestE&userid=AntestE&sid=VN350100000000001&pwd=AntestE";
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
//        String url = "http://testinfo.tinfochina.com/trafficplat/trafficservice?validateid=2691328829434014&citynum=110000&former_batchtime=0";
//        Long date1 = System.currentTimeMillis();
//
//        byte[] jsonSrcCom = AutoNaviUtil.getOTAData(url);
//        Long date2 = System.currentTimeMillis();
//        System.out.println(date2-date1);
//        int len = jsonSrcCom.length;
//        System.out.println(len);
//        int maxLen = 1024 * 16 - 1;
////        byte[] littlt = subBytes(jsonSrcCom,0,1024 * 16 - 1);
//        AchieveTrafficInfoResp achieveTrafficInfoResp = new AchieveTrafficInfoResp();
//        List<TrafficInfo> trafficInfoList = new ArrayList<TrafficInfo>();
//        int listSize = len/maxLen + (len%maxLen == 0 ? 0 : 1);
//        for(int i = 0;i<listSize;i++){
//            TrafficInfo trafficInfo = new TrafficInfo();
//            if((i+ 1)*maxLen>=len){
//                trafficInfo.setInfoPart(subBytes(jsonSrcCom,i * maxLen,len - i * maxLen));
//            }else{
//                trafficInfo.setInfoPart(subBytes(jsonSrcCom,i * maxLen,maxLen));
//            }
//            trafficInfoList.add(trafficInfo);
//        }
//        achieveTrafficInfoResp.setTrafficInfoList(trafficInfoList);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        OTAEncoder encoder = new OTAEncoder(outputStream);
//        encoder.encode(achieveTrafficInfoResp);
//        byte[] bytes = outputStream.toByteArray();
//        Long date3 = System.currentTimeMillis();
//        System.out.println(date3-date2);
//        System.out.println(jsonSrcCom);
//    }
//
//    public static byte[] subBytes(byte[] src, int begin, int count) {
//        byte[] bs = new byte[count];
//        for (int i=begin; i<begin+count; i++) bs[i-begin] = src[i];
//        return bs;
//    }
//
//}
