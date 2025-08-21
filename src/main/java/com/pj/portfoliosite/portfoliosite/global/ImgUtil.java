package com.pj.portfoliosite.portfoliosite.global;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ImgUtil {
    // 이미지 업로드 관련 URL 출력
    @Value("${img.upload-dir}")
    private String uploadDir; // 디렉토리 URL

    public String imgUpload(MultipartFile file) throws IOException {
        //만약 file 이 비어있으면 처리 처리 안함
        if(!file.isEmpty()) {
            String fileName = file.getOriginalFilename(); // 파일에 실제 이름
            String exit = fileName.substring(fileName.lastIndexOf(".")); // 확장자 출
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
            String reName = timestamp +"_"+exit;

            File saveDir = new File(uploadDir,reName); // 파일 위치와 저장
            file.transferTo(saveDir);

            return reName;
        }else{
            return null; // 실패 하거나 file이 없다.
        }
    }
}
