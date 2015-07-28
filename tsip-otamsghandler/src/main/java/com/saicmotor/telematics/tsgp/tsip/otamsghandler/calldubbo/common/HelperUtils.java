package com.saicmotor.telematics.tsgp.tsip.otamsghandler.calldubbo.common;

import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.AdapterHelper;

/**
 * Created by Administrator on 2015/7/28.
 */
public class HelperUtils {

    /**
     * 编码请求对象为字符串
     * @param platform
     * @param version
     * @param requestObject
     * @return
     */
    public static String changeObj2String(String platform, String version, Object requestObject) {
        return new String(AdapterHelper.adapterGetBytesData(platform, version, requestObject));
    }
}
