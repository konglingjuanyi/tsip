package com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo;

import com.saicmotor.telematics.framework.core.exception.ApiException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;

/**
 * Created by Administrator on 2015/7/28.
 */
public interface ServiceHelper {

    public String callDubboService(RequestContext context) throws ApiException;
}
