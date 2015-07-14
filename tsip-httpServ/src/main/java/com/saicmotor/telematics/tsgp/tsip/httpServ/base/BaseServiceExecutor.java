/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.base;

import com.saicmotor.telematics.framework.core.common.MimeType;
import com.saicmotor.telematics.framework.core.common.ModelMap;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.exception.HTTPServException;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.writer.IResponseWriter;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.writer.ResponseWriterRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * 根据Json请求参数运行服务方法
 */
@SuppressWarnings("unchecked")
abstract  public class BaseServiceExecutor implements IBaseServiceExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseServiceExecutor.class);

	protected HttpServletRequest request = ContextManager.getRequest();

	protected HttpServletResponse response = ContextManager.getResponse();


	public BaseServiceExecutor() {

	}

    abstract protected String invokeService(String message);

	/**
	 * 根据服务的方法类型，执行相应的业务方法
	 */
	public void execute(String charset) {
		Object result = null;

		Exception exception = null;

        LOGGER.debug("Session ID: "+request.getSession().getId());
		ModelMap modelMap = ContextManager.getModelMap();

        String message = (String)modelMap.get(ModelMap.RPC_ARGS_KEY);

        LOGGER.debug("Get message In BaseServiceExecutor:"+message);

        //todo: 调用安全模块, 解码，认证

        try{
            String resultString = invokeService(message);
            writeResult(charset,resultString,null);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        }catch(Exception e){
            //构造错误返回消息
            LOGGER.debug("返回", e);
            exception = e;
        }

        // 向客户端写回异常结果
        if (exception != null) {
            Throwable throwable = exception;
            writeResult(charset,null,throwable);
        }

    }

	public void writeResult(String charset, String result,Throwable exception) {
		String mimeType = MimeType.MIME_OF_TEXT_HTML;

		// 向客户端写回结果数据	
		IResponseWriter responseWriter = ResponseWriterRegister.getInstance().getResponseWriter(mimeType);

		if (responseWriter != null) {
			try {				
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				// 将结果写入缓冲区
				responseWriter.writeResult(out, result, exception, charset);
				final byte[] bytes = out.toByteArray();
                LOGGER.debug("BaseServiceExecutor.return bytes:"+new String(bytes));
				if (bytes.length > 0) {
					// 将结果写回客户端
					response.setCharacterEncoding(charset);
					//使用text/html作为Json数据的content-type会避免数据返回到客户端
					//时被加上<p>data</p>标签的情况。
					response.setContentType("text/html" + ";charset=" + charset);

					response.getOutputStream().write(bytes);
				}

			} catch (Exception e) {
				throw new HTTPServException("向客户端写回信息错误" + e, e);
			}
		}
	}	 
	
}
