/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.base.writer;

import com.saicmotor.telematics.framework.core.common.MimeType;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.ServiceResult;

/**
 * Json格式的数据回写类 
 */
public class ResponseWriter extends AbstractTextResponseWriter {
	
	public String generateTextContent(String result, Throwable e) {
		return ServiceResult.createHttpResult(result, e);
	}

	public String getMimeType() {
		return MimeType.MIME_OF_TEXT_HTML;
	}
}
