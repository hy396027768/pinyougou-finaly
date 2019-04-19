package com.pinyougou.portal.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 扩展SpringSecurity认证方式
 * @author Steven
 * @version 1.0
 * @description com.itheima.demo.service
 * @date 2019-3-30
 */
public class UserDetailServiceImpl implements UserDetailsService{
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //构建角色列表
        List<GrantedAuthority> authourities = new ArrayList<GrantedAuthority>();
        authourities.add(new SimpleGrantedAuthority("ROLE_USER"));
        //注意：这里的密码返回空，相当于让cas来配置用户账号信息
        return new User(username,"",authourities);
    }
}
