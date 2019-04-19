package com.pinyougou.order.service.impl;
import java.math.BigDecimal;
import java.util.*;

import com.pinyougou.mapper.*;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.IdWorker;
import entity.OrderCount;
import entity.OrderGoods;
import entity.OrderResult;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;
import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 业务逻辑实现
 * @author Steven
 *
 */
@Service(timeout = 10000)
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private IdWorker idWorker;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbPayLogMapper payLogMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbSellerMapper sellerMapper;

	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.select(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		
		PageResult<TbOrder> result = new PageResult<TbOrder>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //查询数据
        List<TbOrder> list = orderMapper.select(null);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(list);
        result.setTotal(info.getTotal());
		return result;
	}


	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {
		//1、从redis中把购物车查询出来
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());

		double money = 0.00;  //支付总金额
		List<Long> orderIds = new ArrayList<>();
		//2、拆单保存订单
		for (Cart cart : cartList) {
			TbOrder beSave = new TbOrder();
			//生成订单id
			long orderId = idWorker.nextId();

			//记录订单号
			orderIds.add(orderId);

			beSave.setOrderId(orderId);
			double payment = 0.00;  //每张订单总金额
			beSave.setPaymentType(order.getPaymentType());  //支付方式
			beSave.setStatus("1");  //未付款状态
			beSave.setCreateTime(new Date());  //创建时间
			beSave.setUpdateTime(beSave.getCreateTime()); //更新时间
			beSave.setUserId(order.getUserId());  //订单所属用户
			beSave.setSourceType(order.getSourceType());  //来源
			beSave.setReceiverAreaName(order.getReceiverAreaName());//地址
			beSave.setReceiverMobile(order.getReceiverMobile());//手机号
			beSave.setReceiver(order.getReceiver());//收货人
			beSave.setSellerId(cart.getSellerId());  //商家
			for (TbOrderItem orderItem : cart.getOrderItemList()) {
				//计算总金额
				payment += orderItem.getTotalFee().doubleValue();
				//设置订单商品参数
				long id = idWorker.nextId();
				orderItem.setId(id);
				orderItem.setOrderId(orderId);
				//保存订单商品列表
				orderItemMapper.insertSelective(orderItem);
			}
			//设置总金额
			beSave.setPayment(new BigDecimal(payment));
			//计算总金额
			money += payment;
			//保存订单
			orderMapper.insertSelective(beSave);
		}

		//如果是在线支付
		if("1".equals(order.getPaymentType())){
			TbPayLog payLog=new TbPayLog();
			String outTradeNo=  idWorker.nextId()+"";//支付订单号
			payLog.setOutTradeNo(outTradeNo);//支付订单号
			payLog.setCreateTime(new Date());//创建时间
			//设置支付金额
			payLog.setTotalFee((long)(money * 100));
			payLog.setUserId(order.getUserId());  //用户名
			payLog.setTradeState("0");  //支付状态0未支付 1已支付
			payLog.setPayType("1");  //支付的类型,目前只支持微信
			//把订单id格式化
			String orderList = orderIds.toString().replace("[", "")
					.replace(" ", "").replace("]", "");
			payLog.setOrderList(orderList);
			//保存日志
			payLogMapper.insertSelective(payLog);

			//把日志对象放入redis
			redisTemplate.boundHashOps("pagLogs").put(order.getUserId(), payLog);
		}
		//3、清空购物车
		redisTemplate.boundHashOps("cartList").delete(order.getUserId());

	}

	@Override
	public TbPayLog searchPayLogFromRedis(String userId) {
		TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("pagLogs").get(userId);
		return payLog;
	}

	@Override
	public void updateOrderStatus(String out_trade_no, String transaction_id) {
		//1. 修改支付日志状态
		TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
		payLog.setTradeState("1");  //已支付
		payLog.setPayTime(new Date());
		payLog.setTransactionId(transaction_id);
		payLogMapper.updateByPrimaryKeySelective(payLog);
		//2. 修改关联的订单的状态
		String[] split = payLog.getOrderList().split(",");
		for (String orderId : split) {
			TbOrder beUpdate = new TbOrder();
			beUpdate.setOrderId(new Long(orderId));
			beUpdate.setStatus("2");  //已付款状态
			//更新订单状态
			orderMapper.updateByPrimaryKeySelective(beUpdate);
		}
		//3. 清除缓存中的支付日志对象
		redisTemplate.boundHashOps("pagLogs").delete(payLog.getUserId());

	}

	/**
	 * 查询指定商家订单统计
	 * @param startTime	开始时间
	 * @param endTime		结束时间
	 * @return
	 */
	@Override
	public List<OrderCount> findOrderService(String sellerId, Date startTime, Date endTime) {
		if (sellerId == null || sellerId.equals("admin")){
			//查询运营商指定时间的订单
			return orderMapper.selectByPaymentTimeAll(startTime,endTime);
		}
		//查询商家指定时间的订单
		return orderMapper.selectByPaymentTime(sellerId,startTime,endTime);
	}

	/**
	 *查询指定时间内卖出去的商品名称
	 * @param sellerId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Override
	public List<String> selectGoodsNameByPaymentTime(String sellerId, Date startTime, Date endTime) {
		return orderMapper.selectGoodsNameByPaymentTime(sellerId,startTime ,endTime );
	}

	@Override
	public Integer selectGoodsNamePayMentByPaymentTime(String sellerId, String goodsName, String parTime) {
		List<Integer> list = orderMapper.selectGoodsNamePayMentByPaymentTime(sellerId, goodsName, parTime);
		if (list == null ||list.size()<1){
			return 0;
		}
		return list.get(0);
	}


	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKeySelective(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id){
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		//数组转list
        List longs = Arrays.asList(ids);
        //构建查询条件
        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", longs);

        //跟据查询条件删除数据
        orderMapper.deleteByExample(example);
	}

	@Override
	public PageResult findOrderBySeller(TbSeller seller, int pageNum, int pageSize){
		TbOrder order = new TbOrder();
		if (seller.getSellerId() == null || "".equals(seller.getSellerId())){
			//构建查询条件
			Example example = new Example(TbSeller.class);
			Example.Criteria criteria = example.createCriteria();
			criteria.andLike("nickName", "%" + seller.getNickName() + "%");
		}
		order.setSellerId(seller.getSellerId());

		return findPage(order,pageNum,pageSize);

	}
	
	
	@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageResult<TbOrder> result = new PageResult<TbOrder>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //构建查询条件
        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						//如果字段不为空
			if (order.getPaymentType()!=null && order.getPaymentType().length()>0) {
				criteria.andLike("paymentType", "%" + order.getPaymentType() + "%");
			}
			//如果字段不为空
			if (order.getPostFee()!=null && order.getPostFee().length()>0) {
				criteria.andLike("postFee", "%" + order.getPostFee() + "%");
			}
			//如果字段不为空
			if (order.getStatus()!=null && order.getStatus().length()>0) {
				criteria.andLike("status", "%" + order.getStatus() + "%");
			}
			//如果字段不为空
			if (order.getShippingName()!=null && order.getShippingName().length()>0) {
				criteria.andLike("shippingName", "%" + order.getShippingName() + "%");
			}
			//如果字段不为空
			if (order.getShippingCode()!=null && order.getShippingCode().length()>0) {
				criteria.andLike("shippingCode", "%" + order.getShippingCode() + "%");
			}
			//如果字段不为空
			if (order.getUserId()!=null && order.getUserId().length()>0) {
				criteria.andLike("userId", "%" + order.getUserId() + "%");
			}
			//如果字段不为空
			if (order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0) {
				criteria.andLike("buyerMessage", "%" + order.getBuyerMessage() + "%");
			}
			//如果字段不为空
			if (order.getBuyerNick()!=null && order.getBuyerNick().length()>0) {
				criteria.andLike("buyerNick", "%" + order.getBuyerNick() + "%");
			}
			//如果字段不为空
			if (order.getBuyerRate()!=null && order.getBuyerRate().length()>0) {
				criteria.andLike("buyerRate", "%" + order.getBuyerRate() + "%");
			}
			//如果字段不为空
			if (order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0) {
				criteria.andLike("receiverAreaName", "%" + order.getReceiverAreaName() + "%");
			}
			//如果字段不为空
			if (order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0) {
				criteria.andLike("receiverMobile", "%" + order.getReceiverMobile() + "%");
			}
			//如果字段不为空
			if (order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0) {
				criteria.andLike("receiverZipCode", "%" + order.getReceiverZipCode() + "%");
			}
			//如果字段不为空
			if (order.getReceiver()!=null && order.getReceiver().length()>0) {
				criteria.andLike("receiver", "%" + order.getReceiver() + "%");
			}
			//如果字段不为空
			if (order.getInvoiceType()!=null && order.getInvoiceType().length()>0) {
				criteria.andLike("invoiceType", "%" + order.getInvoiceType() + "%");
			}
			//如果字段不为空
			if (order.getSourceType()!=null && order.getSourceType().length()>0) {
				criteria.andLike("sourceType", "%" + order.getSourceType() + "%");
			}
			//如果字段不为空
			if (order.getSellerId()!=null && order.getSellerId().length()>0) {
				criteria.andEqualTo("sellerId", order.getSellerId());
			}
	
		}

		PageResult pageResult = new PageResult();

        //查询数据
		example.setOrderByClause("createTime ASC");
        List<TbOrder> list = orderMapper.selectByExample(example);
		//设置总记录数
		PageInfo<TbOrder> pageInfo = new PageInfo<TbOrder>(list);
		pageResult.setTotal(pageInfo.getTotal());
		TbOrderItem orderItem = null;
		List<OrderResult> orderResults = new ArrayList<>();

		OrderResult orderResult = null;
		for (TbOrder tbOrder : list) {
			orderItem = new TbOrderItem();
			orderItem.setOrderId(tbOrder.getOrderId());//设置订单ID
			List<TbOrderItem> tbOrderItemList = orderItemMapper.select(orderItem);//查询订单商品数据
			for (TbOrderItem tbOrderItem : tbOrderItemList) {
				TbItem tbItem = new TbItem();
				tbItem.setGoodsId(tbOrderItem.getGoodsId());
				TbItem item = itemMapper.selectByPrimaryKey(tbOrderItem.getItemId());
				TbSeller tbSeller = sellerMapper.selectByPrimaryKey(tbOrderItem.getSellerId());

				//封装数据
				if (orderResult == null){
					orderResult = new OrderResult();

					orderResult.setCreateTime(tbOrder.getCreateTime());
					orderResult.setOrderId(tbOrderItem.getOrderId());
					//商家名称封装
					orderResult.setNickName(tbSeller.getNickName());
				}

				//订单详情表数据封装
				OrderGoods orderGoods = new OrderGoods();
				orderGoods.setTitle(tbOrderItem.getTitle());
				orderGoods.setPrice(tbOrderItem.getPrice());
				orderGoods.setNum(tbOrderItem.getNum());
				orderGoods.setTotalFee(tbOrderItem.getTotalFee());
				orderGoods.setPicPath(tbOrderItem.getPicPath());
				//订单商品状态
				orderGoods.setStatus(tbOrder.getStatus());
				//商品规格属性封装
				orderGoods.setSpec(item.getSpec());

				List<OrderGoods> orderGoodsList = orderResult.getOrderGoods();
				if (orderGoodsList == null){
					orderGoodsList = new ArrayList<>();
				}
				orderGoodsList.add(orderGoods);
				orderResult.setOrderGoods(orderGoodsList);
			}
			orderResults.add(orderResult);
		}
		//设置数据
		pageResult.setRows(orderResults);

		return pageResult;
	}

	@Override
	public Integer updateOrder(String status, long orderId){

		Integer line = orderMapper.updateOrder(status,orderId);


		return line;
	}




}
