package com.pj.portfoliosite.portfoliosite.portfolio;

import com.pj.portfoliosite.portfoliosite.global.entity.*;
import com.pj.portfoliosite.portfoliosite.portfolio.bookmark.PortfolioBookMarkRepository;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.*;
import com.pj.portfoliosite.portfoliosite.portfolio.like.PortFolioLikeRepository;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PortFolioService {
    private final PortFolioRepository pfRepository;
    private final UserRepository userRepository;
    private final PortFolioLikeRepository pfLikeRepository;
    private final PortfolioBookMarkRepository pfBookMarkRepository;
    // 저장 로직
    public void save(ReqPortfolioDTO reqPortfolioDTO) {
        //user part
        String testLogin = "portfolio@naver.com";
        Optional<User> user = userRepository.findByEmail(testLogin);

        // user 로직

        PortFolio portfolio = new PortFolio();
        portfolio.addUser(user.get());
        // award list
        portfolio.addAward(toAwardList(reqPortfolioDTO.getAwards()));
        // career list
        portfolio.addCareer(toCareerList(reqPortfolioDTO.getCareers()));
        // education list
        portfolio.addEducation(toEducationList(reqPortfolioDTO.getEducations()));
        // certificate list
        portfolio.addCertificate(toCertificateList(reqPortfolioDTO.getCertificates()));
        portfolio.save(reqPortfolioDTO);
        pfRepository.insert(portfolio);
    }
    // award list 형 변환
    private List<Award> toAwardList(List<ReqAwardDTO> reqAwardDTOList) {
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
        //변경
        resPortFolioDTO.setId(portFolio.getId());
        resPortFolioDTO.setEmail(portFolio.getUser().getEmail());
        resPortFolioDTO.setTitle(portFolio.getTitle());
        resPortFolioDTO.setIndustry(portFolio.getIndustry());
        resPortFolioDTO.setSkill(portFolio.getSkill());
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

        return resPortFolioDTO;
    }
    // entity to dto
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
        String testLogin = "portfolio@naver.com";
        Optional<User> user = userRepository.findByEmail(testLogin);
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
}
