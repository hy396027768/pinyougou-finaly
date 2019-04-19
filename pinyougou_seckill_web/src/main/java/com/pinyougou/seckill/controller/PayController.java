package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.cart.controller
 * @date 2019-4-3
 */
@RestController
@RequestMapping("pay")
public class PayController {
    @Reference(timeout = 7000)
    private WeixinPayService weixinPayService;
    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("createNative")
    public Map createNative(){
        //整合日志功能后的代码
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
        return weixinPayService.createNative(seckillOrder.getId() + "", (long)(seckillOrder.getMoney().doubleValue() * 100) + "");
    }

    @RequestMapping("queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result = null;
        //查询的次数
        int count = 1;
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        while (true){
            //查询订单
            Map map = weixinPayService.queryPayStatus(out_trade_no);

            if(map == null){
                result =  new Result(false, "支付失败！");
                break;
            }
            //识别订单状态是否已支付
            if(map.get("trade_state").equals("SUCCESS")){
                result = new Result(true, "支付成功！");

                //更新订单状态与日志状态
                //orderService.updateOrderStatus(out_trade_no,map.get("transaction_id").toString());
                seckillOrderService.saveOrderFromRedisToDb(userId,new Long(out_trade_no),map.get("transaction_id").toString());
                break;
            }
            //3秒发起一次请求
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
            //3秒发起一次请求，一分钟20次,约5分钟后结束查询
            if(count > 3){
                result =  new Result(false, "支付已超时");
                //还原库存
                //seckillOrderService.deleteOrderFromRedis(userId,new Long(out_trade_no));

                //1.调用微信的关闭订单接口（学员实现）
                Map<String,String> payresult = weixinPayService.closePay(out_trade_no);
                if( !"SUCCESS".equals(payresult.get("result_code")) ){//如果返回结果是正常关闭
                    if("ORDERPAID".equals(payresult.get("err_code"))){
                        result=new Result(true, "支付成功");
                        //修改订单状态
                        seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id").toString());
                    }
                }
                if(result.isSuccess()==false){
                    System.out.println("超时，取消订单");
                    //2.调用删除
                    seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));
                }


                break;
            }
        }
        return result;
    }
}
