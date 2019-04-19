package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.abel533.entity.Example;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.page.service.impl
 * @date 2019-3-26
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Value("${PAGE_DIR}")
    private String PAGE_DIR;

    @Override
    public boolean genItemHtml(Long goodsId) {
        try {
            //1、查询商品所有信息
            //基本信息
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            //扩展信息
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            //2、使用Freemarker输出静态html
            Configuration cfg = freeMarkerConfigurer.getConfiguration();
            //获取模板对象
            Template template = cfg.getTemplate("item.ftl");
            Map map = new HashMap();
            //返回商品信息
            map.put("goods", goods);
            map.put("goodsDesc", goodsDesc);

            //查询商品三级分类名称
            String category1Name = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            map.put("category1Name", category1Name);
            String category2Name = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            map.put("category2Name", category2Name);
            String category3Name = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
            map.put("category3Name", category3Name);

            //查询商品sku列表
            Example example = new Example(TbItem.class);
            //组装查询条件
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("status", "1");
            criteria.andEqualTo("goodsId", goodsId);
            //排序，把默认的sku放到第一条中
            example.setOrderByClause("isDefault desc");
            //查询数据
            List<TbItem> itemList = itemMapper.selectByExample(example);
            map.put("itemList", itemList);

            //文档输出路径
            Writer out = new FileWriter(PAGE_DIR + goodsId + ".html");
            //输出文档
            template.process(map,out);

            //关闭流
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
