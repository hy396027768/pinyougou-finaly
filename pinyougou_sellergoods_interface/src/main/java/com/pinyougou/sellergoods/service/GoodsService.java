package com.pinyougou.sellergoods.service;
import java.util.List;
import com.pinyougou.pojo.TbGoods;

import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import entity.PageResult;
/**
 * 业务逻辑接口
 * @author Steven
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(Goods goods);
	
	
	/**
	 * 修改
	 */
	public void update(Goods goods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public Goods findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long [] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbGoods goods, int pageNum,int pageSize);

	/**
	 * 跟据id列表，更新状态
	 * @param ids 审核的商品列表
	 * @param status 审核的状态
	 */
	public void updateStatus(Long[] ids,String status);

	/**
	 * 跟据SPU-ID列表和状态，查询SKU列表
	 * @param goodsIds
	 * @param status
	 * @return
	 */
	public List<TbItem> findItemListByGoodsIdsAndStatus(Long[] goodsIds, String status);

	/**
	 * 商品上下架
	 * @param tbGoods
	 */
	void shopUpAndDown(TbGoods tbGoods);


}
