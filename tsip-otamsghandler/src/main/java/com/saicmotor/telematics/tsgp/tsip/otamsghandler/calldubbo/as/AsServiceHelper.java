package com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.as;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.framework.core.exception.ApiException;
import com.saicmotor.telematics.tsgp.as.api.IApplicationService;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.ServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2015/7/28.
 */
@Service
public class AsServiceHelper implements ServiceHelper {

    @Override
    public String callDubboService(RequestContext context) throws ApiException {
        String plf = context.getPlatform();
        String source = context.getSource();
        IApplicationService service = (IApplicationService) SpringContext.getInstance().getBean("asService");
        String requestBack = service.execute(plf,source);
        return requestBack;
    }
}
