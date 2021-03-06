package com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common;

import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTAEncoder;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.dispatcher.AVN_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.dispatcher.MP_OTARequest;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/28.
 */
public class HelperUtils {

    /**
     * 编码请求对象为字符串
     * @param platform
     * @param version
     * @param requestObject
     * @return
     */
    public static String changeObj2String(String platform, String version, Object requestObject) {
        return new String(AdapterHelper.adapterGetBytesData(platform, version, requestObject));
    }

    /**
     *
     * @param o
     * @param request
     * @return
     */
    public static MP_OTARequest enCode_MP_OTARequest(Object o,MP_OTARequest request){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OTAEncoder encoder = new OTAEncoder(outputStream);
        encoder.encode(o);
        byte[] resultBytes = outputStream.toByteArray();
        request.setApplicationData(resultBytes);
        request.getDispatcherBody().setApplicationDataLength(Long.valueOf(resultBytes.length));
        request.getDispatcherBody().setMessageID(2);
        //return success status
        request.getDispatcherBody().setResult(0);
        return request;
    }

    /**
     *
     * @param o
     * @param request
     * @return
     */
    public static AVN_OTARequest enCode_AVN_OTARequest(Object o,AVN_OTARequest request){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OTAEncoder encoder = new OTAEncoder(outputStream);
        encoder.encode(o);
        byte[] resultBytes = outputStream.toByteArray();
        request.setApplicationData(resultBytes);
        request.getDispatcherBody().setApplicationDataLength(Long.valueOf(resultBytes.length));
        request.getDispatcherBody().setMessageID(2);
        //return success status
        request.getDispatcherBody().setResult(0);
        return request;
    }

    public static Map<String, Integer> getOperationTypeMap() {
        Map<String,Integer> operationTypeMap = new HashMap<String,Integer>();
        operationTypeMap.put("verify",0);
        operationTypeMap.put("pin", 1);
        operationTypeMap.put("register", 2);
        operationTypeMap.put("retrieve", 3);
        operationTypeMap.put("addVehicle", 4);
        operationTypeMap.put("setMainPhoneNumber", 5);
        operationTypeMap.put("extend3", 6);
        operationTypeMap.put("extend4", 7);
        operationTypeMap.put("extend5", 8);
        operationTypeMap.put("extend6", 9);
        return operationTypeMap;
    }
}
