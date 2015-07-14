package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.avn.autonavi;

/**
 * Created with IntelliJ IDEA.
 * User: yicni
 * Date: 14-12-25
 * Time: 下午3:22
 * To change this template use File | Settings | File Templates.
 */
public class Constant4AutoNavi {

    /**MongoDB Collection**/
    public static String CollectionAutoNaviInfo = "autoNaviInfoCollection";
    public static String CollectionAutoNaviValidate = "autoNaviValidateCollection";
    public static String DefaultSchema = "AuthManager";

    public static String Mongo_ID = "_id";
    /**MongoDB CollectionAutoNaviInfo column**/
    /*_id,vin,regTime,duration,firstTime,status,loginCount,batchCount,trafficCount*/
    public static String ANInfo_VIN = "vin";
    public static String ANInfo_RegTime = "regTime";
    public static String ANInfo_Duration = "duration";
    public static String ANInfo_FirstTime = "firstTime";
    public static String ANInfo_Status = "status";
    public static String ANInfo_LoginCount = "loginCount";
    public static String ANInfo_BatchCount = "batchCount";
    public static String ANInfo_TrafficCount = "trafficCount";
    public static String ANInfo_TrafficCount2 = "trafficCount2";
    public static String ANInfo_EventTypeCount = "eventTypeCount";
    public static String ANInfo_LocationTypeCount = "locationTypeCount";
    public static String ANInfo_IntercityTypeCount = "intercityTypeCount";
    public static String ANInfo_IntercityTypeCount2 = "intercityTypeCount2";
    //TODO 增加新增类型记录

    /**MongoDB CollectionAutoNaviValidate column**/
    //_id,vin,validateId,expireTime，createTime
    public static String ANValidate_VIN = "vin";
    public static String ANValidate_ValidateId = "validateId";
    public static String ANValidate_ExpireTime = "expireTime";
    public static String ANValidate_CreateTime = "createTime";

}
