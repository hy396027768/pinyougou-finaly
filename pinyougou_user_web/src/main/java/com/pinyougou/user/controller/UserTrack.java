package com.pinyougou.user.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbCollect;
import com.pinyougou.user.service.UserTrackService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserTrack {

    @Reference(timeout = 5000)
    private UserTrackService userTrackService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    /**
     * 用户浏览足迹记录
     * @param id
     */
    @RequestMapping("/track")
    public Result track(Long id) {
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            userTrackService.track(id, name);
            return new Result(true, "添加足迹成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加足迹请求超时");
        }

    }
    @RequestMapping("/cancleCollect")
    public Result cancleCol(Long id) {
        try {
            userTrackService.cancleCol(id);
            return new Result(true,"加入成功");
            } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
    //显示收藏
    @RequestMapping("/findListByContect")
    public List<TbCollect> findListByContect(){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        return userTrackService.findListByUserId(name);


    }

    /**
     * 用户收藏商品
     * @param id
     */
    @RequestMapping("/collect")
    @CrossOrigin(origins = "http://localhost:8088",allowCredentials = "true")
    public Result collect(Long id , HttpServletRequest request) {
        try {
            //response.setHeader("Access-Control-Allow-Origin", "http://localhost:8088");
            //如果需要操作cookies，必须加上此配置，标识服务端可以写cookies，
            // 并且Access-Control-Allow-Origin不能设置为*，因为cookies操作需要域名
            //response.setHeader("Access-Control-Allow-Credentials", "true");
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            userTrackService.collect(id ,name);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }


    /**
     *查询足迹列表
     */

    @RequestMapping("/findListByUserId")
    public List<TbCollect> findListByUserId(){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

         return userTrackService.findListByUserId(name);


    }


}
