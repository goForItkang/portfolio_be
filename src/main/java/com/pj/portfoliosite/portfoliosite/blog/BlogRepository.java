package com.pj.portfoliosite.portfoliosite.blog;

import com.pj.portfoliosite.portfoliosite.blog.dto.ReqBlogDTO;
import com.pj.portfoliosite.portfoliosite.global.entity.Blog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlogRepository {
    @PersistenceContext
    private EntityManager em;

    public void save(Blog blog) {
        em.persist(blog);
    }

    public Blog selectById(Long id) {
        return em.find(Blog.class, id);
    }

    public void delete(Blog blog) {
        em.remove(blog);
    }
}
