package com.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.content.service.ItemCatService;
import com.pinyougou.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.portal.controller
 * @date 2019-3-20
 */
@RestController
@RequestMapping("content")
public class ContentController {

    @Reference
    private ContentService contentService;
    @Reference
    private ItemCatService itemCatService;

    @RequestMapping("findByCategoryId")
    public List<TbContent> findByCategoryId(Long categoryId){
        return contentService.findByCategoryId(categoryId);
    }
    @RequestMapping("initCategoryList")
    public List initCategoryList(){
        return itemCatService.findItemCatList();
    }
}
