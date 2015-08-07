package com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common;

/**
 * Created by Administrator on 2015/8/6.
 */
public enum AuthServiceEnum {

    USERLOGINAUTH("201","701","AVN用户登录验证"),
    EQUIPLOGINAUTH("203","703"," AVN设备登陆验证"),
    MOBILELOGINVERIFY("501","705","手机登录验证"),
    GETMOBILEDYNAMICPASSWORD("502","721"," 获取手机动态密码"),
    MOBILEVERIFICATIONAUTH("503","722"," 手机验证码验证");

    private  String aid;
    private String sid;
    private String info;

    AuthServiceEnum(String aid,String sid,String info) {
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
