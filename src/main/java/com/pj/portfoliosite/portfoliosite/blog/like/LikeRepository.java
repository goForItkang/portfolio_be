package com.pj.portfoliosite.portfoliosite.blog.like;

import com.pj.portfoliosite.portfoliosite.global.entity.BlogLike;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class LikeRepository {
    @PersistenceContext
    private EntityManager em;
    // 좋아요 저장
    public void save(BlogLike blogLike) {
        em.persist(blogLike);
    }

    public void deleteByBlogIdAndUserId(Long BlogId, Long UserId) {
        em.createQuery(
                "DELETE FROM BlogLike l " +
                        "WHERE l.blog.id = :BlogId " +
                        "AND l.user.id = :UserId"
        ).setParameter("BlogId",BlogId)
                .setParameter("UserId",UserId)
                .executeUpdate();
    }
}
