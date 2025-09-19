package com.pj.portfoliosite.portfoliosite.blog.like;

import com.pj.portfoliosite.portfoliosite.global.entity.BlogLike;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
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

    public boolean selectByBlogIdAndUserID(Long blogId, Long userId) {
        em.createQuery(
                "SELECT COUNT(l) FROM BlogLike l WHERE l.blog.id = :id AND l.user.id = :id1", Long.class
        ).setParameter("id", blogId).setParameter("id1", userId).getSingleResult();
        return true;
    }
}
