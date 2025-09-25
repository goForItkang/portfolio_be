package com.pj.portfoliosite.portfoliosite.config;


import jakarta.servlet.MultipartConfigElement;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
public class FileUploadConfig {
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(20));      // 파일당 최대 20MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(100));  // 전체 요청 최대 100MB
        return factory.createMultipartConfig();
    }
}
