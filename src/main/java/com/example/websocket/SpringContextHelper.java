package com.example.websocket;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.websocket.server.ServerEndpointConfig;

/**
 * Description: WebSocket无法通过autowired自动注入bean，需要通过容器手动获取
 * Date: 2018-12-17
 */
@Component
public class SpringContextHelper extends ServerEndpointConfig.Configurator implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringContextHelper.applicationContext == null){
            SpringContextHelper.applicationContext  = applicationContext;

        }
    }
    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //通过name获取 Bean.
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);

    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }

}
