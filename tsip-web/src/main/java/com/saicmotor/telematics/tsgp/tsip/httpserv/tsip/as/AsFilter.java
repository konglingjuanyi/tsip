package com.saicmotor.telematics.tsgp.tsip.httpserv.tsip.as;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import com.saicmotor.telematics.tsgp.tsip.httpserv.tsip.TFilter;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.service.ApplicationServiceImpl;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.service.IApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2015/7/17.
 */
public class AsFilter extends TFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsFilter.class);

    private static String  CHARSET = "UTF-8";


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
     * call dubbo service
     * @param res
     * @return
     */
    String callAppService(HttpServletRequest res){
        String code = processRequest(res);
        IApplicationService applicationService = SpringContext.getInstance().getBean(ApplicationServiceImpl.class);
        String result = applicationService.execute(Cfg.PL_STR_MAP.get("014"), code, "014");
        return result;
    }

    @Override
    public void destroy() {

    }
}
