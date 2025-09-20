package com.pj.portfoliosite.portfoliosite.blog;

import com.pj.portfoliosite.portfoliosite.blog.dto.ResBlogInfo;
import com.pj.portfoliosite.portfoliosite.global.entity.Blog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@Transactional
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
        if(((Long)row[2])>0){
            resBlogInfo.setBookMarkCheck(true);
        }else{
            resBlogInfo.setBookMarkCheck(false);
        }
        if(((Long)row[3])>0){
            resBlogInfo.setLikeCheck(true);
        }else{
            resBlogInfo.setLikeCheck(false);
        }
        return resBlogInfo;
    }

    public List<Blog> selectByLikeDesc(LocalDate today, LocalDate weekAgo) {
        return em.createQuery(
                """
        select b from Blog b
        left join b.blogLikes bl
        where b.createdAt between :startDate and :andDate
        group by b 
        order by count(bl) desc
        
""",Blog.class
        ).setParameter("startDate", weekAgo.atStartOfDay())
                .setParameter("andDate", today.atTime(23,59,59))
                .getResultList();
    }


}
