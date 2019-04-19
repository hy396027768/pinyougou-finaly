package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.CookieUtil;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.cart.controller
 * @date 2019-4-1
 */
@RestController
@RequestMapping("cart")
public class CartController {
    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    //查询当前用户的购物车列表
    /*
    未合并时的代码
    @RequestMapping("findCartList")
    public List<Cart> findCartList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Cart> cartList = null;
        //未登录
        if("anonymousUser".equals(username)) {
            System.out.println("从cookie中获取了购物车数据...");
            //从cookie中获取购物车列表
            String cartListStr = CookieUtil.getCookieValue(request, "cartList", true);
            if (cartListStr != null && cartListStr.length() > 0) {
                cartList = JSON.parseArray(cartListStr, Cart.class);
            } else {
                //为保证后续逻辑不错，这里返回list
                cartList = new ArrayList<>();
            }
        }else{  //已登录
            System.out.println("从redis中获取了购物车数据...");
            cartList = cartService.findCartListFromRedis(username);
        }
        return cartList;
    }*/

    //合并购物车后的代码逻辑
    @RequestMapping("findCartList")
    public List<Cart> findCartList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Cart> cartList = null;
        //先查询cookie的数据出来
        //从cookie中获取购物车列表
        String cartListStr = CookieUtil.getCookieValue(request, "cartList", true);
        if (cartListStr != null && cartListStr.length() > 0) {
            cartList = JSON.parseArray(cartListStr, Cart.class);
        } else {
            //为保证后续逻辑不错，这里返回list
            cartList = new ArrayList<>();
        }
        //未登录
        if("anonymousUser".equals(username)) {
            System.out.println("从cookie中获取了购物车数据...");
        }else{  //已登录
            System.out.println("从redis中获取了购物车数据...");
            //查询redis数据
            List<Cart> cartRedisList = cartService.findCartListFromRedis(username);
            //如果cookie中有数据,合并购物车
            if(cartList.size() > 0){
                System.out.println("合并了cookie与redis的购物...");
                //合并购物车
                cartList = cartService.mergeCartList(cartList, cartRedisList);
                //更新redis数据
                cartService.saveCartListToRedis(username,cartList);
                //清空cookies数据
                CookieUtil.deleteCookie(request,response,"cartList");
            }else{
                cartList = cartRedisList;
            }
        }
        return cartList;
    }

    //添加购物车
    @RequestMapping("addGoodsToCartList")
    //origins：绑定跨域授权的域，allowCredentials：设置操作浏览器的cookie，此参数可以缺省，默认情况就是true
    @CrossOrigin(origins = "http://localhost:8085",allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId,Integer num){
        try {
            //设置可以访问的域，值设置为*时，允许所有域
            //response.setHeader("Access-Control-Allow-Origin", "http://localhost:8085");
            //如果需要操作cookies，必须加上此配置，标识服务端可以写cookies，
            // 并且Access-Control-Allow-Origin不能设置为*，因为cookies操作需要域名
            //response.setHeader("Access-Control-Allow-Credentials", "true");

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //查询当前的购物车列表
            List<Cart> cartList = this.findCartList();
            //计算购物车逻辑,这里是发起了一个dubbo接口调用，一定要记得重新接收参数
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            //未登录
            if("anonymousUser".equals(username)) {
                System.out.println("操作了cookis的购物车数据...");
                //保存购物列表
                String jsonString = JSON.toJSONString(cartList);
                //注意这里的有效时间，跟具体业务场景有关系，我们这里暂时设置1天有效
                CookieUtil.setCookie(request, response, "cartList", jsonString, 60 * 60 * 24, true);
            }else {
                System.out.println("操作了redis的购物车数据...");
                cartService.saveCartListToRedis(username, cartList);
            }
            return new Result(true, "操作购物车成功!");
        }catch (RuntimeException e){
            //提示用户详细的信息
            return new Result(false, e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "操作购物车失败!");
    }

}
