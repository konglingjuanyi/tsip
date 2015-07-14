/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.service.intercepter;

import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.drivingbehavior.FuelEconomyRankingReq;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: szksr
 * Date: 13-8-8
 * Time: 上午9:41
 * To change this template use File | Settings | File Templates.
 */
@Service
public class Aid212Interceptor implements IInterceptor {
    public String change(String aid,byte [] appData) {
        String verifyAid = null;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(appData);
        OTADecoder decoder = new OTADecoder(inputStream);
        FuelEconomyRankingReq fuelEconomyRankingReq = (FuelEconomyRankingReq) decoder.decode(FuelEconomyRankingReq.class);
        if(fuelEconomyRankingReq.getUid() != null && fuelEconomyRankingReq.getUid() !=""){
            verifyAid = "FEU";
        }else {
            verifyAid = "FEV";
        }
        return  verifyAid;
    }
}
