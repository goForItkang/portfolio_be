package com.pj.portfoliosite.portfoliosite.mypage;

import com.pj.portfoliosite.portfoliosite.global.entity.*;
import com.pj.portfoliosite.portfoliosite.mypage.dto.ResCommentActivityDTO;
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

    // 사용자가 작성한 프로젝트 댓글 조회
    public List<ResCommentActivityDTO> selectProjectCommentsByUserId(Long userId) {
        return em.createQuery("""
        select new com.pj.portfoliosite.portfoliosite.mypage.dto.ResCommentActivityDTO(
            pc.id,
            pc.comment,
            pc.createdAt,
            p.id,
            p.title,
            'project'
        )
        from ProjectComment pc
        join pc.project p
        where pc.user.id = :userId
        order by pc.createdAt desc
        """, ResCommentActivityDTO.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    // 사용자가 작성한 포트폴리오 댓글 조회
    public List<ResCommentActivityDTO> selectPortfolioCommentsByUserId(Long userId) {
        return em.createQuery("""
        select new com.pj.portfoliosite.portfoliosite.mypage.dto.ResCommentActivityDTO(
            pc.id,
            pc.comment,
            pc.createdAt,
            pf.id,
            pf.title,
            'portfolio'
        )
        from PortfolioComment pc
        join pc.portfolio pf
        where pc.user.id = :userId
        order by pc.createdAt desc
        """, ResCommentActivityDTO.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    // 사용자가 작성한 팀구하기 댓글 조회
    public List<ResCommentActivityDTO> selectTeamPostCommentsByUserId(Long userId) {
        return em.createQuery("""
        select new com.pj.portfoliosite.portfoliosite.mypage.dto.ResCommentActivityDTO(
            tpc.id,
            tpc.comment,
            tpc.createdAt,
            tp.id,
            tp.title,
            'teampost'
        )
        from TeamPostComment tpc
        join tpc.teamPost tp
        where tpc.user.id = :userId
        order by tpc.createdAt desc
        """, ResCommentActivityDTO.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
