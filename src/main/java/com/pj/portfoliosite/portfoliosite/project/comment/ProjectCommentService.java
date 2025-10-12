package com.pj.portfoliosite.portfoliosite.project.comment;

import com.pj.portfoliosite.portfoliosite.global.dto.ReqCommentDTO;
import com.pj.portfoliosite.portfoliosite.global.dto.ResCommentListDTO;
import com.pj.portfoliosite.portfoliosite.global.entity.Project;
import com.pj.portfoliosite.portfoliosite.global.entity.ProjectComment;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.project.ProjectRepository;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.util.AESUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectCommentService {
    private final ProjectCommentRepository projectCommentRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final AESUtil aesUtil;
    //프로젝트 댓글 등록
    public void addComment(Long projectId, ReqCommentDTO reqCommentDTO) {
        Project project = projectRepository.getReference(projectId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userEmail;
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            userEmail = authentication.getName();
        } else {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        Optional<User> user = userRepository.findByEmail(userEmail);
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

    public boolean deleteComment(Long projectId, Long commentId) {
        //ProjectComment 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userEmail;
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            userEmail = authentication.getName();
        } else {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        ProjectComment projectComment = projectCommentRepository.selectByProjectIdAndId(projectId,commentId);
        if(projectComment!=null) {
            projectCommentRepository.deleteComment(projectComment);
            return true; // 정상적으로 성공 하면 true 반환
        }else{
            return false; // 실패하면 false 반환
        }

    }
    @Transactional
    public boolean updateComment(Long projectId, Long commentId, String comment) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userEmail;
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            userEmail = authentication.getName();
        } else {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        ProjectComment projectComment = projectCommentRepository.selectByProjectIdAndId(projectId,commentId);
        if(projectComment!=null) {
            projectComment.updateComment(comment);
            return true;
        }else{
            return false; // 댓귿을 찾지 못해서 실패
        }
    }

    public List<ResCommentListDTO> getComment(Long projectId) {
        // 1. 현재 인증 정보를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long loginUserId = null; // 로그인하지 않은 사용자를 위해 null로 초기화

        // 2. 인증된 사용자인지 확인하고, 이메일로 ID를 찾아옵니다.
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            String email = authentication.getName();
            loginUserId = userRepository.findByEmail(aesUtil.encode(email))
                    .map(User::getId)
                    .orElse(null);
        }

        // 3. 프로젝트에 해당하는 최상위 댓글(부모가 없는 댓글) 목록을 가져옵니다.
        List<ProjectComment> parents = projectCommentRepository.findByProjectIdAndParentIsNull(projectId);

        // 4. 각 최상위 댓글을 시작으로 트리 구조의 DTO로 변환합니다.
        List<ResCommentListDTO> result = new ArrayList<>();
        for (ProjectComment parent : parents) {
            result.add(toTreeDTO(parent, loginUserId));
        }
        return result;
    }

    // ✅ ProjectComment → 트리 DTO 변환 메서드
    private ResCommentListDTO toTreeDTO(ProjectComment c, Long loginUserId) {
        if (c == null) return null;

        ResCommentListDTO dto = new ResCommentListDTO();
        dto.setId(c.getId());
        dto.setComment(c.getComment());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setParentId(c.getParent() != null ? c.getParent().getId() : null);

        // 댓글 작성자 정보 설정
        if (c.getUser() != null) {
            User commentUser = c.getUser();
            dto.setUserId(commentUser.getId());
            dto.setUserProfileURL(aesUtil.decode(commentUser.getProfile()));
            dto.setUserWriteName(aesUtil.decode(commentUser.getNickname()));
            dto.setOwner(loginUserId != null && commentUser.getId().equals(loginUserId));
        } else {
            dto.setOwner(false);
        }

        // 대댓글(자식) 목록을 재귀적으로 처리
        List<ResCommentListDTO> replies = new ArrayList<>();
        if (c.getReplies() != null && !c.getReplies().isEmpty()) {
            for (ProjectComment child : c.getReplies()) {
                replies.add(toTreeDTO(child, loginUserId));
            }
        }
        dto.setReplies(replies);

        return dto;
    }

}
