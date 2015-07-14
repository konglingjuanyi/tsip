<%@ page import="java.net.*,java.io.*,org.w3c.dom.*,java.util.*,javax.xml.parsers.*" %>
<%@ page import="com.saicmotor.telematics.tsgp.tsip.otamsghandler.configure.Cfg" %>
<%@ page import="com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper" %>
<%@ page import="com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.ErrorMessageHelper" %>

<%
    int errorCode=26175;

    if(errorCode>10000 && Cfg.PLATFORM_NEED_CONVERT_ERROR_CODE.contains("005")){
        errorCode = Integer.valueOf(ErrorMessageHelper.getErrorMapping(String.valueOf(errorCode),"223"));
    }
    out.println("errorCode:"+errorCode);
  
	
%>

