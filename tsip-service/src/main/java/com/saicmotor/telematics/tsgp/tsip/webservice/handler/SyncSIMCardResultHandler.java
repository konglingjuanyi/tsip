package com.saicmotor.telematics.tsgp.tsip.webservice.handler;

import cn.chinaunicom.chinaunicomsimcard4tsipservices.simcardtype.SIMCardResultRequestType;
import cn.chinaunicom.chinaunicomsimcard4tsipservices.simcardtype.SIMCardResultResponseType;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.isp.entity.cardstatus.SyncSIMCardResultReq;
import com.saicmotor.telematics.tsgp.otaadapter.isp.entity.cardstatus.SyncSIMCardResultResp;
import com.saicmotor.telematics.tsgp.otaadapter.isp.entity.common.Timestamp;
import com.saicmotor.telematics.tsgp.otaadapter.isp.entity.dispatcher.ISP_OTARequest;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.LogHelper;
import com.saicmotor.telematics.tsgp.tsip.webservice.util.InvokeClient4ISP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * Created with IntelliJ IDEA.
 * User: jwdsq
 * Date: 14-3-19
 * Time: 上午9:12
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class SyncSIMCardResultHandler {
    private static final SimpleDateFormat simpleDateFormatSS = new SimpleDateFormat("yyyyMMddhhmmss");

    private static final Logger logger = LoggerFactory.getLogger(SyncSIMCardResultHandler.class);

    public SIMCardResultResponseType process(SIMCardResultRequestType request) {
        Integer result = 0;
        SyncSIMCardResultReq syncSIMCardResultReq = null;
        SIMCardResultResponseType cardResultResponse = new SIMCardResultResponseType();
        cardResultResponse.setRequestor("SAIC");
        //F2	请求类型
        cardResultResponse.setRequestType(request.getRequestType());
        // V20	请求ID
        cardResultResponse.setRequestId(request.getRequestId());

        try {
            syncSIMCardResultReq = createReq(request);
            InvokeClient4ISP invokeClient = new InvokeClient4ISP();
            ISP_OTARequest otaRequest = invokeClient.invoke("6I1", syncSIMCardResultReq);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(otaRequest.getApplicationData());
            OTADecoder decoder = new OTADecoder(inputStream);
            SyncSIMCardResultResp syncSIMCardResultResp = (SyncSIMCardResultResp) decoder.decode(SyncSIMCardResultResp.class);

            cardResultResponse.setRspCode(syncSIMCardResultResp.getRspCode());
            cardResultResponse.setRspMsg(new String(syncSIMCardResultResp.getRspMsg(),"utf-8"));
        } catch (Exception e) {
            cardResultResponse.setRspCode("9999");
            cardResultResponse.setRspMsg("异常失败");
            LogHelper.error(logger, "6I1", 2, "", "", "70002", e, "");
        }
        return cardResultResponse;
    }


    private SyncSIMCardResultReq createReq(SIMCardResultRequestType request) throws ParseException, UnsupportedEncodingException {
        SyncSIMCardResultReq syncSIMCardResultReq = new SyncSIMCardResultReq();
        //19位转20位，最后一位补0
        syncSIMCardResultReq.setIccid(request.getIccid() + '0');
        syncSIMCardResultReq.setMsisdn(request.getMsisdn());
        syncSIMCardResultReq.setCardStatus(request.getCardStatus());
        syncSIMCardResultReq.setServiceStatus(request.getServiceStatus());
        if (request.getCardStatusChgDate() != null) {
            Timestamp timestamp = new Timestamp();
            timestamp.setSeconds(simpleDateFormatSS.parse(request.getCardStatusChgDate()).getTime() / 1000l);
            syncSIMCardResultReq.setCardStatusChgDate(timestamp);
        }
        if (request.getServiceStatusChgDate() != null) {
            Timestamp timestamp = new Timestamp();
            timestamp.setSeconds(simpleDateFormatSS.parse(request.getServiceStatusChgDate()).getTime() / 1000l);
            syncSIMCardResultReq.setServiceStatusChgDate(timestamp);
        }
        syncSIMCardResultReq.setRequestId(request.getRequestId());
        syncSIMCardResultReq.setOldRequestId(request.getOldRequestId());
        syncSIMCardResultReq.setRequestor(request.getRequestor());
        syncSIMCardResultReq.setRequestType(request.getRequestType());
        syncSIMCardResultReq.setRspCode(request.getRspCode());
        if(request.getRspMsg() != null){
            syncSIMCardResultReq.setRspMsg(request.getRspMsg().getBytes("utf-8"));
        }
        return syncSIMCardResultReq;
    }
}
