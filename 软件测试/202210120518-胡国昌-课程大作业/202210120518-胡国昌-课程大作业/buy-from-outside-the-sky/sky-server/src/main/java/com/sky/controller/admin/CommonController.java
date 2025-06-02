package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.temporal.Temporal;
import java.util.UUID;

@Slf4j
@Api(tags = "文件上传控制层")
@RestController
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 上传图片
     *
     * @param file
     * @return Result 返回图片路径
     * @throws IOException
     */
    @PostMapping("/admin/common/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传");
        //        通过获取上传文件名获取文件路径
        String filename = file.getOriginalFilename();
        int index = filename.lastIndexOf(".");
        String fileSuffix = filename.substring(index);
        //        通过uuid获取一个不可能重复的对象
        String UploadFile = UUID.randomUUID().toString();
        //        通过UploadFile和fileSuffix拼接形成一个不可能重复的上传文件名
        String UploadFileName = "sky-take/" + UploadFile + fileSuffix;
        byte[] bytes = new byte[0];
        try {
            bytes = file.getBytes();
            String url = aliOssUtil.upload(bytes, UploadFileName);
            return Result.success(url);
        } catch (Exception e) {
           e.printStackTrace();
        }

        return Result.success(MessageConstant.UPLOAD_FAILED);
    }
}
