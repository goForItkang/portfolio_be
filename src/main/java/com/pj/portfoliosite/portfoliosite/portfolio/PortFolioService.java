package com.pj.portfoliosite.portfoliosite.portfolio;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.dto.PageDTO;
import com.pj.portfoliosite.portfoliosite.global.entity.*;
import com.pj.portfoliosite.portfoliosite.portfolio.bookmark.PortfolioBookMarkRepository;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.*;
import com.pj.portfoliosite.portfoliosite.portfolio.like.PortFolioLikeRepository;
import com.pj.portfoliosite.portfoliosite.skill.ResSkill;
import com.pj.portfoliosite.portfoliosite.skill.SkillRepository;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.util.AESUtil;
import com.pj.portfoliosite.portfoliosite.util.ImgUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortFolioService {
    private final PortFolioRepository pfRepository;
    private final UserRepository userRepository;
    private final PortFolioLikeRepository pfLikeRepository;
    private final PortfolioBookMarkRepository pfBookMarkRepository;
    private final ImgUtil imgUtil;
    private final AESUtil aesUtil;
    private final SkillRepository skillRepository;
    // 저장 로직
    @Transactional
    public Long save(ReqPortfolioDTO reqPortfolioDTO) throws IOException {
        //user part
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info(userEmail);
        String endcodeEamil = aesUtil.encode(userEmail);

        Optional<User> user = userRepository.findByEmail(endcodeEamil);
        // user 로직
        PortFolio portfolio = new PortFolio();
        if(reqPortfolioDTO.getFile() == null){
            return null;
        }else{
            String url = imgUtil.imgUpload(reqPortfolioDTO.getFile());
            portfolio.addPortfolioFile(url);
        }

        portfolio.addUser(user.get());
        // award list
        portfolio.addAward(toAwardList(reqPortfolioDTO.getAwards()));
        // career list
        portfolio.addCareer(toCareerList(reqPortfolioDTO.getCareers()));
        // education list
        portfolio.addEducation(toEducationList(reqPortfolioDTO.getEducations()));
        // certificate list
        portfolio.addCertificate(toCertificateList(reqPortfolioDTO.getCertificates()));
        // project description list
        portfolio.addProjectDescription(toProjectDescription(reqPortfolioDTO.getProjectDescriptions()));

        // ===== 스킬 처리 로직 추가 시작 =====
        List<String> skillIds = reqPortfolioDTO.getSkillIds(); // DTO에서 ID 목록을 가져옵니다.

        if (skillIds != null && !skillIds.isEmpty()) {
            List<PortfolioSkill> portfolioSkills = new ArrayList<>();
            for (String skillIdString : skillIds) { // 변수명을 skillIdString으로 변경하여 명확히 함
                try {
                    // 문자열을 Long 타입으로 변환합니다.
                    Long skillId = Long.parseLong(skillIdString);

                    // getReferenceById를 사용해 실제 DB 조회 없이 엔티티 참조(프록시)만 가져옵니다. (성능 최적화)
                    Skill skillReference = skillRepository.getReferenceById(skillId);

                    // 중간 테이블 엔티티인 PortfolioSkill을 생성합니다.
                    PortfolioSkill portfolioSkill = new PortfolioSkill();
                    portfolioSkill.setSkill(skillReference); // Skill 참조 설정
                    portfolioSkills.add(portfolioSkill);
                } catch (NumberFormatException e) {
                    // 만약 skillIdString이 숫자로 변환될 수 없는 값이라면, 이 예외가 발생합니다.
                    // 해당 스킬은 무시하고 계속 진행하거나, 로그를 남길 수 있습니다.
                    log.warn("숫자로 변환할 수 없는 Skill ID를 건너뜁니다: {}", skillIdString);
                }
            }
            // PortFolio에 최종적으로 생성된 중간 엔티티 리스트를 연결합니다.
            portfolio.addPortfolioSkills(portfolioSkills);
        }

        // ===== 스킬 처리 로직 추가 끝 =====

        portfolio.save(reqPortfolioDTO);
        pfRepository.insert(portfolio);
        Long id = portfolio.getId();
        return id;
    }

    // 프로젝트 설명란
     private List<ProjectDescription> toProjectDescription(List<ReqProjectDescription> reqProjectDescriptionList) {
        List<ProjectDescription> projectDescriptionList = new ArrayList<>();
         for (ReqProjectDescription reqProjectDescription : reqProjectDescriptionList) {
             ProjectDescription projectDescription = new ProjectDescription();
             projectDescription.setDescription(reqProjectDescription.getDescription());
             projectDescriptionList.add(projectDescription);
         }
         return projectDescriptionList;
     }

    // award list 형 변환
    private List<Award> toAwardList(List<ReqAwardDTO> reqAwardDTOList) {
        if (reqAwardDTOList == null || reqAwardDTOList.isEmpty()) {
            return new ArrayList<>();
        }
        List<Award> awardList = new ArrayList<>();
        for (ReqAwardDTO reqAwardDTO : reqAwardDTOList) {
            Award award = new Award();
            award.addReqAwardDTO(reqAwardDTO);
            awardList.add(award);
        }

        return awardList;
    }
    // 커리어 리스트 db에 값으로 변환
    private List<Career> toCareerList(List<ReqCareerDTO> reqCareerDTOList) {
        if (reqCareerDTOList == null || reqCareerDTOList.isEmpty()) {
            return new ArrayList<>();
        }
        List<Career> careerList = new ArrayList<>();

        for (ReqCareerDTO reqCareerDTO : reqCareerDTOList) {
            Career career = new Career();
            career.ReqCareerDTO(reqCareerDTO);
            careerList.add(career);

        }
        return careerList;
    }
    // education 리스트
    private List<Education> toEducationList(List<ReqEducationDTO> reqEducationDTOList ) {
        if (reqEducationDTOList == null || reqEducationDTOList.isEmpty()) {
            return new ArrayList<>();
        }
        List<Education> educationList = new ArrayList<>();
        for (ReqEducationDTO reqEducationDTO : reqEducationDTOList) {
            Education education = new Education();
            education.ReqEducationDTO(reqEducationDTO);
            educationList.add(education);
        }
        return educationList;
    }
    // certificate 리스트
    private List<Certificate> toCertificateList(List<ReqCertificateDTO> reqCertificateDTOList) {
        if (reqCertificateDTOList == null || reqCertificateDTOList.isEmpty()) {
            return new ArrayList<>();
        }
        List<Certificate> certificateList = new ArrayList<>();
        for (ReqCertificateDTO reqCertificateDTO : reqCertificateDTOList) {
            Certificate certificate = new Certificate();
            certificate.reqCertificateDTO(reqCertificateDTO);
            certificateList.add(certificate);
        }
        return certificateList;
    }

    public ResPortFolioDTO getPortFolio(Long id) {
        // 전체 가져오기
//        PortFolio portFolio = pfRepository.selectWithAllById(id);
       PortFolio portFolio = pfRepository.selectById(id);
       // entity to dto
        ResPortFolioDTO resPortFolioDTO = new ResPortFolioDTO(); // dto
        resPortFolioDTO.setFile(portFolio.getThumbnailURL());
        //변경
        resPortFolioDTO.setId(portFolio.getId());
        resPortFolioDTO.setEmail(
                aesUtil.decode(portFolio.getUser().getEmail())
        );
        resPortFolioDTO.setWriteName(
               aesUtil.decode(portFolio.getUser().getNickname())
        );
        resPortFolioDTO.setTitle(portFolio.getTitle());
        resPortFolioDTO.setIndustry(portFolio.getIndustry());
        List<Skill> portfolioSkills = skillRepository.selectByPortfolioId(id);
        ResSkill resSkill = new ResSkill();
        resPortFolioDTO.setSkill(resSkill.toResSkillList(portfolioSkills));
        resPortFolioDTO.setIntroductions(portFolio.getIntroductions());
        resPortFolioDTO.setCreateAt(portFolio.getCreateAt());

        // dto 관련 list 변경
        List<Award> awards = pfRepository.awardSelectByPortfolioId(id);
        resPortFolioDTO.setAwards(awardListToResAwardDTOList(awards));
        List<Career> careers = pfRepository.careerSelectByPortfolioId(id);
        resPortFolioDTO.setCareers(careerListToResCareerDTOList(careers));
        List<Education> educations = pfRepository.educationSelectByPortfolioId(id);
        resPortFolioDTO.setEducations(educationListToResEducationDTOList(educations));
        List<Certificate> certificates = pfRepository.certificateSelectByPortfolioId(id);
        resPortFolioDTO.setCertificates(certificateListToResCertificateDTOList(certificates));
        List<ProjectDescription> projectDescriptions = pfRepository.projectDescriptionSelectByPortfolioId(id);
        resPortFolioDTO.setProjectDescriptions(projectDescriptionListToProjectDTOList(projectDescriptions));
        return resPortFolioDTO;
    }
    // entity to dto
    private List<ResProjectDescription> projectDescriptionListToProjectDTOList(List<ProjectDescription> projectDescriptions) {
        List<ResProjectDescription> resProjectDescriptionList = new ArrayList<>();
        for (ProjectDescription projectDescription : projectDescriptions) {
            ResProjectDescription resProjectDescription = new ResProjectDescription();
            resProjectDescription.setId(projectDescription.getId());
            resProjectDescription.setDescription(projectDescription.getDescription());
            resProjectDescriptionList.add(resProjectDescription);
        }
        return resProjectDescriptionList;
    }


    private List<ResAwardDTO> awardListToResAwardDTOList(List<Award> awards) {
        if(awards == null){
            return null;
        }else{
            List<ResAwardDTO> resAwardDTOS = new ArrayList<>();
            for (Award award : awards) {
                ResAwardDTO resAwardDTO = new ResAwardDTO();
                resAwardDTO.setId(award.getId());
                resAwardDTO.setAwardDescription(award.getAwardDescription());
                resAwardDTOS.add(resAwardDTO);
            }
            return resAwardDTOS;
        }
    }
    private List<ResProjectDescription> projectDescriptionListToResProjectDescriptionList(List<ProjectDescription> projectDescriptions) {
        if(projectDescriptions == null){
            return null;
        }else{
            List<ResProjectDescription> resProjectDescriptionS = new ArrayList<>();
            for (ProjectDescription projectDescription : projectDescriptions) {
                ResProjectDescription resProjectDescription = new ResProjectDescription();
                resProjectDescription.setDescription(projectDescription.getDescription());
                resProjectDescriptionS.add(resProjectDescription);
            }
            return resProjectDescriptionS;
        }
    }
    private List<ResCareerDTO> careerListToResCareerDTOList(List<Career> careers) {
        if(careers == null){
            return null;
        }else{
            List<ResCareerDTO> resCareerDTOS = new ArrayList<>();
            for (Career career : careers) {
                ResCareerDTO resCareerDTO = new ResCareerDTO();
                resCareerDTO.setId(career.getId());
                resCareerDTO.setCompanyName(career.getCompanyName());
                resCareerDTO.setCompanyPosition(career.getCompanyPosition());
                resCareerDTO.setStartDate(career.getStartDate());
                resCareerDTO.setEndDate(career.getEndDate());
                resCareerDTO.setDate(career.getDate());
                resCareerDTO.setDuty(career.getDuty());
                resCareerDTO.setDutyDescription(career.getDutyDescription());
                resCareerDTOS.add(resCareerDTO);
            }
            return resCareerDTOS;
        }
    }
    // education to dto
    private List<ResEducationDTO> educationListToResEducationDTOList(List<Education> educations) {
        if(educations == null){
            return null;
        }else{
            List<ResEducationDTO> resEducationDTOS = new ArrayList<>();
            for (Education education : educations) {
                ResEducationDTO resEducationDTO = new ResEducationDTO();
                resEducationDTO.setId(education.getId());
                resEducationDTO.setSchool(education.getSchool());
                resEducationDTO.setSchoolStatus(education.getSchoolStatus());

                resEducationDTOS.add(resEducationDTO);
            }
            return resEducationDTOS;
        }

    }
    private List<ResCertificateDTO> certificateListToResCertificateDTOList(List<Certificate> certificates) {
        if(certificates == null){
            return null;
        }else{
            List<ResCertificateDTO> resCertificateDTOS = new ArrayList<>();
            for (Certificate certificate : certificates) {
                ResCertificateDTO resCertificateDTO = new ResCertificateDTO();
                resCertificateDTO.setId(certificate.getId());
                resCertificateDTO.setCertificateName(certificate.getCertificateName());
                resCertificateDTO.setCertificateDate(certificate.getCertificateDate());
                resCertificateDTO.setNumber(certificate.getNumber());
                resCertificateDTOS.add(resCertificateDTO);
            }
            return resCertificateDTOS;
        }
    }

    public ResPortfolioDetailDTO getPortFolioDetails(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userEmail;
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            userEmail = authentication.getName();
        } else {
            throw new RuntimeException("로그인이 필요합니다.");
        }
        Optional<User> user = userRepository.findByEmail(userEmail);
        ResPortfolioDetailDTO resPortfolioDetailDTO = new ResPortfolioDetailDTO();
        if(user.isPresent()){
            //get user
            boolean result = pfBookMarkRepository.existBookMark(user.get().getId(),id);
            resPortfolioDetailDTO.setBookMarkCheck(result);
            boolean result2 = pfLikeRepository.existLike(user.get().getId(),id);
            resPortfolioDetailDTO.setLikeCheck(result2);
        }else{
            // not user
            resPortfolioDetailDTO.setLikeCheck(false);
            resPortfolioDetailDTO.setBookMarkCheck(false);
        }
        // Like 및 bookmark 갯수
        Long likeCount = pfLikeRepository.countByPortfolioId(id);
        resPortfolioDetailDTO.setLikeCount(likeCount);
        Long bookCount = pfBookMarkRepository.countByPortfolioId(id);
        resPortfolioDetailDTO.setBookMarkCount(bookCount);
        return resPortfolioDetailDTO;
    }
    public List<ResPortFolioDTO> portfolioDTOTOEntity(List<PortFolio> portfolios) {
        if(portfolios == null){
            return null;
        }else{
            List<ResPortFolioDTO> resPortfolioDTOS = new ArrayList<>();
            for (PortFolio portfolio : portfolios) {
                ResPortFolioDTO resPortfolioDTO = new ResPortFolioDTO();
                resPortfolioDTO.setFile(portfolio.getThumbnailURL());
                resPortfolioDTO.setId(portfolio.getId()); // id
                resPortfolioDTO.setEmail(portfolio.getEmail()); //이메일
                resPortfolioDTO.setWriteName(aesUtil.decode(portfolio.getUser().getNickname()));
                resPortfolioDTO.setTitle(portfolio.getTitle()); //제목
                resPortfolioDTO.setIndustry(portfolio.getIndustry());// 분야
                resPortfolioDTO.setJobPosition(portfolio.getJobPosition());
                resPortfolioDTO.setCreateAt(portfolio.getCreateAt()); // 날짜
                resPortfolioDTOS.add(resPortfolioDTO);
            }

            return resPortfolioDTOS;
        }
    }
    //추천 프로젝트 오늘 부터 일주일 동안 가장 많이 받은 좋아요 갯수
    public List<ResPortFolioDTO> getPortFolioRecommend() {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusWeeks(1);

        List<PortFolio> portFolios = pfRepository.findTopProjectsByLikesInPeriod(today,weekAgo);
        return portfolioDTOTOEntity(portFolios);

    }


    public void deletePortfolio(Long id) {
        PortFolio portFolio = pfRepository.selectById(id);
        pfRepository.deleteById(portFolio);
    }


    public PageDTO<ResPortFolioDTO> getAll(int page, int size) {
        // 1) 입력 값 검증 및 기본값 설정
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        // 2) Repository에서 데이터 목록과 전체 개수 조회
        List<PortFolio> rows = pfRepository.selectByCreateAtDesc(safePage, safeSize);
        long totalElements = pfRepository.selectAllCount();

        // 3) 조회된 엔티티 목록을 DTO 목록으로 변환
        List<ResPortFolioDTO> content = portfolioDTOTOEntity(rows);

        // 4) 페이지네이션 메타 데이터 계산
        int totalPages = (int) Math.ceil((double) totalElements / safeSize);
        boolean first = safePage == 0;
        boolean last = (totalPages == 0) || (safePage >= totalPages - 1);
        boolean hasNext = !last;
        boolean hasPrevious = !first;
        int count = content.size();

        // 5) 최종 PageDTO 객체를 생성하여 반환
        return new PageDTO<>(
                content,
                safePage,
                safeSize,
                totalElements,
                totalPages,
                first,
                last,
                hasNext,
                hasPrevious,
                count
        );
    }

}
