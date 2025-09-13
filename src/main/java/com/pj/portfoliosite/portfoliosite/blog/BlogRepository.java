package com.pj.portfoliosite.portfoliosite.blog;

import com.pj.portfoliosite.portfoliosite.blog.dto.ReqBlogDTO;
import com.pj.portfoliosite.portfoliosite.blog.dto.ResBlogInfo;
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
    // 사용자가 없는 경우
    public ResBlogInfo selectBlogInfoByBlogId(Long id) {
        Object[] row = (Object[]) em.createQuery(
                """
                    select
                    (select count(bm) from BlogBookmark bm where bm.blog.id =: blogId),
                    (select count(bl) from BlogLike bl where bl.blog.id =: blogId)
                    from Blog b 
                    where b.id =: blogId
                    """
                )
                .setParameter("blogId",id)
                .getSingleResult();
        ResBlogInfo resBlogInfo = new ResBlogInfo();
        resBlogInfo.setLikeCount((Long)row[1]);
        resBlogInfo.setBookMarkCount((Long)row[0]);
        return resBlogInfo;
    }

    public ResBlogInfo selectBlogInfoByBlogAndUserId(Long blogId, Long userId) {
        Object[] row = (Object[]) em.createQuery(
                        """
                            select
                            (select count(bm) from BlogBookmark bm where bm.blog.id =: blogId),
                            (select count(bl) from BlogLike bl where bl.blog.id =: blogId),
                            (select count(bm) from BlogBookmark bm where bm.blog.id =: blogId and bm.user.id =: userId),
                            (select count(bl) from BlogLike bl where bl.blog.id =: blogId and bl.user.id =: userId)
                            from Blog b 
                            where b.id =: blogId
                            """
                )
                .setParameter("blogId",blogId)
                .setParameter("userId",userId)
                .getSingleResult();
        ResBlogInfo resBlogInfo = new ResBlogInfo();
        resBlogInfo.setBookMarkCount((Long)row[0]);
        resBlogInfo.setLikeCount((Long)row[1]);
        if(row[2] == null){
           resBlogInfo.setBookMarkCheck(false);
        }else{
            resBlogInfo.setBookMarkCheck(true);
        }
        if(row[3] == null){
            resBlogInfo.setLikeCheck(false);
        }else{
            resBlogInfo.setLikeCheck(true);
        }
        return resBlogInfo;
    }

}
