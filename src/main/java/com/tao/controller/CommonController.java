package com.tao.controller;

import com.tao.common.R;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.create.table.Index;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.basepath}")
    private String basePath;

    //文件上传
    @PostMapping("/upload")
    public R<String> upload(@RequestBody MultipartFile file) {
        log.info(file.toString());
        //file为临时文件，需要转存

        //动态获取原始文件名后缀 例如.jpg  .png
        String substring = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

        //生成UUID放置文件覆盖
        String filename = UUID.randomUUID().toString() + substring;

        //判断配合的目录是否存在，如不存在则创建
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }

        try {
            file.transferTo(new File(basePath + filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return R.success(filename);
    }

    //文件下载
    @GetMapping("/download")
    public void download(String name,HttpServletResponse response){
        try {
            //通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(basePath + name);

            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");  //设置响应为图片

            int len = 0;
            byte[] bytes = new byte[2048];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
