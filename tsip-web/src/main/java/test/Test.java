package test;

import com.saicmotor.telematics.framework.core.common.SpringContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Created by Administrator on 2015/7/15.
 */
public class Test {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new FileSystemXmlApplicationContext("applicationContext.xml");
        SpringContext.getInstance().init(applicationContext);
        System.out.printf(SpringContext.getInstance().getProperty("mongoclient.host"));
    }
}
