package com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.avn.autonavi;

/**
 * Created with IntelliJ IDEA.
 * User: yicni
 * Date: 14-12-25
 * Time: 下午3:49
 * To change this template use File | Settings | File Templates.
 */
public class ANValidate {
    //_id,vin,validateId,expireTime，createTime
    Object id;
    String vin;
    String validateId;
    Long expireTime;
    Long createTime;

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

    public String getValidateId() {
        return validateId;
    }

    public void setValidateId(String validateId) {
        this.validateId = validateId;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
