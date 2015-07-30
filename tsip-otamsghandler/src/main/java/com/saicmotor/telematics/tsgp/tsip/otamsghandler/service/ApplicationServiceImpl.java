/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.otamsghandler.service;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.callback.OTACallback;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.ServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common.DubboHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.mds.MdsServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.roadbook.RoadbookServiceHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.MessageTemplate;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.context.RequestContext;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ErrorMessageHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ExceptionHandler;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ProtocolException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.LogHelper;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.MessageHelper;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * OTA请求控制类，负责响应客户端发出的请求
 * @author jozbt
 * @author cqzzl
 */
@Service
public class ApplicationServiceImpl implements IApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    public String execute(String from, String source, String platform) {
        LOGGER.debug("enter ApplicationService.execute()...");
        long start = System.currentTimeMillis();
        String requestBack = null;
        RequestContext context = null;
        try {
//            try{
                context = RequestContext.initContext("001", source);
//            }catch (Exception e){
//            }
//            try{
//                context = RequestContext.initContext("004", source);
//            }catch (Exception e){
//            }
//            try{
//                context = RequestContext.initContext("005", source);
//            }catch (Exception e){
//            }
//            try{
//                context = RequestContext.initContext("007", source);
//            }catch (Exception e){
//            }
//            try{
//                context = RequestContext.initContext("013", source);
//            }catch (Exception e){
//            }
//            try{
//                context = RequestContext.initContext("014", source);
//            }catch (Exception e){
//            }
//            RequestContext context = RequestContext.initContext(platform, source);


            if(!"113".equals(context.getAid())){
            //记录请求日志
                //LogHelper.reportDataInfo(context.getAid(), context.getMid(), context.getVin(), source);
                LogHelper.info(LOGGER, context.getAid(), context.getMid(), context.getVin(), context.getUid(), source, from, context.getToken());
            }

            MessageTemplate messageTemplate = MessageHelper.getMessage(context.getAid(), context.getMid());
            if (messageTemplate == null) {
                throw new ProtocolException("没有配置callback. AID:" + context.getAid() + ",MID:" + context.getMid());
            }

            int authResultCode = 0;
            Integer testFlag = (Integer) (AdapterHelper.getProperty(context.getRequestObject(), "dispatcherBody.testFlag") == null ? 0 : AdapterHelper.getProperty(context.getRequestObject(), "dispatcherBody.testFlag"));
            if (testFlag != 1) {//如果非测试请求，则调用验证服务进行权限验证
                //TODO call dubbo to do 权限验证
                AuthenticationService authenticationService = SpringContext.getInstance().getBean(AuthenticationService.class);
                authResultCode = authenticationService.check(context);
            }
            //验证失败
            if (authResultCode != 0) {
                Object request = context.getRequestObject();
                AdapterHelper.setProperty(request, "applicationData", new byte[0]);
                AdapterHelper.setProperty(request, "dispatcherBody.result", authResultCode);
                //设置errorMessage
                String errorMessage = ErrorMessageHelper.getErrorMessage(authResultCode);
                if (errorMessage != null && !Cfg.PLATFORM_TBOX.equals(RequestContext.getContext().getPlatform())) {
                    PropertyUtils.setNestedProperty(request, "dispatcherBody.errorMessage", errorMessage.getBytes("utf-8"));
                }
                requestBack = new String(AdapterHelper.adapterGetBytesData(platform, context.getClientVersion(), context.getRequestObject()));
            }
            //验证通过
            else {
                //根据各种条件组合获取protocol，通过protocol获取callback
                //传protocol入callback执行invoke
                MessageTemplate.Protocol protocol = messageTemplate.findProtocol(context);
                OTACallback callback = protocol.getCallback();
                //调用服务时将token设置为新token
                if((!"201".equals(context.getAid())) && (!Cfg.PLATFORM_TBOX.equals(RequestContext.getContext().getPlatform()))){
                    AdapterHelper.setProperty(context.getRequestObject(), "dispatcherBody.token", context.getToken());
                }
                ServiceHelper serviceHelper = null;
                //CALL MDS DUBBO SERVICE
                //init serviceHelper
                if(DubboHelper.aidMdsList.contains(context.getAid())){
                     serviceHelper = (ServiceHelper) SpringContext.getInstance().getBean(MdsServiceHelper.class);
                }
                if(DubboHelper.aidRoadBookList.contains(context.getAid())){
                     serviceHelper = (ServiceHelper) SpringContext.getInstance().getBean(RoadbookServiceHelper.class);
                }

                if(null != serviceHelper){
                    requestBack = serviceHelper.callDubboService(context);
                }else {
                    requestBack = callback.invoke(protocol, context.getRequestObject());
                }

            }
            if(!"113".equals(context.getAid())){
                //记录返回的日志
                LogHelper.returnResultInfo(context.getAid(), context.getMid(), context.getVin(), context.getUid(), requestBack, "TSIP", "", "", from, context.getToken());
            }
        } catch (Exception e) {
            requestBack = ExceptionHandler.processException(LOGGER,e);
        } finally {
            RequestContext.clear();
            LOGGER.debug("exit ApplicationService.execute(), spend time:" + (System.currentTimeMillis() - start));
        }
        return requestBack;
    }
}


