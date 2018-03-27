package chars.config;


import chars.handler.CharWebsocketHandler;
import chars.interceptor.CharHandShakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @Author: chenwj
 * @Description:
 * @Date: 2018/3/21
 */
@Component
@EnableWebSocket
public class WebsocketConfigure extends WebMvcConfigurerAdapter implements WebSocketConfigurer{

    @Autowired
    private CharWebsocketHandler handler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(handler, "/ws").setAllowedOrigins("*").addInterceptors(new CharHandShakeInterceptor());
        webSocketHandlerRegistry.addHandler(handler, "/ws/sockjs").setAllowedOrigins("*").addInterceptors(new CharHandShakeInterceptor()).withSockJS();
    }

}
