package com.pj.portfoliosite.portfoliosite.skill;

import com.pj.portfoliosite.portfoliosite.global.entity.Skill;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SkillRepository {
   @PersistenceContext
   private EntityManager entityManager;
    public List<Skill> selectAllSkill() {
    return entityManager.createQuery("SELECT s FROM Skill s", Skill.class).getResultList();
    }
}
