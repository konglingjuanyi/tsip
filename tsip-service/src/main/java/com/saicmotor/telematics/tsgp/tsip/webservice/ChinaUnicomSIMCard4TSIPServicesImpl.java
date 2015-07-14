package com.saicmotor.telematics.tsgp.tsip.webservice;


import cn.chinaunicom.chinaunicomsimcard4tsipservices.ChinaUnicomSIMCard4TSIPServices;
import cn.chinaunicom.chinaunicomsimcard4tsipservices.simcardtype.*;
import com.saicmotor.telematics.tsgp.tsip.webservice.handler.SyncSIMCardResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Created with IntelliJ IDEA.
 * User: jwdsq
 * Date: 14-2-21
 * Time: 上午8:39
 * To change this template use File | Settings | File Templates.
 */
//@WebService(targetNamespace = "http://www.chinaunicom.cn/ChinaUnicomSIMCardServices", name = "ChinaUnicomSIMCardServices")
@WebService(wsdlLocation = "classpath:META-INF/wsdl/SQ-ChinaUnicomSIMCard4TSIPServices.wsdl", targetNamespace = "http://www.chinaunicom.cn/ChinaUnicomSIMCard4TSIPServices", name = "ChinaUnicomSIMCard4TSIPServices", serviceName = "ChinaUnicomSIMCard4TSIPServices", endpointInterface = "cn.chinaunicom.chinaunicomsimcard4tsipservices.ChinaUnicomSIMCard4TSIPServices")

public class ChinaUnicomSIMCard4TSIPServicesImpl implements ChinaUnicomSIMCard4TSIPServices {
    private static final Logger logger = LoggerFactory.getLogger(ChinaUnicomSIMCard4TSIPServicesImpl.class);

    public SIMCardStatusResponseType getSIMCardStatus(
            @WebParam(partName = "request", name = "getSIMCardStatusRequest", targetNamespace = "http://www.chinaunicom.cn/ChinaUnicomSIMCard4TSIPServices/SIMCardType")
            SIMCardStatusRequestType request
    ) {
        SIMCardStatusResponseType response = new SIMCardStatusResponseType();
        //V20	厂商标识(CNUNICOM)
        response.setRequestor("CNUNICOM");
        //F11	手机号码
        response.setMsisdn("12345678903");
        //F19	SIM卡号
        response.setIccid(request.getIccid());
        //F2	卡状态
        //4：测试启用(是否收费根据业务规定)
        // 5：待销售(不收费)
        // 6：在用(收费)
        response.setSubStatus("5");
        //F2	功能状态
        //1：开机
        //2：停机
        response.setServiceStatus("2");
        //F14	卡状态变更时间(yyyyMMddhh24miss)
        response.setCardStatusChgDate("20140101010101");
        //F14	功能状态变更时间(yyyyMMddhh24miss)
        response.setServiceStatusChgDate("20140202020202");
        //V20	当前套餐
        response.setProductId("1");
        response.setProductName("当前套餐1");
        response.setRspCode("0000");
        response.setRspMsg("成功");
        response.setVoiceStatus("1");
        response.setDataStatus("1");
        response.setNormalizeDate("20150202020202");
        return response;
    }

    public SIMCardResourceResponseType getSIMCardResource(
            @WebParam(partName = "request", name = "getSIMCardResourceRequest", targetNamespace = "http://www.chinaunicom.cn/ChinaUnicomSIMCard4TSIPServices/SIMCardType")
            SIMCardResourceRequestType request
    ) {
        SIMCardResourceResponseType cardResourceResponse = new SIMCardResourceResponseType();
        //V20	中国联通(CNUNICOM)
        cardResourceResponse.setRequestor("CNUNICOM");
        //F11	手机号码
        cardResourceResponse.setMsisdn("12345678903");
        //F19	SIM卡号
        cardResourceResponse.setIccid(request.getIccid());
        //F2	卡状态
        cardResourceResponse.setCardStatus("5");
        //F2	功能状态
        //1：开机
        //2：停机
        cardResourceResponse.setServiceStatus("2");
        //F14	卡状态变更时间(yyyyMMddhh24miss)
        cardResourceResponse.setCardStatusChgDate("20140101010101");
        //F14	功能状态变更时间(yyyyMMddhh24miss)
        cardResourceResponse.setServiceStatusChgDate("20140101010101");

        //V30	车型
        cardResourceResponse.setCarType("550");
        //F6	月份(yyyyMM)
        cardResourceResponse.setMonth("201301");
        cardResourceResponse.setDate("20140101013333");
        // V20	余额(单位：分)
        cardResourceResponse.setBalance("10000");
        //V20	欠费(单位：分)
        cardResourceResponse.setOwed("0");
        //V20	已使用流量（KB）
        cardResourceResponse.setUsedData("10000");
        //V20	剩余流量（KB）
        cardResourceResponse.setRemainData("20000");
        //V20　	已使用分钟数
        cardResourceResponse.setUsedVoice("30000");
        //V20	剩余分钟数
        cardResourceResponse.setRemainVoice("40000");
        //V20	已使用条数
        cardResourceResponse.setUsedSMS("50000");
        //V20	剩余条数
        cardResourceResponse.setRemainSMS("60000");

        //V20	当前套餐
        cardResourceResponse.setProductId("12");
        //V20	当前套餐
        cardResourceResponse.setProductName("当前套餐1");
        cardResourceResponse.setRspCode("0000");
        cardResourceResponse.setRspMsg("成功");
        return cardResourceResponse;
    }

    public ActivedSIMCardResponseType getBusiResult(@WebParam(partName = "request", name = "getBusiResultRequest", targetNamespace = "http://www.chinaunicom.cn/ChinaUnicomSIMCardServices/SIMCardType") BusiResultRequestType request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public SIMCardResultResponseType syncSIMCardResult(
            @WebParam(partName = "request", name = "syncSIMCardResultRequest", targetNamespace = "http://www.chinaunicom.cn/ChinaUnicomSIMCard4TSIPServices/SIMCardType")
            SIMCardResultRequestType request
    ) {
        SyncSIMCardResultHandler  syncSIMCardResultHandler=new SyncSIMCardResultHandler();

        return syncSIMCardResultHandler.process(request);
    }

    public ActivedSIMCardResponseType syncActivedSIMCard(@WebParam(partName = "request", name = "syncActivedSIMCardRequest", targetNamespace = "http://www.chinaunicom.cn/ChinaUnicomSIMCardServices/SIMCardType") ActivedSIMCardRequestType request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public SIMCardResponseType syncSIMCard(
            @WebParam(partName = "request", name = "syncSIMCardRequest", targetNamespace = "http://www.chinaunicom.cn/ChinaUnicomSIMCard4TSIPServices/SIMCardType")
            SIMCardRequestType request
    ) {
        SIMCardResponseType cardResponse = new SIMCardResponseType();
        //V20	中国联通(CNUNICOM)
        cardResponse.setRequestor("CNUNICOM");
        //F2	业务类型
        cardResponse.setRequestType(request.getRequestType());
        //V20	请求ID
        cardResponse.setRequestId(request.getRequestId());
        //F19	响应码
        cardResponse.setRspCode("0000");
        //V100	响应信息
        cardResponse.setRspMsg("成功");
        return cardResponse;
    }

    public GetSIMCardPaymentResponseType getSIMCardPayment(@WebParam(partName = "request", name = "getSIMCardPaymentRequest", targetNamespace = "http://www.chinaunicom.cn/ChinaUnicomSIMCardServices/SIMCardType") GetSIMCardPaymentRequestType request) {
        GetSIMCardPaymentResponseType response =  new GetSIMCardPaymentResponseType() ;
        //V20	厂商标识(CNUNICOM)
        response.setRequestor("CNUNICOM");
        response.setRspCode("0000");
        response.setRspMsg("成功");
        PayInfoType payInfoType = new PayInfoType();
        payInfoType.setPayId("00000000000000000001");
        payInfoType.setPayChannel("渠道1");
        payInfoType.setPayTime("20140101010101");
        payInfoType.setTotalAmount("108");
        PayInfoType payInfoType2 = new PayInfoType();
        payInfoType2.setPayId("00000000000000000002");
        payInfoType2.setPayChannel("渠道2");
        payInfoType2.setPayTime("20140101010102");
        payInfoType2.setTotalAmount("102");
        response.getPayInfo().add(payInfoType);
        response.getPayInfo().add(payInfoType2);
        return response;
    }


}
