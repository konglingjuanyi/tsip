/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.httpserv.base.reader;

import com.saicmotor.telematics.framework.core.common.MimeType;
import com.saicmotor.telematics.framework.core.common.ModelMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;

/**
 * 读取MIME Type为application/json的http请求的数据
 */
public class ContentReader extends ParameterPairContentReader {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContentReader.class);
	
	public void read(HttpServletRequest request, ModelMap params,
			String charset) {	 				
		super.read(request, params, charset);
		try {
			ServletInputStream inputStream = request.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024];
            int size=0;
			while((size = inputStream.read(bytes)) != -1) {
				baos.write(bytes,0,size);
			}

            String content = new String(baos.toByteArray(), charset);
			params.put(ModelMap.RPC_ARGS_KEY, content);
			baos.close();
		} catch (Exception e) {
			LOGGER.error("Http请求数据读取错误",e);
		}
	}
	
	public String getContentType() {
		return MimeType.MIME_OF_TEXT_HTML;
	}

}
  
