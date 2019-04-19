package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.utils.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.pay.service.impl
 * @date 2019-4-3
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {
    @Value("${appid}")
    private String appid;  //公众号
    @Value("${partner}")
    private String partner;  //商户号
    @Value("${partnerkey}")
    private String partnerkey;  //支付密匙
    @Value("${notifyurl}")
    private String notifyurl;  //回调地址


    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        Map map = new HashMap();

        try {
            //1、发起http请求，调用微信统一下单接口
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            //设置请求参数
            Map param = new HashMap();
            param.put("appid", appid);  //公众号
            param.put("mch_id", partner);  //商户号
            String nonceStr = WXPayUtil.generateNonceStr();//通过sdk生成随机字符串
            param.put("nonce_str", nonceStr);  //随机串
            param.put("body", "品优购");  //商品描述，此信息用户扫码后显示
            param.put("out_trade_no", out_trade_no);  //商户订单号
            param.put("total_fee", total_fee);  //支付金额，单位为分
            param.put("spbill_create_ip", "127.0.0.1");  //终端IP
            param.put("notify_url", notifyurl);  //回调地址
            param.put("trade_type", "NATIVE");  //交易类型
            //签名可以通过sdk直接得到,把map转成了xml
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("调起统一下单接口，入参为：" + xmlParam);
            //设置参数
            httpClient.setXmlParam(xmlParam);
            httpClient.post();  //发起请求
            //获取结果
            String content = httpClient.getContent();
            //2、解析并包装结果
            Map<String, String> responseMap = WXPayUtil.xmlToMap(content);
            System.out.println("调起统一下单接口，响应结果为：" + responseMap);
            //返回订单号
            map.put("out_trade_no", out_trade_no);
            map.put("total_fee", total_fee);  //金额(分)
            //返回二维码信息
            map.put("code_url", responseMap.get("code_url"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        try {
            //1、发起http请求，调用微信统一下单接口
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            //设置请求参数
            Map param = new HashMap();
            param.put("appid", appid);  //公众号
            param.put("mch_id", partner);  //商户号
            param.put("out_trade_no", out_trade_no);  //商户订单号
            String nonceStr = WXPayUtil.generateNonceStr();//通过sdk生成随机字符串
            param.put("nonce_str", nonceStr);  //随机串
            //签名可以通过sdk直接得到,把map转成了xml
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("调起查询订单接口，入参为：" + xmlParam);
            //设置参数
            httpClient.setXmlParam(xmlParam);
            httpClient.post();  //发起请求
            //获取结果
            String content = httpClient.getContent();
            //2、解析并包装结果
            Map<String, String> responseMap = WXPayUtil.xmlToMap(content);
            System.out.println("调起查询订单接口，响应结果为：" + responseMap);
            //返回订单号
            return responseMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map closePay(String out_trade_no) {
        try {
            //1、发起http请求，调用微信统一下单接口
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            httpClient.setHttps(true);
            //设置请求参数
            Map param = new HashMap();
            param.put("appid", appid);  //公众号
            param.put("mch_id", partner);  //商户号
            param.put("out_trade_no", out_trade_no);  //商户订单号
            String nonceStr = WXPayUtil.generateNonceStr();//通过sdk生成随机字符串
            param.put("nonce_str", nonceStr);  //随机串
            //签名可以通过sdk直接得到,把map转成了xml
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("调起查询订单接口，入参为：" + xmlParam);
            //设置参数
            httpClient.setXmlParam(xmlParam);
            httpClient.post();  //发起请求
            //获取结果
            String content = httpClient.getContent();
            //2、解析并包装结果
            Map<String, String> responseMap = WXPayUtil.xmlToMap(content);
            System.out.println("调起查询订单接口，响应结果为：" + responseMap);
            //返回订单号
            return responseMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
