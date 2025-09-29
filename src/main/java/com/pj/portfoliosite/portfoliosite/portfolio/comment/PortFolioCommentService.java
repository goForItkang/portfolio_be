package com.pj.portfoliosite.portfoliosite.portfolio.comment;

import com.pj.portfoliosite.portfoliosite.global.dto.ReqCommentDTO;
// ... existing code ...
import com.pj.portfoliosite.portfoliosite.global.dto.ResCommentListDTO;
import com.pj.portfoliosite.portfoliosite.global.dto.ResCommentsDTO;
import com.pj.portfoliosite.portfoliosite.global.entity.PortFolio;
import com.pj.portfoliosite.portfoliosite.global.entity.PortfolioComment;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.portfolio.PortFolioRepository;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.util.AESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortFolioCommentService {
    private final PortFolioCommentRepository portFolioCommentRepository;
    private final UserRepository userRepository;
    private final PortFolioRepository portFolioRepository;
    private final AESUtil aesUtil;

    @Transactional
    public void saveComment(Long portfolioId, ReqCommentDTO reqCommentDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(email == null){
            throw new RuntimeException("로그인이 필요합니다. ");
        }
        log.info("portfolioSaveComment email {} ",email);
        Optional<User> userOpt = userRepository.findByEmail(aesUtil.encode(email));
        PortFolio portfolio = portFolioRepository.selectById(portfolioId);
        if (portfolio == null) {
            PortfolioComment comment = new PortfolioComment(reqCommentDTO.getComment(), userOpt.get(), portfolio);
            portFolioCommentRepository.insertComment(comment);
        }

        User user = userOpt.get();
        PortfolioComment comment = new PortfolioComment(reqCommentDTO.getComment(), user, portfolio);

        if (reqCommentDTO.getParentCommentId() != null) {
            PortfolioComment parent = portFolioCommentRepository.getReference(reqCommentDTO.getParentCommentId());
            comment.setParent(parent);
            parent.addReply(comment); // 양방향 연관관계 유지
        } else {

        }

        portFolioCommentRepository.insertComment(comment);
    }

    @Transactional
    public boolean deleteComment(Long portfolioId, Long commentId) {
        PortfolioComment comment = portFolioCommentRepository.selectByPortfolioIdAndId(portfolioId, commentId);
        if (comment != null) {
            portFolioCommentRepository.deleteComment(comment);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updateComment(Long portfolioId, Long commentId, String newComment) {
        PortfolioComment comment = portFolioCommentRepository.selectByPortfolioIdAndId(portfolioId, commentId);
        if (comment != null) {
            comment.updateComment(newComment);
            return true;
        }
        return false;
    }


    public List<ResCommentListDTO> getComment(Long portfolioId) {
        // 1. 현재 인증 정보를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long loginUserId = null; // 로그인하지 않은 사용자를 위해 null로 초기화

        // 2. 인증된 사용자인지 확인하고, 이메일로 ID를 찾아옵니다.
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            String email = authentication.getName();
            // findByEmail은 Optional<User>를 반환하므로, map을 이용해 ID만 추출합니다.
            loginUserId = userRepository.findByEmail(aesUtil.encode(email))
                    .map(User::getId)
                    .orElse(null); // 사용자를 찾지 못한 경우 null
        }

        // 3. 포트폴리오에 해당하는 최상위 댓글(부모가 없는 댓글) 목록을 가져옵니다.
        List<PortfolioComment> parents = portFolioCommentRepository.findByPortfolioIdAndParentIsNull(portfolioId);

        // 4. 각 최상위 댓글을 시작으로 트리 구조의 DTO로 변환합니다.
        List<ResCommentListDTO> result = new ArrayList<>();
        for (PortfolioComment parent : parents) {
            result.add(toTreeDTO(parent, loginUserId));
        }
        return result;
    }

    // 엔티티 → 트리 DTO(재귀) + isOwner 세팅
    private ResCommentListDTO toTreeDTO(PortfolioComment c, Long loginUserId) {
        if (c == null) return null; // 방어 코드

        ResCommentListDTO dto = new ResCommentListDTO();
        dto.setId(c.getId());
        dto.setComment(c.getComment());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setParentId(c.getParent() != null ? c.getParent().getId() : null);

        // 댓글 작성자 정보 설정
        if (c.getUser() != null) {
            User commentUser = c.getUser();
            dto.setUserId(commentUser.getId());
            // 개인정보는 필요 시 복호화하여 설정합니다.
            dto.setUserProfileURL(aesUtil.decode(commentUser.getProfile()));
            dto.setUserWriteName(aesUtil.decode(commentUser.getNickname()));

            // 현재 로그인한 사용자와 댓글 작성자가 동일한지 확인하여 isOwner 설정
            dto.setOwner(loginUserId != null && commentUser.getId().equals(loginUserId));
        } else {
            // 작성자 정보가 없는 비정상적인 경우
            dto.setOwner(false);
        }

        // 대댓글(자식) 목록을 재귀적으로 처리
        List<ResCommentListDTO> replies = new ArrayList<>();
        if (c.getReplies() != null && !c.getReplies().isEmpty()) {
            for (PortfolioComment child : c.getReplies()) {
                replies.add(toTreeDTO(child, loginUserId));
            }
        }
        dto.setReplies(replies);

        return dto;
    }



    private ResCommentsDTO toResCommentsDTO(PortfolioComment c, Long parentId, Long loginUserId) {
        ResCommentsDTO dto = new ResCommentsDTO();
        dto.setId(c.getId());
        dto.setComment(c.getComment());
        dto.setParentCommentId(parentId); // 부모가 없으면 null

        // 작성자 정보
        if (c.getUser() != null) {
            dto.setWriteName(c.getUser().getName());
            dto.setWriteId(String.valueOf(c.getUser().getId()));
            dto.setProfileUrl(c.getUser().getProfile());
            dto.setCheckMe(loginUserId != null && c.getUser().getId().equals(loginUserId));
        } else {
            dto.setCheckMe(false);
        }
        return dto;
    }


}
