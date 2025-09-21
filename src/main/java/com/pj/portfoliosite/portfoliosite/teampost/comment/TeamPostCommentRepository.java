package com.pj.portfoliosite.portfoliosite.teampost.comment;

import com.pj.portfoliosite.portfoliosite.global.entity.TeamPostComment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class TeamPostCommentRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<TeamPostComment> findByTeamPostId(Long teamPostId) {
        return entityManager.createQuery(
                        "SELECT tpc FROM TeamPostComment tpc " +
                                "JOIN FETCH tpc.user u " +
                                "LEFT JOIN FETCH tpc.replies r " +
                                "WHERE tpc.teamPost.id = :teamPostId " +
                                "AND tpc.parent IS NULL " +
                                "ORDER BY tpc.createdAt DESC", TeamPostComment.class)
                .setParameter("teamPostId", teamPostId)
                .getResultList();
    }

    public TeamPostComment getReference(Long parentCommentId) {
        return entityManager.getReference(TeamPostComment.class, parentCommentId);
    }

    public void insertComment(TeamPostComment teamPostComment) {
        entityManager.persist(teamPostComment);
    }

    public TeamPostComment selectByTeamPostIdAndId(Long teamPostId, Long commentId) {
        return entityManager.createQuery(
                        """
                        select tpc from TeamPostComment tpc 
                        where tpc.id = :id and tpc.teamPost.id = :teamPostId
                        """, TeamPostComment.class)
                .setParameter("id", commentId)
                .setParameter("teamPostId", teamPostId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public void deleteComment(TeamPostComment teamPostComment) {
        entityManager.remove(teamPostComment);
    }
}