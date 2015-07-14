package com.saicmotor.telematics.tsgp.tsip.webservice.handler;

import com.saicmotor.framework.context.guice.GuiceContext;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.AttrModel;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.MessageHeader;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.SendDataRequest4TSP;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.SendDataResponse4TSP;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.asn.utils.BinaryAndHexUtil;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.BasicPosition;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.dispatcher.Navi_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.poi.v2_0.POI;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.poi.v2_0.POISendToCar4BaiduReq;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.poi.v2_0.PoiSend;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.poi.v2_0.PoiSendSource;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.dispatcher.TCMP_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.open.VehicleMesPart4OpenResp;
import com.saicmotor.telematics.tsgp.otaadapter.vp.v1_1.security.VP_OTASignature;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.TSIPException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.LogHelper;
import com.saicmotor.telematics.tsgp.tsip.webservice.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: szksr
 * Date: 13-12-26
 * Time: 下午12:23
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class VehicleMesPart4OpenHandler extends AbstractTSPHandler{
    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleMesPart4OpenHandler.class);

    public SendDataResponse4TSP process(SendDataRequest4TSP parameters) {
        LogHelper.info(LOGGER, "VehicleMesPart4OpenHandler", "TSIP", "811", 1, null, null, "", null, null, null);
        MessageHeader messageHeader = parameters.getMessageHeader();
        //创建响应
        SendDataResponse4TSP sendDataResponse = new SendDataResponse4TSP();
        messageHeader.setMessageID(2);
        sendDataResponse.setMessageHeader(messageHeader);
        Integer result = 0;
        try{
            List<AttrModel> list = parameters.getApplicationData();
            String signature =  list.get(0).getValue().get(0);
            String nonce =  list.get(1).getValue().get(0);
            String timestamp = list.get(2).getValue().get(0);
            String platform = list.get(3).getValue().get(0);

            String vin = list.get(4).getValue().get(0);
            //timestamp校验 28111
            //ak校验 28112
            //check VIN
            //req组装
            //TODO Resp组装

            if(!checkTimestamp(timestamp)){
                result = 28111;
                sendDataResponse.getMessageHeader().setErrorMessage("请求已超时");
                sendDataResponse.getMessageHeader().setResult(result);
                LogHelper.error(LOGGER, "811", 2, "", "", result + "请求已超时", null, "");
                return sendDataResponse;
            }
            if(!checkAuthority(signature,nonce,timestamp,platform)){
                result = 28112;
                sendDataResponse.getMessageHeader().setErrorMessage("签名无效");
                sendDataResponse.getMessageHeader().setResult(result);
                LogHelper.error(LOGGER, "811", 2, "", "", result + "签名无效", null, "");
                return sendDataResponse;
            }
            if(!checkVIN(vin)){
                result = 28113;
                sendDataResponse.getMessageHeader().setErrorMessage("VIN码格式错误");
                sendDataResponse.getMessageHeader().setResult(result);
                LogHelper.error(LOGGER, "811", 2, "", "", result + "VIN码格式错误", null, "");
                return sendDataResponse;
            }

            InvokeClient4TCMP invokeClient4TCMP = new InvokeClient4TCMP();
            TCMP_OTARequest tcmp_otaRequest = RequestUtil4TCMP.createRequest("766");
            tcmp_otaRequest.getDispatcherBody().setVin(vin);
            TCMP_OTARequest otaRequest = invokeClient4TCMP.invoke(tcmp_otaRequest);
            if(otaRequest.getDispatcherBody().getResult()!=0){
                result = otaRequest.getDispatcherBody().getResult();
                if(result==29061){
                    result = 28114;
                    sendDataResponse.getMessageHeader().setErrorMessage("系统中无此车辆");
                    LogHelper.error(LOGGER, "811", 2, "", "", result + "系统中无此车辆", null, "");
                }else{
                    result = 28110;
                    sendDataResponse.getMessageHeader().setErrorMessage("系统内部异常");
                    LogHelper.error(LOGGER, "811", 2, "", "", result + "系统内部异常", null, "");
                }
            }else {
                byte[] appBytes = otaRequest.getApplicationData();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(appBytes);
                OTADecoder decoder = new OTADecoder(inputStream);
                VehicleMesPart4OpenResp vehicleMesPart4OpenResp = (VehicleMesPart4OpenResp) decoder.decode(VehicleMesPart4OpenResp.class);
                String _engerNo = vehicleMesPart4OpenResp.getEngerNo() == null ? "" : vehicleMesPart4OpenResp.getEngerNo();
                String _brand = vehicleMesPart4OpenResp.getBrand() == null ? "" : new String(vehicleMesPart4OpenResp.getBrand(),"UTF-8");
                String _paint = vehicleMesPart4OpenResp.getPaint() == null ? "" : new String(vehicleMesPart4OpenResp.getPaint(),"UTF-8");
                List<AttrModel> attrModels = sendDataResponse.getApplicationData();
                AttrModel engerNo = new AttrModel();
                engerNo.setName("engerNo");
                List<String> list1 = engerNo.getValue();
                list1.add(_engerNo);

                AttrModel brand = new AttrModel();
                brand.setName("brand");
                List<String> list2 = brand.getValue();
                list2.add(_brand);

                AttrModel paint = new AttrModel();
                paint.setName("paint");
                List<String> list3 = paint.getValue();
                list3.add(_paint);

                attrModels.add(engerNo);
                attrModels.add(brand);
                attrModels.add(paint);
            }

        }catch (Exception e){
            result = 28110;
            sendDataResponse.getMessageHeader().setErrorMessage("系统内部异常");
            LogHelper.error(LOGGER, "811", 2, "", "", "28110", e, "");
        }
        sendDataResponse.getMessageHeader().setResult(result);
        return sendDataResponse;
    }

    private boolean checkVIN(String vin){
        return vin != null ? vin.matches("[A-HJ-NPR-Z0-9]{17}") : false;
    }

    private boolean checkTimestamp(String timestamp){
        Date date =new Date();
        return Math.abs(date.getTime()-Long.parseLong(timestamp)) <= 5*60*1000L;
    }

    private boolean checkAuthority(String signature,String nonce,String timestamp,String platform){
        String token = (String)GuiceContext.getInstance().getConfig().getProperties().get("openAK").get("platform"+platform);
        TSIP_OTASignature tsip_OTASignature = new TSIP_OTASignature();
        String md5 = BinaryAndHexUtil.bytesToHexString(tsip_OTASignature.digest((token + nonce + timestamp).getBytes()),true);
        return signature.equals(md5);
    }
}

