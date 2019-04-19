package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojogroup.Address;
import com.pinyougou.user.service.AddressService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.UnsupportedEncodingException;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Reference
    private AddressService addressService;


    /**
     * 查询对应用户的地址信息
     * @return
     */
    @RequestMapping("/findListByUserId")
    public List<Address> findListByUserId(){
        // 得到登录用户用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.findListByUserId(userName);
    }

    /**
     *修改地址的是否为默认值
     * @param id
     * @return
     */
    @RequestMapping("/updateIsDefault")
    public Result updateIsDefault(Long id){
        try {
            //获取用户名
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            addressService.updateIsDefault(userName,id);
            return new Result(true,"修改默认成功" );
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改默认失败！" );
        }
    }
    /**
     * 添加地址
     */
    @RequestMapping("/add")
    public Result saveAddress(@RequestBody TbAddress address){
        try {
            //获取用户名
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            address.setUserId(userName);
            addressService.add(address);
            return new Result(true,"添加成功" );
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败" );
        }
    }



    /**
     * 删除指定id的地址
     * @param id
     * @return
     */
    @RequestMapping("/deleteById")
    public Result deleteById(Long id){
        try {
            addressService.deleteById(id);
            return new Result(true,"删除成功" );
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败" );
        }
    }

    /**
     * 查询指定id对应的地址信息
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Address findOne(Long id){
        return addressService.findOne(id);
    }

    /**
     * 修改地址属性
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbAddress address){
        try {
            addressService.update(address);
            return new Result(true,"修改成功" );
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败" );
        }
    }
}
