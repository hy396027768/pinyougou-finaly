package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.cart.service.impl
 * @date 2019-4-1
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据商品SKU ID查询SKU商品信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if(item == null){
            throw new RuntimeException("商品信息不存在!");
        }
        if(!"1".equals(item.getStatus())){
            throw new RuntimeException("商品不存在或者已下架！");
        }
        //2.获取商家ID
        String sellerId = item.getSellerId();
        //3.根据商家ID判断购物车列表中是否存在该商家的购物车
        Cart cart = searchCartBySellerId(cartList,sellerId);
        //4.如果购物车列表中不存在该商家的购物车
        if(cart == null){
            //4.1 新建购物车对象
            cart = new Cart();
            cart.setSellerId(sellerId);  //商家id
            cart.setSellerName(item.getSeller());  //商家名称
            //商品列表
            List<TbOrderItem> orderItemList = new ArrayList<>();
            //创建购物车商品信息
            TbOrderItem orderItem = createOrderItem(item,num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            //4.2 将新建的购物车对象添加到购物车列表
            cartList.add(cart);
        }else{//5.如果购物车列表中存在该商家的购物车
            // 查询购物车明细列表中是否存在该商品
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(),itemId);
            //5.1. 如果没有，新增购物车明细
            if(orderItem == null){
                //构建商品信息
                orderItem = createOrderItem(item,num);
                //向购物车商品列表中追加一条记录
                cart.getOrderItemList().add(orderItem);
            }else{
                //5.2. 如果有，在原购物车明细上添加数量，更改金额
                orderItem.setNum(orderItem.getNum() + num);
                //计算小计
                double totalFee = orderItem.getNum() * orderItem.getPrice().doubleValue();
                orderItem.setTotalFee(new BigDecimal(totalFee));

                //如果数量修改后，购买数量不足1
                if(orderItem.getNum() < 1){
                    //在当前购物车商品列表中移除一个商品信息
                    cart.getOrderItemList().remove(orderItem);

                    //如果删除商品信息后，当前购物车商品列表没有了
                    if(cart.getOrderItemList().size() < 1){
                        //删除整个购物车对象
                        cartList.remove(cart);
                    }
                }
            }
        }
        return cartList;
    }
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if(cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username, cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList1) {
            //获取第一组list所有商品列表
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                //把第一组的所有商品，追加到第二级购物车中
                this.addGoodsToCartList(cartList2, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return cartList2;
    }

    /**
     * 跟据sku-id查询购物商品列表中，没有有相应的商品
     * @param orderItemList 当前购物车的商品列表
     * @param itemId 当前购买的商品sku-id
     * @return 查找结果，null-标识没有找到，找到了返回该商品信息
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            //如果找到了相应商品
            if (itemId.longValue() == orderItem.getItemId().longValue()) {
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 构建购物车商品信息
     * @param item 当前的sku信息
     * @param num 购物数量
     * @return 购物车商品信息
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        if(num < 1){
            throw new RuntimeException("请输入正确的商品数量！");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setNum(num);  //购买数量
        orderItem.setSellerId(item.getSellerId()); //商家id
        orderItem.setPicPath(item.getImage());  //商品图片
        orderItem.setPrice(item.getPrice()); //单价
        //计算小计
        double totalFee = item.getPrice().doubleValue() * num;
        orderItem.setTotalFee(new BigDecimal(totalFee));
        orderItem.setTitle(item.getTitle());  //标题
        orderItem.setGoodsId(item.getGoodsId());  //spu-id
        orderItem.setItemId(item.getId());  //sku-id
        return orderItem;
    }

    /**
     * 跟据商家id检测当前购物车是否存在当前商家的商品
     * @param cartList 原来的购物车
     * @param sellerId 商家的id
     * @return 查找的结果，null-没有找到，找到情况下返回的是这个商家的有购物车信息
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            //如果找到了这个商家的商品
            if(sellerId.equals(cart.getSellerId())){
                return cart;
            }
        }
        return null;
    }
}
