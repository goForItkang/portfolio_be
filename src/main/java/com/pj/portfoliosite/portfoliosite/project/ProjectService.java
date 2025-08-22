package com.pj.portfoliosite.portfoliosite.project;

import com.pj.portfoliosite.portfoliosite.global.dto.ReqProject;
import com.pj.portfoliosite.portfoliosite.global.dto.ResProjectDto;
import com.pj.portfoliosite.portfoliosite.global.dto.ResProjectDetailDTO;
import com.pj.portfoliosite.portfoliosite.global.dto.ResProjectRecommendDto;
import com.pj.portfoliosite.portfoliosite.global.entity.Project;
import com.pj.portfoliosite.portfoliosite.global.entity.ProjectComment;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.project.comment.ProjectCommentRepository;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.user.UserService;
import com.pj.portfoliosite.portfoliosite.util.ImgUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private final ProjectCommentRepository projectCommentRepository;


    //추천 프로젝트 로직 오늘 부터 일주일 동안 가장 많은 좋아요 갯수
    public List<ResProjectRecommendDto> getRecommend() {
        //로직 오늘 날짜 부터 7일중 가장 좋아요 많은 프로젝트 12개 선정
        // 오늘 날짜
        LocalDate today = LocalDate.now();
        // 7일 이후 날짜
        LocalDate weekAgo = today.minusWeeks(1);

        List<Project> projects = projectRepository.findTopProjectsByLikesInPeriod(today,weekAgo);
        List<ResProjectRecommendDto> result = new ArrayList<>();
        for(Project project : projects) {
            ResProjectRecommendDto resProjectRecommendDto = new ResProjectRecommendDto();
            resProjectRecommendDto.setId(project.getId());
            resProjectRecommendDto.setTitle(project.getTitle());
            resProjectRecommendDto.setDescription(project.getDescription());
            resProjectRecommendDto.setWriteName(project.getUser().getName());
            resProjectRecommendDto.setThumbnailURL(project.getThumbnailURL());
            result.add(resProjectRecommendDto);
        }
        return result;
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
    // 프로젝트 상세 페이지 가져오기
    public ResProjectDetailDTO projectGetById(Long id) {
        Project project = projectRepository.findById(id);
        // 사용자 ID 가져오기
        String testLoginId = "portfolio@naver.com";
        Optional<User> user = userRepository.findByEmail(testLoginId);

        // 사용자가 null 일경우
        // 이메일로 수정함 ????
        if(user.isPresent()) {

        } // 사용자가 없을 경우 bookmark 및 like 는 false 로 변경해서 보낼예정임
        else{

        } // 사용자 정보를 찾아서 logic bookmark like
        //댓글 가져오기

        List<ProjectComment> projectComments = projectCommentRepository.findByProjectId(id);
        return null;
    }



    public List<ResProjectDto> getProjects(int page, int size) {
        List<Project> projects = projectRepository.selectByCreateAtDesc(page,size); //project
        Long count =  projectRepository.selectAllCount();
        return null;
    }
}
