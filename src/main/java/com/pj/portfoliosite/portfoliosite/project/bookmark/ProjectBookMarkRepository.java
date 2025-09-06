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

    public void deleteBookMark(Long userId, Long projectId) {
        String jpql = "DELETE FROM ProjectBookMark b " +
                "WHERE b.user.id = :userId AND b.project.id = :projectId";

        entityManager.createQuery(jpql)
                .setParameter("userId", userId)
                .setParameter("projectId", projectId)
                .executeUpdate();
    }
    // id 기준으로 count 가져옴
    public Long countById(Long id) {
    return    entityManager.createQuery(
                """
        select count(pb) from ProjectBookMark pb where pb.project.id = : id
""",Long.class
        ).setParameter("id", id)
                .getSingleResult();
    }

    public boolean existBookMark(Long projectId, Long userId) {
        Long count = entityManager.createQuery(
                        """
                select count(pb) from ProjectBookMark pb where pb.project.id  =: projectId
                and pb.user.id  =: userId
        """,Long.class
                ).setParameter("projectId",projectId)
                .setParameter("userId",userId)
                .getSingleResult();
        return count > 0;
    }
}
