package com.pj.portfoliosite.portfoliosite.mypage;

import com.pj.portfoliosite.portfoliosite.global.entity.Blog;
import com.pj.portfoliosite.portfoliosite.global.entity.PortFolio;
import com.pj.portfoliosite.portfoliosite.global.entity.Project;
import com.pj.portfoliosite.portfoliosite.global.entity.TeamPost;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MyPageRepository {
    @PersistenceContext
    private EntityManager em;
    public List<Project> selectProjectByLikeUserId(Long id) {
       return em.createQuery(
                """
    select p from Project p join ProjectLike pl on pl.project.id=p.id where pl.user.id =:id
""",Project.class).setParameter("id", id).getResultList();

    }

    public List<PortFolio> selectPortfolioByLikeUserId(Long userId) {
        return em.createQuery("""
        select pl.portfolio
        from PortFolioLike pl
        where pl.user.id = :userId
        """, PortFolio.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Blog> selectBlogByLikeUserId(Long userId) {
        return em.createQuery("""
        select bl.blog
        from BlogLike bl
        where bl.user.id = :userId
        """, Blog.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<TeamPost> selectTeamPostByLikeUserId(Long userId) {
        return em.createQuery("""
        select tl.teamPost
        from TeamPostLike tl
        where tl.user.id = :userId
        """, TeamPost.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
