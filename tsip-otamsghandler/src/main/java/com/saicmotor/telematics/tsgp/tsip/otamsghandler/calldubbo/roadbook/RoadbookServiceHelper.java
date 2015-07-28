package com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.roadbook;

import com.saicmotor.telematics.framework.core.exception.ApiException;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.dispatcher.AVN_OTARequest;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.ServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common.RoadbookServiceEnum;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

/**
 * Created by Administrator on 2015/7/28.
 */
@Service
public class RoadbookServiceHelper implements ServiceHelper {

    @Override
    public String callDubboService(RequestContext context) throws ApiException{
        String requestBack = null;
        AVN_OTARequest request = (AVN_OTARequest) context.getRequestObject();
        byte[] appBytes = request.getApplicationData();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(appBytes);
        OTADecoder decoder = new OTADecoder(inputStream);

        //路书搜索
        if(RoadbookServiceEnum.QUERY.getAid().equals(context.getAid())) {
//            IGetAvnActivationCodeService getAvnActivationCodeService = (IGetAvnActivationCodeService) SpringContext.getInstance().getBean("getAvnActivationCodeService");
//            com.zxq.iov.cloud.sp.mds.api.dto.ActivationCodeReq req = new com.zxq.iov.cloud.sp.mds.api.dto.ActivationCodeReq();
//            req.setVin(request.getDispatcherBody().getVin());
            //请求对象编码为字符串
//            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), getAvnActivationCodeService.getAvnActivationCode(req));
        }
        if(RoadbookServiceEnum.DETAIL.getAid().equals(context.getAid())) {

        }
        if(RoadbookServiceEnum.COMMENTRECEIVE.getAid().equals(context.getAid())) {

        }
        if(RoadbookServiceEnum.ADDDELETECOMMENT.getAid().equals(context.getAid())) {

        }
        if(RoadbookServiceEnum.SENDTOCAR.getAid().equals(context.getAid())) {

        }
        if(RoadbookServiceEnum.FAVORITEQUERY.getAid().equals(context.getAid())) {

        }
        if(RoadbookServiceEnum.ADDTOFAVORITE.getAid().equals(context.getAid())) {

        }
        if(RoadbookServiceEnum.DELETEFAVORITE.getAid().equals(context.getAid())) {

        }
        if(RoadbookServiceEnum.WEATHERINFOMATION.getAid().equals(context.getAid())) {

        }
        if(RoadbookServiceEnum.DETAILBATCH.getAid().equals(context.getAid())) {

        }
        return requestBack;
    }
}
