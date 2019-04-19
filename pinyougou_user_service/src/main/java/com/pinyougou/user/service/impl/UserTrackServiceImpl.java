package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbCollectMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbCollect;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.user.service.UserTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;


import java.util.*;

@Service
public class UserTrackServiceImpl implements UserTrackService {


    @Autowired
    private TbGoodsMapper tbGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbCollectMapper tbCollectMapper;


    /**
     * 用户浏览足迹
     * @param id
     * @param name
     */
    @Override
    public void track(Long id, String name) {

        //获取到用户当前浏览的宝贝
        TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(id);


        TbCollect tbCollect = new TbCollect();

        tbCollect.setCreateTime(new Date());
        tbCollect.setId(id);
        tbCollect.setTitle(tbGoods.getGoodsName());
        tbCollect.setPrice(tbGoods.getPrice());
        tbCollect.setImage(tbGoods.getSmallPic());
        tbCollect.setTitle("1");
        tbCollect.setCollect("0");
        tbCollect.setUserName(name);

        //保存到缓存数据库中
        redisTemplate.boundListOps(name).leftPush(tbCollect);

        //设定定时器 保存到数据库中
        Timer timer = new Timer();

        //定时器30分钟
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                //保存到数据库中

                List<TbCollect> range = redisTemplate.boundListOps(name).range(0, 10);

                for (TbCollect collect : range) {
                    tbCollectMapper.insertSelective(collect);
                }

                redisTemplate.delete(name);
            }
        }, 1000*60*30);

    }


    /**
     * 收藏
     * @param id
     */
    @Override
    public void collect(Long id,String name) {


        //获取到用户当前浏览的宝贝
        TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(id);


        TbCollect tbCollect = new TbCollect();

        tbCollect.setCreateTime(new Date());
        tbCollect.setId(id);
        tbCollect.setTitle(tbGoods.getGoodsName());
        tbCollect.setPrice(tbGoods.getPrice());
        tbCollect.setImage(tbGoods.getSmallPic());
        tbCollect.setTitle("0");
        tbCollect.setCollect("1");
        tbCollect.setUserName(name);

        tbCollectMapper.insertSelective(tbCollect);
    }

    @Override
    public List<TbCollect> findListByUserId(String userId) {

        TbCollect where=new TbCollect();

        where.setUserName(userId);

        List<TbCollect> collects = tbCollectMapper.select(where);
        return collects;
    }
    //取消收藏
    @Override
    public void cancleCol(Long id) {
        int i = tbCollectMapper.deleteByPrimaryKey(id);

   }
}
