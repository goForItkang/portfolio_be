package com.pj.portfoliosite.portfoliosite.project;

import com.pj.portfoliosite.portfoliosite.global.entity.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
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

}
