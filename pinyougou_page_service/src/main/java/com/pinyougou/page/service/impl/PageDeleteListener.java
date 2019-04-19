package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.File;

/**
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.page.service.impl
 * @date 2019-3-27
 */
@Component
public class PageDeleteListener implements MessageListener{

    @Value("${PAGE_DIR}")
    private String PAGE_DIR;

    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage msg = (ObjectMessage) message;
            //读取消息内容
            Long[] ids = (Long[]) msg.getObject();
            //生成商品静态页
            for (Long id : ids) {
                File beDelete = new File(PAGE_DIR + id + ".html");
                boolean flag = beDelete.delete();
                System.out.println("删除goodId为:" + id + " 的商品详情页，结果为：" + flag);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
