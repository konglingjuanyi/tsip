package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.avn.autonavi;

import com.mongodb.*;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.MongoHelper;
import org.bson.BSONObject;

import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: yicni
 * Date: 14-12-25
 * Time: 下午3:35
 * To change this template use File | Settings | File Templates.
 */
public class AutoNaviMongoService {

    //1.查询VIN号是否存在注册信息

    //1.更新注册信息首次调用时间

    public static ANInfo findANInfo(String vin){
        MongoClient mongoClient = MongoHelper.getClient();
        DB mongo = mongoClient.getDB(Constant4AutoNavi.DefaultSchema);
        DBCollection collection = mongo.getCollection(Constant4AutoNavi.CollectionAutoNaviInfo);
        DBObject query = new BasicDBObject();
        query.put(Constant4AutoNavi.ANInfo_VIN, vin);
        ANInfo anInfo = null;
        if(mongo.collectionExists(Constant4AutoNavi.CollectionAutoNaviInfo)){
            DBCursor cursor = collection.find(query).sort(new BasicDBObject(Constant4AutoNavi.Mongo_ID, -1)).limit(1);
            if(cursor.hasNext()) {
                DBObject data = cursor.next();
                anInfo = new ANInfo();
                anInfo.setBatchCount((Long)data.get(Constant4AutoNavi.ANInfo_BatchCount));
                anInfo.setDuration((Long)data.get(Constant4AutoNavi.ANInfo_Duration));
                anInfo.setId(data.get(Constant4AutoNavi.Mongo_ID));
                anInfo.setLoginCount((Long)data.get(Constant4AutoNavi.ANInfo_LoginCount));
                anInfo.setRegTime((Long)data.get(Constant4AutoNavi.ANInfo_RegTime));
                anInfo.setStatus((String)data.get(Constant4AutoNavi.ANInfo_Status));
                /*anInfo.setTrafficCount((Long)data.get(Constant4AutoNavi.ANInfo_TrafficCount));
                anInfo.setEventTypeCount((Long)data.get(Constant4AutoNavi.ANInfo_EventTypeCount));
                anInfo.setLocationTypeCount((Long)data.get(Constant4AutoNavi.ANInfo_LocationTypeCount));
                anInfo.setInterCityTypeCount((Long)data.get(Constant4AutoNavi.ANInfo_IntercityTypeCount));*/
                anInfo.setVin((String)data.get(Constant4AutoNavi.ANInfo_VIN));
            }
        }
        return anInfo;
    }

    //1.查询VIN号是否存在临时ID
    public static ANValidate findANValidate(String vin){
        MongoClient mongoClient = MongoHelper.getClient();
        DB mongo = mongoClient.getDB(Constant4AutoNavi.DefaultSchema);
        DBCollection collection = mongo.getCollection(Constant4AutoNavi.CollectionAutoNaviValidate);
        DBObject query = new BasicDBObject();
        query.put(Constant4AutoNavi.ANValidate_VIN, vin);
        ANValidate anValidate = null;
        if(mongo.collectionExists(Constant4AutoNavi.CollectionAutoNaviValidate)){
            DBCursor cursor = collection.find(query).sort(new BasicDBObject(Constant4AutoNavi.Mongo_ID, -1)).limit(1);
            if(cursor.hasNext()) {
                DBObject data = cursor.next();
                anValidate = new ANValidate();
                anValidate.setCreateTime((Long)data.get(Constant4AutoNavi.ANValidate_CreateTime));
                anValidate.setExpireTime((Long) data.get(Constant4AutoNavi.ANValidate_ExpireTime));
                anValidate.setId(data.get(Constant4AutoNavi.Mongo_ID));
                anValidate.setValidateId((String)data.get(Constant4AutoNavi.ANValidate_ValidateId));
                anValidate.setVin((String)data.get(Constant4AutoNavi.ANValidate_VIN));
            }
        }
        return anValidate;
    }

    //1.创建注册信息
    public static ANInfo createANInfo(ANInfo anInfo){
        MongoClient mongoClient = MongoHelper.getClient();
        DB mongo = mongoClient.getDB(Constant4AutoNavi.DefaultSchema);
        DBCollection collection = mongo.getCollection(Constant4AutoNavi.CollectionAutoNaviInfo);
        DBObject data = new BasicDBObject();
        data.put(Constant4AutoNavi.ANInfo_BatchCount,anInfo.getBatchCount());
        data.put(Constant4AutoNavi.ANInfo_Duration,anInfo.getDuration());
        data.put(Constant4AutoNavi.ANInfo_LoginCount,anInfo.getLoginCount());
        data.put(Constant4AutoNavi.ANInfo_RegTime,anInfo.getRegTime());
        data.put(Constant4AutoNavi.ANInfo_Status,anInfo.getStatus());
        data.put(Constant4AutoNavi.ANInfo_TrafficCount,anInfo.getTrafficCount());
        data.put(Constant4AutoNavi.ANInfo_TrafficCount2,anInfo.getTrafficCount2());
        data.put(Constant4AutoNavi.ANInfo_EventTypeCount,anInfo.getEventTypeCount());
        data.put(Constant4AutoNavi.ANInfo_LocationTypeCount,anInfo.getLocationTypeCount());
        data.put(Constant4AutoNavi.ANInfo_IntercityTypeCount,anInfo.getInterCityTypeCount());
        data.put(Constant4AutoNavi.ANInfo_IntercityTypeCount2,anInfo.getInterCityTypeCount2());
        data.put(Constant4AutoNavi.ANInfo_VIN,anInfo.getVin());
        WriteResult writeResult = collection.insert(data);
        Object id = writeResult.getField(Constant4AutoNavi.Mongo_ID);
        anInfo.setId(id);
        return anInfo;
    }

    //1.创建临时ID
    public static ANValidate createANValidate(ANValidate anValidate){
        MongoClient mongoClient = MongoHelper.getClient();
        DB mongo = mongoClient.getDB(Constant4AutoNavi.DefaultSchema);
        DBCollection collection = mongo.getCollection(Constant4AutoNavi.CollectionAutoNaviValidate);
        DBObject data = new BasicDBObject();
        data.put(Constant4AutoNavi.ANValidate_CreateTime,anValidate.getCreateTime());
        data.put(Constant4AutoNavi.ANValidate_ExpireTime,anValidate.getExpireTime());
        data.put(Constant4AutoNavi.ANValidate_ValidateId,anValidate.getValidateId());
        data.put(Constant4AutoNavi.ANValidate_VIN,anValidate.getVin());
        WriteResult writeResult = collection.insert(data);
        anValidate.setId(writeResult.getField(Constant4AutoNavi.Mongo_ID));
        return anValidate;
    }

    //1.更新注册信息中接口调用次数3
    public static void updateCount4ANInfo(String vin,String columnName){
        MongoClient mongoClient = MongoHelper.getClient();
        DB mongo = mongoClient.getDB(Constant4AutoNavi.DefaultSchema);
        DBCollection collection = mongo.getCollection(Constant4AutoNavi.CollectionAutoNaviInfo);
        //db.test.findAndModify({update:{$inc:{'id':1}},query:{"name":"roker"},new:false});
        DBObject updateObject = new BasicDBObject();
        updateObject.put("$inc",new BasicDBObject(columnName,1));
        DBObject queryObject = new BasicDBObject(Constant4AutoNavi.ANInfo_VIN,vin);
        collection.findAndModify(queryObject,updateObject);
    }
}
