package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.search.service.ItemSearchService;
import entity.SolrItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.search.service.impl
 * @date 2019-3-21
 */
@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map search(Map searchMap) {
        Map map = new HashMap();
        //1、查询商品列表
        searchList(searchMap, map);
        //2、查询分组查询商品分类列表
        searchCategoryList(searchMap, map);
        //3、查询品牌与规格列表
        String category = searchMap.get("category") == null ? "" : searchMap.get("category").toString();
        if(category.trim().length() > 0){
            searchBrandAndSpecList(category,map);
        }else {
            List<String> categoryList = (List<String>) map.get("categoryList");
            if (categoryList.size() > 0) {
                searchBrandAndSpecList(categoryList.get(0), map);
            }
        }
        return map;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(Long  goodsId) {
        Query query = new SimpleQuery();
        //组装删除条件
        Criteria criteria = new Criteria("item_goodsid").in(goodsId);
        query.addCriteria(criteria);
        //删除索引库
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询品牌与规格列表
     * @param category 商品分类名称
     * @param map 查询结果
     */
    private void searchBrandAndSpecList(String category, Map map) {
        //跟据分类名称查询模板id
        Long typeId = (Long) redisTemplate.boundHashOps("itemCats").get(category);
        if(typeId != null){
            //查询品牌列表
            List<Map> brandIds = (List<Map>) redisTemplate.boundHashOps("brandIds").get(typeId);
            map.put("brandIds", brandIds);
            //查询规格列表
            List<Map> specIds = (List<Map>) redisTemplate.boundHashOps("specIds").get(typeId);
            map.put("specIds", specIds);
        }
    }

    /**
     * 分组查询商品分类列表
     * @param searchMap 查询条件
     * @param map 结果集
     */
    private void searchCategoryList(Map searchMap, Map map) {
        //商品分类列表
        List<String> categoryList = new ArrayList<>();

        //1.创建查询条件对象query = new SimpleQuery()
        Query query = new SimpleQuery();
        //2.复制之前的Criteria组装查询条件的代码
        //关键字条件
        String keywords = searchMap.get("keywords") == null ? "" : searchMap.get("keywords").toString();
        if (keywords.trim().length() > 0) {
            Criteria criteria = new Criteria("item_keywords").is(keywords);
            query.addCriteria(criteria);
        }
        //3.创建分组选项对象new GroupOptions().addGroupByField(域名)
        GroupOptions gOptions = new GroupOptions().addGroupByField("item_category");
        //4.设置分组对象query.setGroupOptions
        query.setGroupOptions(gOptions);
        //5.得到分组页对象page = solrTemplate.queryForGroupPage
        GroupPage<SolrItem> page = solrTemplate.queryForGroupPage(query, SolrItem.class);
        //6.得到分组结果集groupResult = page.getGroupResult(域名)
        GroupResult<SolrItem> groupResult = page.getGroupResult("item_category");
        //7.得到分组结果入口groupEntries = groupResult.getGroupEntries()
        Page<GroupEntry<SolrItem>> groupEntries = groupResult.getGroupEntries();
        //8.得到分组入口集合content = groupEntries.getContent()
        List<GroupEntry<SolrItem>> content = groupEntries.getContent();
        //9.遍历分组入口集合content.for(entry)，记录结果entry.getGroupValue()
        for (GroupEntry<SolrItem> entry : content) {
            //记录查询结果
            categoryList.add(entry.getGroupValue());
        }
        //把结果返回
        map.put("categoryList", categoryList);
    }

    /**
     * 查询商品列表
     *
     * @param searchMap 查询条件
     * @param map       返回结果
     */
    private void searchList(Map searchMap, Map map) {
        //2、使用高亮后的解决方案
        //2.构建query高亮查询对象new SimpleHighlightQuery
        HighlightQuery query = new SimpleHighlightQuery();
        //3.复制之前的Criteria组装查询条件的代码
        //3.1关键字条件
        String keywords = searchMap.get("keywords") == null ? "" : searchMap.get("keywords").toString();
        //去除空格
        keywords = keywords.replaceAll(" ", "");
        //把keywords重新放入searchMap中
        searchMap.put("keywords", keywords);
        if (keywords.trim().length() > 0) {
            Criteria criteria = new Criteria("item_keywords").is(keywords);
            query.addCriteria(criteria);
        }
        //3.2 商品分类条件
        String category = searchMap.get("category") == null ? "" : searchMap.get("category").toString();
        if(category.trim().length() > 0){
            Criteria criteria = new Criteria("item_category").is(category);
            FilterQuery filterQuery = new SimpleFilterQuery(criteria);
            query.addFilterQuery(filterQuery);
        }
        //3.3 品牌条件
        String brand = searchMap.get("brand") == null ? "" : searchMap.get("brand").toString();
        if(brand.trim().length() > 0){
            Criteria criteria = new Criteria("item_brand").is(brand);
            FilterQuery filterQuery = new SimpleFilterQuery(criteria);
            query.addFilterQuery(filterQuery);
        }
        //3.4 规格条件
        String spec = searchMap.get("spec") == null ? "" : searchMap.get("spec").toString();
        if(spec.trim().length() > 0){
            Map<String,String> specMap = JSON.parseObject(spec, Map.class);
            for (String key : specMap.keySet()) {
                Criteria criteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(criteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //3.5 价格区间:0-500 500-100 ..... 3000-*
        String price = searchMap.get("price") == null ? "" : searchMap.get("price").toString();
        if(price.trim().length() > 0){
            String[] split = price.split("-");
            /*//方案一:between查询，存在问题，不支持*
            Criteria criteria = new Criteria("item_price").between(split[0],split[1]);
            FilterQuery filterQuery = new SimpleFilterQuery(criteria);
            query.addFilterQuery(filterQuery);*/

            //方案二：大于小于
            //商品价格 >= 开始价格
            if(!"0".equals(split[0])){
                Criteria criteria = new Criteria("item_price").greaterThanEqual(split[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(criteria);
                query.addFilterQuery(filterQuery);
            }
            //商品价格 <= 结束价格
            if(!"*".equals(split[1])){
                Criteria criteria = new Criteria("item_price").lessThanEqual(split[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(criteria);
                query.addFilterQuery(filterQuery);
            }
        }
        // 3.6分页参数获取
        //当前页
        Integer pageNo = searchMap.get("pageNo") == null ? 1 : new Integer(searchMap.get("pageNo").toString());
        //每页查询的记录数
        Integer pageSize = searchMap.get("pageSize") == null ? 10 : new Integer(searchMap.get("pageSize").toString());
        query.setOffset((pageNo - 1) * pageSize);  //起始行号
        query.setRows(pageSize);

        //3.7 设置排序
        //排序方式 asc|desc
        String sortValue = searchMap.get("sort") == null ? "" : searchMap.get("sort").toString();
        //排序的域名
        String sortField = searchMap.get("sortField") == null ? "" : searchMap.get("sortField").toString();
        //用户有传入排序参数，设定排序参数
        if(sortValue.trim().length() > 0 && sortField.trim().length() > 0){
            //升序
            if("ASC".equals(sortValue.toUpperCase())){
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }

            if("DESC".equals(sortValue.toUpperCase())){
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
        }

        //4.调用query.setHighlightOptions()方法，
        // 构建高亮数据三步曲：一曲：new HighlightOptions()
        HighlightOptions hOptions = new HighlightOptions();
        // 二曲：.addField(高亮业务域)，
        hOptions.addField("item_title");
        //三曲 .setSimpleP..(前缀)，.setSimpleP..(后缀)
        hOptions.setSimplePrefix("<em style=\"color:red;\">");
        hOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(hOptions);
        //1.调用solrTemplate.queryForHighlightPage(query,class)方法，高亮查询数据
        //5.接收solrTemplate.queryForHighlightPage的返回数据，定义page变量
        HighlightPage<SolrItem> page = solrTemplate.queryForHighlightPage(query, SolrItem.class);
        //6.遍历解析page对象，page.getHighlighted().for，
        // item = h.getEntity()，item.setTitle(h.getHighlights().get(0).getSnipplets().get(0))，
        // 在设置高亮之前最好判断一下;
        for (HighlightEntry<SolrItem> h : page.getHighlighted()) {
            //获取每一条查询结果
            SolrItem item = h.getEntity();
            //获取高亮数据
            if (h.getHighlights() != null && h.getHighlights().size() > 0
                    && h.getHighlights().get(0).getSnipplets().size() > 0) {
                //设置高亮title
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
            }

        }
        //7.在循环完成外map.put("rows", page.getContent())返回数据列表
        map.put("rows", page.getContent());
        //返回分页参数
        map.put("total", page.getTotalElements());  //总记录数
        map.put("totalPages", page.getTotalPages()); //总页数

    }
}
