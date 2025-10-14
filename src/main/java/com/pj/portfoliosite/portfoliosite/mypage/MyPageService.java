package com.pj.portfoliosite.portfoliosite.mypage;

import com.pj.portfoliosite.portfoliosite.blog.BlogRepository;
import com.pj.portfoliosite.portfoliosite.blog.BlogService;
import com.pj.portfoliosite.portfoliosite.blog.dto.ResBlogDTO;
import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.dto.ResProjectDto;
import com.pj.portfoliosite.portfoliosite.global.entity.Blog;
import com.pj.portfoliosite.portfoliosite.global.entity.PortFolio;
import com.pj.portfoliosite.portfoliosite.global.entity.Project;
import com.pj.portfoliosite.portfoliosite.portfolio.PortFolioRepository;
import com.pj.portfoliosite.portfoliosite.portfolio.PortFolioService;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.ResPortFolioDTO;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.ResPortfolioDetailDTO;
import com.pj.portfoliosite.portfoliosite.project.ProjectRepository;
import com.pj.portfoliosite.portfoliosite.project.ProjectService;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.user.UserService;
import com.pj.portfoliosite.portfoliosite.util.AESUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final PortFolioRepository  portfolioRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PortFolioService portfolioService;
    private final AESUtil aesUtil;
    private final BlogRepository blogRepository;
    private final BlogService blogService;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    public DataResponse getPortfolio() {
        DataResponse dataResponse = new DataResponse(); //객체 생성
        String email =  SecurityContextHolder.getContext().getAuthentication().getName();
        String endoceEamil = aesUtil.encode(email);
        if(endoceEamil == null){
            dataResponse.setData(null);
            dataResponse.setStatus(401);
            dataResponse.setMessage("로그인이 필요합니다. ");
        return dataResponse;
        }else if(userRepository.findByEmail(endoceEamil).isPresent()){
            List<PortFolio> portFolioList = portfolioRepository.selectByUserEmail(endoceEamil);
            if(portFolioList.isEmpty()){
                dataResponse.setData(null);
                dataResponse.setStatus(404);
                dataResponse.setMessage("메세지가 없음");
                return dataResponse;
            }else{
                List<ResPortFolioDTO> res = portfolioService.portfolioDTOTOEntity(portFolioList);
                dataResponse.setData(res);
                dataResponse.setStatus(200);
                return dataResponse;
            }
        }
        return null;
    }

    public DataResponse getBlog() {
        DataResponse dataResponse = new DataResponse();
        String email =  SecurityContextHolder.getContext().getAuthentication().getName();
        String endoceEamil = aesUtil.encode(email);
        if(userRepository.findByEmail(endoceEamil).isPresent()){
            List<Blog> blogList = blogRepository.selectByUserEmail(endoceEamil);
            if(blogList.isEmpty()){
                dataResponse.setData(null);
                dataResponse.setStatus(404);
                dataResponse.setMessage("조회된 정보가 없습니다.");
                return dataResponse;
            }else{
                dataResponse.setData(blogService.entityTOBlogDTO(blogList));
                dataResponse.setStatus(200);
                return dataResponse;
            }
        }
        dataResponse.setData(null);
        dataResponse.setStatus(401);
        dataResponse.setMessage("로그인이 필요합니다.");
        return dataResponse;
    }

    public DataResponse getProject() {
        DataResponse dataResponse = new DataResponse();
        String email =  SecurityContextHolder.getContext().getAuthentication().getName();
        String endoceEamil = aesUtil.encode(email);
        if(userRepository.findByEmail(endoceEamil).isPresent()){
            List<Project> projects = projectRepository.findByUserEmail(endoceEamil);
            if(projects.isEmpty()) {
                dataResponse.setStatus(404);
                dataResponse.setMessage("프로젝트가 없습니다.");
            }else{
                List<ResProjectDto> resProjectDtos = new ArrayList<>();

                for (Project project : projects) {
                    ResProjectDto dto = new ResProjectDto();
                    dto.setId(project.getId());
                    dto.setTitle(project.getTitle());
                    dto.setRole(project.getRole());
                    dto.setThumbnailURL(project.getThumbnailURL());
                    dto.setWriteName(aesUtil.decode(project.getUser().getNickname()));
                    resProjectDtos.add(dto);
                }
                dataResponse.setData(resProjectDtos);
                dataResponse.setStatus(200);
                return dataResponse;
            }



        }
        dataResponse.setStatus(404);
        dataResponse.setMessage("로그인을 해주세요");
        return dataResponse;
    }
}
