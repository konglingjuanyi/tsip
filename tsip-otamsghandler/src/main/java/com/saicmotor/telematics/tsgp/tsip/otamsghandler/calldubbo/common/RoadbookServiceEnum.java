package com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common;

/**
 * Created by Administrator on 2015/7/28.
 */
public enum RoadbookServiceEnum {
    QUERY("481","路书搜索"),
    DETAIL("482","路书详情获取"),
    COMMENTRECEIVE("483","路书评论信息获取"),
    ADDDELETECOMMENT("484","路书评论信息添加和删除"),
    SENDTOCAR("485","路书下发"),
    FAVORITEQUERY("486","路书收藏列表获取"),
    ADDTOFAVORITE("487","路书收藏添加"),
    DELETEFAVORITE("488","路书收藏删除"),
    WEATHERINFOMATION("2F4","路书沿途天气预报查询"),
    DETAILBATCH("489","路书详情批量获取");

    private  String aid;
    private String info;

    RoadbookServiceEnum(String aid,String info) {
        this.aid = aid;
        this.info = info;
    }

    public String getAid() {
        return aid;
    }

    public String getInfo() {
        return info;
    }

    public static void main(String[] args) {
        System.out.println(RoadbookServiceEnum.ADDDELETECOMMENT.getAid());
        System.out.println(RoadbookServiceEnum.ADDDELETECOMMENT.getInfo());
    }
}
