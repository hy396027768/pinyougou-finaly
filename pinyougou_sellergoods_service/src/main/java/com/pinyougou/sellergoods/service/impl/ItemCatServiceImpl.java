package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.ItemCatService;
import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 业务逻辑实现
 * @author Steven
 *
 */
@Service(timeout = 60000)
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.select(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		
		PageResult<TbItemCat> result = new PageResult<TbItemCat>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //查询数据
        List<TbItemCat> list = itemCatMapper.select(null);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbItemCat> info = new PageInfo<TbItemCat>(list);
        result.setTotal(info.getTotal());
		return result;
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		//设置初始默认状态
		itemCat.setTypeStatus("0");
		itemCatMapper.insertSelective(itemCat);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat){

		//设置初始默认状态
		itemCat.setTypeStatus("0");
		itemCatMapper.updateByPrimaryKeySelective(itemCat);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id){
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		//数组转list
        List longs = Arrays.asList(ids);
        //构建查询条件
        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", longs);

        //跟据查询条件删除数据
        itemCatMapper.deleteByExample(example);
	}
	
	
	@Override
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageResult<TbItemCat> result = new PageResult<TbItemCat>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //构建查询条件
        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();
		
		if(itemCat!=null){			
						//如果字段不为空
			if (itemCat.getName()!=null && itemCat.getName().length()>0) {
				criteria.andLike("name", "%" + itemCat.getName() + "%");
			}

			if (itemCat.getTypeStatus() != null && itemCat.getTypeStatus().length() > 0){
				criteria.andEqualTo("typeStatus", itemCat.getTypeStatus());
			}

			if (itemCat.getParentId()!=null){
				criteria.andEqualTo("parentId", itemCat.getParentId());

			}
	
		}

        //查询数据
        List<TbItemCat> list = itemCatMapper.selectByExample(example);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbItemCat> info = new PageInfo<TbItemCat>(list);
        result.setTotal(info.getTotal());
		
		return result;
	}

	@Override
	public List<TbItemCat> findByParentId(Long parentId) {

		//组装查询条件
		TbItemCat where=new TbItemCat();

		where.setParentId(parentId);

		List<TbItemCat> catList = itemCatMapper.select(where);

		//将商品分类数据放入缓存,已分类名称作为key,以模板ID作为值
		List<TbItemCat>itemCats=findAll();

		for (TbItemCat itemCat : itemCats) {

			redisTemplate.boundHashOps("itemCat").put(itemCat.getName(),itemCat.getTypeId());
		}

		return catList;
	}

	@Override
	public void updateStatus(Long[] ids, String status) {

		TbItemCat record =new TbItemCat();

		record.setTypeStatus(status);
		//组装查询条件
		Example example=new Example(TbItemCat.class);

		Example.Criteria criteria = example.createCriteria();

		List longs = Arrays.asList(ids);

		criteria.andIn("id",longs);

		itemCatMapper.updateByExampleSelective(record,example);
	}

}
