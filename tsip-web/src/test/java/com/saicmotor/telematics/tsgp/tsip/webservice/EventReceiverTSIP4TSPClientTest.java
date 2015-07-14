package com.saicmotor.telematics.tsgp.tsip.webservice;


import com.saicmotor.mce550.tsgp.tsip.msg.v1.*;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: iwskd
 * Date: 13-1-21
 * Time: 下午2:48
 * To change this template use File | Settings | File Templates.
 */
public class EventReceiverTSIP4TSPClientTest {
    @Test
    public void test() throws MalformedURLException {
        URL location = new URL("http://10.90.1.232:9080/TSIP.Web/services/EventReceiverTSIP4TSP?WSDL");
        EventReceiverTSIP4TSPService ss = new EventReceiverTSIP4TSPService(location);
        EventReceiverTSIP4TSP port = ss.getEventReceiverTSIP4TSPPort();

        // Build the request
        SendDataRequest4TSP parameters = new SendDataRequest4TSP();
        MessageHeader messageHeader =new MessageHeader();
        messageHeader.setProtocalVersion(16);
        messageHeader.setApplicationID("802");
        messageHeader.setMessageID(1);
        parameters.setMessageHeader(messageHeader);

        List<AttrModel> appData = parameters.getApplicationData();
        //add phone number
        AttrModel attrPhone = new AttrModel();
        attrPhone.setName("phone");
        List<String> listValuePhone = attrPhone.getValue();
        listValuePhone.add("18016264360");

        //add sms content
        AttrModel attrSMS = new AttrModel();
        attrSMS.setName("sms");
        List<String> listValueSMS = attrSMS.getValue();
        listValueSMS.add("39303101000001564E303030303030303030353530303033737E3E28FDBA523FA80F2264EE2A6AF0DDFF9FBFFBD7DB0FE80A1E20ACD78FB4B4D35C93B5B847359319D57059E71000");

        appData.add(attrSMS);
        appData.add(attrPhone);

        // Send request
        SendDataResponse4TSP response = port.sendDataTSIP(parameters);
        System.out.println("Result:"+response.getMessageHeader().getResult());


    }

    @Test
    public void test805() throws Exception {
        URL location = new URL("http://10.91.225.97:9080/TSIP.Web/services/EventReceiverTSIP4TSP?WSDL");
        EventReceiverTSIP4TSPService ss = new EventReceiverTSIP4TSPService(location);
        EventReceiverTSIP4TSP port = ss.getEventReceiverTSIP4TSPPort();

        // Build the request
        SendDataRequest4TSP parameters = new SendDataRequest4TSP();
        MessageHeader messageHeader =new MessageHeader();
        messageHeader.setProtocalVersion(16);
        messageHeader.setApplicationID("805");
        messageHeader.setMessageID(1);
        parameters.setMessageHeader(messageHeader);

        List<AttrModel> appData = parameters.getApplicationData();
        //add phone number
        String token = "efba112a92fa4659b47218642e6b099b";
        AttrModel attrToken = createModel("token",token);
        appData.add(attrToken);
        // Send request
        SendDataResponse4TSP response = port.sendDataTSIP(parameters);
        System.out.println("Result:"+response.getMessageHeader().getResult());
        System.out.println("Result:"+response.getMessageHeader().getErrorMessage());
    }


    @Test
    public void test811() throws Exception {
        URL location = new URL("http://localhost:8080/TSIP.Web/services/EventReceiverTSIP4TSP?WSDL");
        EventReceiverTSIP4TSPService ss = new EventReceiverTSIP4TSPService(location);
        EventReceiverTSIP4TSP port = ss.getEventReceiverTSIP4TSPPort();

        // Build the request
        SendDataRequest4TSP parameters = new SendDataRequest4TSP();
        MessageHeader messageHeader =new MessageHeader();
        messageHeader.setProtocalVersion(16);
        messageHeader.setApplicationID("811");
        messageHeader.setMessageID(1);
        parameters.setMessageHeader(messageHeader);

        List<AttrModel> appData = parameters.getApplicationData();
        //add phone number
        String timestamp = String.valueOf(System.currentTimeMillis());
        String nonce = String.valueOf((int) (Math.random() * 1000000));
        String token = "1f9e3c36201a4f269817094a4fa6fccd";
        String signature =  bytesToHexString(digest((token + nonce + timestamp).getBytes()), true);
        String from = "1";

        AttrModel signatureModel = createModel("signature",signature);
        AttrModel nonceModel = createModel("nonce",nonce);
        AttrModel timestampModel = createModel("timestamp",timestamp);
        AttrModel fromModel = createModel("from",from);
        AttrModel attrPhone = createModel("vin","LSJW26H6XDS012584");

        appData.add(signatureModel);
        appData.add(nonceModel);
        appData.add(timestampModel);
        appData.add(fromModel);
        appData.add(attrPhone);
        // Send request
        SendDataResponse4TSP response = port.sendDataTSIP(parameters);
        System.out.println("Result:"+response.getMessageHeader().getResult());
        System.out.println("Result:" + response.getApplicationData().get(0).getValue().get(0));
        System.out.println("Result:"+response.getApplicationData().get(1).getValue().get(0));
        System.out.println("Result:"+response.getApplicationData().get(2).getValue().get(0));

    }

    public static byte[] digest(byte[] in)throws Exception{
        String _algorithm = "MD5";
        MessageDigest digest = MessageDigest
                .getInstance(_algorithm);
        return digest.digest(in);

    }

    public static final String bytesToHexString(byte[] bArray,boolean isUpperCase) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(isUpperCase?sTemp.toUpperCase():sTemp.toLowerCase());
        }
        return sb.toString();
    }

    private static AttrModel createModel(String name,String value){
        AttrModel attrModel = new AttrModel();
        attrModel.setName(name);
        List<String> list = attrModel.getValue();
        list.add(value);
        return attrModel;
    }
}
