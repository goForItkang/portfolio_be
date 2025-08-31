package com.pj.portfoliosite.portfoliosite.portfolio.like;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class PortFolioLikeRepository {
    @PersistenceContext
    private EntityManager entityManager;
    public void delectById(Long userId, Long portfolioId) {
        entityManager.createQuery(
                        "DELETE FROM PortFolioLike l " +
                                "WHERE l.user.id = :userId " +
                                "AND l.portfolio.id = :portfolioId"
                )
                .setParameter("userId", userId)
                .setParameter("portfolioId", portfolioId)
                .executeUpdate();
    }

}
