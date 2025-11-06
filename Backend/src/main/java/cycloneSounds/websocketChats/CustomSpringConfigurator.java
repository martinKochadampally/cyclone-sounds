package cycloneSounds.websocketChats;

import cycloneSounds.chat.ChatMessageService;
import cycloneSounds.chat.ChatSocket;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import jakarta.websocket.server.ServerEndpointConfig;

@Component
public class CustomSpringConfigurator extends ServerEndpointConfig.Configurator implements ApplicationContextAware {

    private static volatile ApplicationContext context;

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        T endpoint = super.getEndpointInstance(endpointClass);
        if (endpoint instanceof ChatSocket) {
            ((ChatSocket) endpoint).setChatMessageService(context.getBean(ChatMessageService.class));
        }
        return endpoint;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CustomSpringConfigurator.context = applicationContext;
    }
}