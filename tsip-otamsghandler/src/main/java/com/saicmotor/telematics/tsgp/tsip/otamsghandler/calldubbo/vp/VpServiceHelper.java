package com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.vp;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.framework.core.exception.ApiException;
import com.saicmotor.telematics.tsgp.sp.vp.api.IApplicationService;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.ServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common.DubboHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2015/7/28.
 */
@Service
public class VpServiceHelper implements ServiceHelper {
    @Override
    public String callDubboService(RequestContext context) throws ApiException {
        String aid = context.getAid();
        String plf = context.getPlatform();
        String source = context.getSource();
        String requestBack = null;
        if(DubboHelper.isVp1(aid)){
            IApplicationService service = (IApplicationService) SpringContext.getInstance().getBean("vpAppServiceV1");
            requestBack = service.execute(plf,source);
        }
        if(DubboHelper.isVp2(aid)){
            IApplicationService service = (IApplicationService) SpringContext.getInstance().getBean("vpAppServiceV2");
            requestBack = service.execute(plf,source);
        }
        return requestBack;
    }
}
