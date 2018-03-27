package chars.controller;


import chars.handler.CharWebsocketHandler;
import chars.model.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: chenwj
 * @Description:
 * @Date: 2018/3/23
 */
@ResponseBody
@RequestMapping("char")
public class CharController {

    @RequestMapping("getUsers")
    public List<User> getUsers(){
        List<User> result = new ArrayList<>();
        for(Map.Entry<String, Map<String, Object>> map : CharWebsocketHandler.userWebsocketMap.entrySet()){
            User user = new User();
            user.setUid(map.getKey());
            user.setName((String) map.getValue().get("name"));
        }
        return result;
    }

}
