package com.saicmotor.telematics.tsgp.tsip.httpserv.tsip.mp;

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
public class MpFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MpFilter.class);

    private static String  CHARSET = "UTF-8";

    String processRequest(HttpServletRequest request) {
        try {
            request.setCharacterEncoding("iso-8859-1");
            int size = request.getContentLength();
//            System.out.println(size);
            InputStream is = request.getInputStream();
            byte[] reqBodyBytes = readBytes(is, size);
            String res = new String(reqBodyBytes);
            return res;
//            System.out.println(res);
//            response.setContentType("text/html;charset=UTF-8");
//            response.setCharacterEncoding("UTF-8");
//            response.getOutputStream().write(res.getBytes("utf-8"));
//            response.flushBuffer();
        } catch (Exception e) {
            return null;
        }
    }

    byte[] readBytes(InputStream is, int contentLen) {
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

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest res =(HttpServletRequest)servletRequest;
        HttpServletResponse rep =(HttpServletResponse)servletResponse;
        String resultString = callAppService(res);
        writeResult(CHARSET,resultString,rep);
        rep.getOutputStream().flush();
        rep.getOutputStream().close();
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
            rep.setCharacterEncoding("UTF-8");
            if(StringUtils.isNotEmpty(result))
                rep.getOutputStream().write(result.getBytes(charset));
            rep.flushBuffer();
        } catch (IOException e) {
            throw new HTTPServException("向客户端写回信息错误" + e, e);
        }

//            try {
//                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                // 将结果写入缓冲区
//                responseWriter.writeResult(out, result, exception, charset);
//                final byte[] bytes = out.toByteArray();
//                LOGGER.debug("BaseServiceExecutor.return bytes:"+new String(bytes));
//                if (bytes.length > 0) {
//                    // 将结果写回客户端
//                    rep.setCharacterEncoding(charset);
//                    //使用text/html作为Json数据的content-type会避免数据返回到客户端
//                    //时被加上<p>data</p>标签的情况。
//                    rep.setContentType("text/html" + ";charset=" + charset);
//                    rep.getOutputStream().write(bytes);
//                }
//            } catch (Exception e) {
//                throw new HTTPServException("向客户端写回信息错误" + e, e);
//            }
    }

    /**
     * call dubbo service
     * @param res
     * @return
     */
    String callAppService(HttpServletRequest res){
        String code = processRequest(res);
        IApplicationService applicationService = SpringContext.getInstance().getBean(ApplicationServiceImpl.class);
        String result = applicationService.execute(Cfg.PL_STR_MAP.get("004"), code, "004");
        return result;
    }

    @Override
    public void destroy() {

    }
}
