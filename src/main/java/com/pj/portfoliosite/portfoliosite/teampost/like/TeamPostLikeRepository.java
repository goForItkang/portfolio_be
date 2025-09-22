package com.pj.portfoliosite.portfoliosite.teampost.like;

import com.pj.portfoliosite.portfoliosite.global.entity.TeamPostLike;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class TeamPostLikeRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void insertLike(TeamPostLike teamPostLike) {
        entityManager.persist(teamPostLike);
    }

    public void deleteLike(Long userId, Long teamPostId) {
        entityManager.createQuery(
                        "DELETE FROM TeamPostLike tpl WHERE tpl.user.id = :userId AND tpl.teamPost.id = :teamPostId")
                .setParameter("userId", userId)
                .setParameter("teamPostId", teamPostId)
                .executeUpdate();
    }

    public boolean existLike(Long teamPostId, Long userId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(tpl) FROM TeamPostLike tpl WHERE tpl.teamPost.id = :teamPostId AND tpl.user.id = :userId",
                        Long.class)
                .setParameter("teamPostId", teamPostId)
                .setParameter("userId", userId)
                .getSingleResult();
        return count > 0;
    }

    public Long countByTeamPostId(Long teamPostId) {
        return entityManager.createQuery(
                        "SELECT COUNT(tpl) FROM TeamPostLike tpl WHERE tpl.teamPost.id = :teamPostId",
                        Long.class)
                .setParameter("teamPostId", teamPostId)
                .getSingleResult();
    }
}