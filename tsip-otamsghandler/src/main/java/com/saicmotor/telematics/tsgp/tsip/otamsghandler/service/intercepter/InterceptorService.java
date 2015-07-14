/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.otamsghandler.service.intercepter;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 拦截器服务
 * @author xujunjie
 */
@Service
public class InterceptorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterceptorService.class);

    public static Map<String,IInterceptor> interceptorMap = new HashMap<String, IInterceptor>();
    static{
//        Map<String, String> map = GuiceContext.getInstance().getConfig().getProperties().get("Interceptor");

//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            String className = entry.getValue();
            String className = SpringContext.getInstance().getProperty("Interceptor.212_1.1");
            try {
                Class<?> clazz = Class.forName(className);
                IInterceptor interceptor = (IInterceptor) SpringContext.getInstance().getBean(clazz);
                interceptorMap.put("Interceptor.212_1.1", interceptor);
//                interceptorMap.put("212_1.1", interceptor);
//                interceptorMap.put(entry.getKey(), interceptor);初始版本代码
            } catch (Exception e) {
                LOGGER.error("加载拦截器类失败", e);
//                continue;
            }
//        }
    }



    public IInterceptor getInterceptor(String aid){
        return interceptorMap.get(aid);
    }


    public  String convertAid(String aid,String version,byte[] bytes){
        IInterceptor interceptor = getInterceptor(aid+"_"+version);
        if(interceptor != null ){
            return interceptor.change(aid,bytes);
        }
        return aid;
    }

}
