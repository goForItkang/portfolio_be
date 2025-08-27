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

    //좋아요 가져옴
    public Long countById(Long id) {
        return entityManager.createQuery(

                        """
                select count(pl) from ProjectLike pl where pl.project.id  =: id 
        """,Long.class
                )
                .setParameter("id",id)
                .getSingleResult();
    }

    public boolean existLike(Long projectId, Long userId) {
        Long count = entityManager.createQuery(
                """
        select count(pl) from ProjectLike pl where pl.project.id  =: projectId
        and pl.user.id  =: userId
""",Long.class
        ).setParameter("projectId",projectId)
                .setParameter("userId",userId)
                .getSingleResult();
         return count > 0;
    }
}
