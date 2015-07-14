/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.base.writer;

import com.saicmotor.telematics.tsgp.tsip.httpserv.base.ContextManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * 文本格式的数据回写抽象类
 */
public abstract class AbstractTextResponseWriter implements IResponseWriter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTextResponseWriter.class);
	
	protected HttpServletRequest request = ContextManager.getRequest();
 
	protected HttpServletResponse response = ContextManager.getResponse();

	/**
	 * 回写结果
	 */
	public void writeResult(ByteArrayOutputStream out, String result, Throwable exception,String charset) {

		String textContent = this.generateTextContent(result,exception);
		LOGGER.debug("Service Response Result:"+textContent);
		if(textContent == null){
			textContent = "";
		}	
		try {
			 out.write(textContent.getBytes(charset));
		} catch (IOException e) {
			LOGGER.error("向客户端写回数据错误",e);
		}
	}

	protected abstract String generateTextContent(String result,Throwable e);
}
