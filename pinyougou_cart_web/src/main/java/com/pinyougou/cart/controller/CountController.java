package com.pinyougou.cart.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.http.client.OkHttpClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.cart.controller
 * @date 2019-4-2
 */
@Controller
public class CountController {
    private static Map<Integer, Integer> ok = new HashMap<>();

    static {
        ok.put(1, 0);
        ok.put(2, 0);
        ok.put(3, 0);
        ok.put(4, 0);
        ok.put(5, 0);
    }

    @RequestMapping("count")
    public synchronized String count(Integer index){
        ok.put(index,ok.get(index) + 1);
        return "redirect:get.do";
    }

    @RequestMapping("get")
    public void get(HttpServletResponse response){
        String str = "完成情况：\n";
        for (Integer i : ok.keySet()) {
            switch (i) {
                case 1:
                    str += "5天以下:" + ok.get(i) + " 人\n";
                    break;
                case 2:
                    str += "9天以下:" + ok.get(i) + " 人\n";
                    break;
                case 3:
                    str += "12天以下:" + ok.get(i) + " 人\n";
                    break;
                case 4:
                    str += "15天以下:" + ok.get(i) + " 人\n";
                    break;
                case 5:
                    str += "17天以上:" + ok.get(i) + " 人\n";
                    break;
            }
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");
        try {
            response.getWriter().print(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
