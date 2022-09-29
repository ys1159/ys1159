package com.reggie.controller;


import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${img.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //文件上传
        log.info(file.toString());
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));


        String Filename = UUID.randomUUID().toString()+suffix;

        File dir=new File(basePath);
        if (!dir.exists()){
            //不存在，创建
            dir.mkdirs();
        }
        //文件上传
        try {
            //将临时文件转存
            file.transferTo(new File(basePath+Filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(Filename);

    }

    /**
     * 文件下载
     *
     * @param name
     * @param response
     */

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        FileInputStream fileInputStream=null;
        ServletOutputStream outputStream=null;
        try {
            //输入流
             fileInputStream = new FileInputStream(new File(basePath + name));
            //输出流
             outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            int len=0;
            byte[] bytes=new byte[1024];
            while ((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (fileInputStream!=null){
                    fileInputStream.close();
                }
                if (outputStream!=null){
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
