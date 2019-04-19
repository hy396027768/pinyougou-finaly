package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 业务逻辑实现
 * @author Steven
 *
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	@Autowired
	private TbSpecificationOptionMapper optionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.select(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		
		PageResult<TbTypeTemplate> result = new PageResult<TbTypeTemplate>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //查询数据
        List<TbTypeTemplate> list = typeTemplateMapper.select(null);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbTypeTemplate> info = new PageInfo<TbTypeTemplate>(list);
        result.setTotal(info.getTotal());
		return result;
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {

		//设置默认状态为:未审核

		typeTemplate.setStatus("0");
		typeTemplateMapper.insertSelective(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		//修改以后设置初始化状态为0
		typeTemplate.setStatus("0");
		typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		//数组转list
        List longs = Arrays.asList(ids);
        //构建查询条件
        Example example = new Example(TbTypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", longs);

        //跟据查询条件删除数据
        typeTemplateMapper.deleteByExample(example);
	}
	
	
	@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageResult<TbTypeTemplate> result = new PageResult<TbTypeTemplate>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //构建查询条件
        Example example = new Example(TbTypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						//如果字段不为空
			if (typeTemplate.getName()!=null && typeTemplate.getName().length()>0) {
				criteria.andLike("name", "%" + typeTemplate.getName() + "%");
			}
			//如果字段不为空
			if (typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0) {
				criteria.andLike("specIds", "%" + typeTemplate.getSpecIds() + "%");
			}
			//如果字段不为空
			if (typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0) {
				criteria.andLike("brandIds", "%" + typeTemplate.getBrandIds() + "%");
			}
			//如果字段不为空
			if (typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0) {
				criteria.andLike("customAttributeItems", "%" + typeTemplate.getCustomAttributeItems() + "%");
			}
			if (typeTemplate.getStatus() != null && typeTemplate.getStatus().length() > 0){
			    criteria.andEqualTo("status", typeTemplate.getStatus());
			}
	
		}

        //查询数据
        List<TbTypeTemplate> list = typeTemplateMapper.selectByExample(example);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbTypeTemplate> info = new PageInfo<TbTypeTemplate>(list);
        result.setTotal(info.getTotal());

        //saveToRedis(),更新缓存
		saveToRedis();
		
		return result;
	}

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 更新缓存
	 */
	private void saveToRedis() {
		List<TbTypeTemplate> all = this.findAll();
		for (TbTypeTemplate typeTemplate : all) {
			//缓存品牌列表
			List<Map> brandIds = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
			redisTemplate.boundHashOps("brandIds").put(typeTemplate.getId(), brandIds);

			//查询规格与选项列表
			List<Map> specIds = this.findSpecList(typeTemplate.getId());
			//更新规格缓存
			redisTemplate.boundHashOps("specIds").put(typeTemplate.getId(), specIds);
		}
	}


	@Override
    public List<Map> findSpecList(Long id) {
		TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
		if(typeTemplate != null){
			//先把模板的规格名称列表完成转换
			List<Map> maps = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
			//查询规格选项列表
			for (Map map : maps) {
				//查询列表
				TbSpecificationOption where = new TbSpecificationOption();
				where.setSpecId(new Long(map.get("id").toString()));
				List<TbSpecificationOption> options = optionMapper.select(where);
				//组装选项列表数据
				map.put("options", options);
			}
			return maps;
		}
		return null;
    }

    @Override
    public void updateStatus(Long[] ids, String status) {

		TbTypeTemplate record=new TbTypeTemplate();
		record.setStatus(status);


		Example example =new Example(TbTypeTemplate.class);

		Example.Criteria criteria = example.createCriteria();

		List longs = Arrays.asList(ids);

		criteria.andIn("id",longs );

		typeTemplateMapper.updateByExampleSelective(record, example);
    }

    @Override
    public List<TbTypeTemplate> findItemListByGoodsIdsAndStatus(Long[] Ids, String status) {

		Example example =new Example(TbTypeTemplate.class);

		Example.Criteria criteria = example.createCriteria();

		criteria.andEqualTo("status", status);

		List longs = Arrays.asList(Ids);

		criteria.andIn("id", longs);

		return typeTemplateMapper.selectByExample(example);

    }

}
