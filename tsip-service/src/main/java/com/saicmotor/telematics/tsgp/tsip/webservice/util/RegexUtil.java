/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.webservice.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: szksr
 * Date: 13-9-3
 * Time: 上午9:24
 * To change this template use File | Settings | File Templates.
 */
public class RegexUtil {
    public static final String MOBILE = "[0-9\\+\\-\\ ]+";
    public static final String POST_CODE = "\\d{6}";

    public static boolean check(String regex,String str){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    public static void main(String[] args){
        String str = "010-64214657";
        System.out.print(check(MOBILE,str));
    }
}
