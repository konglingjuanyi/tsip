<%@ page import="java.net.*,java.io.*,org.w3c.dom.*,java.util.*,javax.xml.parsers.*" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    HttpURLConnection conn = null;
    URL url1 = new URL("http://550.webservice.pateo.com.cn/weather/CityWeatherInfomation/");
    InputStream is = null;
    conn = (HttpURLConnection)url1.openConnection();
    conn.setRequestProperty("Content-Type","text/xml;charset=utf-8");
    conn.setDoInput(true);
    conn.setUseCaches(false);
    conn.setDoOutput(true);
    conn.setConnectTimeout(60000);
    conn.setReadTimeout(60000);

    StringBuffer sb = new StringBuffer();
    sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">");
    sb.append("	<soapenv:Header/>");
    sb.append("		<soapenv:Body>");
    sb.append("			<param>00DF110005600D08C183060C183060C183060C183060C183060C183060C183060C183060C183060C583060C183060C183060C18088888888B328CC6B4E62C99B46AD9BB872C593368D5B27D4E6E4808000800000038000200400C58B062C5A8BA01B10669944253BC32223ECAEA7ED9</param>");
    sb.append("		</soapenv:Body>");
    //sb.append("	</soapenv:Header>");
    sb.append("</soapenv:Envelope>");

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String startTime = simpleDateFormat.format(new Date());

    OutputStream os = conn.getOutputStream();
    os.write(sb.toString().getBytes());
    os.close();
    is = conn.getInputStream();
    if(conn.getResponseCode() != 200){
        throw new Exception(conn.getResponseMessage());
    }
    BufferedReader in = new BufferedReader(new InputStreamReader(is,"UTF-8"));
    StringBuffer str = new StringBuffer();
    String inputLine = "";
    while((inputLine = in.readLine()) != null){
        str.append(inputLine);
    }
    String endTime = simpleDateFormat.format(new Date());
    response.setCharacterEncoding("UTF-8");
    out.println("startTime " + startTime + "<br/>");
    out.println("endTime   " + endTime + "<br/>");
    out.println("return "+ str.length() + "<br/>");
    out.println(str.toString());

%>

