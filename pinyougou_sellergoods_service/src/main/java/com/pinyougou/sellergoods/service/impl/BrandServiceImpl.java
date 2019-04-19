package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;

/**
 * 业务逻辑实现
 * @author Steven
 *
 */
@Service
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper brandMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbBrand> findAll() {
		return brandMapper.select(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		
		PageResult<TbBrand> result = new PageResult<TbBrand>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //查询数据
        List<TbBrand> list = brandMapper.select(null);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbBrand> info = new PageInfo<TbBrand>(list);
        result.setTotal(info.getTotal());
		return result;
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbBrand brand) {
	    brand.setStatus("0");
		brandMapper.insertSelective(brand);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbBrand brand){
        if (brand.getStatus().equals("")) {
            throw new RuntimeException("请选择状态");
        }
		brandMapper.updateByPrimaryKeySelective(brand);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbBrand findOne(Long id){
		return brandMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		//数组转list
        List longs = Arrays.asList(ids);
        //构建查询条件
        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", longs);

        //跟据查询条件删除数据
        brandMapper.deleteByExample(example);
	}
	
	
	@Override
	public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
		PageResult<TbBrand> result = new PageResult<TbBrand>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //构建查询条件
        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();
        //查询待审核的商品
        criteria.andEqualTo("status","0");
		if(brand!=null){			
						//如果字段不为空
			if (brand.getName()!=null && brand.getName().length()>0) {
				criteria.andLike("name", "%" + brand.getName() + "%");
			}
			//如果字段不为空
			if (brand.getFirstChar()!=null && brand.getFirstChar().length()>0) {
				criteria.andLike("firstChar", "%" + brand.getFirstChar() + "%");
			}
	
		}
        //查询数据
        List<TbBrand> list = brandMapper.selectByExample(example);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbBrand> info = new PageInfo<TbBrand>(list);
        result.setTotal(info.getTotal());

		return result;
	}

    @Override
    public PageResult search(TbBrand brand, int pageNum, int pageSize) {
        PageResult<TbBrand> result = new PageResult<TbBrand>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //构建查询条件
        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();
        if(brand!=null){
            //如果字段不为空
            if (brand.getName()!=null && brand.getName().length()>0) {
                criteria.andLike("name", "%" + brand.getName() + "%");
            }
            //如果字段不为空
            if (brand.getFirstChar()!=null && brand.getFirstChar().length()>0) {
                criteria.andLike("firstChar", "%" + brand.getFirstChar() + "%");
            }

        }
        //查询数据
        List<TbBrand> list = brandMapper.selectByExample(example);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbBrand> info = new PageInfo<TbBrand>(list);
        result.setTotal(info.getTotal());

        return result;
    }

}
