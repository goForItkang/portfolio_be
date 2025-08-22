package com.pj.portfoliosite.portfoliosite.project;

import com.pj.portfoliosite.portfoliosite.global.entity.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Transactional
@Repository
public class ProjectRepository {
    @PersistenceContext
    private EntityManager entityManager;


    public List<Project> findTopProjectsByLikesInPeriod(LocalDate today,LocalDate weekAgo) {
        today = LocalDate.now();
        weekAgo = today.minusWeeks(1);

        LocalDateTime start = weekAgo.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);
        return entityManager.createQuery(
                        """
                        select p
                        from Project p
                        left join p.likes l
                        where p.createdAt between :startDate and :endDate
                        group by p
                        order by count(l) desc
                        """, Project.class
                )

                .setParameter("startDate", start)
                .setParameter("endDate", end)
                .setMaxResults(12)   // LIMIT 12 대체
                .getResultList();
    }

    public void insertProject(Project project) {
        // 프러젝트 삽입
        try {
            entityManager.persist(project);
        }catch (Exception e) {
            //Exception 처리
            e.printStackTrace();
        }
    }

    public Project findById(Long id) {
        return entityManager.find(Project.class, id);
    }


    public List<Project> selectByCreateAtDesc(int page, int size) {
        return entityManager.createQuery(
                        "select p from Project p order by p.createdAt desc",
                        Project.class
                )
                .setFirstResult(page * size)   // OFFSET (몇 번째부터 가져올지)
                .setMaxResults(size)           // LIMIT (몇 개 가져올지)
                .getResultList();
    }

    public Long selectAllCount() {
        return entityManager.createQuery(
                "select count(p) from Project p"
        ,Long.class).getSingleResult();
    }

    public Project getReference(Long projectId) {
        return entityManager.getReference(Project.class, projectId);
    }
}
