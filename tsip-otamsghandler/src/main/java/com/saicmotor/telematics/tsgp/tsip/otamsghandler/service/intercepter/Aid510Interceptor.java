/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.service.intercepter;

import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.tbox.v1_1.entity.vehicle.CtrlCommandStartReq;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: szksr
 * Date: 13-8-8
 * Time: 上午10:09
 * To change this template use File | Settings | File Templates.
 */
@Service
public class Aid510Interceptor implements IInterceptor {

    public String change(String aid,byte [] appData) {
        String verifyAid = null;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(appData);
        OTADecoder decoder = new OTADecoder(inputStream);
        CtrlCommandStartReq ctrlCommandStartReq = (CtrlCommandStartReq)decoder.decode(CtrlCommandStartReq.class);
        String command =ctrlCommandStartReq.getCommand();
        if("010".equals(command)||"011".equals(command)||"020".equals(command)){
            verifyAid = "TC1";
        }else if("030".equals(command)||"031".equals(command)){
            verifyAid = "TC2";
        }
        return  verifyAid;
    }
}
