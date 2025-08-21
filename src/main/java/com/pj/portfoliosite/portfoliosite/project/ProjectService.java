package com.pj.portfoliosite.portfoliosite.project;

import com.pj.portfoliosite.portfoliosite.global.dto.ReqProject;
import com.pj.portfoliosite.portfoliosite.global.dto.ResProjectRecommendDto;
import com.pj.portfoliosite.portfoliosite.global.entity.Project;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.user.UserService;
import com.pj.portfoliosite.portfoliosite.util.ImgUtil;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService; // userServiced;
    private final UserRepository userRepository;
    private final ImgUtil imageUtil;
    private final ImgUtil imgUtil;

    //추천 프로젝트 로직 오늘 부터 일주일 동안 가장 많은 좋아요 갯수
    public List<ResProjectRecommendDto> getRecommend() {
        //로직 오늘 날짜 부터 7일중 가장 좋아요 많은 프로젝트 12개 선정
        // 오늘 날짜
        LocalDate today = LocalDate.now();
        // 7일 이후 날짜
        LocalDate weekAgo = today.minusWeeks(1);

        List<Project> projects = projectRepository.findTopProjectsByLikesInPeriod(today,weekAgo);
        return null;
    }

    public void projectUpload(ReqProject reqProject) throws IOException {
        //TEST 단계에서 값을 가져옴
        // 실제 배포단계면  securitContectHolder 에 값 가져옴
        String testLoginId = "portfolio@naver.com";
        Optional<User> user = userRepository.findByEmail(testLoginId);
        if(user.isPresent()) {
            // Null이 아닐경우 project 에 user 삽입
            Project project = new Project();
            project.setUser(user.get()); // 사입하고
            project.setProject(reqProject);
            String imgUrl = imgUtil.imgUpload(reqProject.getThumbnailImg());
            String demonstrationURL = imgUtil.imgUpload(reqProject.getDemonstrationVideo());
            project.setThumbnailURL(imgUrl);
            project.setDemonstrationVideo(demonstrationURL);
            projectRepository.insertProject(project);
        }
    }
}
