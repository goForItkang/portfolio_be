package com.pj.portfoliosite.portfoliosite.teampost;

import com.pj.portfoliosite.portfoliosite.global.entity.TeamPost;
import com.pj.portfoliosite.portfoliosite.global.exception.CustomException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class TeamPostRepository {
    @PersistenceContext
    private EntityManager em;

    public void teamPostWrite(TeamPost teamPost) {
        try {
            em.persist(teamPost);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
