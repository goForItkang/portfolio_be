package com.pj.portfoliosite.portfoliosite.teampost.comment;

import com.pj.portfoliosite.portfoliosite.global.entity.TeamPost;
import com.pj.portfoliosite.portfoliosite.global.entity.TeamPostComment;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.teampost.TeamPostRepository;
import com.pj.portfoliosite.portfoliosite.teampost.dto.ReqTeamCommentDTO;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamPostCommentService {
    private final TeamPostCommentRepository teamPostCommentRepository;
    private final UserRepository userRepository;
    private final TeamPostRepository teamPostRepository;

    @Transactional
    public void addComment(Long teamPostId, ReqTeamCommentDTO reqTeamCommentDTO) {
        TeamPost teamPost = teamPostRepository.getReference(teamPostId);
        String testLoginId = "portfolio@naver.com";
        Optional<User> user = userRepository.findByEmail(testLoginId);

        TeamPostComment teamPostComment = new TeamPostComment(
                reqTeamCommentDTO.getComment(), user.get(), teamPost);

        if (reqTeamCommentDTO.getParentCommentId() != null) {
            TeamPostComment parentComment = teamPostCommentRepository.getReference(
                    reqTeamCommentDTO.getParentCommentId());
            teamPostComment.setParent(parentComment);
            parentComment.addReply(teamPostComment);
        }

        teamPostCommentRepository.insertComment(teamPostComment);
    }

    @Transactional
    public boolean deleteComment(Long teamPostId, Long commentId) {
        TeamPostComment teamPostComment = teamPostCommentRepository.selectByTeamPostIdAndId(
                teamPostId, commentId);
        if (teamPostComment != null) {
            teamPostCommentRepository.deleteComment(teamPostComment);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updateComment(Long teamPostId, Long commentId, String comment) {
        TeamPostComment teamPostComment = teamPostCommentRepository.selectByTeamPostIdAndId(
                teamPostId, commentId);
        if (teamPostComment != null) {
            teamPostComment.updateComment(comment);
            return true;
        }
        return false;
    }
}