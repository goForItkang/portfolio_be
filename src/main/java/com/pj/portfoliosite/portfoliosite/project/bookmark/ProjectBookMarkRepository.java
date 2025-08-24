package com.pj.portfoliosite.portfoliosite.project.bookmark;

import com.pj.portfoliosite.portfoliosite.global.entity.ProjectBookMark;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ProjectBookMarkRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void insertBookMark(ProjectBookMark projectBookMark) {
        entityManager.persist(projectBookMark);
    }

    public void deleteBookMark(User user) {
    }
}
