package com.pj.portfoliosite.portfoliosite.mypage;

import com.pj.portfoliosite.portfoliosite.blog.BlogRepository;
import com.pj.portfoliosite.portfoliosite.blog.BlogService;
import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.dto.ResProjectDto;
import com.pj.portfoliosite.portfoliosite.global.entity.*;
import com.pj.portfoliosite.portfoliosite.mypage.dto.ResCommentActivityDTO;
import com.pj.portfoliosite.portfoliosite.mypage.dto.ResWorkBookmarkDTO;
import com.pj.portfoliosite.portfoliosite.mypage.dto.ResWorkLikeDTO;
import com.pj.portfoliosite.portfoliosite.portfolio.PortFolioRepository;
import com.pj.portfoliosite.portfoliosite.portfolio.PortFolioService;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.ResPortFolioDTO;
import com.pj.portfoliosite.portfoliosite.project.ProjectRepository;
import com.pj.portfoliosite.portfoliosite.project.ProjectService;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.user.UserService;
import com.pj.portfoliosite.portfoliosite.util.AESUtil;
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
        return dataResponse;
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
                return dataResponse;
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
        dataResponse.setStatus(401);
        dataResponse.setMessage("로그인을 해주세요");
        return dataResponse;
    }
    // 사용자 북마크 - 통합 배열 방식
    public DataResponse getBookMark() {
        String email =  SecurityContextHolder.getContext().getAuthentication().getName();
        String endoceEamil = aesUtil.encode(email);
        DataResponse dataResponse = new DataResponse();
        Optional<User> user = userRepository.findByEmail(endoceEamil);
        
        if(!user.isPresent()) {
            dataResponse.setStatus(401);
            dataResponse.setMessage("접근 권한이 없습니다.");
            return dataResponse;
        }
        
        Long userId = user.get().getId();
        List<ResWorkBookmarkDTO> resWorkBookmarkDTOs = new ArrayList<>();
        
        // 프로젝트 북마크
        List<Project> projects = projectRepository.findProjectBookmarksByUserEmail(endoceEamil);
        for (Project project : projects) {
            ResWorkBookmarkDTO resWorkBookmarkDTO = new ResWorkBookmarkDTO();
            resWorkBookmarkDTO.setId(project.getId());
            resWorkBookmarkDTO.setTitle(project.getTitle());
            resWorkBookmarkDTO.setType("project");
            resWorkBookmarkDTO.setCreateTime(project.getCreatedAt());
            resWorkBookmarkDTO.setDescription(project.getDescription());
            resWorkBookmarkDTO.setThumbnailURL(project.getThumbnailURL());
            resWorkBookmarkDTOs.add(resWorkBookmarkDTO);
        }
        
        // 블로그 북마크
        List<Blog> blogList = blogRepository.findBlogBookmarksByUserEmail(endoceEamil);
        for (Blog blog : blogList) {
            ResWorkBookmarkDTO resWorkBookmarkDTO = new ResWorkBookmarkDTO();
            resWorkBookmarkDTO.setId(blog.getId());
            resWorkBookmarkDTO.setTitle(blog.getTitle());
            resWorkBookmarkDTO.setType("blog");
            resWorkBookmarkDTO.setCreateTime(blog.getCreatedAt());
            resWorkBookmarkDTO.setDescription(blog.getContent() != null ? blog.getContent().substring(0, Math.min(100, blog.getContent().length())) : null);
            resWorkBookmarkDTO.setThumbnailURL(blog.getThumbnailURL());
            resWorkBookmarkDTOs.add(resWorkBookmarkDTO);
        }
        
        // 포트폴리오 북마크
        List<PortFolio> portfolioList = portfolioRepository.findPortfolioBookmarksByUserEmail(endoceEamil);
        for (PortFolio folio : portfolioList) {
            ResWorkBookmarkDTO resWorkBookmarkDTO = new ResWorkBookmarkDTO();
            resWorkBookmarkDTO.setId(folio.getId());
            resWorkBookmarkDTO.setTitle(folio.getTitle());
            resWorkBookmarkDTO.setType("portfolio");
            resWorkBookmarkDTO.setCreateTime(folio.getCreateAt());
            resWorkBookmarkDTO.setDescription(folio.getIntroductions() != null ? folio.getIntroductions().substring(0, Math.min(100, folio.getIntroductions().length())) : null);
            resWorkBookmarkDTO.setThumbnailURL(folio.getThumbnailURL());
            resWorkBookmarkDTOs.add(resWorkBookmarkDTO);
        }
        
        // TeamPost 북마크
        List<TeamPost> teamPostList = myPageRepository.selectTeamPostBookmarksByUserId(userId);
        for (TeamPost teamPost : teamPostList) {
            ResWorkBookmarkDTO resWorkBookmarkDTO = new ResWorkBookmarkDTO();
            resWorkBookmarkDTO.setId(teamPost.getId());
            resWorkBookmarkDTO.setTitle(teamPost.getTitle());
            resWorkBookmarkDTO.setType("teampost");
            resWorkBookmarkDTO.setCreateTime(teamPost.getCreatedAt());
            resWorkBookmarkDTO.setDescription(teamPost.getContent() != null ? teamPost.getContent().substring(0, Math.min(100, teamPost.getContent().length())) : null);
            resWorkBookmarkDTOs.add(resWorkBookmarkDTO);
        }
        
        // 모든 북마크가 비어있는지 확인
        if(resWorkBookmarkDTOs.isEmpty()) {
            dataResponse.setStatus(404);
            dataResponse.setMessage("북마크한 항목이 없습니다.");
            return dataResponse;
        }
        
        dataResponse.setData(resWorkBookmarkDTOs);
        dataResponse.setStatus(200);
        return dataResponse;
    }

    public DataResponse getComment() {
        String email =  SecurityContextHolder.getContext().getAuthentication().getName();
        String encodedEmail = aesUtil.encode(email);
        DataResponse dataResponse = new DataResponse();
        
        Optional<User> user = userRepository.findByEmail(encodedEmail);
        if(!user.isPresent()) {
            dataResponse.setStatus(401);
            dataResponse.setMessage("로그인이 필요합니다.");
            return dataResponse;
        }
        
        Long userId = user.get().getId();
        
        // 사용자가 작성한 모든 댓글 조회
        List<ResCommentActivityDTO> allComments = new ArrayList<>();
        
        // 프로젝트 댓글
        List<ResCommentActivityDTO> projectComments = 
            myPageRepository.selectProjectCommentsByUserId(userId);
        allComments.addAll(projectComments);
        
        // 블로그 댓글
        List<ResCommentActivityDTO> blogComments = 
            myPageRepository.selectBlogCommentsByUserId(userId);
        allComments.addAll(blogComments);
        
        // 포트폴리오 댓글
        List<ResCommentActivityDTO> portfolioComments = 
            myPageRepository.selectPortfolioCommentsByUserId(userId);
        allComments.addAll(portfolioComments);
        
        // 팀구하기 댓글
        List<ResCommentActivityDTO> teamPostComments = 
            myPageRepository.selectTeamPostCommentsByUserId(userId);
        allComments.addAll(teamPostComments);
        
        if(allComments.isEmpty()) {
            dataResponse.setStatus(404);
            dataResponse.setMessage("작성한 댓글이 없습니다.");
            return dataResponse;
        }
        
        // 최신순으로 정렬
        allComments.sort((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));
        
        dataResponse.setData(allComments);
        dataResponse.setStatus(200);
        return dataResponse;
    }

    public DataResponse getLike() {
        //사용자 정보
        String email =  SecurityContextHolder.getContext().getAuthentication().getName();
        String endoceEamil = aesUtil.encode(email);
        Optional<User> user = userRepository.findByEmail(endoceEamil);
        DataResponse dataResponse = new DataResponse();
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
                resWorkLikeDTO.setDescription(teamPost.getContent() != null ? teamPost.getContent().substring(0, Math.min(100, teamPost.getContent().length())) : null);
                resWorkLikeDTOs.add(resWorkLikeDTO);
            }
            for (Blog blog : blogs) {
                ResWorkLikeDTO resWorkLikeDTO = new ResWorkLikeDTO();
                resWorkLikeDTO.setId(blog.getId());
                resWorkLikeDTO.setTitle(blog.getTitle());
                resWorkLikeDTO.setType("blog");
                resWorkLikeDTO.setCreateTime(blog.getCreatedAt());
                resWorkLikeDTO.setDescription(blog.getContent() != null ? blog.getContent().substring(0, Math.min(100, blog.getContent().length())) : null);
                resWorkLikeDTO.setThumbnailURL(blog.getThumbnailURL());
                resWorkLikeDTOs.add(resWorkLikeDTO);
            }
            for (PortFolio folio : portFolio) {
                ResWorkLikeDTO resWorkLikeDTO = new ResWorkLikeDTO();
                resWorkLikeDTO.setId(folio.getId());
                resWorkLikeDTO.setTitle(folio.getTitle());
                resWorkLikeDTO.setType("portfolio");
                resWorkLikeDTO.setCreateTime(folio.getCreateAt());
                resWorkLikeDTO.setDescription(folio.getIntroductions() != null ? folio.getIntroductions().substring(0, Math.min(100, folio.getIntroductions().length())) : null);
                resWorkLikeDTO.setThumbnailURL(folio.getThumbnailURL());
                resWorkLikeDTOs.add(resWorkLikeDTO);
            }
            for (Project project1 : project) {
                ResWorkLikeDTO resWorkLikeDTO = new ResWorkLikeDTO();
                resWorkLikeDTO.setId(project1.getId());
                resWorkLikeDTO.setTitle(project1.getTitle());
                resWorkLikeDTO.setType("project");
                resWorkLikeDTO.setCreateTime(project1.getCreatedAt());
                resWorkLikeDTO.setDescription(project1.getDescription());
                resWorkLikeDTO.setThumbnailURL(project1.getThumbnailURL());
                resWorkLikeDTOs.add(resWorkLikeDTO);
            }
            
            if(resWorkLikeDTOs.isEmpty()) {
                dataResponse.setStatus(404);
                dataResponse.setMessage("좋아요한 항목이 없습니다.");
                return dataResponse;
            }
            
            dataResponse.setData(resWorkLikeDTOs);
            dataResponse.setStatus(200);
            return dataResponse;
        }else{
            dataResponse.setStatus(401);
            dataResponse.setMessage("로그인이 필요합니다.");
            return dataResponse;
        }

    // 사용자가 좋아요한 게시글, 블로그, 포트폴리오
//    public DataResponse getActivityLike() {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        String encodedEmail = aesUtil.encode(email);
//        DataResponse dataResponse = new DataResponse();
//
//        if (userRepository.findByEmail(encodedEmail).isPresent()) {
//            List<Project> likedProjects = projectRepository.findProjectLikesByUserEmail(encodedEmail);
//            List<Blog> likedBlogs = blogRepository.findBlogLikesByUserEmail(encodedEmail);
//
//            List<com.pj.portfoliosite.portfoliosite.mypage.dto.ResWorkLikeDTO> workLikeDTOList = new ArrayList<>();
//
//            // 프로젝트 좋아요 추가
//            for (Project project : likedProjects) {
//                com.pj.portfoliosite.portfoliosite.mypage.dto.ResWorkLikeDTO dto = new com.pj.portfoliosite.portfoliosite.mypage.dto.ResWorkLikeDTO();
//                dto.setId(project.getId());
//                dto.setTitle(project.getTitle());
//                dto.setCreateTime(project.getCreatedAt());
//                dto.setType("PROJECT");
//                dto.setDescription(project.getDescription());
//                dto.setThumbnailURL(project.getThumbnailURL());
//                workLikeDTOList.add(dto);
//            }
//
//            // 블로그 좋아요 추가
//            for (Blog blog : likedBlogs) {
//                com.pj.portfoliosite.portfoliosite.mypage.dto.ResWorkLikeDTO dto = new com.pj.portfoliosite.portfoliosite.mypage.dto.ResWorkLikeDTO();
//                dto.setId(blog.getId());
//                dto.setTitle(blog.getTitle());
//                dto.setCreateTime(blog.getCreatedAt());
//                dto.setType("BLOG");
//                dto.setDescription(blog.getContent() != null ? blog.getContent().substring(0, Math.min(100, blog.getContent().length())) : "");
//                dto.setThumbnailURL(blog.getThumbnailURL());
//                workLikeDTOList.add(dto);
//            }
//
//            if (workLikeDTOList.isEmpty()) {
//                dataResponse.setStatus(404);
//                dataResponse.setMessage("좋아요한 게시글이 없습니다.");
//            } else {
//                dataResponse.setData(workLikeDTOList);
//                dataResponse.setStatus(200);
//            }
//        } else {
//            dataResponse.setStatus(401);
//            dataResponse.setMessage("접근 권한이 없습니다.");
//        }
//
//        return dataResponse;
    }
}
