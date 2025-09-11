package com.pj.portfoliosite.portfoliosite.blog.bookmark;

import com.pj.portfoliosite.portfoliosite.global.entity.BlogBookmark;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
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
}
