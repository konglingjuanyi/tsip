package com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.roadbook;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.framework.core.common.dto.RequestBodyDto;
import com.saicmotor.telematics.framework.core.exception.ApiException;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.dispatcher.AVN_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.roadbook.v1_1.entity.roadbook.*;
import com.saicmotor.telematics.tsgp.sp.roadbook.api.IRoadBookBizService;
import com.saicmotor.telematics.tsgp.sp.roadbook.dto.QueryValue;
import com.saicmotor.telematics.tsgp.sp.roadbook.dto.req.RoadBookSendToCarReq;
import com.saicmotor.telematics.tsgp.sp.roadbook.dto.resp.RoadBookDetailResp;
import com.saicmotor.telematics.tsgp.sp.roadbook.dto.resp.RoadBookQueryResp;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.ServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common.HelperUtils;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common.RoadbookServiceEnum;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.List;

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
        RequestBodyDto reqBodyDto = new RequestBodyDto();
        reqBodyDto.setVin(vin);
        reqBodyDto.setUid(uid);
        reqBodyDto.setApplicationID(aid);
        reqBodyDto.setToken(token);
        reqBodyDto.setPlatformID(pid);

        IRoadBookBizService roadBookBizService = (IRoadBookBizService) SpringContext.getInstance().getBean("roadBookBizService");
        //路书搜索
        if(RoadbookServiceEnum.QUERY.getAid().equals(aid)) {
            //对传递过来的参数进行解码
            RoadBookQueryReq req = (RoadBookQueryReq) decoder.decode(RoadBookQueryReq.class);
            com.saicmotor.telematics.tsgp.sp.roadbook.dto.req.RoadBookQueryReq roadBookQueryReq = new com.saicmotor.telematics.tsgp.sp.roadbook.dto.req.RoadBookQueryReq();
            Collection authNames = req.getAuthorNameList();
            roadBookQueryReq.setAuthorNameList((List<QueryValue>) authNames);
            Collection departNames = req.getDepartNameList();
            roadBookQueryReq.setDepartNameList((List<QueryValue>) departNames);
            Collection destNames = req.getDestNameList();
            roadBookQueryReq.setDestNameList((List<QueryValue>) destNames);
            roadBookQueryReq.setEndNumber(req.getStartEndNumber().getEndNumber());
            Collection keyword = req.getKeyword();
            roadBookQueryReq.setKeyword((List<QueryValue>) keyword);
            Collection passNames = req.getPassNameList();
            roadBookQueryReq.setPassNameList((List<QueryValue>) passNames);
            Collection poiNames = req.getPoiNameList();
            roadBookQueryReq.setPoiNameList((List<QueryValue>) poiNames);
            ReqType rt = req.getReqType();
            com.saicmotor.telematics.tsgp.sp.roadbook.dto.ReqType reqType = new com.saicmotor.telematics.tsgp.sp.roadbook.dto.ReqType();
            reqType.setIntegerForm(rt.getIntegerForm());
//            reqType.setValue(rt.getValue());
            roadBookQueryReq.setReqType(reqType);
            roadBookQueryReq.setRoadBookName(req.getRoadBookName());
            roadBookQueryReq.setStartNumber(req.getStartEndNumber().getStartNumber());
            RoadBookQueryResp resp = roadBookBizService.queryRoadBook(roadBookQueryReq,reqBodyDto);
            request = HelperUtils.enCode_AVN_OTARequest(resp,request);
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), request);
        }
        if(RoadbookServiceEnum.DETAIL.getAid().equals(context.getAid())) {
            //将传递过来的参数进行解码
            RoadBookDetailReq roadBookDetailReq = (RoadBookDetailReq) decoder.decode(RoadBookDetailReq.class);
            com.saicmotor.telematics.tsgp.sp.roadbook.dto.req.RoadBookDetailReq req = new com.saicmotor.telematics.tsgp.sp.roadbook.dto.req.RoadBookDetailReq();
            req.setEndNumber(roadBookDetailReq.getStartEndNumber().getEndNumber());
            req.setRoadBookId(roadBookDetailReq.getRbID());
            req.setStartNumber(roadBookDetailReq.getStartEndNumber().getStartNumber());
            RoadBookDetailResp resp = roadBookBizService.queryRoadBookDetail(req,reqBodyDto);
            request = HelperUtils.enCode_AVN_OTARequest(resp,request);
            //请求对象编码为字符串
            requestBack = HelperUtils.changeObj2String(RequestContext.getContext().getPlatform(), RequestContext.getContext().getClientVersion(), request);

        }
        if(RoadbookServiceEnum.COMMENTRECEIVE.getAid().equals(context.getAid())) {
            //解码传递过来的参数
            RoadBookCommentReq roadBookCommentReq = (RoadBookCommentReq) decoder.decode(RoadBookCommentReq.class);

        }
        if(RoadbookServiceEnum.WEATHERINFOMATION.getAid().equals(context.getAid())) {
            //解码传递过来的参数
            RBWeatherInformationReq rbWeatherInformationReq = (RBWeatherInformationReq) decoder.decode(RBWeatherInformationReq.class);
        }
        return requestBack;
    }
}
