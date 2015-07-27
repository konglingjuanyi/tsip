package com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper;

/**
 * Created by Administrator on 2015/7/27.
 */
public enum MdsServiceEnum {
    GETAVNACTIVATIONCODE("5F7"),AVNACTIVATE("202"),AVNACTIVATESTATUSCHECK("4A4"),VEHICLEMESPART4OPEN("766")
    ,CITYLISTDOWNLOAD("262"),ADDORREMOVECONCERNCITIES("2F3"),MPUSERINFOQUERY("506"),MPUSERINFOUPDATE("507")
    ,MPVEHICLEINFOQUERY("508"),MPVEHICLEINFOUPDATE("509"),MPUSERFEEDBACK("50A"),MPCONTROLGRADE("50B")
    , ASVEHICLECHECKED("A05"),ASGETVEHICLELIST("A07"),ASGETVEHICLEDETAIL("A09"),UPDATEVEHICLEFROMTCMP("A0C")
    ,ACTIVATESIMCARD("4A8"),UPDATESIMACTIVESTATUS("7A7"),SETPINCODE("5F1"),GETVEHICLEBRANDMODEL("5F6")
    ,ADDPERSONALVEHICLE("5F2"),ADDORGVEHICLE("5F3"),SETUSERMOBILEPHONE("5F4"),MPFASTREGISTER("504")
    ,UPDATEPASSWORD("505");

    private  String aid;

    MdsServiceEnum(String aid) {
        this.aid = aid;
    }

    @Override
    public String toString() {
        return String.valueOf(this.aid);
    }

    public static void main(String[] args) {
        System.out.println(MdsServiceEnum.ACTIVATESIMCARD);
    }
}
