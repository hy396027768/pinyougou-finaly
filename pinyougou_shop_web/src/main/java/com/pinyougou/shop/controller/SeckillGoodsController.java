package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Create by Qiudongqiu on 2019/4/14.<br>
 */
@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {
    @Reference
    private SeckillGoodsService seckillGoodsService;
    @RequestMapping("/add")
    public Result add(@RequestBody TbSeckillGoods seckillGoods){
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        seckillGoods.setSellerId(sellerId);
        try {
            seckillGoodsService.add(seckillGoods);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
}
