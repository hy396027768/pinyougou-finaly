package com.pinyougou.user.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbJob;
import com.pinyougou.user.service.AddressService;
import com.pinyougou.user.service.UserService;
import com.pinyougou.utils.PhoneFormatCheckUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;



import com.pinyougou.user.service.AddressService;
import com.pinyougou.user.service.UserService;
import com.pinyougou.utils.PhoneFormatCheckUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;

import entity.PageResult;
import entity.Result;

/**
 * 请求处理器
 *
 * @author Steven
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @Reference
    private OrderService orderService;

    @Reference
    private AddressService addressService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbUser> findAll() {
        return userService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return userService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param user
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbUser user,String code) {
        try {
            //先验证验证码
            if(!userService.checkSmsCode(user.getPhone(),code)){
                return new Result(false, "验证码输入错误！");
            }
            userService.add(user);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param user
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbUser user) {
        try {
            userService.update(user);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbUser findOne(Long id) {
        return userService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            userService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param user
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbUser user, int page, int rows) {
        return userService.findPage(user, page, rows);
    }

    @RequestMapping("sendCode")
    public Result sendCode(String phone){
        try {
            //验证手机号
            if(!PhoneFormatCheckUtils.isPhoneLegal(phone)){
                return new Result(false, "请输入正确的手机号！");
            }
            //生成验证码
            userService.createSmsCode(phone);

            return new Result(true, "验证码已成功发送！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "发送验证码失败！");
    }

    @RequestMapping("/updateOrder")
    public Result updateOrder(String status, long order_id){
        try {
            orderService.updateOrder(status, order_id);
            return new Result(true,"操作完成");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,e.getMessage());
        }
    }

    @RequestMapping("/updateUserMessage")
    public Result updateUserMessage(@RequestBody TbUser user){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        user.setUsername(name);

        try {
            int line = userService.updateUserMessage(user);
            return new Result(true,"提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"提交失败，请重新输入");
        }

    }

    @RequestMapping("/findJob")
    public List<TbJob> findJob(){
        List<TbJob> jobList = userService.findJob();

        return jobList;
    }

    @RequestMapping("/findOneAddress")
    public TbUser findOneByUsername(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        TbUser user = userService.findOneByUsername(name);

        return user;

    }

    //@RequestMapping("/findAddressId")
    //public Map<String,Object> findAddressId(String province,String city,String areas) throws UnsupportedEncodingException {
    //    //province = URLDecoder.decode(province, "UTF-8");
    //    //city =URLDecoder.decode(city,"UTF-8");
    //    //areas =URLDecoder.decode(areas,"UTF-8");
    //    province = new String(province.getBytes("iso8859-1"),"utf-8");
    //    city = new String(city.getBytes("iso8859-1"),"utf-8");
    //    areas = new String(areas.getBytes("iso8859-1"),"utf-8");
    //    Map<String,Object> map = userService.findAddressId(province,city,areas);
    //
    //    return map;
    //}


}
