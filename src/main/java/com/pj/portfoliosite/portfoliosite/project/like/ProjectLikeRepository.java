package com.pj.portfoliosite.portfoliosite.project.like;

import com.pj.portfoliosite.portfoliosite.global.entity.ProjectLike;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class ProjectLikeRepository {
    @PersistenceContext
    private EntityManager entityManager;
    // 사람이 좋아요 누른경우
    public void insertLike(ProjectLike projectLike) {
        entityManager.persist(projectLike);
    }

    public void deleteLike(User user) {
        entityManager.remove(entityManager.merge(user));
    }
}
