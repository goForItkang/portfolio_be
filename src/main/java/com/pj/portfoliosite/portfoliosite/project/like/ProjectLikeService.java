package com.pj.portfoliosite.portfoliosite.project.like;

import com.pj.portfoliosite.portfoliosite.global.entity.ProjectLike;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectLikeService {
    private final ProjectLikeRepository projectLikeRepository;
    private final UserRepository userRepository;
    // 프로젝트 좋아요
    public void likeProject(Long id) {
        //

        // Try catch 로 분기 처리
        try{
            String testLogin = "portfolio@naver.com";
            Optional<User> user = userRepository.findByEmail(testLogin);
            if(user.isPresent()) {
                ProjectLike projectLike = new ProjectLike();
                projectLike.addUser(user.get());
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
                String testLogin = "portfolio@naver.com";
                Optional<User> user = userRepository.findByEmail(testLogin);
                if(user.isPresent()) {
                    projectLikeRepository.deleteLike(user.get());
                    // Exception 터트림
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

}
