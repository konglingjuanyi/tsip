package com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.vp;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.framework.core.exception.ApiException;
import com.saicmotor.telematics.tsgp.sp.vp.api.IApplicationService;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.ServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common.DubboHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;

/**
 * Created by Administrator on 2015/7/28.
 */
public class VpServiceHelper implements ServiceHelper {
    @Override
    public String callDubboService(RequestContext context) throws ApiException {
        String aid = context.getAid();
        String plf = context.getPlatform();
        String source = context.getSource();
        String requestBack = null;
        if(DubboHelper.vpList_v1.contains(aid)){
            IApplicationService service = (IApplicationService) SpringContext.getInstance().getBean("vpAppServiceV1");
            requestBack = service.execute(plf,source);
        }
        if(DubboHelper.vpList_v2.contains(aid)){
            IApplicationService service = (IApplicationService) SpringContext.getInstance().getBean("vpAppServiceV2");
            requestBack = service.execute(plf,source);
        }
        return requestBack;
    }
}
