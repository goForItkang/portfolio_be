package com.pj.portfoliosite.portfoliosite.project.bookmark;

import com.pj.portfoliosite.portfoliosite.global.entity.Project;
import com.pj.portfoliosite.portfoliosite.global.entity.ProjectBookMark;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.project.ProjectRepository;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.util.AESUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectBookMarkService {
    private final ProjectBookMarkRepository projectBookMarkRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final AESUtil aesUtil;
    // 프로젝트 북 마크를 한 경우

    public void bookMarkProject(Long id) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> user = userRepository.findByEmail(aesUtil.encode(email));
            if(user.isPresent()) {
                Project project = projectRepository.findById(id);
                ProjectBookMark projectBookMark = new ProjectBookMark();
                projectBookMark.setProject(project);
                projectBookMark.addUser(user.get());
                projectBookMarkRepository.insertBookMark(projectBookMark);
                project.addBookMark(projectBookMark);
            }

    }
    //ㅍ로젝트 북 마크 취송한 경우
    public void bookMarkDeleteProject(Long id) {
        try{
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> user = userRepository.findByEmail(aesUtil.encode(email));
            Project project = projectRepository.findById(id);
            if(user.isPresent()) {

                projectBookMarkRepository.deleteBookMark(user.get().getId()
                        ,project.getId());
                // Exception 터트림
            }
        }catch (Exception e){
        }
    }
}
