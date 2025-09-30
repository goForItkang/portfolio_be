package com.pj.portfoliosite.portfoliosite.blog.comment;

import com.pj.portfoliosite.portfoliosite.global.entity.BlogComment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class CommentRepository {
    @PersistenceContext
    private EntityManager em;
    public List<BlogComment> selectByBlogId(Long id) {
        return em.createQuery(
                """
     select bc from BlogComment bc where bc.blog.id =:id 
 """,BlogComment.class
        ).setParameter("id",id)
                .getResultList();
    }
    // 부모 댓글 가져오기
    public BlogComment selectById(Long id) {
        return em.find(BlogComment.class, id);
    }

    public void save(BlogComment comment) {
        em.persist(comment);
    }
}
