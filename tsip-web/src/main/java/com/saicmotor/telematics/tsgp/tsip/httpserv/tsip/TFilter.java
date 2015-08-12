package com.saicmotor.telematics.tsgp.tsip.httpserv.tsip;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.exception.HTTPServException;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.service.ApplicationServiceImpl;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.service.IApplicationService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2015/7/17.
 */
public abstract class TFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TFilter.class);

    private static String  CHARSET = "UTF-8";

    public String processRequest(HttpServletRequest request) {
        try {
            request.setCharacterEncoding("iso-8859-1");
            int size = request.getContentLength();
            InputStream is = request.getInputStream();
            byte[] reqBodyBytes = readBytes(is, size);
            String res = new String(reqBodyBytes);
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    public byte[] readBytes(InputStream is, int contentLen) {
        if (contentLen > 0) {
            int readLen = 0;

            int readLengthThisTime = 0;

            byte[] message = new byte[contentLen];

            try {

                while (readLen != contentLen) {

                    readLengthThisTime = is.read(message, readLen, contentLen
                            - readLen);

                    if (readLengthThisTime == -1) {// Should not happen.
                        break;
                    }

                    readLen += readLengthThisTime;
                }

                return message;
            } catch (IOException e) {
                // Ignore
                // e.printStackTrace();
            }
        }

        return new byte[] {};
    }

    /**
     * 回写
     * @param charset
     * @param result
     * @param rep
     */
    public void writeResult(String charset, String result,HttpServletResponse rep) {
        try {
            rep.setContentType("text/html;charset=UTF-8");
            rep.setCharacterEncoding(CHARSET);
            if(StringUtils.isNotEmpty(result))
                rep.getOutputStream().write(result.getBytes(charset));
            rep.flushBuffer();
        } catch (IOException e) {
            throw new HTTPServException("向客户端写回信息错误" + e, e);
        }
    }
}
