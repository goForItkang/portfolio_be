package com.pj.portfoliosite.portfoliosite.teampost;

import com.pj.portfoliosite.portfoliosite.global.dto.ReqPostWriteDTO;
import com.pj.portfoliosite.portfoliosite.global.entity.RecruitRole;
import com.pj.portfoliosite.portfoliosite.global.entity.TeamPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamPostService {
    private final TeamPostRepository teamPostRepository;

    // 팀원 작성글 service
    public void teamPostWrite(ReqPostWriteDTO reqPostWriteDTO) {
        // teampost 내용
        TeamPost teamPost = TeamPost.builder()
                .title(reqPostWriteDTO.getTitle())
                .content(reqPostWriteDTO.getContent())
                .build();

        //인원
        List<RecruitRole> recruitRoles = new ArrayList<>();
        for(int i =0; i<reqPostWriteDTO.getRecruitList().size(); i++) {
            RecruitRole recruitRole = new RecruitRole();
            recruitRoles.add(
                    recruitRole.builder().
                            role(reqPostWriteDTO.getRecruitList().get(i).getRole())
                            .count(reqPostWriteDTO.getRecruitList().get(i).getCount())
                            .teamPost(teamPost)
                            .build()
            );
        }
        teamPost.addRecruitRole(recruitRoles);


        teamPostRepository.teamPostWrite(teamPost);
    }
}
