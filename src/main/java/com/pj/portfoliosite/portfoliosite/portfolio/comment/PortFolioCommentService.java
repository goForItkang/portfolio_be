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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PortFolioCommentService {
    private final PortFolioCommentRepository portFolioCommentRepository;
    private final UserRepository userRepository;
    private final PortFolioRepository portFolioRepository;

    @Transactional
    public void saveComment(Long portfolioId, ReqCommentDTO reqCommentDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(email == null){
            throw new RuntimeException("로그인이 필요합니다. ");
        }
        Optional<User> userOpt = userRepository.findByEmail(email);


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
        // 로그인 사용자 ID 확보(소유자 표시용)
        String testLogin  = "portfolio@naver.com";
        Long loginUserId = userRepository.findByEmail(testLogin)
                .map(User::getId)
                .orElse(null);

        List<PortfolioComment> parents = portFolioCommentRepository.findByPortfolioId(portfolioId);
        List<ResCommentListDTO> result = new ArrayList<>();
        for (PortfolioComment parent : parents) {
            result.add(toTreeDTO(parent, loginUserId));
        }
        return result;
    }

    // 엔티티 → 트리 DTO(재귀) + isOwner 세팅
    private ResCommentListDTO toTreeDTO(PortfolioComment c, Long loginUserId) {
        ResCommentListDTO dto = new ResCommentListDTO();
        dto.setId(c.getId());
        dto.setComment(c.getComment());
        dto.setParentId(c.getParent() != null ? c.getParent().getId() : null);
        if (c.getUser() != null) {
            dto.setUserId(c.getUser().getId());
            dto.setUserProfileURL(c.getUser().getProfile());
            dto.setUserWriteName(c.getUser().getName());
            dto.setOwner(loginUserId != null && c.getUser().getId().equals(loginUserId));
        } else {
            dto.setOwner(false);
        }
        List<ResCommentListDTO> replies = new ArrayList<>();
        if (c.getReplies() != null) {
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
