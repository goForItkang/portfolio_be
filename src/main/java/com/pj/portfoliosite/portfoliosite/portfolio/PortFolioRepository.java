package com.pj.portfoliosite.portfoliosite.portfolio;

import com.pj.portfoliosite.portfoliosite.global.entity.*;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.ResPortFolioDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class PortFolioRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void insert(PortFolio portfolio) {
        entityManager.persist(portfolio);
    }

    public PortFolio selectById(Long id) {
        return entityManager.find(PortFolio.class, id);
    }
    // 전체 가져오기
    public PortFolio selectWithAllById(Long id){
            return entityManager.createQuery(
                            "select distinct p " +
                                    "from PortFolio p " +
                                    "left join fetch p.awards " +
                                    "left join fetch p.careers " +
                                    "left join fetch p.educations " +
                                    "left join fetch p.certificates " +
                                    "where p.id = :id", PortFolio.class)
                    .setParameter("id", id)
                    .getSingleResult();
    }
    // award select by portfolio id
    public List<Award> awardSelectByPortfolioId(Long portfolioId) {
        return entityManager.createQuery(
                        "select a from Award a where a.portfolio.id = :pid order by a.id", Award.class)
                .setParameter("pid", portfolioId)
                .getResultList();
    }

    public List<Career> careerSelectByPortfolioId(Long portfolioId) {
        return entityManager.createQuery(
                        "select c from Career c where c.portfolio.id = :pid order by c.id", Career.class)
                .setParameter("pid", portfolioId)
                .getResultList();
    }
    public List<Certificate> certificateSelectByPortfolioId(Long id){
        return  entityManager.createQuery(
                "select c from Certificate c where c.portfolio.id = :pid order by c.id",Certificate.class
        )
                .setParameter("pid",id)
                .getResultList();
    }
    public List<Education> educationSelectByPortfolioId(Long id){
        return entityManager.createQuery(
                "select e from Education e where e.portfolio.id = :pid",Education.class
        )
                .setParameter("pid",id)
                .getResultList();
    }

}
