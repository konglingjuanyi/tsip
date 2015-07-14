/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.avn.softwave;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.otaadapter.tcmp.entity.resource.ResourceDownloadResp;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.MessageTemplate;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.OTACallback;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.OTATransform;

/**
 * Created with IntelliJ IDEA.
 * User: szksr
 * Date: 13-10-10
 * Time: 下午3:35
 * To change this template use File | Settings | File Templates.
 */
public class ResourceDownloadReqCallback extends OTACallback {

    @Override
    protected void transformServiceObj2Obj(MessageTemplate.Protocol protocol, final Object requestObj, final Object serviceObj){
        String clientVersion = RequestContext.getContext().getClientVersion();
        String serviceVersion = protocol.getServiceVersion();

        if ("1.1".equals(clientVersion) && "1.1".equals(serviceVersion)) {
            new OTATransform.Converter() {
                @Override
                public void convert(Object requestObj, Object serverObj) {
                    byte[] serviceBytes = (byte[])AdapterHelper.getProperty(serverObj, "applicationData");
                    Object serviceResp =  decode(ResourceDownloadResp.class, serviceBytes);
                    String dataUrl = (String) AdapterHelper.getProperty(serviceResp,"dataUrl");
                    int pos = dataUrl.lastIndexOf("=");
                    String downloadId =  dataUrl.substring(pos + 1);
//                    String url = (String) GuiceContext.getInstance().getConfig().getProperties().get("downloadUrl").get("value")+ downloadId;
                    String url = SpringContext.getInstance().getProperty("downloadUrl.value")+downloadId;
                    AdapterHelper.setProperty(serviceResp, "dataUrl", url);
                    byte[] clientBytes = encode(serviceResp);
                    AdapterHelper.setProperty(requestObj, "applicationData", clientBytes);
                }
            }.convert(requestObj, serviceObj);
        }
        if ("1.0".equals(clientVersion) && "1.1".equals(serviceVersion)) {
            new OTATransform.Converter() {
                @Override
                public void convert(Object requestObj, Object serverObj) {
                    byte[] serviceBytes = (byte[])AdapterHelper.getProperty(serverObj, "applicationData");
                    Object serviceResp =  decode(ResourceDownloadResp.class, serviceBytes);
                    String dataUrl = (String) AdapterHelper.getProperty(serviceResp,"dataUrl");
                    int pos = dataUrl.lastIndexOf("=");
                    String downloadId =  dataUrl.substring(pos + 1);
//                    String url = (String) GuiceContext.getInstance().getConfig().getProperties().get("downloadUrl").get("value")+ downloadId;
                    String url = SpringContext.getInstance().getProperty("downloadUrl.value")+downloadId;
                    AdapterHelper.setProperty(serviceResp, "dataUrl", url);
                    byte[] clientBytes = encode(serviceResp);
                    AdapterHelper.setProperty(requestObj, "applicationData", clientBytes);
                }
            }.convert(requestObj, serviceObj);
        }
        if ("6.4".equals(clientVersion) && "1.1".equals(serviceVersion)) {
            new OTATransform.Converter() {
                @Override
                public void convert(Object requestObj, Object serverObj) {
                    byte[] serviceBytes = (byte[])AdapterHelper.getProperty(serverObj, "applicationData");
                    Object serviceResp =  decode(ResourceDownloadResp.class, serviceBytes);
                    String dataUrl = (String) AdapterHelper.getProperty(serviceResp,"dataUrl");
                    int pos = dataUrl.lastIndexOf("=");
                    String downloadId =  dataUrl.substring(pos + 1);
//                    String url = (String) GuiceContext.getInstance().getConfig().getProperties().get("downloadUrl").get("value")+ downloadId;
                    String url = SpringContext.getInstance().getProperty("downloadUrl.value")+downloadId;
                    AdapterHelper.setProperty(serviceResp, "dataUrl", url);
                    byte[] clientBytes = encode(serviceResp);
                    AdapterHelper.setProperty(requestObj, "applicationData", clientBytes);
                }
            }.convert(requestObj, serviceObj);
        }
    }
}