package com.pinyougou.shop.controller;

import com.pinyougou.utils.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Steven
 * @version 1.0
 * @description com.pinyougou.shop.controller
 * @date 2019-3-15
 */
@RestController
public class UploadController {

    @Value("${FAST_DFS_SERVER_URL}")
    private String FAST_DFS_SERVER_URL;

    @RequestMapping("upload")
    public Result upload(MultipartFile file){

        try {
            //1、创建FastDFS工具类
            FastDFSClient dfsClient = new FastDFSClient("classpath:fdfs_client.conf");
            //2、解析文件后缀名
            String oldName = file.getOriginalFilename();  //原来的文件名
            String extName = oldName.substring(oldName.lastIndexOf(".") + 1);  //文件后缀名
            //3、把用户上传的文件流，上传到FastDfs中
            String uploadUrl = dfsClient.uploadFile(file.getBytes(), extName, null);
            //4、拼接图片访问url
            String url = FAST_DFS_SERVER_URL + uploadUrl;
            //5、返回图片url
            return new Result(true, url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "文件上传失败！");
    }
}
