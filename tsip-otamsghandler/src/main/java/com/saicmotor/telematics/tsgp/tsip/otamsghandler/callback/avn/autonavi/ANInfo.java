package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.avn.autonavi;

/**
 * Created with IntelliJ IDEA.
 * User: yicni
 * Date: 14-12-25
 * Time: 下午3:45
 * To change this template use File | Settings | File Templates.
 */
public class ANInfo {
//    _id,vin,regTime,duration,firstTime,status,loginCount,batchCount,trafficCount,eventTypeCount,locationTypeCount;
    Object id;
    String vin;
    Long regTime;
    Long duration;
    String status;
    Long loginCount;
    Long batchCount;
    Long trafficCount;
    Long trafficCount2;
    Long eventTypeCount;
    Long locationTypeCount;
    Long interCityTypeCount;
    Long interCityTypeCount2;

    public Long getTrafficCount2() {
        return trafficCount2;
    }

    public void setTrafficCount2(Long trafficCount2) {
        this.trafficCount2 = trafficCount2;
    }

    public Long getInterCityTypeCount2() {
        return interCityTypeCount2;
    }

    public void setInterCityTypeCount2(Long interCityTypeCount2) {
        this.interCityTypeCount2 = interCityTypeCount2;
    }

    public Long getInterCityTypeCount() {
        return interCityTypeCount;
    }

    public void setInterCityTypeCount(Long interCityTypeCount) {
        this.interCityTypeCount = interCityTypeCount;
    }

    public Long getEventTypeCount() {
        return eventTypeCount;
    }

    public void setEventTypeCount(Long eventTypeCount) {
        this.eventTypeCount = eventTypeCount;
    }

    public Long getLocationTypeCount() {
        return locationTypeCount;
    }

    public void setLocationTypeCount(Long locationTypeCount) {
        this.locationTypeCount = locationTypeCount;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Long getRegTime() {
        return regTime;
    }

    public void setRegTime(Long regTime) {
        this.regTime = regTime;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Long loginCount) {
        this.loginCount = loginCount;
    }

    public Long getBatchCount() {
        return batchCount;
    }

    public void setBatchCount(Long batchCount) {
        this.batchCount = batchCount;
    }

    public Long getTrafficCount() {
        return trafficCount;
    }

    public void setTrafficCount(Long trafficCount) {
        this.trafficCount = trafficCount;
    }
}
