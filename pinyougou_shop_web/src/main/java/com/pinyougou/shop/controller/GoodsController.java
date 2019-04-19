package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;

/**
 * 请求处理器
 * @author Steven
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	//注入消息中间件对象
	@Autowired
	private JmsTemplate jmsTemplate;

	//生成详情页面的订阅消息对象
	@Autowired
	private Destination topicPageDestination;
	//删除详情页面的订阅消息对象
	@Autowired
	private Destination topicPageDeleteDestination;

	//添加索引库
	@Autowired
	private Destination queueDestination;

	//删除索引
	@Autowired
	private Destination queueDeleteDestination;

	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			//绑定商家
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			goods.getGoods().setSellerId(sellerId);

			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			//将要更新的商品信息
			Goods beUpdate = goodsService.findOne(goods.getGoods().getId());
			//如果要修改的商品不属于当前登录商家
			if(!sellerId.equals(beUpdate.getGoods().getSellerId())){
				return new Result(false, "请注意你的言行，这是一个非法操作！");
			}
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(sellerId);
		return goodsService.findPage(goods, page, rows);		
	}

	/**
	 * 商品上下架
	 * @param goods
	 * @return
	 */
	@RequestMapping("/shopUpAndDown")
	public Result shopUpAndDown(@RequestBody Goods goods) {

		//获取商品的对象
		TbGoods tbGoods = goods.getGoods();

		//判断商品是否已经审核
		if ("1".equals(tbGoods.getAuditStatus())) {

			try {
				goodsService.shopUpAndDown(tbGoods);


				if ("1".equals(tbGoods.getIsMarketable())) {

					//商品下架成功后发送jms信息 删除静态页面
					jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createObjectMessage(tbGoods.getId());
						}
					});

					//商品下架成功后发送jms消息 删除solr索引库
					jmsTemplate.send(queueDeleteDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createObjectMessage(tbGoods.getId());
						}

					});


					return new Result(true, "商品下架成功");

				} else {

					//商品上架成功后发送 jms信息 生成静态页面
					jmsTemplate.send(topicPageDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createObjectMessage(tbGoods.getId());
						}
					});

					//商品上架成功后添加solr索引库
					jmsTemplate.send(queueDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createObjectMessage(tbGoods.getId());
						}
					});
					return new Result(true, "商品上架成功");
				}

			} catch (Exception e) {
				e.printStackTrace();

				if ("1".equals(tbGoods.getIsMarketable())) {
					return new Result(false, "商品下架请求超时");
				} else {
					return new Result(true, "商品上架请求超时");
				}
			}
		}else {
			//商品还未审核
			return new Result(false, "商品还未审核不允许操作");
		}



	}
	
}
