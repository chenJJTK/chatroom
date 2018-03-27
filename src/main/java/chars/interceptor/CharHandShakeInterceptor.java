package chars.interceptor;

import chars.handler.CharWebsocketHandler;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: chenwj
 * @Description:
 * @Date: 2018/3/21
 */
public class CharHandShakeInterceptor implements HandshakeInterceptor{

    public static final String WS_NAME = "websocket_name";

    String token_1 = "";

    String name = "";

    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        if (serverHttpRequest instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) serverHttpRequest;
            name = servletServerHttpRequest.getURI().getQuery();
            name = name.substring(name.indexOf("=") + 1);
            map.put("name", name);
            HttpServletRequest request = servletServerHttpRequest.getServletRequest();
            Cookie[] cookies;
            cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (WS_NAME.equals(cookie.getName())) {
                        if (CharWebsocketHandler.userWebsocketMap.get(cookie.getValue()) != null &&
                                !"".equals(CharWebsocketHandler.userWebsocketMap.get(cookie.getValue()))) {
                            token_1 = cookie.getValue();
                            map.put("uid", token_1);
                            return true;
                        }
                    }
                }
            }
        }
        token_1 = UUID.randomUUID().toString();
        map.put("uid", token_1);
        return true;
    }

    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
        if(CharWebsocketHandler.userWebsocketMap.get(token_1) != null) return;
        if(serverHttpResponse instanceof  ServletServerHttpResponse){
            ServletServerHttpResponse servletServerHttpResponse = (ServletServerHttpResponse) serverHttpResponse;
            HttpServletResponse response = servletServerHttpResponse.getServletResponse();
            Cookie cookiea = new Cookie("name", name);
            cookiea.setMaxAge(30 * 60 * 12);
            cookiea.setPath("/");
            response.addCookie(cookiea);
            Cookie cookieb = new Cookie(WS_NAME, token_1);
            cookieb.setMaxAge(30 * 60 * 12);
            cookieb.setPath("/");
            response.addCookie(cookieb);
        }
    }
}
