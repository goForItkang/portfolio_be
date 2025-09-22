package com.pj.portfoliosite.portfoliosite.teampost.bookmark;

import com.pj.portfoliosite.portfoliosite.global.entity.TeamPostBookMark;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class TeamPostBookMarkRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void insertBookMark(TeamPostBookMark teamPostBookMark) {
        entityManager.persist(teamPostBookMark);
    }

    public void deleteBookMark(Long userId, Long teamPostId) {
        entityManager.createQuery(
                        "DELETE FROM TeamPostBookMark tpb WHERE tpb.user.id = :userId AND tpb.teamPost.id = :teamPostId")
                .setParameter("userId", userId)
                .setParameter("teamPostId", teamPostId)
                .executeUpdate();
    }

    public boolean existBookMark(Long teamPostId, Long userId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(tpb) FROM TeamPostBookMark tpb WHERE tpb.teamPost.id = :teamPostId AND tpb.user.id = :userId",
                        Long.class)
                .setParameter("teamPostId", teamPostId)
                .setParameter("userId", userId)
                .getSingleResult();
        return count > 0;
    }

    public Long countByTeamPostId(Long teamPostId) {
        return entityManager.createQuery(
                        "SELECT COUNT(tpb) FROM TeamPostBookMark tpb WHERE tpb.teamPost.id = :teamPostId",
                        Long.class)
                .setParameter("teamPostId", teamPostId)
                .getSingleResult();
    }
}