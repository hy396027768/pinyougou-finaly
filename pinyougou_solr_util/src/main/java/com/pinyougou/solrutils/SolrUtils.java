package com.pinyougou.solrutils;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import entity.SolrItem;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.solrutils
 * @date 2019-3-21
 */
@Component
public class SolrUtils {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 导入数据
     */
    public void importData(){
        //把商品sku列表查询出来
        TbItem where = new TbItem();
        where.setStatus("1");
        List<TbItem> itemList = itemMapper.select(where);
        System.out.println("查找到 " + itemList.size() + " 个商品信息!");

        System.out.println("开始组装数据...");
        List<SolrItem> solrItems = new ArrayList<>();
        SolrItem solrItem = null;
        for (TbItem item : itemList) {
            solrItem = new SolrItem();

            //使用深克隆,把TbItem与SolrItem相同属性的，内容复制过来
            //copyProperties(数据源,复制目标)
            BeanUtils.copyProperties(item,solrItem);

            //转换规格数据
            Map specMap = JSON.parseObject(item.getSpec(), Map.class);
            solrItem.setSpecMap(specMap);

            solrItems.add(solrItem);
        }
        System.out.println("数据组装完毕，开始导入索引库..");
        solrTemplate.saveBeans(solrItems);
        solrTemplate.commit();

    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-*.xml");

        SolrUtils solrUtils = context.getBean(SolrUtils.class);

        solrUtils.importData();

    }
}
