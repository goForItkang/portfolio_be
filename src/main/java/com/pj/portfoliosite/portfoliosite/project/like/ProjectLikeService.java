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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
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
            Optional<User> user = userRepository.findByEmail(aesUtil.encode(email));
            log.info(user.get().getId().toString());
            log.info(id.toString());
            Project project = projectRepository.findById(id);
            if(user.isPresent()) {
                ProjectLike projectLike = new ProjectLike();
                projectLike.addProject(project);
                projectLike.addUser(user.get());
                project.addLike(projectLike);
                projectLikeRepository.insertLike(projectLike);
                // Exception 터트림
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void likeDeleteProject(Long id) {
            // Try catch 로 분기 처리
            try{
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                Optional<User> user = userRepository.findByEmail(aesUtil.encode(email));
                if(user.isPresent()) {
                    projectLikeRepository.deleteLike(user.get().getId(),id);
                    // Exception 터트림
                }
            }catch (Exception e){
            }
        }

}
