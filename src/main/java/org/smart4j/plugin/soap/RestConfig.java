package org.smart4j.plugin.soap;

import org.smart4j.framework.core.ConfigHelper;
import org.smart4j.framework.util.StringUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/5/4 0004.
 */
public class RestConfig {
    public static boolean isLog(){
        return ConfigHelper.getBoolean(RestConstant.LOG);
    }
    public static boolean isJsonp(){
        return ConfigHelper.getBoolean(RestConstant.JSONP_FUNCTION);
    }
    public static String getJsonpFunction(){
        return ConfigHelper.getString(RestConstant.JSONP_FUNCTION);
    }
    public static boolean isCors(){
        return ConfigHelper.getBoolean(RestConstant.CORS);
    }
    public static List<String> getCorsOriginList(){
        String corsOrigin = ConfigHelper.getString(RestConstant.CORS_ORIGIN);
        return Arrays.asList(StringUtil.splitString(corsOrigin,","));
    }
}
