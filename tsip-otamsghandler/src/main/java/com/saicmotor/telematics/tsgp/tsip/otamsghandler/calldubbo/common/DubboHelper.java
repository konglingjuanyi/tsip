package com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.ServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.as.AsServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.auth.AuthServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.mds.MdsServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.message.MessageServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.navi.NaviServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.roadbook.RoadbookServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.vp.VpServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.weather.WeatherServiceHelper;
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

    private static List<String> mdsList = new ArrayList<String>();

    private static  List<String> roadBookList = new ArrayList<String>();

    private static  List<String> vpList_v1 = new ArrayList<String>();

    private static  List<String> vpList_v2 = new ArrayList<String>();

    private static  List<String> authList = new ArrayList<String>();

    private static  List<String> asList = new ArrayList<String>();

    private static List<String> messageList = new ArrayList<String>();

    private static List<String> naviList = new ArrayList<String>();

    private static List<String> weatherList = new ArrayList<String>();

    /**
     * 存放各版本对应的AdapterService实例
     */
    private static Map<String, Object> dubboMap = new HashMap<String, Object>();

    static {
        mdsList.add("202");
        mdsList.add("4A3");
        mdsList.add("5F7");
        mdsList.add("4A4");
        mdsList.add("766");
        mdsList.add("262");
        mdsList.add("2F3");
        mdsList.add("506");
        mdsList.add("507");
        mdsList.add("508");
        mdsList.add("509");
        mdsList.add("50A");
        mdsList.add("50B");
        mdsList.add("A05");
        mdsList.add("A07");
        mdsList.add("A09");
        mdsList.add("A0C");
        mdsList.add("4A8");
        mdsList.add("7A7");
        mdsList.add("5F1");
        mdsList.add("5F6");
        mdsList.add("5F2");
        mdsList.add("5F3");
        mdsList.add("5F4");
        mdsList.add("504");
        mdsList.add("505");
        mdsList.add("591");

        roadBookList.add("481");
        roadBookList.add("482");
        roadBookList.add("483");
        roadBookList.add("484");
        roadBookList.add("485");
        roadBookList.add("486");
        roadBookList.add("487");
        roadBookList.add("488");
        roadBookList.add("489");
        roadBookList.add("2F4");

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

        authList.add("201");
        authList.add("203");
        authList.add("501");
        authList.add("502");
        authList.add("503");

        messageList.add("781");
        messageList.add("793");
        messageList.add("493");
        messageList.add("531");
        messageList.add("533");
        messageList.add("534");
        messageList.add("2A1");
        messageList.add("2A2");
        messageList.add("2A3");
        messageList.add("611");
        messageList.add("612");
        messageList.add("613");
        messageList.add("614");
        messageList.add("615");
        messageList.add("616");
        messageList.add("617");
        messageList.add("618");
        messageList.add("619");
        messageList.add("651");

        naviList.add("6A2");
        naviList.add("6A3");
        naviList.add("6B1");
        naviList.add("6B2");
        naviList.add("6B3");
        naviList.add("6D1");
        naviList.add("6D2");
        naviList.add("6D3");
        naviList.add("6D4");
        naviList.add("6D5");
        naviList.add("6D6");
        naviList.add("6D7");
        naviList.add("6D8");
        naviList.add("6D9");
        naviList.add("6DA");
        naviList.add("241");
        naviList.add("2C1");
        naviList.add("2C2");
        naviList.add("2G1");
        naviList.add("551");
        naviList.add("552");
        naviList.add("553");
        naviList.add("554");
        naviList.add("555");
        naviList.add("556");
        naviList.add("557");
        naviList.add("558");
        naviList.add("559");
        naviList.add("461");
        naviList.add("471");
        naviList.add("481");
        naviList.add("342");

        weatherList.add("2F5");
        weatherList.add("2F6");


    }

    /**
     * 是否是导航
     * @param aid
     * @return
     */
    private static boolean isNavi(String aid){
        return naviList.contains(aid) ? true : false;
    }

    /**
     * 是否是消息
     * @param aid
     * @return
     */
    private static boolean isMessage(String aid){
        return messageList.contains(aid) ? true :false;
    }

    /**
     * 是否是mds
     * @param aid
     * @return
     */
    private static boolean isMds(String aid){
        return mdsList.contains(aid) ? true : false;
    }

    /**
     * 是否是roadbook
     * @param aid
     * @return
     */
    private static boolean isRoadBook(String aid){
        return roadBookList.contains(aid) ? true : false;
    }


    /**
     * 是否是鉴权
     * @param aid
     * @return
     */
    private static boolean isAuth(String aid){
        return authList.contains(aid) ? true : false;
    }

    /**
     * 是否是VP1
     * @param aid
     * @return
     */
    public static boolean isVp1(String aid){
        return vpList_v1.contains(aid) ? true : false;
    }

    /**
     * 是否是VP2
     * @param aid
     * @return
     */
    public static boolean isVp2(String aid){
        return vpList_v2.contains(aid) ? true : false;
    }

    /**
     * 是否是VP
     * @param aid
     * @return
     */
    private static boolean isVp(String aid){
        return isVp1(aid) || isVp2(aid) ? true : false;
    }

    /**
     * 是否是AS
     * @param aid
     * @return
     */
    private static boolean isAs(String aid){
        return aid.startsWith("A");
    }

    /**
     * 是否是天气
     * @param aid
     * @return
     */
    private static boolean isWeather(String aid){
        return weatherList.contains(aid) ? true :false;
    }

    /**
     * 初始化dubboService
     * @param aid
     * @return
     */
    public static ServiceHelper initDubboService(String aid){
        ServiceHelper serviceHelper = null;
        if(DubboHelper.isMds(aid)){
            serviceHelper = (ServiceHelper) SpringContext.getInstance().getBean(MdsServiceHelper.class);
        }
        if(DubboHelper.isRoadBook(aid)){
            serviceHelper = (ServiceHelper) SpringContext.getInstance().getBean(RoadbookServiceHelper.class);
        }
        if(DubboHelper.isVp(aid)){
            serviceHelper = (ServiceHelper) SpringContext.getInstance().getBean(VpServiceHelper.class);
        }
        if(DubboHelper.isAuth(aid)){
            serviceHelper = (ServiceHelper) SpringContext.getInstance().getBean(AuthServiceHelper.class);
        }
        if(DubboHelper.isAs(aid)){
            serviceHelper = (ServiceHelper) SpringContext.getInstance().getBean(AsServiceHelper.class);
        }
        if(DubboHelper.isNavi(aid)){
            serviceHelper = (ServiceHelper) SpringContext.getInstance().getBean(NaviServiceHelper.class);
        }
        if(DubboHelper.isMessage(aid)){
            serviceHelper = (ServiceHelper) SpringContext.getInstance().getBean(MessageServiceHelper.class);
        }
        if(DubboHelper.isWeather(aid)){
            serviceHelper = (ServiceHelper) SpringContext.getInstance().getBean(WeatherServiceHelper.class);
        }
        return serviceHelper;
    }

    public static void main(String[] args) {
        System.out.println(mdsList.contains("202"));
    }
}
