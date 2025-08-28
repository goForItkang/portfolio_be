package com.pj.portfoliosite.portfoliosite.portfolio;

import com.pj.portfoliosite.portfoliosite.global.entity.PortFolio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class PortFolioRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void insert(PortFolio portfolio) {
        entityManager.persist(portfolio);
    }
}
