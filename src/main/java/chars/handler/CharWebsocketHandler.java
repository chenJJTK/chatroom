package chars.handler;

import chars.model.Message;
import chars.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: chenwj
 * @Description:
 * @Date: 2018/3/21
 */
@Component
public class CharWebsocketHandler implements WebSocketHandler{

    public static final Map<String, Map<String, Object>> userWebsocketMap;

    static{
        userWebsocketMap = new HashMap<>();
    }

    /**
     *
     * @Author: chenwj
     * @Description: 握手实现连接后执行的行为
     * @Date: 2018/3/21
     * @Param:
     *
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String username = (String) webSocketSession.getAttributes().get("name");
        String uid = (String) webSocketSession.getAttributes().get("uid");
        Map<String, Object> user = new HashMap<>();
        user.put("name", username);
        user.put("session", webSocketSession);
        userWebsocketMap.put(uid, user);
        Message msg = new Message();
        msg.setType(2);
        msg.setMessageDate(new Timestamp(System.currentTimeMillis()));
        msg.setFromName("系统提示");
        msg.setMessageText(username + "进入聊天室！");
        TextMessage text = new TextMessage(new Gson().toJson(msg));
        TextMessage aboutUser = new TextMessage(new Gson().toJson(getUsers()));
        /* 向所有用户广播有用户接入聊天室 */
        for(Map.Entry<String, Map<String, Object>> en : userWebsocketMap.entrySet()){
            ((WebSocketSession) en.getValue().get("session")).sendMessage(text);
            ((WebSocketSession) en.getValue().get("session")).sendMessage(aboutUser);
        }
    }

    /**
     *
     * @Author: chenwj
     * @Description: 发送信息之前执行的行为
     * @Date: 2018/3/21
     * @Param:
     *
     */
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        /* 当message为空时执行的行为 */
        if(webSocketMessage.getPayloadLength() == 0){
            return;
        }
        String uid = (String) webSocketSession.getAttributes().get("uid");
        String username = (String) userWebsocketMap.get(uid).get("name");
        Message msg = new GsonBuilder().create().fromJson(webSocketMessage.getPayload().toString(), Message.class);
        msg.setMessageDate(new Timestamp(System.currentTimeMillis()));
        /* 私聊 */
        if(msg.getType() != null && msg.getType() == 3){// 更名
            Map<String, Object> map = userWebsocketMap.get(uid);
            String oldName = (String) map.get("name");
            String newName = msg.getFromName();
            map.put("name", newName);
            userWebsocketMap.put(uid, map);
            msg.setType(2);
            msg.setFromName("系统提示");
            msg.setMessageText(oldName + "更名为" + newName);
            TextMessage text = new TextMessage(new Gson().toJson(msg));
            TextMessage aboutUser = new TextMessage(new Gson().toJson(getUsers()));
            for(Map.Entry<String, Map<String, Object>> en : userWebsocketMap.entrySet()){
                ((WebSocketSession) en.getValue().get("session")).sendMessage(text);
                ((WebSocketSession) en.getValue().get("session")).sendMessage(aboutUser);
            }
            return;
        }
        msg.setFromId(uid);
        msg.setFromName(username);
        if(msg.getToId() != null && !"".equals(msg.getToId())){
            msg.setType(1);
            msg.setToName((String) userWebsocketMap.get(msg.getToId()).get("name"));
            Object session =  userWebsocketMap.get(msg.getToId()).get("session");
            if(session != null && session instanceof WebSocketSession){
                ((WebSocketSession) session).sendMessage(
                        new TextMessage(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(msg)));
            }
            webSocketSession.sendMessage(new TextMessage(
                    new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(msg)));
            /* 群发 */
        }else {
            msg.setType(0);
            for (Map.Entry<String, Map<String, Object>> en : userWebsocketMap.entrySet()) {
                ((WebSocketSession) en.getValue().get("session")).
                        sendMessage(new TextMessage(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(msg)));
            }
        }
    }

    /**
     *
     * @Author: chenwj
     * @Description: 发生传输异常时执行的行为
     * @Date: 2018/3/21
     * @Param:
     *
     */
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    /**
     *
     * @Author: chenwj
     * @Description: 用户断开连接后执行的行为，一般将该用户session删除
     * @Date: 2018/3/21
     * @Param:
     *
     */
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        String uid = (String) webSocketSession.getAttributes().get("uid");
        String name = (String) userWebsocketMap.get(uid).get("name");
        userWebsocketMap.remove(uid);
        Message msg = new Message();
        msg.setType(2);
        msg.setFromName("系统提示");
        msg.setMessageText(name + "已离开聊天室！");
        TextMessage text = new TextMessage(new Gson().toJson(msg));
        TextMessage aboutUser = new TextMessage(new Gson().toJson(getUsers()));
        for(Map.Entry<String, Map<String, Object>> en : userWebsocketMap.entrySet()){
            ((WebSocketSession) en.getValue().get("session")).sendMessage(text);
            ((WebSocketSession) en.getValue().get("session")).sendMessage(aboutUser);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private Map<String, Object> getUsers(){
        List<User> users = new ArrayList<>();
        int i = 0;
        for(Map.Entry<String, Map<String, Object>> map : userWebsocketMap.entrySet()){
            i++;
            User user = new User();
            user.setUid(map.getKey());
            user.setName((String) map.getValue().get("name"));
            users.add(user);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("count", i);
        result.put("users", users);
        result.put("aboutUser", true);
        return result;
    }
}
