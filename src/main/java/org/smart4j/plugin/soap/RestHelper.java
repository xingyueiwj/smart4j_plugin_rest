package org.smart4j.plugin.soap;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.jsonp.JsonpInInterceptor;
import org.apache.cxf.jaxrs.provider.jsonp.JsonpPostStreamInterceptor;
import org.apache.cxf.jaxrs.provider.jsonp.JsonpPreStreamInterceptor;
import org.apache.cxf.message.Message;
import org.smart4j.framework.ioc.BeanHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/4 0004.
 */
public class RestHelper {
    private static final List<Object> provideList = new ArrayList<Object>();
    private static final List<Interceptor<? extends Message>> inInterceptorList = new ArrayList<Interceptor<? extends Message>>();
    private static final List<Interceptor<? extends Message>> outInterceptorList = new ArrayList<Interceptor<? extends Message>>();

    static {
        //添加JSON Provider
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider();
        provideList.add(jacksonJsonProvider);
        //添加Logging Interceptor
        if (RestConfig.isLog()){
            LoggingInInterceptor loggingInInterceptor = new LoggingInInterceptor();
            inInterceptorList.add(loggingInInterceptor);
            LoggingOutInterceptor loggingOutInterceptor = new LoggingOutInterceptor();
            outInterceptorList.add(loggingOutInterceptor);
        }
        //添加JSONP Interceptor
        if (RestConfig.isJsonp()){
            JsonpInInterceptor jsonpInInterceptor = new JsonpInInterceptor();
            jsonpInInterceptor.setCallbackParam(RestConfig.getJsonpFunction());
            inInterceptorList.add(jsonpInInterceptor);
            JsonpPreStreamInterceptor jsonpPreStreamInterceptor = new JsonpPreStreamInterceptor();
            outInterceptorList.add(jsonpPreStreamInterceptor);
            JsonpPostStreamInterceptor jsonpPostStreamInterceptor = new JsonpPostStreamInterceptor();
            outInterceptorList.add(jsonpPostStreamInterceptor);
        }
    }
    //发布REST服务
    public static void publishService(String wadl,Class<?> resourceClass){
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setAddress(wadl);
        factory.setResourceClasses(resourceClass);
        factory.setResourceProvider(resourceClass,new SingletonResourceProvider(BeanHelper.getBean(resourceClass)));
        factory.setProviders(provideList);
        factory.setInInterceptors(inInterceptorList);
        factory.setOutFaultInterceptors(outInterceptorList);
        factory.create();
    }
    //创建REST客户端
    public static <T> T createClient(String wadl,Class<? extends T> resourceClass){
        return JAXRSClientFactory.create(wadl,resourceClass,provideList);
    }
}
