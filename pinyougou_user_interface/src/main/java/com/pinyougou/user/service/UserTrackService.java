package com.pinyougou.user.service;

import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbCollect;

import java.util.List;

public interface UserTrackService {
    void track(Long id, String name);

    void collect(Long id, String name);

    /**
     * 根据用户查询足迹
     * @param userId
     * @return
     */
    public List<TbCollect> findListByUserId(String userId );

    //取消收藏
    void cancleCol(Long id);
}
