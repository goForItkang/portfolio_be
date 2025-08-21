package com.pj.portfoliosite.portfoliosite.project;

import com.pj.portfoliosite.portfoliosite.global.entity.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Transactional
@Repository
public class ProjectRepository {
    @PersistenceContext
    private EntityManager entityManager;


    public List<Project> findTopProjectsByLikesInPeriod(LocalDate today, LocalDate weekAgo) {
        return null;
    }
}
