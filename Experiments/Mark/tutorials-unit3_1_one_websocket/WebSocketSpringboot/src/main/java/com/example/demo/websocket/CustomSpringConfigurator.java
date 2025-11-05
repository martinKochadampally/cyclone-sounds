package com.example.demo.websocket;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import jakarta.websocket.server.ServerEndpointConfig;

/**
 * This class is the "bridge" that allows our Tomcat-managed @ServerEndpoint
 * to access Spring-managed beans (like the database repository).
 */
@Component
public class CustomSpringConfigurator extends ServerEndpointConfig.Configurator implements ApplicationContextAware {

    private static volatile BeanFactory context;

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        // Get the bean from the Spring Context
        return context.getBean(endpointClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CustomSpringConfigurator.context = applicationContext;
    }
}