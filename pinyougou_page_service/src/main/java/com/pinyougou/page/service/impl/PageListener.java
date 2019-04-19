package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.aspectj.bridge.MessageWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

/**
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.page.service.impl
 * @date 2019-3-27
 */
@Component
public class PageListener implements MessageListener{

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {


        //try {
        //    ObjectMessage msg = (ObjectMessage) message;
        //    //读取消息内容
        //    Long[] ids = (Long[]) msg.getObject();
        //    //生成商品静态页
        //    for (Long id : ids) {
        //        itemPageService.genItemHtml(id);
        //    }
        //} catch (JMSException e) {
        //    e.printStackTrace();
        //}


        try {
            ObjectMessage msg = (ObjectMessage) message;
            Long id = (Long) msg.getObject();

            itemPageService.genItemHtml(id);

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
