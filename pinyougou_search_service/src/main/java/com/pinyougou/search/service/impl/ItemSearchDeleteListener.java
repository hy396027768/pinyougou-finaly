package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import entity.SolrItem;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.search.service.impl
 * @date 2019-3-27
 */
@Component
public class ItemSearchDeleteListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {

        try {
            ObjectMessage msg = (ObjectMessage) message;
            Long id = (Long) msg.getObject();

            //删除索引库
            itemSearchService.deleteByGoodsIds(id);


        } catch (JMSException e) {
            e.printStackTrace();
        }


        //try {
        //    ObjectMessage objectMessage = (ObjectMessage) message;
        //    //获取消息内容
        //    Long[] ids = (Long[]) objectMessage.getObject();
        //    //删除索引库
        //    itemSearchService.deleteByGoodsIds(ids);
        //} catch (JMSException e) {
        //    e.printStackTrace();
        //}
    }
}
