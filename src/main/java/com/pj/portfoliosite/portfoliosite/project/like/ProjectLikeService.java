package com.pj.portfoliosite.portfoliosite.project.like;

import com.pj.portfoliosite.portfoliosite.global.entity.Project;
import com.pj.portfoliosite.portfoliosite.global.entity.ProjectLike;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.project.ProjectRepository;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.user.UserService;
import com.pj.portfoliosite.portfoliosite.util.AESUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectLikeService {
    private final ProjectLikeRepository projectLikeRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final AESUtil aesUtil;
    // 프로젝트 좋아요
    public void likeProject(Long id) {
        //

        // Try catch 로 분기 처리
        try{
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> user = userRepository.findByEmail(aesUtil.decode(email));
            Project project = projectRepository.findById(id);
            if(user.isPresent()) {
                ProjectLike projectLike = new ProjectLike();
                projectLike.addProject(project);
                projectLike.addUser(user.get());
                projectLikeRepository.insertLike(projectLike);
                project.addLike(projectLike);
                // Exception 터트림
            }
        }catch (Exception e){
        }
    }

    public void likeDeleteProject(Long id) {
            // Try catch 로 분기 처리
            try{
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                Optional<User> user = userRepository.findByEmail(aesUtil.decode(email));
                if(user.isPresent()) {
                    projectLikeRepository.deleteLike(user.get().getId(),id);
                    // Exception 터트림
                }
            }catch (Exception e){
            }
        }

}
