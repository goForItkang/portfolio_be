package com.pj.portfoliosite.portfoliosite.mypage;

import com.pj.portfoliosite.portfoliosite.blog.BlogRepository;
import com.pj.portfoliosite.portfoliosite.blog.BlogService;
import com.pj.portfoliosite.portfoliosite.blog.dto.ResBlogDTO;
import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.dto.ResProjectDto;
import com.pj.portfoliosite.portfoliosite.global.entity.*;
import com.pj.portfoliosite.portfoliosite.mypage.dto.ResBookmark;
import com.pj.portfoliosite.portfoliosite.mypage.dto.ResWorkLikeDTO;
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

import java.util.*;

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
    private final MyPageRepository myPageRepository;

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

    public ResWorkLikeDTO getComment() {
        //사용자 정보
        return null;
    }

    public List<ResWorkLikeDTO> getLike() {
        //사용자 정보
        String email =  SecurityContextHolder.getContext().getAuthentication().getName();
        String endoceEamil = aesUtil.encode(email);
        Optional<User> user = userRepository.findByEmail(endoceEamil);
        List<ResWorkLikeDTO> resWorkLikeDTOs = new ArrayList<>();
        if(user.isPresent()){
            // 사용자가 있는경우
            // project 게시물에 좋아요 있는 user_id 쿼리문
            List<Project> project = myPageRepository.selectProjectByLikeUserId(user.get().getId());
            // portfolio 게시물에 좋아요 있는 user_id 쿼리문
            List<PortFolio> portFolio = myPageRepository.selectPortfolioByLikeUserId(user.get().getId());
            // blog
            List<Blog> blogs = myPageRepository.selectBlogByLikeUserId(user.get().getId());
            // teamProject
            List<TeamPost> teamPosts = myPageRepository.selectTeamPostByLikeUserId(user.get().getId());
            for (TeamPost teamPost : teamPosts) {
                ResWorkLikeDTO resWorkLikeDTO = new ResWorkLikeDTO();
                resWorkLikeDTO.setId(teamPost.getId());
                resWorkLikeDTO.setTitle(teamPost.getTitle());
                resWorkLikeDTO.setType("teampost");
                resWorkLikeDTO.setCreateTime(teamPost.getCreatedAt());
                resWorkLikeDTOs.add(resWorkLikeDTO);
            }
            for (Blog blog : blogs) {
                ResWorkLikeDTO resWorkLikeDTO = new ResWorkLikeDTO();
                resWorkLikeDTO.setId(blog.getId());
                resWorkLikeDTO.setTitle(blog.getTitle());
                resWorkLikeDTO.setType("blog");
                resWorkLikeDTO.setCreateTime(blog.getCreatedAt());
                resWorkLikeDTOs.add(resWorkLikeDTO);
            }
            for (PortFolio folio : portFolio) {
                ResWorkLikeDTO resWorkLikeDTO = new ResWorkLikeDTO();
                resWorkLikeDTO.setId(folio.getId());
                resWorkLikeDTO.setTitle(folio.getTitle());
                resWorkLikeDTO.setType("portfolio");
                resWorkLikeDTO.setCreateTime(folio.getCreateAt());
                resWorkLikeDTOs.add(resWorkLikeDTO);
            }
            for (Project project1 : project) {
                ResWorkLikeDTO resWorkLikeDTO = new ResWorkLikeDTO();
                resWorkLikeDTO.setId(project1.getId());
                resWorkLikeDTO.setTitle(project1.getTitle());
                resWorkLikeDTO.setType("project");
                resWorkLikeDTO.setCreateTime(project1.getCreatedAt());
                resWorkLikeDTOs.add(resWorkLikeDTO);
            }
            return resWorkLikeDTOs;
        }else{

        }
        return null;
    }
}
