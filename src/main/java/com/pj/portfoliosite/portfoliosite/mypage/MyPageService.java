package com.pj.portfoliosite.portfoliosite.mypage;

import com.pj.portfoliosite.portfoliosite.blog.BlogRepository;
import com.pj.portfoliosite.portfoliosite.blog.BlogService;
import com.pj.portfoliosite.portfoliosite.blog.dto.ResBlogDTO;
import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.dto.ResProjectDto;
import com.pj.portfoliosite.portfoliosite.global.entity.Blog;
import com.pj.portfoliosite.portfoliosite.global.entity.PortFolio;
import com.pj.portfoliosite.portfoliosite.global.entity.Project;
import com.pj.portfoliosite.portfoliosite.mypage.dto.ResBookmark;
import com.pj.portfoliosite.portfoliosite.portfolio.PortFolioRepository;
import com.pj.portfoliosite.portfoliosite.portfolio.PortFolioService;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.ResPortFolioDTO;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.ResPortfolioDetailDTO;
import com.pj.portfoliosite.portfoliosite.project.ProjectRepository;
import com.pj.portfoliosite.portfoliosite.project.ProjectService;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.user.UserService;
import com.pj.portfoliosite.portfoliosite.util.AESUtil;
import lombok.Data;
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
    // 사용자 사용자 북마크한
    public DataResponse getBookMark() {
        String email =  SecurityContextHolder.getContext().getAuthentication().getName();
        String endoceEamil = aesUtil.encode(email);
        DataResponse dataResponse = new DataResponse();
        if(userRepository.findByEmail(endoceEamil).isPresent()){
            List<Project> projects = projectRepository.findProjectBookmarksByUserEmail(endoceEamil);
            List<Blog> blogList = blogRepository.findBlogBookmarksByUserEmail(endoceEamil);
            List<PortFolio> portfolioList = portfolioRepository.findPortfolioBookmarksByUserEmail(endoceEamil);
            //
            List<ResProjectDto> resProjectDtos = new ArrayList<>();
            for (Project project : projects) {
                ResProjectDto resProjectDto = new ResProjectDto();
                resProjectDto.setId(project.getId());
                resProjectDto.setTitle(project.getTitle());
                resProjectDto.setRole(project.getRole());
                resProjectDto.setThumbnailURL(project.getThumbnailURL());
                resProjectDto.setWriteName(aesUtil.decode(project.getUser().getNickname()));
                resProjectDtos.add(resProjectDto);
            }
            List<ResPortFolioDTO> resPortFolio = portfolioService.portfolioDTOTOEntity(portfolioList);
            List<ResBlogDTO> resBlogs = blogService.blogListToResBlogDTOList(blogList);
            if(resProjectDtos.isEmpty()&& resBlogs.isEmpty()&& resPortFolio.isEmpty()){
                dataResponse.setStatus(404);
                return  dataResponse;
            }
            ResBookmark resBookmark = new ResBookmark(resBlogs,resPortFolio,resProjectDtos);

        }else{
            dataResponse.setStatus(401);
            dataResponse.setMessage("접근 권한이 없습니다.");
        }
        return dataResponse;
    }

    // 사용자가 좋아요한 게시글, 블로그, 포트폴리오
    public DataResponse getActivityLike() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String encodedEmail = aesUtil.encode(email);
        DataResponse dataResponse = new DataResponse();
        
        if (userRepository.findByEmail(encodedEmail).isPresent()) {
            List<Project> likedProjects = projectRepository.findProjectLikesByUserEmail(encodedEmail);
            List<Blog> likedBlogs = blogRepository.findBlogLikesByUserEmail(encodedEmail);
            
            List<com.pj.portfoliosite.portfoliosite.mypage.dto.ResWorkLikeDTO> workLikeDTOList = new ArrayList<>();
            
            // 프로젝트 좋아요 추가
            for (Project project : likedProjects) {
                com.pj.portfoliosite.portfoliosite.mypage.dto.ResWorkLikeDTO dto = new com.pj.portfoliosite.portfoliosite.mypage.dto.ResWorkLikeDTO();
                dto.setId(project.getId());
                dto.setTitle(project.getTitle());
                dto.setCreateTime(project.getCreatedAt());
                dto.setType("PROJECT");
                dto.setDescription(project.getDescription());
                dto.setThumbnailURL(project.getThumbnailURL());
                workLikeDTOList.add(dto);
            }
            
            // 블로그 좋아요 추가
            for (Blog blog : likedBlogs) {
                com.pj.portfoliosite.portfoliosite.mypage.dto.ResWorkLikeDTO dto = new com.pj.portfoliosite.portfoliosite.mypage.dto.ResWorkLikeDTO();
                dto.setId(blog.getId());
                dto.setTitle(blog.getTitle());
                dto.setCreateTime(blog.getCreatedAt());
                dto.setType("BLOG");
                dto.setDescription(blog.getContent() != null ? blog.getContent().substring(0, Math.min(100, blog.getContent().length())) : "");
                dto.setThumbnailURL(blog.getThumbnailURL());
                workLikeDTOList.add(dto);
            }
            
            if (workLikeDTOList.isEmpty()) {
                dataResponse.setStatus(404);
                dataResponse.setMessage("좋아요한 게시글이 없습니다.");
            } else {
                dataResponse.setData(workLikeDTOList);
                dataResponse.setStatus(200);
            }
        } else {
            dataResponse.setStatus(401);
            dataResponse.setMessage("접근 권한이 없습니다.");
        }
        
        return dataResponse;
    }
}
