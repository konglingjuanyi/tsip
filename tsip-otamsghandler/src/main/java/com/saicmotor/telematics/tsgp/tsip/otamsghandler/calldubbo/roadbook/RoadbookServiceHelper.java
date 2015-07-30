package com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.roadbook;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.framework.core.common.dto.RequestBodyDto;
import com.saicmotor.telematics.framework.core.exception.ApiException;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.dispatcher.AVN_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.roadbook.ReqType;
import com.saicmotor.telematics.tsgp.otaadapter.roadbook.v1_1.entity.roadbook.RoadBookQueryReq;
import com.saicmotor.telematics.tsgp.sp.roadbook.api.IRoadBookBizService;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.ServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common.HelperUtils;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common.RoadbookServiceEnum;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

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

        String aid = context.getAid();
        String uid = context.getUid();
        String vin = context.getVin();
        String token = context.getToken();
        String  pid = context.getPlatform();

        IRoadBookBizService roadBookBizService = (IRoadBookBizService) SpringContext.getInstance().getBean("roadBookBizService");
        //路书搜索
        if(RoadbookServiceEnum.QUERY.getAid().equals(context.getAid())) {
            //对传递过来的参数进行解码
            RoadBookQueryReq req = (RoadBookQueryReq) decoder.decode(RoadBookQueryReq.class);
            com.saicmotor.telematics.tsgp.sp.roadbook.dto.req.RoadBookQueryReq roadBookQueryReq = new com.saicmotor.telematics.tsgp.sp.roadbook.dto.req.RoadBookQueryReq();
//            roadBookQueryReq.setAuthorNameList();
//            roadBookQueryReq.setDepartNameList();
//            roadBookQueryReq.setDestNameList();
            roadBookQueryReq.setEndNumber(req.getStartEndNumber().getEndNumber());
//            roadBookQueryReq.setKeyword(req.getKeyword());
//            roadBookQueryReq.setPassNameList();
//            roadBookQueryReq.setPoiNameList();
//            roadBookQueryReq.setReqType();
            roadBookQueryReq.setRoadBookName(req.getRoadBookName());
            roadBookQueryReq.setStartNumber(req.getStartEndNumber().getStartNumber());
            RequestBodyDto reqBodyDto = new RequestBodyDto();
            reqBodyDto.setVin(vin);
            reqBodyDto.setUid(uid);
            reqBodyDto.setApplicationID(aid);
            reqBodyDto.setToken(token);
            reqBodyDto.setPlatformID(pid);
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), roadBookBizService.queryRoadBook(roadBookQueryReq, reqBodyDto));
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
