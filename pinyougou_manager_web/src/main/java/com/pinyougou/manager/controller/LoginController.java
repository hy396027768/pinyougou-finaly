package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.manager.controller
 * @date 2019-3-13
 */
@RestController
@RequestMapping("login")
public class LoginController {

    @RequestMapping("name")
    public Map name(){
        Map map = new HashMap();

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("loginName", name);

        return map;
    }
}
