package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;

import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.utils.DateUtil;
import entity.OrderCount;
import entity.PageResult;
import entity.Result;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 请求处理器
 *
 * @author Steven
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference(timeout = 5000)
    private OrderService orderService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbOrder> findAll() {
        return orderService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return orderService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param order
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbOrder order) {
        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            order.setUserId(userId);
            order.setSourceType("2");
            orderService.add(order);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param order
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbOrder order) {
        try {
            orderService.update(order);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbOrder findOne(Long id) {
        return orderService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            orderService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param order
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbOrder order, int page, int rows) {
        return orderService.findPage(order, page, rows);
    }

    /**
     * 查询订单统计信息
     *
     * @return
     */
    @RequestMapping("/findOrderService")
    public List<OrderCount> findOrderService(@DateTimeFormat(pattern = "yyyy-MM-dd")Date startTime, @DateTimeFormat(pattern = "yyyy-MM-dd")Date endTime) {

        //将商家id传递到后台
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderService.findOrderService(name,startTime,endTime);
    }
    /**
     * 封装折线图需要的数据
     * @return
     */
    @RequestMapping("/findOrderLineChart")
    public Map findOrderLineChart(@DateTimeFormat(pattern = "yyyy-MM-dd")Date startTime, @DateTimeFormat(pattern = "yyyy-MM-dd")Date endTime){
        //存储折线图横坐标名称
        //将时间段拆分成每一天时间的集合
        List<String> lineList = DateUtil.getIntervalTimeList(startTime,endTime,24);

        //得到当前时间段内商家卖出去的所有商品名称
        //获取商家的名称
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<String> goodsNameList = orderService.selectGoodsNameByPaymentTime(name, startTime, endTime);

        //获取表格需要的具体数据
        Map<String,List> seriesMap = new HashMap<>();
        //获取每个商家在时间段内每天卖出的数量
        for (String goodsName : goodsNameList) {
            List<Integer> list = new ArrayList();
            for (String date : lineList) {
                Integer num = orderService.selectGoodsNamePayMentByPaymentTime(name, goodsName, date + "%");
                list.add(num);
            }
            seriesMap.put(goodsName,list );
        }

        //封装数据
        HashMap<String,Object> map = new HashMap();
        map.put("lineList",lineList );
        map.put("goodsNameList",goodsNameList );
        map.put("seriesMap",seriesMap );

        return map;
    }
}
