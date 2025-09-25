package com.pj.portfoliosite.portfoliosite.config;


import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileUploadConfig {
    @Bean
    public FileUpload customFileUpload() {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        FileUpload upload = new FileUpload(); // 1. 기본 생성자로 객체를 생성합니다.
        upload.setFileItemFactory(factory);   // 2. Setter를 사용해 factory를 설정합니다.
        upload.setFileCountMax(30);


        return upload;
    }
}
