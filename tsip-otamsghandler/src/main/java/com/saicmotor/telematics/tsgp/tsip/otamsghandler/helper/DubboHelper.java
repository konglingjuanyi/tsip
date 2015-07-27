package com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/21.
 */
public class DubboHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DubboHelper.class);

    public static List<String> aidList = new ArrayList<String>();

    private static  Map<String,String> aidMap = new HashMap<String,String>();

    /**
     * 存放各版本对应的AdapterService实例
     */
    private static Map<String, Object> dubboMap = new HashMap<String, Object>();

    static {
        aidList.add("202");
        aidList.add("726");
        aidList.add("772");
        aidList.add("7A4");
        aidList.add("7A2");
        aidList.add("725");
        aidList.add("723");
        aidList.add("771");
        aidList.add("79B");
        aidList.add("79A");
        aidList.add("794");
        aidList.add("795");
        aidList.add("796");
        aidList.add("797");
    }

    public static void main(String[] args) {
        System.out.println(aidList.contains("202"));
    }
}
