package com.reins.bookstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
public class userManagementController {
    private StringRedisTemplate redisTemplate;
    @Autowired
    public userManagementController(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/session/login")
    public String login(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute("username", "zzt");
        redisTemplate.opsForValue().set("username:zzt", session.getId());
        return "login finished";
    }

    @GetMapping("/session/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "logout finished";
    }

    @GetMapping(value = "/session/getInfo")
    public Map<String, String> addSession (HttpServletRequest request){
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String username = (String) session.getAttribute("username");
        String requestURI = request.getRequestURI();

        Map<String, String> sessionInfoMap = new HashMap<>();
        sessionInfoMap.put("sessionId", sessionId);
        sessionInfoMap.put("username", username);
        sessionInfoMap.put("requestURI", requestURI);
        return sessionInfoMap;
    }
}
