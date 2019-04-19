package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import entity.PageResult;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 创建者      Administrator
 * 创建时间    2019/4/14 15:21
 * 描述
 */
@RestController
@RequestMapping(value = "/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping(value = "/findAllByUserId")
    public PageResult findAllByUserId(int pageNum, int pageSize,@RequestBody TbOrder order){
        //获取登录用户名
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        order.setUserId(userId);//设置查询条件
        return orderService.findPage(order,pageNum, pageSize);
    }
}
