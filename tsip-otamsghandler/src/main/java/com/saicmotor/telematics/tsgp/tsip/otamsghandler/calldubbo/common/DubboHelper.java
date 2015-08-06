package com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common.DubboHelper.class);

    public static List<String> aidMdsList = new ArrayList<String>();

    public static  List<String> aidRoadBookList = new ArrayList<String>();

    public static  List<String> vpList_v1 = new ArrayList<String>();

    public static  List<String> vpList_v2 = new ArrayList<String>();

    public static  List<String> aidCommonList = new ArrayList<String>();

    /**
     * 存放各版本对应的AdapterService实例
     */
    private static Map<String, Object> dubboMap = new HashMap<String, Object>();

    static {
        aidMdsList.add("202");
        aidMdsList.add("4A3");
        aidMdsList.add("5F7");
        aidMdsList.add("4A4");
        aidMdsList.add("766");
        aidMdsList.add("262");
        aidMdsList.add("2F3");
        aidMdsList.add("506");
        aidMdsList.add("507");
        aidMdsList.add("508");
        aidMdsList.add("509");
        aidMdsList.add("50A");
        aidMdsList.add("50B");
        aidMdsList.add("A05");
        aidMdsList.add("A07");
        aidMdsList.add("A09");
        aidMdsList.add("A0C");
        aidMdsList.add("4A8");
        aidMdsList.add("7A7");
        aidMdsList.add("5F1");
        aidMdsList.add("5F6");
        aidMdsList.add("5F2");
        aidMdsList.add("5F3");
        aidMdsList.add("5F4");
        aidMdsList.add("504");
        aidMdsList.add("505");
        aidMdsList.add("591");

        aidRoadBookList.add("481");
        aidRoadBookList.add("482");
        aidRoadBookList.add("483");
        aidRoadBookList.add("484");
        aidRoadBookList.add("485");
        aidRoadBookList.add("486");
        aidRoadBookList.add("487");
        aidRoadBookList.add("488");
        aidRoadBookList.add("489");
        aidRoadBookList.add("2F4");

        vpList_v1.add("205");
        vpList_v1.add("206");
        vpList_v1.add("211");
        vpList_v1.add("212");
        vpList_v1.add("213");
        vpList_v1.add("214");
        vpList_v1.add("215");
        vpList_v1.add("222");
        vpList_v1.add("223");
        vpList_v1.add("240");
        vpList_v1.add("241");
        vpList_v1.add("250");
        vpList_v1.add("251");
        vpList_v2.add("2K1");
        vpList_v2.add("2K2");
        vpList_v1.add("510");
        vpList_v1.add("511");
        vpList_v1.add("521");
        vpList_v1.add("522");
        vpList_v1.add("523");
        vpList_v1.add("561");
        vpList_v1.add("562");
        vpList_v1.add("563");
        vpList_v1.add("564");
        vpList_v1.add("571");
        vpList_v1.add("591");
        vpList_v1.add("5B1");
        vpList_v1.add("5B2");
        vpList_v1.add("5B3");
        vpList_v1.add("5B4");
        vpList_v2.add("5B5");
        vpList_v2.add("5B6");
        vpList_v2.add("5B7");
        vpList_v2.add("5B8");
        vpList_v2.add("5B9");
        vpList_v2.add("5BA");
        vpList_v2.add("5BB");
        vpList_v2.add("5BC");
        vpList_v1.add("5C1");
        vpList_v1.add("5D1");
        vpList_v1.add("5D2");
        vpList_v1.add("5D3");
        vpList_v1.add("5D4");
        vpList_v1.add("5E1");
        vpList_v1.add("5E2");
        vpList_v1.add("782");
    }

    public static void main(String[] args) {
        System.out.println(aidMdsList.contains("202"));
    }
}
