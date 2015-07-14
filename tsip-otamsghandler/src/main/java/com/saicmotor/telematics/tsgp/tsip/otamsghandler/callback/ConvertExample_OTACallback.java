/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback;

import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.MessageTemplate;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.OTATransform;

/**
 * Created with IntelliJ IDEA.
 * User: jozbt
 * Date: 13-9-16
 * Time: 下午4:00
 * To change this template use File | Settings | File Templates.
 */
public class ConvertExample_OTACallback extends OTACallback{
    /**
     * 转换请求的对象到服务对象
     * @param protocol
     * @param requestObj 请求的OTA_Request对象
     * @return
     */
    @Override
    protected Object transformRequestObj2ServiceObj(MessageTemplate.Protocol protocol, Object requestObj){
        //获取请求的OTA_Request的类型
        Class sourceClass = AdapterHelper.getRequestObjClass(RequestContext.getContext().getPlatform(),RequestContext.getContext().getClientVersion());
        //获取服务端OTA_Request的类型
        Class targetClass = AdapterHelper.getRequestObjClass(protocol.getServicePlatform(), protocol.getServiceVersion());
        //调用OTATransform的方法进行转换，提供转换器对象
        return OTATransform.transform(sourceClass,targetClass,requestObj,new OTATransform.Converter(){
            /**
             * 转换方法，可自由拷贝或修改源OTARequest对象里的属性， 然后设置到目标OTARequest对象
             * 目标OTARequest对象已进行了基本的属性拷贝工作，包括header和body部分
             * 一般情况下只要处理请求的applicationData到目标对象里的applicationData的转换
             * @param sourceRequest 源OTARequest对象
             * @param targetRequest 目标OTARequest对象
             */
            @Override
            public void convert(Object sourceRequest, Object targetRequest) {
                //示例代码
                //获取请求的applicationData
                byte[] sourceBytes = (byte[])AdapterHelper.getProperty(sourceRequest, "applicationData");
                //使用源请求的adapter类型解码applicationData
                Object sourceReq =  decode(com.saicmotor.telematics.tsgp.otaadapter.mp.v1_1.entity.poi.AddToFavoritePOIReq.class, sourceBytes);
                //copy请求对象的属性值到目标对象，可用fastCopy方法
                Object serviceReq = fastCopy(sourceReq, com.saicmotor.telematics.tsgp.otaadapter.navi.v1_1.entity.poi.AddToFavoritePOIReq.class);
                //使用目标的adapter类型编码成applicationData, 设置到服务端的请求对象
                byte[] serviceBytes = encode(serviceReq);
                AdapterHelper.setProperty(targetRequest, "applicationData", serviceBytes);
            }
        });
    }

    /**
     * 转换服务端对象到请求对象
     * @param protocol
     * @param requestObj 原始请求OTARequest对象
     * @param serviceObj 服务端响应的OTARequest对象
     */
    @Override
    protected void transformServiceObj2Obj(MessageTemplate.Protocol protocol, Object requestObj, Object serviceObj){
        //示例代码
        new OTATransform.Converter() {
            @Override
            public void convert(Object requestObj, Object serviceObj) {
                //获取服务端的applicationData
                byte[] serviceBytes = (byte[])AdapterHelper.getProperty(serviceObj, "applicationData");
                //使用服务端adapter解码applicationData
                Object serviceResp =  decode(com.saicmotor.telematics.tsgp.otaadapter.roadbook.v1_1.entity.roadbook.RoadBookDetailResp.class, serviceBytes);
                //使用fastCopy快速拷贝属性至客户端的adapter中
                Object clientResp = fastCopy(serviceResp, com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.roadbook.RoadBookDetailResp.class);
                //编码并设置applicationData到客户端的OTARequest对象
                byte[] clientBytes = encode(clientResp);
                AdapterHelper.setProperty(requestObj, "applicationData", clientBytes);
            }
        }.convert(requestObj, serviceObj);
    }
}
