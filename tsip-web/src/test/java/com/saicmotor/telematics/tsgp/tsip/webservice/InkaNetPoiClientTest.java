package com.saicmotor.telematics.tsgp.tsip.webservice;

import com.saicmotor.framework.context.guice.GuiceContext;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: szksr
 * Date: 13-9-11
 * Time: 上午10:21
 * To change this template use File | Settings | File Templates.
 */
public class InkaNetPoiClientTest {
    public static void main(String[] args)throws Exception{
        System.out.println(invokePoiDirect("18739747987","21.234","112.345","上海嘉定XX弄XX路XX号","锅德火锅"));
    }


    public static String invokePoiDirect(String phone,String lat,String lng,String address,String addressName) throws Exception{

        HttpURLConnection conn = null;
        ByteArrayOutputStream temp_baos = null;
        try{
//            String tapUrl = (String) GuiceContext.getInstance().getConfig().getProperties().get("TSIP").get("tsp");
            String tapUrl = "http://10.90.1.232/TAP.Web/services/EventReceiverTSP";
            URL url1 = new URL(tapUrl);
            InputStream is = null;
            conn = (HttpURLConnection)url1.openConnection();
            conn.setRequestProperty("Content-Type","text/xml;charset=utf-8");
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            address = UnicodeUtil.toEncodedUnicode(address, true);
            addressName = UnicodeUtil.toEncodedUnicode(addressName,true);
            String json = String.format("[{" +
                    "\"addressName\":\"%s\"," +
                    "additionalInformation:{" +
                    "\"address\":\"%s\"" +
                    "},\n" +
                    "location:{" +
                    "\"latitude\":%s," +
                    "\"longitude\":%s" +
                    "},\n" +
                    "cityCode:\"011111\"" +
                    "}]",addressName,address,lat,lng);

            StringBuffer sb = new StringBuffer();
            sb.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
            sb.append("	<soap:Header/>");
            sb.append("		<soap:Body>");
            sb.append("			<sendDataRequest xmlns=\"http://saicmotor.com/mce550/tsgp/tsp/msg/v1\">\n" +
                    "         <messageHeader xmlns=\"\">\n" +
                    "            <protocalVersion>16</protocalVersion>\n" +
                    "            <applicationID>810</applicationID>\n" +
                    "            <messageID>1</messageID>\n" +
                    "            <result>0</result>\n" +
                    "<errorMessage>1</errorMessage>\n"+
                    "         </messageHeader>\n" +
                    "         <!--Zero or more repetitions:-->\n" +
                    "         <applicationData xmlns=\"\">\n" +
                    "        <name>signature</name>\n" +
                    "        <value>%s</value>\n" +
                    "      </applicationData>\n" +
                    "      <applicationData xmlns=\"\">\n" +
                    "        <name>nonce</name>\n" +
                    "        <value>%s</value>\n" +
                    "      </applicationData>\n" +
                    "      <applicationData xmlns=\"\">\n" +
                    "        <name>timestamp</name>\n" +
                    "        <value>%s</value>\n" +
                    "      </applicationData>\n" +
                    "      <applicationData xmlns=\"\">\n" +
                    "        <name>from</name>\n" +
                    "        <value>3</value>\n" +
                    "      </applicationData>\n" +
                    "       <applicationData xmlns=\"\">\n" +
                    "            <name>poilist</name>\n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    "            <value>%s</value>\n" +
                    "         </applicationData>\n" +
                    "       <applicationData xmlns=\"\">\n" +
                    "            <name>simno</name>\n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    "            <value>%s</value>\n" +
                    "         </applicationData>\n" +
                    "      </sendDataRequest>\n");
            sb.append("		</soap:Body>");
            sb.append("</soap:Envelope>");

            OutputStream os = conn.getOutputStream();
            String token = "a32e5d61ed3f7859e19ae32d926c43ad";
            Date date = new Date();
            String timestamp = String.valueOf(date.getTime());
            String nonce = "121212";
            MessageDigest digest = MessageDigest.getInstance("MD5");
            String signature =  UnicodeUtil.bytesToHexString(digest.digest((token + nonce + timestamp).getBytes()), true);

            os.write(String.format(sb.toString(),signature,nonce,timestamp,json,phone).getBytes());
            os.close();

            is = conn.getInputStream();
            if(conn.getResponseCode() != 200){
                throw new Exception("调用下发POI服务出错，http返回码: " + conn.getResponseMessage());
            }

            temp_baos = new ByteArrayOutputStream();
            try {
                int c = 0;
                while ((c = is.read()) != -1 ) {
                    if (c != '\n') {
                        temp_baos.write(c);
                    }
                }
            } catch(Exception e) {
//                LogHelper.error("inkanet2POI",1,"","","读流异常",e,"");
                e.printStackTrace();
            }
            is.close();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream xmlinput = new ByteArrayInputStream(temp_baos.toByteArray());
            org.w3c.dom.Document doc = builder.parse(xmlinput);
            NodeList nodeList = doc.getElementsByTagName("messageHeader");

            return parseNoteList(nodeList);
        }catch (Exception e1){
//            LogHelper.error("inkanet2POI", 1, "", "", "下发POI异常", e1, "");
            e1.printStackTrace();
            return "-1";
        }finally {
            try {
                if (temp_baos != null) {
                    temp_baos.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }


    }
    public static String parseNoteList(NodeList nodeList){
        org.w3c.dom.Element element = (org.w3c.dom.Element)nodeList.item(0);
//        String protocalVersion = element.getElementsByTagName("protocalVersion").item(0).getFirstChild().getNodeValue();
//        String applicationID = element.getElementsByTagName("applicationID").item(0).getFirstChild().getNodeValue();
//        String messageID = element.getElementsByTagName("messageID").item(0).getFirstChild().getNodeValue();
        String result = element.getElementsByTagName("result").item(0).getFirstChild().getNodeValue();
//        String errorMessage = "下发成功！";
//        if(!result.equals("0") && element.getElementsByTagName("errorMessage").item(0) != null){
//            errorMessage = element.getElementsByTagName("errorMessage").item(0).getFirstChild().getNodeValue();
//        }else if(!result.equals("0") && element.getElementsByTagName("errorMessage").item(0) == null){
//            errorMessage = "请重试！";
//        }
        return result;
    }

}

