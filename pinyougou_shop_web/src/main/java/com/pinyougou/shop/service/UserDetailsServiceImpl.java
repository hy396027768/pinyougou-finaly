package com.pinyougou.shop.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * 权限认证扩展类
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.shop.service
 * @date 2019-3-13
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    @Reference
    private SellerService sellerService;

    private static Logger logger = Logger.getLogger(String.valueOf(UserDetailsServiceImpl.class));




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(username + "进入了loadUserByUsername....");
        //构建角色列表
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));



        logger.info("========="+username+"===============");


        //对接数据库前方案
        // return new User(username,"123456",authorities);
        //对接数据库后
        TbSeller seller = sellerService.findShopOne(username);

        logger.info("========="+seller+"===============");

        //只有审核通过的商家，才可以登录
        if(seller != null && "1".equals(seller.getStatus())){
            return new User(username,seller.getPassword(),authorities);
        }
        //返回null，表示认证不通过
        return null;
    }
}
