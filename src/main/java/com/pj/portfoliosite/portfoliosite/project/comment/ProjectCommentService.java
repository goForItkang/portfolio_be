package com.pj.portfoliosite.portfoliosite.project.comment;

import com.pj.portfoliosite.portfoliosite.global.dto.ReqCommentDTO;
import com.pj.portfoliosite.portfoliosite.global.entity.Project;
import com.pj.portfoliosite.portfoliosite.global.entity.ProjectComment;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.project.ProjectRepository;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectCommentService {
    private final ProjectCommentRepository projectCommentRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    //프로젝트 댓글 등록
    public void addComment(Long projectId, ReqCommentDTO reqCommentDTO) {
        Project project = projectRepository.getReference(projectId);

        String testLoginId = "portfolio@naver.com";
        Optional<User> user = userRepository.findByEmail(testLoginId); // 로그인 한 유저
        // Comment 객체  생성
        ProjectComment projectComment = new ProjectComment(reqCommentDTO.getComment(),user.get(),project);

        if(reqCommentDTO.getParentCommentId()!=null) {
            System.out.println("여까지는 동작함 ?");
            //  대댓글일 경우
            ProjectComment parentComment = projectCommentRepository.getReference(reqCommentDTO.getParentCommentId());
            // 자식 → 부모 연결
            projectComment.setParent(parentComment);

            // 부모 → 자식도 연결 (양방향 연관관계 유지)
            parentComment.addReply(projectComment);
        }else{
            // 대댓글이 아닐 경우
            projectComment.addReply(null);
        }
        // 로직 성공하고 repository로 보냄

        projectCommentRepository.insertComment(projectComment);

    }
}
