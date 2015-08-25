package com.saicmotor.telematics.tsgp.tsip.otamsghandler.test.servicecatalog;

import org.apache.commons.lang.StringUtils;

/**
 * Created by Administrator on 2015/7/30.
 */
public class Test {

    public static void main(String[] args) {
//        int len = 9;
//        System.out.println(len & 0x01);
//
//        String uid = "0000000000000001010";
//        if(StringUtils.isNotEmpty(uid))
//            uid = uid.replaceAll("^(0+)", "");
//        System.out.println(uid);

        String simNum = "0086000013813813838";
        if(StringUtils.isNotEmpty(simNum) && simNum.startsWith("00860000"))
            simNum = simNum.substring(8);
        System.out.printf(simNum);
    }
}
