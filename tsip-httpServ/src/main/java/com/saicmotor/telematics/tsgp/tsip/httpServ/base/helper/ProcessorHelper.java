/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.base.helper;

import com.saicmotor.telematics.framework.core.common.ModelMap;
import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.reader.ParameterPairContentReader;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.reader.RequestContentReader;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.reader.RequestContentReaderRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;


/**
 * Json请求处理器的帮助类
 */
@SuppressWarnings("unchecked")
public class ProcessorHelper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorHelper.class);
	
	private String charset;
	
	public void setCharset(String charset) {
		this.charset = charset;
	}



	/**
	 * 填充参数
	 */
	public void fillParameters(HttpServletRequest request, ModelMap params) {
		LOGGER.debug("JsonPreocessorHelper.fillParameters:ContentType:" + RequestHelper.getContentType(request));
//		RequestContentReaderRegister rc = new RequestContentReaderRegister();
//		RequestContentReaderRegister rcrr = SpringContext.getInstance().getBean(RequestContentReaderRegister.class);
//		RequestContentReader reader = rcrr.getRequestReader(RequestHelper.getContentType(request));
		RequestContentReader reader = new ParameterPairContentReader();
		if(reader != null){
			reader.readData(request, params, charset);
		}	
	}
}
