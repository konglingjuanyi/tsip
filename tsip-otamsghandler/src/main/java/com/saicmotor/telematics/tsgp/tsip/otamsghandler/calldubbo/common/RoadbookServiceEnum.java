package com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common;

/**
 * Created by Administrator on 2015/7/28.
 */
public enum RoadbookServiceEnum {
    QUERY("2E1","641","路书搜索"),
    DETAIL("2E2","642","路书详情获取"),
    COMMENTRECEIVE("2E3","643","路书评论信息获取"),
    WEATHERINFOMATION("2F4","633","路书沿途天气预报查询");

    private  String aid;
    private String sid;
    private String info;

    RoadbookServiceEnum(String aid,String sid,String info) {
        this.aid = aid;
        this.sid = sid;
        this.info = info;
    }

    public String getAid() {
        return aid;
    }

    public String getInfo() {
        return info;
    }

    public String getSid() {
        return sid;
    }

    public static void main(String[] args) {
    }
}
