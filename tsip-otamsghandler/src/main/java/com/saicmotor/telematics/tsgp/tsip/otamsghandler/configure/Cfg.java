/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure;


import com.saicmotor.telematics.framework.core.common.SpringContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量配置类
 * @author: jozbt
 * @author zhuxiaoyan
 */
public class Cfg {
    public static final String AVN_VERIFY_AID = "781";
    public static final String MP_VERIFY_AID = "782";
    public static final String WXG4AS_VERIFY_AID = "786";

    public static final String PLATFORM_B2C = "001";
    public static final String PLATFORM_TBOX = "002";
    public static final String PLATFORM_CC = "003";
    public static final String PLATFORM_MP = "004";
    public static final String PLATFORM_AVN = "005";
    public static final String PLATFORM_TCMP = "007";
    public static final String PLATFORM_MESSAGE = "008";
    public static final String PLATFORM_ROAD_BOOK = "009";
    public static final String PLATFORM_NAVI = "010";
    public static final String PLATFORM_VP = "011";
    public static final String PLATFORM_BT = "012";

    public static final String PLATFORM_WXG4AS = "013";
    public static final String PLATFORM_AS = "014";
    public static final String PLATFORM_ISP = "015";

    //短信上行平台
    public static final String PLATFORM_SMS = "016";

    public static final String PLATFORM_WEATHER = "017";

    public static final String PLATFORM_ASM = "018";

    public static final String DEFAULT_VERSION = "1.1";

    public static final Map<String,String> VERSION_PATH_MAP = new HashMap<String, String>(){{
        put(Cfg.PLATFORM_AVN + CONNECTOR + "6.4", "v1_1");
        put(Cfg.PLATFORM_AVN + CONNECTOR + "1.0", "v1_1");
        put(Cfg.PLATFORM_TBOX + CONNECTOR + "0.1", "v1_1");
        put(Cfg.PLATFORM_TBOX + CONNECTOR + "1.0", "v1_1");

        put(Cfg.PLATFORM_TBOX + CONNECTOR + "1.1", "v1_1");
        put(Cfg.PLATFORM_AVN + CONNECTOR + "1.1", "v1_1");
        put(Cfg.PLATFORM_MESSAGE + CONNECTOR + "1.1", "v1_1");
        put(Cfg.PLATFORM_ROAD_BOOK + CONNECTOR + "1.1", "v1_1");
        put(Cfg.PLATFORM_NAVI + CONNECTOR + "1.1", "v1_1");
        put(Cfg.PLATFORM_VP + CONNECTOR + "1.1", "v1_1");
        put(Cfg.PLATFORM_MP+ CONNECTOR + "1.1", "v1_1");
        put(Cfg.PLATFORM_NAVI + CONNECTOR + "1.2", "v1_1");
        put(Cfg.PLATFORM_VP+ CONNECTOR + "1.2", "v1_1");
        put(Cfg.PLATFORM_MESSAGE+ CONNECTOR + "1.2", "v1_1");
    }};

    public static final HashMap<String,String> PL_ADAPTER_MAP = new HashMap<String,String>(){{
        put(Cfg.PLATFORM_TBOX, "com.saicmotor.telematics.tsgp.otaadapter.tbox.%s.service.TBOXAdapterServiceImpl");
        put(Cfg.PLATFORM_AVN, "com.saicmotor.telematics.tsgp.otaadapter.avn.%s.service.AVNAdapterServiceImpl");
        put(Cfg.PLATFORM_MESSAGE, "com.saicmotor.telematics.tsgp.otaadapter.message.%s.service.MessageAdapterServiceImpl");
        put(Cfg.PLATFORM_ROAD_BOOK, "com.saicmotor.telematics.tsgp.otaadapter.roadbook.%s.service.RBAdapterServiceImpl");
        put(Cfg.PLATFORM_NAVI, "com.saicmotor.telematics.tsgp.otaadapter.navi.%s.service.NaviAdapterServiceImpl");
        put(Cfg.PLATFORM_VP, "com.saicmotor.telematics.tsgp.otaadapter.vp.%s.service.VPAdapterServiceImpl");
        put(Cfg.PLATFORM_MP, "com.saicmotor.telematics.tsgp.otaadapter.mp.%s.service.MPAdapterServiceImpl");
    }};

    public static final Map<String,String> PL_REQUEST_MAP = new HashMap<String,String>(){{
        put(Cfg.PLATFORM_TBOX, "com.saicmotor.telematics.tsgp.otaadapter.tbox.%s.entity.dispatcher.TBOX_OTARequest");
        put(Cfg.PLATFORM_AVN, "com.saicmotor.telematics.tsgp.otaadapter.avn.%s.entity.dispatcher.AVN_OTARequest");
        put(Cfg.PLATFORM_MESSAGE, "com.saicmotor.telematics.tsgp.otaadapter.message.%s.entity.dispatcher.Message_OTARequest");
        put(Cfg.PLATFORM_ROAD_BOOK, "com.saicmotor.telematics.tsgp.otaadapter.roadbook.%s.entity.dispatcher.RB_OTARequest");
        put(Cfg.PLATFORM_NAVI, "com.saicmotor.telematics.tsgp.otaadapter.navi.%s.entity.dispatcher.Navi_OTARequest");
        put(Cfg.PLATFORM_VP, "com.saicmotor.telematics.tsgp.otaadapter.vp.%s.entity.dispatcher.VP_OTARequest");
        put(Cfg.PLATFORM_MP, "com.saicmotor.telematics.tsgp.otaadapter.mp.%s.entity.dispatcher.MP_OTARequest");
    }};

    public static final Map<String,String> PL_STR_MAP = new HashMap<String,String>(){{
        put(PLATFORM_AVN,"AVN");
        put(PLATFORM_MP,"MP");
        put(PLATFORM_TBOX,"TBOX");
        put(PLATFORM_B2C,"B2C");
        put(PLATFORM_CC,"CC");
        put(PLATFORM_TCMP,"TCMP");
        put(PLATFORM_MESSAGE,"Message");
        put(PLATFORM_ROAD_BOOK,"RoadBook");
        put(PLATFORM_NAVI,"Navi");
        put(PLATFORM_VP,"VP");
        put(PLATFORM_AS,"AS");
        put(PLATFORM_ISP,"ISP");
        put(PLATFORM_WXG4AS,"WXG4AS");
        put(PLATFORM_WEATHER,"Weather");
        put(PLATFORM_ASM,"ASM");
    }};

    public static final Map<String,String> PL_ERROR_CODE_MAP = new HashMap<String,String>(){{
        put(PLATFORM_B2C,"509");
        put(PLATFORM_CC,"507");
        put(PLATFORM_TCMP,"508");
        put(PLATFORM_MESSAGE,"504");
        put(PLATFORM_ROAD_BOOK,"506");
        put(PLATFORM_NAVI,"505");
        put(PLATFORM_VP,"503");
        put(PLATFORM_AS,"510");
        put(PLATFORM_ISP,"511");
        put(PLATFORM_WEATHER,"512");
    }};

    public static final Map<String,String> URL_MAP = new HashMap<String,String>(){{
            put(PL_STR_MAP.get(PLATFORM_TCMP),SpringContext.getInstance().getProperty("TCMP.url"));
            put(PL_STR_MAP.get(PLATFORM_MESSAGE),SpringContext.getInstance().getProperty("Message.url"));
            put(PL_STR_MAP.get(PLATFORM_ROAD_BOOK),SpringContext.getInstance().getProperty("RoadBook.url"));
            put(PL_STR_MAP.get(PLATFORM_NAVI),SpringContext.getInstance().getProperty("Navi.url"));
            put(PL_STR_MAP.get(PLATFORM_VP),SpringContext.getInstance().getProperty("VP.url"));
            put(PL_STR_MAP.get(PLATFORM_B2C),SpringContext.getInstance().getProperty("B2C.url"));
            put(PL_STR_MAP.get(PLATFORM_AS),SpringContext.getInstance().getProperty("AS.url"));
            put(PL_STR_MAP.get(PLATFORM_ISP),SpringContext.getInstance().getProperty("ISP.url"));
            put(PL_STR_MAP.get(PLATFORM_WEATHER),SpringContext.getInstance().getProperty("WEATHER.url"));
            put("cc_local",SpringContext.getInstance().getProperty("cc_local.wsLocation"));
            put("cc_city",SpringContext.getInstance().getProperty("cc_city.wsLocation"));
    }};

    //旧客户端错误协议版本到新协议版本的转换
    public static final Map<String, String> LEGACY_VERSION_MAP = new HashMap<String, String>() {{
        put("AVN-6.4", "1.1");
        put("MP-4.0", "1.1");
        put("AVN-1.0", "1.1");//for博泰ESO前
        put("CC-1.0", "1.1");
    }};

    //旧客户端错误软件版本到新软件版本的转换
    public static final Map<String, String> LEGACY_APP_VERSION_MAP = new HashMap<String, String>() {{
        put("AVN-0.100", "1.0");
        put("MP-0.64", "1.0");
        put("TBOX-0.1", "1.0");
        put("MP-1.1", "2.0");//TODO for test
        put("AVN-1.0", "2.0");//for博泰ESO前
    }};

    public static final String CONNECTOR = "|";

    public static final String PLATFORM_NEED_CONVERT_ERROR_CODE =PLATFORM_B2C+ CONNECTOR +PLATFORM_CC+ CONNECTOR +PLATFORM_MP+ CONNECTOR +PLATFORM_TCMP+PLATFORM_AVN+ CONNECTOR +PLATFORM_TBOX;

    public static String getPlatformStrByID(String platformId) {
        return PL_STR_MAP.get(platformId);
    }

    public static String getUrlByFlag(String urlFlag) {
        String url = URL_MAP.get(urlFlag);

        if (url == null) {
            url =  SpringContext.getInstance().getProperty(urlFlag+"url");
            URL_MAP.put(urlFlag, url);
        }

        return url;
    }
}
