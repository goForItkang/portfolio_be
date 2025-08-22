package com.pj.portfoliosite.portfoliosite.project.comment;

import com.pj.portfoliosite.portfoliosite.global.entity.ProjectComment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ProjectCommentRepository {
    @PersistenceContext
    private EntityManager entityManager;
    // 프로젝트 Id로 comment 가져옴
    public List<ProjectComment> findByProjectId(Long id) {
        return null;
    }

    // 성능 튜닝

    // 레퍼런스를 가져옴
    public ProjectComment getReference(Long parentCommentId) {
      return entityManager.getReference(ProjectComment.class, parentCommentId);
    }

    //프로젝트 댓글을 넣음
    public void insertComment(ProjectComment projectComment) {
        entityManager.persist(projectComment);
    }
}
