package com.reins.bookstore.controller;

import com.reins.bookstore.constant.Constant;
import com.reins.bookstore.entity.UserAuth;
import com.reins.bookstore.service.UserService;
import com.reins.bookstore.utils.msgutils.Msg;
import com.reins.bookstore.utils.msgutils.MsgCode;
import com.reins.bookstore.utils.msgutils.MsgUtil;
import com.reins.bookstore.utils.sessionutils.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;


/**
 *@ClassName LoginController
 *@Description Controller for login
 *@Author thunderBoy
 *@Date 2019-11-05 15:09
 */
@Slf4j
@RestController
public class LoginController {

    @Autowired
    private UserService userService;
    private StringRedisTemplate redisTemplate;


    /**
     * @Description: login
     * @Param: username,password,remember
     * @return: Msg
     * @Author: thunderBoy
     */
    @Autowired
    public LoginController(StringRedisTemplate redisTemplate, UserService userService){
        this.redisTemplate = redisTemplate;
        this.userService = userService;
    }

    @GetMapping("/login")
    public Msg login(@RequestParam("username") String username, @RequestParam("password") String password,HttpSession session){
        log.info("login. Current session id: {}", session.getId());
//        String username = params.get(Constant.USERNAME);
//        String password = params.get(Constant.PASSWORD);
        UserAuth auth = userService.checkUser(username, password);
        if(auth != null){
            session.setAttribute("username", username);
            redisTemplate.opsForValue().set(String.format("username:%s", username), session.getId());
            return MsgUtil.makeMsg(MsgCode.SUCCESS, MsgUtil.LOGIN_SUCCESS_MSG, null);
        }else{
            return MsgUtil.makeMsg(MsgCode.LOGIN_USER_ERROR);
        }
    }

@GetMapping("/logout")
public Msg logout(HttpSession session){
    log.info("current session id: {}", session.getId());
    session.invalidate();
    return MsgUtil.makeMsg(MsgCode.SUCCESS, MsgUtil.LOGOUT_SUCCESS_MSG);
}

    @RequestMapping("/checkSession")
    public Msg checkSession(HttpSession session){
        log.info("checkSession. Current session id: {}", session.getId());
        String currentSessionId = session.getId();
        String username = (String) session.getAttribute("username");
        if(username == null){
            log.info("checkSession. Username is null");
        }else{
            String loginSessionId = redisTemplate.opsForValue().get(String.format("username:%s", username));
            if(loginSessionId != null && loginSessionId.equals(currentSessionId)){
                log.info("checkSession success");
                return MsgUtil.makeMsg(MsgCode.SUCCESS, MsgUtil.LOGIN_SUCCESS_MSG, null);
            }else{
                log.info("checkSession failed");
            }
        }
        return MsgUtil.makeMsg(MsgCode.NOT_LOGGED_IN_ERROR, MsgUtil.NOT_LOGGED_IN_ERROR_MSG);
    }
}

