package com.pj.portfoliosite.portfoliosite.portfolio.comment;

import com.pj.portfoliosite.portfoliosite.global.entity.PortfolioComment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
// import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class PortFolioCommentRepository {
    @PersistenceContext
    private EntityManager entityManager;

    // 포트폴리오 Id로 부모 댓글 및 (선택) 대댓글 가져오기
    public List<PortfolioComment> findByPortfolioId(Long portfolioId) {
        return entityManager.createQuery(
                        "SELECT fc FROM PortfolioComment fc " +
                                "JOIN FETCH fc.user u " +
                                "LEFT JOIN FETCH fc.replies r " +
                                "WHERE fc.portfolio.id = :portfolioId " +
                                "AND fc.parent IS NULL " +
                                "ORDER BY fc.createdAt DESC", PortfolioComment.class
                )
                .setParameter("portfolioId", portfolioId)
                .getResultList();
    }

    // 부모 댓글 레퍼런스
    public PortfolioComment getReference(Long parentCommentId) {
        return entityManager.getReference(PortfolioComment.class, parentCommentId);
    }

    @Transactional
    public void insertComment(PortfolioComment comment) {
        entityManager.persist(comment);
    }

    // portfolioId + commentId로 단건 조회
    public PortfolioComment selectByPortfolioIdAndId(Long portfolioId, Long commentId) {
        return entityManager.createQuery(
                        """
                        select fc
                        from PortfolioComment fc
                        where fc.id = :id
                          and fc.portfolio.id = :portfolioId
                        """, PortfolioComment.class
                )
                .setParameter("id", commentId)
                .setParameter("portfolioId", portfolioId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public void deleteComment(PortfolioComment comment) {
        entityManager.remove(comment);
    }

    public List<PortfolioComment> findByPortfolioIdAndParentIsNull(Long portfolioId) {
        return entityManager.createQuery(
                        "SELECT pc FROM PortfolioComment pc " +
                                "LEFT JOIN FETCH pc.user " + // 댓글 작성자 정보를 함께 가져오기 (N+1 문제 방지)
                                "WHERE pc.portfolio.id = :portfolioId AND pc.parent IS NULL " +
                                "ORDER BY pc.createdAt ASC", // 오래된 댓글부터 정렬
                        PortfolioComment.class
                )
                .setParameter("portfolioId", portfolioId)
                .getResultList();
    }

}
