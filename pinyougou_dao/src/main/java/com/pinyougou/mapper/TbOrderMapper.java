package com.pinyougou.mapper;

import com.github.abel533.mapper.Mapper;
import com.pinyougou.pojo.TbOrder;
import entity.OrderCount;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface TbOrderMapper extends Mapper<TbOrder> {
    /**
     * 查询指定商家指定时间段内的订单统计
     * @param sellerId
     * @param startTime
     * @param endTime
     * @return
     */
    @Select("select " +
            "g.goods_name goodsName,count(*) count,sum(o.payment) saleRoom,g.is_marketable status,o.seller_id sellerId" +
            " from " +
            " tb_order o ,tb_order_item oi,tb_goods g" +
            " where" +
            " o.order_id = oi.order_id" +
            " AND" +
            " o.seller_id = #{sellerId}" +
            " AND" +
            " oi.goods_id = g.id" +
            " AND " +
            " o.`status` = '2'" +
            " AND " +
            " o.payment_time" +
            " BETWEEN" +
            " #{startTime}" +
            " AND" +
            " #{endTime}" +
            " GROUP BY" +
            " oi.goods_id;")
    List<OrderCount> selectByPaymentTime(@Param("sellerId") String sellerId,@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 查询指定时间内所有商家的订单统计
     * @param startTime
     * @param endTime
     * @return
     */
    @Select("select " +
            "g.goods_name goodsName,count(*) count,sum(o.payment) saleRoom,g.is_marketable status,o.seller_id sellerId" +
            " from " +
            " tb_order o ,tb_order_item oi,tb_goods g" +
            " where" +
            " o.order_id = oi.order_id" +
            " AND" +
            " oi.goods_id = g.id" +
            " AND " +
            " o.`status` = '2'" +
            " AND " +
            " o.seller_id != 'null' " +
            " AND " +
            " o.payment_time" +
            " BETWEEN" +
            " #{startTime}" +
            " AND" +
            " #{endTime}" +
            " GROUP BY" +
            " oi.goods_id;")
    List<OrderCount> selectByPaymentTimeAll(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 查询指定商家指定时间内的所有商品id
     * @param sellerId
     * @param startTime
     * @param endTime
     * @return
     */
    @Select(" select" +
            " g.goods_name goodsName" +
            " from" +
            " tb_order o ,tb_order_item oi,tb_goods g" +
            " where" +
            " o.order_id = oi.order_id" +
            " AND" +
            " oi.goods_id = g.id" +
            " AND" +
            " o.`status` = '2'" +
            " AND" +
            " o.seller_id = #{sellerId}" +
            " AND" +
            " o.payment_time" +
            " BETWEEN" +
            " #{startTime}" +
            " AND" +
            " #{endTime}" +
            " GROUP BY" +
            " oi.goods_id;")
    List<String> selectGoodsNameByPaymentTime(@Param("sellerId") String sellerId,@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 查询商品指定时间的销售额
     * @param sellerId
     * @param goodsName
     * @param payTime
     * @return
     */
    @Select(" select" +
            " sum(oi.num) payment" +
            " from" +
            " tb_order o ,tb_order_item oi,tb_goods g" +
            " where" +
            " o.order_id = oi.order_id" +
            " AND" +
            " oi.goods_id = g.id" +
            " AND" +
            " o.`status` = '2'" +
            " AND" +
            " o.seller_id = #{sellerId}" +
            " AND" +
            " g.goods_name = #{goodsName}" +
            " AND" +
            " o.payment_time like #{payTime}" +
            " GROUP BY" +
            " oi.goods_id;")
    List<Integer> selectGoodsNamePayMentByPaymentTime(@Param("sellerId") String sellerId,@Param("goodsName") String goodsName,@Param("payTime")String payTime);


    @Update("update tb_order set status=#{status} where order_id=#{orderId}")
    Integer updateOrder(@Param("status") String status, @Param("orderId") long orderId);


}