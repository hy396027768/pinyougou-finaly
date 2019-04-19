package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.utils.IdWorker;
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
    private OrderService orderService;

    @RequestMapping("createNative")
    public Map createNative(){
        //生成订单号,这是没有整合日志功能前的代码
        /*IdWorker worker = new IdWorker();
        String out_trade_no = worker.nextId() + "";
        //测试支付一分钱
        return weixinPayService.createNative(out_trade_no, "1");*/

        //整合日志功能后的代码
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = orderService.searchPayLogFromRedis(userId);
        return weixinPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee() + "");
    }

    @RequestMapping("queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result = null;
        //查询的次数
        int count = 1;

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
                orderService.updateOrderStatus(out_trade_no,map.get("transaction_id").toString());
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
            if(count > 100){
                result =  new Result(false, "支付已超时");
                break;
            }
        }
        return result;
    }
}
