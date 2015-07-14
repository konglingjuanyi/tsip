package com.saicmotor.telematics.tsgp.tsip.webservice.handler;

import com.saicmotor.framework.context.guice.GuiceContext;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.AttrModel;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.MessageHeader;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.SendDataRequest4TSP;
import com.saicmotor.mce550.tsgp.tsip.msg.v1.SendDataResponse4TSP;
import com.saicmotor.telematics.tsgp.otaadapter.asn.utils.BinaryAndHexUtil;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.BasicPosition;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.dispatcher.Navi_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.poi.v2_0.POI;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.poi.v2_0.POISendToCar4BaiduReq;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.poi.v2_0.PoiSend;
import com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.poi.v2_0.PoiSendSource;
import com.saicmotor.telematics.tsgp.otaadapter.vp.v1_1.security.VP_OTASignature;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.TSIPException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.LogHelper;
import com.saicmotor.telematics.tsgp.tsip.webservice.util.InvokeClient4Navi;
import com.saicmotor.telematics.tsgp.tsip.webservice.util.RegexUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
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
public class InkaNetPOIHandler extends AbstractTSPHandler{
    private static final Logger LOGGER = LoggerFactory.getLogger(InkaNetPOIHandler.class);

    public SendDataResponse4TSP process(SendDataRequest4TSP parameters) {
        LogHelper.info(LOGGER, "InkaNetPOI", "TSIP", "810", 1, null, null, "", null, null, null);
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

            String poiList = list.get(4).getValue().get(0);
            String simNo =   list.get(5).getValue().get(0);
            if(!checkTimestamp(timestamp)){
                result = 28104;
                sendDataResponse.getMessageHeader().setErrorMessage("请求已失效");
                sendDataResponse.getMessageHeader().setResult(result);
                return sendDataResponse;
            }
            if(!checkAuthority(signature,nonce,timestamp,platform)){
                result = 28103;
                sendDataResponse.getMessageHeader().setErrorMessage("签名无效");
                sendDataResponse.getMessageHeader().setResult(result);
                return sendDataResponse;
            }
            PoiSendSource poiSendSource = new PoiSendSource();
            if("1".equals(platform)){
                poiSendSource.setValue(PoiSendSource.EnumType.baidu);
            }else if("2".equals(platform)){
                poiSendSource.setValue(PoiSendSource.EnumType.gaode);
            }else if("3".equals(platform)){
                poiSendSource.setValue(PoiSendSource.EnumType.weixin);
            }else{
                poiSendSource.setValue(PoiSendSource.EnumType.def);
            }
            List<JSONObject> jsonObjectList = JSONArray.fromObject(poiList);
            POISendToCar4BaiduReq poiSendToCarReq=null;
            try{
                poiSendToCarReq = createReq(simNo,jsonObjectList,poiSendSource);
            }catch (Exception e){
                LogHelper.error(LOGGER, "810", 1, null, null, "28101", e, null);
                result = 28101;
                sendDataResponse.getMessageHeader().setErrorMessage("数据格式错误");
                sendDataResponse.getMessageHeader().setResult(result);
                return sendDataResponse;
            }

            InvokeClient4Navi invokeClient = new InvokeClient4Navi();
            Navi_OTARequest otaRequest = invokeClient.invoke("6D7", poiSendToCarReq);
            if(otaRequest.getDispatcherBody().getResult()!=0){
                result = otaRequest.getDispatcherBody().getResult();
                if(result==28102){
                    sendDataResponse.getMessageHeader().setErrorMessage("InkaNet系统中无此电话号码");
                }else if(result == 28105){
                    result = 28105;
                    sendDataResponse.getMessageHeader().setErrorMessage("账户未绑定车辆");
                }else{
                    result = 28100;
                    sendDataResponse.getMessageHeader().setErrorMessage("InkaNet系统内部异常");
                }
            }
        }catch (Exception e){
            LogHelper.error(LOGGER, "810", 1, null, null, "28100", e, null);
            result = 28100;
            sendDataResponse.getMessageHeader().setErrorMessage("InkaNet系统内部异常");
            LogHelper.error(LOGGER, "InkaNetPOI", 2, "", "", "28100", e, "");
        }
        sendDataResponse.getMessageHeader().setResult(result);
        return sendDataResponse;
    }

    private POISendToCar4BaiduReq createReq(String simNo,List<JSONObject> list,PoiSendSource poiSendSource) {
        try {
            POISendToCar4BaiduReq poiSendToCarReq = new POISendToCar4BaiduReq();
            poiSendToCarReq.setSimNo(simNo);

            List<PoiSend> poiList = new ArrayList<PoiSend>();
            for(JSONObject jsonObject:list){
                POI poi = new POI();
                poi.setAddressName(jsonObject.getString("addressName").getBytes("utf-8"));
                poi.setCityCode(jsonObject.getString("cityCode"));

                poi.setAddress(jsonObject.getJSONObject("additionalInformation").getString("address").getBytes("utf-8"));
                if(jsonObject.getJSONObject("additionalInformation").has("phone")){
                    poi.setPhone(checkPhone(jsonObject.getJSONObject("additionalInformation").getString("phone")));
                }
                if(jsonObject.getJSONObject("additionalInformation").has("postCode")){
                    String postCode = jsonObject.getJSONObject("additionalInformation").getString("postCode");
                    if(RegexUtil.check(RegexUtil.POST_CODE, postCode)){
                        poi.setPostCode(postCode);
                    }
                }
                BasicPosition basicPosition=new BasicPosition();
                basicPosition.setLatitude((int)(jsonObject.getJSONObject("location").getDouble("latitude")*1000000D));
                basicPosition.setLongitude((int)(jsonObject.getJSONObject("location").getDouble("longitude")*1000000D));
                poi.setBasicPosition(basicPosition);
                PoiSend poiSend = new PoiSend();
                poiSend.setPoi(poi);
                poiSend.setPoiSendSource(poiSendSource);
                poiList.add(poiSend);
            }
            poiSendToCarReq.setPoiSendList(poiList);
            return poiSendToCarReq;
        } catch (Exception e) {
            throw new TSIPException("生成poi下发请求对象出错", e);
        }
    }


    private String checkPhone(String str) {
        if (str == null || "".equals(str)) {
            return null;
        }

        StringBuilder phoneStr = new StringBuilder("");
        String returnStr = null;
        String[] phones = str.split(",");

        for(String phone:phones){
            if(RegexUtil.check(RegexUtil.MOBILE,phone)){
                phoneStr.append(phone).append(",");
            }
        }

        if(phoneStr.length() > 0){
            returnStr = phoneStr.substring(0, phoneStr.length() - 1);
        } else {
            returnStr = phoneStr.toString();
        }

        return returnStr;
    }

    private boolean checkTimestamp(String timestamp){
        Date date =new Date();
        return Math.abs(date.getTime()-Long.parseLong(timestamp)) <= 5*60*1000L;
    }

    private boolean checkAuthority(String signature,String nonce,String timestamp,String platform){
        String token = (String)GuiceContext.getInstance().getConfig().getProperties().get("token").get("platform"+platform);
        VP_OTASignature vp_OTASignature = new VP_OTASignature();
        String md5 = BinaryAndHexUtil.bytesToHexString(vp_OTASignature.digest((token + nonce + timestamp).getBytes()),true);
        return signature.equals(md5);
    }
}

