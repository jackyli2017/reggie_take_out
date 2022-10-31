package com.jacky.reggie.controller;

import com.jacky.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    //参数的名字不能随便定，必须要和网页传过来的form表单里的name属性定义的名字相同
    public R<String> upload(MultipartFile file) {
        //这里的file是个临时文件。需要转存到指定位置，否则本次请求完成后临时文件会被删除
        log.info(file.toString());
        String orignalFilename = file.getOriginalFilename();
        String suffix = orignalFilename.substring(orignalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + suffix;

        File dir = new File(basePath);
        if(!dir.exists()) {
            dir.mkdirs();
        }

        try {
            file.transferTo(new File(basePath + filename));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.success(filename);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int length = 0;
            byte[] bytes = new byte[1024];
            while((length=fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, length);
                outputStream.flush();
            };

            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
