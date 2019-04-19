package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.content.service.ItemCatService;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;
import entity.PageResult;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

/**
 * 业务逻辑实现
 * @author Steven
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	Logger logger = Logger.getLogger(ItemCatServiceImpl.class);

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
		itemCatMapper.insertSelective(itemCat);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat){
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
		List<TbItemCat> list = getTbItemCats(parentId);

		//查询所有商品分类
		List<TbItemCat> all = this.findAll();
		for (TbItemCat itemCat : all) {
			redisTemplate.boundHashOps("itemCats").put(itemCat.getName(),itemCat.getTypeId());
		}
		return list;
    }

	private List<TbItemCat> getTbItemCats(Long parentId) {
		TbItemCat where = new TbItemCat();
		where.setParentId(parentId);
		return itemCatMapper.select(where);
	}

	/*@Override
	public List initCategoryList() {
		List list = new ArrayList();
		List<TbItemCat> topItemCatList = getTbItemCats(0L);
		Map<String,Object> map;
		for (TbItemCat tbItemCat : topItemCatList) {
			map = new HashMap<>();
			List<TbItemCat> childItemCat = getTbItemCats(tbItemCat.getId());
			Map<String,Object> map2;
			List list2 = new ArrayList();
			for (TbItemCat itemCat : childItemCat) {
				map2 = new HashMap<>();
				map2.put("itemCat",itemCat);
				List<TbItemCat> itemCatList = getTbItemCats(itemCat.getId());
				map2.put("list",itemCatList);
				list2.add(map2);
			}
			map.put("itemCat",tbItemCat);
			map.put("list",list2);
			list.add(map);
		}
		return list;
	}
*/
	@Override
	public List<TbItemCat>  findItemCatList() {
		//从缓存中查询首页商品分类
		List<TbItemCat>  catList = (List<TbItemCat>) redisTemplate.boundHashOps("itemCats").get("indexItemCat");
		List list = new ArrayList();
		if (catList==null) {
			List<TbItemCat> topItemCatList = getTbItemCats(0L);
			Map<String,Object> map;
			for (TbItemCat tbItemCat : topItemCatList) {
				map = new HashMap<>();
				List<TbItemCat> childItemCat = getTbItemCats(tbItemCat.getId());
				Map<String,Object> map2;
				List list2 = new ArrayList();
				for (TbItemCat itemCat : childItemCat) {
					map2 = new HashMap<>();
					map2.put("itemCat",itemCat);
					List<TbItemCat> itemCatList = getTbItemCats(itemCat.getId());
					map2.put("list",itemCatList);
					list2.add(map2);
				}
				map.put("itemCat",tbItemCat);
				map.put("list",list2);
				list.add(map);
				//存入缓存
				redisTemplate.boundHashOps("itemCats").put("indexItemCat",list);
			}
		}
		logger.info("从缓存中读取分类");
		return catList;
	}

}
