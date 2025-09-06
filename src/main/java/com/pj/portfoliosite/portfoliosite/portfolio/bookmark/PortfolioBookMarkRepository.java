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

    public boolean existBookMark(Long userId, Long portfolioId) {
        Long count = entityManager.createQuery(
                """
    select count(pm) from PortFolioBookMark pm where pm.portfolio.id  =: portfolioId
        and pm.user.id  =: userId
    """,Long.class
        ).setParameter("portfolioId",portfolioId)
                .setParameter("userId",userId)
                .getSingleResult();
        return count > 0;
    }

    public Long countByPortfolioId(Long PortfolioId) {
        return entityManager.createQuery(
                """
    select count(pb) from PortFolioBookMark pb 
    where pb.portfolio.id =:id
""",Long.class
        ).setParameter("id",PortfolioId)
                .getSingleResult();
    }
}
