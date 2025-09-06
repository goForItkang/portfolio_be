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
    public List<ProjectComment> findByProjectId(Long projectId) {
        return entityManager.createQuery(
                        "SELECT pc FROM ProjectComment pc " +
                                "JOIN FETCH pc.user u " +            // 작성자 즉시 로딩 (원하면)
                                "LEFT JOIN FETCH pc.replies r " +    // 대댓글 즉시 로딩 (원하면)
                                "WHERE pc.project.id = :projectId " +
                                "AND pc.parent IS NULL " +           // 부모 댓글만 가져오기 (필요한 경우)
                                "ORDER BY pc.createdAt DESC", ProjectComment.class)
                .setParameter("projectId", projectId)
                .getResultList();
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
    //projectId 와 CommentId로 찾음
    public ProjectComment selectByProjectIdAndId(Long projectId, Long commentId) {
        return
                entityManager.createQuery(
                    """
                            select pc from ProjectComment pc where pc.id =: id
                            and  pc.project.id =: projectId                    
                        """
                ,ProjectComment.class).setParameter("id",commentId)
                        .setParameter("projectId",projectId)
                        .setParameter("id", commentId)
                        .setParameter("projectId", projectId)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
    }

    public void deleteComment(ProjectComment projectComment) {
        entityManager.remove(projectComment);
    }

}
