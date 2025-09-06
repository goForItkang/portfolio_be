package com.pj.portfoliosite.portfoliosite.project.bookmark;

import com.pj.portfoliosite.portfoliosite.global.entity.Project;
import com.pj.portfoliosite.portfoliosite.global.entity.ProjectBookMark;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.project.ProjectRepository;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectBookMarkService {
    private final ProjectBookMarkRepository projectBookMarkRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    // 프로젝트 북 마크를 한 경우
    @Transactional
    public void bookMarkProject(Long id) {
        try{
            String testLogin = "portfolio@naver.com";
            Optional<User> user = userRepository.findByEmail(testLogin);
            if(user.isPresent()) {
                Project project = projectRepository.findById(id);
                ProjectBookMark projectBookMark = new ProjectBookMark();
                projectBookMark.setProject(project);
                projectBookMark.addUser(user.get());
                projectBookMarkRepository.insertBookMark(projectBookMark);
                project.addBookMark(projectBookMark);
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
            Project project = projectRepository.findById(id);
            if(user.isPresent()) {

                projectBookMarkRepository.deleteBookMark(user.get().getId()
                        ,project.getId());
                // Exception 터트림
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
