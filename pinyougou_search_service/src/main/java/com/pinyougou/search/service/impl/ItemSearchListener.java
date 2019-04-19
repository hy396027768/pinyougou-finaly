package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.abel533.entity.Example;
import com.pinyougou.mapper.TbItemMapper;
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
import java.util.logging.Logger;

/**
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.search.service.impl
 * @date 2019-3-27
 */
@Component
public class ItemSearchListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;

    @Autowired
    private TbItemMapper tbItemMapper;

    private static Logger logger = Logger.getLogger(String.valueOf(ItemSearchListener.class));

    @Override
    public void onMessage(Message message) {

        try {

            ObjectMessage msg = (ObjectMessage) message;

            Long id = (Long) msg.getObject();
            Example example = new Example(TbItem.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("goodsId", id);

            List<TbItem> tbItems = tbItemMapper.selectByExample(example);

            List<SolrItem> list = new ArrayList<>();

            SolrItem solrItem = null;

            for (TbItem tbItem : tbItems) {

                solrItem = new SolrItem();

                //深克隆
                BeanUtils.copyProperties(tbItem, solrItem);

                //把规格转换成map集合保存到solr中
                Map map = JSON.parseObject(tbItem.getSpec(), Map.class);

                solrItem.setSpecMap(map);


                logger.info("=================================" + solrItem + "===============================");



                list.add(solrItem);

            }

            itemSearchService.importList(list);


        } catch (JMSException e) {
            e.printStackTrace();
        }


        //try {
        //    //获取对象消息
        //    TextMessage msg = (TextMessage) message;
        //    //转换消息内容
        //    //List<SolrItem> solrItems = (List<SolrItem>) objectMessage.getObject();

        //    String jsonString = msg.getText();

        //    List<TbItem> itemList = JSON.parseArray(jsonString, TbItem.class);
        //    List<SolrItem> solrItems = new ArrayList<>();
			//	SolrItem solrItem = null;
			//	for (TbItem item : itemList) {
			//		solrItem = new SolrItem();
        //
			//		//使用深克隆,把TbItem与SolrItem相同属性的，内容复制过来
			//		//copyProperties(数据源,复制目标)
			//		BeanUtils.copyProperties(item,solrItem);
        //
			//		//转换规格数据
			//		Map specMap = JSON.parseObject(item.getSpec(), Map.class);
			//		solrItem.setSpecMap(specMap);
        //
			//		solrItems.add(solrItem);
			//	}
        //    //导入索引库
        //    itemSearchService.importList(solrItems);
        //} catch (JMSException e) {
        //    e.printStackTrace();
        //}






    }
}
