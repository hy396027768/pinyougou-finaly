package com.pinyougou.user.service.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.pinyougou.mapper.TbAreasMapper;
import com.pinyougou.mapper.TbCitiesMapper;
import com.pinyougou.mapper.TbProvincesMapper;
import com.pinyougou.pojo.TbAreas;
import com.pinyougou.pojo.TbCities;
import com.pinyougou.pojo.TbProvinces;
import com.pinyougou.pojogroup.Address;
import com.pinyougou.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbAddressMapper;
import com.pinyougou.pojo.TbAddress;
import entity.PageResult;

/**
 * 业务逻辑实现
 * @author Steven
 *
 */
@Service(timeout = 10000)
public class AddressServiceImpl implements AddressService {

	@Autowired
	private TbAddressMapper addressMapper;

	@Autowired
	private TbProvincesMapper provincesMapper;

	@Autowired
	private TbCitiesMapper citiesMapper;

	@Autowired
	private TbAreasMapper areasMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbAddress> findAll() {
		return addressMapper.select(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		
		PageResult<TbAddress> result = new PageResult<TbAddress>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //查询数据
        List<TbAddress> list = addressMapper.select(null);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbAddress> info = new PageInfo<TbAddress>(list);
        result.setTotal(info.getTotal());
		return result;
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbAddress address) {

		//设置是否默认，默认为否
		address.setIsDefault("0");
		//设置创建时间
		address.setCreateDate(new Date());
		addressMapper.insertSelective(address);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbAddress address){
		addressMapper.updateByPrimaryKeySelective(address);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Address findOne(Long id){

		Address add = new Address();

		TbAddress address = addressMapper.selectByPrimaryKey(id);

		//查询省份
		TbProvinces provinces = null;
		if (address.getProvinceId() !=null){
			Example example1 = new Example(TbProvinces.class);
			Example.Criteria criteria1 = example1.createCriteria();
			criteria1.andEqualTo("provinceid", address.getProvinceId());
			provinces = provincesMapper.selectByExample(example1).get(0);


		}

		//查询市区
		TbCities cities = null;
		if (address.getCityId()!=null){
			Example example2 = new Example(TbCities.class);
			Example.Criteria criteria2 = example2.createCriteria();
			criteria2.andEqualTo("cityid", address.getCityId());
			cities = (TbCities)citiesMapper.selectByExample(example2).get(0);
		}

		//查询区县
		TbAreas areas = null;
		if (address.getTownId() !=null){
			Example example3 = new Example(TbAreas.class);
			Example.Criteria criteria3 = example3.createCriteria();
			criteria3.andEqualTo("areaid", address.getTownId());
			areas = (TbAreas)areasMapper.selectByExample(example3).get(0);
		}
		//封装数据
		add.setAddress(address);
		add.setProvinces(provinces);
		add.setCities(cities);
		add.setAreas(areas);


		return add;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		//数组转list
        List longs = Arrays.asList(ids);
        //构建查询条件
        Example example = new Example(TbAddress.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", longs);

        //跟据查询条件删除数据
        addressMapper.deleteByExample(example);
	}
	
	
	@Override
	public PageResult findPage(TbAddress address, int pageNum, int pageSize) {
		PageResult<TbAddress> result = new PageResult<TbAddress>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //构建查询条件
        Example example = new Example(TbAddress.class);
        Example.Criteria criteria = example.createCriteria();
		
		if(address!=null){			
						//如果字段不为空
			if (address.getUserId()!=null && address.getUserId().length()>0) {
				criteria.andLike("userId", "%" + address.getUserId() + "%");
			}
			//如果字段不为空
			if (address.getProvinceId()!=null && address.getProvinceId().length()>0) {
				criteria.andLike("provinceId", "%" + address.getProvinceId() + "%");
			}
			//如果字段不为空
			if (address.getCityId()!=null && address.getCityId().length()>0) {
				criteria.andLike("cityId", "%" + address.getCityId() + "%");
			}
			//如果字段不为空
			if (address.getTownId()!=null && address.getTownId().length()>0) {
				criteria.andLike("townId", "%" + address.getTownId() + "%");
			}
			//如果字段不为空
			if (address.getMobile()!=null && address.getMobile().length()>0) {
				criteria.andLike("mobile", "%" + address.getMobile() + "%");
			}
			//如果字段不为空
			if (address.getAddress()!=null && address.getAddress().length()>0) {
				criteria.andLike("address", "%" + address.getAddress() + "%");
			}
			//如果字段不为空
			if (address.getContact()!=null && address.getContact().length()>0) {
				criteria.andLike("contact", "%" + address.getContact() + "%");
			}
			//如果字段不为空
			if (address.getIsDefault()!=null && address.getIsDefault().length()>0) {
				criteria.andLike("isDefault", "%" + address.getIsDefault() + "%");
			}
			//如果字段不为空
			if (address.getNotes()!=null && address.getNotes().length()>0) {
				criteria.andLike("notes", "%" + address.getNotes() + "%");
			}
			//如果字段不为空
			if (address.getAlias()!=null && address.getAlias().length()>0) {
				criteria.andLike("alias", "%" + address.getAlias() + "%");
			}
	
		}

        //查询数据
        List<TbAddress> list = addressMapper.selectByExample(example);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbAddress> info = new PageInfo<TbAddress>(list);
        result.setTotal(info.getTotal());
		
		return result;
	}

    @Override
    public List<Address> findListByUserId(String userId) {

		List<Address> addressList = new ArrayList();

		//查询地址基础信息
		TbAddress where = new TbAddress();
		where.setUserId(userId);
		List<TbAddress> addresses = addressMapper.select(where);

		Address add = null;
		for (TbAddress address : addresses) {
			add = new Address();
			//查询数据

			//查询省份
			TbProvinces provinces = null;
			if (address.getProvinceId() !=null){
				Example example1 = new Example(TbProvinces.class);
				Example.Criteria criteria1 = example1.createCriteria();
				criteria1.andEqualTo("provinceid", address.getProvinceId());
				provinces = provincesMapper.selectByExample(example1).get(0);


			}

			//查询市区
			TbCities cities = null;
			if (address.getCityId()!=null){
				Example example2 = new Example(TbCities.class);
				Example.Criteria criteria2 = example2.createCriteria();
				criteria2.andEqualTo("cityid", address.getCityId());
				cities = (TbCities)citiesMapper.selectByExample(example2).get(0);
			}

			//查询区县
			TbAreas areas = null;
			if (address.getTownId() !=null){
				Example example3 = new Example(TbAreas.class);
				Example.Criteria criteria3 = example3.createCriteria();
				criteria3.andEqualTo("areaid", address.getTownId());
				areas = (TbAreas)areasMapper.selectByExample(example3).get(0);
			}

			//将电话号码隐藏一部分
			String mobile = address.getMobile();
			if (mobile.length() == 11){
				mobile = address.getMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
			}else if (mobile.length() == 6){
				mobile = address.getMobile().replaceAll("(\\d{2})\\d{2}(\\d{2})", "$1**$2");
			}
			address.setMobile(mobile);
			//封装数据
			add.setAddress(address);
			add.setProvinces(provinces);
			add.setCities(cities);
			add.setAreas(areas);
			addressList.add(add);
		}
		return addressList;
    }

	/**
	 * 修改地址的是否为默认值
	 * @param userName
	 * @param id
	 */
	@Override
	public void updateIsDefault(String userName,Long id) {

		//先将用户所有地址查询出来，将isdefault属性设置为0，将id对应的设置为1，然后再更新到数据库
		TbAddress where = new TbAddress();
		where.setUserId(userName);
		List<TbAddress> addressList = addressMapper.select(where);
		for (TbAddress tbAddress : addressList) {
			if ((tbAddress.getId().equals(id))) {
				//要修改的地址
				tbAddress.setIsDefault("1");
			}else{
				//不要修改的地址
				tbAddress.setIsDefault("0");
			}
			//将addressList存到数据库中
			addressMapper.updateByPrimaryKey(tbAddress);
		}
	}

	/**
	 * 删除指定的地址
	 * @param id
	 */
	@Override
	public void deleteById(Long id) {
		addressMapper.deleteByPrimaryKey(id);
	}
}
