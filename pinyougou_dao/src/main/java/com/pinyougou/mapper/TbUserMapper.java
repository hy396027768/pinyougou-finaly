package com.pinyougou.mapper;

import com.github.abel533.mapper.Mapper;
import com.pinyougou.pojo.TbUser;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface TbUserMapper extends Mapper<TbUser> {


    @Select("SELECT sex ,COUNT(*) calculate FROM tb_user WHERE STATUS =#{status}  GROUP BY sex")
    List<Map<String,Object>> sexStatistics(String status);

    @Select("SELECT COUNT(*) FROM tb_user")
    Long count();

    @Select("SELECT sex, COUNT(*) calculate FROM tb_user  GROUP BY sex")
    List<Map<String, Object>> statistics1();

    @Select("SELECT status, COUNT(*) calculate FROM tb_user  GROUP BY status")
    List<Map<String, Object>> statistics2();

    @Select("SELECT user_level, COUNT(*) calculate FROM tb_user  GROUP BY user_level")
    List<Map<String, Object>> statistics3();
}