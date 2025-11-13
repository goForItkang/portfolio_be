package com.pj.portfoliosite.portfoliosite.teampost;

import com.pj.portfoliosite.portfoliosite.global.entity.TeamPost;
import com.pj.portfoliosite.portfoliosite.global.entity.RecruitRole;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.global.dto.RecruitRoleDto;
import com.pj.portfoliosite.portfoliosite.teampost.dto.ResTeamPostDTO;
import com.pj.portfoliosite.portfoliosite.util.PersonalDataUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Repository
@Transactional
@RequiredArgsConstructor
public class TeamPostRepository {
    @PersistenceContext
    private EntityManager entityManager;
    
    private final PersonalDataUtil personalDataUtil;

    public void insertTeamPost(TeamPost teamPost) {
        try {
            entityManager.persist(teamPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TeamPost findById(Long id) {
        try {
            return entityManager.createQuery(
                            "SELECT tp FROM TeamPost tp " +
                            "LEFT JOIN FETCH tp.user " +
                            "LEFT JOIN FETCH tp.recruitRoles " +
                            "WHERE tp.id = :id",
                            TeamPost.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public List<TeamPost> selectByCreateAtDesc(int page, int size) {
        return entityManager.createQuery(
                        "select tp " +
                                "from TeamPost tp " +
                                "left join fetch tp.user u " +
                                "left join fetch tp.recruitRoles " +
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
                                "left join fetch tp.recruitRoles rr " +
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
                        "SELECT tp FROM TeamPost tp " +
                        "LEFT JOIN FETCH tp.recruitRoles " +
                        "WHERE tp.user.id = :userId AND tp.saveStatus = true ORDER BY tp.createdAt DESC",
                        TeamPost.class)
                .setParameter("userId", userId)
                .getResultList();

        return drafts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // 좋아요 많은 TeamPost 4개 조회 (메인 페이지용)
    public List<TeamPost> findTop4ByLikesInLastWeek() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        
        // 1) 먼저 좋아요 수가 많은 TeamPost ID들을 조회 (GROUP BY 사용)
        List<Long> topTeamPostIds = entityManager.createQuery(
                "SELECT tp.id " +
                "FROM TeamPost tp " +
                "LEFT JOIN tp.likes l " +
                "WHERE tp.saveStatus = false " +
                "AND (l.createdAt >= :oneWeekAgo OR l.createdAt IS NULL) " +
                "GROUP BY tp.id " +
                "ORDER BY COUNT(l.id) DESC, tp.createdAt DESC",
                Long.class)
                .setParameter("oneWeekAgo", oneWeekAgo)
                .setMaxResults(4)
                .getResultList();
        
        // 2) ID 리스트가 비어있으면 빈 리스트 반환
        if (topTeamPostIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 3) 해당 ID들의 TeamPost를 모든 관계와 함께 조회
        return entityManager.createQuery(
                "SELECT tp FROM TeamPost tp " +
                "LEFT JOIN FETCH tp.user " +
                "LEFT JOIN FETCH tp.recruitRoles " +
                "WHERE tp.id IN :ids " +
                "ORDER BY tp.createdAt DESC",
                TeamPost.class)
                .setParameter("ids", topTeamPostIds)
                .getResultList();
    }

    // 전체 팀포스트 목록 조회
    public List<TeamPost> selectAllTeamPosts(int page, int size) {
        return entityManager.createQuery(
                        "select tp " +
                                "from TeamPost tp " +
                                "left join fetch tp.user u " +
                                "left join fetch tp.recruitRoles " +
                                "where tp.saveStatus = false " +
                                "order by tp.createdAt desc, tp.id desc",
                        TeamPost.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    // 전체 팀포스트 개수 조회 (임시저장 제외)
    public Long selectAllTeamPostsCount() {
        return entityManager.createQuery(
                        "select count(tp) from TeamPost tp where tp.saveStatus = false",
                        Long.class)
                .getSingleResult();
    }

    /**
     * User의 nickname 또는 name을 복호화하여 반환
     */
    private String getDecryptedWriterName(User user) {
        if (user == null) {
            return null;
        }
        
        try {
            // nickname 우선 시도
            String nickname = user.getNickname();
            if (nickname != null && !nickname.isEmpty()) {
                try {
                    String decryptedNickname = personalDataUtil.decryptPersonalData(nickname);
                    // 복호화가 성공하고 결과가 다르면 복호화된 값 사용
                    if (!decryptedNickname.equals(nickname)) {
                        return decryptedNickname;
                    }
                    // 복호화 결과가 같으면 이미 평문
                    return nickname;
                } catch (Exception e) {
                    // 복호화 실패 시 평문으로 간주
                    return nickname;
                }
            }
            
            // nickname이 없으면 name 사용
            String name = user.getName();
            if (name != null && !name.isEmpty()) {
                try {
                    String decryptedName = personalDataUtil.decryptPersonalData(name);
                    if (!decryptedName.equals(name)) {
                        return decryptedName;
                    }
                    return name;
                } catch (Exception e) {
                    return name;
                }
            }
            
            return "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    // TeamPost를 ResTeamPostDTO로 변환하는 헬퍼 메서드
    private ResTeamPostDTO convertToDTO(TeamPost teamPost) {
        ResTeamPostDTO dto = new ResTeamPostDTO();
        dto.setId(teamPost.getId());
        dto.setTitle(teamPost.getTitle());
        dto.setWriterName(getDecryptedWriterName(teamPost.getUser()));
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
                
                // SkillInfo를 SkillDto로 변환
                if (role.getSkills() != null && !role.getSkills().isEmpty()) {
                    List<RecruitRoleDto.SkillDto> skillDtos = role.getSkills().stream()
                            .map(skillInfo -> RecruitRoleDto.SkillDto.builder()
                                    .id(skillInfo.getId())
                                    .name(skillInfo.getName())
                                    .build())
                            .collect(Collectors.toList());
                    roleDto.setSkills(skillDtos);
                }
                
                requiredRoles.add(roleDto);
            }
        }
        dto.setRequiredRoles(requiredRoles);

        return dto;
    }
}
