package com.pinyougou.user.service.impl;
import java.security.Security;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.user.service.UserService;
import entity.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;

/**
 * 业务逻辑实现
 * @author Steven
 *
 */
@Service(timeout = 10000)
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;

	@Autowired
	private TbJobMapper jobMapper;

	@Autowired
	private TbProvincesMapper provincesMapper;

	@Autowired
	private TbCitiesMapper citiesMapper;

	@Autowired
	private TbAreasMapper areasMapper;

	@Autowired
	private TbCollectMapper tbCollectMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbUser> findAll() {
		return userMapper.select(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		
		PageResult<TbUser> result = new PageResult<TbUser>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //查询数据
        List<TbUser> list = userMapper.select(null);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbUser> info = new PageInfo<TbUser>(list);
        result.setTotal(info.getTotal());
		return result;
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbUser user) {
		user.setCreated(new Date());
		user.setUpdated(user.getCreated());
		//密码加密
		user.setPassword(DigestUtils.md5Hex(user.getPassword()));
		userMapper.insertSelective(user);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbUser user){

			userMapper.updateByPrimaryKeySelective(user);


	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbUser findOne(Long id){
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		//数组转list
        List longs = Arrays.asList(ids);
        //构建查询条件
        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", longs);

        //跟据查询条件删除数据
        userMapper.deleteByExample(example);
	}


	@Override
	public PageResult findPage(TbUser user, int pageNum, int pageSize) {
		PageResult<TbUser> result = new PageResult<TbUser>();
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);

        //构建查询条件
        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();
		
		if(user!=null){			
						//如果字段不为空
			if (user.getUsername()!=null && user.getUsername().length()>0) {
				criteria.andLike("username", "%" + user.getUsername() + "%");
			}
			//如果字段不为空
			if (user.getPassword()!=null && user.getPassword().length()>0) {
				criteria.andLike("password", "%" + user.getPassword() + "%");
			}
			//如果字段不为空
			if (user.getPhone()!=null && user.getPhone().length()>0) {
				criteria.andLike("phone", "%" + user.getPhone() + "%");
			}
			//如果字段不为空
			if (user.getEmail()!=null && user.getEmail().length()>0) {
				criteria.andLike("email", "%" + user.getEmail() + "%");
			}
			//如果字段不为空
			if (user.getSourceType()!=null && user.getSourceType().length()>0) {
				criteria.andLike("sourceType", "%" + user.getSourceType() + "%");
			}
			//如果字段不为空
			if (user.getNickName()!=null && user.getNickName().length()>0) {
				criteria.andLike("nickName", "%" + user.getNickName() + "%");
			}
			//如果字段不为空
			if (user.getName()!=null && user.getName().length()>0) {
				criteria.andLike("name", "%" + user.getName() + "%");
			}
			//如果字段不为空
			if (user.getStatus()!=null && user.getStatus().length()>0) {
				criteria.andLike("status", "%" + user.getStatus() + "%");
			}
			//如果字段不为空
			if (user.getHeadPic()!=null && user.getHeadPic().length()>0) {
				criteria.andLike("headPic", "%" + user.getHeadPic() + "%");
			}
			//如果字段不为空
			if (user.getQq()!=null && user.getQq().length()>0) {
				criteria.andLike("qq", "%" + user.getQq() + "%");
			}
			//如果字段不为空
			if (user.getIsMobileCheck()!=null && user.getIsMobileCheck().length()>0) {
				criteria.andLike("isMobileCheck", "%" + user.getIsMobileCheck() + "%");
			}
			//如果字段不为空
			if (user.getIsEmailCheck()!=null && user.getIsEmailCheck().length()>0) {
				criteria.andLike("isEmailCheck", "%" + user.getIsEmailCheck() + "%");
			}
			//如果字段不为空
			if (user.getSex()!=null && user.getSex().length()>0) {
				criteria.andLike("sex", "%" + user.getSex() + "%");
			}
	
		}

        //查询数据
        List<TbUser> list = userMapper.selectByExample(example);
        //保存数据列表
        result.setRows(list);

        //获取总记录数
        PageInfo<TbUser> info = new PageInfo<TbUser>(list);
        result.setTotal(info.getTotal());
		
		return result;
	}

	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination queueSmsDestination;

	@Override
	public void createSmsCode(String phone) {
		//生成验证码
		String code = (long) (Math.random() * 1000000) + "";
		System.out.println("验证码为：" + code);
		//把验证码存入redis
		redisTemplate.boundHashOps("smsCode").put(phone, code);

		//发送mq消息，用于发送短信
		jmsTemplate.send(queueSmsDestination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("mobile", phone);//手机号
				mapMessage.setString("template_code", "SMS_135802012");//模板编号
				mapMessage.setString("sign_name", "黑马");//签名
				Map m=new HashMap<>();
				m.put("number", code);
				//"{'number':'294892'}"
				mapMessage.setString("param", JSON.toJSONString(m));//参数
				return mapMessage;
			}
		});
	}

	@Override
	public boolean checkSmsCode(String phone, String code) {
		String smsCode = (String) redisTemplate.boundHashOps("smsCode").get(phone);
		if(smsCode == null){
			return false;
		}
		return smsCode.equals(code);
	}



	///**======================================*/
    //
	///**
	// * 性别统计
	// * @return
	// */
	//@Override
	//public Map<String, Long> sexStatistics() {
    //
	//	Map<String, Long> map = new HashMap<>();
	//	//查询正常状态的男女数量
	//	map = sexStatusMap(map, "1", "man1", "girl1");
	//	//查询已经冻结的男女数量
	//	map = sexStatusMap(map, "0", "man2", "girl2");
    //
	//	//查询所有的总数量
	//	map.put("count", count());
    //
	//	return map;
	//}
    //
	//// 查询用户的总数
	//public Long count() {
	//	return userMapper.count();
	//}
    //
    //
	////查询男女的数据
	//public    Map<String, Long> sexStatusMap( Map<String, Long> map ,String a,String man, String girl) {
    //
	//	List<Map<String, Object>> maps = userMapper.sexStatistics(a);
    //
	//	if (maps != null && maps.size() > 0) {
	//		for (Map<String, Object> mapSex : maps) {
    //
	//			String sex = (String) mapSex.get("sex");
	//			if ("1".equals(sex)) {
	//				map.put(man, (Long) mapSex.get("calculate"));
	//			} else {
	//				map.put(girl, (Long) mapSex.get("calculate"));
	//			}
	//		}
	//	}
	//	return map;
	//}
    //
    //
	///**
	// * 查询基础数据统计
	// * @return
	// */
	//@Override
	//public Map<String, List> basic() {
    //
	//	Map<String, List> map = new HashMap<>();
    //
	//	//查询性别
	//	List<Map<String, Object>> sex = userMapper.statistics1();
    //
	//	map.put("sex", sex);
    //
	//	//查询状态
	//	List<Map<String, Object>> status = userMapper.statistics2();
	//	map.put("status", status);
    //
	//	//查询会员等级
	//	List<Map<String, Object>> userLevel = userMapper.statistics3();
	//	map.put("userLevel", userLevel);
    //
	//	List list = new ArrayList();
    //
	//	list.add(count());
    //
	//	map.put("count", list);
    //
	//	return map;
	//}

	@Override
	public int updateUserMessage(TbUser user) {

		Example example = new Example(TbUser.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("username",user.getUsername());
		int i = userMapper.updateByExampleSelective(user, example);

		return i;
	}

	@Override
	public List<TbJob> findJob() {
		List<TbJob> select = jobMapper.select(null);

		return select;
	}


	@Override
	public TbUser findOneByUsername(String name) {
		TbUser record = new TbUser();

		record.setUsername(name);
		TbUser tbUser = userMapper.selectOne(record);

		return tbUser;
	}


	@Override
	public Map<String, Object> findAddressId(String province,String city,String areas) {

		Map<String,Object> map = new HashMap<>();

		TbProvinces where1 = new TbProvinces();
		where1.setProvince(province);
		TbProvinces tbProvinces = provincesMapper.selectOne(where1);

		TbCities where2 = new TbCities();
		where2.setCity(city);
		TbCities tbCities = citiesMapper.selectOne(where2);

		TbAreas where3 = new TbAreas();
		where3.setArea(areas);
		TbAreas tbAreas = areasMapper.selectOne(where3);

		map.put("provinceid",tbProvinces.getProvinceid());
		map.put("citiesid",tbCities.getCityid());
		map.put("areasid",tbAreas.getAreaid());

		return map;
	}

    /**======================================*/

    /**
     * 性别统计
     *
     * @return
     */
    @Override
    public Map<String, Long> sexStatistics() {

        Map<String, Long> map = new HashMap<>();
        //查询正常状态的男女数量
        map = sexStatusMap(map, "1", "man1", "girl1");
        //查询已经冻结的男女数量
        map = sexStatusMap(map, "0", "man2", "girl2");

        //查询所有的总数量
        map.put("count", count());

        return map;
    }

    // 查询用户的总数
    public Long count() {
        return userMapper.count();
    }


    //查询男女的数据
    public Map<String, Long> sexStatusMap(Map<String, Long> map, String a, String man, String girl) {

        List<Map<String, Object>> maps = userMapper.sexStatistics(a);

        if (maps != null && maps.size() > 0) {
            for (Map<String, Object> mapSex : maps) {

                String sex = (String) mapSex.get("sex");
                if ("1".equals(sex)) {
                    map.put(man, (Long) mapSex.get("calculate"));
                } else {
                    map.put(girl, (Long) mapSex.get("calculate"));
                }
            }
        }
        return map;
    }


    /**
     * 查询基础数据统计
     *
     * @return
     */
    @Override
    public Map<String, List> basic() {

        Map<String, List> map = new HashMap<>();

        //查询性别
        List<Map<String, Object>> sex = userMapper.statistics1();

        map.put("sex", sex);

        //查询状态
        List<Map<String, Object>> status = userMapper.statistics2();
        map.put("status", status);

        //查询会员等级
        List<Map<String, Object>> userLevel = userMapper.statistics3();
        map.put("userLevel", userLevel);

        List list = new ArrayList();

        list.add(count());

        map.put("count", list);

        return map;
    }


    @Autowired
    private TbBrandMapper tbBrandMapper;
    @Autowired
    private TbSpecificationOptionMapper tbs;

    @Autowired
    private TbTypeTemplateMapper tbTypeTemplateMapper;


    @Override
    public void setBrand(TbBrand brand) {
        tbBrandMapper.insertSelective(brand);
    }

    @Override
    public void setSpecificationOption(TbSpecificationOption obj) {
        tbs.insertSelective(obj);
    }

    @Override
    public void setTypeTemplate(TbTypeTemplate obj) {

        tbTypeTemplateMapper.insertSelective(obj);

    }


}
