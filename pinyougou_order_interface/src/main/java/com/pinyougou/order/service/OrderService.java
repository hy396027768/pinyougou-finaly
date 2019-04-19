package com.pinyougou.order.service;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import entity.OrderCount;
import entity.PageResult;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.pinyougou.pojo.TbSeller;


/**
 * 业务逻辑接口
 * @author Steven
 *
 */
public interface OrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);


	/**
	 * 增加
	*/
	public void add(TbOrder order);


    List<String> selectGoodsNameByPaymentTime(String sellerId, Date startTime, Date endTime);

	Integer selectGoodsNamePayMentByPaymentTime(String sellerId, String goodsName, String parTime);

	/**
	 * 修改
	 */
	public void update(TbOrder order);


	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbOrder findOne(Long id);


	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

    PageResult findOrderBySeller(TbSeller searchMap, int pageNum, int pageSize);

    /**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbOrder order, int pageNum, int pageSize);

	/**
	 * 根据用户查询payLog
	 * @param userId
	 * @return
	 */
	public TbPayLog searchPayLogFromRedis(String userId);

	/**
	 * 修改订单状态
	 * @param out_trade_no 支付订单号
	 * @param transaction_id 微信返回的交易流水号
	 */
	public void updateOrderStatus(String out_trade_no,String transaction_id);

	/**
	 * 查询指定商家的订单统计
	 * @param sellerId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	List<OrderCount> findOrderService(String sellerId, Date startTime, Date endTime);

	Integer updateOrder(String status, long orderId);
}
