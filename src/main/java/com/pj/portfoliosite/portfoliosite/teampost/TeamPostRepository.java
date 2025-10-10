package com.pj.portfoliosite.portfoliosite.teampost;

import com.pj.portfoliosite.portfoliosite.global.entity.TeamPost;
import com.pj.portfoliosite.portfoliosite.global.entity.RecruitRole;
import com.pj.portfoliosite.portfoliosite.global.dto.RecruitRoleDto;
import com.pj.portfoliosite.portfoliosite.teampost.dto.ResTeamPostDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Repository
@Transactional
public class TeamPostRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void insertTeamPost(TeamPost teamPost) {
        try {
            entityManager.persist(teamPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TeamPost findById(Long id) {
        return entityManager.find(TeamPost.class, id);
    }

    public List<TeamPost> selectByCreateAtDesc(int page, int size) {
        return entityManager.createQuery(
                        "select tp " +
                                "from TeamPost tp " +
                                "left join fetch tp.user u " +
                                "where tp.saveStatus = false " +
                                "order by tp.createdAt desc, tp.id desc",
                        TeamPost.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    public Long selectAllCount() {
        return entityManager.createQuery(
                        "select count(tp) from TeamPost tp where tp.saveStatus = false",
                        Long.class)
                .getSingleResult();
    }

    // 카테고리별 최신 게시글 조회
    public List<TeamPost> selectByCreateAtDescWithCategory(int page, int size, String category) {
        return entityManager.createQuery(
                        "select distinct tp " +
                                "from TeamPost tp " +
                                "left join fetch tp.user u " +
                                "left join tp.recruitRoles rr " +
                                "where tp.saveStatus = false " +
                                "and rr.role = :category " +
                                "order by tp.createdAt desc, tp.id desc",
                        TeamPost.class)
                .setParameter("category", category)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    // 카테고리별 게시글 수 조회
    public Long selectCountByCategory(String category) {
        return entityManager.createQuery(
                        "select count(distinct tp) " +
                                "from TeamPost tp " +
                                "left join tp.recruitRoles rr " +
                                "where tp.saveStatus = false " +
                                "and rr.role = :category",
                        Long.class)
                .setParameter("category", category)
                .getSingleResult();
    }

    public void updateTeamPost(TeamPost teamPost) {
        entityManager.merge(teamPost);
    }

    public void deleteTeamPost(Long id) {
        TeamPost teamPost = entityManager.find(TeamPost.class, id);
        if (teamPost != null) {
            entityManager.remove(teamPost);
        }
    }

    public TeamPost getReference(Long teamPostId) {
        return entityManager.getReference(TeamPost.class, teamPostId);
    }

    // 사용자의 임시저장 게시물 조회
    public List<ResTeamPostDTO> findDraftsByUserId(Long userId) {
        List<TeamPost> drafts = entityManager.createQuery(
                        "SELECT tp FROM TeamPost tp WHERE tp.user.id = :userId AND tp.saveStatus = true ORDER BY tp.createdAt DESC",
                        TeamPost.class)
                .setParameter("userId", userId)
                .getResultList();

        return drafts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // 일주일간 좋아요를 많이 받은 TeamPost 4개 조회 (메인 페이지용)
    public List<TeamPost> findTop4ByLikesInLastWeek() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        
        return entityManager.createQuery(
                "SELECT tp FROM TeamPost tp " +
                "LEFT JOIN tp.likes l " +
                "WHERE tp.saveStatus = false " +
                "AND (l.createdAt >= :oneWeekAgo OR l.createdAt IS NULL) " +
                "GROUP BY tp.id " +
                "ORDER BY COUNT(l.id) DESC, tp.createdAt DESC",
                TeamPost.class)
                .setParameter("oneWeekAgo", oneWeekAgo)
                .setMaxResults(4)
                .getResultList();
    }

    // TeamPost를 ResTeamPostDTO로 변환하는 헬퍼 메서드
    private ResTeamPostDTO convertToDTO(TeamPost teamPost) {
        ResTeamPostDTO dto = new ResTeamPostDTO();
        dto.setId(teamPost.getId());
        dto.setTitle(teamPost.getTitle());
        dto.setWriterName(teamPost.getUser() != null ? teamPost.getUser().getName() : null);
        dto.setCreatedAt(teamPost.getCreatedAt());
        dto.setRecruitStatus(teamPost.getRecruitStatus().toString());
        dto.setViewCount(teamPost.getViewCount());
        dto.setLikeCount(teamPost.getLikes() != null ? teamPost.getLikes().size() : 0);

        List<RecruitRoleDto> requiredRoles = new ArrayList<>();
        if (teamPost.getRecruitRoles() != null) {
            for (RecruitRole role : teamPost.getRecruitRoles()) {
                RecruitRoleDto roleDto = new RecruitRoleDto();
                roleDto.setRole(role.getRole());
                roleDto.setCount(role.getCount());
                roleDto.setPeople(role.getPeople());
                roleDto.setSkills(role.getSkills());
                requiredRoles.add(roleDto);
            }
        }
        dto.setRequiredRoles(requiredRoles);

        return dto;
    }
}