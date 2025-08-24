package com.pj.portfoliosite.portfoliosite.project.bookmark;

import com.pj.portfoliosite.portfoliosite.global.entity.ProjectBookMark;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectBookMarkService {
    private final ProjectBookMarkRepository projectBookMarkRepository;
    private final UserRepository userRepository;
    // 프로젝트 북 마크를 한 경우
    public void bookMarkProject(Long id) {
        try{
            String testLogin = "portfolio@naver.com";
            Optional<User> user = userRepository.findByEmail(testLogin);
            if(user.isPresent()) {
                ProjectBookMark projectBookMark = new ProjectBookMark();
                projectBookMark.addUser(user.get());
                projectBookMarkRepository.insertBookMark(projectBookMark);
                // Exception 터트림
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //ㅍ로젝트 북 마크 취송한 경우
    public void bookMarkDeleteProject(Long id) {
        try{
            String testLogin = "portfolio@naver.com";
            Optional<User> user = userRepository.findByEmail(testLogin);
            if(user.isPresent()) {
                projectBookMarkRepository.deleteBookMark(user.get());
                // Exception 터트림
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
