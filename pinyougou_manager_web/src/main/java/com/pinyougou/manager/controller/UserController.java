package com.pinyougou.manager.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.user.service.UserService;
import com.pinyougou.utils.POIExcelImportUtil;
import entity.PageResult;
import entity.Result;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {


    @Reference(timeout = 5000)
    private UserService userService;
    @Reference(timeout = 5000)
    private OrderService orderService;
    @Autowired
    private POIExcelImportUtil poiUtil;
    @Reference(timeout = 5000)
    private GoodsService goodsService;

    /**
     * 查询所有
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbUser> findAll() {
        return userService.findAll();
    }
    @RequestMapping("aaa")
    public String aaa(){
        return "aaa";
    }
    /**
     * 分页查询所有
     *
     * @param tbUser
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbUser tbUser, int page, int rows) {

        if (tbUser != null) {
            return userService.findPage(tbUser, page, rows);
        } else {
            tbUser = new TbUser();
            return userService.findPage(tbUser, page, rows);
        }
    }

    /**
     * 修改用户的状态
     *
     * @param id
     * @param status
     * @return
     */
    @RequestMapping("/setStatus")
    public Result setStatus(Long id, String status) {

        TbUser tbUser = new TbUser();
        tbUser.setId(id);
        tbUser.setStatus(status);

        try {
            userService.update(tbUser);
            return new Result(true, "用户状态修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "用户状态修改失败");
        }
    }

    /**
     * 根据ID获取用户信息
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbUser findOne(Long id) {
        return userService.findOne(id);
    }

    /**
     * 性别统计
     *
     * @return
     */
    @RequestMapping("/sexStatistics")
    public Map<String, Long> sexStatistics() {

        return userService.sexStatistics();
    }

    /**
     * 查询基础数据统计
     *
     * @return
     */
    @RequestMapping("/basic")
    public Map<String, List> basic() {
        return userService.basic();
    }


    /**
     * 数据上传
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/upload1",produces = "application/json; charset=utf-8")
    public String upload(@RequestParam("file") MultipartFile file) {

        try {
            // 通过文件流，创建工作簿
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
            Iterator<Row> iterator = sheet.iterator(); // 获取行迭代器
            List<TbBrand> emps = new ArrayList<TbBrand>();
            while (iterator.hasNext()) {
                Row row = iterator.next();  // 获取一行
                int rowNum = row.getRowNum(); // 行号
                // 跳过表头
                if (rowNum == 0) {
                    continue;
                }
                String ename = row.getCell(1).getStringCellValue();
                String firstChar = row.getCell(2).getStringCellValue();
                TbBrand emp = new TbBrand(ename, firstChar);
                emps.add(emp);
            }
            workbook.close();
            for (TbBrand emp : emps) {
                userService.setBrand(emp);
            }
            return "上传成功!!!";

        } catch (IOException e) {
            e.printStackTrace();
            return "上传失败!!!";
        }
    }

    @RequestMapping("/upload2")
    public String upload2(@RequestParam("file") MultipartFile file) {


        try {
            // 通过文件流，创建工作簿
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
            Iterator<Row> iterator = sheet.iterator(); // 获取行迭代器
            List<TbBrand> emps = new ArrayList<TbBrand>();
            while (iterator.hasNext()) {
                Row row = iterator.next();  // 获取一行
                int rowNum = row.getRowNum(); // 行号
                // 跳过表头
                if (rowNum == 0) {
                    continue;
                }

                String ename = row.getCell(1).getStringCellValue();
                String firstChar = row.getCell(2).getStringCellValue();
                TbBrand emp = new TbBrand(ename, firstChar);
                emps.add(emp);
            }

            workbook.close();

            for (TbBrand emp : emps) {
                userService.setBrand(emp);

            }
            return "上传成功";

        } catch (IOException e) {
            e.printStackTrace();
            return "上传失败";
        }
    }

    @RequestMapping("/upload3")
    public String upload3(@RequestParam("file") MultipartFile file) {

        try {
            // 通过文件流，创建工作簿
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
            Iterator<Row> iterator = sheet.iterator(); // 获取行迭代器
            List<TbBrand> emps = new ArrayList<TbBrand>();
            while (iterator.hasNext()) {
                Row row = iterator.next();  // 获取一行
                int rowNum = row.getRowNum(); // 行号
                // 跳过表头
                if (rowNum == 0) {
                    continue;
                }

                String ename = row.getCell(1).getStringCellValue();
                String firstChar = row.getCell(2).getStringCellValue();
                TbBrand emp = new TbBrand(ename, firstChar);
                emps.add(emp);
            }

            workbook.close();

            for (TbBrand emp : emps) {
                userService.setBrand(emp);

            }
            return "上传成功";

        } catch (IOException e) {
            e.printStackTrace();
            return "上传失败";
        }
    }


    /**
     * 用户表下载
     *
     * @param response
     * @return
     */
    @RequestMapping("/download")
    public void download(HttpServletResponse response, HttpServletRequest request) {

        String fileId = request.getParameter("fileId");
        if (fileId != null && !fileId.equals("")) {
            if (fileId.equals("1")) {
                List<TbOrder> orders = orderService.findAll();
                String name = "订单表";
                //创建文档表头
                boolean b = poiUtil.setHeader(name, TbOrder.class, orders, response);
            } else if (fileId.equals("2")) {

                List<TbUser> users = userService.findAll();
                String name = "用户表";

                //创建文档表头
                boolean b = poiUtil.setHeader(name, TbUser.class, users, response);

            } else if (fileId.equals("3")) {

                List<TbGoods> goods = goodsService.findAll();
                String name = "商品表";

                //创建文档表头
                boolean b = poiUtil.setHeader(name, TbGoods.class, goods, response);
            }
        }
    }


}
