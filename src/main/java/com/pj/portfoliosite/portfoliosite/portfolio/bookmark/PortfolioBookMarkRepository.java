package com.pj.portfoliosite.portfoliosite.portfolio.bookmark;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class PortfolioBookMarkRepository {
    @PersistenceContext
    private EntityManager entityManager;
    public void delectByPortFolioIdAndUserId(Long userId, Long portfolioId) {
        entityManager.createQuery(

                "DELETE FROM PortFolioBookMark l " +
                        "WHERE l.user.id = :userId " +
                        "AND l.portfolio.id = :portfolioId"

        ).setParameter("userId",userId)
                .setParameter("portfolioId",portfolioId)
                .executeUpdate();
    }
}
