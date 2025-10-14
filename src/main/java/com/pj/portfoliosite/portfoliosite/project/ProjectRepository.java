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
                .setMaxResults(4)   // LIMIT 12 대체
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
                        "select p " +
                                "from Project p " +
                                "left join fetch p.user u " +        // 작성자 같이 로딩(N+1 방지)
                                "order by p.createdAt desc, p.id desc",
                        Project.class
                )
                .setFirstResult(page * size)
                .setMaxResults(size)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    public Long selectAllCount() {
        return entityManager.createQuery(
                "select count(p) from Project p"
        ,Long.class).getSingleResult();
    }

    public Project getReference(Long projectId) {
        return entityManager.find(Project.class, projectId);
    }

    public List<Project> findTopByLikeDescExcludeIds(List<Long> existingIds, int size) {
        String jpql = """
        select p from Project p
        left join p.likes pl
        where (:idsEmpty = true or p.id not in :ids)
        group by p
        order by count(pl) desc
        """;

        return entityManager.createQuery(jpql, Project.class)
                .setParameter("idsEmpty", existingIds == null || existingIds.isEmpty())
                .setParameter("ids", existingIds == null || existingIds.isEmpty() ? List.of(-1L) : existingIds)
                .setMaxResults(size)
                .getResultList();
    }

    public void deleteByid(Long id) {
        entityManager.remove(entityManager.getReference(Project.class, id));
    }

    public List<Project> findByUserEmail(String endoceEamil) {
        return entityManager.createQuery(
                """
    select p from Project p where p.user.email =: encodeEamil
""",Project.class
        ).setParameter(endoceEamil,endoceEamil).getResultList();
    }
}
