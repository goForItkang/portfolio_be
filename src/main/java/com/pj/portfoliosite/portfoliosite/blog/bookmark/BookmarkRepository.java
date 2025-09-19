package com.pj.portfoliosite.portfoliosite.blog.bookmark;

import com.pj.portfoliosite.portfoliosite.global.entity.BlogBookmark;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class BookmarkRepository {
    @PersistenceContext
    private EntityManager em;
    public void save(BlogBookmark blogBookmark) {
        em.persist(blogBookmark);
    }

    public void delete(Long blogId, Long userId) {
        em.createQuery(
                "DELETE FROM BlogBookmark l " +
                        "WHERE l.blog.id = :blogId " +
                        "AND l.user.id = :userId"
        ).setParameter("blogId",blogId)
                .setParameter("userId",userId)
                .executeUpdate();
    }

    public boolean selectBlogIdAndUserId(Long id, Long id1) {
        Long result = em.createQuery(
                "SELECT COUNT(b) FROM BlogBookmark b WHERE b.blog.id = :id AND b.user.id = :id1", Long.class
        ).setParameter("id", id).setParameter("id1", id1).getSingleResult();
        return result > 0;
    }
}
